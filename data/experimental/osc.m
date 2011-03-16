function o = osc(Q, k, i)

    O = Q(:, 1);
    H = Q(:, 2);
    L = Q(:, 3);
    C = Q(:, 4);

    H = H(end - i - k + 1 : end - i);
    L = L(end - i - k + 1 : end - i);
    
    D = H - L;
    p = 2 / k;
    
    SM = sma(C(1 : end - 1), k);
    
    %o = p * sum(D ./ SM);
    o = p * sum(D);
