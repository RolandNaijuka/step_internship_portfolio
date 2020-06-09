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

package com.google.sps.data;

/** Creates an instance which tells us about the status of whether user is logged in or not */
public class LogInStatus{

  /* The email address of the User who is logged in
   * This is not null when the user is logged in
   */
  private String emailAddress;
  
  /* Defines the status of whether user is logged in or not */
  private boolean isLoggedIn;

  /* Redirects user to log in or log out page, depending on their status */
  private String logUrl;

  /* Creates an instance when user is logged in */
  public LogInStatus(String emailAddress, boolean isLoggedIn, String logUrl){
    this.emailAddress = emailAddress;
    this.isLoggedIn = isLoggedIn;
    this.logUrl = logUrl;
  }

  /* Creates an instance when user is not logged in */
  public LogInStatus(boolean isLoggedIn, String logUrl){
    this.emailAddress = "none";
    this.isLoggedIn = isLoggedIn;
    this.logUrl = logUrl;
  }
}
