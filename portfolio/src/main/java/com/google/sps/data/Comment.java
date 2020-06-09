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

public class Comment{

    /* Comment id for querying */
    private long id;
    
    /* The comment of the users */
    private String comment;

    /** The name of the person commenting (or user) */
    private String name;

    /** Image form the user */
    private String imageUrl;

    public Comment(long id, String name, String comment, String imageUrl){
      this.id = id;
      this.name = name;
      this.comment = comment;
      this.imageUrl = imageUrl;
    }
}
