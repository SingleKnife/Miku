uniform mat4 uProjectionMatrix;
uniform mat4 uViewMatrix;
uniform mat4 uModelMatrix;
uniform mat4 uBoneMatrixs[50];
attribute vec3 aPosition;
attribute vec2 aUV;
attribute vec2 aBoneIndices;

void main() {
    vec4 position = vec4(aPosition.x, aPosition.y, -aPosition.z, 1.0);
	gl_Position = uProjectionMatrix * uViewMatrix * position;
}
