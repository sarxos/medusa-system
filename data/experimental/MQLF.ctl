function MQLF;
Input func(string), p1=0, p2=0;
Result resN(number);
vars i=0, v1=0, v2=0, v3=0;
begin
  if func = "MathCeil" then
  begin
    if p1 >= 0 then if frac(p1) = 0 then resN := int(p1) 
                                    else resN := int(p1)+1 
               else if frac(p1) = 0 then resN := int(p1)-1 
                                    else resN := int(p1);
  //end else if func = "bull" then
  
  
  end else begin
    Print("Function MQLF, unknown cmd >"+func+"<");
  end;
end.