#version 330

uniform sampler2D texture_sampler;
uniform ivec2 texture_size;
uniform float weights[10];
uniform int direction;
const int DIRECTION_HORIZONTAL=0;
const int DIRECTION_VERTICAL=1;

in vec2 vs_out_uv;
out vec4 fs_out_color;

vec4 HorizontalBlur(){
    vec4 ret=vec4(0.0);
    vec2 texel_size=1.0/texture_size;
    
    for(int i=1;i<=9;i++){
        ret+=texture(texture_sampler,vs_out_uv+vec2(texel_size.x*float(-i),0.0))*weights[i];
    }
    ret+=texture(texture_sampler,vs_out_uv)*weights[0];
    for(int i=1;i<=9;i++){
        ret+=texture(texture_sampler,vs_out_uv+vec2(texel_size.x*float(i),0.0))*weights[i];
    }

    return ret;
}
vec4 VerticalBlur(){
    vec4 ret=vec4(0.0);
    vec2 texel_size=1.0/texture_size;
    
    for(int i=1;i<=9;i++){
        ret+=texture(texture_sampler,vs_out_uv+vec2(0.0,texel_size.y*float(-i)))*weights[i];
    }
    ret+=texture(texture_sampler,vs_out_uv)*weights[0];
    for(int i=1;i<=9;i++){
        ret+=texture(texture_sampler,vs_out_uv+vec2(0.0,texel_size.y*float(i)))*weights[i];
    }

    return ret;
}

void main(){
    fs_out_color=(direction==DIRECTION_HORIZONTAL)?HorizontalBlur():VerticalBlur();
}
