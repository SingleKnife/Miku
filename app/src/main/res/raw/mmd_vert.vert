uniform mat4 uProjectionMatrix;
uniform mat4 uViewMatrix;
uniform mat4 uModelMatrix;
uniform mat4 uBoneMatrixs[50];
uniform vec3 uLightDir;

attribute vec3 aPosition;
attribute vec3 aNormal;
attribute vec2 aUV;
attribute ivec2 aBoneIndices;
attribute ivec2 aBoneWeightAndEdgeFlag;

varying vec2 UV;
varying vec3 normal;
varying vec3 fragPos;
varying vec3 lightDir;

void main() {
    vec4 position = vec4(aPosition.x, aPosition.y, aPosition.z, 1.0);
    mat4 modelViewMatrix = uViewMatrix * uModelMatrix;
    int firstBoneWeight = aBoneWeightAndEdgeFlag.x;
    int edgeFlag = aBoneWeightAndEdgeFlag.y;
	gl_Position = uProjectionMatrix * modelViewMatrix * position;
    UV = aUV;
    normal = normalize(mat3(modelViewMatrix) * aNormal);
    fragPos = (modelViewMatrix * position).xyz;
    lightDir = normalize((uViewMatrix * vec4(uLightDir, 0.0)).xyz);
}
