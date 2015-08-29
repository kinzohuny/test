create function dna_truncweek(ts timestamp)
returns timestamp
specific dna_truncweek
deterministic
language sql
no external action
begin atomic
	return  timestamp_iso(date(ts)) - (dayofweek(ts)-1) days;
end