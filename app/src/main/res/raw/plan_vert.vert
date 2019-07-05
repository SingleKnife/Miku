precision mediump float;

uniform mat4 uViewMatrix;
uniform mat4 uProjectionMatrix;
uniform mat4 uLightSpaceMatrix;
uniform bool uIsDrawingShadow;
attribute vec3 aVertex;
varying vec3 vertex;
varying vec4 lightSpaceVertex;

void main() {
    gl_Position = uProjectionMatrix * uViewMatrix * vec4(aVertex, 1.0);
    vertex = aVertex;
    if(!uIsDrawingShadow) {
    }
    lightSpaceVertex = uLightSpaceMatrix * vec4(aVertex, 1.0);
}
