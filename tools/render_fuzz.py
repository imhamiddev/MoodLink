import numpy as np, sys, math
from PIL import Image, ImageDraw, ImageFilter
from scipy.ndimage import gaussian_filter

S = 1200           # working canvas
R = 380.0          # ball radius in working px
CX, CY = S/2, S*0.50
L = np.array([-0.45, -0.62, 0.64]); L /= np.linalg.norm(L)

def shade(x, y):
    """Returns (luminance multiplier, white mix) for points in px relative to the centre."""
    nx = np.clip(x/R, -1, 1); ny = np.clip(y/R, -1, 1)
    nz = np.sqrt(np.clip(1-nx*nx-ny*ny, 0, 1))
    d = nx*L[0]+ny*L[1]+nz*L[2]
    wrap = np.clip(d*0.5+0.5, 0, 1)**1.5
    ao = 1.0 - 0.30*np.clip(ny, 0, 1)**1.4
    lum = (0.42 + 0.80*wrap)*ao
    white = np.clip((1-nz)**2.4*0.42 + np.clip(d-0.80, 0, 1)*0.9, 0, 0.7)
    return lum, white

def tinted(col, lum, white):
    c = col[None, :]*lum[:, None] if lum.ndim == 1 else col*lum[..., None]
    return np.clip(c*(1-white[..., None]) + white[..., None], 0, 1.3)

def render(color, face, seed=3, out=None):
    rng = np.random.default_rng(seed)
    col = np.array(color, dtype=np.float32)/255.0
    yy, xx = np.mgrid[0:S, 0:S].astype(np.float32)
    x = xx-CX; y = yy-CY
    r = np.sqrt(x*x+y*y)

    lum, wh = shade(x, y)
    img = tinted(col, lum, wh)
    alpha = np.clip((R*0.975 - r)/5.0, 0, 1)

    acc = np.zeros((S, S, 3), np.float32); w = np.zeros((S, S), np.float32)
    def splat(px, py, c, wt):
        # bilinear splat so thin strands are anti-aliased
        x0 = np.floor(px).astype(int); y0 = np.floor(py).astype(int)
        fx = px-x0; fy = py-y0
        for dx, dy, ww in ((0,0,(1-fx)*(1-fy)),(1,0,fx*(1-fy)),(0,1,(1-fx)*fy),(1,1,fx*fy)):
            ix = np.clip(x0+dx, 0, S-1); iy = np.clip(y0+dy, 0, S-1)
            flat = iy*S+ix
            for k in range(3):
                acc[..., k] += np.bincount(flat, weights=c[:, k]*wt*ww, minlength=S*S).reshape(S, S)
            w[:] += np.bincount(flat, weights=wt*ww, minlength=S*S).reshape(S, S)

    def strands(n, rmin, rmax, lmin, lmax, radial, K, root, tip, wgt, spread=0.35):
        rr = np.sqrt(rng.uniform(rmin**2, rmax**2, n))*R
        th = rng.uniform(0, 2*np.pi, n)
        sx = rr*np.cos(th); sy = rr*np.sin(th)
        ln = rng.uniform(lmin, lmax, n)*R
        if radial:
            ang = th + rng.normal(0, spread, n)
        else:
            ang = (np.sin(sx/190.0+1.3)+np.cos(sy/150.0-0.7))*0.8 + rng.normal(0, 0.5, n) + 1.3
        curl = rng.normal(0, 1.1, n)
        t = np.linspace(0, 1, K)[None, :]
        a = ang[:, None] + curl[:, None]*t
        px = sx[:, None] + np.cumsum(np.cos(a), axis=1)*ln[:, None]/K
        py = sy[:, None] + np.cumsum(np.sin(a), axis=1)*ln[:, None]/K
        l2, w2 = shade(px, py)
        jit = rng.uniform(0.58, 1.40, (n, 1))
        tone = (root + (tip-root)*t)*jit
        c = tinted(col, l2*tone, np.clip(w2*(0.6+t), 0, 0.7))
        wt = wgt*(0.5+0.5*(1-t))*np.ones_like(l2)
        splat((px+CX).ravel(), (py+CY).ravel(), c.reshape(-1, 3), wt.ravel())

    strands(200000, 0.0, 1.0, 0.030, 0.060, False, 9, 0.62, 1.12, 0.6)
    strands(110000, 0.0, 1.0, 0.07, 0.13, False, 11, 0.60, 1.25, 0.30)
    strands(90000, 0.55, 1.02, 0.05, 0.11, True, 10, 0.70, 1.25, 0.28, 0.5)
    strands(30000, 0.85, 1.03, 0.09, 0.17, True, 12, 0.90, 1.30, 0.22, 0.6)

    # crisp light hairs sticking out of the silhouette
    strands(7000, 0.92, 1.0, 0.10, 0.20, True, 16, 1.0, 1.35, 0.9, 0.5)
    strands(5000, 0.3, 0.95, 0.06, 0.12, False, 12, 1.0, 1.4, 0.7)

    fur = acc/np.maximum(w, 1e-6)[..., None]
    fa = np.clip(1-np.exp(-w*1.2), 0, 1)

    out_rgb = img*(1-fa[..., None]) + fur*fa[..., None]
    out_a = np.clip(np.maximum(alpha, fa), 0, 1)

    # face on top, so eyes and mouth stay crisp; soft edges come from the final blur
    face_layer = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    draw_face(face_layer, face, rng)
    fl = np.asarray(face_layer, dtype=np.float32)/255.0
    fa2 = fl[..., 3:4]
    out_rgb = out_rgb*(1-fa2) + fl[..., :3]*fa2
    out_a = np.maximum(out_a, fa2[..., 0])

    rgba = np.dstack([out_rgb, out_a])
    for k in range(4):
        rgba[..., k] = gaussian_filter(rgba[..., k], 0.55)
    rgba = np.clip(rgba, 0, 1)
    return Image.fromarray((rgba*255).astype(np.uint8), "RGBA")

