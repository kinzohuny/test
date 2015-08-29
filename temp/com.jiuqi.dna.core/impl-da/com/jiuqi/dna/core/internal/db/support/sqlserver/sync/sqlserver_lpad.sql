create function dna.lpad(@str varchar(8000), @len int, @pad varchar(8000) = ' ')
returns varchar(8000)
begin
	if @len > 8000
		set @len = 8000;
	declare @strl int;
	declare @r varchar(8000);
	set @strl = dna.length(@str);
	if @strl > @len
		set @r = substring(@str, 1, @len);
	else if @strl = @len
		set @r = @str;
	else
		begin
			declare @lack int;
			set @lack = @len - @strl;
			declare @rep int;
			set @rep = ceiling((cast(@lack as float)) / (dna.length(@pad)));
			set @r = substring(replicate(@pad, @rep), 1, @lack) + @str;
		end;
	return @r;
end