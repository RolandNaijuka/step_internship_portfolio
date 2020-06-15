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

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.HashSet;
import java.util.List;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // Consider the edge cases where there is no attendes or the meeting duration is more than 24 hours or there are no known events

    // Options where there are no Attendees
    if (request.getAttendees().isEmpty())
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
    int START_OF_DAY = TimeRange.START_OF_DAY;
    int END_OF_DAY = TimeRange.END_OF_DAY;
    long durationOfMeeting = request.getDuration();

    // List of all possible time ranges that attendees can have a meeting
    Collection<TimeRange> possibleTimes = new ArrayList<>();
    // Create a list of attendees and add all the attendees to that list
    Collection<String> attendees = new ArrayList<>();
    attendees.addAll(request.getAttendees());

    // Collection of timeranges that can not work for the proposed schedule
    // These are times when the attendees have an event scheduled at that particular time
    List<TimeRange> attendeesCannotScheduleHere = cannotSheduleMeeting(attendees, events); 

    //Return the whole day if all attendees are free for the whole day
    if (attendeesCannotScheduleHere.isEmpty()) 
      return Arrays.asList(TimeRange.WHOLE_DAY);

    // Sort the time ranges by the end time in ascending order
    Collections.sort(attendeesCannotScheduleHere, TimeRange.ORDER_BY_END);

    // Remove the time ranges which are part of longer time ranges
    removeNestedTimes(attendeesCannotScheduleHere);

    for (int index = 0; index <attendeesCannotScheduleHere.size(); index++) {
      TimeRange currentTimeRange = attendeesCannotScheduleHere.get(index);
      int startOfCurrentTimeRange = currentTimeRange.start();
      int endOfCurrentTimeRange = currentTimeRange.end();
      int durationOfCurrentTimeRange = currentTimeRange.duration();

      // We have 3 different ways we can schedule the meeting at according to the list of time ranges
      // 1. When we have the first end time, we can schedule before it's start time and start of day. It is also the first start time because we removed the nested times
      // 2. Times which are in between the first and last end time in the time ranges
      // 3. The last end time, we can check if we can schedule an item between it and the end of day
      // First(1) way
    }
    return possibleTimes;
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
    for (int i = 0; i < attendeesTimeranges.size(); i++) {
      for (int j = 0; j < attendeesTimeranges.size(); j++) {
        // Skip the Timeranges which are the same
        if (i == j) continue;
        else if (attendeesTimeranges.get(i).contains(attendeesTimeranges.get(j))) {
          attendeesTimeranges.remove(j);
        }
      }
    }
  }
}
