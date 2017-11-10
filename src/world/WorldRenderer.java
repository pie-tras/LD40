package world;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import assets.Assets;
import render.Camera;
import render.Shader;
import render.Texture;

public class WorldRenderer {
	
	private Matrix4f transform = new Matrix4f();
	
	public void renderMap(Texture texture, Shader shader, Camera cam) {
		Vector2f position = new Vector2f((1024)-16, (-1024)+16),
				scale = new Vector2f(1024, 1024);
		
		transform.identity().translate(position.x, position.y, 0).scale(scale.x, scale.y, 1);
		
		shader.setUniform("projection", cam.getProjection().mul(transform));
		texture.bind(0);
		Assets.getModel().render();
	}
}
