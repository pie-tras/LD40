package world;

import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

import audio.AudioMaster;
import audio.Source;
import collision.AABB;
import collision.TriggerBox;
import effects.Particle;
import entity.Entity;
import entity.Player;
import entity.ShadowPlayer;
import entity.Transform;
import font.Font;
import gui.Gui;
import io.Timer;
import io.Window;
import render.Camera;
import render.Shader;
import render.Texture;

public class World {
	private AABB[] bounding_boxes;
	private TriggerBox[] trigger_boxes;
	private boolean[][] jumpPermitted;
	
	private List<Entity> entities;
	private List<Entity> entitiesAdd;
	private List<Particle> particles;
	
	//private List<Entity> entityKill;
	
	private double spawnTime = 0, timeSurvived = 0, tempDisable = -100;
	private boolean disabled = false;
	
	private float fogDen;
	private boolean clearing = false;
	
	private int width;
	private int height;
	private float scale;
	
	private Shader particleShader = new Shader("particle");
	
	private Texture map, map2, sky, fog, bar, barBase, dieScreen;
	
	private int coolDown;
	
	private Matrix4f world;
	
	public static Player player;
	
	public World(String world, Camera camera){
		
		map = new Texture("Map.png");
		map2 = new Texture("Map2.png");
		sky = new Texture("Sky.png");
		fog = new Texture("Fog.png");
		bar = new Texture("bar.png"); 
		barBase = new Texture("barBase.png");
		dieScreen = new Texture("gameOver.png");
		
		try {
			BufferedImage bound_sheet = ImageIO.read(new File("./levels/" + world + "/bounds.png"));
			BufferedImage entity_sheet = ImageIO.read(new File("./levels/" + world + "/entities.png"));
			BufferedImage trigger_sheet = ImageIO.read(new File("./levels/" + world + "/triggers.png"));
			
			
			width  = bound_sheet.getWidth();
			height  = bound_sheet.getHeight();
			
			GLFWVidMode vid = glfwGetVideoMode(glfwGetPrimaryMonitor());
			scale=vid.width()/64;
			
			this.world = new Matrix4f().setTranslation(new Vector3f(0));
			this.world.scale(scale);
			
			int[] colorTileSheet = bound_sheet.getRGB(0, 0, width, height, null, 0, width);
			int[] colorEntitySheet = entity_sheet.getRGB(0, 0, width, height, null, 0, width);
			int[] colorTriggerSheet = trigger_sheet.getRGB(0, 0, width, height, null, 0, width);
			
			jumpPermitted = new boolean[width][height];
			bounding_boxes = new AABB[width * height];
			trigger_boxes = new TriggerBox[width * height];
			
			entities = new ArrayList<Entity>();
			entitiesAdd = new ArrayList<Entity>();
			particles = new ArrayList<Particle>();
			
			//entityKill = new ArrayList<Entity>();
			
			Transform transform;
			
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					int red = (colorTileSheet[x + y * width] >> 16) & 0xFF;
					int triggerRed = (colorTriggerSheet[x + y * width] >> 16) & 0xFF;
					int entity_index = (colorEntitySheet[x + y * width] >> 16)& 0xFF;
					int entity_alpha = (colorEntitySheet[x + y * width] >> 24)& 0xFF;
					
					if(red>0) {
						bounding_boxes[x + y * width] = new AABB(new Vector2f(x*2, -y*2), new Vector2f(1,1));
						jumpPermitted[x][y] = true;
					}
					
					if(triggerRed>0) {
						trigger_boxes[x + y * width] = new TriggerBox(new Vector2f(x*2, -y*2), new Vector2f(1,1));
					}
					
					if(entity_alpha > 0){
						transform = new Transform();
						transform.pos.x = x*2;
						transform.pos.y = -y*2;
						switch(entity_index){
						case 1:
							player = new Player(transform, this);
							entities.add(player);
							spawnTime = Timer.getTime();
							camera.getPosition().set(transform.pos.mul(-scale, new Vector3f()));
							break;
						default:
							
							break;
						}
					}
				}
			}
			
