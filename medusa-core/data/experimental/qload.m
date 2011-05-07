function [t, o, h, l, c, v] = qload(name)
    
    path = strcat('../tmp/mstcgl/', name, '.mst');
    pattern = strcat('%', num2str(length(name)),'s,%4d%2d%2d,%f,%f,%f,%f,%d');
    [n, y, m, d, o, h, l, c, v] = textread(path, pattern, 'headerlines', 1, 'delimiter', '\n');
    t = datenum(y, m, d);