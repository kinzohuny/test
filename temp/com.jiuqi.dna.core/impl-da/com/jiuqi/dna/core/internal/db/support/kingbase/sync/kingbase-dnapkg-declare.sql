create or replace package dna as
function isleapyear(ts timestamp) returns number;
function isleapmonth(ts timestamp) returns number;
function isleapday(ts timestamp) returns number;
function collate_gbk(s character(4000)) returns bytea;
end;