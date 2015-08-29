create function dna_truncday(ts timestamp)
returns timestamp
specific dna_truncday
language sql
deterministic
no external action
begin atomic
	return timestamp_iso(date(ts));
end