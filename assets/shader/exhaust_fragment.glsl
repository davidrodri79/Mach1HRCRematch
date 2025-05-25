#ifdef GL_ES
precision mediump float;
#endif

uniform vec4 u_meshColor;

varying vec4 v_color;
varying float v_gasColor;

void main() {

    gl_FragColor = vec4(mix(vec3(1,1,1),u_meshColor,v_gasColor), v_color.a * u_meshColor.a);

}
