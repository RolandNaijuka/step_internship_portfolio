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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  // Initialize the number of comments here so that the system always remembers even after refreshing with default 5
  int numComments = 5;

  /**
   * This method receives a client's request for their comments data and responds with a json with the data
   * @param request This holds the client's HttpServletRequest information for the number of comments to display
   * @param response This holds the server's HttpServletResponse to the client's request
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    // Get the number of MAX comments to display
    try {
      numComments = Integer.parseInt(request.getParameter("numComments"));
    } catch (NumberFormatException e) {
      System.err.println("This is the error: "+e);
    }

    // Get the email of the client
    UserService userService = UserServiceFactory.getUserService();
    String emailAddress = userService.getCurrentUser().getEmail();

    // Use the client's email to filter the comments when querying
    Query query = new Query("Comments").setFilter(new Query.FilterPredicate("emailAddress", Query.FilterOperator.EQUAL, emailAddress));
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    
    ArrayList<Comment> userComments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String username = (String) entity.getProperty("name");
      String comment = (String) entity.getProperty("comment");
      String imageUrl = (String) entity.getProperty("imageUrl");
      
      Comment userComment =  new Comment(id, username, comment, imageUrl);
      
      // Do not exceed max number of comments to display, {@param userComments} will be zero if the user doesn't want to display any comments
      if (userComments.size() >= numComments) {
        break;
      }

      userComments.add(userComment);
    }
    
    response.setContentType("application/json");

    // Convert the arraylist to json string
    String comments = new Gson().toJson(userComments);
    response.getWriter().println(comments);
  } 
}
