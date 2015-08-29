//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";



package com.jiuqi.dna.core.impl;



//#line 1 "dnasql.y"

import java.io.Reader;

import com.jiuqi.dna.core.da.SQLFuncSpec;
import com.jiuqi.dna.core.def.DNASqlType;
import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.def.query.GroupByType;
import com.jiuqi.dna.core.def.query.UserFunctionDefine;
import com.jiuqi.dna.core.spi.sql.*;
//#line 27 "SQLParser.java"




public class SQLParser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//## **user defined:Object
String   yytext;//user variable to return contextual strings
Object yyval; //used to return semantic vals from action routines
Object yylval;//the 'lval' (result) I got from yylex()
Object valstk[] = new Object[YYSTACKSIZE];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
final void val_init()
{
  yyval=new Object();
  yylval=new Object();
  valptr=-1;
}
final void val_push(Object val)
{
  try {
    valptr++;
    valstk[valptr]=val;
  }
  catch (ArrayIndexOutOfBoundsException e) {
    int oldsize = valstk.length;
    int newsize = oldsize*2;
    Object[] newstack = new Object[newsize];
    System.arraycopy(valstk,0,newstack,0,oldsize);
    valstk = newstack;
    valstk[valptr]=val;
  }
}
final Object val_pop()
{
  return valstk[valptr--];
}
final void val_drop(int cnt)
{
  valptr -= cnt;
}
final Object val_peek(int relative)
{
  return valstk[valptr-relative];
}
final Object dup_yyval(Object val)
{
  return val;
}
//#### end semantic value section ####
public final static short ID=257;
public final static short VAR_REF=258;
public final static short ENV_REF=259;
public final static short INT_VAL=260;
public final static short LONG_VAL=261;
public final static short DOUBLE_VAL=262;
public final static short STR_VAL=263;
public final static short LE=264;
public final static short GE=265;
public final static short NE=266;
public final static short CB=267;
public final static short IF=268;
public final static short COMMENT=269;
public final static short WHITESPACE=270;
public final static short IFX=271;
public final static short ELSE=272;
public final static short DEFINE=273;
public final static short INOUT=274;
public final static short OUT=275;
public final static short NOT=276;
public final static short NULL=277;
public final static short DEFAULT=278;
public final static short BOOLEAN=279;
public final static short BYTE=280;
public final static short BYTES=281;
public final static short DATE=282;
public final static short DOUBLE=283;
public final static short ENUM=284;
public final static short FLOAT=285;
public final static short GUID=286;
public final static short INT=287;
public final static short LONG=288;
public final static short SHORT=289;
public final static short STRING=290;
public final static short RECORDSET=291;
public final static short TRUE=292;
public final static short FALSE=293;
public final static short OR=294;
public final static short AND=295;
public final static short BETWEEN=296;
public final static short LIKE=297;
public final static short ESCAPE=298;
public final static short STARTS_WITH=299;
public final static short ENDS_WITH=300;
public final static short CONTAINS=301;
public final static short IN=302;
public final static short IS=303;
public final static short EXISTS=304;
public final static short USING=305;
public final static short RELATIVE=306;
public final static short RANGE=307;
public final static short LEAF=308;
public final static short CHILDOF=309;
public final static short PARENTOF=310;
public final static short ANCESTOROF=311;
public final static short DESCENDANTOF=312;
public final static short AVG=313;
public final static short MIN=314;
public final static short MAX=315;
public final static short COUNT=316;
public final static short SUM=317;
public final static short H_LV=318;
public final static short H_AID=319;
public final static short REL=320;
public final static short ABO=321;
public final static short COALESCE=322;
public final static short CASE=323;
public final static short END=324;
public final static short WHEN=325;
public final static short THEN=326;
public final static short QUERY=327;
public final static short BEGIN=328;
public final static short WITH=329;
public final static short UNION=330;
public final static short ALL=331;
public final static short AS=332;
public final static short SELECT=333;
public final static short FROM=334;
public final static short WHERE=335;
public final static short CURRENT=336;
public final static short OF=337;
public final static short GROUP=338;
public final static short BY=339;
public final static short ROLLUP=340;
public final static short HAVING=341;
public final static short ORDER=342;
public final static short ASC=343;
public final static short DESC=344;
public final static short OVER=345;
public final static short ROW_NUMBER=346;
public final static short RANK=347;
public final static short DENSE_RANK=348;
public final static short PARTITION=349;
public final static short ROWS=350;
public final static short UNBOUNDED=351;
public final static short PRECEDING=352;
public final static short ROW=353;
public final static short FOLLOWING=354;
public final static short JOIN=355;
public final static short ON=356;
public final static short RELATE=357;
public final static short FOR=358;
public final static short UPDATE=359;
public final static short LEFT=360;
public final static short RIGHT=361;
public final static short FULL=362;
public final static short DISTINCT=363;
public final static short ORM=364;
public final static short MAPPING=365;
public final static short OVERRIDE=366;
public final static short RETURNING=367;
public final static short INTO=368;
public final static short INSERT=369;
public final static short VALUES=370;
public final static short SET=371;
public final static short DELETE=372;
public final static short TABLE=373;
public final static short ABSTRACT=374;
public final static short EXTEND=375;
public final static short FIELDS=376;
public final static short INDEXES=377;
public final static short RELATIONS=378;
public final static short HIERARCHIES=379;
public final static short VAVLE=380;
public final static short MAXCOUNT=381;
public final static short PRIMARY=382;
public final static short KEY=383;
public final static short BINARY=384;
public final static short VARBINARY=385;
public final static short BLOB=386;
public final static short CHAR=387;
public final static short VARCHAR=388;
public final static short NCHAR=389;
public final static short NVARCHAR=390;
public final static short TEXT=391;
public final static short NTEXT=392;
public final static short NUMERIC=393;
public final static short RELATION=394;
public final static short TO=395;
public final static short UNIQUE=396;
public final static short MAXLEVEL=397;
public final static short PROCEDURE=398;
public final static short VAR=399;
public final static short WHILE=400;
public final static short LOOP=401;
public final static short FOREACH=402;
public final static short BREAK=403;
public final static short PRINT=404;
public final static short RETURN=405;
public final static short FUNCTION=406;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,    1,    1,    1,    1,    1,    1,    1,
   10,   11,   11,   11,   11,   11,   11,   11,   11,   11,
   11,   11,   11,   11,   11,   11,   13,   13,   13,   14,
   14,   14,   14,   12,   12,   12,   12,   12,   12,   12,
   12,   12,   12,   12,   12,   12,   12,   12,   12,   17,
   17,   17,   18,   18,   19,   19,   20,   15,   15,   15,
   15,   15,   15,   15,   15,   15,   15,   15,   15,   21,
   21,   21,   22,   22,   22,   23,   23,   23,   24,   24,
   24,   24,   24,   24,   24,   24,   24,   24,   24,   24,
   26,   26,   26,   26,   26,   26,   27,   27,   27,   27,
   28,   28,   36,   36,   36,   29,   29,   37,   37,   37,
   30,   30,   38,   38,   38,   38,   39,   39,   39,   31,
   31,   35,   35,   32,   32,   32,   32,   33,   33,   33,
   33,   33,   33,   33,   33,   33,   33,   33,   33,   41,
   41,   41,   41,   34,   34,   34,   34,   34,   34,   34,
   25,   25,   25,   25,   25,   25,   25,   42,   42,   42,
   42,   42,   42,   43,   43,   43,   45,   45,   44,   44,
   44,   44,   44,   44,   44,   44,   44,   44,   44,   44,
   44,   44,   46,   46,   46,   46,   46,   46,   46,   46,
   46,   46,   46,   46,   46,   46,   52,   52,   52,   47,
   47,   47,   47,   48,   48,   48,   48,   48,   48,   48,
   48,   48,   48,   48,   48,   48,   48,   48,   48,   48,
   48,   49,   49,   49,   49,   50,   50,   50,   50,   55,
   55,   57,   57,   57,   57,   56,   56,   56,   51,   51,
   58,   58,   59,   59,   59,   54,   54,   54,   60,   60,
    2,    2,    2,    2,    2,    2,    2,    2,   61,   61,
   61,   61,   63,   63,   63,   63,   63,   63,   63,   66,
   66,   66,   65,   65,   67,   67,   40,   62,   62,   62,
   73,   73,   73,   73,   68,   68,   69,   69,   75,   75,
   75,   70,   70,   70,   70,   70,   70,   71,   71,   71,
   71,   71,   71,   77,   77,   77,   72,   72,   72,   64,
   64,   78,   78,   78,   79,   79,   80,   80,   80,   80,
   80,   80,   74,   74,   74,   81,   81,   81,   81,   81,
   81,   82,   82,   82,   82,   82,   82,   82,   82,   82,
   82,   82,   82,   82,   82,   82,   82,   82,   82,   82,
   82,   82,   82,   82,   82,   82,   83,   83,   83,   83,
   85,   85,   85,   84,   84,   86,   86,   87,   87,   88,
   88,   88,   88,   88,   88,   89,   89,   89,   89,   89,
   89,   76,   76,   76,   76,   76,   76,   76,   76,   76,
   76,   76,   91,   91,   91,   91,   91,   91,   91,   91,
   91,   91,   90,   90,   90,   90,   53,   53,   53,    3,
    3,    3,    3,    3,    3,    3,    3,    3,    3,    3,
    3,    3,   16,   92,   92,   92,   93,   93,   93,   93,
   93,    4,    4,    4,    4,    4,    4,    4,    4,   94,
   94,   94,   94,   95,   95,   95,   97,   97,   96,   96,
   96,   96,   96,   96,   99,   99,   99,   98,   98,   98,
    5,    5,    5,    5,    5,    5,    5,    5,  100,  100,
  101,  101,  102,  102,  103,  103,  103,  104,  104,  104,
    6,    6,    6,    6,    6,    6,    6,    6,  105,  106,
  106,  106,    7,    7,    7,    7,    7,    7,    7,    7,
    7,  107,  107,  107,  108,  108,  109,  109,  115,  115,
  116,  116,  116,  110,  110,  110,  111,  111,  111,  112,
  112,  112,  113,  113,  113,  113,  113,  113,  113,  113,
  113,  114,  114,  114,  121,  121,  121,  121,  121,  123,
  123,  124,  124,  124,  122,  122,  122,  122,  122,  122,
  122,  122,  122,  122,  122,  122,  122,  122,  122,  122,
  122,  122,  122,  122,  122,  122,  122,  122,  122,  122,
  122,  122,  122,  122,  122,  122,  122,  122,  122,  122,
  122,  122,  122,  122,  122,  125,  125,  125,  125,  125,
  125,  117,  117,  117,  126,  126,  126,  126,  126,  126,
  126,  126,  126,  127,  127,  127,  128,  128,  128,  118,
  118,  118,  129,  129,  129,  129,  129,  119,  119,  119,
  130,  130,  130,  130,  130,  120,  120,  120,    8,    8,
    8,    8,    8,    8,    8,  131,  131,  132,  132,  132,
  132,  132,  132,  132,  132,  132,  132,  132,  132,  132,
  132,  132,  132,  132,  132,  132,  132,  132,  132,  132,
  142,  142,  142,  133,  133,  133,  133,  133,  143,  143,
  143,  143,  143,  143,  143,  143,  143,  143,  143,  143,
  143,  143,  143,  134,  134,  134,  134,  134,  134,  134,
  134,  134,  134,  144,  144,  144,  144,  135,  135,  135,
  135,  135,  135,  136,  136,  136,  136,  137,  137,  138,
  138,  138,  138,  138,  138,  138,  138,  138,  139,  140,
  140,  141,  141,    9,    9,    9,    9,    9,    9,    9,
    9,
};
final static short yylen[] = {                            2,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    2,    4,    4,    3,    3,    2,    5,    5,    4,    4,
    3,    3,    4,    2,    3,    3,    2,    2,    1,    2,
    3,    2,    1,    1,    1,    1,    1,    1,    4,    1,
    1,    1,    1,    1,    1,    1,    4,    3,    2,    3,
    1,    3,    1,    0,    3,    3,    1,    1,    1,    1,
    1,    1,    1,    2,    2,    2,    2,    2,    2,    3,
    1,    3,    3,    1,    3,    2,    1,    2,    3,    3,
    1,    1,    1,    1,    1,    1,    1,    1,    3,    3,
    1,    1,    1,    1,    1,    1,    6,    6,    5,    4,
    5,    4,    2,    0,    2,    4,    4,    1,    1,    1,
    4,    4,    3,    3,    3,    2,    3,    1,    3,    4,
    3,    1,    0,    4,    4,    3,    2,    5,    7,    7,
    5,    7,    7,    5,    4,    3,    5,    4,    3,    1,
    1,    1,    1,    9,   11,   11,    9,    8,    7,    6,
    3,    3,    3,    1,    3,    3,    3,    3,    3,    3,
    1,    3,    3,    2,    1,    2,    3,    1,    1,    1,
    1,    1,    3,    3,    1,    1,    1,    1,    1,    1,
    3,    2,    1,    5,    4,    4,    5,    4,    5,    4,
    4,    4,    3,    2,    2,    2,    5,    5,    2,    4,
    3,    4,    3,    6,    6,    8,    8,    6,    5,    4,
    3,    2,    8,    8,    7,    7,    6,    5,    4,    3,
    2,    4,    4,    3,    2,    5,    5,    3,    2,    2,
    1,    4,    4,    3,    2,    2,    0,    2,    4,    4,
    2,    1,    4,    4,    3,    3,    1,    3,    4,    3,
    9,   10,    9,    8,    7,    5,    4,    3,    4,    2,
    3,    2,    3,    4,    3,    4,    1,    3,    3,    3,
    3,    2,    1,    1,    5,    2,    2,    3,    1,    3,
    5,    5,    4,    3,    3,    2,    2,    2,    3,    1,
    3,    2,    4,    0,    2,    4,    3,    3,    5,    0,
    5,    3,    2,    3,    1,    3,    2,    0,    2,    1,
    0,    3,    3,    2,    3,    1,    1,    2,    2,    1,
    2,    2,    3,    1,    3,    1,    3,    3,    3,    3,
    2,    7,    8,    8,    8,    6,    4,    3,    8,    6,
    4,    3,    2,    1,    8,    6,    4,    3,    2,    1,
    8,    6,    4,    3,    2,    1,    3,    0,    3,    2,
    3,    1,    3,    1,    0,    2,    5,    1,    1,    2,
    2,    2,    2,    2,    2,    2,    2,    2,    2,    2,
    2,    6,    6,    8,    1,    6,    5,    4,    8,    6,
    5,    4,    3,    5,    1,    3,    5,    3,    3,    5,
    3,    5,    1,    1,    1,    0,    1,    1,    0,   11,
   13,   11,   10,    9,    8,   10,    9,    8,    7,    5,
    4,    3,    1,    3,    1,    3,    4,    0,    4,    3,
    2,    9,   10,    9,    8,    7,    5,    4,    3,    3,
    3,    3,    2,    3,    3,    2,    3,    3,    7,    7,
    6,    5,    4,    3,    3,    1,    3,    3,    1,    3,
    9,   10,    9,    8,    7,    5,    4,    3,    4,    2,
    2,    2,    2,    2,    3,    1,    3,    3,    3,    2,
    9,   10,    9,    8,    7,    5,    4,    3,    3,    3,
    3,    2,   12,   13,    6,    4,    3,    7,    5,    4,
    3,    2,    0,    2,    2,    2,    1,    0,    2,    1,
    4,    3,    2,    2,    0,    2,    2,    0,    2,    2,
    0,    2,    8,    0,    8,    7,    6,    5,    4,    3,
    2,    3,    1,    3,    4,    6,    5,    6,    2,    1,
    0,    4,    5,    0,    1,    1,    1,    1,    1,    1,
    1,    1,    4,    4,    1,    4,    4,    4,    4,    1,
    1,    6,    4,    3,    2,    4,    3,    2,    4,    3,
    2,    4,    3,    2,    4,    3,    2,    4,    3,    2,
    6,    5,    4,    3,    2,    6,    6,    5,    4,    3,
    2,    3,    1,    3,    4,    5,    4,    3,    2,    5,
    4,    3,    2,    3,    1,    3,    2,    2,    2,    3,
    1,    3,    5,    5,    4,    3,    2,    3,    1,    3,
    5,    5,    4,    3,    2,    3,    1,    3,    9,    9,
    8,    7,    5,    4,    3,    2,    1,    2,    2,    2,
    1,    1,    2,    2,    1,    1,    1,    1,    2,    2,
    2,    1,    2,    2,    2,    2,    2,    2,    2,    2,
    3,    3,    2,    3,    5,    5,    3,    2,    1,    1,
    1,    1,    1,    4,    1,    1,    1,    1,    1,    1,
    4,    3,    2,    3,    7,    3,    5,    3,    7,    6,
    5,    3,    3,    3,    1,    3,    2,    4,    6,    6,
    4,    3,    2,    4,    4,    3,    2,    2,    2,    8,
    6,    8,    7,    6,    5,    4,    3,    2,    1,    2,
    2,    1,    2,   10,   10,    9,    8,    7,    5,    4,
    3,
};
final static short yydefred[] = {                         0,
    0,    0,    1,    2,    3,    4,    5,    6,    7,    8,
    9,   10,   11,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  258,    0,  468,    0,  422,    0,  439,    0,
  488,    0,  497,    0,  501,    0,  635,    0,  731,    0,
  257,    0,  467,    0,  421,    0,  438,    0,  487,    0,
  496,    0,    0,  500,    0,  634,    0,  730,    0,  256,
    0,    0,    0,   51,    0,    0,  466,    0,  420,    0,
  437,    0,  486,    0,  504,  502,    0,  499,    0,  633,
    0,  729,    0,   24,   34,   35,   36,   37,   38,    0,
   40,   41,   42,   43,   44,   45,   46,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  495,    0,    0,    0,
    0,    0,   49,    0,   59,   60,   61,   58,    0,   29,
    0,    0,    0,    0,   62,   63,    0,    0,   33,   25,
    0,   26,    0,   52,   50,  255,    0,  465,    0,  419,
    0,    0,  436,    0,  485,    0,  506,    0,    0,  533,
    0,    0,    0,  510,  498,    0,  632,    0,  728,    0,
   48,  425,    0,    0,   28,   27,   32,    0,   30,   69,
   66,   67,   64,   68,   65,   12,   13,    0,    0,   23,
  254,    0,    0,    0,    0,    0,    0,    0,  267,    0,
  464,    0,    0,    0,  415,    0,  418,    0,  435,    0,
    0,    0,  484,    0,    0,    0,  539,  545,  546,  547,
  548,  549,  550,  551,  552,    0,    0,  555,    0,    0,
    0,    0,  560,  561,    0,    0,    0,  513,    0,    0,
    0,  509,    0,  631,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  719,    0,    0,  641,  642,    0,    0,
    0,    0,  637,    0,    0,  645,  646,  647,  648,    0,
    0,    0,  652,    0,  727,    0,   47,   39,    0,   31,
   17,   18,  272,    0,    0,  262,    0,    0,  279,  286,
  407,  408,    0,    0,  253,  251,    0,    0,    0,  310,
    0,  276,    0,    0,  472,   57,    0,    0,    0,  385,
  463,  461,    0,  470,    0,    0,  414,    0,  417,    0,
  446,    0,  434,  432,    0,  443,    0,    0,    0,  492,
    0,  483,  481,    0,    0,    0,  565,    0,  568,    0,
  571,    0,  574,    0,  577,    0,  580,    0,  585,    0,
  540,    0,  534,  532,  512,    0,  516,    0,    0,    0,
  593,    0,    0,    0,    0,  703,    0,  168,    0,  172,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  170,  169,    0,    0,   74,   77,    0,   81,
   82,   83,   84,   85,   86,   87,   88,    0,  161,  165,
    0,  175,  176,  177,  178,  179,  180,  183,  695,    0,
  663,    0,  668,    0,  707,    0,  709,  708,  718,    0,
  721,    0,    0,    0,    0,  653,  638,  654,  639,  655,
  640,  630,  629,  636,  656,  643,  657,  644,  658,  649,
  659,  650,  660,  651,  697,    0,    0,  726,    0,  426,
  424,  271,  270,  277,    0,  261,    0,    0,    0,    0,
    0,    0,    0,    0,  324,    0,  268,    0,  263,  273,
  274,  252,  314,    0,  269,    0,  265,  288,    0,    0,
    0,    0,    0,    0,    0,    0,  403,  404,  405,    0,
  462,  474,    0,    0,  476,    0,  413,    0,  416,    0,
  445,  444,  433,  442,  459,    0,    0,    0,  440,  441,
  491,    0,  482,  295,    0,    0,  489,  564,    0,  567,
    0,  570,    0,  573,    0,  576,    0,  579,    0,  584,
    0,    0,    0,    0,  599,    0,  603,    0,    0,  519,
    0,    0,  611,    0,    0,    0,  688,    0,    0,  686,
    0,    0,    0,  140,  141,  142,  143,    0,   78,   76,
  166,  164,  182,    0,    0,    0,    0,  127,    0,  194,
    0,  195,    0,  196,    0,    0,  199,    0,  212,    0,
  221,    0,  225,    0,  229,    0,    0,    0,  242,  702,
    0,    0,    0,   94,   93,   96,    0,  122,    0,   92,
   91,   95,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  692,  662,  661,  667,  669,  670,  671,  672,  673,
    0,  675,  676,  677,  678,  679,  680,    0,  706,    0,
  717,    0,    0,  696,  694,  693,  725,  724,  284,    0,
  280,  278,    0,    0,    0,    0,    0,    0,    0,  331,
    0,  264,  313,    0,    0,    0,  316,  266,    0,    0,
    0,    0,    0,  398,  401,    0,  399,  393,    0,    0,
  480,    0,    0,  469,  412,  410,    0,    0,  448,  447,
  454,    0,    0,  431,    0,  297,    0,  563,  553,  566,
  554,  569,  556,  572,  557,  575,  558,  578,  559,  583,
    0,    0,    0,    0,  537,  598,    0,    0,  605,  602,
    0,  594,  592,  617,    0,    0,  522,    0,    0,  619,
    0,    0,    0,    0,    0,   56,   55,  203,  201,    0,
    0,  139,    0,  136,    0,   89,   79,  181,  173,  174,
  126,    0,    0,    0,    0,  193,    0,    0,    0,  211,
    0,  220,    0,  224,    0,    0,  228,    0,    0,  231,
    0,    0,  241,   72,    0,  701,    0,   75,   73,  157,
    0,  156,    0,  121,    0,  155,    0,   90,    0,    0,
    0,  108,  109,  110,    0,    0,  162,  158,  163,  159,
  160,  167,    0,  683,    0,    0,  705,  704,  716,    0,
    0,    0,  283,    0,    0,    0,    0,  329,  327,  338,
    0,  325,  323,  330,  328,  321,  322,  318,  319,    0,
  291,    0,  303,    0,  275,    0,    0,    0,  388,    0,
  392,    0,  479,    0,  477,  475,  309,    0,    0,  460,
  458,  453,    0,  430,    0,  296,  293,  582,    0,    0,
    0,  538,  536,  591,    0,  609,  607,  608,  597,    0,
  595,  601,    0,  616,    0,  612,  610,  625,    0,    0,
  531,    0,  493,    0,    0,  202,    0,  200,  138,    0,
  135,    0,  125,  124,    0,  190,  185,  191,  186,  192,
  188,    0,    0,  210,    0,  219,    0,  223,  222,  245,
    0,  235,    0,    0,  230,  238,    0,  240,  239,    0,
  120,  100,    0,  102,    0,  112,    0,  111,  107,    0,
  691,    0,  687,  682,    0,  666,    0,    0,  715,    0,
    0,  282,  281,    0,    0,    0,  337,    0,    0,  315,
  302,    0,    0,  402,  397,  400,  394,  387,    0,  391,
    0,  411,  452,    0,  429,  427,  581,  562,    0,  542,
  590,    0,  606,  604,  600,  596,  615,    0,  624,    0,
  620,  618,  530,  627,    0,  494,  248,    0,  137,  131,
  134,    0,    0,  189,  184,  187,  198,  197,  209,    0,
  218,    0,  244,    0,  234,    0,  227,  226,  700,  699,
   99,    0,    0,  101,  116,    0,    0,    0,    0,    0,
  681,  674,  250,    0,  714,    0,  711,    0,    0,    0,
  360,    0,    0,    0,    0,  386,    0,  390,    0,  451,
    0,    0,  543,  589,    0,  614,    0,  623,    0,  529,
    0,    0,    0,    0,  150,    0,  208,  204,  217,  205,
    0,    0,  233,    0,   98,    0,  105,    0,  115,    0,
  113,  114,  689,  685,  249,  713,    0,  340,    0,  346,
    0,  352,    0,  359,    0,    0,  336,  369,  368,    0,
  364,    0,  306,    0,  301,  299,    0,  450,    0,  449,
  588,    0,  622,  621,  628,  626,  528,    0,  132,    0,
  133,    0,  149,    0,  215,    0,  216,    0,  119,    0,
  712,  710,    0,    0,    0,    0,  332,    0,    0,    0,
    0,  366,  389,  384,  457,    0,  587,  586,  527,    0,
  148,    0,  213,  206,  214,  207,  339,  333,  345,  334,
  351,  335,  363,    0,    0,  374,  371,  373,  370,  375,
  372,  526,    0,  147,    0,    0,  525,  523,    0,    0,
    0,    0,  367,  146,    0,  380,  377,  379,  376,  381,
  378,
};
final static short yydgoto[] = {                          2,
    3,  247,    5,    6,    7,    8,  248,   10,   11,   12,
   64,   98,  127,  128,  373,  163,   65,   66,  374,  298,
  555,  376,  377,  378,  379,  595,  380,  381,  382,  383,
  384,  385,  386,  387,  596,  994,  776,  908,  997,  185,
  548,  388,  389,  390,  391,  392,  393,  394,  395,  396,
  397,  398,  283,  721,  749,  752,  750,  578,  579,  792,
  186,  278,  275,  289,  459,  188,  189,  190,  294,  326,
  651,  668,  279,  454,  469,  474,  933,  290,  646,  647,
  455,  456,  929, 1070, 1066, 1071, 1072, 1112, 1153,  480,
  300,  164,  499,  249,  202,  318,  319,  497, 1022,  250,
  194,  306,  484,  485,  251,  206,   53,  109,  152,  231,
  353,  535,  712,  149,  153,  154,  350,  532,  709,  965,
  150,  226,  342,  523,  695,  351,  698,  699,  533,  710,
  252,  253,  254,  255,  256,  257,  258,  259,  260,  261,
  262,  263,  618,  264,
};
final static short yysindex[] = {                      -104,
 1545,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  830, 1341, 1395, 1417, 1429, 1460, -160,
 1495, 1504,    0,   28,    0,   48,    0,   51,    0,  140,
    0,  170,    0, -163,    0, 1514,    0,  192,    0,  206,
    0, 1221,    0, 1286,    0, 1306,    0, 1310,    0, 1330,
    0, 1526, -182,    0, -126,    0, 1370,    0, 1374,    0,
 1964,  176,  209,    0,  552,  644,    0,  665,    0,  713,
    0,  729,    0,  810,    0,    0, -174,    0,  293,    0,
  837,    0,  898,    0,    0,    0,    0,    0,    0,  522,
    0,    0,    0,    0,    0,    0,    0, 3215, 2368, 2783,
 1489, -141, -138,  784, -133,  382,    0, 1533,  614, -170,
 1001, 3640,    0, 1536,    0,    0,    0,    0, -106,    0,
  821,  328,  508, 1155,    0,    0, -127, 1540,    0,    0,
 3215,    0, 1540,    0,    0,    0,  141,    0,  757,    0,
 1567, 1603,    0,  195,    0,  -81,    0, 3080, 1054,    0,
  752,  812,  614,    0,    0,  614,    0,  208,    0, 1045,
    0,    0,  517, 1191,    0,    0,    0, 1401,    0,    0,
    0,    0,    0,    0,    0,    0,    0, -127, 1540,    0,
    0,    4,  418, -140,  890,  395,  924,  941,    0,  859,
    0,   -3,  427, -145,    0, 1052,    0, 1081,    0,  336,
  469,  431,    0,  906,  509,  942,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  437,  440,    0,  483,  512,
  563,  576,    0,    0,  585, 1540, 1621,    0, 1634, -114,
  932,    0,  812,    0, 1291, 3138, 1166, 1133,  229, 1070,
 3279,  381, 1357,    0, 3816, 5363,    0,    0,   43,  690,
  797,  -16,    0,  829,  840,    0,    0,    0,    0,  850,
  895,  899,    0,   13,    0,  407,    0,    0, 1645,    0,
    0,    0,    0,  -13,  924,    0,   49,   57,    0,    0,
    0,    0, 2463,  189,    0,    0, 1048,  330,    0,    0,
  223,    0,   45,  942,    0,    0,   58, -200, 1297,    0,
    0,    0, 1126,    0, 1656,  942,    0,  163,    0, -142,
    0, 1683,    0,    0, 1156,    0,   -6, 1043, 1043,    0,
  402,    0,    0, 1170, 2583, 1043,    0, 1101,    0, 1114,
    0, 1129,    0, 1231,    0, 1309,    0, 1360,    0, 1367,
    0, 1210,    0,    0,    0, 1245,    0,  604, 1688, 1467,
    0, 1692, 1141,  932, 2768,    0,  706,    0, 3655,    0,
 5406, 2651,  608,  637,  663,  671, 1491,  673,  680,  691,
  692, 3065,    0,    0, 1163, 1268,    0,    0, 1402,    0,
    0,    0,    0,    0,    0,    0,    0, 1623,    0,    0,
 1576,    0,    0,    0,    0,    0,    0,    0,    0,    1,
    0,    6,    0, 1642,    0, -187,    0,    0,    0, -128,
    0, 1021, 2846,   -2,   -2,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0, 1446, 1517,    0,  145,    0,
    0,    0,    0,    0,   24,    0,  704,  924, 1550, 1594,
 1666,  -22, 1368, 1685,    0,  273,    0,   49,    0,    0,
    0,    0,    0, 3829,    0,   49,    0,    0, 1695, 1297,
 1404,  204,   63,  -21, 1699,  779,    0,    0,    0, 1352,
    0,    0,  856, 1702,    0, 1043,    0,  295,    0, 1391,
    0,    0,    0,    0,    0,  160,   31, -115,    0,    0,
    0, 1297,    0,    0, -151, 1481,    0,    0,   11,    0,
  434,    0,  559,    0,  606,    0,  611,    0,  677,    0,
   33, 1737, -222, 1054,    0, 1701,    0,  715,  822,    0,
 -164, 1750,    0, 1703, 1431, 1141,    0, 2889,   -2,    0,
 1706, 3704, -125,    0,    0,    0,    0, 1720,    0,    0,
    0,    0,    0, 2719,  501, 1343,   68,    0,   66,    0,
 -210,    0, 5363,    0, 5363,  -17,    0, -210,    0, 1730,
    0, 1736,    0, 3898,    0, 3748,  233, -146,    0,    0,
 3352,  444, 3401,    0,    0,    0, 3911,    0, 3988,    0,
    0,    0, -119, 4035, 4078, 1544, 4125, 4195, 5363, 1543,
 1741,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  534,    0,    0,    0,    0,    0,    0, 1745,    0,  470,
    0,  543,  717,    0,    0,    0,    0,    0,    0,  603,
    0,    0,    0, 1769, 1771, 1780, 1742,  716, 2420,    0,
 1748,    0,    0,  -11,   18, 1798,    0,    0,  545,  936,
 1391,  175, 1506,    0,    0, 1505,    0,    0,  580, 1752,
    0, 4238, 1758,    0,    0,    0, 3450,  924,    0,    0,
    0, 1760, -153,    0,  339,    0, 1454,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
 1379,  905, -175, 1764,    0,    0,  883,  582,    0,    0,
 1768,    0,    0,    0, 1772, 1775,    0, -209, 1827,    0,
  735, 1512, 1431,    0,  179,    0,    0,    0,    0,   -2,
  635,    0, -132,    0,  775,    0,    0,    0,    0,    0,
    0,  181, 5363,  718,  853,    0,  681, 5363, 5363,    0,
   21,    0,  558,    0,  782, 1164,    0, 4285,  997,    0,
 4328,  988,    0,    0, 1268,    0, 1583,    0,    0,    0,
 1623,    0, 1623,    0, 1592,    0, 1623,    0,   -2, 4375,
 4419,    0,    0,    0,  751, 4462,    0,    0,    0,    0,
    0,    0,  226,    0, 1779, 4530,    0,    0,    0, 1843,
  260, 1484,    0, 1781, 1560, 1563, 1565,    0,    0,    0,
  863,    0,    0,    0,    0,    0,    0,    0,    0, 5449,
    0, 1297,    0, 4573,    0, 1506, 1783,  804,    0,  808,
    0,  568,    0,   -2,    0,    0,    0, 1481, 1564,    0,
    0,    0,  754,    0, 1485,    0,    0,    0,  809, 1401,
 1854,    0,    0,    0, -161,    0,    0,    0,    0, 1786,
    0,    0,  843,    0,  947,    0,    0,    0,  764, 1790,
    0, 1792,    0, 1596,    0,    0, 4620,    0,    0, 1794,
    0,  596,    0,    0, 1011,    0,    0,    0,    0,    0,
    0,    8, 1086,    0, 1799,    0, 1803,    0,    0,    0,
 4663,    0,  612, 1092,    0,    0,   -2,    0,    0,  562,
    0,    0,  649,    0,  -12,    0, 2938,    0,    0,   -2,
    0, 2982,    0,    0,  652,    0,   -2, 4706,    0,  816,
  642,    0,    0, 1876, 1897, 1901,    0,  965,  924,    0,
    0,   -2,   30,    0,    0,    0,    0,    0, 3518,    0,
 1808,    0,    0, 4749,    0,    0,    0,    0, 1905,    0,
    0, 1811,    0,    0,    0,    0,    0, 3586,    0, 1394,
    0,    0,    0,    0,  870,    0,    0,   -2,    0,    0,
    0, 1573, 1823,    0,    0,    0,    0,    0,    0,  817,
    0,  416,    0,   -2,    0, 4792,    0,    0,    0,    0,
    0, 4839, 4882,    0,    0,   -2,  934,   84,    0,  978,
    0,    0,    0,  120,    0, -196,    0,  879,  914,  948,
    0, 4925,  952, 4968, -136,    0, 1481,    0, 1620,    0,
   -2,  995,    0,    0,  572,    0, 1481,    0,  835,    0,
 1825, -180, 5011, 5058,    0,   34,    0,    0,    0,    0,
 5101, 5144,    0,   -2,    0,   -2,    0,   -2,    0, 5187,
    0,    0,    0,    0,    0,    0,  624,    0,  924,    0,
  924,    0,  924,    0,   -2, 1921,    0,    0,    0, 1927,
    0, 2238,    0,   -2,    0,    0, 1830,    0, 5230,    0,
    0, 1835,    0,    0,    0,    0,    0, 1416,    0,   -2,
    0,   -2,    0, 1840,    0, 1091,    0, 1111,    0,   -2,
    0,    0,  836,  845,  861, 5277,    0, 2285, -156, -143,
    5,    0,    0,    0,    0,   -2,    0,    0,    0, -148,
    0,  862,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   -2, 1676,    0,    0,    0,    0,    0,
    0,    0, 1419,    0, 1674, 2352,    0,    0, 5320,  200,
  477,  -26,    0,    0,   -2,    0,    0,    0,    0,    0,
    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0, 1662,    0,    0,    0,    0,    0,    0,
    0, 1953,    0, 1953,    0, 1953,    0, 1953,    0, 1953,
    0,    0,    0,    0, 1662,    0, 1953,    0, 1953,    0,
    0,    0,    0,    0, 1955,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0, 1371,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0, 1179,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0, 1469, 1478,    0,    0,
 1497,    0, 1580,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0, 1157,    0,
    0, -202, 1263,    0,    0, 1179,    0,    0,    0,    0,
    0,    0,    0,  196,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0, 1651, 1657,    0,
    0,    0,    0, 2535,    0,    0,  625,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  -41,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   46,    0,    0,    0,    0,
 1122,    0, -202,    0,   15,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  917,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  359,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  629,    0,
    0,    0,    0,  197,    0,    0,    0,  733,  814,    0,
    0,    0,    0,    0,    0,  -41,    0,    0,    0, 1174,
    0,    0,    0,    0,    0,    0,    0,  531,  531,    0,
    0,    0,    0,    0,    0,  531,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  184,    0,    0,    0,    0,    0,    0,    0, 1168,
    0,    0, -186, 1122,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0, 1188,    0,    0, 1552,    0,
    0,    0,    0,    0,    0,    0,    0,  989,    0,    0,
  881,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  918,  943,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  625,  689,  744,
  885,   14,  598,  916,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  657,  108,
  565,    0,    0, 1400,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   12,    0,  531,    0,    0,    0,  872,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  -32,    0,    0,    0,   25,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   -5, 1175,    0,    0,    0,    0,    0,    0,
    0, 1148,    0,    0, 1682, -186,    0,    0,  944,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0, 1552,    0,    0,    0,    0,
 5496,    0,    0,    0,    0, 5496,    0, 5496,    0,    0,
    0,    0,    0,    0,    0,    0,    0, 1151,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0, 1733,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  945,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  656,  993, 1002, 1050,    0,    0,    0,    0,
    0,    0,    0,  148,  371,  -19,    0,    0,    0,    0,
  257,    0,    0,    0,    0,  938,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0, 1694,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0, -179,    0,
    0,    0, 1682, 3074,    0,    0,    0,    0,    0, 1014,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0, 1151,    0,
    0,    0,    0,    0, 2112,    0,  171,    0,    0,    0,
 1099,    0, 1424,    0,    0,    0, 1532,    0, 1276,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0, 1051, 1055, 1060,    0,    0,    0,
 1670,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  177,    0,    0,    0, 1696,    0,    0,    0,    0,
    0,    0,    0,  -29,    0,    0,    0,  400,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0, 3247,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0, 1152,    0,    0,    0,
    0,    0,    0,    0, 1612,    0,    0,    0,    0, 1640,
    0,    0,    0,    0,    0,    0,  953,    0,    0,    0,
    0,    0,    0, 1098, 1103, 1106,    0,    0,    0,    0,
    0,  627,  664,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0, 1018,    0,    0,
    0, 1728,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0, 1180,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0, 1025,    0,    0,  361,    0,
    0,    0,    0,    0,    0,    0,    0, 1670, 1670, 1670,
    0,    0, 1992,    0,    0,    0, 1373,    0, 2034,    0,
 1080,    0,    0,    0,    0,    0,  -28,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0, 1183,    0, 1778,    0, 1806,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   26, 1711,    0,    0,    0,    0,
    0,    0,    0,  781,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0, 1856,
    0, 1944,    0,    0,    0,    0,    0,    0,    0, 1084,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0, 1089,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   35,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0, 1986,    0,    0,    0,    0,    0,
    0,    0,    0,    0, 2084,    0,    0,    0,    0,    0,
    0,
};
final static short yygindex[] = {                         0,
    0, 2044,    0,    0,    0,    0, 2057,    0,    0,    0,
 1960, 1483, 1248, 1295,  -95, -135,    0, 1761,    0, 1759,
 -231, 1493, 1501, 1717, -245,    0,    0,    0,    0,    0,
    0,    0,    0,    0, 1510,    0,    0,    0,    0, -178,
    0,  985, 1139, 1744,    0,    0,    0,    0,    0,    0,
    0, -270, 1035, -560,    0, 1345, 1349,    0, 1528,    0,
 -300,    0, -130, -266,  806,  911,  955,    0,    0, 1215,
    0, 1458, 1663,    0,    0, -181,    0, -865,    0, 1301,
 1475,    0,  805,    0,    0,    0,    0, 1007,    0,    0,
 1459,    0, -239, 1973,    0,    0,    0,    0,    0, 1982,
    0,    0,    0, 1461, 1976,    0, 2068, 2016, 1975, 1904,
 1788, 1598, 1433, 1812,    0, 1999,    0,    0,    0,    0,
 1932,    0,    0,    0,    0, 1625, 1463, 1312, 1464, 1300,
 -110, -240,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0, 1931,
};
final static int YYTABLESIZE=5819;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                        414,
  415,  408,  129,  274,  375,  196,  187,  488,  444,  406,
  299,  424,  453,  745,  478,  613,  594,  294,  589,  654,
  594,  312,  589,  238,  737,  169,  490,  443,  542,  478,
  594,  129,  589,  182,  541,  129,  297,  452,  535,  312,
  594,  601,  589,  182,  436,  238,  858,  594,  976,  589,
  594,  679,  589,  437,  540,  695,  436,  326,  695, 1056,
  594,  602,  589, 1013,  630,  292,  885,   42,  619,  362,
  473,  673,  270, 1014,  672, 1087,  691, 1094,  361,  500,
  842,  107,  129,  292,  297,  155,  507,   44,  182,  541,
   46,  704,   51,  506,  951,   35,  182,  472,  445, 1136,
  447,  417,  832,  653,  676,  182,  581, 1142,  730,  539,
  304,  470, 1138,  489,  136,  280,  556,  138,  473, 1075,
  281,  515,  143,  869, 1052,  751,  577,  621,  402,   78,
  722,  475,  115,  116,  117,  118,  764,  521,  496,  502,
  674,  347,  348,  675,  520,   77,  515,  448,  290,  165,
  121,  290,  282,  122,  123,  439,  588,  476,  124,  693,
 1055,  424,  521,  867,  125,  126,  290,  623,    1,  520,
  166,  694,  870,  622,  203,  515,  515,  187,  576,   48,
  182,  633,  723,  557,  238,  677,  137,  859,  320,  139,
  281,  320,  325, 1103,  144, 1104, 1137, 1105,  424, 1088,
  670,  108,  182, 1076, 1057,  108,  320,  843, 1139,   50,
  698,   52,   36,  620,  294,  816,  833,  289,  645,  865,
  289,  874,  282,  490,  187,  305,  478,  544,  182, 1160,
  705,   57, 1143,  952,  557,  289,  312,  294,  736,  422,
  587,  235,  442,  472,  587,   59,  664,  238,   52,  494,
  495,  236,  295,  296,  587,  294,  237,  423,  435,  273,
 1140,  603,  182,  235,  587,  912,  678,  473,  238,  326,
  435,  587,  695,  236,  587,  594,  884,  589,  237,  629,
  292,  349,  294,   41,  587,  993,  671,  312,  690, 1093,
  204,  490,  623,  652,  478,  613,  720,  308,  416,  182,
  468,  296,  490,   43,  312,  478,   45,  423,  556,  637,
  312,  239,  446,  281,  296,  308,  284,  734,  535,  735,
  613,  731,  406,  541,  406,  294,  184, 1161,  720,  604,
  312,  806,  807,  239,  490,  473,  184,  478,  477,  478,
  479,  757,  192,  535,  746,  282,  473,  326,  292,  769,
  613, 1000,  200,  284,  292,  204, 1141, 1004, 1015,  715,
  808,  809,  292,  290,  192,  292,  292,  362,  453,  541,
  535,  535,  535,  535,  200,  715,  361,  204,  473,  788,
  732,  184,  240,  241,  242,  243,  244,  245,  246,  184,
  184,  292,  284,  452,  541,   47,  181,  284,  184,  311,
  627,  829,  235,  320,  240,  241,  242,  243,  244,  245,
  246,  317,  236,  284,  317,  669,  824,  237,  487,  690,
  238,  541,  541,  541,  541,   49,  698,  541,  698,  317,
  442,  290,  289,   99,  442,  828,  873,  290,  698,  541,
  307,  297,  290,  698,  457,  290,  238,   56,  290,  290,
  199,  423,  294,  287,  320, 1156, 1040,  277,  307,  273,
  296,   58,  406,  234,  406,  235,  100,  812,  628,  183,
  317,  320,  239,  184,  681,  236,  328,  320,  465,  330,
  237,  911,  913,  238,  401,  303,  235,  875,  747,  284,
  920,  183,  882,  883,  698,  184,  236,  320,  698,  587,
  289,  237,  893,  192,  284,  897,  289,  544,  284,  238,
  284,  289,  308,  200,  289,  919,  204,  289,  289,  458,
  294,  184,  332,  423,  903,  905,  294,  315,  640,  698,
  910,  406,  544,  406,  294,  239,  184,  294,  294,  698,
  917,  727,  698,  240,  241,  242,  243,  244,  245,  246,
  665,  334, 1157,  466,  183,  184,  239,  748,  184,  544,
  544,  544,  544,  200,  645,  544,  192,  324,  932,  698,
  698,  698,  698,  698,  698,  698,  200,  544,  268,  204,
  308,  114,  791,  170,  297,  463,  308,  192,  183,  428,
  171,  311,  184,  785,  834,  101,  841,  200,  308,  683,
  204,  238,  336,  887,  641,  300,  240,  241,  242,  243,
  244,  245,  246,  941,  311,  338,  690, 1082,  666,  297,
  110,  968,  851,  300,  340,  850,  317,  240,  241,  242,
  243,  244,  245,  246,  183,  973,  407,  145,  235,  183,
  183,  183,  183,  526,  183,  984,  685,  559,  236,  915,
  285,  687,  187,  237,  594,  307,  589,  501,  296,  990,
  187,  996,  438,  238,  235,  311,  720,  305,  464,  260,
  305, 1039,  720,  276,  236,  868,  561,  317,  867,  237,
 1007,  238,  301,  311,  102,  305,  316,  260,  311,  680,
  272,  594,  327,  589,  317,  329,  259,  287, 1021,  756,
  317,  235,  563,  312,  298,  103,  835, 1017,  239,  146,
  565,  236,  568, 1002,  259,  287,  237,  689,  286,  570,
  317,  881,  298,  307,  313,  787, 1027,  235,  998,  307,
  572,  574, 1158,  274,  239, 1041, 1042,  236,  331,  192,
 1044,  307,  237,  277,  949,  542, 1046, 1048,  419,  200,
  302,  541,  204,  104,  701,  801,  726,  729,  877,  594,
  594,  589,  589,  172,  322,  192, 1065,  333, 1074,  105,
  173,  239,  267,  395,  862,  200,  395,  113,  204,  240,
  241,  242,  243,  244,  245,  246,  428, 1090, 1092,  784,
  907,  395,  314,  944,  581, 1096, 1098,  239,  789,  790,
  811,  296,  192,  960, 1100,  240,  241,  242,  243,  244,
  245,  246,  200,  886,  682,  204, 1102,  989,  335,  235,
  300,  304,  889,  940,  304,  867, 1111, 1081,  192,  236,
 1159,  337,  323, 1116,  237,  819,  296,  849,  200,  304,
  339,  204,  240,  241,  242,  243,  244,  245,  246,  948,
  106,  971,  972,  183,  428,  421, 1006, 1038,  793,  525,
 1134,  684, 1111,  558,  183,  168,  686,  985,  240,  241,
  242,  243,  244,  245,  246, 1084, 1128,  111,  587, 1101,
  311,  235,  305,  956,  260, 1130,  850,  426,  300,  239,
  866,  236,  560,  879,  300,  594,  237,  589,  428,  235,
 1152, 1132, 1145, 1155,  991,  300,  300, 1001,  430,  236,
 1032,  259,  287, 1031,  237,  587,  662,  171,  562,  298,
  192,  171,  171,  171,  171,  171,  564,  171,  567,  183,
  200,  183,  688,  204,  794,  569,  880,  986,  112,  171,
  171,  171,  171,  992,  344,  418,  571,  573,  311,  840,
  305,  239,  260,  432,  311,  305,  305,  434,  277,  631,
  240,  241,  242,  243,  244,  245,  246,  305,  305,  239,
  700,  800,  728,  876, 1051,  722,  720, 1050,  396,  259,
  287,  396,  192,  587,  587,  277,  287,  298,  395,  151,
  861,  287,  200,  298,  287,  204,  396,  287,  287,  350,
  192,  723,  684,  664,  298,  298,  906,  228,  543,  943,
  200,  665,  191,  204,  544,  545,  546,  547, 1054,  959,
  344,  867,  240,  241,  242,  243,  244,  245,  246,  154,
  871,  154,  154,  154,  657, 1080,  304,  888, 1079,  140,
  240,  241,  242,  243,  244,  245,  246,  154,  154,  154,
  154,  975,  420,  594,  247,  589,  395,  247,  246,  936,
  542,  246,  395,  938,  947,  118,  541,  395,  118,  471,
  395, 1005, 1037,  395,  395,  350,  167,  702,  348,  872,
  115,  116,  117,  118,  425,   23,   24,  395,  395,  395,
 1083, 1127,  395,  395,  395,  427,  467,  227,  955,  395,
 1129,  122,  123,  395,  304,  429,  124,  229,  878,  304,
  304,  661,  125,  126,  292,  192, 1131, 1144,  927,  587,
  456,  304,  304,  456,  117, 1030,  978,  117,  594,  455,
  589, 1124,  455,  594, 1058,  589,  171,  658,  846,  153,
  356,  153,  153,  153,  171,  171,  171,  171,  141,  142,
  431, 1126,  171,  594,  433,  589,  171,  153,  153,  153,
  153,  320,  937,  939,  115,  116,  117,  118,  406, 1060,
  406,  285,  722,  720,  171,  171,  171,  171,  171,  171,
  171,  171,  171,  171,  471,  122,  123,  171,  230, 1049,
  124,  813,  293,  396,  460,  308,  125,  126,  723,  684,
  664,  460,  957, 1062,  171,  171,  171, 1067,  665,  171,
  171,  928,  171,  308,  171,  171,  356,  349,  171,  284,
 1011,  171,  171,  171,  171,  847,  848,  928,   71,  171,
  171,   71,  171, 1053,  171,  171,  269,  171,  461,  321,
  171,  171,  171,  898,  154,  461,   71,  171,  343,  285,
 1078,  171,  154,  154,  154,  154,  157,  349, 1068,  171,
  154,  396,  928,  642,  154,  288,  974,  396,  751,  247,
  291,  648,  396,  246,  814,  396,  325,  587,  396,  396,
  118,  171,  154,  154,  154,  154,  154,  154,  154,  154,
  154,  154,  396,  396,  396,  154,  928,  396,  396,  396,
  265, 1069,  958, 1012,  396,  355,  342,  307,  396,  352,
  348,  899,  154,  154,  154,  354,   80,  154,  154,   80,
  154,  748,  154,  154,  343,  403,  154,  404,  158,  154,
  154,  154,  154,  349,   80,  456,  309,  154,  154,  117,
  154,  977,  154,  154,  455,  154, 1123,  987,  154,  154,
  154,  355,  587,  341,  153,  154,  508,  587,  347,  154,
  509,  353,  153,  153,  153,  153, 1125,  154,  460,  510,
  153,  462,  266,  511,  153,  177,  460,  587,  178,  308,
  180,  355,  342,  729,  512,  594,  348,  589,  513,  154,
  399,  354,  153,  153,  153,  153,  153,  153,  153,  153,
  153,  153,  590,  592,  591,  153,  237,  236,  310,  498,
  174,   16,  461,  382,   16,  988,  382,  175,  580,  890,
  461,  176,  153,  153,  153,  179,  272,  153,  153,  341,
  153,  382,  153,  153,  347,  243,  153,  353,  232,  153,
  153,  153,  153,   71,  594,  518,  589,  153,  153,  481,
  153,  243,  153,  153,  232,  153,  581,  581,  153,  153,
  153,  590,  592,  591,  152,  153,  152,  152,  152,  153,
  518,  517,  271,  341,  237,  236,   60,  153,   61,  493,
  505,   71,  152,  152,  152,  152,  514,  522,  582,  891,
  515,  514,   14,  503,   62,   63,  517,  294,  511,  153,
  518,  148,  508,  243,  243,  505,  232,  232,  471,   15,
  529,   71,   15,   71,  294,  294,  514,   71,   14,  534,
  486,   14,   71,  511,  490,   71,  517,  508,   71,   71,
  566,   80,  505,  505,  505,  505,   71,   21,   19,   20,
   21,   67,   71,   61,   71,  514,  514,   71,   71,   71,
  511,  511,  511,  511,   71,  508,  508,  508,   71,   62,
   63,   69,  583,   61,  516,   71,   71,   61,  517,   80,
   80,  761,  151,  763,  151,  151,  151,  626,  767,   62,
   63,  131,  133,   62,   63,   73,  507,   61,   71,  634,
  151,  151,  151,  151,  160,  733,   25,   26,  728,   80,
  738,   80,  739,   62,   63,   80,  584,  585,  586,  587,
   80,  507,  409,   80,  410,  518,   80,   80,  588,  519,
   22,  600,  520,   22,   80,   80,  521,   61,  382,   82,
   80,   61,   80,  635,  838,   80,   80,   80,  839,  507,
  507,  507,   80,   62,   63,  593,   80,   62,   63, 1028,
   27,   28,  104, 1029,   80,  104,  477,  478,  479,  599,
  115,  116,  117,  118,  597,  584,  585,  586,  587,  598,
  104, 1119,   29,   30, 1147, 1120,   80,  588, 1148,  152,
  106,  122,  123,  106,   31,   32,  124,  152,  152,  152,
  152,   20,  125,  126,   20,  152,  382,   19,  106,  152,
   19,  624,  382,  625,  593,  636,  659,  382,  660,  836,
  382,  837,  638,  382,  382,   33,   34,  152,  152,  152,
  152,  152,  152,  152,  152,  152,  152,  382,  639,  382,
  152,  667,  382,  382,  382,  778,  780,  781,  649,  382,
  945,  650,  946,  382,  134,  663,   61,  152,  152,  152,
   37,   38,  152,  152,  406,  152,  406,  152,  152,   39,
   40,  152,   62,   63,  152,  152,  152,  152,  128,   54,
   55,  128,  152,  152,  581,  152,  692,  152,  152,  711,
  152,   75,   76,  152,  152,  152,  128,  151,  147,  148,
  152,  161,  162,  706,  152,  151,  151,  151,  151,  782,
   13,  783,  152,  151,   68,  786,   70,  151,   72,  795,
   74,  796, 1059, 1061, 1063,  119,  120,   81,   97,   83,
  797,   97,  195,  162,  152,  151,  151,  151,  151,  151,
  151,  151,  151,  151,  151,  863,   97,  817,  151,  770,
  771,  810,  772,  773,  774,  775,  103,  123,  123,  103,
  123,  123,  123,  123,  900,  151,  151,  151,  197,  198,
  151,  151,  818,  151,  103,  151,  151,  104,  901,  151,
  860,   14,  151,  151,  151,  151,  343,  148, 1033, 1034,
  151,  151,  918,  151,  921,  151,  151,  942,  151,  345,
  346,  151,  151,  151,  950,  106,  129,  605,  151,  129,
  440,  441,  151,   15,  924,  104,  104,  925,   16,  926,
  151,  482,  483,   17,  129, 1008,   18,   19,   20,  966,
  606,  607,  608,  609,  610,  611,  612,  613,  614,  615,
  616,  617,  151,  106,  106,  104, 1009,  104,  491,  296,
 1010,  104,   21,  527,  528, 1023,  104,  530,  531,  104,
   22, 1077,  104,  104,  655,  656,  696,  697,  707,  708,
  104,  716,  717,  106, 1106,  106,  104, 1107,  104,  106,
 1146,  104,  104,  104,  106,  724,  725,  106,  104, 1149,
  106,  106,  104,  128,  130,  740,  741,  130,  106,  503,
  104,  742,  743,   54,  106,   53,  106,  798,  799,  106,
  106,  106,  130,  804,  805,  524,  106,  821,  822,  123,
  106,  358,  104,  825,  483,  830,  831,  311,  106,  844,
  845,  128,  128,  852,  697,  270,  144,  854,  855,  144,
  856,  531,  365,   97,  914,  162,  922,  923,  934,  935,
  106,  953,  697,    4,  144,  961,  708,  963,  964,  969,
  970,  128,  357,  128,  979,  980,    9,  128,  981,  982,
  135,  103,  128, 1018, 1019,  128, 1024, 1025,  128,  128,
  492,   97,   97,  755,  383,  550,  128,  383, 1035, 1036,
 1085, 1086,  128,  759,  128, 1113, 1114,  128,  128,  128,
 1117, 1118,  383,  894,  128, 1121, 1122,  895,  128,  103,
  103,   97,  765,   97,  552,  753,  128,   97,  815,  632,
  930,  129,   97,  803, 1135,   97,  201,  820,   97,   97,
  193,  205,   79,  826,  145,  156,   97,  145,  128,  103,
  233,  103,   97,  713,   97,  103,  354,   97,   97,   97,
  103,  536,  145,  103,   97,  864,  103,  103,   97,  129,
  129,  232,   70,  703,  103,   70,   97,  524,  344,  962,
  103,  954,  103,  853,    0,  103,  103,  103,  400,  857,
   70,    0,  103,    0,    0,    0,  103,    0,   97,  129,
    0,  129,    0,    0,  103,  129,    0,    0,    0,    0,
  129,    0,    0,  129,    0,    0,  129,  129,    0,  130,
    0,    0,    0,    0,  129,    0,  103,    0,    0,    0,
  129,    0,  129,    0,    0,  129,  129,  129,    0,   84,
    0,    0,  129,    0,    0,    0,  129,    0,    0,    0,
    0,    0,    0,    0,  129,    0,    0,  130,  130,    0,
    0,  144,   85,   86,   87,   88,   89,   90,   91,   92,
   93,   94,   95,   96,   97,    0,  129,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  130,    0,  130,
    0,    0,    0,  130,    0,    0,    0,  413,  130,  144,
  144,  130,  361,    0,  130,  130,    0,    0,    0,  383,
    0,    0,  130,    0,    0,    0,    0,    0,  130,    0,
  130,    0,    0,  130,  130,  130,    0,    0,    0,  144,
  130,  144,    0,    0,  130,  144,    0,    0,    0,    0,
  144,    0,  130,  144,  413,    0,  144,  144,    0,  361,
    0,    0,    0,    0,  144,    0,    0,    0,    0,  145,
  144,    0,  144,    0,  130,  144,  144,  144,    0,    0,
    0,    0,  144,    0,    0,    0,  144,  383,    0,    0,
    0,    0,    0,  383,  144,    0,    0,   70,  383,    0,
    0,  383,    0,    0,  383,  383,    0,  145,  145,    0,
    0,    0,    0,    0,    0,    0,  144,    0,  383,    0,
  383,  413,    0,  383,  383,  383,  361,    0,    0,    0,
  383,    0,    0,    0,  383,   70,    0,  145,    0,  145,
    0,    0,    0,  145,    0,    0,    0,    0,  145,    0,
    0,  145,    0,    0,  145,  145,    0,    0,    0,    0,
    0,    0,  145,    0,    0,   70,    0,   70,  145,    0,
  145,   70,    0,  145,  145,  145,   70,    0,    0,   70,
  145,    0,   70,   70,  145,    0,    0,    0,    0,  413,
   70,    0,  145,    0,  361,    0,   70,    0,   70,    0,
    0,   70,   70,   70,    0,    0,    0,    0,   70,    0,
    0,    0,   70,    0,  145,    0,    0,    0,    0,    0,
   70,    0,    0,    0,  412,  358,    0,  115,  116,  117,
  118,    0,  413,    0,    0,    0,    0,  361,    0,    0,
    0,    0,   70,    0,  360,    0,    0,    0,  122,  123,
    0,    0,    0,  124,    0,    0,    0,    0,    0,  125,
  126,    0,    0, 1108,    0,    0,    0,    0,    0,    0,
    0,  412,  358,    0,  115,  116,  117,  118,    0,    0,
  364,  365,  366,  367,  368,  369,  370,    0,    0,  371,
  372,  360,    0,    0,    0,  122,  123,    0,    0,    0,
  124,    0,    0, 1109,  409,    0,  125,  126,    0,  409,
    0,    0,    0,    0,    0,    0,    0,    0, 1110,    0,
    0,    0,    0,    0,    0,    0,    0,  364,  365,  366,
  367,  368,  369,  370,    0,    0,  371,  372,  412,  358,
    0,  115,  116,  117,  118,    0,    0,    0,    0,    0,
 1109,    0,  362,  130,    0,    0,    0,  361,  360,    0,
    0,    0,  122,  123,    0, 1110,    0,  124,    0,    0,
    0,    0,    0,  125,  126,    0,   85,   86,   87,   88,
   89,   90,   91,   92,   93,   94,   95,   96,   97,    0,
    0,    0,    0,    0,  364,  365,  366,  367,  368,  369,
  370,    0,    0,  371,  372,  802,  412,  358,    0,  115,
  116,  117,  118,    0,    0,    0,    0, 1150,    0,    0,
  554,    0,    0,    0,    0,  361,  360,    0,    0,    0,
  122,  123, 1151,    0,    0,  124,    0,    0,    0,    0,
    0,  125,  126,    0,    0,    0,    0,    0,    0,  412,
  358,    0,  115,  116,  117,  118,    0,    0,    0,    0,
    0,    0,  364,  365,  366,  367,  368,  369,  370,  360,
    0,  371,  372,  122,  123,    0,    0,    0,  124,    0,
    0,    0,    0,    0,  125,  126,    0,    0,  554,    0,
    0,    0,    0,  361,    0,  449,  450,  451,    0,    0,
    0,    0,    0,    0,    0,  364,  365,  366,  367,  368,
  369,  370,    0,    0,  371,  372,    0,    0,    0,    0,
    0,  409,  409,    0,  409,  409,  409,  409,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  538,  449,  450,
  451,  409,  361,    0,    0,  409,  409,    0,    0,    0,
  409,    0,    0,    0,    0,    0,  409,  409,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  504,  357,
  358,    0,  115,  116,  117,  118,    0,  409,  409,  409,
  409,  409,  409,  409,    0,    0,  409,  409,  359,  360,
    0,    0,    0,  122,  123,    0,    0,    0,  124,    0,
    0,    0,    0,    0,  125,  126,    0,    0,    0,    0,
  409,  409,  409,    0,    0,  538,  363,    0,    0,    0,
  361,    0,    0,    0,    0,  364,  365,  366,  367,  368,
  369,  370,    0,    0,  371,  372,  553,  357,  358,    0,
  115,  116,  117,  118,    0,    0,    0,    0,  505,    0,
    0,    0,    0,    0,    0,    0,  359,  360,  538,    0,
    0,  122,  123,  361,    0,    0,  124,    0,    0,    0,
    0,    0,  125,  126,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  363,    0,    0,    0,    0,    0,
    0,    0,    0,  364,  365,  366,  367,  368,  369,  370,
    0,    0,  371,  372,  714,  357,  358,  538,  115,  116,
  117,  118,  361,  184,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  359,  360,    0,    0,    0,  122,
  123,    0,    0,    0,  124,    0,    0,    0,    0,    0,
  125,  126,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  538,  363,  537,  412,  358,  361,  115,  116,  117,
  118,  364,  365,  366,  367,  368,  369,  370,  132,    0,
  371,  372,    0,    0,  360,    0,    0,    0,  122,  123,
    0,  184,    0,  124,    0,    0,    0,    0,    0,  125,
  126,   85,   86,   87,   88,   89,   90,   91,   92,   93,
   94,   95,   96,   97,    0,    0,    0,    0,    0,    0,
  364,  365,  366,  367,  368,  369,  370,    0,    0,  371,
  372,    0,    0,    0,    0,    0,  183,    0,    0,    0,
  184,  553,  412,  358,  413,  115,  116,  117,  118,  361,
  182,    0,    0,    0,  182,  182,  182,  182,  182,    0,
  182,    0,  360,    0,    0,    0,  122,  123,    0,    0,
    0,  124,  182,  182,  182,  182,    0,  125,  126,    0,
    0,    0,    0,    0,  714,  412,  358,    0,  115,  116,
  117,  118,    0,    0,    0,    0,    0,    0,  364,  365,
  366,  367,  368,  369,  370,  360,    0,  371,  372,  122,
  123,    0,    0,    0,  124,    0,    0,  362,  184,    0,
  125,  126,  361,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  995,  412,  358,    0,  115,  116,  117,
  118,  364,  365,  366,  367,  368,  369,  370,    0,    0,
  371,  372,    0,    0,  360,    0,    0,    0,  122,  123,
    0,  184,    0,  124,    0,    0,    0,    0,    0,  125,
  126,    0,    0,    0,    0,    0,    0,  999,  412,  358,
    0,  115,  116,  117,  118,    0,    0,    0,    0,    0,
  364,  365,  366,  367,  368,  369,  370,    0,  360,  371,
  372,    0,  122,  123,    0,    0,    0,  124,    0,    0,
  184,    0,    0,  125,  126,    0,    0,    0,    0,    0,
    0,    0,    0,  174,    0,    0,    0,  174,  174,  174,
  174,  174,    0,  174,  364,  365,  366,  367,  368,  369,
  370,    0,    0,  371,  372,  174,  174,  174,  174,    0,
    0,    0,    0,    0,  184,    0,    0,    0,  362,    0,
  575,  412,  358,  361,  115,  116,  117,  118,    0,  182,
    0,    0,    0,    0,    0,  207,    0,  182,  182,  182,
  182,  360,    0,    0,    0,  122,  123,    0,    0,  182,
  124,    0,    0,    0,    0,    0,  125,  126,  208,    0,
    0,  209,  210,    0,  211,  212,  213,  214,  215,  182,
  182,    0,  182,  182,  182,  182,  182,  364,  365,  366,
  367,  368,  369,  370,    0,    0,  371,  372,    0,  576,
    0,  362,    0,  356,  357,  358,  361,  115,  116,  117,
  118,    0,    0,  272,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  359,  360,    0,    0,    0,  122,  123,
    0,    0,    0,  124,    0,    0,    0,    0,    0,  125,
  126,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  362,  363,    0,    0,    0,  361,    0,    0,    0,    0,
  364,  365,  366,  367,  368,  369,  370,    0,    0,  371,
  372,    0,    0,  216,  217,  218,  219,  220,  221,  222,
  223,  224,  225,    0,  115,  116,  117,  118,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  362,
  119,  120,  121,    0,  361,  122,  123,    0,    0,    0,
  124,    0,  174,    0,    0,    0,  125,  126,    0,    0,
  174,  174,  174,  174,    0,    0,    0,    0,    0,    0,
    0,    0,  174,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  405,  357,  358,    0,  115,  116,
  117,  118,  174,  174,    0,  174,  174,  174,  174,  174,
    0,    0,    0,    0,  359,  360,    0,  362,    0,  122,
  123,    0,  361,    0,  124,    0,    0,    0,    0,    0,
  125,  126,    0,    0,    0,    0,  270,    0,    0,    0,
    0,    0,  363,    0,    0,    0,    0,    0,    0,    0,
    0,  364,  365,  366,  367,  368,  369,  370,    0,    0,
  371,  372,    0,    0,    0,    0,    0,  754,  357,  358,
    0,  115,  116,  117,  118,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  362,    0,  359,  360,    0,
  361,    0,  122,  123,    0,    0,    0,  124,    0,    0,
    0,    0,    0,  125,  126,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  363,  758,  357,  358,    0,
  115,  116,  117,  118,  364,  365,  366,  367,  368,  369,
  370,    0,    0,  371,  372,    0,  359,  360,    0,    0,
    0,  122,  123,    0,    0,    0,  124,    0,    0,    0,
    0,    0,  125,  126,  362,    0,    0,    0,    0,  361,
    0,    0,    0,    0,  363,  827,  357,  358,    0,  115,
  116,  117,  118,  364,  365,  366,  367,  368,  369,  370,
    0,    0,  371,  372,    0,  359,  360,    0,    0,    0,
  122,  123,    0,    0,    0,  124,    0,    0,    0,    0,
    0,  125,  126,  413,  719,    0,    0,    0,  361,    0,
    0,    0,    0,  363,    0,    0,    0,    0,    0,    0,
    0,    0,  364,  365,  366,  367,  368,  369,  370,    0,
    0,  371,  372, 1016,  357,  358,    0,  115,  116,  117,
  118,    0,    0,    0,    0,    0,    0,  362,    0,    0,
    0,    0,  361,  359,  360,    0,    0,    0,  122,  123,
    0,    0,    0,  124,    0,    0,    0,    0,    0,  125,
  126,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  363,    0,    0,    0,    0,    0,    0,    0,    0,
  364,  365,  366,  367,  368,  369,  370,    0,    0,  371,
  372, 1026,  357,  358,    0,  115,  116,  117,  118,    0,
    0,    0,    0,    0,    0,  413,    0,    0,    0,    0,
  361,  359,  360,    0,    0,    0,  122,  123,  413,    0,
    0,  124,    0,  361,    0,    0,    0,  125,  126,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  363,
    0,    0,    0,    0,    0,  159,    0,    0,  364,  365,
  366,  367,  368,  369,  370,    0,    0,  371,  372,    0,
  549,  357,  358,    0,  115,  116,  117,  118,   85,   86,
   87,   88,   89,   90,   91,   92,   93,   94,   95,   96,
   97,  360,    0,    0,    0,  122,  123,  413,    0,    0,
  124,    0,  361,    0,    0,    0,  125,  126,    0,    0,
  413,    0,    0,    0,    0,  361,    0,    0,  363,  718,
  412,  358,    0,  115,  116,  117,  118,  364,  365,  366,
  367,  368,  369,  370,    0,    0,  371,  372,    0,    0,
  360,    0,    0,    0,  122,  123,    0,    0,    0,  124,
    0,    0,    0,    0,    0,  125,  126,    0,    0,    0,
    0,    0,    0,    0,  357,  358,    0,  115,  116,  117,
  118,    0,    0,    0,    0,    0,  364,  365,  366,  367,
  368,  369,  370,  359,  360,  371,  372,  413,  122,  123,
    0,    0,  361,  124,    0,    0,    0,    0,    0,  125,
  126,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  363,    0,    0,    0,    0,    0,    0,    0,    0,
  364,  365,  366,  367,  368,  369,  370,    0,    0,  371,
  372,  411,  412,  358,  413,  115,  116,  117,  118,  361,
    0,    0,    0,    0,  643,  644,  358,    0,  115,  116,
  117,  118,  360,    0,    0,    0,  122,  123,    0,    0,
    0,  124,    0,    0,    0,  360,    0,  125,  126,  122,
  123,    0,    0,    0,  124,    0,    0,  413,    0,    0,
  125,  126,  361,    0,    0,    0,    0,    0,  364,  365,
  366,  367,  368,  369,  370,    0,    0,  371,  372,    0,
    0,  364,  365,  366,  367,  368,  369,  370,    0,    0,
  371,  372,    0,  744,  412,  358,    0,  115,  116,  117,
  118,    0,    0,    0,  413,    0,  760,  412,  358,  361,
  115,  116,  117,  118,  360,    0,    0,    0,  122,  123,
    0,    0,    0,  124,    0,    0,    0,  360,    0,  125,
  126,  122,  123,    0,    0,    0,  124,    0,    0,    0,
    0,    0,  125,  126,    0,    0,    0,    0,    0,    0,
  364,  365,  366,  367,  368,  369,  370,    0,    0,  371,
  372,    0,    0,  364,  365,  366,  367,  368,  369,  370,
    0,    0,  371,  372,  413,    0,    0,    0,    0,  361,
    0,    0,    0,  762,  412,  358,    0,  115,  116,  117,
  118,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  360,    0,    0,    0,  122,  123,
    0,    0,    0,  124,    0,    0,    0,  413,    0,  125,
  126,    0,  361,    0,    0,    0,    0,    0,    0,    0,
  766,  412,  358,    0,  115,  116,  117,  118,    0,    0,
  364,  365,  366,  367,  368,  369,  370,    0,    0,  371,
  372,  360,    0,    0,    0,  122,  123,    0,    0,    0,
  124,    0,    0,    0,  413,    0,  125,  126,    0,  361,
    0,    0,    0,  768,  412,  358,    0,  115,  116,  117,
  118,    0,    0,    0,    0,    0,    0,  364,  365,  366,
  367,  368,  369,  370,  360,    0,  371,  372,  122,  123,
    0,    0,    0,  124,    0,    0,    0,  413,    0,  125,
  126,    0,  361,    0,    0,    0,    0,    0,    0,    0,
  777,  412,  358,    0,  115,  116,  117,  118,    0,    0,
  364,  365,  366,  367,  368,  369,  370,    0,    0,  371,
  372,  360,    0,    0,    0,  122,  123,    0,    0,    0,
  124,    0,    0,    0,  413,    0,  125,  126,    0,  361,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  364,  365,  366,
  367,  368,  369,  370,    0,    0,  371,  372,    0,    0,
  779,  412,  358,    0,  115,  116,  117,  118,  413,    0,
    0,    0,    0,  361,    0,    0,    0,    0,    0,    0,
    0,  360,    0,    0,    0,  122,  123,    0,    0,    0,
  124,    0,    0,    0,    0,    0,  125,  126,    0,    0,
    0,    0,    0,  823,  412,  358,    0,  115,  116,  117,
  118,  413,    0,    0,    0,    0,  361,  364,  365,  366,
  367,  368,  369,  370,  360,    0,  371,  372,  122,  123,
    0,    0,    0,  124,    0,    0,    0,    0,    0,  125,
  126,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  892,  412,  358,    0,  115,  116,  117,  118,    0,    0,
  364,  365,  366,  367,  368,  369,  370,    0,    0,  371,
  372,  360,    0,    0,    0,  122,  123,    0,    0,  413,
  124,    0,    0,    0,  361,    0,  125,  126,    0,    0,
    0,    0,    0,  896,  412,  358,    0,  115,  116,  117,
  118,    0,    0,    0,    0,    0,    0,  364,  365,  366,
  367,  368,  369,  370,  360,    0,  371,  372,  122,  123,
    0,    0,  413,  124,    0,    0,    0,  361,    0,  125,
  126,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  902,  412,  358,    0,  115,  116,  117,  118,    0,    0,
  364,  365,  366,  367,  368,  369,  370,    0,    0,  371,
  372,  360,    0,    0,    0,  122,  123,    0,    0,  413,
  124,    0,    0,    0,  361,    0,  125,  126,    0,    0,
    0,    0,    0,    0,  904,  412,  358,    0,  115,  116,
  117,  118,    0,    0,    0,    0,    0,  364,  365,  366,
  367,  368,  369,  370,    0,  360,  371,  372,    0,  122,
  123,    0,  413,    0,  124,    0,    0,  361,    0,    0,
  125,  126,    0,    0,    0,    0,    0,  909,  412,  358,
    0,  115,  116,  117,  118,    0,    0,    0,    0,    0,
    0,  364,  365,  366,  367,  368,  369,  370,  360,    0,
  371,  372,  122,  123,    0,  413, 1003,  124,    0,    0,
  361,    0,    0,  125,  126,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  364,  365,  366,  367,  368,  369,
  370,    0,    0,  371,  372,  916,  412,  358,  413,  115,
  116,  117,  118,  361,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  360,    0,    0,    0,
  122,  123,    0,    0,    0,  124,    0,    0,    0,    0,
    0,  125,  126,    0,    0,    0,    0,    0,  931,  412,
  358,  413,  115,  116,  117,  118,  361,    0,    0,    0,
    0,    0,  364,  365,  366,  367,  368,  369,  370,  360,
    0,  371,  372,  122,  123,    0,    0,    0,  124,    0,
    0,    0,    0,    0,  125,  126,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  967,  412,  358,  413,  115,
  116,  117,  118,  361,    0,  364,  365,  366,  367,  368,
  369,  370,    0,    0,  371,  372,  360,    0,    0,    0,
  122,  123,    0,    0,    0,  124,    0,    0,    0,    0,
    0,  125,  126,    0,    0,    0,    0,    0,  983,  412,
  358,  413,  115,  116,  117,  118,  361,    0,    0,    0,
    0,    0,  364,  365,  366,  367,  368,  369,  370,  360,
    0,  371,  372,  122,  123,    0,    0,    0,  124,    0,
    0,    0,    0,    0,  125,  126,    0,    0,    0,    0,
    0,    0,  412,  358,  413,  115,  116,  117,  118,  361,
    0,    0,    0,    0,    0,  364,  365,  366,  367,  368,
  369,  370,  360,    0,  371,  372,  122,  123,    0,    0,
    0,  124,    0,    0,    0,    0,    0,  125,  126,    0,
    0,    0,    0,    0, 1020,  412,  358,  413,  115,  116,
  117,  118,  361,    0,    0,    0,    0,    0,  364,  365,
  366,  367,  368,  369,  370,  360,    0,  371,  372,  122,
  123,    0,    0,    0,  124,    0,    0,    0,    0,    0,
  125,  126,    0,    0,    0,    0,    0, 1043,  412,  358,
  413,  115,  116,  117,  118,  361,    0,    0,    0,    0,
    0,  364,  365,  366,  367,  368,  369,  370,  360,    0,
  371,  372,  122,  123,    0,    0,    0,  124,    0,    0,
    0,    0,    0,  125,  126,    0,    0,    0,    0,    0,
    0,    0,    0,    0, 1045,  412,  358,  413,  115,  116,
  117,  118,  361,    0,  364,  365,  366,  367,  368,  369,
  370,    0,    0,  371,  372,  360,    0,    0,    0,  122,
  123,    0,    0,    0,  124,    0,    0,    0,    0,    0,
  125,  126,    0,    0,    0,    0,    0, 1047,  412,  358,
  413,  115,  116,  117,  118,  361,    0,    0,    0,    0,
    0,  364,  365,  366,  367,  368,  369,  370,  360,    0,
  371,  372,  122,  123,    0,    0,    0,  124,    0,    0,
    0,    0,    0,  125,  126,    0,    0,    0,    0,    0,
 1064,  412,  358,  413,  115,  116,  117,  118,  361,    0,
    0,    0,    0,    0,  364,  365,  366,  367,  368,  369,
  370,  360,    0,  371,  372,  122,  123,    0,    0,    0,
  124,    0,    0,    0,    0,    0,  125,  126,    0,    0,
    0,    0,    0, 1073,  412,  358,  413,  115,  116,  117,
  118,  361,    0,    0,    0,    0,    0,  364,  365,  366,
  367,  368,  369,  370,  360,    0,  371,  372,  122,  123,
    0,    0,    0,  124,    0,    0,    0,    0,    0,  125,
  126,    0,    0,    0,    0,    0, 1089,  412,  358,  413,
  115,  116,  117,  118,  361,    0,    0,    0,    0,    0,
  364,  365,  366,  367,  368,  369,  370,  360,    0,  371,
  372,  122,  123,    0,    0,    0,  124,    0,    0,    0,
    0,    0,  125,  126,    0,    0,    0,    0,    0,    0,
    0,    0,    0, 1091,  412,  358,  413,  115,  116,  117,
  118,  361,    0,  364,  365,  366,  367,  368,  369,  370,
    0,    0,  371,  372,  360,    0,    0,    0,  122,  123,
    0,    0,    0,  124,    0,    0,    0,    0,    0,  125,
  126,    0,    0,    0,    0,    0, 1095,  412,  358,  413,
  115,  116,  117,  118,  361,    0,    0,    0,    0,    0,
  364,  365,  366,  367,  368,  369,  370,  360,    0,  371,
  372,  122,  123,    0,    0,    0,  124,    0,    0,    0,
    0,    0,  125,  126,    0,    0,    0,    0,    0, 1097,
  412,  358,  413,  115,  116,  117,  118,  361,    0,    0,
    0,    0,    0,  364,  365,  366,  367,  368,  369,  370,
  360,    0,  371,  372,  122,  123,    0,    0,    0,  124,
    0,    0,    0,    0,    0,  125,  126,    0,    0,    0,
    0,    0, 1099,  412,  358,  413,  115,  116,  117,  118,
    0,    0,    0,    0,    0,    0,  364,  365,  366,  367,
  368,  369,  370,  360,    0,  371,  372,  122,  123,    0,
    0,    0,  124,    0,    0,    0,    0,    0,  125,  126,
    0,    0,    0,    0,    0, 1115,  412,  358,  413,  115,
  116,  117,  118,  361,    0,    0,    0,    0,    0,  364,
  365,  366,  367,  368,  369,  370,  360,    0,  371,  372,
  122,  123,    0,    0,    0,  124,    0,    0,    0,    0,
    0,  125,  126,    0,    0,    0,    0,    0,    0,    0,
    0,    0, 1133,  412,  358,  409,  115,  116,  117,  118,
  409,    0,  364,  365,  366,  367,  368,  369,  370,    0,
    0,  371,  372,  360,    0,    0,    0,  122,  123,    0,
    0,    0,  124,    0,    0,    0,    0,    0,  125,  126,
    0,    0,    0,    0,    0, 1154,  412,  358,    0,  115,
  116,  117,  118,    0,    0,    0,    0,    0,    0,  364,
  365,  366,  367,  368,  369,  370,  360,    0,  371,  372,
  122,  123,    0,    0,    0,  124,    0,    0,    0,    0,
    0,  125,  126,    0,    0,    0,    0,    0,    0,  412,
  358,    0,  115,  116,  117,  118,    0,    0,    0,    0,
    0,    0,  364,  365,  366,  367,  368,  369,  370,  360,
    0,  371,  372,  122,  123,    0,    0,    0,  124,    0,
    0,    0,    0,    0,  125,  126,    0,    0,    0,    0,
    0,  551,  412,  358,    0,  115,  116,  117,  118,    0,
    0,    0,    0,    0,    0,  364,  365,  366,  367,  368,
  369,  370,  360,    0,  371,  372,  122,  123,    0,    0,
    0,  124,    0,    0,    0,    0,    0,  125,  126,    0,
    0,    0,    0,    0,    0,  644,  358,    0,  115,  116,
  117,  118,    0,    0,    0,    0,    0,    0,  364,  365,
  366,  367,  368,  369,  370,  360,    0,  371,  372,  122,
  123,    0,    0,    0,  124,    0,    0,    0,    0,    0,
  125,  126,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  409,  409,    0,  409,  409,  409,  409,    0,
    0,  364,  365,  366,  367,  368,  369,  370,    0,    0,
  371,  372,  409,    0,    0,    0,  409,  409,    0,    0,
    0,  409,    0,    0,    0,    0,    0,  409,  409,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  409,  409,
  409,  409,  409,  409,  409,    0,    0,  409,  409,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                        245,
  246,  242,   98,  182,  236,  141,  137,  308,  275,  241,
  192,  252,  283,  574,   44,   44,   43,   59,   45,   41,
   43,   41,   45,   40,   42,  121,   59,   41,   40,   59,
   43,  127,   45,   40,   46,  131,   40,  283,   44,   59,
   43,   41,   45,   40,   44,   40,  256,   43,   41,   45,
   43,   41,   45,   41,  355,   41,   44,   44,   44,  256,
   43,   61,   45,  929,   41,   41,   46,   40,  256,   44,
   59,   41,  168,   44,   44,  256,   44,   44,   44,  319,
  256,  256,  178,   59,   40,  256,  326,   40,   40,   44,
   40,  256,  256,  325,  256,  256,   40,   40,  277,  256,
   44,   59,  256,   41,  256,   40,  294,  256,   41,  355,
  256,  293,  256,  256,  256,  256,  362,  256,  297,  256,
  331,  324,  256,  256,   41,  272,  372,  256,  239,  256,
  256,  332,  260,  261,  262,  263,  256,  324,  317,  321,
  256,  256,  257,  259,  324,  328,  349,  278,   41,  256,
  278,   44,  363,  281,  282,  266,  276,  358,  286,  382,
   41,  402,  349,   44,  292,  293,   59,  413,  273,  349,
  277,  394,  305,  302,  256,  378,  379,  308,  325,   40,
   40,  448,  308,  362,   40,  337,  328,  397,   41,  328,
  331,   44,  335, 1059,  328, 1061,  353, 1063,  439,  380,
   41,  376,   40,  340,  401,  376,   59,  383,  352,   40,
   40,  375,  373,  401,  256,   41,  370,   41,  464,   41,
   44,   41,  363,  256,  355,  371,  256,   44,   40,  256,
  395,   40,  381,  395,  413,   59,  256,   41,  256,  256,
  267,  258,  256,   40,  267,   40,  486,   40,  375,  256,
  257,  268,  256,  257,  267,   59,  273,   62,  258,  256,
  256,  256,   40,  258,  267,   40,  256,  256,   40,  256,
  258,  267,  258,  268,  267,   43,  256,   45,  273,  256,
  256,  396,  324,  256,  267,  298,  256,  307,  256,  256,
  372,  324,  538,  472,  324,  324,  542,   41,  256,   40,
  256,  257,  335,  256,  324,  335,  256,  324,  554,  332,
  330,  328,  256,  331,  257,   59,  330,  563,  324,  565,
  349,  256,  355,  278,  357,  367,  333,  354,  574,  324,
  350,  343,  344,  328,  367,  324,  333,  367,  360,  361,
  362,  582,  359,  349,  576,  363,  335,  334,  324,  595,
  379,  912,  369,  330,  330,  372,  352,  918,  329,  538,
  343,  344,  338,  256,  359,  341,  342,  342,  639,  324,
  376,  377,  378,  379,  369,  554,  342,  372,  367,  620,
  559,  333,  399,  400,  401,  402,  403,  404,  405,  333,
  333,  367,  330,  639,  349,  256,  256,  330,  333,   41,
  256,  668,  258,  256,  399,  400,  401,  402,  403,  404,
  405,   41,  268,  330,   44,  256,  662,  273,  256,   59,
   40,  376,  377,  378,  379,  256,  256,  382,  258,   59,
  256,  324,  256,  258,  256,  667,  256,  330,  268,  394,
   41,   40,  335,  273,  256,  338,   40,  256,  341,  342,
  256,  256,  256,   59,  307,  256,   41,   40,   59,  256,
  257,  256,  355,  256,  357,  258,  258,  649,  324,  329,
   40,  324,  328,  333,   41,  268,   40,  330,  256,   40,
  273,  256,  783,   40,  256,   59,  258,  733,  256,  330,
  791,  329,  738,  739,  324,  333,  268,  350,  328,  267,
  324,  273,  748,  359,  330,  751,  330,  324,  330,   40,
  330,  335,  256,  369,  338,  256,  372,  341,  342,  331,
  324,  333,   40,  328,  770,  771,  330,   59,  256,  359,
  776,  355,  349,  357,  338,  328,  333,  341,  342,  369,
  786,   41,  372,  399,  400,  401,  402,  403,  404,  405,
  256,   40,  353,  331,  329,  333,  328,  325,  333,  376,
  377,  378,  379,  369,  810,  382,  359,   59,  814,  399,
  400,  401,  402,  403,  404,  405,  369,  394,   62,  372,
  324,   60,   40,  256,   40,  256,  330,  359,  329,   59,
  263,  256,  333,   60,  256,   44,  692,  369,  342,   41,
  372,   40,   40,   46,  332,   41,  399,  400,  401,  402,
  403,  404,  405,   46,  256,   40,  256,   46,  324,   40,
  328,  867,   41,   59,   40,   44,  256,  399,  400,  401,
  402,  403,  404,  405,   37,   40,  256,  256,  258,   42,
   43,   44,   45,   40,   47,  891,   41,   40,  268,  785,
  256,   41,  783,  273,   43,  256,   45,  256,  257,  900,
  791,  907,  256,   40,  258,   41,  912,   41,  339,   41,
   44,  256,  918,  256,  268,   41,   40,  307,   44,  273,
  921,   40,  256,   59,   41,   59,  256,   59,  330,  256,
  330,   43,  256,   45,  324,  256,   41,   41,  944,  256,
  330,  258,   40,  368,   41,   41,  368,  939,  328,  328,
   40,  268,   40,   62,   59,   59,  273,   41,  324,   40,
  350,   41,   59,  324,  256,  256,  958,  258,  907,  330,
   40,   40,  256,  912,  328,  320,  321,  268,  256,  359,
  986,  342,  273,   40,  840,   40,  992,  993,   59,  369,
  324,   46,  372,   41,   40,   40,  256,   41,   41,   43,
   43,   45,   45,  256,  256,  359, 1012,  256, 1014,   41,
  263,  328,  256,   41,   40,  369,   44,  256,  372,  399,
  400,  401,  402,  403,  404,  405,  256, 1033, 1034,  256,
   40,   59,  324,   40,  294, 1041, 1042,  328,  256,  257,
  256,  257,  359,   40, 1050,  399,  400,  401,  402,  403,
  404,  405,  369,  256,  256,  372, 1057,  256,  256,  258,
  256,   41,   41,  256,   44,   44, 1072,  256,  359,  268,
  354,  256,  324, 1079,  273,  256,  257,  256,  369,   59,
  256,  372,  399,  400,  401,  402,  403,  404,  405,   41,
   41,  256,  257,  256,  324,   59,   41,   41,  256,  256,
 1106,  256, 1108,  256,  267,   45,  256,  256,  399,  400,
  401,  402,  403,  404,  405,   41,   41,   41,  267,  256,
  256,  258,  256,   41,  256,   41,   44,   59,  324,  328,
  256,  268,  256,   41,  330,   43,  273,   45,   59,  258,
 1146,   41,   41, 1149,  256,  341,  342,  256,   59,  268,
   41,  256,  256,   44,  273,  267,   61,   37,  256,  256,
  359,   41,   42,   43,   44,   45,  256,   47,  256,  332,
  369,  334,  256,  372,  332,  256,  256,  326,   41,   59,
   60,   61,   62,  295,  256,  256,  256,  256,  324,   45,
  324,  328,  324,   59,  330,  329,  330,   59,  330,  256,
  399,  400,  401,  402,  403,  404,  405,  341,  342,  328,
  256,  256,  256,  256,   41,   59,   59,   44,   41,  324,
  324,   44,  359,  267,  267,  330,  330,  324,  256,  376,
  256,  335,  369,  330,  338,  372,   59,  341,  342,  256,
  359,   59,   59,   59,  341,  342,  256,  256,  303,  256,
  369,   59,  256,  372,  309,  310,  311,  312,   41,  256,
  332,   44,  399,  400,  401,  402,  403,  404,  405,   41,
  256,   43,   44,   45,  256,   41,  256,  256,   44,  256,
  399,  400,  401,  402,  403,  404,  405,   59,   60,   61,
   62,   41,  256,   43,   41,   45,  324,   44,   41,  256,
   40,   44,  330,  256,  256,   41,   46,  335,   44,  256,
  338,  256,  256,  341,  342,  332,  256,  256,  257,  305,
  260,  261,  262,  263,  256,  256,  257,  355,  356,  357,
  256,  256,  360,  361,  362,  256,  291,   44,  256,  367,
  256,  281,  282,  371,  324,  256,  286,  356,  256,  329,
  330,  256,  292,  293,  256,  359,  256,  256,  256,  267,
   41,  341,  342,   44,   41,  256,   41,   44,   43,   41,
   45,   41,   44,   43,  256,   45,  256,  359,  256,   41,
  256,   43,   44,   45,  264,  265,  266,  267,  365,  366,
  256,   41,  272,   43,  256,   45,  276,   59,   60,   61,
   62,  256,  359,  356,  260,  261,  262,  263,  355,  256,
  357,  256,  256,  256,  294,  295,  296,  297,  298,  299,
  300,  301,  302,  303,  371,  281,  282,  307,  377,  256,
  286,  256,  334,  256,  284,  324,  292,  293,  256,  256,
  256,  291,  256,  256,  324,  325,  326,  256,  256,  329,
  330,  349,  332,  342,  334,  335,  332,  396,  338,  330,
  256,  341,  342,  343,  344,  343,  344,  349,   41,  349,
  350,   44,  352,  256,  354,  355,   46,  357,  284,  334,
  360,  361,  362,  256,  256,  291,   59,  367,  256,  334,
  256,  371,  264,  265,  266,  267,  256,  256,  307,  379,
  272,  324,  349,  458,  276,  342,  256,  330,  272,  256,
  330,  466,  335,  256,  339,  338,  335,  267,  341,  342,
  256,  401,  294,  295,  296,  297,  298,  299,  300,  301,
  302,  303,  355,  356,  357,  307,  349,  360,  361,  362,
  256,  350,  356,  339,  367,  256,  256,  256,  371,  378,
  256,  324,  324,  325,  326,  256,   41,  329,  330,   44,
  332,  325,  334,  335,  332,  256,  338,  258,  328,  341,
  342,  343,  344,  332,   59,  256,  256,  349,  350,  256,
  352,  256,  354,  355,  256,  357,  256,  256,  360,  361,
  362,   61,  267,  256,  256,  367,  256,  267,  256,  371,
  260,  256,  264,  265,  266,  267,  256,  379,  458,  256,
  272,  324,  328,  260,  276,  128,  466,  267,  131,  328,
  133,  332,  332,   41,  256,   43,  332,   45,  260,  401,
  258,  332,  294,  295,  296,  297,  298,  299,  300,  301,
  302,  303,   60,   61,   62,  307,  256,  256,  328,  367,
  256,   41,  458,   41,   44,  324,   44,  263,  256,  256,
  466,  127,  324,  325,  326,  131,  179,  329,  330,  332,
  332,   59,  334,  335,  332,  256,  338,  332,  256,  341,
  342,  343,  344,  256,   43,  324,   45,  349,  350,  324,
  352,  272,  354,  355,  272,  357,  294,  294,  360,  361,
  362,   60,   61,   62,   41,  367,   43,   44,   45,  371,
  349,  324,  178,  226,  324,  324,  256,  379,  258,  324,
  324,  294,   59,   60,   61,   62,  256,  278,  326,  326,
  260,  324,  327,  324,  274,  275,  349,  324,  324,  401,
  379,  257,  324,  324,  325,  349,  324,  325,  294,   41,
   44,  324,   44,  326,  341,  342,  349,  330,   41,  379,
  306,   44,  335,  349,  310,  338,  379,  349,  341,  342,
   40,  256,  376,  377,  378,  379,  349,   41,  373,  374,
   44,  256,  355,  258,  357,  378,  379,  360,  361,  362,
  376,  377,  378,  379,  367,  377,  378,  379,  371,  274,
  275,  256,  295,  258,  256,  256,  379,  258,  260,  294,
  295,  587,   41,  589,   43,   44,   45,   61,  594,  274,
  275,   99,  100,  274,  275,  256,  324,  258,  401,   40,
   59,   60,   61,   62,  112,  561,  256,  257,  256,  324,
  566,  326,  568,  274,  275,  330,  264,  265,  266,  267,
  335,  349,  256,  338,  258,  256,  341,  342,  276,  260,
   41,   46,  256,   44,  349,  256,  260,  258,  256,  256,
  355,  258,  357,   40,  256,  360,  361,  362,  260,  377,
  378,  379,  367,  274,  275,  303,  371,  274,  275,  256,
  256,  257,   41,  260,  379,   44,  360,  361,  362,   37,
  260,  261,  262,  263,   42,  264,  265,  266,  267,   47,
   59,  256,  256,  257,  256,  260,  401,  276,  260,  256,
   41,  281,  282,   44,  256,  257,  286,  264,  265,  266,
  267,   41,  292,  293,   44,  272,  324,   41,   59,  276,
   44,  256,  330,  258,  303,   40,  355,  335,  357,  256,
  338,  258,  345,  341,  342,  256,  257,  294,  295,  296,
  297,  298,  299,  300,  301,  302,  303,  355,   44,  357,
  307,  341,  360,  361,  362,  597,  598,  599,   44,  367,
  256,  338,  258,  371,  256,   44,  258,  324,  325,  326,
  256,  257,  329,  330,  355,  332,  357,  334,  335,  256,
  257,  338,  274,  275,  341,  342,  343,  344,   41,  256,
  257,   44,  349,  350,  294,  352,   40,  354,  355,  349,
  357,  256,  257,  360,  361,  362,   59,  256,  256,  257,
  367,  256,  257,   44,  371,  264,  265,  266,  267,  257,
  256,   61,  379,  272,   44,   61,   46,  276,   48,   41,
   50,   41, 1008, 1009, 1010,  276,  277,   57,   41,   59,
   41,   44,  256,  257,  401,  294,  295,  296,  297,  298,
  299,  300,  301,  302,  303,  324,   59,  332,  307,  296,
  297,   44,  299,  300,  301,  302,   41,  296,  297,   44,
  299,  300,  301,  302,  272,  324,  325,  326,  256,  257,
  329,  330,  358,  332,   59,  334,  335,  256,  277,  338,
   44,  327,  341,  342,  343,  344,  256,  257,  306,  307,
  349,  350,   40,  352,  401,  354,  355,  324,  357,  256,
  257,  360,  361,  362,   41,  256,   41,  256,  367,   44,
  256,  257,  371,  359,  345,  294,  295,  345,  364,  345,
  379,  256,  257,  369,   59,   40,  372,  373,  374,  324,
  279,  280,  281,  282,  283,  284,  285,  286,  287,  288,
  289,  290,  401,  294,  295,  324,   40,  326,  256,  257,
   40,  330,  398,  256,  257,   41,  335,  256,  257,  338,
  406,  332,  341,  342,  256,  257,  256,  257,  256,  257,
  349,  256,  257,  324,   44,  326,  355,   41,  357,  330,
  295,  360,  361,  362,  335,  256,  257,  338,  367,  306,
  341,  342,  371,  256,   41,  256,  257,   44,  349,  328,
  379,  256,  257,   41,  355,   41,  357,  256,  257,  360,
  361,  362,   59,  256,  257,  324,  367,  256,  257,  277,
  371,  342,  401,  256,  257,  256,  257,  324,  379,  256,
  257,  294,  295,  256,  257,  330,   41,  256,  257,   44,
  256,  257,   41,  256,  256,  257,  256,  257,  256,  257,
  401,  256,  257,    0,   59,  256,  257,  256,  257,  256,
  257,  324,  342,  326,  256,  257,    0,  330,  256,  257,
  101,  256,  335,  256,  257,  338,  256,  257,  341,  342,
  312,  294,  295,  581,   41,  359,  349,   44,  256,  257,
  256,  257,  355,  583,  357,  256,  257,  360,  361,  362,
  256,  257,   59,  749,  367,  256,  257,  749,  371,  294,
  295,  324,  593,  326,  361,  578,  379,  330,  651,  447,
  810,  256,  335,  639, 1108,  338,  144,  659,  341,  342,
  139,  146,   55,  663,   41,  110,  349,   44,  401,  324,
  156,  326,  355,  536,  357,  330,  233,  360,  361,  362,
  335,  354,   59,  338,  367,  713,  341,  342,  371,  294,
  295,  153,   41,  529,  349,   44,  379,  346,  227,  860,
  355,  850,  357,  701,   -1,  360,  361,  362,  238,  706,
   59,   -1,  367,   -1,   -1,   -1,  371,   -1,  401,  324,
   -1,  326,   -1,   -1,  379,  330,   -1,   -1,   -1,   -1,
  335,   -1,   -1,  338,   -1,   -1,  341,  342,   -1,  256,
   -1,   -1,   -1,   -1,  349,   -1,  401,   -1,   -1,   -1,
  355,   -1,  357,   -1,   -1,  360,  361,  362,   -1,  256,
   -1,   -1,  367,   -1,   -1,   -1,  371,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  379,   -1,   -1,  294,  295,   -1,
   -1,  256,  279,  280,  281,  282,  283,  284,  285,  286,
  287,  288,  289,  290,  291,   -1,  401,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  324,   -1,  326,
   -1,   -1,   -1,  330,   -1,   -1,   -1,   40,  335,  294,
  295,  338,   45,   -1,  341,  342,   -1,   -1,   -1,  256,
   -1,   -1,  349,   -1,   -1,   -1,   -1,   -1,  355,   -1,
  357,   -1,   -1,  360,  361,  362,   -1,   -1,   -1,  324,
  367,  326,   -1,   -1,  371,  330,   -1,   -1,   -1,   -1,
  335,   -1,  379,  338,   40,   -1,  341,  342,   -1,   45,
   -1,   -1,   -1,   -1,  349,   -1,   -1,   -1,   -1,  256,
  355,   -1,  357,   -1,  401,  360,  361,  362,   -1,   -1,
   -1,   -1,  367,   -1,   -1,   -1,  371,  324,   -1,   -1,
   -1,   -1,   -1,  330,  379,   -1,   -1,  256,  335,   -1,
   -1,  338,   -1,   -1,  341,  342,   -1,  294,  295,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  401,   -1,  355,   -1,
  357,   40,   -1,  360,  361,  362,   45,   -1,   -1,   -1,
  367,   -1,   -1,   -1,  371,  294,   -1,  324,   -1,  326,
   -1,   -1,   -1,  330,   -1,   -1,   -1,   -1,  335,   -1,
   -1,  338,   -1,   -1,  341,  342,   -1,   -1,   -1,   -1,
   -1,   -1,  349,   -1,   -1,  324,   -1,  326,  355,   -1,
  357,  330,   -1,  360,  361,  362,  335,   -1,   -1,  338,
  367,   -1,  341,  342,  371,   -1,   -1,   -1,   -1,   40,
  349,   -1,  379,   -1,   45,   -1,  355,   -1,  357,   -1,
   -1,  360,  361,  362,   -1,   -1,   -1,   -1,  367,   -1,
   -1,   -1,  371,   -1,  401,   -1,   -1,   -1,   -1,   -1,
  379,   -1,   -1,   -1,  257,  258,   -1,  260,  261,  262,
  263,   -1,   40,   -1,   -1,   -1,   -1,   45,   -1,   -1,
   -1,   -1,  401,   -1,  277,   -1,   -1,   -1,  281,  282,
   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,
  293,   -1,   -1,  296,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  257,  258,   -1,  260,  261,  262,  263,   -1,   -1,
  313,  314,  315,  316,  317,  318,  319,   -1,   -1,  322,
  323,  277,   -1,   -1,   -1,  281,  282,   -1,   -1,   -1,
  286,   -1,   -1,  336,   40,   -1,  292,  293,   -1,   45,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  351,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  313,  314,  315,
  316,  317,  318,  319,   -1,   -1,  322,  323,  257,  258,
   -1,  260,  261,  262,  263,   -1,   -1,   -1,   -1,   -1,
  336,   -1,   40,  256,   -1,   -1,   -1,   45,  277,   -1,
   -1,   -1,  281,  282,   -1,  351,   -1,  286,   -1,   -1,
   -1,   -1,   -1,  292,  293,   -1,  279,  280,  281,  282,
  283,  284,  285,  286,  287,  288,  289,  290,  291,   -1,
   -1,   -1,   -1,   -1,  313,  314,  315,  316,  317,  318,
  319,   -1,   -1,  322,  323,  256,  257,  258,   -1,  260,
  261,  262,  263,   -1,   -1,   -1,   -1,  336,   -1,   -1,
   40,   -1,   -1,   -1,   -1,   45,  277,   -1,   -1,   -1,
  281,  282,  351,   -1,   -1,  286,   -1,   -1,   -1,   -1,
   -1,  292,  293,   -1,   -1,   -1,   -1,   -1,   -1,  257,
  258,   -1,  260,  261,  262,  263,   -1,   -1,   -1,   -1,
   -1,   -1,  313,  314,  315,  316,  317,  318,  319,  277,
   -1,  322,  323,  281,  282,   -1,   -1,   -1,  286,   -1,
   -1,   -1,   -1,   -1,  292,  293,   -1,   -1,   40,   -1,
   -1,   -1,   -1,   45,   -1,  346,  347,  348,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  313,  314,  315,  316,  317,
  318,  319,   -1,   -1,  322,  323,   -1,   -1,   -1,   -1,
   -1,  257,  258,   -1,  260,  261,  262,  263,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   40,  346,  347,
  348,  277,   45,   -1,   -1,  281,  282,   -1,   -1,   -1,
  286,   -1,   -1,   -1,   -1,   -1,  292,  293,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  256,  257,
  258,   -1,  260,  261,  262,  263,   -1,  313,  314,  315,
  316,  317,  318,  319,   -1,   -1,  322,  323,  276,  277,
   -1,   -1,   -1,  281,  282,   -1,   -1,   -1,  286,   -1,
   -1,   -1,   -1,   -1,  292,  293,   -1,   -1,   -1,   -1,
  346,  347,  348,   -1,   -1,   40,  304,   -1,   -1,   -1,
   45,   -1,   -1,   -1,   -1,  313,  314,  315,  316,  317,
  318,  319,   -1,   -1,  322,  323,  256,  257,  258,   -1,
  260,  261,  262,  263,   -1,   -1,   -1,   -1,  336,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  276,  277,   40,   -1,
   -1,  281,  282,   45,   -1,   -1,  286,   -1,   -1,   -1,
   -1,   -1,  292,  293,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  304,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  313,  314,  315,  316,  317,  318,  319,
   -1,   -1,  322,  323,  256,  257,  258,   40,  260,  261,
  262,  263,   45,  333,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  276,  277,   -1,   -1,   -1,  281,
  282,   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,
  292,  293,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   40,  304,  256,  257,  258,   45,  260,  261,  262,
  263,  313,  314,  315,  316,  317,  318,  319,  256,   -1,
  322,  323,   -1,   -1,  277,   -1,   -1,   -1,  281,  282,
   -1,  333,   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,
  293,  279,  280,  281,  282,  283,  284,  285,  286,  287,
  288,  289,  290,  291,   -1,   -1,   -1,   -1,   -1,   -1,
  313,  314,  315,  316,  317,  318,  319,   -1,   -1,  322,
  323,   -1,   -1,   -1,   -1,   -1,  329,   -1,   -1,   -1,
  333,  256,  257,  258,   40,  260,  261,  262,  263,   45,
   37,   -1,   -1,   -1,   41,   42,   43,   44,   45,   -1,
   47,   -1,  277,   -1,   -1,   -1,  281,  282,   -1,   -1,
   -1,  286,   59,   60,   61,   62,   -1,  292,  293,   -1,
   -1,   -1,   -1,   -1,  256,  257,  258,   -1,  260,  261,
  262,  263,   -1,   -1,   -1,   -1,   -1,   -1,  313,  314,
  315,  316,  317,  318,  319,  277,   -1,  322,  323,  281,
  282,   -1,   -1,   -1,  286,   -1,   -1,   40,  333,   -1,
  292,  293,   45,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  256,  257,  258,   -1,  260,  261,  262,
  263,  313,  314,  315,  316,  317,  318,  319,   -1,   -1,
  322,  323,   -1,   -1,  277,   -1,   -1,   -1,  281,  282,
   -1,  333,   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,
  293,   -1,   -1,   -1,   -1,   -1,   -1,  256,  257,  258,
   -1,  260,  261,  262,  263,   -1,   -1,   -1,   -1,   -1,
  313,  314,  315,  316,  317,  318,  319,   -1,  277,  322,
  323,   -1,  281,  282,   -1,   -1,   -1,  286,   -1,   -1,
  333,   -1,   -1,  292,  293,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   37,   -1,   -1,   -1,   41,   42,   43,
   44,   45,   -1,   47,  313,  314,  315,  316,  317,  318,
  319,   -1,   -1,  322,  323,   59,   60,   61,   62,   -1,
   -1,   -1,   -1,   -1,  333,   -1,   -1,   -1,   40,   -1,
  256,  257,  258,   45,  260,  261,  262,  263,   -1,  256,
   -1,   -1,   -1,   -1,   -1,  256,   -1,  264,  265,  266,
  267,  277,   -1,   -1,   -1,  281,  282,   -1,   -1,  276,
  286,   -1,   -1,   -1,   -1,   -1,  292,  293,  279,   -1,
   -1,  282,  283,   -1,  285,  286,  287,  288,  289,  296,
  297,   -1,  299,  300,  301,  302,  303,  313,  314,  315,
  316,  317,  318,  319,   -1,   -1,  322,  323,   -1,  325,
   -1,   40,   -1,  256,  257,  258,   45,  260,  261,  262,
  263,   -1,   -1,  330,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  276,  277,   -1,   -1,   -1,  281,  282,
   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,
  293,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   40,  304,   -1,   -1,   -1,   45,   -1,   -1,   -1,   -1,
  313,  314,  315,  316,  317,  318,  319,   -1,   -1,  322,
  323,   -1,   -1,  384,  385,  386,  387,  388,  389,  390,
  391,  392,  393,   -1,  260,  261,  262,  263,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   40,
  276,  277,  278,   -1,   45,  281,  282,   -1,   -1,   -1,
  286,   -1,  256,   -1,   -1,   -1,  292,  293,   -1,   -1,
  264,  265,  266,  267,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  276,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  256,  257,  258,   -1,  260,  261,
  262,  263,  296,  297,   -1,  299,  300,  301,  302,  303,
   -1,   -1,   -1,   -1,  276,  277,   -1,   40,   -1,  281,
  282,   -1,   45,   -1,  286,   -1,   -1,   -1,   -1,   -1,
  292,  293,   -1,   -1,   -1,   -1,  330,   -1,   -1,   -1,
   -1,   -1,  304,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  313,  314,  315,  316,  317,  318,  319,   -1,   -1,
  322,  323,   -1,   -1,   -1,   -1,   -1,  256,  257,  258,
   -1,  260,  261,  262,  263,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   40,   -1,  276,  277,   -1,
   45,   -1,  281,  282,   -1,   -1,   -1,  286,   -1,   -1,
   -1,   -1,   -1,  292,  293,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  304,  256,  257,  258,   -1,
  260,  261,  262,  263,  313,  314,  315,  316,  317,  318,
  319,   -1,   -1,  322,  323,   -1,  276,  277,   -1,   -1,
   -1,  281,  282,   -1,   -1,   -1,  286,   -1,   -1,   -1,
   -1,   -1,  292,  293,   40,   -1,   -1,   -1,   -1,   45,
   -1,   -1,   -1,   -1,  304,  256,  257,  258,   -1,  260,
  261,  262,  263,  313,  314,  315,  316,  317,  318,  319,
   -1,   -1,  322,  323,   -1,  276,  277,   -1,   -1,   -1,
  281,  282,   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,
   -1,  292,  293,   40,   41,   -1,   -1,   -1,   45,   -1,
   -1,   -1,   -1,  304,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  313,  314,  315,  316,  317,  318,  319,   -1,
   -1,  322,  323,  256,  257,  258,   -1,  260,  261,  262,
  263,   -1,   -1,   -1,   -1,   -1,   -1,   40,   -1,   -1,
   -1,   -1,   45,  276,  277,   -1,   -1,   -1,  281,  282,
   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,
  293,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  304,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  313,  314,  315,  316,  317,  318,  319,   -1,   -1,  322,
  323,  256,  257,  258,   -1,  260,  261,  262,  263,   -1,
   -1,   -1,   -1,   -1,   -1,   40,   -1,   -1,   -1,   -1,
   45,  276,  277,   -1,   -1,   -1,  281,  282,   40,   -1,
   -1,  286,   -1,   45,   -1,   -1,   -1,  292,  293,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  304,
   -1,   -1,   -1,   -1,   -1,  256,   -1,   -1,  313,  314,
  315,  316,  317,  318,  319,   -1,   -1,  322,  323,   -1,
  256,  257,  258,   -1,  260,  261,  262,  263,  279,  280,
  281,  282,  283,  284,  285,  286,  287,  288,  289,  290,
  291,  277,   -1,   -1,   -1,  281,  282,   40,   -1,   -1,
  286,   -1,   45,   -1,   -1,   -1,  292,  293,   -1,   -1,
   40,   -1,   -1,   -1,   -1,   45,   -1,   -1,  304,  256,
  257,  258,   -1,  260,  261,  262,  263,  313,  314,  315,
  316,  317,  318,  319,   -1,   -1,  322,  323,   -1,   -1,
  277,   -1,   -1,   -1,  281,  282,   -1,   -1,   -1,  286,
   -1,   -1,   -1,   -1,   -1,  292,  293,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  257,  258,   -1,  260,  261,  262,
  263,   -1,   -1,   -1,   -1,   -1,  313,  314,  315,  316,
  317,  318,  319,  276,  277,  322,  323,   40,  281,  282,
   -1,   -1,   45,  286,   -1,   -1,   -1,   -1,   -1,  292,
  293,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  304,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  313,  314,  315,  316,  317,  318,  319,   -1,   -1,  322,
  323,  256,  257,  258,   40,  260,  261,  262,  263,   45,
   -1,   -1,   -1,   -1,  256,  257,  258,   -1,  260,  261,
  262,  263,  277,   -1,   -1,   -1,  281,  282,   -1,   -1,
   -1,  286,   -1,   -1,   -1,  277,   -1,  292,  293,  281,
  282,   -1,   -1,   -1,  286,   -1,   -1,   40,   -1,   -1,
  292,  293,   45,   -1,   -1,   -1,   -1,   -1,  313,  314,
  315,  316,  317,  318,  319,   -1,   -1,  322,  323,   -1,
   -1,  313,  314,  315,  316,  317,  318,  319,   -1,   -1,
  322,  323,   -1,  256,  257,  258,   -1,  260,  261,  262,
  263,   -1,   -1,   -1,   40,   -1,  256,  257,  258,   45,
  260,  261,  262,  263,  277,   -1,   -1,   -1,  281,  282,
   -1,   -1,   -1,  286,   -1,   -1,   -1,  277,   -1,  292,
  293,  281,  282,   -1,   -1,   -1,  286,   -1,   -1,   -1,
   -1,   -1,  292,  293,   -1,   -1,   -1,   -1,   -1,   -1,
  313,  314,  315,  316,  317,  318,  319,   -1,   -1,  322,
  323,   -1,   -1,  313,  314,  315,  316,  317,  318,  319,
   -1,   -1,  322,  323,   40,   -1,   -1,   -1,   -1,   45,
   -1,   -1,   -1,  256,  257,  258,   -1,  260,  261,  262,
  263,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  277,   -1,   -1,   -1,  281,  282,
   -1,   -1,   -1,  286,   -1,   -1,   -1,   40,   -1,  292,
  293,   -1,   45,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  256,  257,  258,   -1,  260,  261,  262,  263,   -1,   -1,
  313,  314,  315,  316,  317,  318,  319,   -1,   -1,  322,
  323,  277,   -1,   -1,   -1,  281,  282,   -1,   -1,   -1,
  286,   -1,   -1,   -1,   40,   -1,  292,  293,   -1,   45,
   -1,   -1,   -1,  256,  257,  258,   -1,  260,  261,  262,
  263,   -1,   -1,   -1,   -1,   -1,   -1,  313,  314,  315,
  316,  317,  318,  319,  277,   -1,  322,  323,  281,  282,
   -1,   -1,   -1,  286,   -1,   -1,   -1,   40,   -1,  292,
  293,   -1,   45,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  256,  257,  258,   -1,  260,  261,  262,  263,   -1,   -1,
  313,  314,  315,  316,  317,  318,  319,   -1,   -1,  322,
  323,  277,   -1,   -1,   -1,  281,  282,   -1,   -1,   -1,
  286,   -1,   -1,   -1,   40,   -1,  292,  293,   -1,   45,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  313,  314,  315,
  316,  317,  318,  319,   -1,   -1,  322,  323,   -1,   -1,
  256,  257,  258,   -1,  260,  261,  262,  263,   40,   -1,
   -1,   -1,   -1,   45,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  277,   -1,   -1,   -1,  281,  282,   -1,   -1,   -1,
  286,   -1,   -1,   -1,   -1,   -1,  292,  293,   -1,   -1,
   -1,   -1,   -1,  256,  257,  258,   -1,  260,  261,  262,
  263,   40,   -1,   -1,   -1,   -1,   45,  313,  314,  315,
  316,  317,  318,  319,  277,   -1,  322,  323,  281,  282,
   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,
  293,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  256,  257,  258,   -1,  260,  261,  262,  263,   -1,   -1,
  313,  314,  315,  316,  317,  318,  319,   -1,   -1,  322,
  323,  277,   -1,   -1,   -1,  281,  282,   -1,   -1,   40,
  286,   -1,   -1,   -1,   45,   -1,  292,  293,   -1,   -1,
   -1,   -1,   -1,  256,  257,  258,   -1,  260,  261,  262,
  263,   -1,   -1,   -1,   -1,   -1,   -1,  313,  314,  315,
  316,  317,  318,  319,  277,   -1,  322,  323,  281,  282,
   -1,   -1,   40,  286,   -1,   -1,   -1,   45,   -1,  292,
  293,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  256,  257,  258,   -1,  260,  261,  262,  263,   -1,   -1,
  313,  314,  315,  316,  317,  318,  319,   -1,   -1,  322,
  323,  277,   -1,   -1,   -1,  281,  282,   -1,   -1,   40,
  286,   -1,   -1,   -1,   45,   -1,  292,  293,   -1,   -1,
   -1,   -1,   -1,   -1,  256,  257,  258,   -1,  260,  261,
  262,  263,   -1,   -1,   -1,   -1,   -1,  313,  314,  315,
  316,  317,  318,  319,   -1,  277,  322,  323,   -1,  281,
  282,   -1,   40,   -1,  286,   -1,   -1,   45,   -1,   -1,
  292,  293,   -1,   -1,   -1,   -1,   -1,  256,  257,  258,
   -1,  260,  261,  262,  263,   -1,   -1,   -1,   -1,   -1,
   -1,  313,  314,  315,  316,  317,  318,  319,  277,   -1,
  322,  323,  281,  282,   -1,   40,   41,  286,   -1,   -1,
   45,   -1,   -1,  292,  293,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  313,  314,  315,  316,  317,  318,
  319,   -1,   -1,  322,  323,  256,  257,  258,   40,  260,
  261,  262,  263,   45,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  277,   -1,   -1,   -1,
  281,  282,   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,
   -1,  292,  293,   -1,   -1,   -1,   -1,   -1,  256,  257,
  258,   40,  260,  261,  262,  263,   45,   -1,   -1,   -1,
   -1,   -1,  313,  314,  315,  316,  317,  318,  319,  277,
   -1,  322,  323,  281,  282,   -1,   -1,   -1,  286,   -1,
   -1,   -1,   -1,   -1,  292,  293,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  256,  257,  258,   40,  260,
  261,  262,  263,   45,   -1,  313,  314,  315,  316,  317,
  318,  319,   -1,   -1,  322,  323,  277,   -1,   -1,   -1,
  281,  282,   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,
   -1,  292,  293,   -1,   -1,   -1,   -1,   -1,  256,  257,
  258,   40,  260,  261,  262,  263,   45,   -1,   -1,   -1,
   -1,   -1,  313,  314,  315,  316,  317,  318,  319,  277,
   -1,  322,  323,  281,  282,   -1,   -1,   -1,  286,   -1,
   -1,   -1,   -1,   -1,  292,  293,   -1,   -1,   -1,   -1,
   -1,   -1,  257,  258,   40,  260,  261,  262,  263,   45,
   -1,   -1,   -1,   -1,   -1,  313,  314,  315,  316,  317,
  318,  319,  277,   -1,  322,  323,  281,  282,   -1,   -1,
   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,  293,   -1,
   -1,   -1,   -1,   -1,  256,  257,  258,   40,  260,  261,
  262,  263,   45,   -1,   -1,   -1,   -1,   -1,  313,  314,
  315,  316,  317,  318,  319,  277,   -1,  322,  323,  281,
  282,   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,
  292,  293,   -1,   -1,   -1,   -1,   -1,  256,  257,  258,
   40,  260,  261,  262,  263,   45,   -1,   -1,   -1,   -1,
   -1,  313,  314,  315,  316,  317,  318,  319,  277,   -1,
  322,  323,  281,  282,   -1,   -1,   -1,  286,   -1,   -1,
   -1,   -1,   -1,  292,  293,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  256,  257,  258,   40,  260,  261,
  262,  263,   45,   -1,  313,  314,  315,  316,  317,  318,
  319,   -1,   -1,  322,  323,  277,   -1,   -1,   -1,  281,
  282,   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,
  292,  293,   -1,   -1,   -1,   -1,   -1,  256,  257,  258,
   40,  260,  261,  262,  263,   45,   -1,   -1,   -1,   -1,
   -1,  313,  314,  315,  316,  317,  318,  319,  277,   -1,
  322,  323,  281,  282,   -1,   -1,   -1,  286,   -1,   -1,
   -1,   -1,   -1,  292,  293,   -1,   -1,   -1,   -1,   -1,
  256,  257,  258,   40,  260,  261,  262,  263,   45,   -1,
   -1,   -1,   -1,   -1,  313,  314,  315,  316,  317,  318,
  319,  277,   -1,  322,  323,  281,  282,   -1,   -1,   -1,
  286,   -1,   -1,   -1,   -1,   -1,  292,  293,   -1,   -1,
   -1,   -1,   -1,  256,  257,  258,   40,  260,  261,  262,
  263,   45,   -1,   -1,   -1,   -1,   -1,  313,  314,  315,
  316,  317,  318,  319,  277,   -1,  322,  323,  281,  282,
   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,
  293,   -1,   -1,   -1,   -1,   -1,  256,  257,  258,   40,
  260,  261,  262,  263,   45,   -1,   -1,   -1,   -1,   -1,
  313,  314,  315,  316,  317,  318,  319,  277,   -1,  322,
  323,  281,  282,   -1,   -1,   -1,  286,   -1,   -1,   -1,
   -1,   -1,  292,  293,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  256,  257,  258,   40,  260,  261,  262,
  263,   45,   -1,  313,  314,  315,  316,  317,  318,  319,
   -1,   -1,  322,  323,  277,   -1,   -1,   -1,  281,  282,
   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,
  293,   -1,   -1,   -1,   -1,   -1,  256,  257,  258,   40,
  260,  261,  262,  263,   45,   -1,   -1,   -1,   -1,   -1,
  313,  314,  315,  316,  317,  318,  319,  277,   -1,  322,
  323,  281,  282,   -1,   -1,   -1,  286,   -1,   -1,   -1,
   -1,   -1,  292,  293,   -1,   -1,   -1,   -1,   -1,  256,
  257,  258,   40,  260,  261,  262,  263,   45,   -1,   -1,
   -1,   -1,   -1,  313,  314,  315,  316,  317,  318,  319,
  277,   -1,  322,  323,  281,  282,   -1,   -1,   -1,  286,
   -1,   -1,   -1,   -1,   -1,  292,  293,   -1,   -1,   -1,
   -1,   -1,  256,  257,  258,   40,  260,  261,  262,  263,
   -1,   -1,   -1,   -1,   -1,   -1,  313,  314,  315,  316,
  317,  318,  319,  277,   -1,  322,  323,  281,  282,   -1,
   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,  293,
   -1,   -1,   -1,   -1,   -1,  256,  257,  258,   40,  260,
  261,  262,  263,   45,   -1,   -1,   -1,   -1,   -1,  313,
  314,  315,  316,  317,  318,  319,  277,   -1,  322,  323,
  281,  282,   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,
   -1,  292,  293,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  256,  257,  258,   40,  260,  261,  262,  263,
   45,   -1,  313,  314,  315,  316,  317,  318,  319,   -1,
   -1,  322,  323,  277,   -1,   -1,   -1,  281,  282,   -1,
   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,  293,
   -1,   -1,   -1,   -1,   -1,  256,  257,  258,   -1,  260,
  261,  262,  263,   -1,   -1,   -1,   -1,   -1,   -1,  313,
  314,  315,  316,  317,  318,  319,  277,   -1,  322,  323,
  281,  282,   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,
   -1,  292,  293,   -1,   -1,   -1,   -1,   -1,   -1,  257,
  258,   -1,  260,  261,  262,  263,   -1,   -1,   -1,   -1,
   -1,   -1,  313,  314,  315,  316,  317,  318,  319,  277,
   -1,  322,  323,  281,  282,   -1,   -1,   -1,  286,   -1,
   -1,   -1,   -1,   -1,  292,  293,   -1,   -1,   -1,   -1,
   -1,  256,  257,  258,   -1,  260,  261,  262,  263,   -1,
   -1,   -1,   -1,   -1,   -1,  313,  314,  315,  316,  317,
  318,  319,  277,   -1,  322,  323,  281,  282,   -1,   -1,
   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,  293,   -1,
   -1,   -1,   -1,   -1,   -1,  257,  258,   -1,  260,  261,
  262,  263,   -1,   -1,   -1,   -1,   -1,   -1,  313,  314,
  315,  316,  317,  318,  319,  277,   -1,  322,  323,  281,
  282,   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,
  292,  293,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  257,  258,   -1,  260,  261,  262,  263,   -1,
   -1,  313,  314,  315,  316,  317,  318,  319,   -1,   -1,
  322,  323,  277,   -1,   -1,   -1,  281,  282,   -1,   -1,
   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,  293,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  313,  314,
  315,  316,  317,  318,  319,   -1,   -1,  322,  323,
};
}
final static short YYFINAL=2;
final static short YYMAXTOKEN=406;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,"'%'",null,null,"'('","')'","'*'","'+'",
"','","'-'","'.'","'/'",null,null,null,null,null,null,null,null,null,null,null,
"';'","'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,"ID","VAR_REF","ENV_REF","INT_VAL",
"LONG_VAL","DOUBLE_VAL","STR_VAL","LE","GE","NE","CB","IF","COMMENT",
"WHITESPACE","IFX","ELSE","\"DEFINE\"","\"INOUT\"","\"OUT\"","\"NOT\"",
"\"NULL\"","\"DEFAULT\"","\"BOOLEAN\"","\"BYTE\"","\"BYTES\"","\"DATE\"",
"\"DOUBLE\"","\"ENUM\"","\"FLOAT\"","\"GUID\"","\"INT\"","\"LONG\"","\"SHORT\"",
"\"STRING\"","\"RECORDSET\"","\"TRUE\"","\"FALSE\"","\"OR\"","\"AND\"",
"\"BETWEEN\"","\"LIKE\"","\"ESCAPE\"","\"STARTS_WITH\"","\"ENDS_WITH\"",
"\"CONTAINS\"","\"IN\"","\"IS\"","\"EXISTS\"","\"USING\"","\"RELATIVE\"",
"\"RANGE\"","\"LEAF\"","\"CHILDOF\"","\"PARENTOF\"","\"ANCESTOROF\"",
"\"DESCENDANTOF\"","\"AVG\"","\"MIN\"","\"MAX\"","\"COUNT\"","\"SUM\"",
"\"H_LV\"","\"H_AID\"","\"REL\"","\"ABO\"","\"COALESCE\"","\"CASE\"","\"END\"",
"\"WHEN\"","\"THEN\"","\"QUERY\"","\"BEGIN\"","\"WITH\"","\"UNION\"","\"ALL\"",
"\"AS\"","\"SELECT\"","\"FROM\"","\"WHERE\"","\"CURRENT\"","\"OF\"","\"GROUP\"",
"\"BY\"","\"ROLLUP\"","\"HAVING\"","\"ORDER\"","\"ASC\"","\"DESC\"","\"OVER\"",
"\"ROW_NUMBER\"","\"RANK\"","\"DENSE_RANK\"","\"PARTITION\"","\"ROWS\"",
"\"UNBOUNDED\"","\"PRECEDING\"","\"ROW\"","\"FOLLOWING\"","\"JOIN\"","\"ON\"",
"\"RELATE\"","\"FOR\"","\"UPDATE\"","\"LEFT\"","\"RIGHT\"","\"FULL\"",
"\"DISTINCT\"","\"ORM\"","\"MAPPING\"","\"OVERRIDE\"","\"RETURNING\"",
"\"INTO\"","\"INSERT\"","\"VALUES\"","\"SET\"","\"DELETE\"","\"TABLE\"",
"\"ABSTRACT\"","\"EXTEND\"","\"FIELDS\"","\"INDEXES\"","\"RELATIONS\"",
"\"HIERARCHIES\"","\"VAVLE\"","\"MAXCOUNT\"","\"PRIMARY\"","\"KEY\"",
"\"BINARY\"","\"VARBINARY\"","\"BLOB\"","\"CHAR\"","\"VARCHAR\"","\"NCHAR\"",
"\"NVARCHAR\"","\"TEXT\"","\"NTEXT\"","\"NUMERIC\"","\"RELATION\"","\"TO\"",
"\"UNIQUE\"","\"MAXLEVEL\"","\"PROCEDURE\"","\"VAR\"","\"WHILE\"","\"LOOP\"",
"\"FOREACH\"","\"BREAK\"","\"PRINT\"","\"RETURN\"","\"FUNCTION\"",
};
final static String yyrule[] = {
"$accept : script",
"script : declare_stmt",
"declare_stmt : query_declare",
"declare_stmt : orm_declare",
"declare_stmt : insert_declare",
"declare_stmt : update_declare",
"declare_stmt : delete_declare",
"declare_stmt : table_declare",
"declare_stmt : procedure_declare",
"declare_stmt : function_declare",
"declare_stmt : declare_error",
"declare_error : \"DEFINE\" error",
"param_declare : VAR_REF param_type param_not_null param_default",
"param_declare : VAR_REF param_type param_default param_not_null",
"param_declare : VAR_REF param_type param_default",
"param_declare : VAR_REF param_type param_not_null",
"param_declare : VAR_REF param_type",
"param_declare : \"INOUT\" VAR_REF param_type param_not_null param_default",
"param_declare : \"INOUT\" VAR_REF param_type param_default param_not_null",
"param_declare : \"INOUT\" VAR_REF param_type param_default",
"param_declare : \"INOUT\" VAR_REF param_type param_not_null",
"param_declare : \"INOUT\" VAR_REF param_type",
"param_declare : \"OUT\" VAR_REF param_type",
"param_declare : \"OUT\" VAR_REF param_type param_not_null",
"param_declare : VAR_REF error",
"param_declare : \"INOUT\" VAR_REF error",
"param_declare : \"OUT\" VAR_REF error",
"param_not_null : \"NOT\" \"NULL\"",
"param_not_null : \"NOT\" error",
"param_not_null : \"NULL\"",
"param_default : \"DEFAULT\" literal",
"param_default : \"DEFAULT\" '-' literal",
"param_default : \"DEFAULT\" error",
"param_default : literal",
"param_type : \"BOOLEAN\"",
"param_type : \"BYTE\"",
"param_type : \"BYTES\"",
"param_type : \"DATE\"",
"param_type : \"DOUBLE\"",
"param_type : \"ENUM\" '<' class_name '>'",
"param_type : \"FLOAT\"",
"param_type : \"GUID\"",
"param_type : \"INT\"",
"param_type : \"LONG\"",
"param_type : \"SHORT\"",
"param_type : \"STRING\"",
"param_type : \"RECORDSET\"",
"param_type : \"ENUM\" '<' class_name error",
"param_type : \"ENUM\" '<' error",
"param_type : \"ENUM\" error",
"param_declare_list : param_declare_list ',' param_declare",
"param_declare_list : param_declare",
"param_declare_list : param_declare_list ',' error",
"param_declare_list_op : param_declare_list",
"param_declare_list_op :",
"column_ref : ID '.' ID",
"column_ref : ID '.' error",
"name_ref : ID",
"literal : STR_VAL",
"literal : INT_VAL",
"literal : LONG_VAL",
"literal : DOUBLE_VAL",
"literal : \"TRUE\"",
"literal : \"FALSE\"",
"literal : \"DATE\" STR_VAL",
"literal : \"GUID\" STR_VAL",
"literal : \"BYTES\" STR_VAL",
"literal : \"DATE\" error",
"literal : \"GUID\" error",
"literal : \"BYTES\" error",
"condition_expr : condition_expr \"OR\" and_expr",
"condition_expr : and_expr",
"condition_expr : condition_expr \"OR\" error",
"and_expr : and_expr \"AND\" not_expr",
"and_expr : not_expr",
"and_expr : and_expr \"AND\" error",
"not_expr : \"NOT\" compare_expr",
"not_expr : compare_expr",
"not_expr : \"NOT\" error",
"compare_expr : '(' condition_expr ')'",
"compare_expr : value_expr compare_operator value_expr",
"compare_expr : between_expr",
"compare_expr : like_expr",
"compare_expr : str_compare_expr",
"compare_expr : in_expr",
"compare_expr : is_null_expr",
"compare_expr : exists_expr",
"compare_expr : hierarchy_expr",
"compare_expr : path_expr",
"compare_expr : '(' condition_expr error",
"compare_expr : value_expr compare_operator error",
"compare_operator : '>'",
"compare_operator : '<'",
"compare_operator : GE",
"compare_operator : LE",
"compare_operator : '='",
"compare_operator : NE",
"between_expr : value_expr not_expr_op \"BETWEEN\" value_expr \"AND\" value_expr",
"between_expr : value_expr not_expr_op \"BETWEEN\" value_expr \"AND\" error",
"between_expr : value_expr not_expr_op \"BETWEEN\" value_expr error",
"between_expr : value_expr not_expr_op \"BETWEEN\" error",
"like_expr : value_expr not_expr_op \"LIKE\" value_expr escape_expr_op",
"like_expr : value_expr not_expr_op \"LIKE\" error",
"escape_expr_op : \"ESCAPE\" value_expr",
"escape_expr_op :",
"escape_expr_op : \"ESCAPE\" error",
"str_compare_expr : value_expr not_expr_op str_compare_predicate value_expr",
"str_compare_expr : value_expr not_expr_op str_compare_predicate error",
"str_compare_predicate : \"STARTS_WITH\"",
"str_compare_predicate : \"ENDS_WITH\"",
"str_compare_predicate : \"CONTAINS\"",
"in_expr : value_expr not_expr_op \"IN\" in_expr_param",
"in_expr : value_expr not_expr_op \"IN\" error",
"in_expr_param : '(' in_value_list ')'",
"in_expr_param : '(' sub_query ')'",
"in_expr_param : '(' in_value_list error",
"in_expr_param : '(' error",
"in_value_list : in_value_list ',' value_expr",
"in_value_list : value_expr",
"in_value_list : in_value_list ',' error",
"is_null_expr : value_expr \"IS\" not_expr_op \"NULL\"",
"is_null_expr : value_expr \"IS\" error",
"not_expr_op : \"NOT\"",
"not_expr_op :",
"exists_expr : \"EXISTS\" '(' sub_query ')'",
"exists_expr : \"EXISTS\" '(' sub_query error",
"exists_expr : \"EXISTS\" '(' error",
"exists_expr : \"EXISTS\" error",
"hierarchy_expr : ID hierarchy_predicate ID \"USING\" ID",
"hierarchy_expr : ID hierarchy_predicate ID \"USING\" ID \"RELATIVE\" value_expr",
"hierarchy_expr : ID hierarchy_predicate ID \"USING\" ID \"RANGE\" value_expr",
"hierarchy_expr : ID \"IS\" \"LEAF\" \"USING\" ID",
"hierarchy_expr : ID hierarchy_predicate ID \"USING\" ID \"RELATIVE\" error",
"hierarchy_expr : ID hierarchy_predicate ID \"USING\" ID \"RANGE\" error",
"hierarchy_expr : ID hierarchy_predicate ID \"USING\" error",
"hierarchy_expr : ID hierarchy_predicate ID error",
"hierarchy_expr : ID hierarchy_predicate error",
"hierarchy_expr : ID \"IS\" \"LEAF\" \"USING\" error",
"hierarchy_expr : ID \"IS\" \"LEAF\" error",
"hierarchy_expr : ID \"IS\" error",
"hierarchy_predicate : \"CHILDOF\"",
"hierarchy_predicate : \"PARENTOF\"",
"hierarchy_predicate : \"ANCESTOROF\"",
"hierarchy_predicate : \"DESCENDANTOF\"",
"path_expr : ID hierarchy_predicate ID \"USING\" '(' ID ',' ID ')'",
"path_expr : ID hierarchy_predicate ID \"USING\" '(' ID ',' ID ')' \"RELATIVE\" value_expr",
"path_expr : ID hierarchy_predicate ID \"USING\" '(' ID ',' ID ')' \"RELATIVE\" error",
"path_expr : ID hierarchy_predicate ID \"USING\" '(' ID ',' ID error",
"path_expr : ID hierarchy_predicate ID \"USING\" '(' ID ',' error",
"path_expr : ID hierarchy_predicate ID \"USING\" '(' ID error",
"path_expr : ID hierarchy_predicate ID \"USING\" '(' error",
"value_expr : value_expr '+' mul_expr",
"value_expr : value_expr '-' mul_expr",
"value_expr : value_expr CB mul_expr",
"value_expr : mul_expr",
"value_expr : value_expr '+' error",
"value_expr : value_expr '-' error",
"value_expr : value_expr CB error",
"mul_expr : mul_expr '*' neg_expr",
"mul_expr : mul_expr '/' neg_expr",
"mul_expr : mul_expr '%' neg_expr",
"mul_expr : neg_expr",
"mul_expr : mul_expr '*' error",
"mul_expr : mul_expr '/' error",
"neg_expr : '-' factor",
"neg_expr : factor",
"neg_expr : '-' error",
"var_ref : var_ref '.' ID",
"var_ref : VAR_REF",
"factor : column_ref",
"factor : literal",
"factor : var_ref",
"factor : \"NULL\"",
"factor : '(' value_expr ')'",
"factor : '(' sub_query ')'",
"factor : set_func",
"factor : scalar_func",
"factor : hierarchy_func",
"factor : coalesce_func",
"factor : simple_case",
"factor : searched_case",
"factor : '(' value_expr error",
"factor : '(' error",
"set_func : sum_func",
"set_func : \"AVG\" '(' set_quantifier_op value_expr ')'",
"set_func : \"MIN\" '(' value_expr ')'",
"set_func : \"MAX\" '(' value_expr ')'",
"set_func : \"COUNT\" '(' set_quantifier_op value_expr ')'",
"set_func : \"COUNT\" '(' '*' ')'",
"set_func : \"AVG\" '(' set_quantifier_op value_expr error",
"set_func : \"MIN\" '(' value_expr error",
"set_func : \"MAX\" '(' value_expr error",
"set_func : \"COUNT\" '(' '*' error",
"set_func : \"COUNT\" '(' error",
"set_func : \"AVG\" error",
"set_func : \"MIN\" error",
"set_func : \"MAX\" error",
"sum_func : \"SUM\" '(' set_quantifier_op value_expr ')'",
"sum_func : \"SUM\" '(' set_quantifier_op value_expr error",
"sum_func : \"SUM\" error",
"scalar_func : ID '(' value_list ')'",
"scalar_func : ID '(' ')'",
"scalar_func : ID '(' value_list error",
"scalar_func : ID '(' error",
"hierarchy_func : \"H_LV\" '(' ID '.' ID ')'",
"hierarchy_func : \"H_AID\" '(' ID '.' ID ')'",
"hierarchy_func : \"H_AID\" '(' ID '.' ID \"REL\" value_expr ')'",
"hierarchy_func : \"H_AID\" '(' ID '.' ID \"ABO\" value_expr ')'",
"hierarchy_func : \"H_LV\" '(' ID '.' ID error",
"hierarchy_func : \"H_LV\" '(' ID '.' error",
"hierarchy_func : \"H_LV\" '(' ID error",
"hierarchy_func : \"H_LV\" '(' error",
"hierarchy_func : \"H_LV\" error",
"hierarchy_func : \"H_AID\" '(' ID '.' ID \"REL\" value_expr error",
"hierarchy_func : \"H_AID\" '(' ID '.' ID \"ABO\" value_expr error",
"hierarchy_func : \"H_AID\" '(' ID '.' ID \"REL\" error",
"hierarchy_func : \"H_AID\" '(' ID '.' ID \"ABO\" error",
"hierarchy_func : \"H_AID\" '(' ID '.' ID error",
"hierarchy_func : \"H_AID\" '(' ID '.' error",
"hierarchy_func : \"H_AID\" '(' ID error",
"hierarchy_func : \"H_AID\" '(' error",
"hierarchy_func : \"H_AID\" error",
"coalesce_func : \"COALESCE\" '(' value_list ')'",
"coalesce_func : \"COALESCE\" '(' value_list error",
"coalesce_func : \"COALESCE\" '(' error",
"coalesce_func : \"COALESCE\" error",
"simple_case : \"CASE\" value_expr simple_case_when_list case_else_expr_op \"END\"",
"simple_case : \"CASE\" value_expr simple_case_when_list case_else_expr_op error",
"simple_case : \"CASE\" value_expr error",
"simple_case : \"CASE\" error",
"simple_case_when_list : simple_case_when_list simple_case_when",
"simple_case_when_list : simple_case_when",
"simple_case_when : \"WHEN\" value_expr \"THEN\" value_expr",
"simple_case_when : \"WHEN\" value_expr \"THEN\" error",
"simple_case_when : \"WHEN\" value_expr error",
"simple_case_when : \"WHEN\" error",
"case_else_expr_op : ELSE value_expr",
"case_else_expr_op :",
"case_else_expr_op : ELSE error",
"searched_case : \"CASE\" searched_case_when_list case_else_expr_op \"END\"",
"searched_case : \"CASE\" searched_case_when_list case_else_expr_op error",
"searched_case_when_list : searched_case_when_list searched_case_when",
"searched_case_when_list : searched_case_when",
"searched_case_when : \"WHEN\" condition_expr \"THEN\" value_expr",
"searched_case_when : \"WHEN\" condition_expr \"THEN\" error",
"searched_case_when : \"WHEN\" condition_expr error",
"value_list : value_list ',' value_expr",
"value_list : value_expr",
"value_list : value_list ',' error",
"query_invoke : ID '(' value_list ')'",
"query_invoke : ID '(' ')'",
"query_declare : \"DEFINE\" \"QUERY\" ID '(' param_declare_list_op ')' \"BEGIN\" query_stmt \"END\"",
"query_declare : \"DEFINE\" \"QUERY\" ID '(' param_declare_list_op ')' \"BEGIN\" query_stmt ';' \"END\"",
"query_declare : \"DEFINE\" \"QUERY\" ID '(' param_declare_list_op ')' \"BEGIN\" query_stmt error",
"query_declare : \"DEFINE\" \"QUERY\" ID '(' param_declare_list_op ')' \"BEGIN\" error",
"query_declare : \"DEFINE\" \"QUERY\" ID '(' param_declare_list_op ')' error",
"query_declare : \"DEFINE\" \"QUERY\" ID '(' error",
"query_declare : \"DEFINE\" \"QUERY\" ID error",
"query_declare : \"DEFINE\" \"QUERY\" error",
"query_stmt : \"WITH\" query_with_list query_union orderby_op",
"query_stmt : query_union orderby_op",
"query_stmt : \"WITH\" query_with_list error",
"query_stmt : \"WITH\" error",
"query_union : sub_query \"UNION\" query_primary",
"query_union : sub_query \"UNION\" \"ALL\" query_primary",
"query_union : query_sub \"UNION\" query_primary",
"query_union : query_sub \"UNION\" \"ALL\" query_primary",
"query_union : query_select",
"query_union : sub_query \"UNION\" error",
"query_union : query_sub \"UNION\" error",
"query_sub : '(' sub_query ')'",
"query_sub : '(' sub_query error",
"query_sub : '(' error",
"query_primary : query_sub",
"query_primary : query_select",
"query_select : select from where_op groupby_op having_op",
"query_select : select error",
"sub_query : query_union orderby_op",
"query_with_list : query_with_list ',' query_with",
"query_with_list : query_with",
"query_with_list : query_with_list ',' error",
"query_with : '(' sub_query ')' \"AS\" ID",
"query_with : '(' sub_query ')' \"AS\" error",
"query_with : '(' sub_query ')' error",
"query_with : '(' sub_query error",
"select : \"SELECT\" set_quantifier_op query_column_list",
"select : \"SELECT\" error",
"from : \"FROM\" source_list",
"from : \"FROM\" error",
"source_list : source_list ',' source",
"source_list : source",
"source_list : source_list ',' error",
"where_op : \"WHERE\" condition_expr",
"where_op : \"WHERE\" \"CURRENT\" \"OF\" VAR_REF",
"where_op :",
"where_op : \"WHERE\" error",
"where_op : \"WHERE\" \"CURRENT\" \"OF\" error",
"where_op : \"WHERE\" \"CURRENT\" error",
"groupby_op : \"GROUP\" \"BY\" groupby_column_list",
"groupby_op : \"GROUP\" \"BY\" groupby_column_list \"WITH\" \"ROLLUP\"",
"groupby_op :",
"groupby_op : \"GROUP\" \"BY\" groupby_column_list \"WITH\" error",
"groupby_op : \"GROUP\" \"BY\" error",
"groupby_op : \"GROUP\" error",
"groupby_column_list : groupby_column_list ',' value_expr",
"groupby_column_list : value_expr",
"groupby_column_list : groupby_column_list ',' error",
"having_op : \"HAVING\" condition_expr",
"having_op :",
"having_op : \"HAVING\" error",
"orderby_op : orderby",
"orderby_op :",
"orderby : \"ORDER\" \"BY\" orderby_column_list",
"orderby : \"ORDER\" \"BY\" error",
"orderby : \"ORDER\" error",
"orderby_column_list : orderby_column_list ',' orderby_column",
"orderby_column_list : orderby_column",
"orderby_column : value_expr",
"orderby_column : value_expr \"ASC\"",
"orderby_column : value_expr \"DESC\"",
"orderby_column : ID",
"orderby_column : ID \"ASC\"",
"orderby_column : ID \"DESC\"",
"query_column_list : query_column_list ',' query_column",
"query_column_list : query_column",
"query_column_list : query_column_list ',' error",
"query_column : value_expr",
"query_column : value_expr \"AS\" ID",
"query_column : analytic_function_expr \"AS\" ID",
"query_column : value_expr \"AS\" error",
"query_column : analytic_function_expr \"AS\" error",
"query_column : analytic_function_expr error",
"analytic_function_expr : sum_func \"OVER\" '(' af_sum_partition_op orderby af_sum_window_clause_op ')'",
"analytic_function_expr : \"ROW_NUMBER\" '(' ')' \"OVER\" '(' af_sum_partition_op orderby ')'",
"analytic_function_expr : \"RANK\" '(' ')' \"OVER\" '(' af_sum_partition_op orderby ')'",
"analytic_function_expr : \"DENSE_RANK\" '(' ')' \"OVER\" '(' af_sum_partition_op orderby ')'",
"analytic_function_expr : sum_func \"OVER\" '(' af_sum_partition_op orderby error",
"analytic_function_expr : sum_func \"OVER\" '(' error",
"analytic_function_expr : sum_func \"OVER\" error",
"analytic_function_expr : \"ROW_NUMBER\" '(' ')' \"OVER\" '(' af_sum_partition_op orderby error",
"analytic_function_expr : \"ROW_NUMBER\" '(' ')' \"OVER\" '(' error",
"analytic_function_expr : \"ROW_NUMBER\" '(' ')' \"OVER\"",
"analytic_function_expr : \"ROW_NUMBER\" '(' ')'",
"analytic_function_expr : \"ROW_NUMBER\" '('",
"analytic_function_expr : \"ROW_NUMBER\"",
"analytic_function_expr : \"RANK\" '(' ')' \"OVER\" '(' af_sum_partition_op orderby error",
"analytic_function_expr : \"RANK\" '(' ')' \"OVER\" '(' error",
"analytic_function_expr : \"RANK\" '(' ')' \"OVER\"",
"analytic_function_expr : \"RANK\" '(' ')'",
"analytic_function_expr : \"RANK\" '('",
"analytic_function_expr : \"RANK\"",
"analytic_function_expr : \"DENSE_RANK\" '(' ')' \"OVER\" '(' af_sum_partition_op orderby error",
"analytic_function_expr : \"DENSE_RANK\" '(' ')' \"OVER\" '(' error",
"analytic_function_expr : \"DENSE_RANK\" '(' ')' \"OVER\"",
"analytic_function_expr : \"DENSE_RANK\" '(' ')'",
"analytic_function_expr : \"DENSE_RANK\" '('",
"analytic_function_expr : \"DENSE_RANK\"",
"af_sum_partition_op : \"PARTITION\" \"BY\" af_sum_partition_list",
"af_sum_partition_op :",
"af_sum_partition_op : \"PARTITION\" \"BY\" error",
"af_sum_partition_op : \"PARTITION\" error",
"af_sum_partition_list : af_sum_partition_list ',' value_expr",
"af_sum_partition_list : value_expr",
"af_sum_partition_list : af_sum_partition_list ',' error",
"af_sum_window_clause_op : af_sum_window_clause",
"af_sum_window_clause_op :",
"af_sum_window_clause : af_sum_window_type af_sum_preceding",
"af_sum_window_clause : af_sum_window_type \"BETWEEN\" af_sum_preceding \"AND\" af_sum_following",
"af_sum_window_type : \"ROWS\"",
"af_sum_window_type : \"RANGE\"",
"af_sum_preceding : \"UNBOUNDED\" \"PRECEDING\"",
"af_sum_preceding : \"CURRENT\" \"ROW\"",
"af_sum_preceding : value_expr \"PRECEDING\"",
"af_sum_preceding : \"UNBOUNDED\" error",
"af_sum_preceding : \"CURRENT\" error",
"af_sum_preceding : value_expr error",
"af_sum_following : \"UNBOUNDED\" \"FOLLOWING\"",
"af_sum_following : \"CURRENT\" \"ROW\"",
"af_sum_following : value_expr \"FOLLOWING\"",
"af_sum_following : \"UNBOUNDED\" error",
"af_sum_following : \"CURRENT\" error",
"af_sum_following : value_expr error",
"source : source join_type \"JOIN\" source_ref \"ON\" condition_expr",
"source : source join_type \"RELATE\" ID '.' ID",
"source : source join_type \"RELATE\" ID '.' ID \"AS\" ID",
"source : source_ref",
"source : source join_type \"JOIN\" source_ref \"ON\" error",
"source : source join_type \"JOIN\" source_ref error",
"source : source join_type \"JOIN\" error",
"source : source join_type \"RELATE\" ID '.' ID \"AS\" error",
"source : source join_type \"RELATE\" ID '.' error",
"source : source join_type \"RELATE\" ID error",
"source : source join_type \"RELATE\" error",
"source_ref : name_ref \"FOR\" \"UPDATE\"",
"source_ref : name_ref \"AS\" ID \"FOR\" \"UPDATE\"",
"source_ref : name_ref",
"source_ref : name_ref \"AS\" ID",
"source_ref : '(' sub_query ')' \"AS\" ID",
"source_ref : '(' source ')'",
"source_ref : name_ref \"FOR\" error",
"source_ref : name_ref \"AS\" ID \"FOR\" error",
"source_ref : name_ref \"AS\" error",
"source_ref : '(' sub_query ')' \"AS\" error",
"join_type : \"LEFT\"",
"join_type : \"RIGHT\"",
"join_type : \"FULL\"",
"join_type :",
"set_quantifier_op : \"ALL\"",
"set_quantifier_op : \"DISTINCT\"",
"set_quantifier_op :",
"orm_declare : \"DEFINE\" \"ORM\" ID '(' param_declare_list_op ')' \"MAPPING\" class_name \"BEGIN\" query_stmt \"END\"",
"orm_declare : \"DEFINE\" \"ORM\" ID '(' param_declare_list_op ')' \"OVERRIDE\" ID \"BEGIN\" where_op having_op orderby_op \"END\"",
"orm_declare : \"DEFINE\" \"ORM\" ID '(' param_declare_list_op ')' \"MAPPING\" class_name \"BEGIN\" query_stmt error",
"orm_declare : \"DEFINE\" \"ORM\" ID '(' param_declare_list_op ')' \"MAPPING\" class_name \"BEGIN\" error",
"orm_declare : \"DEFINE\" \"ORM\" ID '(' param_declare_list_op ')' \"MAPPING\" class_name error",
"orm_declare : \"DEFINE\" \"ORM\" ID '(' param_declare_list_op ')' \"MAPPING\" error",
"orm_declare : \"DEFINE\" \"ORM\" ID '(' param_declare_list_op ')' \"OVERRIDE\" ID \"BEGIN\" error",
"orm_declare : \"DEFINE\" \"ORM\" ID '(' param_declare_list_op ')' \"OVERRIDE\" ID error",
"orm_declare : \"DEFINE\" \"ORM\" ID '(' param_declare_list_op ')' \"OVERRIDE\" error",
"orm_declare : \"DEFINE\" \"ORM\" ID '(' param_declare_list_op ')' error",
"orm_declare : \"DEFINE\" \"ORM\" ID '(' error",
"orm_declare : \"DEFINE\" \"ORM\" ID error",
"orm_declare : \"DEFINE\" \"ORM\" error",
"class_name : class_list",
"class_list : class_list '.' ID",
"class_list : ID",
"class_list : class_list '.' error",
"returning_op : \"RETURNING\" ENV_REF \"INTO\" VAR_REF",
"returning_op :",
"returning_op : \"RETURNING\" ENV_REF \"INTO\" error",
"returning_op : \"RETURNING\" ENV_REF error",
"returning_op : \"RETURNING\" error",
"insert_declare : \"DEFINE\" \"INSERT\" ID '(' param_declare_list_op ')' \"BEGIN\" insert_stmt \"END\"",
"insert_declare : \"DEFINE\" \"INSERT\" ID '(' param_declare_list_op ')' \"BEGIN\" insert_stmt ';' \"END\"",
"insert_declare : \"DEFINE\" \"INSERT\" ID '(' param_declare_list_op ')' \"BEGIN\" insert_stmt error",
"insert_declare : \"DEFINE\" \"INSERT\" ID '(' param_declare_list_op ')' \"BEGIN\" error",
"insert_declare : \"DEFINE\" \"INSERT\" ID '(' param_declare_list_op ')' error",
"insert_declare : \"DEFINE\" \"INSERT\" ID '(' error",
"insert_declare : \"DEFINE\" \"INSERT\" ID error",
"insert_declare : \"DEFINE\" \"INSERT\" error",
"insert_stmt : insert insert_values returning_op",
"insert_stmt : insert insert_sub_query returning_op",
"insert_stmt : insert '(' error",
"insert_stmt : insert error",
"insert : \"INSERT\" \"INTO\" name_ref",
"insert : \"INSERT\" \"INTO\" error",
"insert : \"INSERT\" error",
"insert_sub_query : '(' sub_query ')'",
"insert_sub_query : '(' sub_query error",
"insert_values : '(' insert_column_list ')' \"VALUES\" '(' insert_value_list ')'",
"insert_values : '(' insert_column_list ')' \"VALUES\" '(' insert_value_list error",
"insert_values : '(' insert_column_list ')' \"VALUES\" '(' error",
"insert_values : '(' insert_column_list ')' \"VALUES\" error",
"insert_values : '(' insert_column_list ')' error",
"insert_values : '(' insert_column_list error",
"insert_value_list : insert_value_list ',' value_expr",
"insert_value_list : value_expr",
"insert_value_list : insert_value_list ',' error",
"insert_column_list : insert_column_list ',' ID",
"insert_column_list : ID",
"insert_column_list : insert_column_list ',' error",
"update_declare : \"DEFINE\" \"UPDATE\" ID '(' param_declare_list_op ')' \"BEGIN\" update_stmt \"END\"",
"update_declare : \"DEFINE\" \"UPDATE\" ID '(' param_declare_list_op ')' \"BEGIN\" update_stmt ';' \"END\"",
"update_declare : \"DEFINE\" \"UPDATE\" ID '(' param_declare_list_op ')' \"BEGIN\" update_stmt error",
"update_declare : \"DEFINE\" \"UPDATE\" ID '(' param_declare_list_op ')' \"BEGIN\" error",
"update_declare : \"DEFINE\" \"UPDATE\" ID '(' param_declare_list_op ')' error",
"update_declare : \"DEFINE\" \"UPDATE\" ID '(' error",
"update_declare : \"DEFINE\" \"UPDATE\" ID error",
"update_declare : \"DEFINE\" \"UPDATE\" error",
"update_stmt : update update_set where_op returning_op",
"update_stmt : update error",
"update : \"UPDATE\" source",
"update : \"UPDATE\" error",
"update_set : \"SET\" update_column_list",
"update_set : \"SET\" error",
"update_column_list : update_column_list ',' update_column_value",
"update_column_list : update_column_value",
"update_column_list : update_column_list ',' error",
"update_column_value : ID '=' value_expr",
"update_column_value : ID '=' error",
"update_column_value : ID error",
"delete_declare : \"DEFINE\" \"DELETE\" ID '(' param_declare_list_op ')' \"BEGIN\" delete_stmt \"END\"",
"delete_declare : \"DEFINE\" \"DELETE\" ID '(' param_declare_list_op ')' \"BEGIN\" delete_stmt ';' \"END\"",
"delete_declare : \"DEFINE\" \"DELETE\" ID '(' param_declare_list_op ')' \"BEGIN\" delete_stmt error",
"delete_declare : \"DEFINE\" \"DELETE\" ID '(' param_declare_list_op ')' \"BEGIN\" error",
"delete_declare : \"DEFINE\" \"DELETE\" ID '(' param_declare_list_op ')' error",
"delete_declare : \"DEFINE\" \"DELETE\" ID '(' error",
"delete_declare : \"DEFINE\" \"DELETE\" ID error",
"delete_declare : \"DEFINE\" \"DELETE\" error",
"delete_stmt : delete where_op returning_op",
"delete : \"DELETE\" \"FROM\" source",
"delete : \"DELETE\" \"FROM\" error",
"delete : \"DELETE\" error",
"table_declare : \"DEFINE\" \"TABLE\" ID table_extend_op \"BEGIN\" primary_section extend_section_op index_section_op relation_section_op hierarchy_section_op partition_section_op \"END\"",
"table_declare : \"DEFINE\" \"ABSTRACT\" \"TABLE\" ID table_extend_op \"BEGIN\" primary_section extend_section_op index_section_op relation_section_op hierarchy_section_op partition_section_op \"END\"",
"table_declare : \"DEFINE\" \"TABLE\" ID table_extend_op \"BEGIN\" error",
"table_declare : \"DEFINE\" \"TABLE\" ID error",
"table_declare : \"DEFINE\" \"TABLE\" error",
"table_declare : \"DEFINE\" \"ABSTRACT\" \"TABLE\" ID table_extend_op \"BEGIN\" error",
"table_declare : \"DEFINE\" \"ABSTRACT\" \"TABLE\" ID error",
"table_declare : \"DEFINE\" \"ABSTRACT\" \"TABLE\" error",
"table_declare : \"DEFINE\" \"ABSTRACT\" error",
"table_extend_op : \"EXTEND\" ID",
"table_extend_op :",
"table_extend_op : \"EXTEND\" error",
"primary_section : \"FIELDS\" table_field_list",
"primary_section : \"FIELDS\" error",
"extend_section_op : extend_section",
"extend_section_op :",
"extend_section : extend_section extend_declare",
"extend_section : extend_declare",
"extend_declare : \"FIELDS\" \"ON\" ID table_field_list",
"extend_declare : \"FIELDS\" \"ON\" error",
"extend_declare : \"FIELDS\" error",
"index_section_op : \"INDEXES\" index_declare_list",
"index_section_op :",
"index_section_op : \"INDEXES\" error",
"relation_section_op : \"RELATIONS\" relation_declare_list",
"relation_section_op :",
"relation_section_op : \"RELATIONS\" error",
"hierarchy_section_op : \"HIERARCHIES\" hierarchy_declare_list",
"hierarchy_section_op :",
"hierarchy_section_op : \"HIERARCHIES\" error",
"partition_section_op : \"PARTITION\" '(' partition_field_list ')' \"VAVLE\" INT_VAL \"MAXCOUNT\" INT_VAL",
"partition_section_op :",
"partition_section_op : \"PARTITION\" '(' partition_field_list ')' \"VAVLE\" INT_VAL \"MAXCOUNT\" error",
"partition_section_op : \"PARTITION\" '(' partition_field_list ')' \"VAVLE\" INT_VAL error",
"partition_section_op : \"PARTITION\" '(' partition_field_list ')' \"VAVLE\" error",
"partition_section_op : \"PARTITION\" '(' partition_field_list ')' error",
"partition_section_op : \"PARTITION\" '(' partition_field_list error",
"partition_section_op : \"PARTITION\" '(' error",
"partition_section_op : \"PARTITION\" error",
"table_field_list : table_field_list ',' table_field_declare",
"table_field_list : table_field_declare",
"table_field_list : table_field_list ',' error",
"table_field_declare : ID field_type field_not_null_op field_default_op",
"table_field_declare : ID field_type field_not_null_op field_default_op \"PRIMARY\" \"KEY\"",
"table_field_declare : ID field_type field_not_null_op field_default_op field_foreign_key",
"table_field_declare : ID field_type field_not_null_op field_default_op \"PRIMARY\" error",
"table_field_declare : ID error",
"field_not_null_op : param_not_null",
"field_not_null_op :",
"field_default_op : \"DEFAULT\" '(' literal ')'",
"field_default_op : \"DEFAULT\" '(' '-' literal ')'",
"field_default_op :",
"field_type : \"BOOLEAN\"",
"field_type : \"DATE\"",
"field_type : \"DOUBLE\"",
"field_type : \"FLOAT\"",
"field_type : \"GUID\"",
"field_type : \"INT\"",
"field_type : \"LONG\"",
"field_type : \"SHORT\"",
"field_type : \"BINARY\" '(' INT_VAL ')'",
"field_type : \"VARBINARY\" '(' INT_VAL ')'",
"field_type : \"BLOB\"",
"field_type : \"CHAR\" '(' INT_VAL ')'",
"field_type : \"VARCHAR\" '(' INT_VAL ')'",
"field_type : \"NCHAR\" '(' INT_VAL ')'",
"field_type : \"NVARCHAR\" '(' INT_VAL ')'",
"field_type : \"TEXT\"",
"field_type : \"NTEXT\"",
"field_type : \"NUMERIC\" '(' INT_VAL ',' INT_VAL ')'",
"field_type : \"BINARY\" '(' INT_VAL error",
"field_type : \"BINARY\" '(' error",
"field_type : \"BINARY\" error",
"field_type : \"VARBINARY\" '(' INT_VAL error",
"field_type : \"VARBINARY\" '(' error",
"field_type : \"VARBINARY\" error",
"field_type : \"CHAR\" '(' INT_VAL error",
"field_type : \"CHAR\" '(' error",
"field_type : \"CHAR\" error",
"field_type : \"VARCHAR\" '(' INT_VAL error",
"field_type : \"VARCHAR\" '(' error",
"field_type : \"VARCHAR\" error",
"field_type : \"NCHAR\" '(' INT_VAL error",
"field_type : \"NCHAR\" '(' error",
"field_type : \"NCHAR\" error",
"field_type : \"NVARCHAR\" '(' INT_VAL error",
"field_type : \"NVARCHAR\" '(' error",
"field_type : \"NVARCHAR\" error",
"field_type : \"NUMERIC\" '(' INT_VAL ',' INT_VAL error",
"field_type : \"NUMERIC\" '(' INT_VAL ',' error",
"field_type : \"NUMERIC\" '(' INT_VAL error",
"field_type : \"NUMERIC\" '(' error",
"field_type : \"NUMERIC\" error",
"field_foreign_key : \"RELATION\" ID \"TO\" ID '.' ID",
"field_foreign_key : \"RELATION\" ID \"TO\" ID '.' error",
"field_foreign_key : \"RELATION\" ID \"TO\" ID error",
"field_foreign_key : \"RELATION\" ID \"TO\" error",
"field_foreign_key : \"RELATION\" ID error",
"field_foreign_key : \"RELATION\" error",
"index_declare_list : index_declare_list ',' index_declare",
"index_declare_list : index_declare",
"index_declare_list : index_declare_list ',' error",
"index_declare : ID '(' index_order_list ')'",
"index_declare : \"UNIQUE\" ID '(' index_order_list ')'",
"index_declare : ID '(' index_order_list error",
"index_declare : ID '(' error",
"index_declare : ID error",
"index_declare : \"UNIQUE\" ID '(' index_order_list error",
"index_declare : \"UNIQUE\" ID '(' error",
"index_declare : \"UNIQUE\" ID error",
"index_declare : \"UNIQUE\" error",
"index_order_list : index_order_list ',' index_order",
"index_order_list : index_order",
"index_order_list : index_order_list ',' error",
"index_order : ID \"ASC\"",
"index_order : ID \"DESC\"",
"index_order : ID error",
"relation_declare_list : relation_declare_list ',' relation_declare",
"relation_declare_list : relation_declare",
"relation_declare_list : relation_declare_list ',' error",
"relation_declare : ID \"TO\" ID \"ON\" condition_expr",
"relation_declare : ID \"TO\" ID \"ON\" error",
"relation_declare : ID \"TO\" ID error",
"relation_declare : ID \"TO\" error",
"relation_declare : ID error",
"hierarchy_declare_list : hierarchy_declare_list ',' hierarchy_declare",
"hierarchy_declare_list : hierarchy_declare",
"hierarchy_declare_list : hierarchy_declare_list ',' error",
"hierarchy_declare : ID \"MAXLEVEL\" '(' INT_VAL ')'",
"hierarchy_declare : ID \"MAXLEVEL\" '(' INT_VAL error",
"hierarchy_declare : ID \"MAXLEVEL\" '(' error",
"hierarchy_declare : ID \"MAXLEVEL\" error",
"hierarchy_declare : ID error",
"partition_field_list : partition_field_list ',' ID",
"partition_field_list : ID",
"partition_field_list : partition_field_list ',' error",
"procedure_declare : \"DEFINE\" \"PROCEDURE\" ID '(' param_declare_list_op ')' \"BEGIN\" statement_list \"END\"",
"procedure_declare : \"DEFINE\" \"PROCEDURE\" ID '(' param_declare_list_op ')' \"BEGIN\" statement_list error",
"procedure_declare : \"DEFINE\" \"PROCEDURE\" ID '(' param_declare_list_op ')' \"BEGIN\" error",
"procedure_declare : \"DEFINE\" \"PROCEDURE\" ID '(' param_declare_list_op ')' error",
"procedure_declare : \"DEFINE\" \"PROCEDURE\" ID '(' error",
"procedure_declare : \"DEFINE\" \"PROCEDURE\" ID error",
"procedure_declare : \"DEFINE\" \"PROCEDURE\" error",
"statement_list : statement_list statement",
"statement_list : statement",
"statement : insert_stmt ';'",
"statement : update_stmt ';'",
"statement : delete_stmt ';'",
"statement : query_declare",
"statement : table_declare",
"statement : var_stmt ';'",
"statement : assign_stmt ';'",
"statement : if_stmt",
"statement : while_stmt",
"statement : loop_stmt",
"statement : foreach_stmt",
"statement : break ';'",
"statement : print ';'",
"statement : return ';'",
"statement : segment",
"statement : insert_stmt error",
"statement : update_stmt error",
"statement : delete_stmt error",
"statement : var_stmt error",
"statement : assign_stmt error",
"statement : break error",
"statement : print error",
"statement : return error",
"segment : \"BEGIN\" statement_list \"END\"",
"segment : \"BEGIN\" statement_list error",
"segment : \"BEGIN\" error",
"var_stmt : \"VAR\" VAR_REF var_type",
"var_stmt : \"VAR\" VAR_REF var_type '=' value_expr",
"var_stmt : \"VAR\" VAR_REF var_type '=' error",
"var_stmt : \"VAR\" VAR_REF error",
"var_stmt : \"VAR\" error",
"var_type : \"BOOLEAN\"",
"var_type : \"BYTE\"",
"var_type : \"BYTES\"",
"var_type : \"DATE\"",
"var_type : \"DOUBLE\"",
"var_type : \"ENUM\" '<' class_name '>'",
"var_type : \"FLOAT\"",
"var_type : \"GUID\"",
"var_type : \"INT\"",
"var_type : \"LONG\"",
"var_type : \"SHORT\"",
"var_type : \"STRING\"",
"var_type : \"ENUM\" '<' class_name error",
"var_type : \"ENUM\" '<' error",
"var_type : \"ENUM\" error",
"assign_stmt : VAR_REF '=' value_expr",
"assign_stmt : '(' primary_ref_list ')' '=' '(' value_list ')'",
"assign_stmt : VAR_REF '=' query_stmt",
"assign_stmt : '(' primary_ref_list ')' '=' query_stmt",
"assign_stmt : VAR_REF '=' error",
"assign_stmt : '(' primary_ref_list ')' '=' '(' value_list error",
"assign_stmt : '(' primary_ref_list ')' '=' '(' error",
"assign_stmt : '(' primary_ref_list ')' '=' error",
"assign_stmt : '(' primary_ref_list '='",
"assign_stmt : primary_ref_list ')' '='",
"primary_ref_list : primary_ref_list ',' VAR_REF",
"primary_ref_list : VAR_REF",
"primary_ref_list : primary_ref_list ',' error",
"primary_ref_list : primary_ref_list VAR_REF",
"if_stmt : IF condition_expr \"THEN\" statement",
"if_stmt : IF condition_expr \"THEN\" statement ELSE statement",
"if_stmt : IF condition_expr \"THEN\" statement ELSE error",
"if_stmt : IF condition_expr \"THEN\" error",
"if_stmt : IF condition_expr error",
"if_stmt : IF error",
"while_stmt : \"WHILE\" condition_expr \"LOOP\" statement",
"while_stmt : \"WHILE\" condition_expr \"LOOP\" error",
"while_stmt : \"WHILE\" condition_expr error",
"while_stmt : \"WHILE\" error",
"loop_stmt : \"LOOP\" statement",
"loop_stmt : \"LOOP\" error",
"foreach_stmt : \"FOREACH\" VAR_REF \"IN\" '(' query_stmt ')' \"LOOP\" statement",
"foreach_stmt : \"FOREACH\" VAR_REF \"IN\" query_invoke \"LOOP\" statement",
"foreach_stmt : \"FOREACH\" VAR_REF \"IN\" '(' query_stmt ')' \"LOOP\" error",
"foreach_stmt : \"FOREACH\" VAR_REF \"IN\" '(' query_stmt ')' error",
"foreach_stmt : \"FOREACH\" VAR_REF \"IN\" '(' query_stmt error",
"foreach_stmt : \"FOREACH\" VAR_REF \"IN\" '(' error",
"foreach_stmt : \"FOREACH\" VAR_REF \"IN\" error",
"foreach_stmt : \"FOREACH\" VAR_REF error",
"foreach_stmt : \"FOREACH\" error",
"break : \"BREAK\"",
"print : \"PRINT\" value_expr",
"print : \"PRINT\" error",
"return : \"RETURN\"",
"return : \"RETURN\" value_expr",
"function_declare : \"DEFINE\" \"FUNCTION\" ID '(' param_declare_list_op ')' param_type \"BEGIN\" statement_list \"END\"",
"function_declare : \"DEFINE\" \"FUNCTION\" ID '(' param_declare_list_op ')' param_type \"BEGIN\" statement_list error",
"function_declare : \"DEFINE\" \"FUNCTION\" ID '(' param_declare_list_op ')' param_type \"BEGIN\" error",
"function_declare : \"DEFINE\" \"FUNCTION\" ID '(' param_declare_list_op ')' param_type error",
"function_declare : \"DEFINE\" \"FUNCTION\" ID '(' param_declare_list_op ')' error",
"function_declare : \"DEFINE\" \"FUNCTION\" ID '(' error",
"function_declare : \"DEFINE\" \"FUNCTION\" ID error",
"function_declare : \"DEFINE\" \"FUNCTION\" error",
};

