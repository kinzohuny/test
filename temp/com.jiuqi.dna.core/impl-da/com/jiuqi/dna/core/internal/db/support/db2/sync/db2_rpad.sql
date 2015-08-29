create function dna_rpad(str varchar(4000), len integer)
returns varchar(4000)
specific dna_rpad
language sql
deterministic
no external action
begin atomic
	declare len2 int;
	declare strl int;
	declare r varchar(400);
	set strl = length(str, codeunits16);
	if ( len > 4000) then
		set len2 = 4000;
	else
		set len2 = len;
	end if;
	if (strl > len2) then
		set r = substr(str, 1, len2);
	elseif (strl = len2) then
		set r = str;
	else
		set r = str || repeat(' ', len2 - strl);
	end if;
	return r;
end