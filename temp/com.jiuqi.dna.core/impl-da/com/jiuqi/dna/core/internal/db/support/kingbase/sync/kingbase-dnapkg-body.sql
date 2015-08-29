create or replace package body dna as
function isleapyear(ts timestamp) returns number as
 begin
 declare
 y number;
  begin
    y:= DATEPART('year',ts);
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
 end;
 
function isleapmonth(ts timestamp) returns number as
 begin
 declare
 m number;
  begin
    m:= DATEPART('month',ts);
    if public.dna.isleapyear(ts)>0 and m = 2 then
      return 1;
    end if;
    return 0;
  end;
 end;
  
 function isleapday(ts timestamp) returns number as
 begin
 declare
 m number;
 d number;
  begin
    m:= DATEPART('month',ts);
    d:= DATEPART('day',ts);
    if m = 2 and d>28 then
      return 1;
    end if;
    return 0;
  end;
 end;
 
FUNCTION collate_gbk(s character(8000)) return bytea as
 BEGIN
	DECLARE
		i number; -- char index of string
		c character(4);
		r bytea; --return
		t bytea; -- query result of each char's SN
		l number(10);
	BEGIN
		i := 1;
		l := length(s);
		while i <= l loop
			c := substr(s, i, 1);
			if ascii(c) < 128 then
				if i=1 then
					r := decoding('00'||to_hex(ascii(c)),'hex');
				else
					r := r|| decoding('00'||to_hex(ascii(c)),'hex');
				end if;
			else
				select sn into t from core_collate_gbk where ch = c; --exception no_data_found
				if i=1 then
					r := t;
				else
					r := r || t;
				end if;
			end if;
			i := i+1;
		end loop;                  
		return r;
	end;
 end;

end;