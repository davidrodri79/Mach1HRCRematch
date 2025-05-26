attribute vec3 a_position;
attribute vec4 a_color;
attribute float a_gasColor;

uniform mat4 u_mvp;

void main() {

    gl_Position = u_mvp * vec4(a_position, 1.0);
}
