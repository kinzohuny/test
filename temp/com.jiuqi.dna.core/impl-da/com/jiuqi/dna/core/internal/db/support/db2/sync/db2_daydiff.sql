create function dna_daydiff(st timestamp, ed timestamp)
returns int
specific dna_daydiff
language sql
deterministic
no external action
begin atomic
	return days(ed) - days(st);
end