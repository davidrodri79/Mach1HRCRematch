attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord;

uniform mat4 u_mvp;
uniform mat4 u_model;

varying vec4 v_color;
varying vec3 v_worldPos;
varying vec2 v_texCoord;

void main() {
    vec4 worldPos = u_model * vec4(a_position, 1.0);
    v_worldPos = worldPos.xyz;

    //v_color = a_color;
    v_texCoord = a_texCoord;

    gl_Position = u_mvp * vec4(a_position, 1.0);
}
