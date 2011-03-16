% Funkcja oblicza transformatê Fouriera metod¹ fft.
% U¿ycie:
%   >> [Wf, Wa, H] = fourier(t, h)
% Gdzie:
%   Wf  - widmo czêstotliwoœciowe
%   Wa  - widmo amplitudowe
%   H   - transformata pierwotna
function [F, Hr, H] = fourier(t, h)
    % d³ugoœæ wektora czasu.
    n = length(t);
    % Obliczamy transformatê Fouriera
    H = fft(h);
    H2 = fftshift(H);                      % transformata i shift
    H2 = 2 * sqrt(H2 .* conj(H2)) / n;     % widmo amplitudy
    zzero = ceil(n / 2);                   % w tym miejscu dzielimy przez 2
    H2(zzero) = H2(zzero) ./ 2;            % aby sk³ad. sta³a by³a zachowana
    % Obliczamy widmo czêstotliwoœci
    fs = 1 / (t(2) - t(1));
    f = fs .* [0 : n / 2] ./ n;
    f2 = [-fliplr(f(2 : length(f))) f];
    % zwracamy wyniki
    F = f2;
    Hr = H2;