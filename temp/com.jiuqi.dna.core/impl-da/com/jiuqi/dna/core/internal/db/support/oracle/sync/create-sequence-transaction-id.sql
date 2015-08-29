declare
  c integer;
begin
  select count(*)
    into c
    from user_sequences
   where sequence_name = upper('transaction_id');
  if c = 0 then
    execute immediate 'create sequence transaction_id increment by 1 start with 1 nomaxvalue nocycle cache 10';
  end if;
end;