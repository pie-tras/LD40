package effects;

import org.joml.Vector2f;

import entity.Transform;
import font.*;
import io.Window;
import render.Animation;
import render.Camera;
import world.World;

public class Explosion extends Particle{

	public static final int ANIM_SIZE = 1;
	
	public static final int ANIM_PLAY = 0;
	
	public final static int WIDTH=16;
	public final static int HEIGHT=16;
	
	public Explosion(Transform transform, World world) {
		super(ANIM_SIZE, transform, world, WIDTH, HEIGHT);
		setAnimation(ANIM_PLAY, new Animation(9, 20, "/explosion"));
	}

	@Override
	public void update(float delta, Window window, Camera camera) {
		useAnimation(ANIM_PLAY);
		Vector2f movement = drop(delta, transform);
		move(movement);
	}

}
