package collision;

import org.joml.Vector2f;

public class AABB {
	private Vector2f center, half_extent;
	
	private Vector2f VertexA, VertexB, VertexC, VertexD; //Clockwise from top left
	
	private int type;
	
	public AABB(Vector2f center, Vector2f half_extent, int type) {
		this.type = type;
		this.center = center;
		this.half_extent = half_extent;
		VertexA = center.add(-half_extent.x, half_extent.y);
		VertexB = center.add(half_extent.x, half_extent.y);
		VertexC = center.add(half_extent.x, -half_extent.y);
		VertexD = center.add(-half_extent.x, -half_extent.y);
	}
	
	public Collision getCollision(AABB box2) {
		boolean collide = false;
		Vector2f distance = box2.center.sub(center, new Vector2f());
		distance.x = (float)Math.abs(distance.x);
		distance.y = (float)Math.abs(distance.y);
		
		distance.sub(half_extent.add(box2.half_extent, new Vector2f()));
		
		collide = distance.x < 0 && distance.y < 0;
		
		//box1 is entity box2 is wall
		
		//Vertices
		
		// A   B
		
		// D   C
		
		if(box2.type==2) {
			//LD
			//System.out.println("CollisionDetected");
			collide = VertexA.distance(box2.VertexB)+VertexC.distance(box2.VertexB)<=VertexA.distance(VertexC);
			System.out.println(collide);
		}else if(box2.type==3) {
			//RD
			collide = VertexB.distance(box2.VertexA)+VertexD.distance(box2.VertexA)<=VertexB.distance(VertexD);
		}else if(box2.type==4) {
			//LU
			collide = VertexD.distance(box2.VertexC)+VertexB.distance(box2.VertexC)<=VertexD.distance(VertexB);
		}else if(box2.type==5) {
			//RU
			collide = VertexC.distance(box2.VertexD)+VertexA.distance(box2.VertexD)<=VertexC.distance(VertexA);
			
		}
		
		return new Collision(distance, collide);
	}
	
	public void correctPosition(AABB box2, Collision data) {
		Vector2f correction = box2.center.sub(center, new Vector2f());
		
		if(box2.type==2) {
			//LD
			System.out.println("Corrected");
			correction = center.sub(box2.VertexB, new Vector2f());
		}else if(type==3) {
			//RD
			correction = center.sub(box2.VertexA, new Vector2f());
		}else if(type==4) {
			//LU
			correction = center.sub(box2.VertexC, new Vector2f());
		}else if(type==5) {
			//RU
			correction = center.sub(box2.VertexD, new Vector2f());
			
		}
		
		if(data.distance.x>data.distance.y) {
			if(correction.x > 0) {
				center.add(data.distance.x, 0);
			}else{
				center.add(-data.distance.x, 0);
			}
		}else{
			if(correction.y > 0) {
				center.add(0, data.distance.y);
			}else{
				center.add(0, -data.distance.y);
			}
		}
	}
	
	public Vector2f getCenter() { return center; }
	public Vector2f getHalfExtent() { return half_extent; }
}
