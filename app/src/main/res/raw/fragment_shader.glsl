precision mediump float;

varying vec4 v_Color;
varying vec3 v_Normal;
varying vec3 v_LightDirection;

void main() {
    vec3 normal = normalize(v_Normal);
    vec3 lightDir = normalize(v_LightDirection);

    float diffuse = max(dot(normal, lightDir), 0.0);

    vec3 ambient = 0.3 * v_Color.rgb;
    vec3 diffuseColor = diffuse * v_Color.rgb;

    gl_FragColor = vec4(ambient + diffuseColor, v_Color.a);
}
