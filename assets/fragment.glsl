#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_textures[6]; // Puedes ampliar si quieres más texturas

varying vec4 v_color;
varying vec2 v_texCoord;
varying float v_textureID;

void main() {
    if (v_textureID < -0.5) {
        gl_FragColor = v_color;
    } else if (v_textureID < 0.5) {
        gl_FragColor = texture2D(u_textures[0], v_texCoord) * v_color;
    } else if (v_textureID < 1.5) {
        gl_FragColor = texture2D(u_textures[1], v_texCoord) * v_color;
    } else if (v_textureID < 2.5) {
        gl_FragColor = texture2D(u_textures[2], v_texCoord) * v_color;
    } else if (v_textureID < 3.5) {
        gl_FragColor = texture2D(u_textures[3], v_texCoord) * v_color;
    } else if (v_textureID < 4.5) {
        gl_FragColor = texture2D(u_textures[4], v_texCoord) * v_color;
    } else {
        gl_FragColor = texture2D(u_textures[5], v_texCoord) * v_color;
    }

}
