package entity;

import java.util.Random;

import io.Window;
import render.Animation;
import render.Camera;
import world.World;

public class Particle extends Entity{
	public static final int ANIM_IDLE = 0;
	public static final int ANIM_WALK = 1;
	public static final int ANIM_SIZE = 2;
	public final static int WIDTH=16;
	public final static int HEIGHT=16;
	private int timer = 10;

	public boolean hasBox = false;
	
	private Random rand = new Random();
	
	public Particle(Transform transform, World world) {
		super(ANIM_SIZE, transform, world, WIDTH, HEIGHT, false);
		setAnimation(ANIM_IDLE, new Animation(1, 2, "player/idle"));
		setAnimation(ANIM_WALK, new Animation(3, 10, "player/walk"));
		hasSound = false;
		hasBox = false;
	}
	
	@Override
	public void update(float delta, Window window, Camera camera) {
		if(timer == 0) {
			useAnimation(ANIM_WALK);
			int x = rand.nextInt(10)-5;
			int y = rand.nextInt(10)-5;
			transform.pos.set(transform.pos.x+x, transform.pos.y+y, 0);
			timer = 10;
		} else {
			timer--;
		}
		
		//world.kill(this);
	}
	
}
