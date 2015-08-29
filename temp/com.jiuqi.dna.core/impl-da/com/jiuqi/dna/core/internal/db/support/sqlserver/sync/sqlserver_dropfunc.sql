if object_id('dna.isleapyear','FN') is not null
	drop function dna.isleapyear;
	
if object_id('dna.isleapmonth','FN') is not null
	drop function dna.isleapmonth;

if object_id('dna.isleapday','FN') is not null
	drop function dna.isleapday;

if object_id('dna.truncyear','FN') is not null
	drop function dna.truncyear;

if object_id('dna.truncquarter','FN') is not null
	drop function dna.truncquarter;

if object_id('dna.truncmonth','FN') is not null
	drop function dna.truncmonth;

if object_id('dna.truncweek','FN') is not null
	drop function dna.truncweek;

if object_id('dna.truncday','FN') is not null
	drop function dna.truncday;
	
if object_id('dna.current_time_millis','FN') is not null
	drop function dna.current_time_millis;

if object_id('dna.lpad','FN') is not null
	drop function dna.lpad;

if object_id('dna.rpad','FN') is not null
	drop function dna.rpad;

if object_id('dna.length','FN') is not null
	drop function dna.length;
	
if object_id('dna.datestr','FN') is not null
    drop function dna.datestr;

if object_id('dna.hexstr','FN') is not null
    drop function dna.hexstr;