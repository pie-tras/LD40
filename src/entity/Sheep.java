package entity;

import java.lang.Math;
import java.util.Random;

import org.joml.*;

import audio.AudioMaster;
import audio.Source;
import effects.*;
import io.Timer;
import io.Window;
import render.Animation;
import render.Camera;
import world.World;

public class Sheep extends Entity{
	
	private boolean canShoot = true;
	private int wait;
	private double startTime = 0;
	
	
	private int lastMove = 0;
	
	public static final int ANIM_IDLER = 0;
	public static final int ANIM_IDLEL = 1;
	public static final int ANIM_IDLEU = 2;
	public static final int ANIM_IDLED = 3;
	
	public static final int ANIM_WALKR = 4;
	public static final int ANIM_WALKL = 5;
	public static final int ANIM_WALKU = 6;
	public static final int ANIM_WALKD = 7;
	
	public static final int ANIM_SIZE = 8;
	
	public final static int WIDTH=16;
	public final static int HEIGHT=16;
	
	private Random rand = new Random();

	public Sheep(Transform transform, World world, String file) {
		super(ANIM_SIZE, transform, world, WIDTH, HEIGHT);
		setAnimation(ANIM_IDLER, new Animation(2, 2, "sheep/idleR"));
		setAnimation(ANIM_IDLEL, new Animation(2, 2, "sheep/idleL"));
		setAnimation(ANIM_IDLEU, new Animation(2, 2, "sheep/idleU"));
		setAnimation(ANIM_IDLED, new Animation(2, 2, "sheep/idleD"));
		
		setAnimation(ANIM_WALKR, new Animation(4, 5, "sheep/walkR"));
		setAnimation(ANIM_WALKL, new Animation(4, 5, "sheep/walkL"));
		setAnimation(ANIM_WALKU, new Animation(4, 5, "sheep/walkU"));
		setAnimation(ANIM_WALKD, new Animation(4, 5, "sheep/walkD"));
		
		int buffer = AudioMaster.loadSound("audio/"+file);
		source = new Source();
		int pitch = rand.nextInt(3)+1;
		source.setLooping(true);
		source.setPitch(pitch);
		source.play(buffer);
		source.setPosition(transform.pos.x, transform.pos.y, 2);
		AudioMaster.sources.add(source);
		hasSound = true;
		Fire f = new Fire(world.getParticleShader(), transform, world, 3, new Vector3f(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
		world.getParticles().add(f);
	}

	@Override
	public void update(float delta, Window window, Camera camera) {
		checkDead();
		Vector2f movement = wander(delta, transform, 2);
		
		if(movement.x>0){
			lastMove=2;
			useAnimation(ANIM_WALKR);
		}else if(movement.x<0){
			lastMove=3;
			useAnimation(ANIM_WALKL);
		}
		
		if(movement.x == 0 && movement.y==0){
			if(lastMove==0){
				useAnimation(ANIM_IDLEU);
			}else if(lastMove==1){
				useAnimation(ANIM_IDLED);
			}else if(lastMove==2){
				useAnimation(ANIM_IDLER);
			}else{
				useAnimation(ANIM_IDLEL);
			}
		}
		
		move(movement);
		source.setPosition(transform.pos.x, transform.pos.y, 2);
		float disBetween = (float) Math.pow(Math.pow(World.getPlayerX()-transform.pos.x, 2) + Math.pow(World.getPlayerY()-transform.pos.y, 2), .5);
		if(disBetween>30) {
			source.pause();
		} else if(disBetween<=30 && source.isPlaying()==false) {
			source.continuePlaying();
		}
		
		if(impaled == true) {
			health -= 1;
			impaled = false;
			return;
		}
		
		if(canShoot == true){
			startTime = Timer.getTime();
			wait = rand.nextInt(10)+1;
			canShoot = false;
			Transform transform2 = new Transform();
			Vector2f pos = new Vector2f(), velocity = new Vector2f();
			if(world.getPlayer().getTransform().pos.x<getTransform().pos.x) {
				pos.set(transform.pos.x-this.bounding_box.getHalfExtent().x, transform.pos.y);
				velocity.set(-10*delta*4,0);
			} else {
				pos.set(transform.pos.x+this.bounding_box.getHalfExtent().x, transform.pos.y);
				velocity.set(10*delta*4,0);
			}
			Arrow arrow = new Arrow(transform2, world, pos, velocity);
			world.addEntity(arrow);
		}
		
		if((int)(Timer.getTime()-startTime)==wait){
			canShoot = true;
		}
		
	}

	
}