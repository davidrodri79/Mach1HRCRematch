attribute vec3 a_position;
attribute vec3 a_normal;

uniform mat4 u_mvp;
uniform mat4 u_model;

varying vec3 v_worldPos;
varying vec3 v_normal;

void main() {

    vec4 worldPos = u_model * vec4(a_position, 1.0);
    v_worldPos = worldPos.xyz;

    mat3 rotation = mat3(
    u_model[0].xyz,
    u_model[1].xyz,
    u_model[2].xyz
    );
    v_normal = rotation * a_normal;

    gl_Position = u_mvp * vec4(a_position, 1.0);
}
