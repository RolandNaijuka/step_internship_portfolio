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
}
