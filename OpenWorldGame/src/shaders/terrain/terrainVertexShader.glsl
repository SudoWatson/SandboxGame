#version 400 core

// VAO Array Variables
in vec3 position;
in vec3 color;
in vec3 normal;

flat out vec4 pass_color;

flat out vec3 surfaceNormal;
out vec3 toLightVector[4];
out vec3 toCameraVector;
out float visibility;

// Positional Matrixes
uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[4];
uniform float density;
uniform float gradient;

void main(void) {
	vec4 worldPosition = transformationMatrix * vec4(position,1.0);
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	
	gl_Position = projectionMatrix * positionRelativeToCam;
	pass_color = new vec4(color.x,color.y,color.z,1);
	
	surfaceNormal = (transformationMatrix * vec4(normal,00.0)).xyz;
	for (int i=0; i<4;i++) {
		toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}
	toCameraVector = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz - worldPosition.xyz;
	
	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distance*density), gradient));
	visibility = clamp(visibility, 0.0, 1.0);
}
