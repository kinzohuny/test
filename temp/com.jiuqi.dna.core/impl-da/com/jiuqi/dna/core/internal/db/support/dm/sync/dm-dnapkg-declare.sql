create or replace package dna is
function isleapyear(ts timestamp) return bit;
function isleapmonth(ts timestamp) return bit;
function isleapday(ts timestamp) return bit;
end dna;