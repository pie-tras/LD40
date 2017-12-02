package entity;

import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import assets.Assets;
import audio.Source;
import collision.AABB;
import collision.Collision;
import collision.TriggerBox;
import io.Timer;
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
	private double waitTimeStart = 0;
	
	protected boolean jumping;
	
	protected float gravity = 0;

	protected AABB bounding_box;
	protected TriggerBox trigger_box;	
	
	protected Animation[] animations;
	private int use_animation;
	
	public Transform transform;
	
	protected World world;
	
	protected int width;
	protected int height;
	
	protected boolean isTriggered = false;
	
	public boolean shouldUpdate=true;
	
	protected Source source;
	
	public boolean isAlive = true;
	public boolean affectedByGravity = true;
	public boolean isProjectile = false;
	public boolean hasSound;
	public boolean projectileHit = false;
	
	public int time = 0;
	
	public Entity(int max_animations, Transform transform, World world, int width, int height) {

		this.animations = new Animation[max_animations];
		
		this.world = world;
		
		this.width = width;
		this.height = height;
		
		this.transform = transform;
		this.use_animation = 0;
		
		trigger_box = new TriggerBox(new Vector2f(transform.pos.x, transform.pos.y), new Vector2f(width/16, height/16));
		bounding_box = new AABB(new Vector2f(transform.pos.x, transform.pos.y), new Vector2f(width/16, height/16));
	}
	
	
	protected void setAnimation(int index, Animation animation) {
		animations[index] = animation;
	}
	
	public void useAnimation(int index) {
		this.use_animation = index;
	}
	
	public void move(Vector2f direction) {
		if(affectedByGravity) {
			if(this.isStandingOnTile(transform.pos.x, transform.pos.y, world)) {
				gravity=0;
			}
		
		
			if(this.isStandingOnTile(transform.pos.x, transform.pos.y, world) && jumping ){
			//jumping
				if(gravity>-3) {
					gravity-=1;
				}
			}else if(!this.isStandingOnTile(transform.pos.x, transform.pos.y, world)){
				if(gravity<1) {
					gravity+=.05;
					jumping=false;
				} else if(jumping) {
					if(gravity>-3) {
						gravity-=1;
					}
				}
			
			//falling
			}
		}
		
		if(transform.pos.y+direction.y-gravity<-126) {
			transform.pos.add(new Vector3f(direction, 0));
		}else {
			transform.pos.add(new Vector3f(direction.x, direction.y-gravity, 0));
		}
		
		if(transform.pos.x+direction.x<0){
			transform.pos.set(0, transform.pos.y, 0);
			if(isProjectile) projectileHit=true;
		}
		if(transform.pos.x+direction.x>126){
			transform.pos.set(126, transform.pos.y, 0);
			if(isProjectile) projectileHit=true;
		} 
		if(transform.pos.y+direction.y-gravity>0){
			transform.pos.set(transform.pos.x, 0, 0);
		} 
		if(transform.pos.y+direction.y-gravity<-126){
			transform.pos.set(transform.pos.x, -126, 0);
		}
		
		trigger_box.getCenter().set(transform.pos.x, transform.pos.y);	
		bounding_box.getCenter().set(transform.pos.x, transform.pos.y);	

		
		isTriggered = checkCollisionsTriggers();
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
				if(isProjectile) {
					projectileHit = true;
				}
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
					if(isProjectile) {
						projectileHit = true;
					}
			}
			
		}
		
	}
	
	public boolean checkCollisionsTriggers() {
		TriggerBox[] boxes = new TriggerBox[25]; 
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				boxes[i + j * 5] = world.getTileTriggerBox(
							(int)(((transform.pos.x / 2) + 0.5f) - (5/2)) + i,
							(int)(((-transform.pos.y / 2) + 0.5f) - (5/2)) + j
						);
			}
		}
		
		TriggerBox box = null;
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
			Collision data = trigger_box.getCollision(box);
			if(data.isIntersecting) {
				return true;
			}else {
				return false;
			}
			
		}
		
		return false;
		
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
		if(entity.bounding_box!=null) {
			Collision collision = bounding_box.getCollision(entity.bounding_box);
		
			if(collision.isIntersecting) {
			
				collision.distance.x /= 2;
				collision.distance.y /= 2;
				
				bounding_box.correctPosition(entity.bounding_box, collision);
				transform.pos.set(bounding_box.getCenter().x, bounding_box.getCenter().y, 0);
			
				entity.bounding_box.correctPosition(bounding_box, collision);
				entity.transform.pos.set(entity.bounding_box.getCenter().x, entity.bounding_box.getCenter().y, 0);
		
				if(isProjectile) {
					projectileHit = true;
				}
				if(entity.isProjectile) {
					entity.projectileHit = true;
				}
			}
		}
	}
	
	
	protected Vector2f wander(float delta, Transform transform, int speed){
		Vector2f movement = new Vector2f();

		if(canChangeDir == true){
			waitTimeStart = Timer.getTime();
			Compass = rand.nextInt(10);
			Dis = rand.nextInt(10)+1;
			canChangeDir = false;
		}
		
		if((int)(Timer.getTime()-waitTimeStart)==Dis){
			canChangeDir = true;
		}
		
		if(Compass == 0){
			movement.add(0, speed*delta);
		}else if(Compass == 1){
			movement.add(speed*delta, speed*delta);
		}else if(Compass == 2){
			movement.add(speed*delta, 0);
		}else if(Compass == 3){
			movement.add(speed*delta, -speed*delta);
		}else if(Compass == 4){
			movement.add(0, -speed*delta);
		}else if(Compass == 5){
			movement.add(-speed*delta, -speed*delta);
		}else if(Compass == 6){
			movement.add(-speed*delta, 0);
		}else if(Compass == 7){
			movement.add(-speed*delta, speed*delta);
		}else{
	
		}
		
		
		return movement;
	}

	public Source getSource() {
		return this.source;
	}
	
	public boolean isTriggered() {
		return isTriggered;
	}

	public boolean isStandingOnTile(float positionX, float positionY, World world) {
		return world.isTile((int)((positionX/2)) , ((-(int)positionY + 4) / 2) - 2 + 1);
	}
	
	public AABB getBoundingBox() {
		return bounding_box;
	}
	
	public void removeBoundingBox() {
		bounding_box=null;
	}
	
}