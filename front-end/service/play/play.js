
let socket

function onPlayBtnClick(){
    socket = new WebSocket('/');

    socket.onopen = function(event) {
        console.log('WebSocket is open now.');
    };
    
    socket.onclose = function(event) {
        console.log('WebSocket is closed now.');
    };
    
    socket.onerror = function(error) {
        console.error('WebSocket error:', error);
    };
    
    socket.onmessage = function(event) {
        console.log('Received from server:', event.data);
    };
}

function onSendBtnClick(){
    if (socket == null) return;
    socket.send("hello");
}