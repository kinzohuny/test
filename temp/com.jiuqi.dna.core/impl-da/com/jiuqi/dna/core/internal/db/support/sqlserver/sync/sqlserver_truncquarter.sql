create function dna.truncquarter(@ts datetime)
returns datetime
begin
	return dateadd(quarter, datediff(quarter, 0, @ts), 0);
end