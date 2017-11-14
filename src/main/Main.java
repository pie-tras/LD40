package main;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.opengl.GL;

import assets.Assets;
import audio.AudioMaster;
import audio.Source;
import gui.Gui;
import io.Timer;
import io.Window;
import render.Camera;
import render.Shader;
import world.World;
import world.WorldRenderer;

public class Main {
	
	public Main() {
		Window.setCallbacks();
	
		if (!glfwInit()) {
			throw new IllegalStateException("Failed to initalize GLFW!");
		}
		
		Window window = new Window();
		window.setSize(860, 640);
		window.setFullscreen(false);
		window.createWindow("Swarm");
		
		GL.createCapabilities();
		
		AudioMaster.init();
		AL10.alDistanceModel(AL11.AL_EXPONENT_DISTANCE);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		Camera camera = new Camera(window.getWidth(), window.getHeight());
		
		glEnable(GL_TEXTURE_2D);	
		
		WorldRenderer tiles = new WorldRenderer();
		
		Shader shader = new Shader("shader");
		
		Assets.initAsset();
		
		World world = new World("testLevel", camera, tiles);
		
		Gui gui = new Gui(window);
		
		double frame_cap = 1.0/60.0;
		
		double frame_time = 0;
		int frames = 0;
		
		double time = Timer.getTime();
		double unprocessed = 0;
		
		while(!window.shouldClose()) {
			boolean can_render = false;
			
			double time_2 = Timer.getTime();
			double passed = time_2 - time;
			unprocessed += passed;
			frame_time += passed;
			
			time = time_2;
			
			while(unprocessed >= frame_cap) {
				if(window.hasResized()){
					camera.setProjection(window.getWidth(), window.getHeight());
					gui.resizeCamera(window);
					glViewport(0, 0, window.getWidth(), window.getHeight());
				}
				
				unprocessed-=frame_cap;
				can_render = true;
	
				if(window.getInput().isKeyReleased(GLFW_KEY_ESCAPE)) {
					glfwSetWindowShouldClose(window.getWindow(), true);
				}
				
				world.update((float)frame_cap, window, camera);
				
				world.correctCamera(camera, window);
				
				window.update();
				if(frame_time>=1.0) {
					frame_time = 0;
					System.out.println("FPS: " + frames);
					frames = 0;
				}
			}
			
			if(can_render) {
				glClear(GL_COLOR_BUFFER_BIT);
				
				world.render(tiles, shader, camera);
			
				gui.render();
				
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