//#line 3236 "dnasql.y"


private SQLLexer lexer;
private SQLOutput out;
private boolean hasError;
private int token;
private DefineHolderImpl holder;
private ContextImpl<?, ?, ?> context;

private void yyerror(String s) {
	this.hasError = true;
	Token t = (Token) this.yylval;
	if (t != null) {
		this.out.raise(new SQLSyntaxException(t.line, t.col, "在符号"
				+ yyname[this.token] + "处发现语法错误"));
	} else {
		this.out.raise(new SQLSyntaxException("语法错误"));
	}
}

private void yyexception(Throwable ex) {
	this.hasError = true;
	if (ex instanceof SQLParseException) {
		this.out.raise((SQLParseException)ex);
	} else {
		throw Utils.tryThrowException(ex);
	}
}

private int yylex() {
	try {
		this.token = this.lexer.read();
	} catch (SQLParseException ex) {
		raise(null, true, ex);
		this.yyval = 0;
		return 0;
	}
    this.yylval = this.lexer.val;
    return this.token;
}

private NLiteral neg(NLiteral l) {
	if (l instanceof NLiteralInt) {
		NLiteralInt i = (NLiteralInt) l;
		i.value = -i.value;
	} else if (l instanceof NLiteralLong) {
		NLiteralLong i = (NLiteralLong) l;
		i.value = -i.value;
	} else if (l instanceof NLiteralDouble) {
		NLiteralDouble i = (NLiteralDouble) l;
		i.value = -i.value;
	} else {
		raise(l, true, new SQLSyntaxException("该类型的字面量不支持负值"));
	}
	return l;
}