def face_keepout(face):
    S_ = S
    m = Image.new("L", (S_, S_), 0)
    d = ImageDraw.Draw(m)
    for (ex, ey, ew, eh) in face["eyes_boxes"]():
        d.ellipse([CX+ex-ew*1.15, CY+ey-eh*1.15, CX+ex+ew*1.15, CY+ey+eh*1.15], fill=255)
    return gaussian_filter(np.asarray(m, dtype=np.float32)/255.0, 5)


INK = (18, 14, 12, 255)

def _eye(d, cx, cy, w, h, shine2=False):
    d.ellipse([cx-w, cy-h, cx+w, cy+h], fill=INK)
    # glossy highlights
    hr = w*0.30
    d.ellipse([cx-w*0.45-hr, cy-h*0.50-hr, cx-w*0.45+hr, cy-h*0.50+hr], fill=(255, 255, 255, 235))
    sr = w*0.13
    d.ellipse([cx+w*0.40-sr, cy+h*0.42-sr, cx+w*0.40+sr, cy+h*0.42+sr], fill=(255, 255, 255, 120))
    if shine2:
        d.ellipse([cx+w*0.10-sr, cy-h*0.62-sr, cx+w*0.10+sr, cy-h*0.62+sr], fill=(255, 255, 255, 200))

