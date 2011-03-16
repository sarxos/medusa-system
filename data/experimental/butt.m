% a = 0.02
% b = 3
function w = butt(x, y, a, b)
    [Fw, Hw, H] = fourier(x, y);
    [Hfb, Fb] = butterworth(H, Fw, a, b);
    w = ifourier(Hfb);