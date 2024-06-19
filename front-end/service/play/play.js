
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
        console.log('Received from server:', event.data);
    };
}

function startPing(){
    pingInterval = setInterval(() => {
        socket.send("ping");
    }, 2000); // Send a ping every 2 seconds
}

function stopPing(){
    clearInterval(pingInterval);
}

function onSendBtnClick(){
    if (socket == null) return;
    socket.send("hello");
}