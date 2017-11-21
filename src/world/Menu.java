package world;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWVidMode;

import assets.Assets;
import render.Camera;
import render.Shader;
import render.Texture;

public class Menu {
	
	private Matrix4f transform = new Matrix4f();
	
	private Vector2f scale = new Vector2f();
	
	private Texture map;
	
	public Menu() {
		map = new Texture("Menu.png");
		GLFWVidMode vid = glfwGetVideoMode(glfwGetPrimaryMonitor());
		scale.set(vid.width(), vid.height());
	}
	
	
	public void renderMenu(Shader shader, Camera cam) {
		
		transform.identity().translate(0, 0, 0).scale(scale.x/2, scale.y/2, 1);
		
		shader.bind();
		
		shader.setUniform("projection", cam.getProjection().mul(transform));
		map.bind(0);
		Assets.getModel().render();
	}
}
