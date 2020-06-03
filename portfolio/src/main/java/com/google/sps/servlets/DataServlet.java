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

import com.google.gson.Gson;
import com.google.sps.data.Comments;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    private final int MAX_COMMENTS = 3;
    private ArrayList<Comments> userComments = new ArrayList<>();
    

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        //Convert the arraylist to json string
        String comments = new Gson().toJson(userComments);
        response.getWriter().println(comments);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String comment = getParameter(request,"comment","");
        String username = getParameter(request, "name","");
        if(comment.length() != 0){
            //check if user sent their name
            Comments newComment = username.length() == 0 ? new Comments(comment) : new Comments(comment, username);
            if(userComments.size() < MAX_COMMENTS){
                userComments.add(newComment);
            }
            else{
                userComments.clear();
                userComments.add(newComment);
            }
        }
        response.sendRedirect("/contact.html");
    }

    /**
   * @return the request parameter, 
   * or the default value if the parameter
   * was not specified by the client
   */
    private String getParameter(HttpServletRequest request, String name, String defaultValue){
        String value = request.getParameter(name);
        return value == null ? defaultValue : value;
    }
}
