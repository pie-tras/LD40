package world;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import assets.Assets;
import render.Camera;
import render.Shader;
import render.Texture;

public class WorldRenderer {

	private Matrix4f transform = new Matrix4f();
	
	private World world;

	public WorldRenderer(World world){
		this.world = world;
	}

	public void renderMap(Texture texture, Shader shader, Camera cam) {

		Vector2f position = new Vector2f((world.getWidth()*world.getScale())-world.getScale(), (-world.getHeight()*world.getScale())+world.getScale()),
				scale = new Vector2f((world.getWidth()*world.getScale()), (world.getWidth()*world.getScale()));
		
		transform.identity().translate(position.x, position.y, 0).scale(scale.x, scale.y, 1);
		
		shader.bind();
		
		shader.setUniform("projection", cam.getProjection().mul(transform));
		texture.bind(0);
		Assets.getModel().render();
	}

}
