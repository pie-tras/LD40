package world;

import static org.lwjgl.glfw.GLFW.*;

import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.imageio.*;

import org.joml.*;
import org.lwjgl.glfw.*;

import collision.*;
import effects.Explosion;
import effects.Particle;
import entity.*;
import io.*;
import render.*;

public class World {
	private AABB[] bounding_boxes;
	
	private List<Entity> entities;
	private List<Particle> particles;
	
	private List<Entity> entityKill;
	
	private int width;
	private int height;
	private float scale;
	
	private boolean canUpdateParticle = false, canRenderParticle = false;
	
	private Texture map;
	
	private Matrix4f world;
	
	public static Player player;
	
	public World(String world, Camera camera){
		
		map = new Texture("Map.png");
		try {
			BufferedImage bound_sheet = ImageIO.read(new File("./levels/" + world + "/bounds.png"));
			BufferedImage entity_sheet = ImageIO.read(new File("./levels/" + world + "/entities3.png"));
		
			width  = bound_sheet.getWidth();
			height  = bound_sheet.getHeight();
			
			GLFWVidMode vid = glfwGetVideoMode(glfwGetPrimaryMonitor());
			scale=vid.width()/64;
			System.out.println("Scale should be 20?   "+scale);
			
			this.world = new Matrix4f().setTranslation(new Vector3f(0));
			this.world.scale(scale);
			
			int[] colorTileSheet = bound_sheet.getRGB(0, 0, width, height, null, 0, width);
			int[] colorEntitySheet = entity_sheet.getRGB(0, 0, width, height, null, 0, width);
			
			bounding_boxes = new AABB[width * height];
			
			entities = new ArrayList<Entity>();
			particles = new ArrayList<Particle>();
			
			entityKill = new ArrayList<Entity>();
			
			Transform transform;
			
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					int red = (colorTileSheet[x + y * width] >> 16) & 0xFF;
					int entity_index = (colorEntitySheet[x + y * width] >> 16)& 0xFF;
					int entity_alpha = (colorEntitySheet[x + y * width] >> 24)& 0xFF;
					
					if(red>0) {
						bounding_boxes[x + y * width] = new AABB(new Vector2f(x*2, -y*2), new Vector2f(1,1));
					}
					
					if(entity_alpha > 0){
						transform = new Transform();
						transform.pos.x = x*2;
						transform.pos.y = -y*2;
						switch(entity_index){
						case 1:
							player = new Player(transform, this);
							entities.add(player);
							camera.getPosition().set(transform.pos.mul(-scale, new Vector3f()));
							break;
						case 2:
							Sheep sheep = new Sheep(transform, this, "MistyMoor.wav");
							entities.add(sheep);
							break;
						case 3:
							Explosion e = new Explosion(transform, this);
							particles.add(e);
							break;
						default:
							
							break;
						}
					}
					
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	public void rmParticle(Particle p) {
		particles.remove(p);
	}
	
	public Matrix4f getWorldMatrix() { return world; }
	
	public void render(WorldRenderer renderer, Shader shader, Camera cam) {
		renderer.renderMap(map, shader, cam);
		
		for(Entity entity : entities) {
			entity.render(shader, cam);
		}
		
		for(Particle p : particles) {
			if(!p.isShouldRemove()) {
				p.render(shader, cam);
			}
		}
		
	}
	
	public void update(float delta, Window window, Camera camera) {
		for(Entity entity : entities) {
			entity.update(delta, window, camera);
		}
		
		for(Particle p : particles) {
			if(!p.isShouldRemove()) {
				p.update(delta, window, camera);
			}
		}
	
		
		for(int i = 0; i < entities.size(); i++) {
			entities.get(i).checkCollisionsTiles();
			for(int j = i+1; j< entities.size(); j++) {
				entities.get(i).checkCollisionsEntities(entities.get(j));
			}
			entities.get(i).checkCollisionsTiles();
		}
		
		if(!entityKill.isEmpty()) {
			for(int i = 0; i < entityKill.size(); i++) {
				if(entityKill.get(i).hasSound) {
					entityKill.get(i).getSource().delete();;
				}
				
				entities.remove(entityKill.get(i));
			}
			entityKill.removeAll(entityKill);
		}
		
		if(!player.isAlive) {
			System.out.println(" ");
			System.out.println("---------");
			System.out.println("YOU DIED!");
			System.out.println("---------");
			glfwSetWindowShouldClose(window.getWindow(), true);
		}
		
	}
	
	public void correctCamera(Camera camera, Window window) {
		Vector3f pos = camera.getPosition();
	 
	 	float w = -width*scale*2;
	 	float h = height*scale*2;
	  		
	  	if (pos.x > -(window.getWidth() / 2) + scale) pos.x = -(window.getWidth() / 2) + scale;
	  	if (pos.x < w + (window.getWidth() / 2) + scale) pos.x = w + (window.getWidth() / 2) + scale;
	 		
	 	if (pos.y < (window.getHeight() / 2) - scale) pos.y = (window.getHeight() / 2) - scale;
	 	if (pos.y > h - (window.getHeight() / 2) - scale) pos.y = h - (window.getHeight() / 2) - scale;
	}
	
	public AABB getTileBoundingBox(int x, int y) {
		try {
			return bounding_boxes[x + y * width];
		}catch(ArrayIndexOutOfBoundsException e){
			return null;
		}
	}
	
	public void kill(Entity e) {
		entityKill.add(e);
		e.isAlive = false;
	}
	
	public float getScale() { return scale; }
	
	public static float getPlayerX() {
		return player.transform.pos.x;
	}
	
	public static float getPlayerY() {
		return player.transform.pos.y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public List<Particle> getParticles() {
		return particles;
	}

	
}