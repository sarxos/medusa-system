Indicator MQL_JMA;
input
  Len_I = 14, 
  phase_I = 0, 
  BarCount_I = 300; 

draw
  ExtMapBuffer1_L("L1"); 

Vars 
  firstTime_B(bool), 


  series_D(number), 
  vv_D(number), 
  v1_D(number), 
  v2_D(number), 
  v3_D(number), 
  v4_D(number), 
  s8_D(number), 
  s10_D(number), 
  s18_D(number), 
  s20_D(number), 
  s28_D(number), 
  s30_D(number), 
  s68_D(number), 
  s70_D(number), 
  f8_D(number), 
  f10_D(number), 
  f18_D(number), 
  f20_D(number), 
  f28_D(number), 
  f30_D(number), 
  f38_D(number), 
  f40_D(number), 
  f48_D(number), 
  f50_D(number), 
  f58_D(number), 
  f60_D(number), 
  f68_D(number), 
  f70_D(number), 
  f78_D(number), 
  f80_D(number), 
  f88_D(number), 
  f90_D(number), 
  f98_D(number), 
  fA0_D(number), 
  fA8_D(number), 
  fB0_D(number), 
  fB8_D(number), 
  fC0_D(number), 
  fC8_D(number), 
  fD0_D(number), 
  f0_D(number), 
  fD8_D(number), 
  fE0_D(number), 
  fE8_D(number), 
  fF8_D(number), 
  JMA_D(number), 
  prevtime_D(number), 
  list_D(Series), 
  ring1_D(Series), 
  ring2_D(Series), 
  buffer_D(Series), 

  ExtMapBuffer1_L_D(Series), 
 
  deinit_I(number),
  counted_bars_I(number), 
  AccountedBars_I(number), 
  jj_I(number), 
  ii_I(number), 
  shift_I(number), 
  v5_I(number), 
  v6_I(number), 
  s38_I(number), 
  s40_I(number), 
  s48_I(number), 
  s50_I(number), 
  s58_I(number), 
  s60_I(number), 
  fF0_I(number), 
  value2_I(number), 


  fst = 0,
  lst = 0,
  MqBars = 0,
  MqIndicatorCounted = 0,
  MathCeil = 0
  ;

