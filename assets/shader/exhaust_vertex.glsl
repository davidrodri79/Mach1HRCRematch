attribute vec3 a_position;
attribute vec4 a_color;
attribute float a_gasColor;

uniform mat4 u_mvp;

varying vec4 v_color;
varying float v_gasColor;

void main() {

    v_color = a_color;
    v_gasColor = a_gasColor;
    gl_Position = u_mvp * vec4(a_position, 1.0);
}
