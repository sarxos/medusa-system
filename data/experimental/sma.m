% Usage:
%     sma(C, n, k, i)
% Input:
%     C - input vector
%     n - output vector lengths
%     k - days interval
%     i - position from end (default zero)
function s = sma(C, n, k, i)
    p = n + k;
    C = C(end - p - i : end - i);
    s = movavg(C, k, k, 0);
    s = s(end - n + 1 : end);