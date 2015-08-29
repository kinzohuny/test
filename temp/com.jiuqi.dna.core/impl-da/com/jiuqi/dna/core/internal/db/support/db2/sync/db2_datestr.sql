create function dna_datestr(@time timestamp,@fmt varchar(50))
returns varchar(50)
specific dna_datestr
language sql
deterministic
no external action
begin atomic
  declare @year varchar(4);
  declare @month varchar(2);
  declare @day varchar(2);
  declare @dayinweek varchar(2);
  declare @dayinyear varchar(3);
  declare @hour varchar(2);
  declare @minutes varchar(2);
  declare @seconds varchar(2);
  declare @quarter varchar(2);
  declare @wkofyear varchar(2);
  declare @wkofmonth varchar(2);
  declare @result varchar(50);
  set @result=@fmt;
  set @year=char(year(@time));

  if(month(@time)<10)
  then set @month=cast(month(@time) as char);
  else set @month=char(month(@time));
  end if;
  if(length(@month)=1)
  then set @month=concat('0',@month);
  end if;
  
  
  if(day(@time)<10)
  then set @day=cast(day(@time) as char);
  else set @day=char(day(@time));
  end if;
  if(length(@day)=1)
  then set @day=concat('0',@day);
  end if;
  
  
  if(hour(@time)<10)
  then set @hour=cast(hour(@time) as char);
  else set @hour=char(hour(@time));
  end if;
  if(length(@hour)=1)
  then set @hour=concat('0',@hour);
  end if;
  
  if(minute(@time)<10)
  then set @minutes=cast(minute(@time) as char);
  else set @minutes=char(minute(@time));
  end if;
  if(length(@minutes)=1)
  then set @minutes=concat('0',@minutes);
  end if;
  
  if(second(@time)<10)
  then set @seconds=cast(second(@time) as char);
  else set @seconds=char(second(@time));
  end if;
  if(length(@seconds)=1)
  then set @seconds=concat('0',@seconds);
  end if;
  
  set @dayinweek=cast(dayofweek(@time) as char);
  
  if(dayofyear(@time)<10)
  then set @dayinyear=cast(dayofyear(@time) as char);
  elseif(dayofyear(@time)<100)
  then set @dayinyear=cast(dayofyear(@time) as char(2));
  else set @dayinyear=char(dayofyear(@time));
  end if;
  if(length(@dayinyear)=1)
  then set @dayinyear=concat('00',@dayinyear);
  elseif(length(@dayinyear)=2)
  then set @dayinyear=concat('0',@dayinyear);
  end if;
  
  
  if(@month in('01','02','03'))
  then set @quarter='1';
  elseif(@month in ('04','05','06'))
  then set @quarter='2';
  elseif(@month in('07','08','09'))
  then set @quarter='3';
  else set @quarter='4';
  end if;


  if(week(@time)<10)
  then set @wkofyear=cast(week(@time) as char);
  else set @wkofyear=char(week(@time));
  end if;
  if(length(@wkofyear)=1)
  then set @wkofyear=concat('0',@wkofyear);
  end if;
  
  set @wkofmonth=cast((int(@day)+dayofweek(@time-int(@day) day)-1)/7+1 as char);
  
  set @result=replace(@result,'yyyy',@year);
  set @result=replace(@result,'yy',substr(@year,3,2));
  set @result=replace(@result,'mm',@month);
  set @result=replace(@result,'ddd',@dayinyear);
  set @result=replace(@result,'dd',@day);
  set @result=replace(@result,'d',@dayinweek);
  set @result=replace(@result,'q',@quarter);
  set @result=replace(@result,'ww',@wkofyear);
  set @result=replace(@result,'w',@wkofmonth);
  set @result=replace(@result,'hh24',@hour);
  
  if(posstr(@result,'hh12')=0 and posstr(@result,'hh')!=0)
  then set @result=replace(@result,'hh','hh12');
  end if;
  if(posstr(@result,'hh12')!=0 and int(@hour)>12)
  then if(int(@hour)<22) 
  then set @result=replace(@result,'hh12',concat('0',cast(int(@hour)-12 as char)));
  else set @result=replace(@result,'hh12',char(int(@hour)-12));
  end if;
  if(posstr(@result,'am')!=0)
  then set @result=replace(@result,'am','PM');
  end if;
  elseif(posstr(@result,'hh12')!=0 and int(@hour)=12)
  then set @result=replace(@result,'hh12',@hour);
  if(posstr(@result,'am')!=0)
  then set @result=replace(@result,'am','PM');
  end if;
  elseif(posstr(@result,'hh12')!=0 and int(@hour)<12 and int(@hour)!=0)
  then set @result=replace(@result,'hh12',@hour);
  if(posstr(@result,'am')!=0)
  then set @result=replace(@result,'am','AM');
  end if;
  elseif(posstr(@result,'hh12')!=0 and int(@hour)<12 and int(@hour)=0)
  then set @result=replace(@result,'hh12','12');
  if(posstr(@result,'am')!=0)
  then set @result=replace(@result,'am','AM');
  end if;
  end if;
  
  set @result=replace(@result,'mi',@minutes);
  set @result=replace(@result,'ss',@seconds);
  return @result;
end