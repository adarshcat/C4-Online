
let socket;
let pingInterval;

let playerColor = "blue";

function onPlayBtnClick(){
    socket = new WebSocket('/');

    socket.onopen = function(event) {
        console.log('WebSocket is open now.');
        startPing();
    };
    
    socket.onclose = function(event) {
        console.log('WebSocket is closed now.');
        stopPing();
    };
    
    socket.onerror = function(error) {
        console.error('WebSocket error:', error);
    };
    
    socket.onmessage = function(event) {
        let jsonPacket = JSON.parse(event.data);
        let method = jsonPacket.method;
        let param = jsonPacket.param;

        console.log('method:', method, "  param:", param);

        parseMessageFromServer(method, param)
    };
}

function changeTheme(){
    var logoBlue = document.getElementById("logoBlue");
    var logoRed = document.getElementById("logoRed");

    if (playerColor == "red"){
        document.body.style.setProperty("--backgroundCol", "hsl(0, 30%, 23%)");
        document.body.style.setProperty("--infoPanelCol", "hsl(0, 30%, 28%)");
        document.body.style.setProperty("--btnCol", "hsl(0, 100%, 65%)");

        logoRed.style.display = "block";
        logoBlue.style.display = "none";
    } else{
        document.body.style.setProperty("--backgroundCol", "hsl(240, 30%, 23%)");
        document.body.style.setProperty("--infoPanelCol", "hsl(240, 30%, 28%)");
        document.body.style.setProperty("--btnCol", "hsl(240, 100%, 65%)");

        logoRed.style.display = "none";
        logoBlue.style.display = "block";
    }
}

function onDocumentLoaded(){
    changeTheme();
}

function parseMessageFromServer(method, param){
    if (method == "match"){
        let playerData = JSON.parse(param);
        matchStarted(playerData);
    }
    else if (method == "board"){
        parseBoardFromServer(param);
    }
}

function matchStarted(otherPlayer){
    // TODO: update the player card in the html with relavant data and start the match I guess
}


// functions for sending message over to the server
function sendPlayCommand(col){
    let packet = {method: "play", param: col};
    socket.send(JSON.stringify(packet));
}


// function for send periodic pings to the server
function startPing(){
    pingInterval = setInterval(() => {
        socket.send(`{"method": "ping", "param": ""}`);
    }, 2000); // Send a ping every 2 seconds
}

function stopPing(){
    clearInterval(pingInterval);
}
// ----------------------------------------------


// button click callbacks
function onSendBtnClick(){
    if (socket == null) return;
    socket.send(`{"method": "hello", "param": ""}`);
}


// attach the login result checker to its appropriate function
document.addEventListener("DOMContentLoaded", onDocumentLoaded);