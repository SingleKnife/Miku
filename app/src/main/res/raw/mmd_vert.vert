uniform mat4 uProjectionMatrix;
uniform mat4 uViewMatrix;
uniform mat4 uModelMatrix;
uniform mat4 uBoneMatrixs[50];
attribute vec3 aPosition;
attribute vec3 aNormal;
attribute vec2 aUV;
attribute vec2 aBoneIndices;

varying vec2 UV;
varying vec3 normal;
varying vec3 fragPos;

void main() {
    vec4 position = vec4(aPosition.x, aPosition.y, -aPosition.z, 1.0);
	gl_Position = uProjectionMatrix * uViewMatrix * position;

    UV = aUV;
    normal = normalize(mat3(uViewMatrix) * aNormal);
    fragPos = (uViewMatrix * position).xyz;
}
