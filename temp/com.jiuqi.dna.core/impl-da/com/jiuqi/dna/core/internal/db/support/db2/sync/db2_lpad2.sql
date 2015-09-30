create function dna_lpad(str varchar(4000), len integer, pad varchar(4000))
returns varchar(4000)
specific dna_lpad2
language sql
deterministic
no external action
begin atomic
	declare len2 int;
	declare strl int;
	declare r varchar(400);
	declare lack int;
	declare rep int;
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
		set lack = len2 - strl;
		set rep = ceil((cast(lack as double)) / length(pad, codeunits16));
		set r = substr(repeat(pad, rep), 1, lack) || str;
	end if;
	return r;
end