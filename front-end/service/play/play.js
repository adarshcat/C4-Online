
let socket;
let pingInterval;

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

function parseMessageFromServer(method, param){
    if (method == "match"){
        let playerData = JSON.parse(param);
        matchStarted(playerData);
    }
    else if (method == "board"){
        parseBoard(param);
    }
}

function matchStarted(otherPlayer){

}

// functions for sending message over to the server
function sendPlayCommand(col){
    let packet = {method: "play", param: col};
    console.log(JSON.stringify(packet));
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