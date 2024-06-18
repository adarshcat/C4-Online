
async function displayWelcomeText(){
    try{
        // try getting user info for welcome
        const response = await fetch("/userinfo");
        const jsonData = await response.json();
        
        document.getElementById("welcomeTxt").textContent = `Welcome ${jsonData["username"]}!`

        // hide the login/register button since already logged in
        let authBtnCollection = document.getElementsByClassName("authButton")
        
        for (let i=0; i<authBtnCollection.length; i++){
            authBtnCollection[i].hidden = true;
        }
    } catch(e){
        // if not logged in, display another text
        document.getElementById("welcomeTxt").textContent = `Welcome to Connect 4 online! Consider creating an account or logging in`
    }
}

// called when the document load is complete
function onDocumentLoaded(){
    displayWelcomeText();
}

// attach the login result checker to its appropriate function
document.addEventListener("DOMContentLoaded", onDocumentLoaded);