@SuppressWarnings("unchecked")
private <T extends NStatement> T openSQL(Class<T> statementClass, DNASqlType type, String name) {
	if (this.holder != null) {
		T stmt = this.holder.find(statementClass, name);
		if (stmt == null) {
			throw new SQLNotSupportedException("找不到D&A Sql定义[" + name + "]");
		}
		return stmt;
	} else if (this.context != null) {
		Reader reader = context.occorAt.openDeclareScriptReader(name, type);
		SQLScript s = new SQLParser().parse(new SQLLexer(reader),
				this.out, null, this.context);
		if (s == null) {
			throw new SQLNotSupportedException("无法解析D&A Sql[" + name + "]");
		}
		Object stmt = s.content(null);
		if (stmt == null) {
			throw new SQLNotSupportedException("找不到D&A Sql定义[" + name + "]");
		}
		if (!statementClass.isInstance(stmt)) {
			throw new SQLSyntaxException("D&A Sql定义[" + name + "]不是[" + type + "]类型");
		}
		return (T)stmt;
	}
	throw new SQLNotSupportedException("当前环境不支持读取D&A Sql");
}

private void after(Object obj, SQLParseException ex) {
	raise(obj, false, ex);
}

private void at(Object obj, SQLParseException ex) {
	raise(obj, true, ex);
}

