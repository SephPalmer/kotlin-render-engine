precision mediump float;

varying vec3 v_Normal;
varying vec2 v_TexCoord;
varying float v_Diffuse;

uniform sampler2D u_Texture;

void main() {
    vec4 texColor = texture2D(u_Texture, v_TexCoord);

    // Increase ambient light
    vec3 ambient = 0.2 * texColor.rgb;

    // Increase diffuse light
    vec3 diffuse = v_Diffuse * texColor.rgb;

    // Add a specular component for extra brightness
    vec3 specular = vec3(0.2, 0.2, 0.2) * pow(v_Diffuse, 8.0);

    // Combine all lighting components
    vec3 finalColor = ambient + diffuse + specular;

    // Ensure the color doesn't exceed maximum brightness
    finalColor = clamp(finalColor, 0.0, 1.0);

    gl_FragColor = vec4(finalColor, texColor.a);
}