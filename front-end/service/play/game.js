
const PLAYER1 = 1;
const PLAYER2 = 2;
const EMPTY = 0;

const WIDTH = 7;
const HEIGHT = 6;

const BOARD_PIECE_SC = 0.95;
const COIN_SC = 0.7;

let aspectRatio = 7.0/6.0;
let paddingVert = 0.65;
let paddingHor = 0.90;

let rad;

let board = [];
let particles = [];

let turn = "none";

let redCoinSvg;
let blueCoinSvg;
let boardPieceSvg;

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

function preload(){
    redCoinSvg = loadImage("/front-end/assets/game/redCoin.svg");
    blueCoinSvg = loadImage("/front-end/assets/game/blueCoin.svg");
    boardPieceSvg = loadImage("/front-end/assets/game/boardPiece.svg");
}

function setup(){
    let myCanvas = createCanvas(400, 400 / aspectRatio);
    myCanvas.parent("gameCanvas");
    myCanvas.className += "gameCanvas";
    imageMode(CENTER);

    windowResized();
    
    clearBoard();
}

function draw(){
    clear();

    drawBoard();
    drawPieces();
    drawUI();

    for (let i=particles.length-1; i>=0; i--){
        particles[i].update();
        particles[i].show();

        if (particles[i].alpha < 0) particles.splice(i, 1);
    }
}

function drawUI(){
    if (mouseX < 0 || mouseX > width || mouseY < 0 || mouseY > height) return;
    if (turn != playerColor) return;

    let col = floor(mouseX / rad);
    let j = findNextAvailableSpot(col);

    let coinSprite;

    if (playerColor == "red") coinSprite = redCoinSvg;
    else if (playerColor == "blue") coinSprite = blueCoinSvg;
    else return;

    if (j != -1){
        tint(255, 255, 255, 150);
        image(coinSprite, col*rad + rad/2, j*rad + rad/2, rad*COIN_SC, rad*COIN_SC);
    }
}

function drawPieces(){
    noTint();
    for (let i=0; i<WIDTH; i++){
        for (let j=0; j<HEIGHT; j++){
            let player = board[i][j];
            if (player == PLAYER1){
                // fill(255, 0, 0);
                // ellipse(i*rad + rad/2, j*rad + rad/2, rad*0.9, rad*0.9);
                image(redCoinSvg, i*rad + rad/2, j*rad + rad/2, rad*COIN_SC, rad*COIN_SC);
            }
            else if (player == PLAYER2){
                // fill(0, 0, 255);
                // ellipse(i*rad + rad/2, j*rad + rad/2, rad*0.9, rad*0.9);
                image(blueCoinSvg, i*rad + rad/2, j*rad + rad/2, rad*COIN_SC, rad*COIN_SC);
            }
        }
    }
}

function drawBoard(){
    noTint();

    for (let i=0; i<WIDTH; i++){
        for (let j=0; j<HEIGHT; j++){
            image(boardPieceSvg, i*rad + rad/2, j*rad + rad/2, rad*BOARD_PIECE_SC, rad*BOARD_PIECE_SC);
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

function findNextAvailableSpot(col){
    for (let i=HEIGHT-1; i>=0; i--){
        if (board[col][i] == 0) return i;
    }

    return -1;
}

function parseBoardFromServer(boardStateEncoded){
    let tokens = boardStateEncoded.split(" ");
    for (let i=0; i<tokens.length; i++){
        let x = int(i%WIDTH);
        let y = int(i/WIDTH);

        setBoard(x, y, int(tokens[i]));
    }
}

function setBoard(x, y, value){
    if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) return;

    let prevVal = board[x][y];
    if (value != prevVal){
        board[x][y] = value;
        spawnPlaceParticles(x, y, (value == PLAYER1)?"red":"blue");
    }
}

function switchTurns(){
    if (turn == "red") turn = "blue";
    else turn = "red";
}

function playOpponent(col){
    switchTurns();
}

function mousePressed(){
    if (mouseX < 0 || mouseX > width || mouseY < 0 || mouseY > height || turn != playerColor) return;

    let col = floor(mouseX / rad);

    if (col >= 0 && col < WIDTH && socket != null){
        let j = findNextAvailableSpot(col);
        setBoard(col, j, colToPlayer(playerColor));
        sendPlayCommand(col);
        switchTurns();
    }
}

function spawnPlaceParticles(x, y, color){
    return;
    // disable particles for now, not satisfied with how they look

    let particlePos = createVector(x*rad + rad/2, y*rad + rad/2);

    const totalParts = 8;
    for (let i=0; i<totalParts; i++){
        let direction = createVector(1, 0);
        direction.setHeading((i/totalParts) * 3.14*2.0);
        
        particles.push(new PlaceParticle(p5.Vector.add(particlePos, p5.Vector.mult(direction, rad/4)), direction, color));
    }
}