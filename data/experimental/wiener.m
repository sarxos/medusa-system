% Filtrowanie widma sygna³u poprzez filtr Wienera.
% Uzycie:
%   >> [FouPrzef, FunTrans, Per] = wiener(Four, maks);
% gdzie:
%   FourPrzef   - przefiltrowana transformata Fouriera sygna³u
%   FunTrans    - funkcja transmisji filtru Wienera
%   Per         - uzyskany w filtrze periodogram
%   Four        - transformata Fouriera sygna³u ktyór¹ chcemy filtrowaæ
%   maks        - do jakiej czesci wartoœci maksymalnej amplitudy ma filtrowaæ
% Przyk³adowo jeœli max amplituda periodogramu to 200 i wstawimy jako 
% argument maks wartoœæ 0.01 to wtedy obetnie wszystkie czêstotliwoœci, dla
% których amplituda mocy widoczna w periodogramie jest nie wiêksza ni¿ 2,
% czyli wartoœæ 200 * 0.01 = 2.

function [Hfw, Ft, Per] = wiener(Hp, maks)
    
    n = length(Hp);     % d³ugoœæ transformaty
    n2 = ceil(n/2);     % po³owa d³ugoœci transformaty
    wsp = (1/(n^2));    % wspó³czynnik do mno¿enia
    
    Per = zeros(1, n2);         % Periodogram na pocz¹tku jako zera.
    Habs2 = abs(Hp).^2;         % Kwadrat modu³u transformaty Fouriera.
    Per(1) = wsp * Habs2(n2);   % Dla pierwszego elementu tylko jeden element z Fouriera
    
    k = 2;     % index dla periodogramu
    
    % Tworzymy periodogram pocz¹wszy od drugiego elementu.
    for j = 1 : n2 - 1;
        Per(k) = wsp * (Habs2(j) + Habs2(2 * n2 - j));  % periodogram(k)
        k = k + 1;                                      % zwiekszamy indeks periodogramu
    end

    % testy
    %wx = 1 : length(Per);
    %wpof = polyfit(wx(20 : end), Per(20 : end), 1);
    %wpov = abs(polyval(wpof, wx));
    %plot(wx, Per, wx, wpov);
    
    N = zeros(1, n2);           % wektor szumów
    maks = maks * max(Per);     % amplituda odciêcia
    
    for j = 1 : n2              % Przechodzimy ca³y periodogram przez pêtlê for
        if Per(j) > maks        % i jesli periodogram wiêkszy od amplitudy odciêcia
            N(j) = maks;           % to szumów nie ma
        else                    % inaczej
            N(j) = Per(j);      % wstawiamy wartosc periodogramu czyli w tym miejscu wartosc szumu
        end
    end
    
    %plot(Per)
    
    HabsZ = Per - N;            % teoretyczna transformata zawuieraj¹ca tylko sygna³ (bez szumów bo je
                                % odjêliœmy), jednak zawsze w tym sygnale bêd¹ te¿ szumy.

    Ft = HabsZ ./ (HabsZ + N);          % Obliczamy funkcje transmisji dla filtru Wienera.
    Ft = [fliplr(Ft), Ft(2 : end)];     % ³¹czymy po³owê funkcji transmisji z jej lustrzanym odbiciem

    Hfw = fftshift(Hp) .* Ft;           % mno¿ymy transformatê przez funkcje transmisji. To juz jest
                                        % przefiltyrowany sygna³.

