package world;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import assets.Assets;
import render.Camera;
import render.Shader;
import render.Texture;

public class WorldRenderer {
	
	private Matrix4f transform = new Matrix4f();
	private Vector2f position = new Vector2f((1024)-16, (-1024)+16),
			scale = new Vector2f(1024, 1024);
	
	private Texture texture;
	private Shader shader;
	private Camera cam;
	
	public void renderMap(Texture texture, Shader shader, Camera cam) {
		
		this.texture = texture;
		this.cam = cam;
		this.shader = shader;
		
		transform.identity().translate(position.x, position.y, 0).scale(scale.x, scale.y, 1);
		
		shader.setUniform("projection", cam.getProjection().mul(transform));
		texture.bind(0);
		Assets.getModel().render();
	}
	
	public void rescale(Vector2f rescale) {
		scale.x=rescale.x/scale.x;
		scale.y=rescale.y/scale.y;
		position.x*=scale.x;
		position.y*=scale.y;
		scale.x=rescale.x;
		scale.y=rescale.y;
		
		transform.identity().translate(position.x, position.y, 0).scale(scale.x, scale.y, 1);
	}
}
