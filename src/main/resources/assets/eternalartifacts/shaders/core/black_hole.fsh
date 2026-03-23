#version 150

uniform sampler2D DiffuseSampler;
uniform float Time;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 uv = texCoord;
    vec2 center = vec2(0.5, 0.5);

    vec2 dir = uv - center;
    float dist = length(dir);

    float radius = 0.15;

    // Event Horizon (tam siyah merkez)
    if (dist < radius) {
        fragColor = vec4(0.0, 0.0, 0.0, 1.0);
        //return;
    }

    /*// Gravitational lensing
    float strength = 0.04 / (dist * dist + 0.01);

    vec2 distortedUV = uv - normalize(dir) * strength;

    // Swirl efekti (dönme)
    float swirl = 0.8 / (dist + 0.2);
    float s = sin(swirl);
    float c = cos(swirl);
    mat2 rot = mat2(c, -s, s, c);

    distortedUV = center + rot * (distortedUV - center);

    // clamp (çok önemli)
    distortedUV = clamp(distortedUV, 0.0, 1.0);

    vec4 color = texture(DiffuseSampler, distortedUV);

    // Accretion disk (parlayan halka)
    float angle = atan(dir.y, dir.x);
    float diskPattern = sin(angle * 12.0 + Time * 2.0) * 0.5 + 0.5;

    float ring =
    smoothstep(radius + 0.01, radius + 0.03, dist) *
    (1.0 - smoothstep(radius + 0.03, radius + 0.08, dist));

    vec3 diskColor = vec3(1.0, 0.6, 0.2) * diskPattern;

    color.rgb += diskColor * ring;

    // dış glow
    float glow = smoothstep(radius, radius + 0.1, dist);
    color.rgb += vec3(0.2, 0.3, 0.6) * (1.0 - glow) * 0.5;

    fragColor = color;*/
}