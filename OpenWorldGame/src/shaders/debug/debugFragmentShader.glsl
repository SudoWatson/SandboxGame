#version 140

in float visibility;


out vec4 out_Color;


uniform vec3 skyColor;
uniform vec3 color;


void main(void){

	out_Color = new vec4(color,1);
	out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);

}