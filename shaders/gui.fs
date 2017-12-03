#version 400

uniform sampler2D sampler;

uniform vec3 guiColor;

varying vec2 tex_coords;

void main() {

	vec4 tex = texture2D(sampler, tex_coords);

	gl_FragColor = vec4(tex.r*guiColor.x, tex.g*guiColor.y, tex.b*guiColor.z, tex.a);

}