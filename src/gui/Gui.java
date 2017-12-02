package gui;

import org.joml.*;

import font.*;
import io.*;
import render.*;
import world.*;

public class Gui {
	
	private Camera camera;
	private Shader shader;
	
	public Gui(Window window, Camera camera, Shader shader) {
		this.camera = camera;
		this.shader = shader;
	}
	
	public void resizeCamera(Window window){
		camera.setProjection(window.getWidth(), window.getHeight());
	}
	
	public void render() {
		
		//shader.bind();
		//temporary.render(camera, sheet, shader);
	}
}