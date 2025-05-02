#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_textures[6]; // Puedes ampliar si quieres más texturas

#define MAX_LIGHTS 4

uniform vec3 u_lightPos[MAX_LIGHTS];
uniform vec3 u_lightColor[MAX_LIGHTS];
uniform float u_lightIntensity[MAX_LIGHTS];
uniform int u_numLights;

uniform vec3 u_ambientColor;

uniform vec3 u_fogColor;
uniform float u_fogStart;
uniform float u_fogEnd;

varying vec3 v_worldPos;
varying vec4 v_color;
varying vec3 v_normal;
varying vec2 v_texCoord;
varying float v_textureID;

void main() {

    vec4 texColor;

    // --- TEXTURE * Vertex color ---

    if (v_textureID < -0.5) {
        texColor = v_color;
    } else if (v_textureID < 0.5) {
        texColor = texture2D(u_textures[0], v_texCoord);
    } else if (v_textureID < 1.5) {
        texColor = texture2D(u_textures[1], v_texCoord);
    } else if (v_textureID < 2.5) {
        texColor = texture2D(u_textures[2], v_texCoord);
    } else if (v_textureID < 3.5) {
        texColor = texture2D(u_textures[3], v_texCoord);
    } else if (v_textureID < 4.5) {
        texColor = texture2D(u_textures[4], v_texCoord);
    } else {
        texColor = texture2D(u_textures[5], v_texCoord);
    }

    // --- LIGHTING ---
    vec3 normal = normalize(v_normal);
    vec3 lightAccum = u_ambientColor;

    for (int i = 0; i < MAX_LIGHTS; i++) {
        if (i >= u_numLights) break;
        vec3 lightDir = normalize(u_lightPos[i] - v_worldPos);
        float diff = max(dot(normal, lightDir), 0.0);
        lightAccum += u_lightColor[i] * diff * u_lightIntensity[i];
    }

    vec3 finalColor = texColor.rgb * v_color.xyz * lightAccum;

    // --- FOG EFFECT ---
    float dist = length(v_worldPos); // distancia al origen de cámara
    float fogFactor = clamp((u_fogEnd - dist) / (u_fogEnd - u_fogStart), 0.0, 1.0);

    vec3 colorWithFog = mix(u_fogColor, finalColor, fogFactor);
    gl_FragColor = vec4(colorWithFog, texColor.a);

}
