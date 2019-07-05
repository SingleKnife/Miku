precision mediump float;

uniform sampler2D uShadowMap;
uniform bool uIsDrawingShadow;
varying vec4 lightSpaceVertex;

float calculateShadow() {
    vec3 projCoords = lightSpaceVertex.xyz / lightSpaceVertex.w;
    projCoords = projCoords * 0.5 + 0.5;

    float cloestDepth = texture2D(uShadowMap, projCoords.xy).r;
    float currentDepth = projCoords.z;

    float shadow = currentDepth - 0.005> cloestDepth ? 1.0 : 0.0;

    return shadow;
}

void main() {
    if(uIsDrawingShadow) {
        return;
    }
    vec4 color = vec4(1.0, 0.0, 0.0, 1.0);
    float shadow = calculateShadow();
    color = color * (1.0 - shadow);
    gl_FragColor = color;
}
