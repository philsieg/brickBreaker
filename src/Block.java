import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Block extends Hittable{
	
	//how many hits the block has
	private int hits;
	
	public Block(int x, int y, int w, int h, int hits, String filename) {
		//call super constructor but also take in an int, hits
		super(x, y, w, h, filename);
		this.hits = hits;
	}

	
	//if a block gets hit
	public void hit() {
		if (getHits() > 0) {
			this.hits --; 
		}
	}
	
	//see how many hits a block has left
	public int getHits() {
		return hits;
	}
	
	//return a boolean of whether or not the block is destroyed
	public boolean isDestroyed() {
		return (hits < 1); 
	}

	
	
}
