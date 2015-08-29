create function dna.truncweek(@ts datetime)
returns datetime
begin
	return dateadd(day, 1 - datepart(weekday, @ts), dateadd(day, datediff(day, 0, @ts), 0));
end