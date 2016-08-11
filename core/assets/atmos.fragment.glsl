varying vec3 pixel_nor;              // camera direction in ellipsoid space
varying vec4 pixel_pos;              // pixel in ellipsoid space

uniform vec3 planet_r;               // rx^-2,ry^-2,rz^-2 - surface
uniform vec3 planet_R;               // Rx^-2,Ry^-2,Rz^-2 - atmosphere
uniform float planet_h;              // atmoshere height [m]
uniform float view_depth;            // max. optical path length [m] ... saturation

// lights are only for local stars-atmosphere ray colision to set start color to star color
const int _lights=3;
uniform vec3 light_dir[_lights];     // direction to local star in ellipsoid space
uniform vec3 light_col[_lights];     // local star color * visual intensity
uniform vec4 light_posr[_lights];    // local star position and radius^-2 in ellipsoid space
uniform vec4 B0;                     // atmosphere scattering coefficient (affects color) (r,g,b,-)

// compute length of ray(p0,dp) to intersection with ellipsoid((0,0,0),r) -> view_depth_l0,1
// where r.x is elipsoid rx^-2, r.y = ry^-2 and r.z=rz^-2
float view_depth_l0=-1.0,view_depth_l1=-1.0;
bool _view_depth(vec3 p0,vec3 dp,vec3 r)
    {
    float a,b,c,d,l0,l1;
    view_depth_l0=-1.0;
    view_depth_l1=-1.0;
    a=(dp.x*dp.x*r.x)
     +(dp.y*dp.y*r.y)
     +(dp.z*dp.z*r.z); a*=2.0;
    b=(p0.x*dp.x*r.x)
     +(p0.y*dp.y*r.y)
     +(p0.z*dp.z*r.z); b*=2.0;
    c=(p0.x*p0.x*r.x)
     +(p0.y*p0.y*r.y)
     +(p0.z*p0.z*r.z)-1.0;
    d=((b*b)-(2.0*a*c));
    if (d<0.0) return false;
    d=sqrt(d);
    l0=(-b+d)/a;
    l1=(-b-d)/a;
    if (abs(l0)>abs(l1)) { a=l0; l0=l1; l1=a; }
    if (l0<0.0)          { a=l0; l0=l1; l1=a; }
    if (l0<0.0) return false;
    view_depth_l0=l0;
    view_depth_l1=l1;
    return true;
    }
// determine if ray (p0,dp) hits a sphere ((0,0,0),r)
// where r is (sphere radius)^-2
bool _star_colide(vec3 p0,vec3 dp,float r)
    {
    float a,b,c,d,l0,l1;
    a=(dp.x*dp.x*r)
     +(dp.y*dp.y*r)
     +(dp.z*dp.z*r); a*=2.0;
    b=(p0.x*dp.x*r)
     +(p0.y*dp.y*r)
     +(p0.z*dp.z*r); b*=2.0;
    c=(p0.x*p0.x*r)
     +(p0.y*p0.y*r)
     +(p0.z*p0.z*r)-1.0;
    d=((b*b)-(2.0*a*c));
    if (d<0.0) return false;
    d=sqrt(d);
    l0=(-b+d)/a;
    l1=(-b-d)/a;
    if (abs(l0)>abs(l1)) { a=l0; l0=l1; l1=a; }
    if (l0<0.0)          { a=l0; l0=l1; l1=a; }
    if (l0<0.0) return false;
    return true;
    }

// compute atmosphere color between ellipsoids (planet_pos,planet_r) and (planet_pos,planet_R) for ray(pixel_pos,pixel_nor)
vec3 atmosphere()
    {
    const int n=8;
    const float _n=1.0/float(n);
    int i;
    bool b0,b1;
    vec3 p0,p1,dp,p,c,b;
    // c - color of pixel from start to end

    float l0,l1,l2,h,dl;
    c=vec3(0.0,0.0,0.0);
    b0=_view_depth(pixel_pos.xyz,pixel_nor,planet_r);
    if ((b0)&&(view_depth_l0>0.0)&&(view_depth_l1<0.0)) return c;
    l0=view_depth_l0;
    b1=_view_depth(pixel_pos.xyz,pixel_nor,planet_R);
    l1=view_depth_l0;
    l2=view_depth_l1;

    dp=pixel_nor;
    p0=pixel_pos.xyz;

    if (!b0)
        {                                       // outside surface
        if (!b1) return c;                      // completly outside planet
        if (l2<=0.0)                            // inside atmosphere to its boundary
            {
            l0=l1;
            }
        else{                                   // throu atmosphere from boundary to boundary
            p0=p0+(l1*dp);
            l0=l2-l1;
            }
        // if a light source is in visible path then start color is light source color
        for (i=0;i<_lights;i++)
        if (light_posr[i].a<=1.0)
        if (_star_colide(p0-light_posr[i].xyz,dp,light_posr[i].a))
        c+=light_col[i];
        }
    else{                                       // into surface
        if (l0<l1) b1=false;                    // atmosphere is behind surface
        if (!b1)                                // inside atmosphere to surface
            {
            l0=l0;
            }
        else{                                   // from atmosphere boundary to surface
            p0=p0+(l1*dp);
            l0=l0-l1;
            }
        }
    dp*=l0;
    p1=p0+dp;
    dp*=_n;
/*
    p=normalize(p1);
    h=0.0; l2=0.0;
    for (i=0;i<_lights;i++)
     if (light_posr[i].a<=1.0)
        {
        dl=dot(pixel_nor,light_dir[i]);         // cos(ang: light-eye)
        if (dl<0.0) dl=0.0;
        h+=dl;
        dl=dot(p,light_dir[i]);                 // normal shading
        if (dl<0.0) dl=0.0;
        l2+=dl;
        }
    if (h>1.0) h=1.0;
    if (l2>1.0) l2=1.0;
    h=0.5*(2.0+(h*h));
*/
    float qqq=dot(normalize(p1),light_dir[0]);


    dl=l0*_n/view_depth;
    for (p=p1,i=0;i<n;p-=dp,i++)                // p1->p0 path throu atmosphere from ground
        {
        _view_depth(p,normalize(p),planet_R);   // view_depth_l0=depth above atmosphere top [m]
        h=exp(view_depth_l0/planet_h)/2.78;

        b=B0.rgb*h*dl;
        c.r*=1.0-b.r;
        c.g*=1.0-b.g;
        c.b*=1.0-b.b;
        c+=b*qqq;
        }
    if (c.r<0.0) c.r=0.0;
    if (c.g<0.0) c.g=0.0;
    if (c.b<0.0) c.b=0.0;
    h=0.0;
    if (h<c.r) h=c.r;
    if (h<c.g) h=c.g;
    if (h<c.b) h=c.b;
    if (h>1.0)
        {
        h=1.0/h;
        c.r*=h;
        c.g*=h;
        c.b*=h;
        }
    return c;
    }

void main(void)
    {
    gl_FragColor.rgb=atmosphere();
}