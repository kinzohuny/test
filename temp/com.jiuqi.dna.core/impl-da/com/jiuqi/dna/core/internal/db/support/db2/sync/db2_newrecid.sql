create function dna_newrecid()
returns char(16) for bit data
specific dna_newrecid
language sql
not deterministic
no external action
return generate_unique() concat cast(substr(hex(rand()),1,3) as char(3) for bit data)