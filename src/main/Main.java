package main;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.openal.*;
import org.lwjgl.opengl.*;

import assets.*;
import audio.*;
import font.*;
import gui.*;
import io.*;
import render.*;
import world.*;

public class Main {
	
	@SuppressWarnings("unused")
	private float scale;
	
	private Font font;
	
	private Menu menu;
	private World world;
	private WorldRenderer map;
	
	private boolean menuOff = false;
	
	public Main() {
		Window.setCallbacks();
	
		if (!glfwInit()) {
			throw new IllegalStateException("Failed to initalize GLFW!");
		}
		
		Window window = new Window();
		window.createWindow("Forest of Insanity");
		
		GL.createCapabilities();
		
		AudioMaster.init();
		AL10.alDistanceModel(AL11.AL_EXPONENT_DISTANCE);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glEnable(GL_TEXTURE_2D);	

		Camera camera = new Camera(window.getWidth(), window.getHeight());
		
		
		Shader shader = new Shader("shader");
		Shader guiShader = new Shader("gui");
		Shader fontShader = new Shader("font");
		
		font = new Font(fontShader);
		
		Assets.initAsset();
		
		Gui gui = new Gui(guiShader);
	
		double frame_cap = 1.0/60.0;
		
		double frame_time = 0;
		int frames = 0;
		
		double time = Timer.getTime();
		double unprocessed = 0;
		
		menu  = new Menu();
		
		while(!window.shouldClose()) {
			boolean can_render = false;
			
			double time_2 = Timer.getTime();
			double passed = time_2 - time;
			unprocessed += passed;
			frame_time += passed;
			
			time = time_2;
			
			while(unprocessed >= frame_cap) {
				if(window.hasResized()){
					scale = window.getHeight()/640;
					camera.setProjection(window.getWidth(), window.getHeight());
					gui.resizeCamera(window);
					glViewport(0, 0, window.getWidth(), window.getHeight());
				}
				
				if(window.getInput().isKeyReleased(GLFW_KEY_ESCAPE)) {
 					glfwSetWindowShouldClose(window.getWindow(), true);
 				}
				if(window.getInput().isKeyPressed(GLFW_KEY_Q) && !menuOff) {

					world = new World("level", camera);
					
					map = new WorldRenderer(world);
					menuOff=true;
				}
				
				unprocessed-=frame_cap;
				can_render = true;
				
				if(menuOff) {
					world.update((float)frame_cap, window, camera);
					world.correctCamera(camera, window);
				}
				
				window.update();
				if(frame_time>=1.0) {
					frame_time = 0;
					frames = 0;
				}
				System.out.println(window.getInput().getMousePos());
			}
			
			if(can_render) {
				glClear(GL_COLOR_BUFFER_BIT);
				
				if(menuOff) {
					world.render(font, gui, map, shader, camera);
				} else {
					menu.renderMenu(shader, camera);
				}
				
				window.swapBuffers();
				frames++;
			}
		}
		
		Assets.deleteAsset();
		
		for(Source source: AudioMaster.sources) {
			source.delete();
		}
		AudioMaster.cleanUp();
		
		glfwTerminate();
	}
	
	public static void main(String[] args) {
		new Main();
	}
}