BEGIN
  fst := front(close);
  lst := back(close);
  if fst >= lst then return;
  MqBars := lst;

   /*~S~T~A~R~T~*/
  
   begin 
     counted_bars_I := MqIndicatorCounted ;
     firstTime_B := True ;
     AccountedBars_I := 0 ;
     jj_I := 0 ;
     ii_I := 0 ;
     shift_I := 0 ;
     series_D := 0 ;
     vv_D := 0 ;
     v1_D := 0 ;
     v2_D := 0 ;
     v3_D := 0 ;
     v4_D := 0 ;
     s8_D := 0 ;
     s10_D := 0 ;
     s18_D := 0 ;
     s20_D := 0 ;
     v5_I := 0 ;
     v6_I := 0 ;
     s28_D := 0 ;
     s30_D := 0 ;
     s38_I := 0 ;
     s40_I := 0 ;
     s48_I := 0 ;
     s50_I := 0 ;
     s58_I := 0 ;
     s60_I := 0 ;
     s68_D := 0 ;
     s70_D := 0 ;
     f8_D := 0 ;
     f10_D := 0 ;
     f18_D := 0 ;
     f20_D := 0 ;
     f28_D := 0 ;
     f30_D := 0 ;
     f38_D := 0 ;
     f40_D := 0 ;
     f48_D := 0 ;
     f50_D := 0 ;
     f58_D := 0 ;
     f60_D := 0 ;
     f68_D := 0 ;
     f70_D := 0 ;
     f78_D := 0 ;
     f80_D := 0 ;
     f88_D := 0 ;
     f90_D := 0 ;
     f98_D := 0 ;
     fA0_D := 0 ;
     fA8_D := 0 ;
     fB0_D := 0 ;
     fB8_D := 0 ;
     fC0_D := 0 ;
     fC8_D := 0 ;
     fD0_D := 0 ;
     f0_D := 0 ;
     fD8_D := 0 ;
     fE0_D := 0 ;
     fE8_D := 0 ;
     fF0_I := 0 ;
     fF8_D := 0 ;
     value2_I := 0 ;
     JMA_D := 0 ;
     prevtime_D := 0 ;
  //   list_D [ 127 ] ;
  //   ring1_D [ 127 ] ;
  //   ring2_D [ 10 ] ;
  //   buffer_D [ 61 ] ;
     list_D := makeseries(fst, lst, 0 ) ; 
     ring1_D := makeseries(fst, lst, 0 ) ; 
     ring2_D := makeseries(fst, lst, 0 ) ; 
     buffer_D := makeseries(fst, lst, 0 ) ; 
     if ( firstTime_B )  then 
     begin 
       AccountedBars_I := MqBars - BarCount_I ;
       firstTime_B := False ;      
     end; 
     if ( ( Time ( ) - prevtime_D ) < 30 )  then  return ;
     prevtime_D := Time ( ) ;
     
     begin 
       s28_D := 63 ;
       s30_D := 64 ;
       for ii_I := 1 to {ii_I <=} s28_D { ii_I := ii_I + 1 } do  
       begin 
         list_D [ ii_I ] := - 1000000 ;
       end; 
       for ii_I := s30_D to {ii_I <=} 127 { ii_I := ii_I + 1 } do  
       begin 
         list_D [ ii_I ] := 1000000 ;
       end; 
       f0_D := 1 ;
     end; 
     //for shift_I := BarCount_I downto {shift_I >=} 0 { shift_I := shift_I - 1 } do  
     for shift_I := fst to lst do
     begin 
       series_D := Close [ shift_I ] ;
       if ( fF0_I < 61 )  then 
       begin 
         fF0_I := fF0_I + 1 ;
         buffer_D [ fF0_I ] := series_D ;
         
       end; 
       if ( fF0_I > 30 )  then 
       begin 
         if ( Len_I < 1.0000000002 )  then 
         begin 
           f80_D := 0.0000000001 ;
           
         end 
         else 
         begin 
           f80_D := ( Len_I - 1 ) / 2.0 ;
           
         end; 
         if ( phase_I < - 100 )  then 
         begin 
           f10_D := 0.5 ;
           
         end 
         else 
         begin 
           if ( phase_I > 100 )  then 
           begin 
             f10_D := 2.5 ;
             
           end 
           else 
           begin 
             f10_D := phase_I / 100 + 1.5 ;
             
           end; 
           
         end; 
         v1_D := {math}Log ( {math}Sqrt ( f80_D ) ) ;
         v2_D := v1_D ;
         if ( v1_D / {math}Log ( 2.0 ) + 2.0 < 0.0 )  then 
         begin 
           v3_D := 0 ;
           
         end 
         else 
         begin 
           v3_D := v2_D / {math}Log ( 2.0 ) + 2.0 ;
           
         end; 
         f98_D := v3_D ;
         if ( 0.5 <= f98_D - 2.0 )  then 
         begin 
           f88_D := f98_D - 2.0 ;
           
         end 
         else 
         begin 
           f88_D := 0.5 ;
           
         end; 
         f78_D := {math}Sqrt ( f80_D ) * f98_D ;
         f90_D := f78_D / ( f78_D + 1.0 ) ;
         f80_D := f80_D * 0.9 ;
         f50_D := f80_D / ( f80_D + 2.0 ) ;
         if ( f0_D <> 0 )  then 
         begin 
           f0_D := 0 ;
           v5_I := 0 ;
           for ii_I := 1 to {ii_I <=} 29 { ii_I := ii_I + 1 } do  
           begin 
             if ( buffer_D [ ii_I + 1 ] <> buffer_D [ ii_I ] )  then 
             begin 
               v5_I := 1.0 ;
               
             end; 
             
           end; 
           fD8_D := v5_I * 30.0 ;
           if ( fD8_D = 0 )  then 
           begin 
             f38_D := series_D ;
             
           end 
           else 
           begin 
             f38_D := buffer_D [ 1 ] ;
             
           end; 
           f18_D := f38_D ;
           if ( fD8_D > 29 )  then 
              fD8_D := 29 ;
           
         end 
         else fD8_D := 0 ;
         //for ii_I := fD8_D downto {ii_I >=} 0 { ii_I := ii_I - 1 } do  
         for ii_I := lst-fD8_D to lst do  
         begin 
           value2_I := 31 - ii_I ;
           if ( ii_I = 0 )  then 
           begin 
             f8_D := series_D ;
             
           end 
           else 
           begin 
             f8_D := buffer_D [ value2_I ] ;
             
           end; 
           f28_D := f8_D - f18_D ;
           f48_D := f8_D - f38_D ;
           if ( {math}Abs ( f28_D ) > {math}Abs ( f48_D ) )  then 
           begin 
             v2_D := {math}Abs ( f28_D ) ;
             
           end 
           else 
           begin 
             v2_D := {math}Abs ( f48_D ) ;
             
           end; 
           fA0_D := v2_D ;
           vv_D := fA0_D + 0.0000000001 ;
           if ( s48_I <= 1 )  then 
           begin 
             s48_I := 127 ;
             
           end 
           else 
           begin 
             s48_I := s48_I - 1 ;
             
           end; 
           if ( s50_I <= 1 )  then 
           begin 
             s50_I := 10 ;
             
           end 
           else 
           begin 
             s50_I := s50_I - 1 ;
             
           end; 
           if ( s70_D < 128 )  then 
              s70_D := s70_D + 1 ;
           s8_D := s8_D + vv_D - ring2_D [ s50_I ] ;
           ring2_D [ s50_I ] := vv_D ;
           if ( s70_D > 10 )  then 
           begin 
             s20_D := s8_D / 10 ;
             
           end 
           else s20_D := s8_D / s70_D ;
           if ( s70_D > 127 )  then 
           begin 
             s10_D := ring1_D [ s48_I ] ;
             ring1_D [ s48_I ] := s20_D ;
             s68_D := 64 ;
             s58_I := s68_D ;
             while ( s68_D > 1 )  do 
             begin 
               if ( list_D [ s58_I ] < s10_D )  then 
               begin 
                 s68_D := s68_D * 0.5 ;
                 s58_I := s58_I + s68_D ;
                 
               end 
               else if ( list_D [ s58_I ] <= s10_D )  then 
               begin 
                 s68_D := 1 ;
                 
               end 
               else 
               begin 
                 s68_D := s68_D * 0.5 ;
                 s58_I := s58_I - s68_D ;
                 
               end; 
               
             end; 
             
           end 
           else 
           begin 
             ring1_D [ s48_I ] := s20_D ;
             if ( s28_D + s30_D > 127 )  then 
             begin 
               s30_D := s30_D - 1 ;
               s58_I := s30_D ;
               
             end 
             else 
             begin 
               s28_D := s28_D + 1 ;
               s58_I := s28_D ;
               
             end; 
             if ( s28_D > 96 )  then 
             begin 
               s38_I := 96 ;
               
             end 
             else s38_I := s28_D ;
             if ( s30_D < 32 )  then 
             begin 
               s40_I := 32 ;
               
             end 
             else s40_I := s30_D ;
             
           end; 
           s68_D := 64 ;
           s60_I := s68_D ;
           while ( s68_D > 1 )  do 
           begin 
             if ( list_D [ s60_I ] >= s20_D )  then 
             begin 
               if ( list_D [ s60_I - 1 ] <= s20_D )  then 
               begin 
                 s68_D := 1 ;
                 
               end 
               else 
               begin 
                 s68_D := s68_D * 0.5 ;
                 s60_I := s60_I - s68_D ;
                 
               end; 
               
             end 
             else 
             begin 
               s68_D := s68_D * 0.5 ;
               s60_I := s60_I + s68_D ;
               
             end; 
             if ( ( s60_I = 127 ) and ( s20_D > list_D [ 127 ] ) )  then 
                s60_I := 128 ;
             
           end; 
           if ( s70_D > 127 )  then 
           begin 
             if ( s58_I >= s60_I )  then 
             begin 
               if ( ( s38_I + 1 > s60_I ) and ( s40_I - 1 < s60_I ) )  then 
               begin 
                 s18_D := s18_D + s20_D ;
                 
               end 
               else if ( ( s40_I > s60_I ) and ( s40_I - 1 < s58_I ) )  then 
                  s18_D := s18_D + list_D [ s40_I - 1 ] ;
               
             end 
             else if ( s40_I >= s60_I )  then 
             begin 
               if ( ( s38_I + 1 < s60_I ) and ( s38_I + 1 > s58_I ) )  then 
                  s18_D := s18_D + list_D [ s38_I + 1 ] ;
               
             end 
             else if ( s38_I + 2 > s60_I )  then 
             begin 
               s18_D := s18_D + s20_D ;
               
             end 
             else if ( ( s38_I + 1 < s60_I ) and ( s38_I + 1 > s58_I ) )  then 
                s18_D := s18_D + list_D [ s38_I + 1 ] ;
             if ( s58_I > s60_I )  then 
             begin 
               if ( ( s40_I - 1 < s58_I ) and ( s38_I + 1 > s58_I ) )  then 
               begin 
                 s18_D := s18_D - list_D [ s58_I ] ;
                 
               end 
               else if ( ( s38_I < s58_I ) and ( s38_I + 1 > s60_I ) )  then 
                  s18_D := s18_D - list_D [ s38_I ] ;
               
             end 
             else 
             begin 
               if ( ( s38_I + 1 > s58_I ) and ( s40_I - 1 < s58_I ) )  then 
               begin 
                 s18_D := s18_D - list_D [ s58_I ] ;
                 
               end 
               else if ( ( s40_I > s58_I ) and ( s40_I < s60_I ) )  then 
                  s18_D := s18_D - list_D [ s40_I ] ;
               
             end; 
             
           end; 
           if ( s58_I <= s60_I )  then 
           begin 
             if ( s58_I >= s60_I )  then 
             begin 
               list_D [ s60_I ] := s20_D ;
               
             end 
             else 
             begin 
               for jj_I := s58_I + 1 to {jj_I <=} s60_I - 1 { jj_I := jj_I + 1 } do  
               begin 
                 list_D [ jj_I - 1 ] := list_D [ jj_I ] ;
                 
               end; 
               list_D [ s60_I - 1 ] := s20_D ;
               
             end; 
             
           end 
           else 
           begin 
             for jj_I := s58_I - 1 downto {jj_I >=} s60_I { jj_I := jj_I - 1 } do  
             begin 
               list_D [ jj_I + 1 ] := list_D [ jj_I ] ;
               
             end; 
             list_D [ s60_I ] := s20_D ;
             
           end; 
           if ( s70_D <= 127 )  then 
           begin 
             s18_D := 0 ;
             for jj_I := s40_I to {jj_I <=} s38_I { jj_I := jj_I + 1 } do  
             begin 
               s18_D := s18_D + list_D [ jj_I ] ;
               
             end; 
             
           end; 
           f60_D := s18_D / ( s38_I - s40_I + 1 ) ;
           if ( fF8_D + 1 > 31 )  then 
           begin 
             fF8_D := 31 ;
             
           end 
           else fF8_D := fF8_D + 1 ;
           if ( fF8_D <= 30 )  then 
           begin 
             if ( f28_D > 0 )  then 
             begin 
               f18_D := f8_D ;
               
             end 
             else f18_D := f8_D - f28_D * f90_D ;
             if ( f48_D < 0 )  then 
             begin 
               f38_D := f8_D ;
               
             end 
             else f38_D := f8_D - f48_D * f90_D ;
             fB8_D := series_D ;
             if ( fF8_D <> 30 )  then 
             begin 
               continue ;
               
             end; 
             if ( fF8_D = 30 )  then 
             begin 
               fC0_D := series_D ;
               if ( MQLF("MathCeil", f78_D ) >= 1 )  then 
               begin 
                 v4_D := MQLF("MathCeil", f78_D ) ;
                 
               end 
               else v4_D := 1 ;
               fE8_D := MQLF("MathCeil", v4_D ) ;
               if ( {MathFloor}int ( f78_D ) >= 1 )  then 
               begin 
                 v2_D := {MathFloor}int ( f78_D ) ;
                 
               end 
               else v2_D := 1 ;
               fE0_D := MQLF("MathCeil", v2_D ) ;
               if ( fE8_D = fE0_D )  then 
               begin 
                 f68_D := 1 ;
                 
               end 
               else 
               begin 
                 v4_D := fE8_D - fE0_D ;
                 f68_D := ( f78_D - fE0_D ) / v4_D ;
                 
               end; 
               if ( fE0_D <= 29 )  then 
               begin 
                 v5_I := fE0_D ;
                 
               end 
               else v5_I := 29 ;
               if ( fE8_D <= 29 )  then 
               begin 
                 v6_I := fE8_D ;
                 
               end 
               else v6_I := 29 ;
               fA8_D := ( series_D - buffer_D [ fF0_I - v5_I ] ) * ( 1 - f68_D ) / fE0_D + ( series_D - buffer_D [               fF0_I - v6_I ] ) * f68_D / 
               fE8_D ;
               
             end; 
             
           end 
           else 
           begin 
             if ( f98_D >= {math}Pow ( fA0_D / f60_D ,
             f88_D ) )  then 
             begin 
               v1_D := {math}Pow ( fA0_D / f60_D ,
               f88_D ) ;
               
             end 
             else v1_D := f98_D ;
             if ( v1_D < 1 )  then 
             begin 
               v2_D := 1 ;
               
             end 
             else 
             begin 
               if ( f98_D >= {math}Pow ( fA0_D / f60_D ,
               f88_D ) )  then 
               begin 
                 v3_D := {math}Pow ( fA0_D / f60_D ,
                 f88_D ) ;
                 
               end 
               else v3_D := f98_D ;
               v2_D := v3_D ;
               
             end; 
             f58_D := v2_D ;
             f70_D := {math}Pow ( f90_D ,
             {math}Sqrt ( f58_D ) ) ;
             if ( f28_D > 0 )  then 
             begin 
               f18_D := f8_D ;
               
             end 
             else 
             begin 
               f18_D := f8_D - f28_D * f70_D ;
               
             end; 
             if ( f48_D < 0 )  then 
             begin 
               f38_D := f8_D ;
               
             end 
             else 
             begin 
               f38_D := f8_D - f48_D * f70_D ;
               
             end; 
             
           end; 
           
         end; 
         if ( fF8_D > 30 )  then 
         begin 
           f30_D := {math}Pow ( f50_D ,
           f58_D ) ;
           fC0_D := ( 1 - f30_D ) * series_D + f30_D * fC0_D ;
           fC8_D := ( series_D - fC0_D ) * ( 1 - f50_D ) + f50_D * fC8_D ;
           fD0_D := f10_D * fC8_D + fC0_D ;
           f20_D := - f30_D * 2 ;
           f40_D := f30_D * f30_D ;
           fB0_D := f20_D + f40_D + 1 ;
           fA8_D := ( fD0_D - fB8_D ) * fB0_D + f40_D * fA8_D ;
           fB8_D := fB8_D + fA8_D ;
           
         end; 
         JMA_D := fB8_D ;
         
       end; 
       if ( fF0_I <= 30 )  then 
       begin 
         JMA_D := 0 ;
         
       end; 
       Print ( " JMA_D is " + numbertostring(JMA_D) + " shift_I is " + numbertostring(shift_I) ) ;
       ExtMapBuffer1_L_D [ shift_I ] := JMA_D ;
       if ( shift_I > 0 )  then 
       begin 
         AccountedBars_I := AccountedBars_I + 1 ;
         
       end; 
       
     end; 
     
   end;
END.