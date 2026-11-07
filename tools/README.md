# tools

`render_fuzz.py` renders the ten plush mood frames (`fuzz_0.png` .. `fuzz_9.png`) used on the home
screen. It needs Python with numpy, scipy and pillow. Convert the output to WebP and put it in
`app/src/main/res/drawable-nodpi/` as `fuzz_<n>.webp` (crop 470px around the centre, resize to 720px).
