
async function displayWelcomeText(){
    try{
        // try getting user info for welcome
        const response = await fetch("/userinfo");
        const jsonData = await response.json();
        
        document.getElementById("welcomeTxt").textContent = `Welcome ${jsonData["username"]}!`

        // hide the login/register button since already logged in
        let authDiv = document.getElementById("authButtons")
        authDiv.style.display = "none";
    } catch(e){
        // if not logged in, display another text
        document.getElementById("welcomeTxt").textContent = `Welcome to Connect 4 online! Consider creating an account or logging in`

        let userInfo = document.getElementById("userInfo");
        userInfo.style.display = "none";
    }
}

// called when the document load is complete
function onDocumentLoaded(){
    displayWelcomeText();
}

// attach the login result checker to its appropriate function
document.addEventListener("DOMContentLoaded", onDocumentLoaded);