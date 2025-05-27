#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_textures[6]; // Puedes ampliar si quieres más texturas

uniform sampler2D u_normalMap;

//#define FOG_ENABLED 0
//#define SHADOWMAP_ENABLED 0
//#define LIGHTING_ENABLED 0
//#define SHADOWPCF_ENABLED 0

#define MAX_LIGHTS 20

uniform vec3 u_cameraPos;
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

#define MAX_SHADOW_MAPS 4

uniform sampler2D u_shadowMap[MAX_SHADOW_MAPS];     // Shadow map generado
uniform mat4 u_lightVP[MAX_SHADOW_MAPS];            // Matriz ViewProjection de la luz
uniform float u_cascadeEnds[MAX_SHADOW_MAPS];       // distancias fin de cada cascada
uniform float u_shadowMapSize[MAX_SHADOW_MAPS];
uniform mat4 u_model;                                // Matriz de modelo
varying vec4 v_shadowCoord[MAX_SHADOW_MAPS];


uniform sampler2D u_face0;
uniform sampler2D u_face1;
uniform sampler2D u_face2;
uniform sampler2D u_face3;
uniform sampler2D u_face4;
uniform sampler2D u_face5;


float decodeDepth(vec3 rgb) {
    return dot(rgb, vec3(1.0, 1.0 / 255.0, 1.0 / 65025.0));
}

vec2 getUVForDirection(vec3 dir, int face) {
    vec2 uv;
    if (face == 0) uv = vec2(-dir.z, dir.y) / abs(dir.x); // +X
    if (face == 1) uv = vec2(dir.z, dir.y) / abs(dir.x);  // -X
    if (face == 2) uv = vec2(dir.x, -dir.z) / abs(dir.y); // +Y
    if (face == 3) uv = vec2(dir.x, dir.z) / abs(dir.y);  // -Y
    if (face == 4) uv = vec2(dir.x, dir.y) / abs(dir.z);  // +Z
    if (face == 5) uv = vec2(-dir.x, dir.y) / abs(dir.z); // -Z
    return uv * 0.5 + 0.5;
}

vec4 sampleCubemap(vec3 dir) {
    vec3 absDir = abs(dir);
    int face;
    if (absDir.x >= absDir.y && absDir.x >= absDir.z) {
        face = dir.x > 0.0 ? 0 : 1;
    } else if (absDir.y >= absDir.x && absDir.y >= absDir.z) {
        face = dir.y > 0.0 ? 2 : 3;
    } else {
        face = dir.z > 0.0 ? 4 : 5;
    }

    vec2 uv = getUVForDirection(dir, face);

    if (face == 0) return texture2D(u_face0, uv);
    if (face == 1) return texture2D(u_face1, uv);
    if (face == 2) return texture2D(u_face2, uv);
    if (face == 3) return texture2D(u_face3, uv);
    if (face == 4) return texture2D(u_face4, uv);
    return texture2D(u_face5, uv);
}

float getShadow()
{
    // --- Shadow map ---
    #ifdef SHADOWMAP_ENABLED

    float viewDepth = length(v_worldPos - u_cameraPos);

    for(int i = 0; i < MAX_SHADOW_MAPS; i++)
    {
        if (viewDepth < u_cascadeEnds[i])
        {
            vec3 shadowCoord = v_shadowCoord[i].xyz / v_shadowCoord[i].w;
            shadowCoord = shadowCoord * 0.5 + 0.5;// convertir de clip space a [0,1]

            // Si está fuera del shadow map
            if (shadowCoord.x < 0.0 || shadowCoord.x > 1.0 ||
            shadowCoord.y < 0.0 || shadowCoord.y > 1.0)
                return 1.0;
            else
            {
                #ifdef SHADOWPCF_ENABLED
                float shadow = 0.0;
                float texelSize = 1.0 / u_shadowMapSize[i]; // Tamaño de texel (ajustar a la resolución real del shadow map)

                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        vec2 offset = vec2(float(x), float(y)) * texelSize;
                        #ifdef SHADOWMAP24B
                        float closestDepth = decodeDepth(texture2D(u_shadowMap[i], shadowCoord.xy + offset).rgb);
                        #else
                        float closestDepth = texture2D(u_shadowMap[i], shadowCoord.xy + offset).r;
                        #endif
                        float currentDepth = shadowCoord.z;
                        if (currentDepth - 0.005 <= closestDepth) {
                            shadow += 1.0;
                        }
                    }
                }

                shadow /= 9.0; // Promedio de 3x3 muestras
                return shadow;
                #else
                float closestDepth = texture2D(u_shadowMap[i], shadowCoord.xy).r;
                float currentDepth = shadowCoord.z;
                if (currentDepth - 0.005 <= closestDepth) {
                   return 1.0;
                }
                else
                {
                    return 0.0;
                }
                #endif
            }
        }
    }
    return 1.0;
    #else
    return 1.0;
    #endif
}

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
    float shadow = getShadow();

    vec3 viewDir = normalize(u_cameraPos - v_worldPos);

