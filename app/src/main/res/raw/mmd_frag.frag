//光照计算在view space中计算

uniform bool uHasToon;
uniform sampler2D uToonTexture;

uniform bool uHasTexture;
uniform sampler2D uTextrue;

uniform vec3 uLightDir;
uniform vec3 uLightColor;

uniform vec4 uDiffuse;
uniform float uSpecularPower;
uniform vec3 uSpecular;
uniform vec3 uAmbient;

varying vec2 UV;
varying vec3 normal;
varying vec3 fragPos;       //view space中片段位置

void main() {
    vec3 eyeDir = normalize(fragPos);
    vec3 lightDir = normalize(-uLightDir);
    float ln = dot(lightDir, normal);
    ln = clamp(ln + 0.5, 0.0, 1.0);

    float alpha = uDiffuse.a;
    if(alpha == 0.0) {
        discard;
    }

    vec3 diffuseColor = uDiffuse.rgb * uLightColor;
    vec3 color = diffuseColor;
    color += uAmbient;

    if(uHasToon) {
        vec3 toonColor = texture2D(uToonTexture, vec2(0.0, ln)).rgb;
        color *= toonColor;
    }

    if(uSpecularPower > 0.0) {
        vec3 halfVec = normalize(eyeDir + lightDir);
        vec3 specularColor = uSpecular * uLightColor;
        specularColor = pow(max(0.0, dot(halfVec, normal)), uSpecularPower) * specularColor;

        color += specularColor;
    }

	gl_FragColor = vec4(color, alpha);
}
