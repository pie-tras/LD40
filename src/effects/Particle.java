package effects;

import org.joml.*;

import assets.*;
import audio.*;
import entity.*;
import io.*;
import render.*;
import world.*;

public abstract class Particle {
	
	private Shader shader;
	
	private boolean endOfLife = true;
	private float Dis = 1;
	private double waitTimeStart = 0;
	
	protected Animation[] animations;
	private int use_animation;
	
	public Transform transform;
	
	protected World world;

	protected int width;
	protected int height;
	
	protected Source source;
	
	protected Vector3f color;
	
	protected boolean shouldRemove = false;
	
	public Particle(Shader shader, Transform transform, World world, int width, int height) {

		this.shader = shader;
		
		this.animations = new Animation[1];
		
		this.world = world;
		
		this.width = width;
		this.height = height;
		
		this.transform = transform;
		this.use_animation = 0;
	}
	
	
	protected void setAnimation(int index, Animation animation) {
		animations[index] = animation;
	}
	
	public void useAnimation(int index) {
		this.use_animation = index;
	}
	
	public void move(Vector2f direction) {
		transform.pos.add(new Vector3f(direction, 0));
	}
	
	protected void regulate(){
		if(endOfLife == true){
			waitTimeStart = Timer.getTime();
			endOfLife = false;
		}
		
		if((int)(Timer.getTime()-waitTimeStart)>=Dis){
			endOfLife = true;
			shouldRemove=true;
		}
	}
	
	public abstract void update(float delta, Window window, Camera camera);
		
	
	public void render(Camera camera) {
		Matrix4f target = camera.getProjection();
		target.mul(world.getWorldMatrix());
		shader.bind();
		shader.setUniform("sampler", 0);
		shader.setUniform("fontColor", new Vector3f(color.x/255, color.y/255, color.z/255));
		shader.setUniform("projection", transform.getProjection(target).scale(width, height, 0));
		animations[use_animation].bind(0);
		Assets.getModel().render();
	}
	
	public boolean isShouldRemove() {
		return shouldRemove;
	}



}