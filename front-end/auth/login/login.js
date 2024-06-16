
// update page according to url params

function updateOnStatusParams(){
    const urlParams = new URLSearchParams(window.location.search);
    const loginResult = urlParams.get("status");

    if (loginResult == "failed"){
        document.getElementById("message").textContent = "Login failed, incorrect credentials";
    } else if (loginResult == "registered"){
        document.getElementById("message").textContent = "Login with your new account";
    }
}

// called when the document load is complete
function onDocumentLoaded(){
    updateOnStatusParams();
}

// attach the login result checker to its appropriate function
document.addEventListener("DOMContentLoaded", onDocumentLoaded);
