package gui;

import org.joml.*;

import assets.*;
import collision.*;
import render.*;

public class Button {
	public static final int STATE_IDLE = 0;
	public static final int STATE_SELECTED = 1;
	public static final int STATE_CLICKED = 2;
	
	private AABB boundingBox;
	
	private int selectedState;
	
	private static Matrix4f transform = new Matrix4f();
	
	public Button(Vector2f position, Vector2f scale){
		this.boundingBox = new AABB(position, scale);
	}
	
	public void render(Camera camera, SpriteSheet sheet, Shader shader){
		Vector2f position = boundingBox.getCenter(),
				scale = boundingBox.getHalfExtent();
		
		transform.identity().translate(position.x, position.y, 0).scale(scale.x, scale.y, 1);
		
		shader.setUniform("projection", camera.getProjection().mul(transform));
		sheet.bindTile(shader, 1, 1);
		Assets.getModel().render();
		
		renderSides(position, scale, camera, sheet, shader);
		renderCorners(position, scale, camera, sheet, shader);
	}
	
	private void renderSides(Vector2f position, Vector2f scale,  Camera camera, SpriteSheet sheet, Shader shader){
		
	}
	
	private void renderCorners(Vector2f position, Vector2f scale,  Camera camera, SpriteSheet sheet, Shader shader){
		
	}
}
