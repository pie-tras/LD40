package world;

import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.imageio.*;

import org.joml.*;

import collision.*;
import entity.*;
import io.*;
import render.*;

public class World {
	private AABB[] bounding_boxes;
	private List<Entity> entities;
	private int width;
	private int height;
	private int scale;
	
	private Texture map;
	
	private Matrix4f world;
	
	public World(String world, Camera camera){
		
		map = new Texture("Map.png");
		
		try {
			BufferedImage bound_sheet = ImageIO.read(new File("./levels/" + world + "/tiles2.png"));
			BufferedImage entity_sheet = ImageIO.read(new File("./levels/" + world + "/entities.png"));
		
			width  = bound_sheet.getWidth();
			height  = bound_sheet.getHeight();
			
			scale=16;
			
			this.world = new Matrix4f().setTranslation(new Vector3f(0));
			this.world.scale(scale);
			
			int[] colorTileSheet = bound_sheet.getRGB(0, 0, width, height, null, 0, width);
			int[] colorEntitySheet = entity_sheet.getRGB(0, 0, width, height, null, 0, width);
			
			bounding_boxes = new AABB[width * height];
			entities = new ArrayList<Entity>();
			
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
							Player player = new Player(transform, this);
							entities.add(player);
							camera.getPosition().set(transform.pos.mul(-scale, new Vector3f()));
							break;
						case 2:
							Sheep sheep = new Sheep(transform, this);
							entities.add(sheep);
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
	
	public Matrix4f getWorldMatrix() { return world; }
	
	public void render(WorldRenderer renderer, Shader shader, Camera cam) {
		renderer.renderMap(map, shader, cam);

		for(Entity entity : entities) {
			entity.render(shader, cam);
		}
	}
	
	public void update(float delta, Window window, Camera camera) {
		for(Entity entity : entities) {
			entity.update(delta, window, camera);
		}
		
		for(int i = 0; i < entities.size(); i++) {
			entities.get(i).checkCollisionsTiles();
			for(int j = i+1; j< entities.size(); j++) {
				entities.get(i).checkCollisionsEntities(entities.get(j));
			}
			entities.get(i).checkCollisionsTiles();
		}
	}
	
	public void correctCamera(Camera camera, Window window) {
		Vector3f pos = camera.getPosition();
		
		int w = -width*scale * 2;
		int h = height*scale * 2;
		
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
	
	public int getScale() { return scale; }
}
