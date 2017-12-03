package gui;

import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWVidMode;

import assets.Assets;
import io.Window;
import render.Camera;
import render.Shader;
import render.Texture;

public class Gui {
	
	GLFWVidMode vid = glfwGetVideoMode(glfwGetPrimaryMonitor());
	Camera camera = new Camera(vid.width(), vid.height());
	private Shader shader;
	
	private Matrix4f transform = new Matrix4f();
	
	private float width = vid.width();
	private float height = vid.height();

	public Gui(Shader shader) {
		this.shader = shader;
	}
	
	public void resizeCamera(Window window){
		camera.setProjection(window.getWidth(), window.getHeight());
	}
	
	public void renderGui(Texture t, Vector2f position, Vector2f scale, Vector3f color) {
		
		transform.identity().translate(position.x, position.y, 0).scale(scale.x, scale.y, 1);
		
		shader.bind();
		shader.setUniform("sampler", 0);
		shader.setUniform("guiColor", new Vector3f(color.x/255, color.y/255, color.z/255));
		shader.setUniform("projection", camera.getProjection().mul(transform));
	
		t.bind(0);
		Assets.getModel().render();
	
	}
	
	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}
}