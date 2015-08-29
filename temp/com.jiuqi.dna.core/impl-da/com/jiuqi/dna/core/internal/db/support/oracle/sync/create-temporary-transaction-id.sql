declare
  c integer;
begin
  select count(*)
    into c
    from user_tables
   where table_name = upper('core_transaction_id');
  if c = 0 then
    execute immediate 'create global temporary table core_transaction_id(id number) on commit delete rows';
  end if;
end;