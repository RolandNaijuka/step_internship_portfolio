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
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        int numComments;
        try{
            numComments = Integer.parseInt(request.getParameter("numComments"));
        }catch(NumberFormatException e){
            // Default number of comments
            numComments = 5;
        }
        Query query = new Query("Comments");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        
        ArrayList<Comment> userComments = new ArrayList<>();
        for(Entity entity: results.asIterable()){
            long id = entity.getKey().getId();
            String username = (String) entity.getProperty("name");
            String comment = (String) entity.getProperty("comment");
            
            Comment userComment =  new Comment(id, username, comment);
            
            // Do not exceed max number of comments to display
            if(userComments.size() >= numComments){
                break;
            }

            userComments.add(userComment);
        }
        
        response.setContentType("application/json");

        //Convert the arraylist to json string
        String comments = new Gson().toJson(userComments);
        response.getWriter().println(comments);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String comment = getParameter(request,"comment","");
        String username = getParameter(request, "name","user");
        // Do not store an empty comment
        if(comment.length() == 0){
            response.sendRedirect("/contact.html");
            return;
        }
        Entity commentEntity = new Entity("Comments");
        commentEntity.setProperty("name",username);
        commentEntity.setProperty("comment",comment);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentEntity);

        response.sendRedirect("/contact.html");
    }

    /**
   * Generate the user details that make up a comment sent by the user.
   */
    private String getParameter(HttpServletRequest request, String name, String defaultValue){
        String value = request.getParameter(name);
        return value.length() == 0 ? defaultValue : value;
    }
}
