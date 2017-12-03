#version 400

uniform sampler2D sampler;

uniform vec4 fogColor;

varying vec2 tex_coords;

void main() {

	vec4 tex = texture2D(sampler, tex_coords);

	gl_FragColor = vec4(tex.r*fogColor.x, tex.g*fogColor.y, tex.b*fogColor.z, tex.a*fogColor.w);

}