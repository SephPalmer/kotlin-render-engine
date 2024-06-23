attribute vec4 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_TexCoord;

uniform mat4 u_MVP;
uniform mat4 u_ModelMatrix;
uniform vec3 u_LightDirection;

varying vec3 v_Normal;
varying vec2 v_TexCoord;
varying float v_Diffuse;

void main() {
    v_Normal = vec3(u_ModelMatrix * vec4(a_Normal, 0.0));
    v_TexCoord = a_TexCoord;

    vec3 lightDir = normalize(-u_LightDirection);
    // Increase the diffuse factor by multiplying it (e.g., by 1.5)
    v_Diffuse = max(dot(normalize(v_Normal), lightDir), 0.0) * 1.5;

    gl_Position = u_MVP * a_Position;
}