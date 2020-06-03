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


/* @return {string}
 * Generate a random greeting
 */
function generateRandomGreeting() {
    //TODO use google translated to get greetings in different languages
    const greetings =
        ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

    // Pick a random greeting.
    const greeting = greetings[Math.floor(Math.random() * greetings.length)];
    return greeting;
}

/* Retrieve user comments and display them */
async function getUserComments(){
    try{
    const response = await fetch("/data");
    const data = await response.json();

    const commentEl = document.querySelector("#user-comments");
        if(typeof(commentEl) != 'undefined' && commentEl != null){
            for(var comment in data){
                commentEl.appendChild(createElement(data[comment]));
            }
            commentEl.style.display = "block";
        }
    }
    catch(err){
        console.log("There was an error loading comments!");
    }
}

/** Creates an <p> element containing comments. */
function createElement(comment){
    const pElement = document.createElement('p');
    pElement.innerHTML = `${comment.name}: ${comment.comment}`;
    return pElement;
}

/**
 * Change the innerHTML to a greeting and name
 * every time use loads or refreshes
 */
function loadContent() {
    getUserComments();
    const greetingEl = document.getElementById("welcome-note")
    if(typeof(greetingEl) != 'undefined' && greetingEl != null){
        greetingEl.innerHTML = `${generateRandomGreeting()} My name is Roland`;
    }
}

window.onload = loadContent;
