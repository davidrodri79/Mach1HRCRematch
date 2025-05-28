#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;

uniform vec3 u_fogColor;
uniform float u_fogStart;
uniform float u_fogEnd;
uniform vec3 u_meshColor;


varying vec3 v_worldPos;
varying vec4 v_color;
varying vec2 v_texCoord;

void main() {

    vec4 texColor;

    texColor = texture2D(u_texture, v_texCoord);

    vec3 finalColor = mix(u_meshColor, vec3(1,1,1), texColor.a);

    // --- FOG EFFECT ---
    float height = v_worldPos.y; // Altura
    float fogFactor = clamp((height - u_fogEnd) / (u_fogStart - u_fogEnd), 0.0, 1.0);

    vec3 colorWithFog = mix(u_fogColor, finalColor, fogFactor);
    gl_FragColor = vec4(colorWithFog, 1.0);

}