private void raise(Object obj, boolean startOrEnd, SQLParseException ex) {
	this.hasError = true;
	this.yyval = null;
	if (obj != null && obj instanceof TextLocalizable) {
		TextLocalizable n = (TextLocalizable)obj;
		if (startOrEnd) {
			ex.line = n.startLine();
			ex.col = n.startCol();
		} else {
			ex.line = n.endLine();
			ex.col = n.endCol();
		}
	}
	if (this.out != null)
		this.out.raise(ex);
}

public SQLScript parse(SQLLexer lex, SQLOutput out, DefineHolderImpl holder, ContextImpl<?, ?, ?> context) {
	this.lexer = lex;
    this.out = out;
    this.holder = holder;
    this.context = context;
    this.yydebug = false;
	this.hasError = false;
	if (this.yyparse() == 0 && !this.hasError) {
		return (SQLScript)this.yyval;
	}
	return null;
}

public SQLScript parse(SQLLexer lex, DefineHolderImpl holder, ContextImpl<?, ?, ?> context) {
	return this.parse(lex, SQLOutput.PRINT_TO_CONSOLE, holder, context);
}
//#line 2873 "SQLParser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
		//reduce on error
       yyn = yyrindex[yystate];
       if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
			yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE) {
	      if (yydebug) debug("reduce (error)");
         yyn = yytable[yyn];
         doaction=true;
         break;
       }
       if (yyerrflag==0) {
          // 判断是否存在error规则
          yyn = yysindex[state_peek(0)];
          if ((yyn == 0) || (yyn += YYERRCODE) < 0 ||
				yyn > YYTABLESIZE || yycheck[yyn] != YYERRCODE) {
             yyerror("syntax error");
          }
          yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 32 "dnasql.y"
{
			yyval = new SQLScript((NStatement)val_peek(0), this.out);
		}
break;
case 2:
//#line 38 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 3:
//#line 39 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 4:
//#line 40 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 5:
//#line 41 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 6:
//#line 42 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 7:
//#line 43 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 8:
//#line 44 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 9:
//#line 45 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 10:
//#line 46 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 11:
//#line 50 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("无法识别的声明语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 12:
//#line 59 "dnasql.y"
{
			yyval = new NParamDeclare(NParamDeclare.InOut.IN, (TString)val_peek(3),
					(NDataType)val_peek(2), true, (NLiteral)val_peek(0));
		}
break;
case 13:
//#line 63 "dnasql.y"
{
			yyval = new NParamDeclare(NParamDeclare.InOut.IN, (TString)val_peek(3),
					(NDataType)val_peek(2), true, (NLiteral)val_peek(1));
		}
break;
case 14:
//#line 67 "dnasql.y"
{
			yyval = new NParamDeclare(NParamDeclare.InOut.IN, (TString)val_peek(2),
					(NDataType)val_peek(1), false, (NLiteral)val_peek(0));
		}
break;
case 15:
//#line 71 "dnasql.y"
{
			yyval = new NParamDeclare(NParamDeclare.InOut.IN, (TString)val_peek(2),
					(NDataType)val_peek(1), true, null);
		}
break;
case 16:
//#line 75 "dnasql.y"
{
			yyval = new NParamDeclare(NParamDeclare.InOut.IN, (TString)val_peek(1),
					(NDataType)val_peek(0), false, null);
		}
break;
case 17:
//#line 79 "dnasql.y"
{
			yyval = new NParamDeclare(NParamDeclare.InOut.INOUT, (TString)val_peek(3),
					(NDataType)val_peek(2), true, (NLiteral)val_peek(0));
		}
break;
case 18:
//#line 83 "dnasql.y"
{
			yyval = new NParamDeclare(NParamDeclare.InOut.INOUT, (TString)val_peek(3),
					(NDataType)val_peek(2), true, (NLiteral)val_peek(1));
		}
break;
case 19:
//#line 87 "dnasql.y"
{
			yyval = new NParamDeclare(NParamDeclare.InOut.INOUT, (TString)val_peek(2),
					(NDataType)val_peek(1), false, (NLiteral)val_peek(0));
		}
break;
case 20:
//#line 91 "dnasql.y"
{
			yyval = new NParamDeclare(NParamDeclare.InOut.INOUT, (TString)val_peek(2),
					(NDataType)val_peek(1), true, null);
		}
break;
case 21:
//#line 95 "dnasql.y"
{
			yyval = new NParamDeclare(NParamDeclare.InOut.INOUT, (TString)val_peek(1),
					(NDataType)val_peek(0), false, null);
		}
break;
case 22:
//#line 99 "dnasql.y"
{
			yyval = new NParamDeclare(NParamDeclare.InOut.OUT, (TString)val_peek(1),
					(NDataType)val_peek(0), false, null);
		}
break;
case 23:
//#line 103 "dnasql.y"
{
			yyval = new NParamDeclare(NParamDeclare.InOut.OUT, (TString)val_peek(2),
					(NDataType)val_peek(1), true, null);
		}
break;
case 24:
//#line 108 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少参数类型"));
			yyval = NParamDeclare.EMPTY;
		}
