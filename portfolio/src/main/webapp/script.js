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
    const response = await fetch(`/${await fetchBlobUrl()}?numComments=${numComments}`);
    const data = await response.json();

    const commentEl = document.querySelector("#user-comments");
    if(typeof(commentEl) != 'undefined' && commentEl != null) {
        commentEl.innerText = "";

        for(let comment in data) {
          commentEl.appendChild(createElement(data[comment]));
        }
    }
  } catch(err) {
    console.log("There was an error loading comments!");
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
function setActionAttr() {
  const commentsForm = document.querySelector("#comment-form");
  if(typeof(commentsForm) != 'undefined' && commentsForm != null){
    commentsForm.actionAttrValue = fetchBlobUrl();
  }
}

/** Creates an <p> element containing comments. */
function createElement(comment) {
  const pElement = document.createElement('p');
  pElement.innerHTML = `${comment.name}: ${comment.comment}`;
  return pElement;
}

/**
* Change the innerHTML to a greeting and name
* every time use loads or refreshes
*/
async function loadContent() {
  setActionAttr();
  console.log(await fetchBlobUrl());
  updateNumComments();
  const greetingEl = document.getElementById("welcome-note")
  if(typeof(greetingEl) != 'undefined' && greetingEl != null) {
    greetingEl.innerHTML = `${generateRandomGreeting()} My name is Roland`;
  }
}

window.onload = loadContent;
