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

function getUserName(){
    const userName =  fetch("/data").then(response => response.text()).then(name);
    return userName;
}

/** @return {string} */
function generateRandomGreeting(){
    //TODO use google translated to get greetings in different languages
    const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

    // Pick a random greeting.
    const greeting = greetings[Math.floor(Math.random() * greetings.length)];
    return greeting;
}

/**
 * Change the innerHTML to a greeting
 * every time use loads or refreshes
 */
function onLoadWindow(){
    document.getElementById("welcome-note").innerHTML = generateRandomGreeting().concat(" My name is ", getUserName());
}


window.onload = onLoadWindow;
