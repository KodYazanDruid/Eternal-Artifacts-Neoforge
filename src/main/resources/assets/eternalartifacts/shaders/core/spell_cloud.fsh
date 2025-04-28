#version 150

uniform float GameTime;

in vec4 vertexColor;
in float time;
in vec3 positionOut;

out vec4 fragColor;

// 3D hash fonksiyonu
float hash(vec3 p) {
    p = fract(p * vec3(0.1031, 0.1030, 0.0973));
    p += dot(p, p.yxz + 33.33);
    return fract((p.x + p.y) * p.z);
}

// 3D gürültü
float noise(vec3 p) {
    vec3 i = floor(p);
    vec3 f = fract(p);

    // Hermite interpolation
    vec3 u = f * f * (3.0 - 2.0 * f);

    // 8 köşe için hash değerleri
    float a = hash(i);
    float b = hash(i + vec3(1.0, 0.0, 0.0));
    float c = hash(i + vec3(0.0, 1.0, 0.0));
    float d = hash(i + vec3(1.0, 1.0, 0.0));
    float e = hash(i + vec3(0.0, 0.0, 1.0));
    float f1 = hash(i + vec3(1.0, 0.0, 1.0));
    float g = hash(i + vec3(0.0, 1.0, 1.0));
    float h = hash(i + vec3(1.0, 1.0, 1.0));

    // Trilinear interpolation
    return mix(
        mix(mix(a, b, u.x), mix(c, d, u.x), u.y),
        mix(mix(e, f1, u.x), mix(g, h, u.x), u.y),
        u.z
    );
}

// 3D FBM
float fbm(vec3 p) {
    float val = 0.0;
    float amp = 0.5;
    float freq = 3.0;

    for (int i = 0; i < 5; i++) {
        val += amp * noise(p * freq);
        freq *= 2.0;
        amp *= 0.5;
    }

    return val;
}

void main() {
    vec3 pos = positionOut;

    // 3D gürültü hesaplaması
    vec3 offset = vec3(time * 0.01, time * 0.005, time * 0.0025);
    float n1 = fbm(pos * 0.4 + offset);
    float n2 = fbm(pos * 0.8 - offset * 0.5);

    // Merkeze göre yoğunluk hesaplama
    float dist = length(pos) * 0.5;
    float edge = smoothstep(0.8, 1.0, dist);

    // Renk ve saydamlık
    float cloudDensity = mix(n1, n2, 0.5) * (1.0 - edge);
    float alpha = cloudDensity * vertexColor.a;

    // Vertex rengini kullan
    vec3 cloudColor = vertexColor.rgb;

    fragColor = vec4(cloudColor, alpha);
}