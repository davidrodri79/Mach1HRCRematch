attribute vec3 a_position;

uniform mat4 u_model;       // Matriz modelo del objeto
uniform mat4 u_lightVP;     // Matriz vista-proyección de la luz

varying vec4 v_lightSpacePos;

void main() {
    // Posición del vértice en mundo
    vec4 worldPos = u_model * vec4(a_position, 1.0);

    // Posición en espacio de luz
    v_lightSpacePos = u_lightVP * worldPos;

    // Posición final para la rasterización
    gl_Position = v_lightSpacePos;
}
