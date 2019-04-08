uniform mat4 uViewMatrix;
uniform mat4 uProjectionMatrix;
attribute vec3 aVertex;
attribute vec3 aColor;
varying vec3 color;

void main() {
    gl_Position = uProjectionMatrix * uViewMatrix * vec4(aVertex, 1.0);
    color = aColor;
}
