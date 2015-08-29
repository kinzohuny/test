create function dna.to_bin8(@n bigint)
returns binary(8)
begin
	declare @silence varbinary(8);
	set @silence = 0x;
	declare @i int;
	set @i = 0;
	while @i < 8
	begin
		set @silence = cast((@n & 255) as binary(1)) + @silence;
		set	@n = abs(@n/256);
		set @i = @i + 1;
	end;
	return @silence;
end