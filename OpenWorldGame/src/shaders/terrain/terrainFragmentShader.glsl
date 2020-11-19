#version 400 core

flat in vec4 pass_color;
flat in vec3 surfaceNormal;
in vec3 toLightVector[4];
in vec3 toCameraVector;
in float visibility;

out vec4 out_Color;

uniform vec3 lightColor[4];
uniform vec3 attenuation[4];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;

void main(void) {
	vec3 normalCameraVector = normalize(toCameraVector);
	
	
	vec3 normalSurfaceNormal = normalize(surfaceNormal);
	
	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);
	
	for (int i=0; i<4; i++) {
		float distance = length(toLightVector[i]);
		float attnFact = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z*distance*distance);
		vec3 normalLightVector = normalize(toLightVector[i]);
		
		float nDot1 = dot(normalSurfaceNormal, normalLightVector);
		float brightness = max(nDot1, 0.0);
		
		vec3 lightDirection = -normalLightVector;
		vec3 reflectedLightDirection = reflect(lightDirection, normalSurfaceNormal);
		
		float specularFactor = dot(reflectedLightDirection, normalCameraVector);
		specularFactor = max(specularFactor, 0.0);
		float dampedFactor = pow(specularFactor, shineDamper);
		totalDiffuse += brightness * lightColor[i] / attnFact;
		totalSpecular += dampedFactor * lightColor[i] * reflectivity / attnFact;
	}
	
	totalDiffuse = max(totalDiffuse, 0.2);
	
	
	
	out_Color = vec4(totalDiffuse ,1.0) * pass_color + vec4(totalSpecular,1.0);
	out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);
}
