#version 140

in vec3 position;


out float visibility;



uniform float density;
uniform float gradient;

uniform vec3 skyColor;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform mat4 transformationMatrix;


void main(void){

	vec4 worldPosition = transformationMatrix * vec4(position,1.0);
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCam;
	
	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distance*density), gradient));
	visibility = clamp(visibility, 0.0, 1.0);
	if (visibility > 0.5) {
		visibility = 1.0;
	}
}