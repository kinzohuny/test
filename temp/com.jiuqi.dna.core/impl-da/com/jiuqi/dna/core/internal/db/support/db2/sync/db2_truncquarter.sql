create function dna_truncquarter(ts timestamp)
returns timestamp
specific dna_truncquarter
language sql
deterministic
no external action
begin atomic
	declare st timestamp;
	set st = timestamp('1900-01-01 00:00:00');
	return timestamp(st + (3 * ((year(ts)-year(st))*4 + quarter(ts) - quarter(st))) months);
end