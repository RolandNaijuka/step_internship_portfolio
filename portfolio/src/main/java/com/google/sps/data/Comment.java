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
 * Comment is a class for a comment that a user writes on the website
 */
public class Comment{

    /** 
     * a long to keep the id of the comment.
     * This is used in querying the comment for database operations 
     */
    private long id;
    
    /** 
     * A string to keep the comment of the user 
     */
    private String comment;

    /**
     * A string to keep the name of the person commenting (or user) 
     */
    private String name;

    /**
     * A string that holds the url for the image from the user
     */
    private String imageUrl;

    /**
     * Creats a new Comment object that represents the comment of a user
     * @param id The id of the specific comment which is unique
     * @param name The name of the user submitting the comment
     * @param imageUrl The url link to the image from the user
     */
    public Comment(long id, String name, String comment, String imageUrl){
      this.id = id;
      this.name = name;
      this.comment = comment;
      this.imageUrl = imageUrl;
    }
}
