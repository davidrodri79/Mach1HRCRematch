#ifdef GL_ES
precision mediump float;
#endif

uniform vec4 u_meshColor;

void main() {

    gl_FragColor = vec4(u_meshColor);

}
