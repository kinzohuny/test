create or replace package body dna is

function year(ts timestamp) return number is
begin
   	return extract(year from ts);
end;

function quarter(ts timestamp) return number is
begin
	return ceil(extract(month from ts)/3);
end;

function month(ts timestamp) return number is
begin
	return extract(month from ts);
end;

function weekofyear(ts timestamp) return number is
begin
	return (trunc(ts, 'd') - trunc(trunc(ts, 'y'), 'd'))/7 + 1;
end;

function dayofyear(ts timestamp) return number is
begin
	return trunc(ts, 'dd') - trunc(ts, 'y') + 1;
end;

function dayofmonth(ts timestamp) return number is
begin
	return extract(day from ts);
end;

function dayofweek(ts timestamp) return number is
begin
	return trunc(ts, 'dd') - trunc(ts, 'd') + 1;
end;

function dayofweek_iso(ts timestamp) return number is
begin
	return trunc(ts, 'dd') - trunc(ts, 'iw') + 1;
end;

function hour(ts timestamp) return number is
begin
	return extract(hour from ts);
end;

function minute(ts timestamp) return number is 
begin
	return extract(minute from ts);
end;

function second(ts timestamp) return number is
begin
	return floor(extract(second from ts));
end;

function millisecond(ts timestamp) return number is
begin
	return to_number(to_char(ts,'ff3'));
end;

function yearadd(ts timestamp, n number) return timestamp is
after timestamp;
diff number;
begin
	after := add_months(ts, 12 * sign(n) * floor(abs(n)));
	diff := dayofmonth(after) - dayofmonth(ts);
	if (diff > 0) then
		return after - diff;
	else
		return after;
	end if;
end;

function quarteradd(ts timestamp, n number) return timestamp is
after timestamp;
diff number;
begin
	after := add_months(ts, 3 * sign(n) * floor(abs(n)));
	diff := dayofmonth(after) - dayofmonth(ts);
	if (diff > 0) then
		return after - diff;
	else
		return after;
	end if;
end;

function monthadd(ts timestamp, n number) return timestamp is
after timestamp;
diff number;
begin
	after := add_months(ts, n); --交给add_months取整
	diff := dayofmonth(after) - dayofmonth(ts);
	if (diff > 0) then
		return after - diff;
	else
		return after;
	end if;
end;

function weekadd(ts timestamp, n number) return timestamp is
begin
	return ts + numtodsinterval(7 * sign(n) * floor(abs(n)), 'day');
end;

function dayadd(ts timestamp, n number) return timestamp is
begin
	return ts + numtodsinterval(sign(n) * floor(abs(n)), 'day');
end;

function houradd(ts timestamp, n number) return timestamp is
begin
	return ts + numtodsinterval(sign(n) * floor(abs(n)), 'hour');
end;

function minuteadd(ts timestamp, n number) return timestamp is
begin
	return ts + numtodsinterval(sign(n) * floor(abs(n)), 'minute');
end;

function secondadd(ts timestamp, n number) return timestamp is
begin
	return ts + numtodsinterval(sign(n) * floor(abs(n)), 'second');
end;
  
function yeardiff(st timestamp, ed timestamp) return number is
begin
	return floor(months_between(trunc(ed,'y'), trunc(st, 'y'))/12);
end;

function quarterdiff(st timestamp, ed timestamp) return number is
begin
	return floor(months_between(trunc(ed,'q'), trunc(st, 'q'))/3);
end;

function monthdiff(st timestamp, ed timestamp) return number is
begin
	return months_between(trunc(ed,'mm'), trunc(st, 'mm'));
end;

function weekdiff(st timestamp, ed timestamp) return number is
begin
	return trunc((trunc(ed,'d') - trunc(st, 'd'))/7);
end;

function weekdiff_iso(st timestamp, ed timestamp) return number is
begin
	return trunc((trunc(ed,'iw') - trunc(st, 'iw'))/7);
end;

function daydiff(st timestamp, ed timestamp) return number is
begin
	return trunc(ed,'dd') - trunc(st, 'dd');
end;

