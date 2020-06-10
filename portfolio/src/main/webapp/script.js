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

/** 
 * checks whether the user is logged in and display the information according to the user's status 
 * @async
 */
async function checkIfUserIsLoggedIn() {
  const userCommentsForm = document.getElementById('comment-form');
  const logInLogOutDiv = document.getElementById('logInLogOut');

  // fetch the data that represents the login status of the user
  const response = await fetch('/login');
  const logInInfo = await response.json();

  // show the login or login link depending on whether the user is logged in
  if (logInInfo.isLoggedIn) {
    userCommentsForm.style.display = 'block';
    logInLogOutDiv.innerHTML = '';
    logInLogOutDiv.appendChild(createParagraphElement(`Email Address: ${logInInfo.emailAddress}`));
    logInLogOutDiv.appendChild(createAchorElement('Logout here', logInInfo.logUrl));
  } else {
    logInLogOutDiv.innerHTML = '';
    logInLogOutDiv.appendChild(createAchorElement('Login here to add comments', logInInfo.logUrl));
  }

  /** set the display of the elements that depends on comments similar to the one for @var userCommentsForm */
  const displayCommentsDiv = document.getElementById('display-comments');
  displayCommentsDiv.style.display = userCommentsForm.style.display;
}

/**
 * Creates an <a> element containing text 
 * @param {String} text holds the destination of the link
 * @param {String} url - url for the href attribute of the link
 * @returns {HTMLAnchorElement} with the text and url
 */
function createAchorElement(text, url) {
  const anchorElement = document.createElement('a');
  anchorElement.appendChild(document.createTextNode(text));
  anchorElement.title = text;
  anchorElement.href = url;
  return anchorElement;
}

/** 
 * create img element to display the image submitted by user
 * @param {String} imageUrl - String that holds the source url for the image submitted by the user
 * @returns {HTMLImageElement} containing the image uploaded by the user
 */
function createImgElement(imageUrl) {
  const imgElement = document.createElement('IMG');
  imgElement.setAttribute('src', imageUrl);
  imgElement.setAttribute('alt', 'Image uploaded by user');
  return imgElement;
}

/**
 * Creates an <p> element containing comments.
 * @param {String} text holds the paragraph text you would like to create
 * @returns {HTMLParagraphElement} element with the {@param text} as the inner text for the paragraph
 */
function createParagraphElement(text) {
  const paragraphElement = document.createElement('p');
  paragraphElement.appendChild(document.createTextNode(text));
  return paragraphElement;
}

/**
 * Delete all the comments from the server
 * @async
 */
async function deleteComments() {
  try {
    await fetch('/delete-data', {
      method: 'POST'
    });
  } catch (error) {
    alert('Try again!');
  } finally {
    updateNumComments();
  }
}

/** 
 * Retrieve the url for the posting the comments section form and store it
 * @async
 * @returns {JSON} containing the url where to send the form
 */
async function fetchBlobUrl() {
  const request = await fetch('/blobstore-upload-url');
  return await request.text();
}

/** 
 * Retrieve user comments and display them
 * @async
 * @param {number} numComments the maximum number of comments that the user wants to display
 * @var {number} MAX_COMMENTS the default number of comments that will be displayed if the user does not choose a number
 */
async function getUserComments(numComments = MAX_COMMENTS) {
  try {
    const response = await fetch(`/data?numComments=${numComments}`);
    const data = await response.json();

    const displayUserCommentsElement = document.getElementById('user-comments');
    /** check to make sure that there is @var displayUserCommentsElement on the current page before displaying the comments*/
    if (displayUserCommentsElement) {
      displayUserCommentsElement.innerText = '';

      for (let comment in data) {
        displayUserCommentsElement.appendChild(createParagraphElement(`${data[comment].name}: ${data[comment].comment}`));
        displayUserCommentsElement.appendChild(createImgElement(data[comment].imageUrl));
      }
    }
  } catch (error) {
    console.error('There was an error loading comments: ', error);
  }
}

/**
 * Generate a random greeting
 * @returns {String} a greeting from the list
 */
function generateRandomGreeting() {
  //TODO use google translated to get greetings in different languages
  const greetings =
    ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];
  return greeting;
}

/**
 * Get the user's maximum if they type the number in the input field
 */
function updateNumComments() {
  const userNumComments = document.getElementById('numComments').value;
  getUserComments(userNumComments);
}

/**
 * Set the action attribute value in the comments' form 
 */
function setActionAttribute() {
  const userCommentsForm = document.getElementById('comment-form');
  if (userCommentsForm) {
    userCommentsForm.action = fetchBlobUrl();
  }
}

/**
 * Change the innerHTML to a greeting and name every time use loads or refreshes
 * @async
 */
async function loadContent() {
  await checkIfUserIsLoggedIn();
  setActionAttribute();
  updateNumComments();
  const greetingsElement = document.getElementById('welcome-note');
  /** check to make sure that there is @var greetingsElement on the current page before displaying the greeting */
  if (greetingsElement) {
    greetingsElement.innerHTML = `${generateRandomGreeting()} My name is Roland`;
  }
}

window.onload = loadContent;
