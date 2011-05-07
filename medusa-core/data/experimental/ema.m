
function e = ema(C, k)
    
    a = 2 / (k + 1);
    e = C(end);
    
    for i = 1 : k
        e = e + a * (C(end - i) - e);
    end
