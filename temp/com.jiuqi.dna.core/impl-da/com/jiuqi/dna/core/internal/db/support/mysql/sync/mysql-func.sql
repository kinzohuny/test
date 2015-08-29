drop function if exists dna_isleapyear;
drop function if exists dna_isleapmonth;
drop function if exists dna_isleapday;

drop function if exists dna_truncyear;
drop function if exists dna_truncquarter;
drop function if exists dna_truncmonth;
drop function if exists dna_truncweek;
drop function if exists dna_truncday;

drop function if exists dna_yeardiff;
drop function if exists dna_quarterdiff;
drop function if exists dna_monthdiff;
drop function if exists dna_weekdiff;
drop function if exists dna_daydiff;

drop function if exists dna_week;

drop function if exists dna_newrecid;
drop function if exists dna_bigint2hex;
drop function if exists dna_byte2hex;

drop function if exists dna_collate_gbk;
drop function if exists dna_datestr;

create function dna_isleapyear(ts datetime)
returns bit
deterministic
begin
	declare y int;
	set y = year(ts);
	return case when y%400=0 then 1 when y%100=0 then 0 when y%4=0 then 1 else 0 end;
end;

create function dna_isleapmonth(ts datetime)
returns bit
deterministic
begin
	declare y int;
	set y = year(ts);
	return case when month(ts)<>2 then 0 when y%400=0 then 1 when y%100=0 then 0 when y%4=0 then 1 else 0 end;
end;

create function dna_isleapday(ts datetime)
returns bit
deterministic
begin
	return case when month(ts)=2 and dayofmonth(ts)=29 then 1 else 0 end;
end;

create function dna_truncyear(ts datetime)
returns datetime
deterministic
return makedate(year(ts),1);

create function dna_truncquarter(ts datetime)
returns datetime
deterministic
return timestampadd(quarter, timestampdiff(quarter, '1900-1-1', ts), '1900-1-1');

create function dna_truncmonth(ts datetime)
returns datetime
deterministic
return timestampadd(month, timestampdiff(month, '1900-1-1', ts), '1900-1-1');

create function dna_truncday(ts datetime)
returns datetime
deterministic
return makedate(year(ts), dayofyear(ts));

create function dna_truncweek(ts datetime)
returns datetime
deterministic
return  dna_truncday(ts) - interval (dayofweek(ts)-1) day;

create function dna_yeardiff(st datetime, ed datetime)
returns int
deterministic
begin
	return year(ed)-year(st);
end;

create function dna_quarterdiff(st datetime, ed datetime)
returns int
deterministic
begin
	return (year(ed)-year(st)) * 4 + quarter(ed) - quarter(st);
end;

create function dna_monthdiff(st datetime, ed datetime)
returns int
deterministic
begin
	return (year(ed)-year(st)) * 12 + month(ed) - month(st);
end;

create function dna_daydiff(st datetime, ed datetime)
returns int
deterministic
return timestampdiff(day, dna_truncday(st), dna_truncday(ed));

create function dna_weekdiff(st datetime, ed datetime)
returns int
deterministic
return dna_daydiff(dna_truncweek(st),dna_truncweek(ed))/7;

create function dna_week(ts datetime)
returns int
deterministic
return timestampdiff(day, dna_truncweek(dna_truncyear(ts)), dna_truncweek(ts))/7+1;

create function dna_byte2hex(val tinyint unsigned)
returns varchar(2)
deterministic
begin
	if (val is null) then
		return '';
	elseif (val <16) then
		return concat('0', hex(val));
	else
		return hex(val);
	end if;
end;

create function dna_bigint2hex(val bigint unsigned)
returns varchar(16)
deterministic
begin
	declare s varchar(16);
	declare i int;
	if (val is null) then
		return '0000000000000000';
	end if;
	set s = dna_byte2hex(val & 255);
	set i = 1;
	repeat
		set s = concat(dna_byte2hex((val >> (i * 8)) & 255), s);
		set i = i + 1;
	until i = 8 end repeat;
	return s;
end;

create function dna_newrecid()
returns binary(16)
not deterministic
begin
	declare ts bigint;
	declare seq bigint;
	declare m bigint;
	declare l bigint;
	set ts = timestampdiff(second, date'1970-1-1', now());
	set seq = (uuid_short() & 1048575) << 4;
	set m = ((ts << 24) & 9223372036854775807) + seq;
	set l = floor(rand() * 9223372036854775807);
	return unhex(concat(dna_bigint2hex(m), dna_bigint2hex(l)));
end;

create function dna_collate_gbk(s varchar(2000))
returns varbinary(4000)
deterministic
begin
	declare i int default 1;
	declare c varchar(4);
	declare r varbinary(4000) default binary '';
	declare t binary(2);
	while i <= char_length(s) do
		set c = substr(s, i, 1);
		if ascii(c) < 128 then 
			set r = concat(r, dna_byte2hex(0), dna_byte2hex(ascii(c)));
		else
			select sn into t from CORE_COLLATE_GBK where ch = c;
			set r = concat(r, t);
		end if;
		set i = i + 1;
	end while;                  
	return r;
end;

create function dna_datestr(rawdate datetime, format_string varchar(50))
returns varchar(50)
deterministic
begin
   declare isd bool;
   declare ishh12 bool;
   declare result varchar(50);
   declare isam bool;
   declare count int;
   set isd=false;
   set ishh12=false;

   if(position('yyyy' in format_string))
   then set format_string=replace(format_string,'yyyy','%Y');
   else set format_string=replace(format_string,'yy','%y');
   end if;
   set format_string=replace(format_string,'mm','%m');


  if(position('ddd' in format_string))
   then set format_string=replace(format_string,'ddd','%j');
   end if;
   if(position('dd' in format_string))
   then set format_string=replace(format_string,'dd','%d');
   end if;
   set format_string=replace(format_string,'%d','%z');
   if(position('d' in format_string) )
   then set format_string=replace(format_string,'d','%wx');
   set isd=true;
   end if;
   set format_string=replace(format_string,'%z','%d');
   
   if(position('hh24' in format_string))
   then set format_string=replace(format_string,'hh24','%H');
   elseif(position('hh12' in format_string))
   then set format_string=replace(format_string,'hh12','%h');
   set ishh12=true;
   else set format_string=replace(format_string,'hh','%h');
   set ishh12=true;
   end if;
   
   if(position('am' in format_string))
   then set isam=true;
   end if;
   
   set format_string=replace(format_string,'mi','%i');
   set format_string=replace(format_string,'ss','%s');
   
   set result=date_format(rawdate,format_string);
   
   if(isd)
   then set count=substring(result,position('x' in result)-1,position('x' in result))+1;
   set result=replace(result,concat(count-1,'x'),count);
   end if;

   if(ishh12 and isam)
   then set result=replace(result,'am',date_format(rawdate,'%p'));
   end if;
   
   set result=replace(result,'q',quarter(rawdate));
   if((week(rawdate,0)+1)<10)
   then set result=replace(result,'ww',concat(0,week(rawdate,0)+1));
   else set result=replace(result,'ww',week(rawdate,0)+1);
   end if;
   set result=replace(result,'w',(day(rawdate)+(WEEKDAY(rawdate-interval day(rawdate) day)+1) mod 7) div 7 + 1);
   return result;
end