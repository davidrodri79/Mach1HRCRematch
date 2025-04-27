attribute vec3 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
attribute float a_textureID;

uniform mat4 u_mvp;

varying vec4 v_color;
varying vec2 v_texCoord;
varying float v_textureID;

void main() {
    v_color = a_color;
    v_texCoord = a_texCoord0;
    v_textureID = a_textureID;
    gl_Position = u_mvp * vec4(a_position, 1.0);
}
