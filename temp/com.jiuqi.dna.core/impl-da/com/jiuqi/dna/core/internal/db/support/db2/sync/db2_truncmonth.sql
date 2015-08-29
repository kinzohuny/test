create function dna_truncmonth(ts timestamp)
returns timestamp
specific dna_truncmonth
deterministic
language sql
no external action
begin atomic
	return timestamp_iso(date(ts)) - (day(ts)-1) days;
end