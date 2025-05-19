#version 150

#moj_import <matrix.glsl>

in vec4 vertexColor;
in float vertexTime;
in vec2 texCoord;

out vec4 fragColor;

// Merkezden uzaklık
float distFromCenter(vec2 coord) {
    return length(coord - vec2(0.5));
}

// Noise fonksiyonu
float noise(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

void main() {
    // Merkezden uzaklık hesaplama (0-1 arasında)
    float dist = distFromCenter(texCoord);

    // Zaman tabanlı animasyon
    float time = vertexTime * 0.01;

    // Ateş dalgalanması için noise efekti
    float noiseVal = noise(texCoord + time * 0.5);
    float flameEdge = 0.5 + 0.1 * sin(time + texCoord.y * 10.0) + 0.05 * noiseVal;

    // Ateş yoğunluğu
    float flameIntensity = smoothstep(flameEdge, flameEdge - 0.3, dist);

    // Renk gradyanı (merkez sarı, kenarlar kırmızı)
    vec3 flameColor = mix(
        vec3(1.0, 0.9, 0.3),  // Sarı (merkez)
        vec3(1.0, 0.2, 0.0),  // Kırmızı (kenar)
        pow(dist * 1.2, 1.5)
    );

    // Parlamayı arttırmak için
    flameIntensity = pow(flameIntensity, 0.8);

    // Zamanla dalgalanma efekti
    float pulseFactor = 0.9 + 0.2 * sin(time * 2.0);
    flameIntensity *= pulseFactor;

    // Son renk
    fragColor = vec4(flameColor, flameIntensity) * vertexColor;

    // Düşük yoğunluklarda tamamen şeffaf
    if (flameIntensity < 0.05) discard;
}