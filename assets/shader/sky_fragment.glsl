#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_cloudsTexture;
uniform sampler2D u_starsTexture;

uniform vec3 u_fogColor;
uniform float u_fogStart;
uniform float u_fogEnd;
uniform vec3 u_skyColor;
uniform vec3 u_cloudColor;
uniform int u_skyMode;      // 0 : Sky / 1: Clouds
uniform float u_cloudThreshold; // 0.0-1.0 : Nivel de densidad de las nubes
uniform float u_cloudOverload; // 0.0-1.0 : Nivel de sobrecarga de las nubes. mayor sobrecarga, más nubes oscuras
uniform float u_starsAlpha;


varying vec3 v_worldPos;
varying vec4 v_color;
varying vec2 v_starsTexCoord;
varying vec2 v_cloudsTexCoord;

void main() {

    vec4 texColor;

    vec4 finalColor;
    // Sky with stars
    if(u_skyMode == 0)
    {
        if(u_starsAlpha < 0.01)
        {
            finalColor = vec4(u_skyColor,1.0);
        }
        else
        {
            texColor = texture2D(u_starsTexture, v_starsTexCoord);
            finalColor = vec4(mix(u_skyColor, texColor.rgb, texColor.a * u_starsAlpha), 1);
        }
    }
    // Clouds
    else
    {
        texColor = texture2D(u_cloudsTexture, v_cloudsTexCoord);

        // Nubes de mayor o menor densidad en función del parámetro
        float value = texColor.a;
        value = (1.0/(1.0-u_cloudThreshold)) * (value - u_cloudThreshold);
        if (value < 0.0) value = 0.0;
        if (value > 1.0) value = 1.0;

        value += u_cloudOverload;

        if (value > 2.0) value = 2.0;

        if(value < 1.0)
            finalColor= vec4(u_cloudColor, value);
        else
            finalColor= vec4(mix(u_cloudColor, u_cloudColor * 0.2, value - 1.0), 1.0);
    }

    // --- FOG EFFECT ---
    float height = v_worldPos.y; // Altura
    float fogFactor = clamp((height - u_fogEnd) / (u_fogStart - u_fogEnd), 0.0, 1.0);

    vec3 colorWithFog = mix(u_fogColor, finalColor.rgb, fogFactor);
    gl_FragColor = vec4(colorWithFog, finalColor.a);

}
