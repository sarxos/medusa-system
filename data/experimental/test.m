[t, o, h, l, c, v] = qload('BRE');
Q = v2q(o, h, l, c);

n = 300;
R = zeros(1, n + 1);
for i = 0 : n
    R(1, i + 1) = osc(Q, 1, i);
end

figure
subplot(3, 1, 1)
plot(R)

subplot(3, 1, 2)
plot(c(end - n : end))

subplot(3, 1, 3)
tt = t(end - n : end);
hh = h(end - n : end);
ll = l(end - n : end);
cc = c(end - n : end);
oo = o(end - n : end);
candle(hh, ll, cc, oo);

