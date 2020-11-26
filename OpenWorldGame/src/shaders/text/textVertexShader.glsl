#version 140

in vec2 position;
in vec2 textureCoords;

out vec2 pass_coords;

uniform mat4 transformationMatrix;

void main(void){

	gl_Position = transformationMatrix * vec4(position, 0.0, 1.0);
	pass_coords = textureCoords;
}
