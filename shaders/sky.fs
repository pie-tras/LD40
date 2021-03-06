#version 400

uniform sampler2D sampler;

uniform vec3 skyColor;

varying vec2 tex_coords;

void main() {

	vec4 tex = texture2D(sampler, tex_coords);

	gl_FragColor = vec4(tex.r*skyColor.x, tex.g*skyColor.y, tex.b*skyColor.z, tex.a);

}