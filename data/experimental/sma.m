function s = sma(C, k)

    s = 0;

    for i = 0 : k - 1
        s = s + C(end - i) / k;
    end