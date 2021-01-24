#version 400 core

const int MAX_LIGHTS = 4;
const int MAX_JOINTS = 50;
const int MAX_WEIGHTS = 3;

// VAO Array Variables for this vertex only
in vec3 position;
in vec3 normal;
in vec4 materialColor;
in vec3 weights;
in ivec3 jointIDs;

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
uniform mat4 jointTransforms[MAX_JOINTS];
uniform vec3 lightPosition[MAX_LIGHTS];
uniform float useFakeLighting;
uniform float density;
uniform float gradient;


void main(void) {

	vec4 totalModelPos = vec4(0.0);  // Animated position
	vec4 totalNormal = vec4(0.0);  // Animated normal
	for (int i = 0; i < MAX_WEIGHTS; i++) {
		// Calculate position
		mat4 jointTransform = jointTransforms[jointIDs[i]];
		vec4 posePosition = jointTransform * vec4(position, 1.0);
		totalModelPos += posePosition * weights[i];

		// Calculate normal
		vec4 poseNormal = jointTransform * vec4(normal, 0.0);
		totalNormal += poseNormal * weights[i];
	}

	// Convert from Z-Up to Y-Up
	vec4 finalPos = vec4(totalModelPos.x, totalModelPos.z, -totalModelPos.y, totalModelPos.w);
	vec4 finalNorm = vec4(totalNormal.x, totalNormal.z, -totalNormal.y, totalNormal.w);

	gl_Position = projectionMatrix * viewMatrix * transformationMatrix * finalPos;


	vec3 actualNormal = vec3(finalNorm.xyz);
	if (useFakeLighting > 0.5) {
		actualNormal = vec3(0.0,1.0,0.0);
	}
	
	vec4 worldPosition = transformationMatrix * totalModelPos;
	vec4 positionRelativeToCam = viewMatrix * worldPosition;

	surfaceNormal = (transformationMatrix * vec4(actualNormal,0.0)).xyz;
	for (int i=0;i<4;i++) {
		toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}
	toCameraVector = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz - worldPosition.xyz;
	
	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distance*density), gradient));
	visibility = clamp(visibility, 0.0, 1.0);

	pass_materialColor = materialColor;

}
