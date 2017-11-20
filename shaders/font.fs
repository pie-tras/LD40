#version 400

uniform sampler2D sampler;

uniform vec3 fontColor;

varying vec2 tex_coords;

void main() {

	vec4 tex = texture2D(sampler, tex_coords);

	gl_FragColor = vec4(tex.r*fontColor.x, tex.g*fontColor.y, tex.b*fontColor.z, tex.a);

}