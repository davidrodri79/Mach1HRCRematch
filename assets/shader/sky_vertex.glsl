attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
attribute float a_textureID;

uniform mat4 u_mvp;

varying vec4 v_color;
varying vec3 v_worldPos;

void main() {
    vec4 worldPos = u_mvp * vec4(a_position, 1.0);
    v_worldPos = worldPos.xyz;

    v_color = a_color;

    gl_Position = u_mvp * vec4(a_position, 1.0);
}
