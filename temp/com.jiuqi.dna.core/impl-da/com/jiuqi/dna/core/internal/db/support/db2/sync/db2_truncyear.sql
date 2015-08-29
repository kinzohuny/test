create function dna_truncyear(ts timestamp)
returns timestamp
specific dna_truncyear
deterministic
language sql
no external action
begin atomic
	return timestamp_iso(date(ts)) - (dayofyear(ts)-1) days;
end