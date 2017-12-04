package entity;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import assets.Assets;
import audio.Source;
import collision.AABB;
import collision.Collision;
import collision.TriggerBox;
import io.Window;
import render.Animation;
import render.Camera;
import render.Shader;
import world.World;

public abstract class Entity {
	
	protected boolean jumping;
	
	protected int health = 100;
	public final int MAX_HEALTH = health;
	
	protected float gravity = 0;

	private Shader shadowShader = new Shader("fog");
	
	protected AABB bounding_box;
	protected TriggerBox trigger_box;	
	
	protected Animation[] animations;
	private int use_animation;
	
	public Transform transform;
	
	protected World world;
	
	protected boolean isFacingLeft = false;
	
	protected int width;
	protected int height;
	
	protected boolean isTriggered = false;
	
	public boolean shouldUpdate=true;
	
	protected Source source;
	
	protected boolean runLeft = true;
	
	public boolean isAlive = true;
	public boolean affectedByGravity = true;
	public boolean hasSound;
	
	protected boolean isShadow=false;

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
		if(checkDead())
			return;
		
		if(affectedByGravity) {
			
			if(this.isStandingOnTile(transform.pos.x, transform.pos.y, world)) {
				gravity=0;
			}
		
			if(this.isStandingOnTile(transform.pos.x, transform.pos.y, world) && jumping ){
			//jumping
				if(gravity>-7f) {
					gravity-=1.2f;
				}
			}else if(!this.isStandingOnTile(transform.pos.x, transform.pos.y, world)){
				if(gravity<1f) {
					gravity+=.07f;
					jumping=false;
				} else if(jumping) {
					if(gravity>-7f) {
						gravity-=1.2f;
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
		}
		if(transform.pos.x+direction.x>126){
			transform.pos.set(126, transform.pos.y, 0);
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

	
	public int getHealth() {
		return health;
	}


	public abstract void update(float delta, Window window, Camera camera);
	
	public boolean checkDead() {
		if(health<=0) {
			world.kill(this);
			return true;
		}
		return false;
	}
	
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
	
	public void renderShadow(Camera camera, Vector4f color) {
		Matrix4f target = camera.getProjection();
		target.mul(world.getWorldMatrix());
		shadowShader.bind();
		shadowShader.setUniform("sampler", 0);
		shadowShader.setUniform("fogColor", new Vector4f(color.x/255, color.y/255, color.z/255, color.w));
		shadowShader.setUniform("projection", transform.getProjection(target));
		animations[use_animation].bind(0);
		Assets.getModel().render();
	}

	public boolean checkCollisionsEntities(Entity entity) {
		if(entity.bounding_box!=null) {
			Collision collision = bounding_box.getCollision(entity.bounding_box);
		
			if(collision.isIntersecting) {
			
				collision.distance.x /= 2;
				collision.distance.y /= 2;
				
				bounding_box.correctPosition(entity.bounding_box, collision);
				transform.pos.set(bounding_box.getCenter().x, bounding_box.getCenter().y, 0);
			
				entity.bounding_box.correctPosition(bounding_box, collision);
				entity.transform.pos.set(entity.bounding_box.getCenter().x, entity.bounding_box.getCenter().y, 0);
		
				return true;
			}
		}
		
		return false;
	}
	
	public Source getSource() {
		return this.source;
	}
	
	public boolean isTriggered() {
		return isTriggered;
	}
	
	private double roundUp(double x, double f) {
		return f * Math.ceil(x / f);
	}

	public boolean isStandingOnTile(float positionX, float positionY, World world) {
		
		int posX = (int)roundUp(positionX, 2)/2;
		
		int posY = -(int)roundUp(positionY, 2)/2;

		return world.isTile((int)((positionX + 4) / 2) - 2 , ((-(int)positionY + 4) / 2) - 2 + 1) || world.isTile((int)posX , (int)posY+1);

	}	
	
	public boolean isTileLeft(float positionX, float positionY, World world) {
		
		int posX = (int)roundUp(positionX, 2)/2;
		
		int posY = -(int)roundUp(positionY, 2)/2;

		return world.isTile(((int)((positionX + 4) / 2) - 2)-1 , ((-(int)positionY + 4) / 2) - 2) || world.isTile((int)posX-1 , (int)posY);

	}	
	
	public boolean isTileRight(float positionX, float positionY, World world) {
		
		int posX = (int)roundUp(positionX, 2)/2;
		
		int posY = -(int)roundUp(positionY, 2)/2;

		return world.isTile(((int)((positionX + 4) / 2) - 2)+1 , ((-(int)positionY + 4) / 2) - 2) || world.isTile((int)posX+1 , (int)posY);

	}	
	
	public boolean isEdgeTileLeft(float positionX, float positionY, World world) {
		
		int posX = (int)roundUp(positionX, 2)/2;
		
		int posY = -(int)roundUp(positionY, 2)/2;

		return !(world.isTile(((int)((positionX + 4) / 2) - 2)-1 , ((-(int)positionY + 4) / 2) - 2 + 1) || world.isTile((int)posX-1 , (int)posY+1));

	}	
	
	public boolean isEdgeTileRight(float positionX, float positionY, World world) {
		
		int posX = (int)roundUp(positionX, 2)/2;
		
		int posY = -(int)roundUp(positionY, 2)/2;

		return !(world.isTile(((int)((positionX + 4) / 2) - 2)+1 , ((-(int)positionY + 4) / 2) - 2 + 1) || world.isTile((int)posX+1 , (int)posY+1));

	}	
	
	public AABB getBoundingBox() {
		return bounding_box;
	}
	
	public void removeBoundingBox() {
		bounding_box=null;
	}
	

	public Transform getTransform() {
		return transform;
	}
	
	public boolean isShadow() {
		return isShadow;
	}


	
}