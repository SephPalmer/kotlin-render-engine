attribute vec4 a_Position;
attribute vec4 a_Color;
attribute vec3 a_Normal;

uniform mat4 u_MVP;
uniform mat4 u_ModelMatrix;
uniform vec3 u_LightDirection;

varying vec4 v_Color;
varying vec3 v_Normal;
varying vec3 v_LightDirection;

void main() {
    v_Color = a_Color;

    v_Normal = mat3(u_ModelMatrix) * a_Normal;
    v_LightDirection = u_LightDirection;

    gl_Position = u_MVP * a_Position;
}