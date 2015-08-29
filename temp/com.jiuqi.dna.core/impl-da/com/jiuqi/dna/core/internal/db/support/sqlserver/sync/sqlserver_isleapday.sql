create function dna.isleapday(@ts datetime)
returns bit
begin
	return case when datepart(mm,@ts)=2 and datepart(dd,@ts)=29 then 1 else 0 end;
end