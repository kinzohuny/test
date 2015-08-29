create function dna.truncday(@ts datetime)
returns datetime
begin
	return dateadd(day, datediff(day, 0, @ts), 0);
end