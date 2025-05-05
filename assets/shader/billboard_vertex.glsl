attribute vec3 a_position;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;        // proyección * vista (combined)
uniform vec3 u_billboardPos;     // posición del billboard en 3D
uniform float u_sizeX;            // tamaño del quad
uniform float u_sizeY;            // tamaño del quad
uniform vec2 u_uvOffset;
uniform vec2 u_uvSize;

varying vec2 v_texCoord;

void main() {
    // Extraer vectores 'right' y 'up' desde la matriz de vista
    vec3 right = vec3(u_projTrans[0][0], u_projTrans[1][0], u_projTrans[2][0]) * u_sizeX;
    vec3 up = vec3(u_projTrans[0][1], u_projTrans[1][1], u_projTrans[2][1]) * u_sizeY;

    // Construir posición del vértice en mundo
    vec3 worldPos = u_billboardPos
                  + right * a_position.x
                  + up * a_position.y;

    v_texCoord = u_uvOffset + a_texCoord0 * u_uvSize;
    gl_Position = u_projTrans * vec4(worldPos, 1.0);
}
