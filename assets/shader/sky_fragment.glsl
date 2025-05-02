#ifdef GL_ES
precision mediump float;
#endif

uniform vec3 u_fogColor;
uniform float u_fogStart;
uniform float u_fogEnd;

varying vec3 v_worldPos;
varying vec4 v_color;

void main() {

    // --- FOG EFFECT ---
    float dist = length(v_worldPos); // distancia al origen de cámara
    float fogFactor = clamp((u_fogEnd - dist) / (u_fogEnd - u_fogStart), 0.0, 1.0);

    vec3 colorWithFog = mix(u_fogColor, v_color.xyz, fogFactor);
    gl_FragColor = vec4(colorWithFog, v_color.a);

}
