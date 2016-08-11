/* SSH GLSL Atmospheric Ray light scattering ver 3.0

    glEnable(GL_BLEND);
    glBlendFunc(GL_ONE,GL_ONE);
    use with single quad covering whole screen

    no Modelview/Projection/Texture matrixes used

    gl_Normal   is camera direction in ellipsoid space
    gl_Vertex   is pixel in ellipsoid space
    gl_Color    is pixel pos in screen space <-1,+1>

    const int _lights=3;
    uniform vec3 light_dir[_lights];     // direction to local star in ellipsoid space
    uniform vec3 light_col[_lights];     // local star color * visual intensity
    uniform vec4 light_posr[_lights];    // local star position and radius^-2 in ellipsoid space
    uniform vec4 B0;                     // atmosphere scattering coefficient (affects color) (r,g,b,-)

    [ToDo:]
    add light map texture for light source instead of uniform star colide parameters
    - all stars and distant planets as dots
    - near planets ??? maybe too slow for reading pixels
    aspect ratio correction
*/

varying vec3 pixel_nor;       // camera direction in ellipsoid space
varying vec4 pixel_pos;       // pixel in ellipsoid space

uniform vec3 u_cam_dir;
attribute vec4 a_pxl_pos;
attribute vec4 a_scr_pos;

void main(void)
    {
    pixel_nor=u_cam_dir;
    pixel_pos=a_pxl_pos;
    gl_Position=a_scr_pos;
}