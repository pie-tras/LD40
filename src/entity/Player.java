package entity;

import org.joml.*;
import org.lwjgl.glfw.*;

import audio.*;
import effects.*;
import io.*;
import render.*;
import world.*;

public class Player extends Entity{
	public static final int ANIM_IDLEL = 0;
	public static final int ANIM_IDLER = 1;
	public static final int ANIM_WALKL = 2;
	public static final int ANIM_WALKR = 3;
	public static final int ANIM_JUMPL = 4;
	public static final int ANIM_JUMPR = 5;
	public static final int ANIM_SIZE = 6;
	public final static int WIDTH=16;
	public final static	int HEIGHT=16;
	
	private float insanity=0f; // 0 to 1
	
	private boolean notOnFire = true;
	
	private boolean isFacingLeft = false;
	
	public Player(Transform transform, World world) {
		super(ANIM_SIZE, transform, world, WIDTH, HEIGHT);
		setAnimation(ANIM_IDLER, new Animation(2, 2, "player/idle/right"));
		setAnimation(ANIM_IDLEL, new Animation(2, 2, "player/idle/left"));
		setAnimation(ANIM_WALKL, new Animation(8, 14, "player/walk/left"));
		setAnimation(ANIM_WALKR, new Animation(8, 14, "player/walk/right"));
		setAnimation(ANIM_JUMPL, new Animation(1, 2, "player/jump/left"));
		setAnimation(ANIM_JUMPR, new Animation(1, 2, "player/jump/right"));
		AudioMaster.setListenerData(transform.pos.x, transform.pos.y, 0);
		hasSound = false;
	}
	
	@Override
	public void update(float delta, Window window, Camera camera) {
		checkDead();
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
			
			if(getInsanity()<1) {
				setInsanity(getInsanity() + .1f);
			}
		}
		
		move(movement);
		AudioMaster.setListenerData(transform.pos.x, transform.pos.y, 0);
		
		if(this.gravity!=0) {
			if(isFacingLeft){
				useAnimation(ANIM_JUMPL);
			}else {
				useAnimation(ANIM_JUMPR);
			}
		}else {
			
			if(movement.x != 0) {
				if(movement.x>0) {
					useAnimation(ANIM_WALKR);
				}else{
					useAnimation(ANIM_WALKL);
				}
			}else{
				if(isFacingLeft){
					useAnimation(ANIM_IDLEL);
				}else {
					useAnimation(ANIM_IDLER);
				}
			}
		}
		
		camera.getPosition().lerp(transform.pos.mul(-world.getScale(), new Vector3f()), 0.05f);
	}

	public float getInsanity() {
		return insanity;
	}

	public void setInsanity(float insanity) {
		this.insanity = insanity;
	}
	
}