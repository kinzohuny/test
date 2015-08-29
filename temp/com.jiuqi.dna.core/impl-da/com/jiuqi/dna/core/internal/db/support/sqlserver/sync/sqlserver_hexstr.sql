create function dna.hexstr(@hex varbinary(8000))
returns varchar(2000)
begin
  return upper(master.sys.fn_varbintohexsubstring(0,@hex, 1, datalength(@hex)));
end