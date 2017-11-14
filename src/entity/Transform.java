package entity;

import org.joml.*;

public class Transform {

	public Vector3f pos;
	public Vector3f scale;
	
	public Transform() {
		pos = new Vector3f();
		scale = new Vector3f(1,1,1);
	}
	
	public Matrix4f getProjection(Matrix4f target) {
		target.translate(pos);
		target.scale(scale);
		return target;
	}
	
	public void setScale(float scaleSize) {
		this.scale = new Vector3f(scale.x*scaleSize, scale.y*scaleSize, scale.z*scaleSize);
	}
	
}
