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
          commentEl.appendChild(createElement(`${comment.name}: ${comment.comment}`));
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
function createElement(text) {
  const pElement = document.createElement('p');
  pElement.appendChild(document.createTextNode(text));
  return pElement;
}

/** create img element to display the image submitted by user */
function createImgElement(comment){
  const imgElement = document.createElement('IMG');
  imgElement.setAttribute("src", `${comment.imageUrl}`);
  imgElement.setAttribute("alt", "Image uploaded by user");
  return imgElement;
}

/** Check whether the user is logged in */
async function checkIfUserIsLoggedIn(){
  const commentsForm = document.getElementById('comment-form');
  const logInLogOutDiv = document.getElementById('logInLogOut');

  //set display of form and login div to none
  commentsForm.style.display = "none";

  // fetch the data that represents the login status of the user
  const response = await fetch("/login");
  const logInInfo = await response.json();

  console.log(logInInfo);

  // show the login or login link depending on whether the user is logged in
  if(logInInfo.isLoggedIn){
    commentsForm.style.display="block";
    logInLogOutDiv.innerHTML = '';
    logInLogOutDiv.appendChild(createElement(`Email Address: ${logInInfo.emailAddress}`));
    logInLogOutDiv.appendChild(createAchorElement("Logout here",logInInfo.logUrl));

    setActionAttr();
    updateNumComments();
  } else {
    logInLogOutDiv.innerHTML = '';
    logInLogOutDiv.appendChild(createAchorElement("Login here to add comments",logInInfo.logUrl));
  }

  //set the display of the elements that depends on comments similar to one of commentsForm
  const displayCommentsDiv = document.getElementById("display-comments");
  displayCommentsDiv.style.display = commentsForm.style.display;
}


/* Creates an <a> element containing text 
 * @param text - String to display the destination of the link
 * @param url - url for the href attribute of the link
*/
function createAchorElement(text,url) {
  const aElement = document.createElement('a');
  aElement.appendChild(document.createTextNode(text));
  aElement.title = text;
  aElement.href = url;
  return aElement;
}

/** Change the innerHTML to a greeting and name every time use loads or refreshes */
function loadContent() {
  checkIfUserIsLoggedIn();
  const greetingEl = document.getElementById("welcome-note")
  if(greetingEl) {
    greetingEl.innerHTML = `${generateRandomGreeting()} My name is Roland`;
  }
}

window.onload = loadContent;
