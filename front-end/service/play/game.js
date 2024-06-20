
const PLAYER1 = 1;
const PLAYER2 = 1;
const EMPTY = 0;

const WIDTH = 7;
const HEIGHT = 6;

let rad;

var board = [];

function setup(){
    let myCanvas = createCanvas(400, 400);
    myCanvas.parent("gameCanvas");

    for (let i=0; i<WIDTH; i++){
        let col = [];
        for (let j=0; j<HEIGHT; j++){
            col.push(floor(random(1, 3)));
        }
        board.push(col);
    }

    rad = 400/WIDTH;
}

function draw(){
    background(255);

    for (let i=0; i<WIDTH; i++){
        for (let j=0; j<HEIGHT; j++){
            let player = board[i][j];
            if (player == 1){
                fill(255, 0, 0);
                ellipse(i*rad + rad/2, j*rad + rad/2, rad*0.9, rad*0.9);
            }
            else if (player == 2){
                fill(0, 0, 255);
                ellipse(i*rad + rad/2, j*rad + rad/2, rad*0.9, rad*0.9);
            }
        }
    }
}

function parseBoard(boardStateEncoded){
    let tokens = boardStateEncoded.split(" ");
    for (let i=0; i<tokens.length; i++){
        let x = int(i%WIDTH);
        let y = int(i/WIDTH);

        board[x][y] = int(tokens[i]);
    }
}

function mousePressed(){
    let col = floor(mouseX / rad);
    if (col >= 0 && col < WIDTH && socket != null){
        sendPlayCommand(col);
    }
}