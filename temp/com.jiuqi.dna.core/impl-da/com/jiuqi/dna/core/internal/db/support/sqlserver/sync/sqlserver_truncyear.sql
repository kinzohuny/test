create function dna.truncyear(@ts datetime)
returns datetime
begin
	return dateadd(year, datediff(year, 0, @ts), 0);
end