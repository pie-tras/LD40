package entity;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import audio.AudioMaster;
import io.Window;
import render.Animation;
import render.Camera;
import world.World;

public class ShadowPlayer extends Entity{
	public static final int ANIM_IDLEL = 0;
	public static final int ANIM_IDLER = 1;
	public static final int ANIM_WALKL = 2;
	public static final int ANIM_WALKR = 3;
	public static final int ANIM_JUMPL = 4;
	public static final int ANIM_JUMPR = 5;
	public static final int ANIM_SIZE = 6;
	public final static int WIDTH=16;
	public final static	int HEIGHT=16;
	
	private Player player;
	
	public ShadowPlayer(Transform transform, World world) {
		super(ANIM_SIZE, transform, world, WIDTH, HEIGHT);
		setAnimation(ANIM_IDLER, new Animation(2, 2, "player/idle/right"));
		setAnimation(ANIM_IDLEL, new Animation(2, 2, "player/idle/left"));
		setAnimation(ANIM_WALKL, new Animation(8, 14, "player/walk/left"));
		setAnimation(ANIM_WALKR, new Animation(8, 14, "player/walk/right"));
		setAnimation(ANIM_JUMPL, new Animation(1, 2, "player/jump/left"));
		setAnimation(ANIM_JUMPR, new Animation(1, 2, "player/jump/right"));
		AudioMaster.setListenerData(transform.pos.x, transform.pos.y, 0);
		hasSound = false;
		isShadow = true;
	}
	
	@Override
	public void update(float delta, Window window, Camera camera) {
		this.player = world.getPlayer();
		checkDead();
		Vector2f movement = new Vector2f();
		
		if(checkCollisionsEntities(player)) {
			if(player.getInsanity()<1) {
				player.setInsanity(player.getInsanity()+.015f);
			}
			player.health -= .01f;
		}
		
		if(this.isStandingOnTile(transform.pos.x, transform.pos.y, world)) {
			gravity = 0;
			jumping = true;
		}
		if(world.getPlayerX()<this.transform.pos.x) {
			isFacingLeft=true;
			movement.add(-5*delta, 0);
		}
		
		if(world.getPlayerX()>this.transform.pos.x) {
			isFacingLeft=false;
			movement.add(5*delta, 0);
		}
		
		
		if(movement.x<0) {
			isFacingLeft=true;
		}else if(movement.y>0) {
			isFacingLeft=false;
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
	}
}