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


/** @return {string} */
function generateRandomGreeting() {
    //TODO use google translated to get greetings in different languages
    const greetings =
        ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

    // Pick a random greeting.
    var greeting = greetings[Math.floor(Math.random() * greetings.length)];
    return greeting;
}

/**
 * Change the innerHTML to a greeting and name
 * every time use loads or refreshes
 */
function loadName() {
    fetch("/data").then(response => response.json()).then((user) => {
        document.getElementById("welcome-note").append(`${generateRandomGreeting()} My name is ${user[0]} ${user[1]}`);
        document.getElementById("welcome-note").style.visibility = "visible";
        document.getElementById("tooltipText").innerText = `Birthday: ${user[2]}\nSchool: ${user[3]}\nMajor: ${user[4]}`;
    });
}
