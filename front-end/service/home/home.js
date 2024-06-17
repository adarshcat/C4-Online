
async function updateWelcomeText(){
    try{
        const response = await fetch("/userinfo");
        const jsonData = await response.json();
        
        document.getElementById("welcomeTxt").textContent = `Welcome ${jsonData["username"]}!`
    } catch(e){
        console.log("request failed");
    }
}

// called when the document load is complete
function onDocumentLoaded(){
    updateWelcomeText();
}

// attach the login result checker to its appropriate function
document.addEventListener("DOMContentLoaded", onDocumentLoaded);