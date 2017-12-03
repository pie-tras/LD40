package entity;

import org.joml.Vector2f;

import io.Window;
import render.Animation;
import render.Camera;
import world.World;

public class Arrow extends Entity{

	private Vector2f velocity;
	
	public Arrow(Transform transform, World world, Vector2f pos, Vector2f velocity) {
		super(2, transform, world, 9, 3);
		setAnimation(0, new Animation(1, 2, "arrow/right"));
		setAnimation(1, new Animation(1, 2, "arrow/left"));
		this.velocity = velocity;
		hasSound = false;
		transform.pos.set(pos, 0);
		affectedByGravity = false;
		isProjectile = true;
	}

	@Override
	public void update(float delta, Window window, Camera camera) {
		Vector2f movement = new Vector2f();
		movement.add(velocity);
		move(movement);
		if(velocity.x>0) {
			useAnimation(0);
		 }else if(velocity.x<0){
			useAnimation(1);
		}
		if(projectileHit && time<50) {
			velocity.set(0,0);
			time++;
		} else if(time==50) {
			time=0;
			projectileHit = false;
			bounding_box = null;
			world.kill(this);
		}
	}
	
}