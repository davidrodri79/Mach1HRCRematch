#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;

varying vec2 v_texCoord;

void main() {

    vec4 texColor;

    texColor = texture2D(u_texture, v_texCoord);
    //if (texColor.a < 0.01) discard;

    gl_FragColor = texColor;

}
