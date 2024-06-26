
let socket;
let pingInterval;

let playerColor = "none";

let opponentTime;
let myTime;

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

        parseMessageFromServer(method, param);
    };

    // hide the play button so that it can't be pressed again and show the matchmaking loader
    let playBtn = document.getElementById("playBtn");
    let matchmakingLoader = document.getElementById("matchmakingLoader");
    let cancelMatchmakingBtn = document.getElementById("cancelMatchmakingBtn");

    playBtn.style.display = "none";
    matchmakingLoader.style.display = "block";
    cancelMatchmakingBtn.style.display = "block";
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
    } else if (playerColor == "blue"){
        document.body.style.setProperty("--backgroundCol", "hsl(240, 30%, 23%)");
        document.body.style.setProperty("--infoPanelCol", "hsl(240, 30%, 28%)");
        document.body.style.setProperty("--btnCol", "hsl(240, 100%, 65%)");

        logoRed.style.display = "none";
        logoBlue.style.display = "block";
    } else{
        document.body.style.setProperty("--backgroundCol", "hsl(240, 0%, 23%)");
        document.body.style.setProperty("--infoPanelCol", "hsl(240, 0%, 28%)");
        document.body.style.setProperty("--btnCol", "hsl(240, 0%, 25%)");

        logoRed.style.display = "none";
        logoBlue.style.display = "block";
    }
}

async function onDocumentLoaded(){
    changeTheme();

    // check url parameters and redirect if autoplay is true
    const urlParams = new URLSearchParams(window.location.search);
    const autoPlay = urlParams.get("autoplay");

    if (autoPlay == "true"){
        onPlayBtnClick();
    }

    opponentTime = document.getElementById("opponentTime");
    myTime = document.getElementById("myTime");

    // update the player info
    let myData = await fetchMyInfo();

    myName.innerText = myData[0];
    myRating.innerText = "(" + myData[1] + ")";
}

function parseMessageFromServer(method, param){
    if (method == "match"){
        let playerData = JSON.parse(param);
        matchStarted(playerData);
    }
    else if (method == "board"){
        parseBoardFromServer(param);
    } else if (method == "color"){
        playerColor = param;
        changeTheme();
        turn = "red";
    } else if (method == "played"){
        let col = int(param);
        playOpponent(col);
    } else if (method == "turn"){
        turn = param;
    } else if (method == "time"){
        updateClock(param);
    } else if (method == "gameTerm"){
        let termData = JSON.parse(param);
        gameTerminated(termData);
    }
}

function updateClock(param){
    let timeData = JSON.parse(param);

    myTime.innerText = millisToClockDisplay(timeData[playerColor]);

    let opponentColor = (playerColor == "red")?"blue":"red";

    opponentTime.innerText = millisToClockDisplay(timeData[opponentColor]);
}

function millisToClockDisplay(millis){
    let secs = int(millis / 1000);
    let minute = int(secs / 60);
    let seconds = int(secs % 60);

    return String(minute).padStart(2, "0") + ": " + String(seconds).padStart(2, "0");
}

async function matchStarted(otherPlayer){
    updatePlayerCards(otherPlayer);

    let matchmakingLoader = document.getElementById("matchmakingLoader");
    let resignBtn = document.getElementById("resignBtn");
    let cancelMatchmakingBtn = document.getElementById("cancelMatchmakingBtn");

    matchmakingLoader.style.display = "none";
    cancelMatchmakingBtn.style.display = "none";
    resignBtn.style.display = "block";
}

async function fetchMyInfo(){
    try{
        // try getting user info for player card display
        const response = await fetch("/userinfo");
        const jsonData = await response.json();
        
        return [jsonData["username"], jsonData["rating"]];
    } catch(e){
        // in case of error... idk bro
        return "";
    }
}

function colToPlayer(color){
    if (color == "red") return 1;
    else if (color == "blue") return 2;
    return -1;
}

function gameTerminated(param){
    let gameResultPanel = document.getElementById("gameResultPanel");
    let resultText = document.getElementById("resultText");
    let ratingText = document.getElementById("ratingText");

    // on game termination, display the game result panel
    gameResultPanel.style.display = "block";

    if (param["reason"] == "win"){
        if (param["winner"] == playerColor)
            resultText.innerText = "You Win";
        else
            resultText.innerText = "You Lose";
    } else if (param["reason"] == "abort"){
        resultText.innerText = "Game aborted";
    } else if (param["reason"] == "draw"){
        resultText.innerText = "Draw";
    }

    let ratingChange = int(param["ratingChange"]);
    let sign = "";

    if (ratingChange < 0) sign = "-";
    else if (ratingChange > 0) sign = "+";

    ratingText.innerText = "Rating: " + sign + abs(ratingChange);

    changePlayerRating(abs(ratingChange), sign);
}

// functions for updating the player card
async function updatePlayerCards(opponentData){
    let myName = document.getElementById("myName");
    let myRating = document.getElementById("myRating");

    let opponentName = document.getElementById("opponentName");
    let opponentRating = document.getElementById("opponentRating");

    let playerData = await fetchMyInfo();

    myName.innerText = playerData[0];
    myRating.innerText = "(" + playerData[1] + ")";

    opponentName.innerText = opponentData["username"];
    opponentRating.innerText = "(" + opponentData["rating"] + ")";
}

function changePlayerRating(ratingChange, sign){
    let myRating = document.getElementById("myRating");
    let opponentRating = document.getElementById("opponentRating");

    let oppSign = (sign == "-")?" + ":" - ";
    sign = " "+sign+" ";

    let myString = myRating.innerText.substring(1, myRating.innerText.length-1) + sign + ratingChange;
    let opponentString = opponentRating.innerText.substring(1, opponentRating.innerText.length-1) + oppSign + ratingChange;

    myRating.innerText = "(" + myString + ")";
    opponentRating.innerText = "(" + opponentString + ")";
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
function onResignBtnClick(){
    // when resign button is clicked, send a resign request to the server
    let packet = {method: "resign", param: ""};
    socket.send(JSON.stringify(packet));
}

function onCancelMatchmakingBtnClick(){
    // send a request to the server to remove from the matchmaking list
    let packet = {method: "cancelMM", param: ""};
    socket.send(JSON.stringify(packet));

    // redirect to the page afterwards
    location.href = "/play";
}


// attach the login result checker to its appropriate function
document.addEventListener("DOMContentLoaded", onDocumentLoaded);