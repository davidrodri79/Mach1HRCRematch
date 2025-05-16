#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_lightSpacePos;

void main() {
    float depth = v_lightSpacePos.z / v_lightSpacePos.w;
    depth = depth * 0.5 + 0.5;  // Mapea de [-1,1] a [0,1]
    gl_FragColor = vec4(depth,0,0,1);//vec4(depth, depth, depth, 1.0);
}
