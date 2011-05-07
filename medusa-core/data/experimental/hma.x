{jtHMA - Hull Moving Average Function}
{Author: Atavachron}
{May 2005}		
	
Inputs: price(NumericSeries), length(NumericSimple);
Vars: halvedLength(0), sqrRootLength(0);

{
 Original equation is:
 ---------------------
 waverage(2*waverage(close,period/2)-waverage(close,period), SquareRoot(Period)
 Implementation below is more efficient with lengthy Weighted Moving Averages.
 In addition, the length needs to be converted to an integer value after it is halved and
 its square root is obtained in order for this to work with Weighted Moving Averaging
}

if ((ceiling(length / 2) - (length / 2))  <= 0.5) then
	halvedLength = ceiling(length / 2)
else
	halvedLength = floor(length / 2);

if ((ceiling(SquareRoot(length)) - SquareRoot(length))  <= 0.5) then
	sqrRootLength = ceiling(SquareRoot(length))
else
	sqrRootLength = floor(SquareRoot(length));

Value1 = 2 * WAverage(price, halvedLength);
Value2 = WAverage(price, length);
Value3 = WAverage((Value1 - Value2), sqrRootLength);

jtHMA = Value3;