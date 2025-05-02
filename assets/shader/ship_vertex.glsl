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

void main() {
    vec4 worldPos = u_mvp * vec4(a_position, 1.0);
    v_worldPos = worldPos.xyz;

    v_normal = mat3(u_model) * a_normal;
    v_color = a_color;
    v_texCoord = a_texCoord0;
    v_textureID = a_textureID;

    gl_Position = u_mvp * vec4(a_position, 1.0);
}
