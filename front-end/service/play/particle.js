
class PlaceParticle{
    
    constructor(startVector, endDirection, color){
        this.startVector = startVector;
        this.endVector = p5.Vector.add(startVector, endDirection.mult(random(rad, rad+rad/4)));

        this.posA = createVector(this.startVector.x, this.startVector.y).add(p5.Vector.mult(endDirection, rad/2));
        this.posB = createVector(this.startVector.x, this.startVector.y);

        this.timeA = 0;
        this.timeB = 0;

        this.alpha = 255;
        this.color = color;

        this.fadeSpeed = random(10, 15);
    }

    update(){
        this.timeB += 0.02;
        this.timeA += 0.01;
        this.alpha -= this.fadeSpeed;

        this.posA = p5.Vector.lerp(this.startVector, this.endVector, this.timeA);
        this.posB = p5.Vector.lerp(this.startVector, this.endVector, this.timeB);
    }

    show(){
        stroke(0, this.alpha);
        strokeWeight(rad/18.0);

        line(this.posA.x, this.posA.y, this.posB.x, this.posB.y);

        if (this.color == "red")
            stroke(230, 127, 127, this.alpha);
        else if (this.color == "blue")
            stroke(127, 127, 230, this.alpha);

        strokeWeight(rad/20.0);
        line(this.posA.x, this.posA.y, this.posB.x, this.posB.y);
    }
}