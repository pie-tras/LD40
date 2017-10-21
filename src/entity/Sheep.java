package entity;

import org.joml.Vector2f;

import io.Window;
import render.Animation;
import render.Camera;
import world.World;

public class Sheep extends Entity{
	public static final int ANIM_IDLEL = 0;
	public static final int ANIM_WALKL = 1;
	public static final int ANIM_SIZE = 2;

	public Sheep(Transform transform, World world) {
		super(ANIM_SIZE, transform, world);
		setAnimation(ANIM_IDLEL, new Animation(2, 2, "sheep/idleL"));
		setAnimation(ANIM_WALKL, new Animation(4, 10, "sheep/walkL"));
	}

	@Override
	public void update(float delta, Window window, Camera camera) {
		
		Vector2f movement = wander(delta);
		
		if(movement.x != 0 || movement.y!=0)
			useAnimation(ANIM_WALKL);
		else
			useAnimation(ANIM_IDLEL);
		
		move(movement);
	}

}
