#version 400 core

// VAO Array Variables for this vertex only
in vec3 position;
in vec3 normal;
in vec4 materialColor;

// Outputs leading to fragment shader inputs
out vec3 surfaceNormal;
out vec3 toLightVector[4];
out vec3 toCameraVector;
out float visibility;
out vec4 pass_materialColor;

// Non-VAO Variables  --  Inputs from *Shader.java
uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[4];
uniform float useFakeLighting;
uniform float density;
uniform float gradient;


void main(void) {
	vec4 worldPosition = transformationMatrix * vec4(position,1.0);
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCam;  // gl_position Tells OpenGL the screen coords for the vertex  -- probably
	
	pass_materialColor = materialColor;
	
	vec3 actualNormal = normal;
	
	if (useFakeLighting > 0.5) {
		actualNormal = vec3(0.0,1.0,0.0);
	}
	
	surfaceNormal = (transformationMatrix * vec4(actualNormal,00.0)).xyz;
	for (int i=0;i<4;i++) {
		toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}
	toCameraVector = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz - worldPosition.xyz;
	
	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distance*density), gradient));
	visibility = clamp(visibility, 0.0, 1.0);
	
}