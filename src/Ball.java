
public class Ball extends Hittable{
	
	//create fields for velocity
	double vX;
	double vY;
	
	public Ball(int x, int y, int w, int h, String filename) {
		super(x, y, w, h, filename);
		
		//set initial velocity for new ball
		this.vX = 0; 
		this.vY = 1;
	}
	
	//return whether or not the ball is moving
	public boolean isMoving() {
		return (vX != 0 && vY !=0);
	}
	//move the ball
	public void move() {
		this.x += this.vX;
		this.y += this.vY;
	}
	
	//getters and setters for two velcoity components
	public double getvX() {
		return vX;
	}

	public void setvX(double vX) {
		this.vX = vX;
	}

	public double getvY() {
		return vY;
	}

	public void setvY(double vY) {
		this.vY = vY;
	}
	
	

}
