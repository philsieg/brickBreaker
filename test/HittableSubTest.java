
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
//test hittable and its childs
public class HittableSubTest {
	
    private Block b;
    private Ball mb;
    private Paddle p;
    private Powerup pu;
    
	@Before
    public void setUp() {
        	b = new Block(15, 16, 17, 18, 2, "yellow.png");
        	mb = new Ball(15, 16, 17, 18, "blue.png");
        	p = new Paddle(15, 16, 10, 18, "green.png");
        	pu = new Powerup(15, 16, 17, 18, 2, "red.png");
    }
	
    //test getting the number of hits from a block
	@Test
	public void testGetHits() {
		assertEquals(b.getHits(), 2);
	}
	
	//test hitting a block
	@Test
	public void testHit() {
		assertEquals(2, b.getHits());
		b.hit();
		assertEquals(1, b.getHits());
	}
	//test getting x and y fields as well as height and width
	@Test
	public void testXandY() {
		assertEquals(15, b.x);
		assertEquals(16, b.y);
		assertEquals(17, b.width);
		assertEquals(18, b.height);
	}
	
	//test velocity features
	@Test
	public void testVeloctiyFeatures() {
		assertFalse(5.0 == mb.getvX());
		mb.setvX(5);
		assertEquals(5, mb.getvX(), 0.01);
		assertFalse(8.0 == mb.getvY());
		mb.setvY(8);
		assertEquals(8, mb.getvY(), 0.01);
	}
	
	//test moving the ball
	@Test
	public void testMove() {
		assertEquals(15, mb.x);
		assertEquals(16, mb.y);
		mb.move();
		assertEquals(15, mb.x);
		assertEquals(17, mb.y);
		mb.vX= -3;
		mb.move();
		assertEquals(12, mb.x);
		assertEquals(18, mb.y);
	}
	
	//test paddle growing
	@Test
	public void testGrow() {
		assertEquals(10, p.width);
		p.grow();
		assertEquals(20, p.width);
		p.grow();
		assertEquals(20, p.width);
	}
	
	//test paddle shrinking
	@Test
	public void testShrink() {
		assertEquals(10, p.width);
		p.shrink();
		assertEquals(5, p.width);
		p.shrink();
		assertEquals(5, p.width);
	}
	//make sure grow and shrink work with eachother and properly update internal size
	@Test
	public void testGrowAndShrink() {
		assertEquals(10, p.width);
		p.grow();
		assertEquals(20, p.width);
		p.grow();
		assertEquals(20, p.width);
		p.shrink();
		assertEquals(10, p.width);
		p.shrink();
		assertEquals(5, p.width);
		p.shrink();
		assertEquals(5, p.width);
		p.grow();
		assertEquals(10, p.width);

	}
	
	//test powerups falling
	@Test 
	public void testFall() {
		assertEquals(15, pu.x);
		assertEquals(16, pu.y);
		pu.fall();
		assertEquals(15, pu.x);
		assertEquals(18, pu.y);
	}
	
	//test whether is powerup is off the screen (assume screen has height 18)
	@Test
	public void testIsGone() {
		int height = 18;
		assertFalse(pu.isGone(height));
		pu.fall();
		assertFalse(pu.isGone(height));
		pu.fall();
		assertTrue(pu.isGone(height));
		pu.fall();
		assertTrue(pu.isGone(height));
	}
	

}
