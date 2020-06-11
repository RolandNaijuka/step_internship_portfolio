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

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * When the fetch() function requests the /blobstore-upload-url URL, the content of the response is
 * the URL that allows a client to upload an image to Blobstore.
 */
@WebServlet("/blobstore-upload-url")
public class BlobstoreUploadUrlServlet extends HttpServlet {
  /**
   * This method receives the client's requests and sends an appropriate response.
   * The client will receive a blobstore url for uploading an image
   * @param request This holds the HttpServletRequest from the client
   * @param response This holds the HttpServletResponse which is sent to the client. This is sent in form of html text.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    // Create the url where the client will upload the images
    String uploadUrl = blobstoreService.createUploadUrl("/store-data");

    response.setContentType("text/html");
    response.getWriter().println(uploadUrl);
  }
}
