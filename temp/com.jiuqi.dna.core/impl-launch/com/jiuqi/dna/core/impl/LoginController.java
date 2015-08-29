package com.jiuqi.dna.core.impl;

import java.util.List;

import org.apache.cxf.common.util.StringUtils;

import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.spi.application.AppUtil;
import com.jiuqi.dna.core.spi.application.Session;

public class LoginController {
	public static final String EL_LOGIN_CONTROL = "login-control";
	public static final String EL_SESSION_NUM = "session-num";
	public static final String AT_MAX = "max";
	public static final String EL_RESERVED = "reserved";
	public static final String AT_NAME = "name";
	public static final String AT_VALUE = "value";
	public static final String LEVEL_V0 = "V0";
	public static final String LEVEL_V1 = "V1";
	public static final String USER_ADMIN = "admin";
	public static final int MAX_SESSION_NONE = -1;
	
	private int maxSessionNum;
	private String reservedLevelName;
	private int reservedLevelNum;
	private int normalLevelNum;
	
	public LoginController(SXElement dnaServerConfig) {
		parseMaxSessionNum(dnaServerConfig);
		parseReservedSessionNum(dnaServerConfig);
	}
	
	private void parseMaxSessionNum(SXElement dnaServerConfig){
		//定位不到配置信息,则抛异常,控制器为null,不对登录进行限制
		SXElement loginControlElement = dnaServerConfig.firstChild(EL_LOGIN_CONTROL);
		if(null == loginControlElement){this.maxSessionNum = MAX_SESSION_NONE;return;}
		
		SXElement sessionNumElement = loginControlElement.firstChild(EL_SESSION_NUM);
		if(null == sessionNumElement){this.maxSessionNum = MAX_SESSION_NONE;return;}
		
		String max = sessionNumElement.getAttribute(AT_MAX);
		if(null == max){this.maxSessionNum = MAX_SESSION_NONE;return;}
		
		//定位到了,但值配置错误,不是整数,或者<=0，则退出启动过程;改成不对登录进行限制
		int maxSessionNum = 0;
		try{
			maxSessionNum = Integer.parseInt(max);
		}catch(Exception e){
			e.printStackTrace();
//			System.exit(-1);
			this.maxSessionNum = MAX_SESSION_NONE;return;
		}
		
		if(maxSessionNum <= 0){
			System.out.println("dna-server.xml中配置的会话总数必须是正整数!");
//			System.exit(-1);
			this.maxSessionNum = MAX_SESSION_NONE;return;
		}
		
		//定位到了,配置正确,则赋值
		this.maxSessionNum = maxSessionNum;
	}
	
	private void parseReservedSessionNum(SXElement dnaServerConfig){
		//定位不到配置信息,则用默认值
		this.reservedLevelName = null;
		this.reservedLevelNum = 0;
		
		SXElement loginControlElement = dnaServerConfig.firstChild(EL_LOGIN_CONTROL);
		if(null != loginControlElement){
			SXElement sessionNumElement = loginControlElement.firstChild(EL_SESSION_NUM);
			if(null != sessionNumElement){
				SXElement reservedElement = sessionNumElement.firstChild(EL_RESERVED);
				if(null != reservedElement){
					
					//定位到了配置信息,但值配置错误,则退出启动过程;改成不对登录进行限制
					String reservedLevelName = reservedElement.getAttribute(AT_NAME);
					if(null != reservedLevelName){
						if(!LEVEL_V1.equals(reservedLevelName)){
							System.out.println("dna-server.xml中配置的预留级别名称不正确!");
//							System.exit(-1);
							reservedLevelName = null;
						}
					}
					
					int reservedLevelNum = 0;
					String reservedNum = reservedElement.getAttribute(AT_VALUE);
					if(null != reservedNum){
						try{
							reservedLevelNum = Integer.parseInt(reservedNum);
						}catch(Exception e){
							e.printStackTrace();
//							System.exit(-1);
							reservedLevelNum = 0;
						}
						
						if(reservedLevelNum <= 0 || reservedLevelNum > maxSessionNum){
							System.out.println("dna-server.xml中配置的预留会话数必须是正整数，并且不能大于限制会话总数!");
//							System.exit(-1);
							
							reservedLevelNum = 0;
						}
					}
					
					//定位到了配置信息,值配置正确,则赋值
					if(null != this.reservedLevelName && 0 != this.reservedLevelNum){
						this.reservedLevelName = reservedLevelName;
						this.reservedLevelNum = reservedLevelNum;
					}
				}
			}
		}
		
		this.normalLevelNum = this.maxSessionNum - this.reservedLevelNum;
	}
	
	public boolean tryLogin(User _current){
		if(USER_ADMIN.equals(_current.getName())){return true;}
		
		if(this.maxSessionNum == MAX_SESSION_NONE){
			return true;
		}

		int actualLoginNum = 0;												//实际登录Session数,去除内置用户,admin用户
		int actualReservedLoginNum = 0;										//实际登录预留级别的Session数
		int actualNormalLoginNum = 0;										//实际登录的普通Session数
		String _currentLevel = _current.getLevel();							//当前登录用户的级别
		if(StringUtils.isEmpty(_currentLevel)){_currentLevel = LEVEL_V0;}
		
		List<? extends Session> list = AppUtil.getDefaultApp().getNormalSessions();
		actualLoginNum = list.size();
		for(int i=0;i<list.size();i++){
			Session _s = list.get(i);
			User _u = _s.getUser();
			if(_u instanceof BuildInUser || USER_ADMIN.equals(_u.getName())){
				actualLoginNum--;
				continue;								//内置用户不参与计算
			}
			
			String _level = _u.getLevel();
			if(StringUtils.isEmpty(_level)){
				_level = LEVEL_V0;
			}
			
			if(_level.equals(reservedLevelName)){
				actualReservedLoginNum++;
			}
		}
		actualNormalLoginNum = actualLoginNum - actualReservedLoginNum;						
		
		if(actualLoginNum >= maxSessionNum){
			return false;
		}
		
		if(null != this.reservedLevelName && 0 != this.reservedLevelNum){
			if(_currentLevel.equals(reservedLevelName)){							//预留级别用户
				return true;
			}else {																	//普通用户
				if(actualNormalLoginNum < normalLevelNum){
					return true;
				}else {
					return false;
				}
			}
		}
		
		return true;
	}

}
