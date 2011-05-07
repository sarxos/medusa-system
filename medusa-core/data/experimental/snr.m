% Funkcja obliczaj¹ca SNR (Signal - Noise Reduction) dla zadanego sygna³u.
% Jako argumenty podajemy w³aœnie sygna³ i liczbê okreslaj¹c¹ iloœæ
% przedzia³ów w którcy liczyc bedziemy SNR.
%
% U¿ycie:
%   wsp_snr = snr(sygnal, n_podzial);
% Gdzie:
%   sygnal      - badany sygna³ (wektor)
%   n_podzial   - iloœæ odcinków w której badamy SNR
function histogram_snr = snr(s, n)

    skok = floor(length(s) / n);
    koniec = skok * floor(length(s) / skok);
    
    k = 1; 
    for j = skok : skok : koniec
        % ma³y zakres w jakim obliczymy SNR
        zakres = [j - skok + 1 : j];
        histogram_snr(k) = max(s) ./ std(detrend(s(zakres)));
        k = k + 1;
    end
    
    
    
