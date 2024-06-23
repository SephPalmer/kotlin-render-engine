precision mediump float;

varying vec3 v_Normal;
varying vec2 v_TexCoord;
varying float v_Diffuse;

uniform sampler2D u_Texture;
uniform float u_AmbientStrength;

void main() {
    vec4 texColor = texture2D(u_Texture, v_TexCoord);

    vec3 ambient = u_AmbientStrength * texColor.rgb;
    vec3 diffuse = v_Diffuse * texColor.rgb;

    vec3 finalColor = ambient + diffuse;
    finalColor = clamp(finalColor, 0.0, 1.0);

    gl_FragColor = vec4(finalColor, texColor.a);
}