function isleapyear(ts timestamp) return number is
y number;
begin
	y:= year(ts);
	if mod(y, 400) = 0 then
		return 1;
	elsif mod(y, 100) = 0 then
		return 0;
	elsif mod(y, 4) = 0 then
		return 1;
	else
		return 0;
	end if;
end;

function isleapmonth(ts timestamp) return number is
begin
	if (extract(day from last_day(ts)) = 29) then
		return 1;
	else
		return 0;
	end if;
end;

function isleapday(ts timestamp) return number is
begin
	if (month(ts) = 2 and dayofmonth(ts) = 29) then
		return 1;
	else
		return 0;
	end if;
end;
  
function truncyear(ts timestamp) return timestamp is
begin
	return cast(trunc(cast(ts as date), 'y') as timestamp);
end;
  
function truncquarter(ts timestamp) return timestamp is
begin
	return cast(trunc(cast(ts as date), 'q') as timestamp);
end;
	
function truncmonth(ts timestamp) return timestamp is
begin
	return cast(trunc(cast(ts as date), 'mm') as timestamp);
end;

function truncweek(ts timestamp) return timestamp is
begin
	return trunc(ts, 'd');
end;

function truncweek_iso(ts timestamp) return timestamp is
begin
	return trunc(ts, 'iw');
end;
	
function truncday(ts timestamp) return timestamp is
begin
	return cast(trunc(cast(ts as date), 'dd') as timestamp);
end;

function current_time_millis return number is
begin
	declare
		st timestamp := timestamp'1970-01-01 00:00:00.000';
		cur timestamp with time zone := systimestamp;
		t int;
	begin
		t := trunc(cur, 'dd') - trunc(st, 'dd');
		t := t * 24 + extract(hour from cur);
		t := t * 60 + extract(minute from cur);
		t := t * 60 + extract(second from cur);
		t := t * 1000 + to_number(to_char(cur,'ff3'));
		return t;
	end;
end;

function hex_len16(n number) return varchar2 is
begin
	declare
		nn number := n;
		res varchar2(16);
		i number(2) := 0;
		b number(2);
	begin
		while i<16 loop
			b:=bitand(nn,15);
			if (b<10) then
				res := cast(b as varchar2) || res;
			else
				select decode(b,10,'A',11,'B',12,'C',13,'D',14,'E',15,'F') || res into res from dual;
        	end if;
        	nn := abs(nn/16);
        	i:=i+1;
      	end loop;
      	return res;
	end;
end;

function new_recid return raw is
begin
	declare
		mostVal number;
		most raw(8);
		least raw(8);
	begin
		--1099511627760 = 0xfffffffff0, 40 bits
		--left shift 24
		mostVal := bitand(current_time_millis(), 1099511627760) * power(2,24);
		most := hextoraw(hex_len16(mostVal));
		least := utl_raw.substr(sys_guid(), 1, 8);
		return utl_raw.concat(most, least);
	end;
end;

function collate_gbk(s varchar2) return raw is 
begin
	declare
		i number; -- char index of string
		c varchar2(4); -- each char of string
		r raw(2000); --return
		t raw(2); -- query result of each char's SN
		l number(10);
	begin
		i := 1;
		l := length(s);
		while i <= l loop
			c := substr(s, i, 1);
			if ascii(c) < 128 then
				r := utl_raw.concat(r, hextoraw('00'), utl_raw.cast_to_raw(c));
			else
				select sn into t from core_collate_gbk where ch = c;
				--exception no_data_found
				r := utl_raw.concat(r, t);
			end if;
			i := i+1;
		end loop;                  
		return r;
	end;
end;

procedure silent_drop_table(tn varchar2) is
begin
	declare
		c number;
	begin
		select count(*) into c from user_tables where table_name = tn;
		if c = 1 then
			--没有转义
			execute immediate 'drop table "' || tn || '"';
  		end if;
	end;
end;

procedure current_transaction_id(tid out number) is
begin
	select id into tid from core_transaction_id;
exception when NO_DATA_FOUND then
	select transaction_id.nextval into tid from dual;
    insert into core_transaction_id values (tid);
end;

end dna;