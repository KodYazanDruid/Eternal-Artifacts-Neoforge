#version 150

#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;

/**uniform mat4 ModelViewMat;
uniform mat4 ProjMat;*/
uniform float GameTime;

out float vertexTime;
out vec2 texCoord;
out vec4 vertexColor;

void main() {
    //gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    texCoord = Position.xy;

    vertexTime = GameTime;
    vertexColor = Color;
}