break;
case 25:
//#line 112 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少参数类型"));
			yyval = NParamDeclare.EMPTY;
		}
break;
case 26:
//#line 116 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少参数类型"));
			yyval = NParamDeclare.EMPTY;
		}
break;
case 28:
//#line 125 "dnasql.y"
{ after(val_peek(1), new SQLTokenNotFoundException("NULL")); }
break;
case 29:
//#line 126 "dnasql.y"
{ after(val_peek(0), new SQLTokenNotFoundException("NOT")); }
break;
case 30:
//#line 130 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 31:
//#line 131 "dnasql.y"
{ yyval = this.neg((NLiteral)val_peek(0)); }
break;
case 32:
//#line 133 "dnasql.y"
{ after(val_peek(1), new SQLSyntaxException("缺少默认值")); }
break;
case 33:
//#line 134 "dnasql.y"
{ after(val_peek(0), new SQLTokenNotFoundException("DEFAULT")); }
break;
case 34:
//#line 138 "dnasql.y"
{ yyval = NDataType.BOOLEAN; }
break;
case 35:
//#line 139 "dnasql.y"
{ yyval = NDataType.BYTE; }
break;
case 36:
//#line 140 "dnasql.y"
{ yyval = NDataType.BYTES; }
break;
case 37:
//#line 141 "dnasql.y"
{ yyval = NDataType.DATE; }
break;
case 38:
//#line 142 "dnasql.y"
{ yyval = NDataType.DOUBLE; }
break;
case 39:
//#line 143 "dnasql.y"
{ yyval = NDataType.ENUM((String)val_peek(1)); }
break;
case 40:
//#line 144 "dnasql.y"
{ yyval = NDataType.FLOAT; }
break;
case 41:
//#line 145 "dnasql.y"
{ yyval = NDataType.GUID; }
break;
case 42:
//#line 146 "dnasql.y"
{ yyval = NDataType.INT; }
break;
case 43:
//#line 147 "dnasql.y"
{ yyval = NDataType.LONG; }
break;
case 44:
//#line 148 "dnasql.y"
{ yyval = NDataType.SHORT; }
break;
case 45:
//#line 149 "dnasql.y"
{ yyval = NDataType.STRING; }
break;
case 46:
//#line 150 "dnasql.y"
{ yyval = NDataType.RECORDSET; }
break;
case 47:
//#line 152 "dnasql.y"
{
			at(val_peek(2), new SQLTokenNotFoundException(">"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 48:
//#line 156 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("无法识别的类名"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 49:
//#line 160 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("<"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 50:
//#line 167 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 51:
//#line 172 "dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 52:
//#line 178 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少参数定义"));
			yyval = val_peek(2);
		}
break;
case 53:
//#line 185 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 54:
//#line 186 "dnasql.y"
{ yyval = null; }
break;
case 55:
//#line 192 "dnasql.y"
{ yyval = new NColumnRefExpr((TString)val_peek(0), (TString)val_peek(2)); }
break;
case 56:
//#line 194 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字段名"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 57:
//#line 201 "dnasql.y"
{ yyval = new NNameRef((TString)val_peek(0)); }
break;
case 58:
//#line 207 "dnasql.y"
{ yyval = new NLiteralString((TString)val_peek(0)); }
break;
case 59:
//#line 208 "dnasql.y"
{ yyval = new NLiteralInt((TInt)val_peek(0)); }
break;
case 60:
//#line 209 "dnasql.y"
{yyval = new NLiteralLong((TLong)val_peek(0)); }
break;
case 61:
//#line 210 "dnasql.y"
{yyval = new NLiteralDouble((TDouble)val_peek(0)); }
break;
case 62:
//#line 211 "dnasql.y"
{ yyval = new NLiteralBoolean((TBoolean)val_peek(0)); }
break;
case 63:
//#line 212 "dnasql.y"
{ yyval = new NLiteralBoolean((TBoolean)val_peek(0)); }
break;
case 64:
//#line 213 "dnasql.y"
{
			try {
				yyval = new NLiteralDate((TString)val_peek(0));
			} catch (SQLValueFormatException ex) {
				at(null, ex);
				yyval = NLiteralDate.EMPTY;
			}
		}
break;
case 65:
//#line 221 "dnasql.y"
{
			try {
				yyval = new NLiteralGUID((TString)val_peek(0));
			} catch (SQLValueFormatException ex) {
				at(null, ex);
				yyval = NLiteralGUID.EMPTY;
			}
		}
break;
case 66:
//#line 229 "dnasql.y"
{
			try {
				yyval = new NLiteralBytes((TString)val_peek(0));
			} catch (SQLValueFormatException ex) {
				at(null, ex);
				yyval = NLiteralBytes.EMPTY;
			}
		}
break;
case 67:
//#line 238 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少日期字符串"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 68:
//#line 242 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少GUID字符串"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 69:
//#line 246 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少BYTES字符串"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 70:
//#line 253 "dnasql.y"
{
			yyval = new NLogicalExpr(NLogicalExpr.Operator.OR, (NConditionExpr)val_peek(2), (NConditionExpr)val_peek(0));
		}
break;
case 71:
//#line 256 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 72:
//#line 258 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少条件表达式"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 73:
//#line 265 "dnasql.y"
{
			yyval = new NLogicalExpr(NLogicalExpr.Operator.AND, (NConditionExpr)val_peek(2), (NConditionExpr)val_peek(0));
		}
break;
case 74:
//#line 268 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 75:
//#line 270 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少条件表达式"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 76:
//#line 277 "dnasql.y"
{
			yyval = new NLogicalExpr(NLogicalExpr.Operator.NOT, (NConditionExpr)val_peek(0), null); 
		}
break;
case 77:
//#line 280 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 78:
//#line 282 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少条件表达式"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 79:
//#line 289 "dnasql.y"
{ yyval = val_peek(1); }
break;
case 80:
//#line 290 "dnasql.y"
{
			TValueCompare op = (TValueCompare)val_peek(1);
			yyval = new NCompareExpr(op.value, (NValueExpr)val_peek(2), (NValueExpr)val_peek(0));
		}
break;
case 89:
//#line 303 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 90:
//#line 307 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 91:
//#line 314 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 92:
//#line 315 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 93:
//#line 316 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 94:
//#line 317 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 95:
//#line 318 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 96:
//#line 319 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 97:
//#line 323 "dnasql.y"
{
			yyval = new NBetweenExpr(((TBoolean)val_peek(4)).value, (NValueExpr)val_peek(5), 
				(NValueExpr)val_peek(2), (NValueExpr)val_peek(0));
		}
break;
case 98:
//#line 328 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 99:
//#line 332 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("AND"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 100:
//#line 336 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 101:
//#line 343 "dnasql.y"
{
			yyval = new NLikeExpr((NValueExpr)val_peek(4), (NValueExpr)val_peek(1), (NValueExpr)val_peek(0),
				((TBoolean)val_peek(3)).value);
		}
break;
case 102:
//#line 348 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字符串表达式"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 103:
//#line 355 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 104:
//#line 356 "dnasql.y"
{ yyval = null; }
break;
case 105:
//#line 358 "dnasql.y"
{ after(val_peek(1), new SQLSyntaxException("缺少值表达式")); }
break;
case 106:
//#line 362 "dnasql.y"
{
			TStrCompare t = (TStrCompare)val_peek(1);
			yyval = new NStrCompareExpr(t.value, (NValueExpr)val_peek(3), 
				(NValueExpr)val_peek(0), ((TBoolean)val_peek(2)).value);
		}
break;
case 107:
//#line 368 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字符串表达式"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 108:
//#line 375 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 109:
//#line 376 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 110:
//#line 377 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 111:
//#line 381 "dnasql.y"
{
			yyval = new NInExpr(((TBoolean)val_peek(2)).value, (NValueExpr)val_peek(3), (NInExprParam)val_peek(0));
		}
break;
case 112:
//#line 385 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值列表或者子查询"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 113:
//#line 392 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(1);
			NValueExpr[] arr = l.toArray(new NValueExpr[l.count()]);
			if (arr.length == 1 && arr[0] instanceof NQuerySpecific) {
				yyval = new NInParamSubQuery((Token)val_peek(2), (Token)val_peek(0), (NQuerySpecific)arr[0]);
			} else {
				yyval = new NInParamValueList((Token)val_peek(2), (Token)val_peek(0), arr);
			}
		}
break;
case 114:
//#line 401 "dnasql.y"
{
			yyval = new NInParamSubQuery((Token)val_peek(2), (Token)val_peek(0), (NQuerySpecific)val_peek(1));
		}
break;
case 115:
//#line 405 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NInExprParam.EMPTY;
		}
