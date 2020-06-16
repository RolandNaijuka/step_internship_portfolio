// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // Consider the edge cases where there is no attendes or the meeting duration is more than 24 hours or there are no known events

    // Options where there are no Attendees and no optional attendees as well
    if (request.getAttendees().isEmpty() && request.getOptionalAttendees().isEmpty())
      return Arrays.asList(TimeRange.WHOLE_DAY);

    // No options when meeting request is too long (more than a day)
    if (request.getDuration() > TimeRange.WHOLE_DAY.duration())
      return Arrays.asList();
    
    // No events
    if (events.isEmpty()) {
      // No events but there is a valid meeting request
      if (request.getDuration() <= TimeRange.WHOLE_DAY.duration())
        return Arrays.asList(TimeRange.WHOLE_DAY);
      return Arrays.asList();
    }

    // Call the helper function for all other cases that no edge cases
    return queryHelper(events, request);
  }

  /**
   * This is a helper function which returns the possible time ranges for the requested meeting
   * @param events These are all the known events that are already scheduled
   * @param request This is a meeting request that is to be scheduled
   * @return a collection of all possible times that {@code request} can be scheduled
   */
  private Collection<TimeRange> queryHelper(Collection<Event> events, MeetingRequest request) {
    // Define the variables to be used
    long durationOfMeeting = request.getDuration();

    // List of all possible time ranges that attendees can have a meeting
    Collection<TimeRange> possibleTimes = new ArrayList<>();
    // Create a list of attendees and add all the attendees to that list
    Collection<String> attendees = new ArrayList<>();
    attendees.addAll(request.getAttendees());

    // Create a list of all attendees (optional and mandatory) for the requested meeting
    Collection<String> mandatoryAndOptionalAttendees = new ArrayList<>();
    mandatoryAndOptionalAttendees.addAll(request.getOptionalAttendees());
    mandatoryAndOptionalAttendees.addAll(attendees);

    // Collection of timeranges that can not work for the proposed schedule
    // These are times when the attendees have an event scheduled at that particular time
    List<TimeRange> attendeesCannotScheduleHere = cannotSheduleMeeting(attendees, events); 

    // Get the timeranges for the optional attendees
    List<TimeRange>  mandatoryAndOptionalAttendeesCannotScheduleHere = cannotSheduleMeeting(mandatoryAndOptionalAttendees, events);

    // Return the whole day if all attendees are free for the whole day annd there are no optional attendees
    if (mandatoryAndOptionalAttendeesCannotScheduleHere.isEmpty()) 
      return Arrays.asList(TimeRange.WHOLE_DAY);

    // Sort the time ranges by the end time in ascending order
    Collections.sort(attendeesCannotScheduleHere, TimeRange.ORDER_BY_END);
    Collections.sort(mandatoryAndOptionalAttendeesCannotScheduleHere, TimeRange.ORDER_BY_END);

    // Remove the time ranges which are part of longer time ranges
    removeNestedTimes(attendeesCannotScheduleHere);
    removeNestedTimes(mandatoryAndOptionalAttendeesCannotScheduleHere);

    // Check times that work for both optional and mandatory attendees
    addRequestedMeetingToList(mandatoryAndOptionalAttendeesCannotScheduleHere, durationOfMeeting, possibleTimes);
    // If there is no time that works for both optional and mandatory attendees, check for only mandatory attendees
    if(possibleTimes.isEmpty()) {
      addRequestedMeetingToList(attendeesCannotScheduleHere, durationOfMeeting, possibleTimes);
    }
    return possibleTimes;
  }
  /**
   * Method runs the algorithm for adding possible times which work for either both mandatory and optional attendees or just mandatory attendees
   * @param attendees is a list of all the attendees for the meeeting that we are considering
   * @param durationOfMeeting is the length / duration of the requested meeting
   * @param possibleTimes this is a list of all the possible times that we can schedule the meeting
   */
  private void addRequestedMeetingToList(List<TimeRange> attendees, long durationOfMeeting, Collection<TimeRange> possibleTimes) {
    int START_OF_DAY = TimeRange.START_OF_DAY;
    int END_OF_DAY = TimeRange.END_OF_DAY;

    for (int index = 0; index < attendees.size(); index++) {
      TimeRange currentTimeRange = attendees.get(index);
      int startOfCurrentTimeRange = currentTimeRange.start();
      int endOfCurrentTimeRange = currentTimeRange.end();

      // We have 3 different ways we can schedule the meeting at according to the list of time ranges
      // 1. When we have the first end time, we can schedule before it's start time and start of day. It is also the first start time because we removed the nested times
      // 2. Times which are in between the first and last end time in the time ranges
      // 3. The last end time, we can check if we can schedule an item between it and the end of day
      // First(1) way
      if (index == 0) {
        addPossibleTime(START_OF_DAY, startOfCurrentTimeRange, false, durationOfMeeting, possibleTimes);
      }
      // Second(2) way
      if (index + 1 < attendees.size()) {
        TimeRange currentTimeRangePlusOne = attendees.get(index + 1);
        addPossibleTime(endOfCurrentTimeRange, currentTimeRangePlusOne.start(), false, durationOfMeeting, possibleTimes);
      }
      //Third(3) way
      if (index == attendees.size() - 1) {
        if (endOfCurrentTimeRange < END_OF_DAY)
          addPossibleTime(endOfCurrentTimeRange, END_OF_DAY, true, durationOfMeeting, possibleTimes);
      }
    }
  }

  /**
   * Create and check if timerange is appropriate for scheduling requesting time
   * @param start represents the start time for the time range we want to create
   * @param end represents the end time for the time range we want to create
   * @param inclusive this tells us if we would like to include the end time in our time range or not
   * @param durationOfMeeting the duration of the requested meeting
   * @param possibleTimes this is a list of all the possible times that we can schedule the meeting
   */
  private void addPossibleTime(int start, int end, boolean inclusive, long durationOfMeeting, Collection<TimeRange> possibleTimes) {
    TimeRange possibleTime = TimeRange.fromStartEnd(start, end, inclusive);
    if (possibleTime.duration() >= durationOfMeeting) {
      if (!possibleTimes.contains(possibleTime))
        possibleTimes.add(possibleTime);
    }
  }

  /** 
   * Check for events which attendees are attending and mark those time ranges
   * @param attendees is a collection of all the attendees for the required meeting
   * @param events is a collection of all the events that are known
   * @return a collection of all the time ranges when the {@code attendees} are busy and we cannot schedule meetings there
  */
  private List<TimeRange> cannotSheduleMeeting(Collection<String> attendees, Collection<Event> events) {
    List<TimeRange> attendeesConflictingTimes = new ArrayList<>();
    for (String currentAttendee: attendees) {
      for (Event currentEvent: events) {
        // Check to see if attendee attends the current event
        if (isAttendeeOfEvent(currentAttendee, currentEvent)) {
          attendeesConflictingTimes.add(currentEvent.getWhen());
        }
      }
    }
    return attendeesConflictingTimes;
  }

  /**
   * Check if an event has this person as an attendee
   * @param attendee represents the person we would like to check if they are attending a particular event
   * @param event represents an event
   * @return true if {@code attendee} is an attendee of {@code event}
   */
  private boolean isAttendeeOfEvent(String attendee, Event event) {
    return event.getAttendees().contains(attendee) ? true : false;
  }

  /**
   * Remove all the times that are nested in another time
   * For instance in this case: |---------|
   *                               |---|
   * We will remove the short time in order to have few times to cross check over
   * @param attendeesTimeranges holds a collection of timeranges that we would like to trim
   */
  private void removeNestedTimes(List<TimeRange> attendeesTimeranges) {
    // Keep traack of the indices to be removed
    List<Integer> indicesToRemove = new ArrayList<>();
    for (int i = 0; i < attendeesTimeranges.size(); i++) {
      for (int j = i + 1; j < attendeesTimeranges.size(); j++) {
        if (attendeesTimeranges.get(i).contains(attendeesTimeranges.get(j))) {
          indicesToRemove.add(j);
        }else if (attendeesTimeranges.get(j).contains(attendeesTimeranges.get(i))) {
          indicesToRemove.add(i);
        }
      }
    }
    // Every time an element is removed from a list, the indices are reduced. Keep track of deleted elements and subtract from indicesToRemove
    // Did not implement it in the above iterations because, there is no better way I could keep track of i and j at the same with one single value
    int trackDeleted = 0;
    for (int index: indicesToRemove) {
      attendeesTimeranges.remove(index - trackDeleted);
      trackDeleted++;
    }
  }
}
