
const PLAYER1 = 1;
const PLAYER2 = 2;
const EMPTY = 0;

const WIDTH = 7;
const HEIGHT = 6;

let rad;

let board = [];

let aspectRatio = 7.0/6.0;
let paddingVert = 0.65;
let paddingHor = 0.90;

function windowResized() {
    if (windowWidth >= windowHeight){
        let winHeight = windowHeight * paddingVert;
        resizeCanvas(winHeight * aspectRatio, winHeight);
    } else{
        let winWidth = windowWidth * paddingHor;
        resizeCanvas(winWidth, winWidth / aspectRatio);
    }

    rad = width/WIDTH;
}

function setup(){
    let myCanvas = createCanvas(400, 400 / aspectRatio);
    myCanvas.parent("gameCanvas");
    myCanvas.className += "gameCanvas";

    windowResized();
    
    clearBoard();
}

function draw(){
    clear();

    // draw the board
    for (let i=0; i<WIDTH; i++){
        for (let j=0; j<HEIGHT; j++){
            let player = board[i][j];
            if (player == PLAYER1){
                fill(255, 0, 0);
                ellipse(i*rad + rad/2, j*rad + rad/2, rad*0.9, rad*0.9);
            }
            else if (player == PLAYER2){
                fill(0, 0, 255);
                ellipse(i*rad + rad/2, j*rad + rad/2, rad*0.9, rad*0.9);
            }
        }
    }
}

function clearBoard(){
    for (let i=0; i<WIDTH; i++){
        let col = [];
        for (let j=0; j<HEIGHT; j++){
            col.push(floor(j/3) + 1);
        }
        board.push(col);
    }
}

function parseBoardFromServer(boardStateEncoded){
    let tokens = boardStateEncoded.split(" ");
    for (let i=0; i<tokens.length; i++){
        let x = int(i%WIDTH);
        let y = int(i/WIDTH);

        board[x][y] = int(tokens[i]);
    }
}

function mousePressed(){
    if (mouseX < 0 || mouseX > width || mouseY < 0 || mouseY > height) return;

    let col = floor(mouseX / rad);
    if (col >= 0 && col < WIDTH && socket != null){
        sendPlayCommand(col);
    }
}