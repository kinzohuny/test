create function dna_collate_gbk(s varchar(2000))
returns varchar(4000) for bit data
specific dna_collate_gbk
language sql
deterministic
no external action
begin atomic
	declare i int;
	declare c varchar(4);
	declare r varchar(4000) for bit data;
	declare t varchar(2) for bit data;
	declare l int;
	set i = 1;
	set r = '';
	set l = length(s, codeunits16);
	while i <= l do
		set c = substr(s, i, 1);
		set t = (select SN from CORE_COLLATE_GBK where CH = c fetch first 1 row only);
		set r = concat(r, t);
		set i = i + 1;
	end while;                  
	return r;
end