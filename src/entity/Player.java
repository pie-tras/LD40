package entity;

import org.joml.*;
import org.lwjgl.glfw.*;

import audio.*;
import effects.*;
import io.*;
import render.*;
import world.*;

public class Player extends Entity{
	public static final int ANIM_IDLE = 0;
	public static final int ANIM_WALK = 1;
	public static final int ANIM_SIZE = 2;
	public final static int WIDTH=16;
	public final static	int HEIGHT=16;
	
	private boolean notOnFire = true;
	
	private boolean isFacingLeft = false;
	
	public Player(Transform transform, World world) {
		super(ANIM_SIZE, transform, world, WIDTH, HEIGHT);
		setAnimation(ANIM_IDLE, new Animation(2, 2, "player/idle"));
		setAnimation(ANIM_WALK, new Animation(3, 10, "player/walk"));
		AudioMaster.setListenerData(transform.pos.x, transform.pos.y, 0);
		hasSound = false;
	}
	
	@Override
	public void update(float delta, Window window, Camera camera) {
		Vector2f movement = new Vector2f();
		
		if(window.getInput().isKeyDown(GLFW.GLFW_KEY_W) && this.isStandingOnTile(transform.pos.x, transform.pos.y, world)) {
			gravity = 0;
			jumping = true;
		}
		if(window.getInput().isKeyDown(GLFW.GLFW_KEY_A)) {
			isFacingLeft=true;
			movement.add(-10*delta, 0);
		}
		
		if(window.getInput().isKeyDown(GLFW.GLFW_KEY_D)) {
			isFacingLeft=false;
			movement.add(10*delta, 0);
		}
		if(window.getInput().isKeyPressed(GLFW.GLFW_KEY_E)) {
			Transform transform2 = new Transform();
			Vector2f pos = new Vector2f(), velocity = new Vector2f();
			if(isFacingLeft) {
				pos.set(transform.pos.x-this.bounding_box.getHalfExtent().x, transform.pos.y);
				velocity.set(-10*delta*4,0);
			} else {
				pos.set(transform.pos.x+this.bounding_box.getHalfExtent().x, transform.pos.y);
				velocity.set(10*delta*4,0);
			}
			Arrow arrow = new Arrow(transform2, world, pos, velocity);
			world.addEntity(arrow);
		}
		
		move(movement);
		AudioMaster.setListenerData(transform.pos.x, transform.pos.y, 0);
		
		if(movement.x != 0 || movement.y!=0)
			useAnimation(ANIM_WALK);
		else
			useAnimation(ANIM_IDLE);
		
		camera.getPosition().lerp(transform.pos.mul(-world.getScale(), new Vector3f()), 0.05f);
	}
	
}