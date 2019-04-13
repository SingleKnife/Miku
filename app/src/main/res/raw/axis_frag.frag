precision mediump float;
varying vec3 vertex;

void main() {
    vec3 color;
    if(vertex.x != 0.0) {
        color = vec3(1.0, 0.0, 0.0);
    } else if(vertex.y != 0.0) {
        color = vec3(0.0, 1.0, 0.0);
    } else if(vertex.z != 0.0) {
        color = vec3(0.0, 0.0, 1.0);
    }
    gl_FragColor = vec4(color, 1.0);
}
