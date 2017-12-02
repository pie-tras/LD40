package world;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import assets.Assets;
import render.Camera;
import render.Shader;
import render.Texture;

public class WorldRenderer {

	private Matrix4f transform = new Matrix4f();
	
	private Shader skyShader = new Shader("sky");
	
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
	
	public void renderSky(Texture texture, Camera cam, Vector3f color) {

		Vector2f position = new Vector2f((world.getWidth()*world.getScale())-world.getScale(), (-world.getHeight()*world.getScale())+world.getScale()),
				scale = new Vector2f((world.getWidth()*world.getScale()), (world.getWidth()*world.getScale()));
		
		transform.identity().translate(position.x, position.y, 0).scale(scale.x, scale.y, 1);
		
		skyShader.bind();
		skyShader.setUniform("sampler", 0);
		skyShader.setUniform("fontColor", new Vector3f(color.x/255, color.y/255, color.z/255));
		skyShader.setUniform("projection", cam.getProjection().mul(transform));
	
		texture.bind(0);
		
		Assets.getModel().render();
	}

}
