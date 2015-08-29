create function dna_current_time_millis()
returns bigint
specific dna_current_time_millis
language sql
not deterministic
no external action
begin atomic
	declare t bigint;
	declare st timestamp;
	declare cur timestamp;
	set st = timestamp('1970-01-01 00:00:00.000');
	set cur = current timestamp - current timezone;--UTC timestamp
	set t = 0;
	set t = days(cur) - days(st);
	set t = t * 24 + hour(cur);
	set t = t * 60 + minute(cur);
	set t = t * 60 + second(cur);
	set t = t * 1000 + microsecond(cur)/1000;
	return t;
end