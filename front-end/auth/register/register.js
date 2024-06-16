
// update page according to url params

function updateOnStatusParams(){
    const urlParams = new URLSearchParams(window.location.search);
    const registerResult = urlParams.get("status");

    if (registerResult == "present"){
        document.getElementById("message").textContent = "Username or Email already exists!";
    } else if (registerResult == "failed"){
        document.getElementById("message").textContent = "Registration failed!";
    }
}

// called when the document load is complete
function onDocumentLoaded(){
    updateOnStatusParams();
}

// attach the login result checker to its appropriate function
document.addEventListener("DOMContentLoaded", onDocumentLoaded);
