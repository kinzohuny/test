create function dna.isleapyear(@ts datetime)
returns bit
begin
	declare @y int;
	set @y = datepart(yy, @ts);
	return case when @y%400=0 then 1 when @y%100=0 then 0 when @y%4=0 then 1 else 0 end;
end