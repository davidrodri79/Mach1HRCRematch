#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;

uniform vec4 u_color;

varying vec2 v_texCoord;

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoord);
    if (texColor.a < 0.01) discard; // opcional: eliminar píxeles transparentes
    gl_FragColor = u_color * texColor;
    //gl_FragColor = vec4(v_texCoord, 0.0, 1.0);
}
