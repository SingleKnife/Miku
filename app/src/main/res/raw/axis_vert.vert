uniform mat4 uViewMatrix;
uniform mat4 uProjectionMatrix;
attribute vec3 aVertex;
varying vec3 vertex;

void main() {
    gl_Position = uProjectionMatrix * uViewMatrix * vec4(aVertex, 1.0);
    vertex = aVertex;
}