			for(int i = 0; i<10; i++) {
				transform = new Transform();
				transform.pos.x = 0;
				transform.pos.y = 0;
				ShadowPlayer sp = new ShadowPlayer(transform, this);
				sp.shouldUpdate = false;
				entities.add(sp);
			}	
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int buffer = AudioMaster.loadSound("audio/Insanity.wav");
		Source source = new Source();
		source.setLooping(true);
		source.play(buffer);
		source.setStuff();
		source.setPosition(64,64,0);
		AudioMaster.sources.add(source);
		
	}
	
	public static Player getPlayer() {
		return player;
	}

	public Matrix4f getWorldMatrix() { return world; }
	
	public void render(Font font, Gui gui, WorldRenderer renderer, Shader shader, Camera cam) {
		
		renderer.renderSky(sky, cam, new Vector3f(75, 45, 65));
		
		renderer.renderMap(map, shader, cam);
	
		for(Entity entity : entities) {
			if(entity.shouldUpdate) {	
				if(entity.isShadow()) {
					entity.renderShadow(cam, new Vector4f(170, 0, 00, fogDen/100));
				}else {	
					entity.render(shader, cam);
				}
			}
		}
		
		if(!player.isTriggered()) {
			renderer.renderMap(map2, shader, cam);
		}
		
		for(Particle p : particles) {
			if(!p.isShouldRemove()) {
				p.render(cam);
			}
		}
		
		renderer.renderFog(fog, cam, new Vector4f(fogDen, fogDen/10, fogDen/10, fogDen/100), fogDen);
		
		gui.renderGui(barBase, new Vector2f(-300, 200), new Vector2f(110, 129), new Vector3f(70, 0, 0));
		gui.renderGui(bar, new Vector2f(-400+player.getInsanity()*100, 220), new Vector2f(player.getInsanity()*100, 100), new Vector3f(95, 0, 0));
			
		font.render("Insanity: "+ ((int)(100*player.getInsanity())+ "%"), new Vector2f(-295, 300), new Vector2f(7, 7), new Vector3f(255, 0, 0));
				
		gui.renderGui(barBase, new Vector2f(300, 200), new Vector2f(110, 129), new Vector3f(0, 70, 0));
		gui.renderGui(bar, new Vector2f(200+((int)(player.getHealth())), 220), new Vector2f((int)player.getHealth(), 100), new Vector3f(0, 150, 0));
			
		font.render("Health: "+ ((int)(player.getHealth())+ "/" + ((int)(player.MAX_HEALTH))), new Vector2f(300, 300), new Vector2f(6, 6), new Vector3f(0, 250, 0));
			
		font.render("Time Survived: " + (int)timeSurvived + " seconds", new Vector2f(0, 200), new Vector2f(10, 10), new Vector3f(255, 255, 255));
		
		if(!player.isAlive) {
			gui.renderGui(dieScreen, new Vector2f(0, 0), new Vector2f(500, 500), new Vector3f(159, 133, 121));
			font.render("YOU HAVE PERISHED!", new Vector2f(0, 300), new Vector2f(16, 16), new Vector3f(195, 0, 0));
			
			font.render("Time Survived: " + (int)timeSurvived + " seconds", new Vector2f(0, 100), new Vector2f(16, 16), new Vector3f(255, 255, 255));
		
			int totalPoints = (((int)timeSurvived));
	
			String comment = "";
			
			if(totalPoints>0 && totalPoints<=10) {
				comment = "\"HOW DID YOU FAIL??? THIS IS EASY!!!\"";
			}else if(totalPoints>10 && totalPoints<50) {
				comment = "\"wow so amazing. (Sarcasm)\"";
			}else{
				comment = "\"Mediocre, I could do better\"";
			}
			
			font.render(comment, new Vector2f(0, -100), new Vector2f(8, 8), new Vector3f(0, 100, 205));
			
			font.render("--Press Esc to quit--     --Press Enter to restart-- ", new Vector2f(0, -200), new Vector2f(8, 8), new Vector3f(0, 205, 205));
		}
	}
	
	public void update(float delta, Window window, Camera camera) {
		
		if(player.isAlive) {
			timeSurvived = (Timer.getTime()-spawnTime);
		}else {
			if(window.getInput().isKeyDown(GLFW.GLFW_KEY_ENTER)) {
				player.setInsanity(0);
				player.setHealth(player.MAX_HEALTH);
				player.transform.pos.x = 62;
				player.transform.pos.y = 102;
				player.isAlive = true;
				player.shouldUpdate = true;
				spawnTime = Timer.getTime();
			}
		}
		
		if(((int) (100-(player.getInsanity()*100))/10)<=0) {
			coolDown=1;
		}else{
			coolDown = ((int) (100-(player.getInsanity()*100))/10);
		}
		
		
		
		for(Entity entity : entities) {
	
			if(Timer.getTime()-tempDisable>=coolDown){
				disabled=false;
			}
				
			if(!disabled){
					
				if(entity.isShadow() && !entity.shouldUpdate) {
					entity.transform.pos = new Vector3f(getPlayerX(), getPlayerY()+15, 1);
					entity.shouldUpdate = true; 
					tempDisable = Timer.getTime();
					disabled = true;
				}
				
				if(entity.isShadow() && entity.shouldUpdate && player.isTriggered()) {
					entity.transform.pos = new Vector3f(getPlayerX(), getPlayerY()+15, 1);
					tempDisable = Timer.getTime();
					disabled = true;
				}
				
			}
			
			
			if(entity.shouldUpdate)
				entity.update(delta, window, camera);
		}
		
		for(Particle p : particles) {
			if(!p.isShouldRemove()) {
				p.update(delta, window, camera);
			}
		}
		
		
		for(int i = 0; i < entities.size(); i++) {
			if(entities.get(i).shouldUpdate) {
				entities.get(i).checkCollisionsTiles();
				for(int j = i+1; j< entities.size(); j++) {
					entities.get(i).checkCollisionsEntities(entities.get(j));
				}
				entities.get(i).checkCollisionsTiles();
			}
		}
		
		if(!entitiesAdd.isEmpty()) {
			for(int i = 0; i < entitiesAdd.size(); i++) {
				entities.add(entitiesAdd.get(i));
			}
			entitiesAdd.removeAll(entitiesAdd);
		}
		
		if(fogDen<(player.getInsanity()*100) && !clearing) {
			fogDen+=.5f;
		}
		
		if(fogDen>=((player.getInsanity()*100)-1)) {
			clearing=true;
		}
		
		if(fogDen>0 && clearing){
			fogDen-=.5f;
		}
		
		if(fogDen<=0) {
			clearing=false;
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
			if(x + y * width<bounding_boxes.length && x + y * width > 0) {
				return bounding_boxes[x + y * width];
			}else {
				return null;
			}
		}catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public TriggerBox getTileTriggerBox(int x, int y) {
		try {
			return trigger_boxes[x + y * width];
		}catch(ArrayIndexOutOfBoundsException e){
			return null;
		}
	}
	
	public boolean isTile(int x, int y) {
		try {
			if(y==height) {
				return true;
			}else {
				return jumpPermitted[x][y];
			}
		}catch(ArrayIndexOutOfBoundsException e){
			return false;
		}
	}
	
	
	public void kill(Entity e) {
		e.shouldUpdate=false;
		if(e.hasSound) {
			e.getSource().delete();
			e.hasSound = false;
		}
		if(e != player) {
			e.removeBoundingBox();
		}
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

	public Shader getParticleShader() {
		return particleShader;
	}
	
	public void addEntity(Entity e) {
		entitiesAdd.add(e);
	}
	
	public double getSurviveTime() {
		return timeSurvived;
	}

	
}