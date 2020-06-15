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
    return possibleTimes;
  }
}
