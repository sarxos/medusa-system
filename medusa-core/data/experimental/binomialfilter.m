function hf = binomialfilter(h, ww)

    if rem(ww, 2) == 0
        ww = ww + 1;
        fprintf('Uwaga. Skorygowano szerokosc okna od wartosci %i do %i\n', ww - 1, ww);
    end

    p   = diag(fliplr(pascal(ww)));
    p   = p ./ sum(p);
    w12 = (length(p) - 1) / 2;
    
    hf                  = zeros(1, length(h));
    hf(1 : w12)         = hf(w12);
    hf(end - w12 : end) = hf(end - w12);
    
    for j = w12 : length(h) - w12 - 1;
        window = h(1 + j - w12 : 1 + j + w12);
        hf(j) = sum((window') .* p);
    end
    