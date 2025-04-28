#version 150

in vec3 Position;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float GameTime;

out vec4 vertexColor;
out float time;
out vec3 positionOut; // Pozisyon bilgisini fragment shader'a aktarıyoruz

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    vertexColor = Color;
    time = GameTime * 1200.0;
    positionOut = Position;
}