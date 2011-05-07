[t, o, h, l, c, v] = qload('BRE');
Q = v2q(o, h, l, c);

n = 450;
x = [0 : n];
%R = zeros(1, n + 1);
%for i = 0 : n
%    R(1, i + 1) = osc(Q, 1, i);
%end
figure
%subplot(3, 1, 1)
%plot(x, R)
%
%subplot(3, 1, 2)

tt = t(end - n : end);
hh = h(end - n : end);
ll = l(end - n : end);
cc = c(end - n : end);
oo = o(end - n : end);



EM = zeros(1, n);
SM = zeros(1, n);
EM2 = zeros(1, n);
for i = 0 : n
    EM(n - i + 1) = ema(c(1 : end - i), 3);
    SM(n - i + 1) = sma(c(1 : end - i), 12);
    EM2(n - i + 1) = ema(c(1 : end - i), 30);
end


%subplot(3, 1, 1)
candle(hh, ll, cc, oo, 'b');
ax = [0 n min(ll) - 5 max(hh) + 5];
axis(ax)
hold

cc = c(end - n : end);
EM2 = smooth(x, EM2, 20, 'sgolay');
%plot(x, EM, 'r', x, SM, 'b', x, EM2, 'g')
%plot(x, EM, 'r', x, SM, 'g', x, EM2, 'k')


mi = min([EM SM]);

sEM = (EM - m);
sSM = (SM - m);

mx = max([sEM sSM]);

sEM = sEM ./ mx;
sSM = sSM ./ mx;

sEM = smooth(x, sEM, 20, 'sgolay');
%sSM = smooth(x, sSM, 3, 'sgolay');

EM_SM = EM - SM;
sEM_SM = EM_SM ./ max(EM_SM);
sEM_SM = smooth(x, sEM_SM, 13, 'sgolay');

subplot(3, 1, 2)
bar(x, sEM_SM);
hold;
plot(x, sEM, '.r', x, sSM, '.g')
axis([0 n -1.1 +1.1]);


subplot(3, 1, 2)
x = x(2:end);
T = diff(EM2);

TY = 90;
k = smooth(x, 100 .* T ./ EM2(2:end), 10, 'sgolay') * TY;

rSM = zeros(1, n - 1);
bk = zeros(1, n - 1);
for i = 0 : n - 1
    if (k(n - i) < 12) 
        k(n - i) = 12;
    end
    bk(n - i) = round(k(n - i) + 1);
    rSM(n - i) = sma(c(1 : end - i), bk(n - i));
end

size(x)
size(k)
size(rSM)
subplot(3,1,3)
plot(x, bk)

subplot(3, 1, 1)
candle(hh, ll, cc, oo, 'b');
ax = [0 n min(ll) - 5 max(hh) + 5];
axis(ax)
hold

x = [0 : n];
cc = c(end - n : end);
EM2 = smooth(x, EM2, 20, 'sgolay');
%plot(x, EM, 'r', x, SM, 'b', x, EM2, 'g')
plot(x, EM, 'r', x, SM, '.g', x, EM2, 'k', x(2:end), rSM, '.r')


%k = 10;
%plot(c(end - k + 1: end), movavg(a, k, k, 'e'), x, movavg(a, k, k, 0))
