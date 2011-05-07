% Odwrotna transformata Fouriera.
% U¿ycie:
%   >> y = ifourier(H);
% gdzie:
%   H   - transformata poddawana odfourierowaniu
%   y   - funkcja wynikowa
function y = ifourier(H)
    y = real(ifft(ifftshift(H)));