#ifdef NORMAL_MAP_ENABLED
    // Leer el normal del mapa normal en espacio modelo/mundo
    vec3 normalMap = texture2D(u_normalMap, v_texCoord).rbg;
    normalMap = normalize(normalMap * 2.0 - 1.0); // De [0,1] a [-1,1]

    // Combinar normal interpolada y la del mapa (simple mezcla)
    //vec3 normal = normalize(v_normal + normalMap * 0.5);
    vec3 normal = normalMap;
#else
    vec3 normal = normalize(v_normal);
#endif

#ifdef LIGHTING_ENABLED
    // --- LIGHTING ---
    vec3 lightAccum = u_ambientColor;

    for (int i = 0; i < MAX_LIGHTS; i++) {
        if (i >= u_numLights) break;

        // DIFFUSE
        vec3 lightDir = u_lightPos[i] - v_worldPos;
        float dist = length(lightDir);
        lightDir = normalize(lightDir);

        float diff = max(dot(normal, lightDir), 0.0);
        vec3 diffuse = u_lightColor[i] * diff * u_lightIntensity[i];
        if(i == 0) diffuse *= shadow;

        // Atenuación por distancia
        float attConstant = 1.0;   // e.g. 1.0
        float attLinear = 0.01;     // e.g. 0.1
        float attQuadratic = 0.001;  // e.g. 0.01
        float attenuation = 1.0 / (attConstant + attLinear * dist + attQuadratic * dist * dist);

        if(i == 0) attenuation = 1.0;

        lightAccum += diffuse * attenuation; //u_lightColor[i] * diff * u_lightIntensity[i] * shadow;;

#ifdef SPECULAR_ENABLED
        // SPECULAR
        vec3 reflectSpecDir = reflect(-lightDir, normal);

        float spec = pow(max(dot(viewDir, reflectSpecDir), 0.0), 32.0); // 32 = shininess
        vec3 specular = spec * u_lightColor[i];
        if(i == 0) specular *= shadow;
        lightAccum += specular * attenuation;
#endif

    }

    //lightAccum = u_lightColor[0];

    vec3 colorWithLight = texColor.rgb * u_colorCoef * v_color.xyz * lightAccum;
#else
    vec3 colorWithLight = texColor.rgb * u_colorCoef * v_color.xyz;
#endif

    // --- REFLECTION WITH CUBEMAP ---
#ifdef REFLECTION_ENABLED
    vec3 reflectDir = reflect(-viewDir, normal);
    vec4 reflectionColor = sampleCubemap(vec3(reflectDir.x, reflectDir.y, reflectDir.z));
    //float fresnel = pow(1.0 - max(dot(viewDir, normalize(v_normal)), 0.0), 3.5);

    float fresnelMin = 0.03;
    float fresnelPower = 3.0;
    float fresnel = fresnelMin + (1.0 - fresnelMin) * pow(1.0 - max(dot(viewDir, normal), 0.0), fresnelPower);


    // Ajuste del reflectionAmount con fresnel
    float reflectionAmount = mix(0.1, 0.6, fresnel); // 0.1 en vista perpendicular, 0.6 en rasante
    reflectionAmount = reflectionAmount * shadow;

    vec3 finalColor = mix(colorWithLight, reflectionColor.rgb, reflectionAmount);
#else
    vec3 finalColor = colorWithLight;
#endif

    // --- FOG EFFECT ---
#ifdef FOG_ENABLED
    float dist = distance(u_cameraPos, v_worldPos); // distancia al origen de cámara
    float fogFactor = clamp((u_fogEnd - dist) / (u_fogEnd - u_fogStart), 0.0, 1.0);
    vec3 colorWithFog = mix(u_fogColor, finalColor, fogFactor);
#else
    vec3 colorWithFog = finalColor;
#endif

    // Debug cascade shadow maps
    /*float viewDepth = length(v_worldPos - u_cameraPos);
    if(viewDepth < u_cascadeEnds[0])
        colorWithFog.r = colorWithFog.r;
    else if (viewDepth < u_cascadeEnds[1])
        colorWithFog.g = 0.0;
    else if (viewDepth < u_cascadeEnds[2])
        colorWithFog.b = 0.0;
    else
        colorWithFog.r = 0.0;*/

    gl_FragColor = vec4(colorWithFog, texColor.a * u_alpha);

}
