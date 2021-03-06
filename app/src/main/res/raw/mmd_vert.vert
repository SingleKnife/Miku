uniform mat4 uProjectionMatrix;
uniform mat4 uViewMatrix;
uniform mat4 uModelMatrix;
uniform mat4 uBoneMatrices[50];
uniform vec3 uLightDir;
uniform mat4 uLightSpaceMatrix;
uniform bool uIsDrawingShadow;

attribute vec3 aPosition;
attribute vec3 aNormal;
attribute vec2 aUV;
attribute vec2 aBoneIndices;
attribute vec2 aBoneWeightAndEdgeFlag;

varying vec2 UV;
varying vec3 normal;
varying vec3 fragPos;
varying vec3 lightDir;
varying float edgeFlag;

void main() {
    vec4 position = vec4(aPosition.x, aPosition.y, aPosition.z, 1.0);
    mat4 modelViewMatrix = uViewMatrix * uModelMatrix;

    mat4 firstBoneMatrix = uBoneMatrices[int(aBoneIndices.x)];
    mat4 secondBoneMatrix = uBoneMatrices[int(aBoneIndices.y)];
    float firstBoneWeight = aBoneWeightAndEdgeFlag.x * 0.01;
    mat4 boneMatrix = firstBoneMatrix * firstBoneWeight + secondBoneMatrix * (1.0 - firstBoneWeight);
    position = boneMatrix * position;
    position.w = 1.0;
//    vec3 animNormal = mat3(boneMatrix) * vec3(aNormal.x, aNormal.y, -aNormal.z);
    vec3 animNormal = mat3(uModelMatrix) * mat3(boneMatrix) * aNormal;

    gl_Position = uProjectionMatrix * modelViewMatrix * position;

    if(uIsDrawingShadow) {
        return;
    }
    edgeFlag = aBoneWeightAndEdgeFlag.y;
    UV = aUV;
    normal = animNormal;
    fragPos = (uModelMatrix * position).xyz;
    lightDir = normalize(uLightDir);
}
