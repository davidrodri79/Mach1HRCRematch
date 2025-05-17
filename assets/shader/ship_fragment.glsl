#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_textures[6]; // Puedes ampliar si quieres más texturas

#define FOG_ENABLED 1
#define SHADOWMAP_ENABLED 1
#define LIGHTING_ENABLED 1
#define SHADOWPCF_ENABLED 1

#define MAX_LIGHTS 4

uniform vec3 u_lightPos[MAX_LIGHTS];
uniform vec3 u_lightColor[MAX_LIGHTS];
uniform float u_lightIntensity[MAX_LIGHTS];
uniform int u_numLights;

uniform vec3 u_ambientColor;

uniform vec3 u_fogColor;
uniform float u_fogStart;
uniform float u_fogEnd;
uniform float u_alpha;

uniform vec3 u_colorCoef;

varying vec3 v_worldPos;
varying vec4 v_color;
varying vec3 v_normal;
varying vec2 v_texCoord;
varying float v_textureID;

uniform sampler2D u_shadowMap;     // Shadow map generado
uniform mat4 u_lightVP;            // Matriz ViewProjection de la luz
uniform mat4 u_model;              // Matriz de modelo
varying vec4 v_shadowCoord;

void main() {

    vec4 texColor;

    // --- TEXTURE * Vertex color ---

    if (v_textureID < -0.5) {
        texColor = v_color;
    } else if (v_textureID < 0.5) {
        texColor = texture2D(u_textures[0], v_texCoord);
    } else if (v_textureID < 1.5) {
        texColor = texture2D(u_textures[1], v_texCoord);
    } else if (v_textureID < 2.5) {
        texColor = texture2D(u_textures[2], v_texCoord);
    } else if (v_textureID < 3.5) {
        texColor = texture2D(u_textures[3], v_texCoord);
    } else if (v_textureID < 4.5) {
        texColor = texture2D(u_textures[4], v_texCoord);
    } else {
        texColor = texture2D(u_textures[5], v_texCoord);
    }

    // --- Shadow map ---
#ifdef SHADOWMAP_ENABLED
    vec3 shadowCoord = v_shadowCoord.xyz / v_shadowCoord.w;
    shadowCoord = shadowCoord * 0.5 + 0.5;  // convertir de clip space a [0,1]

    float shadow = 0.0;

    // Si está fuera del shadow map
    if (shadowCoord.x < 0.0 || shadowCoord.x > 1.0 ||
        shadowCoord.y < 0.0 || shadowCoord.y > 1.0)
        shadow = 1f;
    else
    {
        //float closestDepth = texture2D(u_shadowMap, shadowCoord.xy).r;
#ifdef SHADOWPCF_ENABLED
        float texelSize = 1.0 / 1024.0; // Tamaño de texel (ajustar a la resolución real del shadow map)

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                vec2 offset = vec2(float(x), float(y)) * texelSize;
                float closestDepth = texture2D(u_shadowMap, shadowCoord.xy + offset).r;
                float currentDepth = shadowCoord.z;
                if (currentDepth - 0.005 <= closestDepth) {
                    shadow += 1.0;
                }
            }
        }

        shadow /= 9.0; // Promedio de 3x3 muestras
#else
        float closestDepth = texture2D(u_shadowMap, shadowCoord.xy).r;
        float currentDepth = shadowCoord.z;
        if (currentDepth - 0.005 <= closestDepth) {
            shadow = 1.0;
        }
        else
        {
            shadow = 0.0;
        }
#endif
    }

#else
    float shadow = 1f;
#endif

#ifdef LIGHTING_ENABLED
    // --- LIGHTING ---
    vec3 normal = normalize(v_normal);
    vec3 lightAccum = u_ambientColor;


    for (int i = 0; i < MAX_LIGHTS; i++) {
        if (i >= u_numLights) break;
        vec3 lightDir = normalize(u_lightPos[i] - v_worldPos);
        float diff = max(dot(normal, lightDir), 0.0);
        lightAccum += u_lightColor[i] * diff * u_lightIntensity[i] * shadow;
    }

    //lightAccum = u_lightColor[0];

    vec3 finalColor = texColor.rgb * u_colorCoef * v_color.xyz * lightAccum;
#else
    vec3 finalColor = texColor.rgb * u_colorCoef * v_color.xyz;
#endif

    // --- FOG EFFECT ---
    float dist = length(v_worldPos); // distancia al origen de cámara
    float fogFactor = clamp((u_fogEnd - dist) / (u_fogEnd - u_fogStart), 0.0, 1.0);
#ifdef FOG_ENABLED
    vec3 colorWithFog = mix(u_fogColor, finalColor, fogFactor);
#else
    vec3 colorWithFog = finalColor;
#endif

    gl_FragColor = vec4(colorWithFog, texColor.a * u_alpha);

}
