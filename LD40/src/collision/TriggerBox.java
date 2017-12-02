package collision;

import org.joml.Vector2f;

public class TriggerBox {

private Vector2f center, half_extent;
	
	public TriggerBox(Vector2f center, Vector2f half_extent) {
		this.center = center;
		this.half_extent = half_extent;
	}
	
	public Collision getCollision(TriggerBox box2) {
		Vector2f distance = box2.center.sub(center, new Vector2f());
		distance.x = (float)Math.abs(distance.x);
		distance.y = (float)Math.abs(distance.y);
		
		distance.sub(half_extent.add(box2.half_extent, new Vector2f()));
		
		return new Collision(distance, distance.x < 0 && distance.y < 0);
	}
	
	public Vector2f getCenter() { return center; }
	public Vector2f getHalfExtent() { return half_extent; }
	
}
