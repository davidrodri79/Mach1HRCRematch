#ifdef GL_ES
precision mediump float;
#endif

uniform vec4 u_meshColor;
uniform vec3 u_cameraPos;

varying vec3 v_normal;
varying vec3 v_worldPos;

void main() {

    vec3 viewDir = normalize(u_cameraPos - v_worldPos);
    float fresnelMin = 0.1;
    float fresnelPower = 3.0;
    float fresnel = fresnelMin + (1.0 - fresnelMin) * pow(1.0 - abs(dot(viewDir, normalize(v_normal))), fresnelPower);
    //fresnel = max(dot(-viewDir, normalize(v_normal)), 0.0);

    gl_FragColor = vec4(u_meshColor.rgb, mix(0.2, 1.0, fresnel)*u_meshColor.a);

}
