package entity;

import java.util.Random;

import org.joml.Vector2f;

import audio.AudioMaster;
import audio.Source;
import io.Window;
import render.Animation;
import render.Camera;
import world.World;

public class Sheep extends Entity{
	
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
		super(ANIM_SIZE, transform, world, WIDTH, HEIGHT, true);
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
		hasBox = true;
	}

	@Override
	public void update(float delta, Window window, Camera camera) {
		
		Vector2f movement = wander(delta, transform, 2);
		int killChance = rand.nextInt(200);
		
		if(movement.y>0){
			lastMove=0;
			useAnimation(ANIM_WALKU);
		}else if(movement.y<0){
			lastMove=1;
			useAnimation(ANIM_WALKD);
		}	
		
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
		float disBetween = (float)Math.pow(Math.pow(World.getPlayerX()-transform.pos.x, 2) + Math.pow(World.getPlayerY()-transform.pos.y, 2), .5);
		if(disBetween>30) {
			source.pause();
		} else if(disBetween<=30 && source.isPlaying()==false) {
			source.continuePlaying();
		}
		
		if(killChance == 0) {
			world.kill(this);
		}
	}

	
}
