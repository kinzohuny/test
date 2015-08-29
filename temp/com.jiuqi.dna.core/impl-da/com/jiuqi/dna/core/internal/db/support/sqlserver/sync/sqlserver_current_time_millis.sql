create function dna.current_time_millis()
returns bigint
begin
	declare @ts datetime;
	declare @n bigint;
	set @ts = dbo.getutcdate();
	set @n = datediff(day, '1970-01-01 00:00:00', @ts);
	set @n = @n * 24 + datepart(hour, @ts);
	set @n = @n * 60 + datepart(minute, @ts);
	set @n = @n * 60 + datepart(second, @ts);
	set @n = @n * 1000 + datepart(millisecond, @ts);
	return @n;
end