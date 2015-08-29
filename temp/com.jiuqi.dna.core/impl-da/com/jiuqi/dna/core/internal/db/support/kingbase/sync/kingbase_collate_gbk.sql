CREATE OR REPLACE FUNCTION collate_gbk(s varchar) return bytea as
BEGIN
	DECLARE
		i number; -- char index of string
		c varchar(4);
		r bytea; --return
		t bytea; -- query result of each char's SN
		l number(10);
	BEGIN
		i := 1;
		l := length(s);
		while i <= l loop
			c := substr(s, i, 1);
			if ascii(c) < 128 then
				r := r|| hextoraw('00'||c);
			else
				select sn into t from core_collate_gbk where ch = c;
				--exception no_data_found
				r := r || t;
			end if;
			i := i+1;
		end loop;                  
		return r;
	end;
end;