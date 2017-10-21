package entity;

import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import assets.Assets;
import collision.AABB;
import collision.Collision;
import io.Window;
import render.Animation;
import render.Camera;
import render.Shader;
import world.World;

public abstract class Entity {
	
	private boolean canChangeDir = true;
	private int Compass;
	private int Dis;
	private Random rand = new Random();
	
	
	protected AABB bounding_box;
	
	protected Animation[] animations;
	private int use_animation;
	
	protected Transform transform;
	
	protected World world;
	
	public Entity(int max_animations, Transform transform, World world) {

		this.animations = new Animation[max_animations];
		
		this.world = world;
		
		this.transform = transform;
		this.use_animation = 0;
		
		bounding_box = new AABB(new Vector2f(transform.pos.x, transform.pos.y), new Vector2f(transform.scale.x, transform.scale.y));
	}
	
	protected void setAnimation(int index, Animation animation) {
		animations[index] = animation;
	}
	
	public void useAnimation(int index) {
		this.use_animation = index;
	}
	
	public void move(Vector2f direction) {
		transform.pos.add(new Vector3f(direction, 0));
		
		bounding_box.getCenter().set(transform.pos.x, transform.pos.y);	
	}
	
	public abstract void update(float delta, Window window, Camera camera);
		
	
	public void checkCollisionsTiles() {
		AABB[] boxes = new AABB[25]; 
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				boxes[i + j * 5] = world.getTileBoundingBox(
							(int)(((transform.pos.x / 2) + 0.5f) - (5/2)) + i,
							(int)(((-transform.pos.y / 2) + 0.5f) - (5/2)) + j
						);
			}
		}
		
		AABB box = null;
		for(int i = 0; i< boxes.length; i++) {
			if(boxes[i] != null) {
				if(box == null) box = boxes[i];
				
				Vector2f length1 = box.getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());
				Vector2f length2 = boxes[i].getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());
				
				if(length1.lengthSquared() > length2.lengthSquared()) {
					box = boxes[i];
				}
			}
		}
		
		if(box != null) {
			Collision data = bounding_box.getCollision(box);
			if(data.isIntersecting) {
				bounding_box.correctPosition(box, data);
				transform.pos.set(bounding_box.getCenter(), 0);
			}
			
			for(int i = 0; i< boxes.length; i++) {
				if(boxes[i] != null) {
					if(box == null) box = boxes[i];
					
					Vector2f length1 = box.getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());
					Vector2f length2 = boxes[i].getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());
					
					if(length1.lengthSquared() > length2.lengthSquared()) {
						box = boxes[i];
					}
				}
			}
			
			data = bounding_box.getCollision(box);
			if(data.isIntersecting) {
				bounding_box.correctPosition(box, data);
				transform.pos.set(bounding_box.getCenter(), 0);
			}
			
		}
		
	}
	
	public void render(Shader shader, Camera camera) {
		Matrix4f target = camera.getProjection();
		target.mul(world.getWorldMatrix());
		shader.bind();
		shader.setUniform("sampler", 0);
		shader.setUniform("projection", transform.getProjection(target));
		animations[use_animation].bind(0);
		Assets.getModel().render();
	}

	public void checkCollisionsEntities(Entity entity) {
		Collision collision = bounding_box.getCollision(entity.bounding_box);
		
		if(collision.isIntersecting) {
			
			collision.distance.x /= 2;
			collision.distance.y /= 2;
			
			bounding_box.correctPosition(entity.bounding_box, collision);
			transform.pos.set(bounding_box.getCenter().x, bounding_box.getCenter().y, 0);
			
			entity.bounding_box.correctPosition(bounding_box, collision);
			entity.transform.pos.set(entity.bounding_box.getCenter().x, entity.bounding_box.getCenter().y, 0);
		}
	}
	
	protected Vector2f wander(float delta){
		Vector2f movement = new Vector2f();

		if(canChangeDir == true){
			Compass = rand.nextInt(8);
			Dis = rand.nextInt(10);
			canChangeDir = false;
		}
		
		if(Compass == 0){
			movement.add(0, 10*delta);
		}else if(Compass == 1){
			movement.add(10*delta, 10*delta);
		}else if(Compass == 2){
			movement.add(10*delta, 0);
		}else if(Compass == 3){
			movement.add(10*delta, -10*delta);
		}else if(Compass == 4){
			movement.add(0, -10*delta);
		}else if(Compass == 5){
			movement.add(-10*delta, -10*delta);
		}else if(Compass == 6){
			movement.add(-10*delta, 0);
		}else{
			movement.add(-10*delta, 10*delta);
		}
	
		return movement;
	}
}
