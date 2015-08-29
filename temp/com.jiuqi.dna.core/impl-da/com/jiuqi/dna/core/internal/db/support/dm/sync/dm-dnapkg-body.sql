create or replace package body dna is

function isleapyear(ts timestamp) return bit is
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

function isleapmonth(ts timestamp) return bit is
begin
	if (extract(day from last_day(ts)) = 29) then
		return 1;
	else
		return 0;
	end if;
end;

function isleapday(ts timestamp) return bit is
begin
	if (month(ts) = 2 and dayofmonth(ts) = 29) then
		return 1;
	else
		return 0;
	end if;
end;

end dna;