create function dna_weekdiff(st timestamp, ed timestamp)
returns int
specific dna_weekdiff
deterministic
language sql
no external action
begin atomic
	declare stdays int;
	declare eddays int;
	set stdays = days(timestamp_iso(date(st)) - (dayofweek(st)-1) days);
	set eddays = days(timestamp_iso(date(ed)) - (dayofweek(ed)-1) days);
	return (eddays - stdays)/7;
end