def draw_face(layer, f, rng):
    d = ImageDraw.Draw(layer)
    body = f["lid_color"]
    ex = f["eye_dx"]*R; ey = f["eye_y"]*R; ew = f["eye_w"]*R; eh = f["eye_h"]*R
    for side in (-1, 1):
        cx = CX + side*ex; cy = CY + ey
        if f.get("closed"):
            # a sleepy closed eye: a soft downward arc
            d.arc([cx-ew*1.1, cy-eh*0.9, cx+ew*1.1, cy+eh*0.9], 20, 160, fill=INK, width=int(R*0.028))
            continue
        _eye(d, cx, cy, ew, eh, f.get("shine2", False))
        lid = f.get("lid", 0.0)               # 0..1 covers the top of the eye
        slope = f.get("slope", 0.0)           # + = inner end lower (angry), - = inner end higher (sad)
        if lid > 0 or slope != 0:
            from PIL import ImageChops
            cut_y = cy - eh*(1-2*lid) - eh*0.25
            dy = slope*ew*0.9
            inner_x = cx - side*ew*1.5; outer_x = cx + side*ew*1.5
            poly = Image.new("L", layer.size, 0)
            ImageDraw.Draw(poly).polygon([(inner_x, cut_y+dy), (outer_x, cut_y-dy), (outer_x, cy-eh*3), (inner_x, cy-eh*3)], fill=255)
            eyem = Image.new("L", layer.size, 0)
            ImageDraw.Draw(eyem).ellipse([cx-ew*1.06, cy-eh*1.06, cx+ew*1.06, cy+eh*1.06], fill=255)
            lidm = ImageChops.multiply(poly, eyem)
            layer.paste(body + (255,), mask=lidm)
            if abs(slope) > 0.05:
                p1 = (inner_x+side*ew*0.25, cut_y+dy-R*0.012); p2 = (outer_x-side*ew*0.1, cut_y-dy-R*0.012)
                bw = int(R*0.042); brow = tuple(int(c*0.70) for c in body) + (255,)
                d.line([p1, p2], fill=brow, width=bw)
                for p in (p1, p2): d.ellipse([p[0]-bw/2, p[1]-bw/2, p[0]+bw/2, p[1]+bw/2], fill=brow)
    if f.get("blush"):
        for side in (-1, 1):
            bx = CX + side*R*0.50; by = CY + R*0.24
            d.ellipse([bx-R*0.11, by-R*0.07, bx+R*0.11, by+R*0.07], fill=(240, 110, 140, 120))
    # mouth
    mx = CX; my = CY + f["mouth_y"]*R; mw = f["mouth_w"]*R; k = f["mouth_k"]
    w = int(R*0.032)
    if f.get("open"):
        d.pieslice([mx-mw, my-mw*0.9, mx+mw, my+mw*0.9], 0, 180, fill=(38, 12, 14, 255))
        d.chord([mx-mw*0.55, my+mw*0.35, mx+mw*0.55, my+mw*0.85], 0, 180, fill=(220, 90, 100, 255))
    else:
        pts = []
        for i in range(41):
            t = i/40
            xx = mx + (t*2-1)*mw
            yy = my + k*R*0.20*(1-(t*2-1)**2) * -1 * -1  # parabola
            if f.get("wavy"):
                yy = my + math.sin(t*math.pi*3)*R*0.018
            pts.append((xx, yy))
        d.line(pts, fill=INK, width=w, joint="curve")
        for p in (pts[0], pts[-1]):
            d.ellipse([p[0]-w/2, p[1]-w/2, p[0]+w/2, p[1]+w/2], fill=INK)

# ---- the ten moods, worst to best -------------------------------------------------------
def spectrum(p):
    red, yel, grn = np.array([212, 52, 46]), np.array([238, 190, 72]), np.array([104, 176, 100])
    if p < .4: c = red + (yel-red)*(p/.4)
    elif p < .8: c = yel + (grn-yel)*((p-.4)/.4)
    else: c = grn
    return tuple(int(v) for v in c)

def mk(i, **kw):
    p = i/9
    color = spectrum(p)
    base = dict(eye_dx=.34, eye_y=.00, eye_w=.062, eye_h=.074, mouth_y=.30, mouth_w=.15, mouth_k=0.0,
                lid_color=tuple(int(c*0.78) for c in color))
    base.update(kw)
    base["color"] = color
    base["eyes_boxes"] = (lambda b=base: [(s*b["eye_dx"]*R, b["eye_y"]*R, b["eye_w"]*R*1.4, b["eye_h"]*R*1.4) for s in (-1, 1)])
    return base

FRAMES = [
    mk(0, eye_w=.060, eye_h=.068, slope=.55, lid=.38, mouth_k=-.9, mouth_w=.10, mouth_y=.31),        # angry
    mk(1, eye_w=.066, eye_h=.082, slope=-.45, lid=.22, mouth_k=-1.0, mouth_w=.12, mouth_y=.32),      # sad
    mk(2, eye_w=.070, eye_h=.088, slope=-.25, shine2=True, mouth_k=-.55, mouth_w=.09, mouth_y=.32),  # miss you
    mk(3, eye_w=.062, eye_h=.080, slope=-.30, mouth_k=0, wavy=True, mouth_w=.12),                   # anxious
    mk(4, lid=.45, mouth_k=-.25, mouth_w=.10, mouth_y=.31),                                         # tired
    mk(5, closed=True, mouth_k=.15, mouth_w=.08, mouth_y=.31),                                      # sleepy
    mk(6, mouth_k=.55, mouth_w=.14),                                                                # calm
    mk(7, eye_w=.066, eye_h=.078, mouth_k=.95, mouth_w=.17),                                        # happy
    mk(8, eye_w=.074, eye_h=.090, shine2=True, open=True, mouth_w=.15, mouth_y=.27),                 # excited
    mk(9, eye_w=.070, eye_h=.084, shine2=True, mouth_k=1.0, mouth_w=.18, blush=True),                # love
]

if __name__ == "__main__":
    which = [int(a) for a in sys.argv[1:]] or list(range(10))
    for i in which:
        im = render(FRAMES[i]["color"], FRAMES[i], seed=3+i)
        im.save(f"fuzz_{i}.png")
        print("done", i)