break;
case 116:
//#line 409 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值列表或者子查询"));
			yyval = NInExprParam.EMPTY;
		}
break;
case 117:
//#line 416 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 118:
//#line 421 "dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 119:
//#line 427 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NInExprParam.EMPTY;
		}
break;
case 120:
//#line 434 "dnasql.y"
{
			yyval = new NIsNullExpr((Token)val_peek(0), ((TBoolean)val_peek(1)).value, (NValueExpr)val_peek(3));
		}
break;
case 121:
//#line 438 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("NULL"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 122:
//#line 445 "dnasql.y"
{ yyval = new TBoolean(true, 0, 0, 0); }
break;
case 123:
//#line 446 "dnasql.y"
{ yyval = new TBoolean(false, 0, 0, 0); }
break;
case 124:
//#line 450 "dnasql.y"
{
			yyval = new NExistsExpr((Token)val_peek(3), (Token)val_peek(0), (NQuerySpecific)val_peek(1));
		}
break;
case 125:
//#line 454 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 126:
//#line 458 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少查询语句"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 127:
//#line 462 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 128:
//#line 469 "dnasql.y"
{
			THierarchy t = (THierarchy)val_peek(3);
			if (t.value != NHierarchyExpr.Keywords.DESCENDANTOF) {
				yyval = new NHierarchyExpr(t.value, (TString)val_peek(4), (TString)val_peek(2),
						(TString)val_peek(0));
			} else {
				yyval = new NDescendantOfExpr((TString)val_peek(4), (TString)val_peek(2),
							(TString)val_peek(0), null, false);
			}
		}
break;
case 129:
//#line 479 "dnasql.y"
{
			THierarchy t = (THierarchy)val_peek(5);
			if (t.value != NHierarchyExpr.Keywords.DESCENDANTOF) {
				at(val_peek(1), new SQLNotSupportedException("只有DESCENDANTOF谓词支持RELATIVE关键字"));
				yyval = NConditionExpr.EMPTY;
			} else {
				yyval = new NDescendantOfExpr((TString)val_peek(6), (TString)val_peek(4), (TString)val_peek(2), (NValueExpr)val_peek(0), false);
			}
		}
break;
case 130:
//#line 488 "dnasql.y"
{
			THierarchy t = (THierarchy)val_peek(5);
			if (t.value != NHierarchyExpr.Keywords.DESCENDANTOF) {
				at(val_peek(1), new SQLNotSupportedException("只有DESCENDANTOF谓词支持RANGE关键字"));
				yyval = NConditionExpr.EMPTY;
			} else {
				yyval = new NDescendantOfExpr((TString)val_peek(6), (TString)val_peek(4), (TString)val_peek(2), (NValueExpr)val_peek(0), true);
			}
		}
break;
case 131:
//#line 497 "dnasql.y"
{
			yyval = new NIsLeafExpr((TString)val_peek(4), (TString)val_peek(0));
		}
break;
case 132:
//#line 501 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 133:
//#line 505 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 134:
//#line 509 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表关系名称"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 135:
//#line 513 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("USING"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 136:
//#line 517 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表别名"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 137:
//#line 521 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表关系名称"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 138:
//#line 525 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("USING"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 139:
//#line 529 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("LEAF"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 140:
//#line 536 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 141:
//#line 537 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 142:
//#line 538 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 143:
//#line 539 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 144:
//#line 543 "dnasql.y"
{
			THierarchy t = (THierarchy)val_peek(7);
			yyval = new NPathExpr(t.value, (TString)val_peek(8), (TString)val_peek(6),
						(TString)val_peek(3), (TString)val_peek(1), null);
		}
break;
case 145:
//#line 548 "dnasql.y"
{
			THierarchy t = (THierarchy)val_peek(9);
			NValueExpr diff = (NValueExpr)val_peek(0);
			if (t.value != NHierarchyExpr.Keywords.ANCESTOROF && diff != null) {
				at(val_peek(1), new SQLNotSupportedException("只有ANCESTOROF谓词支持相对级次"));
				yyval = NPathExpr.EMPTY;
			} else {
				yyval = new NPathExpr(t.value, (TString)val_peek(10), (TString)val_peek(8),
							(TString)val_peek(5), (TString)val_peek(3), diff);
			}
		}
break;
case 146:
//#line 560 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NPathExpr.EMPTY;
		}
break;
case 147:
//#line 564 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NPathExpr.EMPTY;
		}
break;
case 148:
//#line 568 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字段名称"));
			yyval = NPathExpr.EMPTY;
		}
break;
case 149:
//#line 572 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(","));
			yyval = NPathExpr.EMPTY;
		}
break;
case 150:
//#line 576 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字段名称"));
			yyval = NPathExpr.EMPTY;
		}
break;
case 151:
//#line 583 "dnasql.y"
{
			yyval = new NBinaryExpr(NBinaryExpr.Operator.ADD, (NValueExpr)val_peek(2), (NValueExpr)val_peek(0));
		}
break;
case 152:
//#line 586 "dnasql.y"
{
			yyval = new NBinaryExpr(NBinaryExpr.Operator.SUB, (NValueExpr)val_peek(2), (NValueExpr)val_peek(0));
		}
break;
case 153:
//#line 589 "dnasql.y"
{
			yyval = new NBinaryExpr(NBinaryExpr.Operator.COMBINE, (NValueExpr)val_peek(2), (NValueExpr)val_peek(0));
		}
break;
case 154:
//#line 592 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 155:
//#line 594 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 156:
//#line 598 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 157:
//#line 602 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 158:
//#line 609 "dnasql.y"
{
			yyval = new NBinaryExpr(NBinaryExpr.Operator.MUL, (NValueExpr)val_peek(2), (NValueExpr)val_peek(0));
		}
break;
case 159:
//#line 612 "dnasql.y"
{
			yyval = new NBinaryExpr(NBinaryExpr.Operator.DIV, (NValueExpr)val_peek(2), (NValueExpr)val_peek(0));
		}
break;
case 160:
//#line 615 "dnasql.y"
{
			yyval = new NBinaryExpr(NBinaryExpr.Operator.MOD, (NValueExpr)val_peek(2), (NValueExpr)val_peek(0));
		}
break;
case 161:
//#line 618 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 162:
//#line 620 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 163:
//#line 624 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 164:
//#line 631 "dnasql.y"
{
			yyval = new NNegativeExpr((NValueExpr)val_peek(0));
		}
break;
case 165:
//#line 634 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 166:
//#line 636 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 167:
//#line 643 "dnasql.y"
{ yyval = new NVarRefExpr((NVarRefExpr)val_peek(2), (TString)val_peek(0)); }
break;
case 168:
//#line 644 "dnasql.y"
{ yyval = new NVarRefExpr(null, (TString)val_peek(0)); }
break;
case 169:
//#line 648 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 170:
//#line 649 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 171:
//#line 650 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 172:
//#line 651 "dnasql.y"
{ yyval = new NNullExpr((Token)val_peek(0)); }
break;
case 173:
//#line 652 "dnasql.y"
{ yyval = val_peek(1); }
break;
case 174:
//#line 653 "dnasql.y"
{ yyval = val_peek(1); }
break;
case 175:
//#line 654 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 176:
//#line 655 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 177:
//#line 656 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 178:
//#line 657 "dnasql.y"
{ yyval= val_peek(0); }
break;
case 179:
//#line 658 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 180:
//#line 659 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 181:
//#line 661 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 182:
//#line 665 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 183:
//#line 672 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 184:
//#line 673 "dnasql.y"
{
			SetQuantifier q = (SetQuantifier)val_peek(2);
			yyval = new NAggregateExpr(NAggregateExpr.Func.AVG, (Token)val_peek(4), (Token)val_peek(0), (NValueExpr)val_peek(1), q == null ? SetQuantifier.ALL : q);
		}
break;
case 185:
//#line 677 "dnasql.y"
{
			yyval = new NAggregateExpr(NAggregateExpr.Func.MIN, (Token)val_peek(3), (Token)val_peek(0), (NValueExpr)val_peek(1), SetQuantifier.ALL);
		}
break;
case 186:
//#line 680 "dnasql.y"
{
			yyval = new NAggregateExpr(NAggregateExpr.Func.MAX, (Token)val_peek(3), (Token)val_peek(0), (NValueExpr)val_peek(1), SetQuantifier.ALL);
		}
break;
case 187:
//#line 683 "dnasql.y"
{
			SetQuantifier q = (SetQuantifier)val_peek(2);
			yyval = new NAggregateExpr(NAggregateExpr.Func.COUNT, (Token)val_peek(4), (Token)val_peek(0), (NValueExpr)val_peek(1), q == null ? SetQuantifier.ALL : q);
		}
break;
case 188:
//#line 687 "dnasql.y"
{
			yyval = new NAggregateExpr(NAggregateExpr.Func.COUNT, (Token)val_peek(3), (Token)val_peek(0), null, SetQuantifier.ALL);
		}
break;
case 189:
//#line 691 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 190:
//#line 695 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 191:
//#line 699 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 192:
//#line 703 "dnasql.y"
{
			after(val_peek(0), new SQLTokenNotFoundException(")"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 193:
//#line 707 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式或者'*'"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 194:
//#line 711 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NValueExpr.EMPTY;
		}
break;
case 195:
//#line 715 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NValueExpr.EMPTY;
		}
break;
case 196:
//#line 719 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NValueExpr.EMPTY;
		}
break;
case 197:
//#line 726 "dnasql.y"
{
			SetQuantifier q = (SetQuantifier)val_peek(2);
			yyval = new NAggregateExpr(NAggregateExpr.Func.SUM, (Token)val_peek(4), (Token)val_peek(0), (NValueExpr)val_peek(1), q == null ? SetQuantifier.ALL : q);
		}
break;
case 198:
//#line 731 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 199:
//#line 735 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NValueExpr.EMPTY;
		}
break;
case 200:
//#line 742 "dnasql.y"
{
			TString name = (TString)val_peek(3);
			LinkList l = (LinkList)val_peek(1);
			Token end = (Token)val_peek(0);
			NValueExpr[] params = l.toArray(new NValueExpr[l.count()]);
			try {
				SQLFuncSpec func = SQLFuncSpec.valueOf(name.value);
				yyval = new NFunctionExpr(name, end, func, params);
			} catch (IllegalArgumentException ex) {
				UserFunctionImpl uf = (UserFunctionImpl) this.context.occorAt
						.site.findNamedDefine(UserFunctionDefine.class,
						name.value.toUpperCase());
				if (uf != null) {
					yyval = new NFunctionExpr(name,
							end, uf, params);
				} else {
					at(name, new SQLFunctionUndefinedException(name.value));
					yyval = NValueExpr.EMPTY;
				}
			}
		}
break;
case 201:
//#line 763 "dnasql.y"
{
			TString name = (TString)val_peek(2);
			try {
				SQLFuncSpec func = SQLFuncSpec.valueOf(name.value);
				yyval = new NFunctionExpr((Token)val_peek(2), (Token)val_peek(0), func, null);
			} catch (IllegalArgumentException ex) {
				at(val_peek(2), new SQLFunctionUndefinedException(name.value));
				yyval = NValueExpr.EMPTY;
			}
		}
break;
case 202:
//#line 774 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 203:
//#line 778 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 204:
//#line 785 "dnasql.y"
{
			yyval = new NHlvExpr((Token)val_peek(5), (Token)val_peek(0), (TString)val_peek(3), (TString)val_peek(1));
		}
break;
case 205:
//#line 788 "dnasql.y"
{
			yyval = new NHaidExpr((Token)val_peek(5), (Token)val_peek(0), (TString)val_peek(3), (TString)val_peek(1), null, false);
		}
break;
case 206:
//#line 791 "dnasql.y"
{
			yyval = new NHaidExpr((Token)val_peek(7), (Token)val_peek(0), (TString)val_peek(5), (TString)val_peek(3), (NValueExpr)val_peek(1), true);
		}
break;
case 207:
//#line 794 "dnasql.y"
{
			yyval = new NHaidExpr((Token)val_peek(7), (Token)val_peek(0), (TString)val_peek(5), (TString)val_peek(3), (NValueExpr)val_peek(1), false);
		}
break;
case 208:
//#line 798 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 209:
//#line 802 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表关系名称"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 210:
//#line 806 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("."));
			yyval = NValueExpr.EMPTY;
		}
break;
case 211:
//#line 810 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表名称"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 212:
//#line 814 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NValueExpr.EMPTY;
		}
break;
case 213:
//#line 818 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 214:
//#line 822 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 215:
//#line 826 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少整型表达式"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 216:
//#line 830 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少整型表达式"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 217:
//#line 834 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 218:
//#line 838 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表关系名称"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 219:
//#line 842 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("."));
			yyval = NValueExpr.EMPTY;
		}
break;
case 220:
//#line 846 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表名称"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 221:
//#line 850 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NValueExpr.EMPTY;
		}
break;
case 222:
//#line 857 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(1);
			NValueExpr[] params = l.toArray(new NValueExpr[l.count()]);
			yyval = new NCoalesceExpr((Token)val_peek(3), (Token)val_peek(0), params);
		}
break;
case 223:
//#line 863 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 224:
//#line 867 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少参数"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 225:
//#line 871 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NValueExpr.EMPTY;
		}
break;
case 226:
//#line 878 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			yyval = new NSimpleCaseExpr((Token)val_peek(4), (Token)val_peek(0), (NValueExpr)val_peek(3),
					l.toArray(new NSimpleCaseWhen[l.count()]), (NValueExpr)val_peek(1));
		}
