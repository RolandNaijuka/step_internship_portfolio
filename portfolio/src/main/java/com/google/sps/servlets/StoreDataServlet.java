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

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that stores data from the client */
@WebServlet("/store-data")
public class StoreDataServlet extends HttpServlet {
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

  /**
   * This method receives the client's request to post data and redirects them when there is success.
   * The client will be redirected to the contact.html page
   * @param request This holds the HttpServletRequest from the client
   * @param response This holds the HttpServletResponse which is sent to the client. This is a redirection to the contact.html page.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    // get the clients comments and name
    String comment = getParameter(request,"comment","");
    String username = getParameter(request, "name","user");

    // Get the client's email 
    String emailAddress = userService.getCurrentUser().getEmail();

    // Get the URL of the image that the client uploaded to Blobstore.
    String imageUrl = getUploadedFileUrl(request, "image");
    response.getWriter().println(imageUrl);

    // Do not store an empty comment,redirect the client to the contact.html page and exit the function
    if (comment.length() == 0) {
      response.sendRedirect("/contact.html");
      return;
    }
    // store the client's email address alongside their comments for better querying of individual comments
    Entity commentEntity = new Entity("Comments", emailAddress);
    commentEntity.setProperty("name",username);
    commentEntity.setProperty("comment",comment);
    commentEntity.setProperty("imageUrl",imageUrl);
    commentEntity.setProperty("emailAddress", emailAddress);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    response.sendRedirect("/contact.html");
  }

  /**
   * Generate the client's details that make up a comment sent by the client.
   * @param request This holds the request details from the client
   * @param name This is the name given to the input field on the client's side
   * @param defaulValue This holds the default value to return in case the clients value is empty
   * @return the client's value or the default value
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    return value.length() == 0 ? defaultValue : value;
  }

  /** 
   * Genereta a URL that points to the uploaded file. 
   * @param request This holds the client's request details
   * @param formInputElementName This holds the name of the form which contains the clients file input field
   * @return the URL that points to the uploaded file or null if the client didn't upload a file
   */
  private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
    
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);

    // Client submitted form without selecting a file, so we can't get a URL. (dev server)
    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }

    // Our form only contains a single file input, so get the first index.
    BlobKey blobKey = blobKeys.get(0);

    // Client submitted form without selecting a file, so we can't get a URL. (live server)
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);

    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }

    // Get a URL that points to the uploaded file.
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);

    // To support running in Google Cloud Shell with AppEngine's devserver, we must use the relative
    // path to the image, rather than the path returned by imagesService which contains a host.
    try {
      URL url = new URL(imagesService.getServingUrl(options));
      return url.getPath();
    } catch (MalformedURLException e) {
      return imagesService.getServingUrl(options);
    }
  }
}
