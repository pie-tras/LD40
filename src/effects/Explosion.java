package effects;

import org.joml.*;

import entity.*;
import io.*;
import render.*;
import world.*;

public class Explosion extends Particle{

	public static final int ANIM_PLAY = 0;
	
	public Explosion(Shader shader, Transform transform, World world, int scale, Vector3f color) {
		super(shader, transform, world, scale, scale);	
		this.color = color;
		setAnimation(ANIM_PLAY, new Animation(12, 20, "/explosion"));
	}

	@Override
	public void update(float delta, Window window, Camera camera) {
		regulate();
		useAnimation(ANIM_PLAY);
	}
}