break;
case 227:
//#line 884 "dnasql.y"
{
			Object obj = val_peek(1);
			if (obj == null) {
				obj = val_peek(2);
			}
			after(obj, new SQLTokenNotFoundException("END"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 228:
//#line 892 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("WHEN"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 229:
//#line 896 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表达式"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 230:
//#line 904 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(1);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 231:
//#line 909 "dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 232:
//#line 917 "dnasql.y"
{
			yyval = new NSimpleCaseWhen((NValueExpr)val_peek(2), (NValueExpr)val_peek(0));
		}
break;
case 233:
//#line 921 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NSimpleCaseWhen.EMPTY;
		}
break;
case 234:
//#line 925 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("THEN"));
			yyval = NSimpleCaseWhen.EMPTY;
		}
break;
case 235:
//#line 929 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表达式"));
			yyval = NSimpleCaseWhen.EMPTY;
		}
break;
case 236:
//#line 936 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 237:
//#line 937 "dnasql.y"
{ yyval = null; }
break;
case 238:
//#line 939 "dnasql.y"
{ after(val_peek(1), new SQLSyntaxException("缺少值表达式")); }
break;
case 239:
//#line 943 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			yyval = new NSearchedCaseExpr((Token)val_peek(3), (Token)val_peek(0),
					l.toArray(new NSearchedCaseWhen[l.count()]), (NValueExpr)val_peek(1));
		}
break;
case 240:
//#line 949 "dnasql.y"
{
			Object obj = val_peek(1);
			if (obj == null) {
				obj = val_peek(2);
			}
			after(obj, new SQLTokenNotFoundException("END"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 241:
//#line 960 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(1);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 242:
//#line 965 "dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 243:
//#line 973 "dnasql.y"
{
			yyval = new NSearchedCaseWhen((NConditionExpr)val_peek(2), (NValueExpr)val_peek(0));
		}
break;
case 244:
//#line 977 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NSearchedCaseWhen.EMPTY;
		}
break;
case 245:
//#line 981 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("THEN"));
			yyval = NSearchedCaseWhen.EMPTY;
		}
break;
case 246:
//#line 988 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 247:
//#line 993 "dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 248:
//#line 999 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = val_peek(2);
		}
break;
case 249:
//#line 1008 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(1);
			yyval = new NQueryInvoke((Token)val_peek(0), (TString)val_peek(3), l.toArray(new NValueExpr[l.count()]));
		}
break;
case 250:
//#line 1012 "dnasql.y"
{
			yyval = new NQueryInvoke((Token)val_peek(0), (TString)val_peek(2), null);
		}
break;
case 251:
//#line 1018 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(4);
			yyval = new NQueryDeclare((Token)val_peek(8), (Token)val_peek(0), (TString)val_peek(6),
					l == null ? null : l.toArray(new NParamDeclare[l.count()]),
					(NQueryStmt)val_peek(1));
		}
break;
case 252:
//#line 1024 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(5);
			yyval = new NQueryDeclare((Token)val_peek(9), (Token)val_peek(0), (TString)val_peek(7),
					l == null ? null : l.toArray(new NParamDeclare[l.count()]),
					(NQueryStmt)val_peek(2));
		}
break;
case 253:
//#line 1032 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("END"));
			yyval = NStatement.EMPTY;
		}
break;
case 254:
//#line 1037 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少查询语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 255:
//#line 1041 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("BEGIN"));
			yyval = NStatement.EMPTY;
		}
break;
case 256:
//#line 1045 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NStatement.EMPTY;
		}
break;
case 257:
//#line 1049 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NStatement.EMPTY;
		}
break;
case 258:
//#line 1053 "dnasql.y"
{
			at(val_peek(1), new SQLSyntaxException("缺少名称"));
			yyval = NStatement.EMPTY;
		}
break;
case 259:
//#line 1060 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			NQueryWith[] arr = l.toArray(new NQueryWith[l.count()]);
			yyval = new NQueryStmt((Token)val_peek(3), arr, (NQuerySpecific)val_peek(1), (NOrderBy)val_peek(0));
		}
break;
case 260:
//#line 1065 "dnasql.y"
{
			yyval = new NQueryStmt(null, null, (NQuerySpecific)val_peek(1), (NOrderBy)val_peek(0));
		}
break;
case 261:
//#line 1069 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少SELECT语句"));
			yyval = NQueryStmt.EMPTY;
		}
break;
case 262:
//#line 1073 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少子查询或者'('"));
			yyval = NQueryStmt.EMPTY;
		}
break;
case 263:
//#line 1080 "dnasql.y"
{
			NQuerySpecific s = (NQuerySpecific)val_peek(2);
			s.union((NQuerySpecific)val_peek(0), false);
			yyval = s;
		}
break;
case 264:
//#line 1085 "dnasql.y"
{
			NQuerySpecific s = (NQuerySpecific)val_peek(3);
			s.union((NQuerySpecific)val_peek(0), true);
			yyval = s;
		}
break;
case 265:
//#line 1090 "dnasql.y"
{
			NQuerySpecific s = (NQuerySpecific)val_peek(2);
			s.union((NQuerySpecific)val_peek(0), false);
			yyval = s;
		}
break;
case 266:
//#line 1095 "dnasql.y"
{
			NQuerySpecific s = (NQuerySpecific)val_peek(3);
			s.union((NQuerySpecific)val_peek(0), true);
			yyval = s;
		}
break;
case 267:
//#line 1100 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 268:
//#line 1102 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少SELECT语句"));
			yyval = val_peek(2);
		}
break;
case 269:
//#line 1106 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少SELECT语句"));
			yyval = val_peek(2);
		}
break;
case 270:
//#line 1113 "dnasql.y"
{ yyval = val_peek(1); }
break;
case 271:
//#line 1115 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = val_peek(1);
		}
break;
case 272:
//#line 1119 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少SELECT语句"));
			yyval = NQuerySpecific.EMPTY;
		}
break;
case 273:
//#line 1126 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 274:
//#line 1127 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 275:
//#line 1131 "dnasql.y"
{
			NWhere w = (NWhere)val_peek(2);
			if (w != null && w.cursor != null) {
				at(w, new SQLNotSupportedException("查询语句中不能使用WHERE CURRENT OF子句"));
				yyval = NQuerySpecific.EMPTY;
			} else {
				yyval = new NQuerySpecific((NSelect)val_peek(4), (NFrom)val_peek(3), w,
							(NGroupBy)val_peek(1), (NHaving)val_peek(0));
			}
		}
break;
case 276:
//#line 1142 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("FROM"));
			yyval = NQuerySpecific.EMPTY;
		}
break;
case 277:
//#line 1149 "dnasql.y"
{ yyval = val_peek(1); }
break;
case 278:
//#line 1153 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 279:
//#line 1158 "dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 280:
//#line 1164 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少子查询"));
			yyval = val_peek(2);
		}
break;
case 281:
//#line 1171 "dnasql.y"
{
			yyval = new NQueryWith((Token)val_peek(4), (TString)val_peek(0), (NQuerySpecific)val_peek(3));
		}
break;
case 282:
//#line 1175 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少别名"));
			yyval = NQueryWith.EMPTY;
		}
break;
case 283:
//#line 1179 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("AS"));
			yyval = NQueryWith.EMPTY;
		}
break;
case 284:
//#line 1183 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NQueryWith.EMPTY;
		}
break;
case 285:
//#line 1190 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(0);
			NQueryColumn[] columns = l.toArray(new NQueryColumn[l.count()]);
			yyval = new NSelect((Token)val_peek(2), (SetQuantifier)val_peek(1), columns);
		}
break;
case 286:
//#line 1196 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少列选表达式"));
			yyval = NSelect.EMPTY;
		}
break;
case 287:
//#line 1203 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(0);
			yyval = new NFrom((Token)val_peek(1), l.toArray(new NSource[l.count()]));
		}
break;
case 288:
//#line 1208 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少数据源"));
			yyval = NFrom.EMPTY;
		}
break;
case 289:
//#line 1215 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 290:
//#line 1220 "dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 291:
//#line 1226 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少数据源"));
			yyval = val_peek(2);
		}
break;
case 292:
//#line 1233 "dnasql.y"
{ yyval = new NWhere((Token)val_peek(1), (NConditionExpr)val_peek(0)); }
break;
case 293:
//#line 1234 "dnasql.y"
{ yyval = new NWhere((Token)val_peek(3), (TString)val_peek(0)); }
break;
case 294:
//#line 1235 "dnasql.y"
{ yyval = null; }
break;
case 295:
//#line 1237 "dnasql.y"
{ after(val_peek(1), new SQLSyntaxException("缺少条件表达式")); }
break;
case 296:
//#line 1238 "dnasql.y"
{ after(val_peek(1), new SQLSyntaxException("缺少变量名")); }
break;
case 297:
//#line 1239 "dnasql.y"
{ after(val_peek(1), new SQLTokenNotFoundException("OF")); }
break;
case 298:
//#line 1243 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(0);
			NValueExpr[] columns = l.toArray(new NValueExpr[l.count()]);
			yyval = new NGroupBy((Token)val_peek(2), null, columns, GroupByType.DEFAULT);
		}
break;
case 299:
//#line 1248 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			NValueExpr[] columns = l.toArray(new NValueExpr[l.count()]);
			yyval = new NGroupBy((Token)val_peek(4), (Token)val_peek(0), columns, GroupByType.ROLL_UP);
		}
break;
case 300:
//#line 1253 "dnasql.y"
{ yyval = null; }
break;
case 301:
//#line 1255 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("ROLLUP"));
		}
break;
case 302:
//#line 1258 "dnasql.y"
{ after(val_peek(1), new SQLSyntaxException("缺少列表达式")); }
break;
case 303:
//#line 1259 "dnasql.y"
{ after(val_peek(1), new SQLTokenNotFoundException("BY")); }
break;
case 304:
//#line 1263 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 305:
//#line 1268 "dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 306:
//#line 1274 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少列表达式"));
			yyval = val_peek(2);
		}
break;
case 307:
//#line 1281 "dnasql.y"
{ yyval = new NHaving((Token)val_peek(1), (NConditionExpr)val_peek(0)); }
break;
case 308:
//#line 1282 "dnasql.y"
{ yyval = null; }
break;
case 309:
//#line 1284 "dnasql.y"
{ after(val_peek(1), new SQLSyntaxException("缺少条件表达式")); }
break;
case 310:
//#line 1288 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 311:
//#line 1289 "dnasql.y"
{ yyval = null; }
break;
case 312:
//#line 1293 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(0);
			NOrderByColumn[] columns = l.toArray(new NOrderByColumn[l.count()]);
			yyval = new NOrderBy((Token)val_peek(2), columns);
		}
break;
case 313:
//#line 1300 "dnasql.y"
{ after(val_peek(1), new SQLSyntaxException("缺少列表达式")); }
break;
case 314:
//#line 1302 "dnasql.y"
{ after(val_peek(1), new SQLTokenNotFoundException("BY")); }
break;
case 315:
//#line 1306 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add((NOrderByColumn)val_peek(0));
			yyval = l;
		}
break;
case 316:
//#line 1311 "dnasql.y"
{
			LinkList l = new LinkList();
			l.add((NOrderByColumn)val_peek(0));
			yyval = l;
		}
break;
case 317:
//#line 1319 "dnasql.y"
{
			NValueExpr e = (NValueExpr)val_peek(0);
			yyval = new NOrderByColumn(e, e, true);
		}
break;
case 318:
//#line 1323 "dnasql.y"
{ yyval = new NOrderByColumn((Token)val_peek(0), (NValueExpr)val_peek(1), true); }
break;
case 319:
//#line 1324 "dnasql.y"
{ yyval = new NOrderByColumn((Token)val_peek(0), (NValueExpr)val_peek(1), false); }
break;
case 320:
//#line 1325 "dnasql.y"
{
			TString name = (TString)val_peek(0);
			yyval = new NOrderByColumn(name, name, true);
		}
break;
case 321:
//#line 1329 "dnasql.y"
{ yyval = new NOrderByColumn((Token)val_peek(0), (TString)val_peek(1), true); }
break;
case 322:
//#line 1330 "dnasql.y"
{ yyval = new NOrderByColumn((Token)val_peek(0), (TString)val_peek(1), false); }
break;
case 323:
//#line 1334 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 324:
//#line 1339 "dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 325:
//#line 1345 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少列选表达式"));
			yyval = val_peek(2);
		}
break;
case 326:
//#line 1352 "dnasql.y"
{
			yyval = new NQueryColumn((NValueExpr)val_peek(0), null);
		}
break;
case 327:
//#line 1355 "dnasql.y"
{
			TString alias = (TString)val_peek(0);
			if (alias.value.charAt(0) == '$') {
				at(alias, new SQLSyntaxException("别名不能以$符号开始"));
				yyval = new NQueryColumn((NValueExpr)val_peek(2), null);
			} else {
				yyval = new NQueryColumn((NValueExpr)val_peek(2), alias);
			}
		}
break;
case 328:
//#line 1364 "dnasql.y"
{
			TString alias = (TString)val_peek(0);
			if (alias.value.charAt(0) == '$') {
				at(alias, new SQLSyntaxException("别名不能以$符号开始"));
				yyval = new NQueryColumn((NValueExpr)val_peek(2), null);
			} else {
				yyval = new NQueryColumn((NValueExpr)val_peek(2), alias);
			}
		}
break;
case 329:
//#line 1374 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少别名"));
			yyval = NQueryColumn.EMPTY;
		}
break;
case 330:
//#line 1378 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少别名"));
			yyval = NQueryColumn.EMPTY;
		}
break;
case 331:
//#line 1382 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少别名"));
			yyval = NQueryColumn.EMPTY;
		}
break;
case 332:
//#line 1389 "dnasql.y"
{
			NAggregateExpr sum = (NAggregateExpr)val_peek(6);
			if (!(sum.expr instanceof NColumnRefExpr)) {
				at(sum.expr, new SQLSyntaxException("分析函数SUM的参数必须是字段引用"));
				yyval = null;
			} else {
				LinkList l = (LinkList)val_peek(3);
				NValueExpr[] partitions;
				if (l != null) {
					partitions = l.toArray(new NValueExpr[l.count()]);
				} else {
					partitions = null;
				}
				NOrderBy orders = (NOrderBy)val_peek(2);
				NAnalyticFunctionExpr.NWindowClause w = (NAnalyticFunctionExpr.NWindowClause)val_peek(1);
				if (w == null) {
					yyval = new NAnalyticFunctionExpr(NAnalyticFunctionExpr.Func.SUM, (TextLocalizable)val_peek(6), (Token)val_peek(0), (NColumnRefExpr)sum.expr, partitions, orders.columns, null, null, null);
				} else {
					yyval = new NAnalyticFunctionExpr(NAnalyticFunctionExpr.Func.SUM, (TextLocalizable)val_peek(6), (Token)val_peek(0), (NColumnRefExpr)sum.expr, partitions, orders.columns, w.windowType, w.preceding, w.following);
				}
			}
		}
break;
case 333:
//#line 1411 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			NValueExpr[] partitions;
			if (l != null) {
				partitions = l.toArray(new NValueExpr[l.count()]);
			} else {
				partitions = null;
			}
			NOrderBy orders = (NOrderBy)val_peek(1);
			yyval = new NAnalyticFunctionExpr(NAnalyticFunctionExpr.Func.ROW_NUMBER, (TextLocalizable)val_peek(7), (Token)val_peek(0), partitions, orders.columns);
		}
break;
case 334:
//#line 1422 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			NValueExpr[] partitions;
			if (l != null) {
				partitions = l.toArray(new NValueExpr[l.count()]);
			} else {
				partitions = null;
			}
			NOrderBy orders = (NOrderBy)val_peek(1);
			yyval = new NAnalyticFunctionExpr(NAnalyticFunctionExpr.Func.RANK, (TextLocalizable)val_peek(7), (Token)val_peek(0), partitions, orders.columns);
		}
break;
case 335:
//#line 1433 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			NValueExpr[] partitions;
			if (l != null) {
				partitions = l.toArray(new NValueExpr[l.count()]);
			} else {
				partitions = null;
			}
			NOrderBy orders = (NOrderBy)val_peek(1);
			yyval = new NAnalyticFunctionExpr(NAnalyticFunctionExpr.Func.DENSE_RANK, (TextLocalizable)val_peek(7), (Token)val_peek(0), partitions, orders.columns);
		}
break;
case 336:
//#line 1445 "dnasql.y"
{
			at(val_peek(3), new SQLTokenNotFoundException(")"));
	  		yyval = null;
		}
break;
case 337:
//#line 1449 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少OrderBy子句"));
	  		yyval = null;
		}
break;
case 338:
//#line 1453 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
	  		yyval = null;
		}
break;
case 339:
//#line 1457 "dnasql.y"
{
			at(val_peek(2), new SQLTokenNotFoundException(")"));
	  		yyval = null;
		}
break;
case 340:
//#line 1461 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少OrderBy子句"));
	  		yyval = null;
		}
break;
case 341:
//#line 1465 "dnasql.y"
{
			after(val_peek(0), new SQLTokenNotFoundException("("));
	  		yyval = null;
		}
break;
case 342:
//#line 1469 "dnasql.y"
{
			after(val_peek(0), new SQLTokenNotFoundException("OVER"));
	  		yyval = null;
		}
break;
case 343:
//#line 1473 "dnasql.y"
{
			after(val_peek(0), new SQLTokenNotFoundException(")"));
	  		yyval = null;
		}
break;
case 344:
//#line 1477 "dnasql.y"
{
			after(val_peek(0), new SQLTokenNotFoundException("("));
	  		yyval = null;
		}
break;
case 345:
//#line 1481 "dnasql.y"
{
			at(val_peek(2), new SQLTokenNotFoundException(")"));
	  		yyval = null;
		}
break;
case 346:
//#line 1485 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少OrderBy子句"));
	  		yyval = null;
		}
break;
case 347:
//#line 1489 "dnasql.y"
{
			after(val_peek(0), new SQLTokenNotFoundException("("));
	  		yyval = null;
		}
break;
case 348:
//#line 1493 "dnasql.y"
{
			after(val_peek(0), new SQLTokenNotFoundException("OVER"));
	  		yyval = null;
		}
break;
case 349:
//#line 1497 "dnasql.y"
{
			after(val_peek(0), new SQLTokenNotFoundException(")"));
	  		yyval = null;
		}
break;
case 350:
//#line 1501 "dnasql.y"
{
			after(val_peek(0), new SQLTokenNotFoundException("("));
	  		yyval = null;
		}
break;
case 351:
//#line 1505 "dnasql.y"
{
			at(val_peek(2), new SQLTokenNotFoundException(")"));
	  		yyval = null;
		}
break;
case 352:
//#line 1509 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少OrderBy子句"));
	  		yyval = null;
		}
break;
case 353:
//#line 1513 "dnasql.y"
{
			after(val_peek(0), new SQLTokenNotFoundException("("));
	  		yyval = null;
		}
break;
case 354:
//#line 1517 "dnasql.y"
{
			after(val_peek(0), new SQLTokenNotFoundException("OVER"));
	  		yyval = null;
		}
break;
case 355:
//#line 1521 "dnasql.y"
{
			after(val_peek(0), new SQLTokenNotFoundException(")"));
	  		yyval = null;
		}
break;
case 356:
//#line 1525 "dnasql.y"
{
			after(val_peek(0), new SQLTokenNotFoundException("("));
	  		yyval = null;
		}
break;
case 357:
//#line 1532 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 358:
//#line 1533 "dnasql.y"
{ yyval = null; }
break;
case 359:
//#line 1535 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表达式"));
			yyval = null;
		}
break;
case 360:
//#line 1539 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("BY"));
			yyval = null;
		}
break;
case 361:
//#line 1546 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 362:
//#line 1551 "dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 363:
//#line 1557 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表达式"));
			yyval = val_peek(2);
		}
break;
case 364:
//#line 1564 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 365:
//#line 1565 "dnasql.y"
{ yyval = null; }
break;
case 366:
//#line 1569 "dnasql.y"
{
			yyval = new NAnalyticFunctionExpr.NWindowClause((NAnalyticFunctionExpr.WindowType)val_peek(1), val_peek(0), null);
		}
break;
case 367:
//#line 1572 "dnasql.y"
{
			yyval = new NAnalyticFunctionExpr.NWindowClause((NAnalyticFunctionExpr.WindowType)val_peek(4), val_peek(2), val_peek(0));
		}
break;
case 368:
//#line 1578 "dnasql.y"
{ yyval = NAnalyticFunctionExpr.WindowType.ROWS; }
break;
case 369:
//#line 1579 "dnasql.y"
{ yyval = NAnalyticFunctionExpr.WindowType.RANGE; }
break;
case 370:
//#line 1583 "dnasql.y"
{ yyval = NAnalyticFunctionExpr.UNBOUNDED; }
break;
case 371:
//#line 1584 "dnasql.y"
{ yyval = NAnalyticFunctionExpr.CURRENT_ROW; }
break;
case 372:
//#line 1585 "dnasql.y"
{ yyval = val_peek(1); }
break;
case 373:
//#line 1587 "dnasql.y"
{
  			after(val_peek(1), new SQLTokenNotFoundException("PRECEDING"));
	  		yyval = null;
  		}
break;
case 374:
//#line 1591 "dnasql.y"
{
  			after(val_peek(1), new SQLTokenNotFoundException("ROW"));
	  		yyval = null;
  		}
break;
case 375:
//#line 1595 "dnasql.y"
{
  			after(val_peek(1), new SQLTokenNotFoundException("PRECEDING"));
	  		yyval = null;
  		}
break;
case 376:
//#line 1602 "dnasql.y"
{ yyval = NAnalyticFunctionExpr.UNBOUNDED; }
break;
case 377:
//#line 1603 "dnasql.y"
{ yyval = NAnalyticFunctionExpr.CURRENT_ROW; }
break;
case 378:
//#line 1604 "dnasql.y"
{ yyval = val_peek(1); }
break;
case 379:
//#line 1606 "dnasql.y"
{
  			after(val_peek(1), new SQLTokenNotFoundException("FOLLOWING"));
	  		yyval = null;
  		}
break;
case 380:
//#line 1610 "dnasql.y"
{
  			after(val_peek(1), new SQLTokenNotFoundException("ROW"));
	  		yyval = null;
  		}
break;
case 381:
//#line 1614 "dnasql.y"
{
  			after(val_peek(1), new SQLTokenNotFoundException("FOLLOWING"));
	  		yyval = null;
  		}
break;
case 382:
//#line 1621 "dnasql.y"
{
			yyval = new NSourceJoin((TableJoinType)val_peek(4), (NSource)val_peek(5), (NSource)val_peek(2), (NConditionExpr)val_peek(0));
		}
break;
case 383:
//#line 1624 "dnasql.y"
{
			yyval = new NSourceRelate((TableJoinType)val_peek(4), (NSource)val_peek(5), (TString)val_peek(2), (TString)val_peek(0), null);
		}
break;
case 384:
//#line 1627 "dnasql.y"
{
			TString alias = (TString)val_peek(0);
			if (alias.value.charAt(0) == '$') {
				at(alias, new SQLSyntaxException("别名不能以$符号开始"));
				yyval = new NSourceRelate((TableJoinType)val_peek(6), (NSource)val_peek(7), (TString)val_peek(4), (TString)val_peek(2), null);
			} else {
				yyval = new NSourceRelate((TableJoinType)val_peek(6), (NSource)val_peek(7), (TString)val_peek(4), (TString)val_peek(2), alias);
			}
		}
break;
case 385:
//#line 1636 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 386:
//#line 1638 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少条件表达式"));
			yyval = NSource.EMPTY;
		}
break;
case 387:
//#line 1642 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("ON"));
			yyval = NSource.EMPTY;
		}
break;
case 388:
//#line 1646 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表引用或者子查询"));
			yyval = NSource.EMPTY;
		}
break;
case 389:
//#line 1650 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少别名"));
			yyval = NSource.EMPTY;
		}
break;
case 390:
//#line 1654 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少关系名"));
			yyval = NSource.EMPTY;
		}
break;
case 391:
//#line 1658 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("."));
			yyval = NSource.EMPTY;
		}
break;
case 392:
//#line 1662 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表引用"));
			yyval = NSource.EMPTY;
		}
break;
case 393:
//#line 1669 "dnasql.y"
{
			yyval = new NSourceTable((NNameRef)val_peek(2), null, true);
		}
break;
case 394:
//#line 1672 "dnasql.y"
{
			TString alias = (TString)val_peek(2);
			if (alias.value.charAt(0) == '$') {
				at(alias, new SQLSyntaxException("别名不能以$符号开始"));
				yyval = new NSourceTable((NNameRef)val_peek(4), null, true);
			} else {
				yyval = new NSourceTable((NNameRef)val_peek(4), alias, true);
			}
		}
break;
case 395:
//#line 1681 "dnasql.y"
{
			yyval = new NSourceTable((NNameRef)val_peek(0), null, false);
		}
break;
case 396:
//#line 1684 "dnasql.y"
{
			TString alias = (TString)val_peek(0);
			if (alias.value.charAt(0) == '$') {
				at(alias, new SQLSyntaxException("别名不能以$符号开始"));
				yyval = new NSourceTable((NNameRef)val_peek(2), null, false);
			} else {
				yyval = new NSourceTable((NNameRef)val_peek(2), alias, false);
			}
		}
break;
case 397:
//#line 1693 "dnasql.y"
{
			TString alias = (TString)val_peek(0);
			if (alias.value.charAt(0) == '$') {
				at(alias, new SQLSyntaxException("别名不能以$符号开始"));
				yyval = NSource.EMPTY;
			} else {
				yyval = new NSourceSubQuery((Token)val_peek(4), (NQuerySpecific)val_peek(3), alias);
			}
		}
break;
case 398:
//#line 1702 "dnasql.y"
{ yyval = val_peek(1); }
break;
case 399:
//#line 1704 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("UPDATE"));
			yyval = NSource.EMPTY;
		}
break;
case 400:
//#line 1708 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("UPDATE"));
			yyval = NSource.EMPTY;
		}
break;
case 401:
//#line 1712 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少别名"));
			yyval = NSource.EMPTY;
		}
break;
case 402:
//#line 1716 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少别名"));
			yyval = NSource.EMPTY;
		}
break;
case 403:
//#line 1723 "dnasql.y"
{ yyval = TableJoinType.LEFT; }
break;
case 404:
//#line 1724 "dnasql.y"
{ yyval = TableJoinType.RIGHT; }
break;
case 405:
//#line 1725 "dnasql.y"
{ yyval = TableJoinType.FULL; }
break;
case 406:
//#line 1726 "dnasql.y"
{ yyval = TableJoinType.INNER; }
break;
case 407:
//#line 1730 "dnasql.y"
{ yyval = SetQuantifier.ALL; }
break;
case 408:
//#line 1731 "dnasql.y"
{ yyval = SetQuantifier.DISTINCT; }
break;
case 409:
//#line 1732 "dnasql.y"
{ yyval = null; }
break;
case 410:
//#line 1737 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(6);
			yyval = new NOrmDeclare((Token)val_peek(10), (Token)val_peek(0), (TString)val_peek(8), 
						l == null ? null : l.toArray(new NParamDeclare[l.count()]),
						(String)val_peek(3), (NQueryStmt)val_peek(1));
		}
break;
case 411:
//#line 1744 "dnasql.y"
{
			String base = ((TString)val_peek(5)).value;
			try {
				NOrmDeclare orm = this.openSQL(NOrmDeclare.class,
						DNASqlType.ORM, base);
				NQueryStmt q = orm.body;
				if (q.expr.unions != null) {
					throw new SQLNotSupportedException("不支持重写包含UNION的查询");
				}
				LinkList l = (LinkList)val_peek(8);
				NParamDeclare[] params = l == null ? null : l.toArray(new NParamDeclare[l.count()]);
				NQuerySpecific s = q.expr;
				s = new NQuerySpecific(s.select, s.from, (NWhere)val_peek(3), s.group, (NHaving)val_peek(2));
				yyval = new NOrmOverride((Token)val_peek(12), (Token)val_peek(0), (TString)val_peek(10), 
						params, (TString)val_peek(5), orm.className,
						new NQueryStmt(null, null, s, (NOrderBy)val_peek(1)));
			} catch (SQLParseException ex) {
				at(val_peek(12), ex);
				yyval = NStatement.EMPTY;
			}
		}
break;
case 412:
//#line 1767 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("BEGIN"));
			yyval = NStatement.EMPTY;
		}
break;
case 413:
//#line 1772 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少查询语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 414:
//#line 1776 "dnasql.y"
{
			after(val_peek(2), new SQLTokenNotFoundException("BEGIN"));
			yyval = NStatement.EMPTY;
		}
break;
case 415:
//#line 1780 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少类名"));
			yyval = NStatement.EMPTY;
		}
break;
case 416:
//#line 1784 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("END"));
			yyval = NStatement.EMPTY;
		}
break;
case 417:
//#line 1788 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("BEGIN"));
			yyval = NStatement.EMPTY;
		}
break;
case 418:
//#line 1792 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少ORM名称"));
			yyval = NStatement.EMPTY;
		}
break;
case 419:
//#line 1796 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("MAPPING/OVERRIDE"));
			yyval = NStatement.EMPTY;
		}
break;
case 420:
//#line 1800 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NStatement.EMPTY;
		}
break;
case 421:
//#line 1804 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NStatement.EMPTY;
		}
break;
case 422:
//#line 1808 "dnasql.y"
{
			at(val_peek(1), new SQLSyntaxException("缺少名称"));
			yyval = NStatement.EMPTY;
		}
break;
case 423:
//#line 1815 "dnasql.y"
{
			StringBuilder sb = (StringBuilder)val_peek(0);
			yyval = sb.toString();
		}
break;
case 424:
//#line 1822 "dnasql.y"
{
			StringBuilder sb = (StringBuilder)val_peek(2);
			sb.append(".");
			sb.append(((TString)val_peek(0)).value);
			yyval = sb;
		}
break;
case 425:
//#line 1828 "dnasql.y"
{
			StringBuilder sb = new StringBuilder();
			sb.append(((TString)val_peek(0)).value);
			yyval = sb;
		}
break;
case 426:
//#line 1834 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少包名或类名"));
			yyval = val_peek(2);
		}
break;
case 427:
//#line 1841 "dnasql.y"
{
			TString ref = (TString)val_peek(2);
			if ("#rowcount".equals(ref.value)) {
				yyval = new NReturning((Token)val_peek(3), (TString)val_peek(2), (TString)val_peek(0));
			} else {
				at(ref, new SQLNotSupportedException("只支持#rowcount系统变量"));
			}
		}
break;
case 428:
//#line 1849 "dnasql.y"
{ yyval = null; }
break;
case 429:
//#line 1851 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少变量名称"));
			yyval = null;
		}
break;
case 430:
//#line 1855 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("INTO"));
			yyval = null;
		}
break;
case 431:
//#line 1859 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少系统变量名称"));
			yyval = null;
		}
break;
case 432:
//#line 1868 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(4);
			yyval = new NInsertDeclare((Token)val_peek(8), (Token)val_peek(0), (TString)val_peek(6),
						l == null ? null : l.toArray(new NParamDeclare[l.count()]),
						(NInsertStmt)val_peek(1));
		}
break;
case 433:
//#line 1874 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(5);
			yyval = new NInsertDeclare((Token)val_peek(9), (Token)val_peek(0), (TString)val_peek(7),
						l == null ? null : l.toArray(new NParamDeclare[l.count()]),
						(NInsertStmt)val_peek(2));
		}
break;
case 434:
//#line 1882 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("END"));
			yyval = NStatement.EMPTY;
		}
break;
case 435:
//#line 1887 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少新增语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 436:
//#line 1891 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("BEGIN"));
			yyval = NStatement.EMPTY;
		}
break;
case 437:
//#line 1895 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NStatement.EMPTY;
		}
break;
case 438:
//#line 1899 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NStatement.EMPTY;
		}
break;
case 439:
//#line 1903 "dnasql.y"
{
			at(val_peek(1), new SQLSyntaxException("缺少名称"));
			yyval = NStatement.EMPTY;
		}
break;
case 440:
//#line 1910 "dnasql.y"
{
			yyval = new NInsertStmt((NInsert)val_peek(2), (NInsertSource)val_peek(1), (NReturning)val_peek(0));
		}
break;
case 441:
//#line 1913 "dnasql.y"
{
			yyval = new NInsertStmt((NInsert)val_peek(2), (NInsertSource)val_peek(1), (NReturning)val_peek(0));
		}
break;
case 442:
//#line 1917 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字段列表或者SELECT语句"));
			yyval = NInsertStmt.EMPTY;
		}
break;
case 443:
//#line 1921 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值列表或者子查询"));
			yyval = NInsertStmt.EMPTY;
		}
break;
case 444:
//#line 1928 "dnasql.y"
{
			yyval = new NInsert((Token)val_peek(2), (NNameRef)val_peek(0));
		}
break;
case 445:
//#line 1932 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表名称"));
			yyval = NInsert.EMPTY;
		}
break;
case 446:
//#line 1936 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("INTO"));
			yyval = NInsert.EMPTY;
		}
break;
case 447:
//#line 1943 "dnasql.y"
{
			yyval = new NInsertSubQuery((Token)val_peek(2), (Token)val_peek(0), (NQuerySpecific)val_peek(1));
		}
break;
case 448:
//#line 1947 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NInsertSource.EMPTY;
		}
break;
case 449:
//#line 1954 "dnasql.y"
{
			LinkList column_list = (LinkList)val_peek(5);
			TString[] columns = column_list.toArray(new TString[column_list.count()]);
			LinkList value_list = (LinkList)val_peek(1);
			NValueExpr[] values = value_list.toArray(new NValueExpr[value_list.count()]);
			yyval = new NInsertValues((Token)val_peek(6), (Token)val_peek(0), columns, values);
		}
