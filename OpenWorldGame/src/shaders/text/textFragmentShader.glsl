#version 140

in vec2 pass_coords;

out vec4 out_Color;

uniform vec3 color;
uniform sampler2D fontMap;

void main(void){

	out_Color = new vec4(color, texture(fontMap,pass_coords).a);

}
