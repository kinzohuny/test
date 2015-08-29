create function dna.length(@str varchar(8000))
returns int
begin
	--blankspace single-byte only
	return len(@str) + datalength(@str) - datalength(rtrim(@str));
end