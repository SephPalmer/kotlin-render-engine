precision mediump float;

varying vec3 v_Normal;
varying vec2 v_TexCoord;
varying float v_Diffuse;

uniform sampler2D u_Texture;

void main() {
    vec4 texColor = texture2D(u_Texture, v_TexCoord);
    vec3 ambient = 0.1 * texColor.rgb;
    vec3 diffuse = v_Diffuse * texColor.rgb;
    gl_FragColor = vec4(ambient + diffuse, texColor.a);
}