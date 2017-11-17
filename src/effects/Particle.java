package effects;

import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import assets.Assets;
import audio.Source;
import entity.Transform;
import io.Timer;
import io.Window;
import render.Animation;
import render.Camera;
import render.Shader;
import world.World;

public abstract class Particle {
	
	private boolean endOfLife = true;
	private int dx, dy, max;
	private int Dis;
	private Random rand = new Random();
	private double waitTimeStart = 0;
	
	protected Animation[] animations;
	private int use_animation;
	
	public Transform transform;
	
	protected World world;
	
	protected int width;
	protected int height;
	
	protected Source source;
	
	protected boolean shouldRemove = false;
	
	public Particle(int max_animations, Transform transform, World world, int width, int height) {

		this.animations = new Animation[max_animations];
		
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
	
	protected Vector2f drop(float delta, Transform transform){
		Vector2f movement = new Vector2f();

		if(endOfLife == true){
			waitTimeStart = Timer.getTime();
			dx = rand.nextInt(20)-10;
			dy = rand.nextInt(20)-10;
			Dis = rand.nextInt(5)+1;
			endOfLife = false;
		}
		
		if((int)(Timer.getTime()-waitTimeStart)==Dis){
			endOfLife = true;
			shouldRemove=true;
		}
		
		movement.add(dx*delta, dy*delta);
		
		return movement;
	}
	
	public abstract void update(float delta, Window window, Camera camera);
		
	
	public void render(Shader shader, Camera camera) {
		Matrix4f target = camera.getProjection();
		target.mul(world.getWorldMatrix());
		shader.bind();
		shader.setUniform("sampler", 0);
		shader.setUniform("projection", transform.getProjection(target));
		animations[use_animation].bind(0);
		Assets.getModel().render();
	}
	
	public boolean isShouldRemove() {
		return shouldRemove;
	}



}