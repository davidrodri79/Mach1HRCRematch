#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_lightSpacePos;

vec3 encodeDepth (float depth)
{
    depth = clamp(depth, 0.0, 1.0);

    // Escala y toma las fracciones
    vec3 enc = fract(depth * vec3(1.0, 256.0, 65536.0));
    // Quita el "carry" (desbordes) de los canales anteriores
    enc -= enc.yzz * vec3(1.0 / 256.0, 1.0 / 256.0, 0.0);
    return enc;
}
void main() {
    float depth = v_lightSpacePos.z / v_lightSpacePos.w;
    depth = depth * 0.5 + 0.5;  // Mapea de [-1,1] a [0,1]
#ifdef SHADOWMAP24B
    gl_FragColor = vec4(encodeDepth(depth),1);
#else
    gl_FragColor = vec4(depth, depth, depth, 1.0);
#endif
}
