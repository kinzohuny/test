create function dna.truncmonth(@ts datetime)
returns datetime
begin
	return dateadd(month, datediff(month, 0, @ts), 0);
end