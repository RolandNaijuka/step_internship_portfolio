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

/**
 * LogInStatus is a class for holding the login status of the user on the website
 * Creates an instance which tells us about the status of whether user is logged in or not 
 */
public class LogInStatus {

  // A string which holds the email address of the User who is logged in. It is "none" when the user is not logged in
  private String emailAddress;
  
  // A boolean which defines the status of whether user is logged in or not
  private boolean isLoggedIn;

  // A url string which redirects user to either log in or log out page, depending on their status
  private String logUrl;

  /**
   * Creates an instance when user is logged in
   * @param emailAddress The email address of the user who is logged in
   * @param logUrl A string that holds the url where the user is redirected to
   */
  public LogInStatus(String emailAddress, String logUrl) {
    this.emailAddress = emailAddress;
    this.isLoggedIn = true;
    this.logUrl = logUrl;
  }

  /**
   * Creates an instance when user is not logged in
   * @param emailAddress The email address is none here because the user is not logged in
   * @param logUrl A string that holds the url where the user is redirected to
   */
  public LogInStatus(String logUrl) {
    this.emailAddress = "none";
    this.isLoggedIn = false;
    this.logUrl = logUrl;
  }
}
