package gui;

import io.*;
import render.*;

public class Gui {
	private Shader shader;
	private Camera camera;
	private SpriteSheet sheet;
	
	private Button temporary;
	
	public Gui(Window window) {
		shader = new Shader("gui");
		camera = new Camera(window.getWidth(), window.getHeight());
		sheet = new SpriteSheet("gui.png", 10);
		//temporary = new Button(new Vector2f(0, 0), new Vector2f(96, 32));
	}
	
	public void resizeCamera(Window window){
		camera.setProjection(window.getWidth(), window.getHeight());
	}
	
	public void render() {
		//shader.bind();
		//temporary.render(camera, sheet, shader);
	}
}