break;
case 450:
//#line 1962 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NInsertSource.EMPTY;
		}
break;
case 451:
//#line 1966 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值列表"));
			yyval = NInsertSource.EMPTY;
		}
break;
case 452:
//#line 1970 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NInsertSource.EMPTY;
		}
break;
case 453:
//#line 1974 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("VALUES"));
			yyval = NInsertSource.EMPTY;
		}
break;
case 454:
//#line 1978 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NInsertSource.EMPTY;
		}
break;
case 455:
//#line 1985 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 456:
//#line 1990 "dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 457:
//#line 1996 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = val_peek(2);
		}
break;
case 458:
//#line 2003 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 459:
//#line 2008 "dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 460:
//#line 2014 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字段名称"));
			yyval = val_peek(2);
		}
break;
case 461:
//#line 2023 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(4);
			yyval = new NUpdateDeclare((Token)val_peek(8), (Token)val_peek(0), (TString)val_peek(6),
						l == null ? null : l.toArray(new NParamDeclare[l.count()]),
						(NUpdateStmt)val_peek(1));
		}
break;
case 462:
//#line 2029 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(5);
			yyval = new NUpdateDeclare((Token)val_peek(9), (Token)val_peek(0), (TString)val_peek(7),
						l == null ? null : l.toArray(new NParamDeclare[l.count()]),
						(NUpdateStmt)val_peek(2));
		}
break;
case 463:
//#line 2037 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("END"));
			yyval = NStatement.EMPTY;
		}
break;
case 464:
//#line 2042 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少更新语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 465:
//#line 2046 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("BEGIN"));
			yyval = NStatement.EMPTY;
		}
break;
case 466:
//#line 2050 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NStatement.EMPTY;
		}
break;
case 467:
//#line 2054 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NStatement.EMPTY;
		}
break;
case 468:
//#line 2058 "dnasql.y"
{
			at(val_peek(1), new SQLSyntaxException("缺少名称"));
			yyval = NStatement.EMPTY;
		}
break;
case 469:
//#line 2065 "dnasql.y"
{
			yyval = new NUpdateStmt((NUpdate)val_peek(3), (NUpdateSet)val_peek(2), (NWhere)val_peek(1), (NReturning)val_peek(0));
		}
break;
case 470:
//#line 2069 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("SET"));
			yyval = NUpdateStmt.EMPTY;
		}
break;
case 471:
//#line 2076 "dnasql.y"
{
			yyval = new NUpdate((Token)val_peek(1), (NSource)val_peek(0));
		}
break;
case 472:
//#line 2080 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少目标表"));
			yyval = NUpdate.EMPTY;
		}
break;
case 473:
//#line 2087 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(0);
			NUpdateColumnValue[] columns = l.toArray(new NUpdateColumnValue[l.count()]);
			yyval = new NUpdateSet((Token)val_peek(1), columns);
		}
break;
case 474:
//#line 2093 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少赋值表达式"));
			yyval = NUpdateSet.EMPTY;
		}
break;
case 475:
//#line 2100 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 476:
//#line 2105 "dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 477:
//#line 2111 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少赋值表达式"));
			yyval = val_peek(2);
		}
break;
case 478:
//#line 2118 "dnasql.y"
{
			yyval = new NUpdateColumnValue((TString)val_peek(2), (NValueExpr)val_peek(0));
		}
break;
case 479:
//#line 2122 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NUpdateColumnValue.EMPTY;
		}
break;
case 480:
//#line 2126 "dnasql.y"
{
			after(val_peek(0), new SQLTokenNotFoundException("="));
			yyval = NUpdateColumnValue.EMPTY;
		}
break;
case 481:
//#line 2135 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(4);
			yyval = new NDeleteDeclare((Token)val_peek(8), (Token)val_peek(0), (TString)val_peek(6),
						l == null ? null : l.toArray(new NParamDeclare[l.count()]),
						(NDeleteStmt)val_peek(1));
		}
break;
case 482:
//#line 2141 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(5);
			yyval = new NDeleteDeclare((Token)val_peek(9), (Token)val_peek(0), (TString)val_peek(7),
						l == null ? null : l.toArray(new NParamDeclare[l.count()]),
						(NDeleteStmt)val_peek(2));
		}
break;
case 483:
//#line 2149 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("END"));
			yyval = NStatement.EMPTY;
		}
break;
case 484:
//#line 2154 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少删除语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 485:
//#line 2158 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("BEGIN"));
			yyval = NStatement.EMPTY;
		}
break;
case 486:
//#line 2162 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NStatement.EMPTY;
		}
break;
case 487:
//#line 2166 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NStatement.EMPTY;
		}
break;
case 488:
//#line 2170 "dnasql.y"
{
			at(val_peek(1), new SQLSyntaxException("缺少名称"));
			yyval = NStatement.EMPTY;
		}
break;
case 489:
//#line 2177 "dnasql.y"
{
			yyval = new NDeleteStmt((NDelete)val_peek(2), (NWhere)val_peek(1), (NReturning)val_peek(0));
		}
break;
case 490:
//#line 2183 "dnasql.y"
{
			yyval = new NDelete((Token)val_peek(2), (NSource)val_peek(0));
		}
break;
case 491:
//#line 2187 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少目标表"));
			yyval = NDelete.EMPTY;
		}
break;
case 492:
//#line 2191 "dnasql.y"
{
			after(val_peek(0), new SQLTokenNotFoundException("FROM"));
			yyval = NDelete.EMPTY;
		}
break;
case 493:
//#line 2204 "dnasql.y"
{
			yyval = new NTableDeclare((Token)val_peek(11), (Token)val_peek(0), (TString)val_peek(9),
					(NAbstractTableDeclare)val_peek(8), (NTablePrimary)val_peek(6),
					(NTableExtend[])val_peek(5), (NTableIndex[])val_peek(4), (NTableRelation[])val_peek(3),
					(NTableHierarchy[])val_peek(2), (NTablePartition)val_peek(1));
		}
break;
case 494:
//#line 2214 "dnasql.y"
{
			NAbstractTableDeclare base = (NAbstractTableDeclare)val_peek(8);
			NTableExtend[] extend = (NTableExtend[])val_peek(5);
			NTableRelation[] relation = (NTableRelation[])val_peek(3);
			NTableHierarchy[] hierarchy = (NTableHierarchy[])val_peek(2);
			NTablePartition partition = (NTablePartition)val_peek(1);
			/* 暂时仅支持主表字段和索引*/
			Object start = val_peek(12);
			if (base != null) {
				at(start, new SQLNotSupportedException("抽象表不能继承"));
			}
			if (extend != null) {
				at(start, new SQLNotSupportedException("抽象表定义不支持物理表"));
			}
			if (relation != null) {
				at(start, new SQLNotSupportedException("抽象表定义不支持关系"));
			}
			if (hierarchy != null) {
				at(start, new SQLNotSupportedException("抽象表定义不支持级次"));
			}
			if (partition != null) {
				at(start, new SQLNotSupportedException("抽象表定义不支持分区"));
			}
			yyval = new NAbstractTableDeclare((Token)start, (Token)val_peek(0), (TString)val_peek(9),
					base, (NTablePrimary)val_peek(6), extend, (NTableIndex[])val_peek(4),
					relation, hierarchy, partition);
		}
break;
case 495:
//#line 2242 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("END"));
			yyval = NStatement.EMPTY;
		}
break;
case 496:
//#line 2246 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("BEGIN"));
			yyval = NStatement.EMPTY;
		}
break;
case 497:
//#line 2250 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少名称"));
			yyval = NStatement.EMPTY;
		}
break;
case 498:
//#line 2254 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("END"));
			yyval = NStatement.EMPTY;
		}
break;
case 499:
//#line 2258 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("BEGIN"));
			yyval = NStatement.EMPTY;
		}
break;
case 500:
//#line 2262 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少名称"));
			yyval = NStatement.EMPTY;
		}
break;
case 501:
//#line 2266 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("TABLE"));
			yyval = NStatement.EMPTY;
		}
break;
case 502:
//#line 2273 "dnasql.y"
{
			try {
				NAbstractTableDeclare base = this.openSQL(NAbstractTableDeclare.class,
						DNASqlType.ABSTRACT_TABLE, ((TString)val_peek(0)).value);
				yyval = base;
			} catch (SQLParseException ex) {
				at(val_peek(1), ex);
			}
		}
break;
case 503:
//#line 2282 "dnasql.y"
{ yyval = null; }
break;
case 504:
//#line 2284 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少基础表名称"));
		}
break;
case 505:
//#line 2290 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(0);
			NTableField[] arr = l.toArray(new NTableField[l.count()]);
			yyval = new NTablePrimary((Token)val_peek(1), arr);
		}
break;
case 506:
//#line 2296 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字段列表"));
			yyval = NTablePrimary.EMPTY;
		}
break;
case 507:
//#line 2303 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(0);
			yyval = l.toArray(new NTableExtend[l.count()]);
		}
break;
case 508:
//#line 2307 "dnasql.y"
{ yyval = null; }
break;
case 509:
//#line 2311 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(1);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 510:
//#line 2316 "dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 511:
//#line 2324 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(0);
			NTableField[] arr = l.toArray(new NTableField[l.count()]);
			yyval = new NTableExtend((Token)val_peek(3), (TString)val_peek(1), arr);
		}
break;
case 512:
//#line 2330 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字段列表"));
		}
break;
case 513:
//#line 2333 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("ON"));
		}
break;
case 514:
//#line 2339 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(0);
			yyval = l.toArray(new NTableIndex[l.count()]);
		}
break;
case 515:
//#line 2343 "dnasql.y"
{ yyval = null; }
break;
case 516:
//#line 2345 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少索引列表"));
		}
break;
case 517:
//#line 2351 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(0);
			yyval = l.toArray(new NTableRelation[l.count()]);
		}
break;
case 518:
//#line 2355 "dnasql.y"
{ yyval = null; }
break;
case 519:
//#line 2357 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少关系列表"));
		}
break;
case 520:
//#line 2363 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(0);
			yyval = l.toArray(new NTableHierarchy[l.count()]);
		}
break;
case 521:
//#line 2367 "dnasql.y"
{ yyval = null; }
break;
case 522:
//#line 2369 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少级次列表"));
		}
break;
case 523:
//#line 2376 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(5);
			TString[] arr = l.toArray(new TString[l.count()]);
			yyval = new NTablePartition((Token)val_peek(7), arr, (TInt)val_peek(2), (TInt)val_peek(0));
		}
break;
case 524:
//#line 2381 "dnasql.y"
{ yyval = null; }
break;
case 525:
//#line 2383 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少最大分区数目"));
		}
break;
case 526:
//#line 2386 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("MAXCOUNT"));
		}
break;
case 527:
//#line 2389 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少阀值"));
		}
break;
case 528:
//#line 2392 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("VAVLE"));
		}
break;
case 529:
//#line 2395 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
		}
break;
case 530:
//#line 2398 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字段列表"));
		}
break;
case 531:
//#line 2401 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
		}
break;
case 532:
//#line 2407 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 533:
//#line 2412 "dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 534:
//#line 2418 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字段声明"));
			yyval = val_peek(2);
		}
break;
case 535:
//#line 2425 "dnasql.y"
{
			yyval = new NTableField((TString)val_peek(3), (NDataType)val_peek(2),
					((Boolean)val_peek(1)).booleanValue(), (NLiteral)val_peek(0), false);
		}
break;
case 536:
//#line 2429 "dnasql.y"
{
			yyval = new NTableField((TString)val_peek(5), (NDataType)val_peek(4),
					((Boolean)val_peek(3)).booleanValue(), (NLiteral)val_peek(2), true);
		}
break;
case 537:
//#line 2434 "dnasql.y"
{
			yyval = new NTableField((TString)val_peek(4), (NDataType)val_peek(3),
					((Boolean)val_peek(2)).booleanValue(), (NLiteral)val_peek(1), (NTableForeignKey)val_peek(0));
		}
break;
case 538:
//#line 2439 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("KEY"));
			yyval = new NTableField((TString)val_peek(5), (NDataType)val_peek(4),
					((Boolean)val_peek(3)).booleanValue(), (NLiteral)val_peek(2), true);
		}
break;
case 539:
//#line 2444 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字段类型"));
			yyval = NTableField.EMPTY;
		}
break;
case 540:
//#line 2451 "dnasql.y"
{ yyval = Boolean.TRUE; }
break;
case 541:
//#line 2452 "dnasql.y"
{ yyval = Boolean.FALSE; }
break;
case 542:
//#line 2456 "dnasql.y"
{ yyval = val_peek(1); }
break;
case 543:
//#line 2457 "dnasql.y"
{ yyval = this.neg((NLiteral)val_peek(1)); }
break;
case 544:
//#line 2458 "dnasql.y"
{ yyval = null; }
break;
case 545:
//#line 2462 "dnasql.y"
{ yyval = NDataType.BOOLEAN; }
break;
case 546:
//#line 2463 "dnasql.y"
{ yyval = NDataType.DATE; }
break;
case 547:
//#line 2464 "dnasql.y"
{ yyval = NDataType.DOUBLE; }
break;
case 548:
//#line 2465 "dnasql.y"
{ yyval = NDataType.FLOAT; }
break;
case 549:
//#line 2466 "dnasql.y"
{ yyval = NDataType.GUID; }
break;
case 550:
//#line 2467 "dnasql.y"
{ yyval = NDataType.INT; }
break;
case 551:
//#line 2468 "dnasql.y"
{ yyval = NDataType.LONG; }
break;
case 552:
//#line 2469 "dnasql.y"
{ yyval = NDataType.SHORT; }
break;
case 553:
//#line 2470 "dnasql.y"
{ yyval = NDataType.BINARY((TInt)val_peek(1)); }
break;
case 554:
//#line 2471 "dnasql.y"
{ yyval = NDataType.VARBINARY((TInt)val_peek(1)); }
break;
case 555:
//#line 2472 "dnasql.y"
{ yyval = NDataType.BLOB; }
break;
case 556:
//#line 2473 "dnasql.y"
{ yyval = NDataType.CHAR((TInt)val_peek(1)); }
break;
case 557:
//#line 2474 "dnasql.y"
{ yyval = NDataType.VARCHAR((TInt)val_peek(1)); }
break;
case 558:
//#line 2475 "dnasql.y"
{ yyval = NDataType.NCHAR((TInt)val_peek(1)); }
break;
case 559:
//#line 2476 "dnasql.y"
{ yyval = NDataType.NVARCHAR((TInt)val_peek(1)); }
break;
case 560:
//#line 2477 "dnasql.y"
{ yyval = NDataType.TEXT; }
break;
case 561:
//#line 2478 "dnasql.y"
{ yyval = NDataType.NTEXT; }
break;
case 562:
//#line 2479 "dnasql.y"
{
			yyval = NDataType.NUMERIC((TInt)val_peek(3), (TInt)val_peek(1));
		}
break;
case 563:
//#line 2483 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 564:
//#line 2487 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少长度"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 565:
//#line 2491 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NDataType.UNKNOWN;
		}
break;
case 566:
//#line 2495 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 567:
//#line 2499 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少长度"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 568:
//#line 2503 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NDataType.UNKNOWN;
		}
break;
case 569:
//#line 2507 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 570:
//#line 2511 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少长度"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 571:
//#line 2515 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NDataType.UNKNOWN;
		}
break;
case 572:
//#line 2519 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 573:
//#line 2523 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少长度"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 574:
//#line 2527 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NDataType.UNKNOWN;
		}
break;
case 575:
//#line 2531 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 576:
//#line 2535 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少长度"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 577:
//#line 2539 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NDataType.UNKNOWN;
		}
break;
case 578:
//#line 2543 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 579:
//#line 2547 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少长度"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 580:
//#line 2551 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NDataType.UNKNOWN;
		}
break;
case 581:
//#line 2555 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 582:
//#line 2559 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少精度"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 583:
//#line 2563 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(","));
			yyval = NDataType.UNKNOWN;
		}
break;
case 584:
//#line 2567 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少长度"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 585:
//#line 2571 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NDataType.UNKNOWN;
		}
break;
case 586:
//#line 2578 "dnasql.y"
{
		yyval = new NTableForeignKey((TString)val_peek(4), (TString)val_peek(2), (TString)val_peek(0));
	}
break;
case 587:
//#line 2582 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字段名称"));
		}
break;
case 588:
//#line 2585 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("."));
		}
break;
case 589:
//#line 2588 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表名称"));
		}
break;
case 590:
//#line 2591 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("TO"));
		}
break;
case 591:
//#line 2594 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少名称"));
		}
break;
case 592:
//#line 2600 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 593:
//#line 2605 "dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 594:
//#line 2611 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少索引声明"));
			yyval = val_peek(2);
		}
break;
case 595:
//#line 2618 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(1);
			NTableIndexField[] arr = l.toArray(new NTableIndexField[l.count()]);
			TString name = (TString)val_peek(3);
			yyval = new NTableIndex(name, (Token)val_peek(0), name, arr, false);
		}
break;
case 596:
//#line 2624 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(1);
			NTableIndexField[] arr = l.toArray(new NTableIndexField[l.count()]);
			yyval = new NTableIndex((Token)val_peek(4), (Token)val_peek(0), (TString)val_peek(3), arr, true);
		}
break;
case 597:
//#line 2630 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NTableIndex.EMPTY;
		}
break;
case 598:
//#line 2634 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少顺序列表"));
			yyval = NTableIndex.EMPTY;
		}
break;
case 599:
//#line 2638 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NTableIndex.EMPTY;
		}
break;
case 600:
//#line 2642 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NTableIndex.EMPTY;
		}
break;
case 601:
//#line 2646 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少顺序列表"));
			yyval = NTableIndex.EMPTY;
		}
break;
case 602:
//#line 2650 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NTableIndex.EMPTY;
		}
break;
case 603:
//#line 2654 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少名称"));
			yyval = NTableIndex.EMPTY;
		}
break;
case 604:
//#line 2661 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 605:
//#line 2666 "dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 606:
//#line 2672 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少顺序声明"));
			yyval = val_peek(2);
		}
break;
case 607:
//#line 2679 "dnasql.y"
{ yyval = new NTableIndexField((TString)val_peek(1), false); }
break;
case 608:
//#line 2680 "dnasql.y"
{ yyval = new NTableIndexField((TString)val_peek(1), true); }
break;
case 609:
//#line 2682 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("ASC/DESC"));
			yyval = new NTableIndexField((TString)val_peek(1), false);
		}
break;
case 610:
//#line 2689 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 611:
//#line 2694 "dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 612:
//#line 2700 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少关系声明"));
			yyval = val_peek(2);
		}
break;
case 613:
//#line 2707 "dnasql.y"
{
			yyval = new NTableRelation((TString)val_peek(4), (TString)val_peek(2), (NConditionExpr)val_peek(0));
		}
break;
case 614:
//#line 2711 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少条件表达式"));
			yyval = NTableRelation.EMPTY;
		}
break;
case 615:
//#line 2715 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("ON"));
			yyval = NTableRelation.EMPTY;
		}
break;
case 616:
//#line 2719 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表名称"));
			yyval = NTableRelation.EMPTY;
		}
break;
case 617:
//#line 2723 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("TO"));
			yyval = NTableRelation.EMPTY;
		}
break;
case 618:
//#line 2730 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 619:
//#line 2735 "dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 620:
//#line 2741 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少级次声明"));
			yyval = val_peek(2);
		}
break;
case 621:
//#line 2748 "dnasql.y"
{
			yyval = new NTableHierarchy((Token)val_peek(0), (TString)val_peek(4), (TInt)val_peek(1));
		}
break;
case 622:
//#line 2752 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NTableHierarchy.EMPTY;
		}
break;
case 623:
//#line 2756 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少最大级次数目"));
			yyval = NTableHierarchy.EMPTY;
		}
break;
case 624:
//#line 2760 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NTableHierarchy.EMPTY;
		}
break;
case 625:
//#line 2764 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("MAXLEVEL"));
			yyval = NTableHierarchy.EMPTY;
		}
break;
case 626:
//#line 2771 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 627:
//#line 2776 "dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 628:
//#line 2782 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字段名称"));
			yyval = val_peek(2);
		}
break;
case 629:
//#line 2792 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(4);
			NParamDeclare[] params = l == null ? null :
										l.toArray(new NParamDeclare[l.count()]);
			l = (LinkList)val_peek(1);
			NStatement[] stmts = l.toArray(new NStatement[l.count()]);
			yyval = new NProcedureDeclare((Token)val_peek(8), (Token)val_peek(0), (TString)val_peek(6),
						params, stmts);
		}
break;
case 630:
//#line 2803 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("END"));
			yyval = NStatement.EMPTY;
		}
break;
case 631:
//#line 2808 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 632:
//#line 2813 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("BEGIN"));
			yyval = NStatement.EMPTY;
		}
break;
case 633:
//#line 2817 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NStatement.EMPTY;
		}
break;
case 634:
//#line 2821 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NStatement.EMPTY;
		}
break;
case 635:
//#line 2825 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少名称"));
			yyval = NStatement.EMPTY;
		}
break;
case 636:
//#line 2832 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(1);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 637:
//#line 2837 "dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 638:
//#line 2845 "dnasql.y"
{ yyval = val_peek(1); }
break;
case 639:
//#line 2846 "dnasql.y"
{ yyval = val_peek(1); }
break;
case 640:
//#line 2847 "dnasql.y"
{ yyval = val_peek(1); }
break;
case 641:
//#line 2848 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 642:
//#line 2849 "dnasql.y"
{
			NTableDeclare t = (NTableDeclare)val_peek(0);
			try {
				if (t.base != null) {
					throw new SQLNotSupportedException(t.name.line, t.name.col,
							"临时表不支持继承");
				}
				if (t.extend != null) {
					throw new SQLNotSupportedException(t.extend[0].startLine(),
						t.extend[0].startCol(), "临时表不支持物理表");
				}
				if (t.relation != null) {
					throw new SQLNotSupportedException(t.relation[0].startLine(),
						t.relation[0].startCol(), "临时表不支持关系");
				}
				if (t.hierarchy != null) {
					throw new SQLNotSupportedException(t.hierarchy[0].startLine(),
						t.hierarchy[0].startCol(), "临时表不支持级次");
				}
				if (t.partition != null) {
					throw new SQLNotSupportedException(t.partition.startLine(),
						t.partition.startCol(), "临时表不支持分区");
				}
				for (NTableField f : t.primary.fields) {
					if (f.foreignKey != null) {
						throw new SQLNotSupportedException(f.startLine(),
								f.startCol(), "临时表不支持关系");
					}
				}
				yyval = t;
			} catch(SQLParseException ex) {
				raise(null, false, ex);
				yyval = NStatement.EMPTY;
			}
		}
break;
case 643:
//#line 2884 "dnasql.y"
{ yyval = val_peek(1); }
break;
case 644:
//#line 2885 "dnasql.y"
{ yyval = val_peek(1); }
break;
case 645:
//#line 2886 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 646:
//#line 2887 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 647:
//#line 2888 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 648:
//#line 2889 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 649:
//#line 2890 "dnasql.y"
{ yyval = val_peek(1); }
break;
case 650:
//#line 2891 "dnasql.y"
{ yyval = val_peek(1); }
break;
case 651:
//#line 2892 "dnasql.y"
{ yyval = val_peek(1); }
break;
case 652:
//#line 2893 "dnasql.y"
{ yyval = val_peek(0); }
break;
case 653:
//#line 2895 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(";"));
			yyval = val_peek(1);
		}
break;
case 654:
//#line 2899 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(";"));
			yyval = val_peek(1);
		}
break;
case 655:
//#line 2903 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(";"));
			yyval = val_peek(1);
		}
break;
case 656:
//#line 2907 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(";"));
			yyval = val_peek(1);
		}
break;
case 657:
//#line 2911 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(";"));
			yyval = val_peek(1);
		}
break;
case 658:
//#line 2915 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(";"));
			yyval = val_peek(1);
		}
break;
case 659:
//#line 2919 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(";"));
			yyval = val_peek(1);
		}
break;
case 660:
//#line 2923 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(";"));
			yyval = val_peek(1);
		}
break;
case 661:
//#line 2930 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(1);
			yyval = new NSegment((Token)val_peek(2), (Token)val_peek(0), l.toArray(new NStatement[l.count()]));
		}
break;
case 662:
//#line 2935 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(1);
			after(l, new SQLTokenNotFoundException("END"));
			yyval = new NSegment((Token)val_peek(2), l, l.toArray(new NStatement[l.count()]));
		}
break;
case 663:
//#line 2940 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 664:
//#line 2947 "dnasql.y"
{
			yyval = new NVarStmt((Token)val_peek(2), (TString)val_peek(1), (NDataType)val_peek(0), null);
		}
break;
case 665:
//#line 2950 "dnasql.y"
{
			yyval = new NVarStmt((Token)val_peek(4), (TString)val_peek(3), (NDataType)val_peek(2), (NValueExpr)val_peek(0));
		}
break;
case 666:
//#line 2954 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = new NVarStmt((Token)val_peek(4), (TString)val_peek(3), (NDataType)val_peek(2), null);
		}
break;
case 667:
//#line 2958 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少数据类型"));
			yyval = NStatement.EMPTY;
		}
break;
case 668:
//#line 2962 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少变量名称"));
			yyval = NStatement.EMPTY;
		}
break;
case 669:
//#line 2969 "dnasql.y"
{ yyval = NDataType.BOOLEAN; }
break;
case 670:
//#line 2970 "dnasql.y"
{ yyval = NDataType.BYTE; }
break;
case 671:
//#line 2971 "dnasql.y"
{ yyval = NDataType.BYTES; }
break;
case 672:
//#line 2972 "dnasql.y"
{ yyval = NDataType.DATE; }
break;
case 673:
//#line 2973 "dnasql.y"
{ yyval = NDataType.DOUBLE; }
break;
case 674:
//#line 2974 "dnasql.y"
{ yyval = NDataType.ENUM((String)val_peek(1)); }
break;
case 675:
//#line 2975 "dnasql.y"
{ yyval = NDataType.FLOAT; }
break;
case 676:
//#line 2976 "dnasql.y"
{ yyval = NDataType.GUID; }
break;
case 677:
//#line 2977 "dnasql.y"
{ yyval = NDataType.INT; }
break;
case 678:
//#line 2978 "dnasql.y"
{ yyval = NDataType.LONG; }
break;
case 679:
//#line 2979 "dnasql.y"
{ yyval = NDataType.SHORT; }
break;
case 680:
//#line 2980 "dnasql.y"
{ yyval = NDataType.STRING; }
break;
case 681:
//#line 2982 "dnasql.y"
{
			at(val_peek(2), new SQLTokenNotFoundException(">"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 682:
//#line 2986 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("无法识别的类名"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 683:
//#line 2990 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("<"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 684:
//#line 2997 "dnasql.y"
{
			TString ref = (TString)val_peek(2);
			NValueExpr val = (NValueExpr)val_peek(0);
			yyval = new NAssignStmt(ref, val, new TString[] { ref }, new NValueExpr[] { val });
		}
break;
case 685:
//#line 3002 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(5);
			TString[] refs = l.toArray(new TString[l.count()]);
			l = (LinkList)val_peek(1);
			if (refs.length == l.count()) {
				yyval = new NAssignStmt((Token)val_peek(6), (Token)val_peek(0), refs,
							l.toArray(new NValueExpr[l.count()]));
			} else {
				at(val_peek(3), new SQLNotSupportedException("赋值运算符两端操作数个数不同"));
				yyval = NStatement.EMPTY;
			}
		}
break;
case 686:
//#line 3014 "dnasql.y"
{
			TString ref = (TString)val_peek(2);
			yyval = new NAssignStmt(ref, new TString[] { ref }, (NQueryStmt)val_peek(0));
		}
break;
case 687:
//#line 3018 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(3);
			TString[] refs = l.toArray(new TString[l.count()]);
			NQueryStmt q = (NQueryStmt)val_peek(0);
			if (refs.length == q.getMasterSelect().select.columns.length) {
				yyval = new NAssignStmt((Token)val_peek(4), refs, q);
			} else {
				at(val_peek(1), new SQLNotSupportedException("赋值运算符左侧操作数个数与查询输出列个数不同"));
				yyval = NStatement.EMPTY;
			}
		}
break;
case 688:
//#line 3030 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NStatement.EMPTY;
		}
break;
case 689:
//#line 3034 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NStatement.EMPTY;
		}
break;
case 690:
//#line 3038 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值列表"));
			yyval = NStatement.EMPTY;
		}
break;
case 691:
//#line 3042 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值列表或查询"));
			yyval = NStatement.EMPTY;
		}
break;
case 692:
//#line 3046 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NStatement.EMPTY;
		}
break;
case 693:
//#line 3050 "dnasql.y"
{
			at(val_peek(2), new SQLTokenNotFoundException("("));
			yyval = NStatement.EMPTY;
		}
break;
case 694:
//#line 3057 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 695:
//#line 3062 "dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 696:
//#line 3068 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少变量名称"));
			yyval = val_peek(2);
		}
break;
case 697:
//#line 3072 "dnasql.y"
{
			Object l = val_peek(1);
			after(l, new SQLTokenNotFoundException(","));
			yyval = l;
		}
break;
case 698:
//#line 3080 "dnasql.y"
{
			yyval = new NIfStmt((Token)val_peek(3), (NConditionExpr)val_peek(2), (NStatement)val_peek(0), null);
		}
break;
case 699:
//#line 3083 "dnasql.y"
{
			yyval = new NIfStmt((Token)val_peek(5), (NConditionExpr)val_peek(4), (NStatement)val_peek(2), (NStatement)val_peek(0));
		}
break;
case 700:
//#line 3087 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 701:
//#line 3091 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 702:
//#line 3095 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("THEN"));
			yyval = NStatement.EMPTY;
		}
break;
case 703:
//#line 3099 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少谓词表达式"));
			yyval = NStatement.EMPTY;
		}
break;
case 704:
//#line 3106 "dnasql.y"
{
			yyval = new NWhileStmt((Token)val_peek(3), (NConditionExpr)val_peek(2), (NStatement)val_peek(0));
		}
break;
case 705:
//#line 3110 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 706:
//#line 3114 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("LOOP"));
			yyval = NStatement.EMPTY;
		}
break;
case 707:
//#line 3118 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少谓词表达式"));
			yyval = NStatement.EMPTY;
		}
break;
case 708:
//#line 3125 "dnasql.y"
{ yyval = new NLoopStmt((Token)val_peek(1), (NStatement)val_peek(0)); }
break;
case 709:
//#line 3127 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 710:
//#line 3134 "dnasql.y"
{
			yyval = new NForeachStmt((Token)val_peek(7), (TString)val_peek(6), (NQueryStmt)val_peek(3), (NStatement)val_peek(0));
		}
break;
case 711:
//#line 3137 "dnasql.y"
{
			yyval = new NForeachStmt((Token)val_peek(5), (TString)val_peek(4), (NQueryInvoke)val_peek(2), (NStatement)val_peek(0));
		}
break;
case 712:
//#line 3141 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 713:
//#line 3145 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("LOOP"));
			yyval = NStatement.EMPTY;
		}
break;
case 714:
//#line 3149 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NStatement.EMPTY;
		}
break;
case 715:
//#line 3153 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少查询"));
			yyval = NStatement.EMPTY;
		}
break;
case 716:
//#line 3157 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少查询或查询调用"));
			yyval = NStatement.EMPTY;
		}
break;
case 717:
//#line 3161 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("IN"));
			yyval = NStatement.EMPTY;
		}
break;
case 718:
//#line 3165 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少变量名称"));
			yyval = NStatement.EMPTY;
		}
break;
case 719:
//#line 3172 "dnasql.y"
{ yyval = new NBreakStmt((Token)val_peek(0)); }
break;
case 720:
//#line 3176 "dnasql.y"
{ yyval = new NPrintStmt((Token)val_peek(1), (NValueExpr)val_peek(0)); }
break;
case 721:
//#line 3178 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NStatement.EMPTY;
		}
break;
case 722:
//#line 3185 "dnasql.y"
{ yyval = new NReturnStmt((Token)val_peek(0), null); }
break;
case 723:
//#line 3186 "dnasql.y"
{ yyval = new NReturnStmt((Token)val_peek(1), (NValueExpr)val_peek(0)); }
break;
case 724:
//#line 3193 "dnasql.y"
{
			LinkList l = (LinkList)val_peek(5);
			NParamDeclare[] params = l == null ? null :
										l.toArray(new NParamDeclare[l.count()]);
			l = (LinkList)val_peek(1);
			NStatement[] stmts = l.toArray(new NStatement[l.count()]);
			yyval = new NFunctionDeclare((Token)val_peek(9), (Token)val_peek(0), (TString)val_peek(7),
						params, (NDataType)val_peek(3), stmts);
		}
break;
case 725:
//#line 3204 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("END"));
			yyval = NStatement.EMPTY;
		}
break;
case 726:
//#line 3209 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 727:
//#line 3214 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("BEGIN"));
			yyval = NStatement.EMPTY;
		}
break;
case 728:
//#line 3218 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少返回值类型"));
			yyval = NStatement.EMPTY;
		}
break;
case 729:
//#line 3222 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NStatement.EMPTY;
		}
break;
case 730:
//#line 3226 "dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NStatement.EMPTY;
		}
break;
case 731:
//#line 3230 "dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少名称"));
			yyval = NStatement.EMPTY;
		}
break;
//#line 7796 "SQLParser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
//## The -Jnoconstruct option was used ##
//###############################################################



}
//################### END OF CLASS ##############################
