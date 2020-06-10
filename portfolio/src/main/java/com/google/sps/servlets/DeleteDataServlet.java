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

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions.Builder;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for deleting tasks. */
@WebServlet("/delete-data")
public class DeleteDataServlet extends HttpServlet {

  /**
   * This method receives the client's requests to post data and redirects them when there is success.
   * The client will receive a blobstore url for uploading an image
   * @param request This holds the HttpServletRequest from the client
   * @param response This holds the HttpServletResponse which is sent to the client. This is a redirection to the contact.html page.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
    // get the email of the client
    UserService userService = UserServiceFactory.getUserService();
    String emailAddress = userService.getCurrentUser().getEmail();

    // Make sure to delete the comments for the current user only
    Query query = new Query("Comments").setFilter(new Query.FilterPredicate("emailAddress", Query.FilterOperator.EQUAL, emailAddress));;

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // Delete all the comments in the database one at a time
    for (Entity entity: results.asIterable()) {
      datastore.delete(entity.getKey());
    }
    response.sendRedirect("/contact.html");
  }
}
