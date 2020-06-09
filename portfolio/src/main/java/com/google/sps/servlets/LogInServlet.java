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

package com.google.sps.servlets;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.LogInStatus;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LogInServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");
    UserService userService = UserServiceFactory.getUserService();
    LogInStatus logInStatus;
    
    if(userService.isUserLoggedIn()){
      boolean isLoggedIn = true;
      String userEmail = userService.getCurrentUser().getEmail();
      String urlToRedirectToAfterUserLogsOut = "/contact.html";
      String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);
      logInStatus = new LogInStatus(userEmail, isLoggedIn, logoutUrl);

      // Convert the login details to json string
      String logInDetails = new Gson().toJson(logInStatus);
      response.getWriter().println(logInDetails);
    }
    else{
      boolean isLoggedIn = false;
      String urlToRedirectToAfterUserLogsOut = "/contact.html";
      String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsOut);
      logInStatus = new LogInStatus(isLoggedIn, loginUrl);

      // Convert the login details to json string
      String logInDetails = new Gson().toJson(logInStatus);
      response.getWriter().println(logInDetails);
    }
  }
}
