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

// default max comments to display
let MAX_COMMENTS = 5;

/** Generate a random greeting */
function generateRandomGreeting() {
  //TODO use google translated to get greetings in different languages
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];
  return greeting;
}

/* Update the MAX_COMMENTS */
function updateNumComments() {
  const userNumComments = document.querySelector("#numComments").value;
  getUserComments(userNumComments);
}

/* Retrieve user comments and display them */
async function getUserComments(numComments=MAX_COMMENTS) {
  try {
    const response = await fetch(`/data?numComments=${numComments}`);
    const data = await response.json();

    const commentEl = document.querySelector("#user-comments");
    if(commentEl) {
        commentEl.innerText = "";

        for(let comment in data) {
          commentEl.appendChild(createElement(data[comment]));
          commentEl.appendChild(createImgElement(data[comment]));
        }
    }
  } catch(error) {
    console.error("There was an error loading comments: ", error);
  }
}

/* Delete all the comments from the server */
async function deleteComments() {
  try {
    await fetch("/delete-data", {
        method: "POST"
    });
  } catch(error) {
    alert("Try again!");
  } finally {
    updateNumComments();
  }
}

/* Retrieve the url for the posting the comments section form and store it */
async function fetchBlobUrl() {
  const request = await fetch("/blobstore-upload-url");
  // TODO get the relative link
  return await request.text();
}

/** Set the action attribute value in the comments' form */
async function setActionAttr() {
  const commentsForm = document.querySelector("#comment-form");

  if(commentsForm){
    commentsForm.action = fetchBlobUrl();
  }
}

/** Creates an <p> element containing comments. */
function createElement(comment) {
  const pElement = document.createElement('p');
  pElement.innerHTML = `${comment.name}: ${comment.comment}`;
  return pElement;
}

/** create img element to display the image submitted by user */
function createImgElement(comment){
  const imgElement = document.createElement('IMG');
  imgElement.setAttribute("src", `${comment.imageUrl}`);
  imgElement.setAttribute("alt", "Image uploaded by user");
  return imgElement;
}


/** Change the innerHTML to a greeting and name every time use loads or refreshes */
async function loadContent() {
  setActionAttr();
  updateNumComments();
  const greetingEl = document.getElementById("welcome-note");
  if(greetingEl) {
    greetingEl.innerHTML = `${generateRandomGreeting()} My name is Roland`;
  }
}

window.onload = loadContent;
