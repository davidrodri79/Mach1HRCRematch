#ifdef GL_ES
precision mediump float;
#endif

attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
attribute float a_textureID;

uniform mat4 u_mvp;
uniform mat4 u_model;

varying vec4 v_color;
varying vec3 v_normal;
varying vec2 v_texCoord;
varying float v_textureID;
varying vec3 v_worldPos;

#define MAX_SHADOW_MAPS 3

uniform mat4 u_lightVP[MAX_SHADOW_MAPS];
varying vec4 v_shadowCoord[MAX_SHADOW_MAPS];

void main() {
    vec4 worldPos = u_model * vec4(a_position, 1.0);
    v_worldPos = worldPos.xyz;

    mat3 rotation = mat3(
        u_model[0].xyz,
        u_model[1].xyz,
        u_model[2].xyz
    );
    v_normal = rotation * a_normal;

    v_color = a_color;
    v_texCoord = a_texCoord0;
    v_textureID = a_textureID;

    for(int i = 0; i < MAX_SHADOW_MAPS; i++)
        v_shadowCoord[i] = u_lightVP[i] * u_model * vec4(a_position, 1.0);  // posición del vértice en espacio de sombra

    gl_Position = u_mvp * vec4(a_position, 1.0);
}
