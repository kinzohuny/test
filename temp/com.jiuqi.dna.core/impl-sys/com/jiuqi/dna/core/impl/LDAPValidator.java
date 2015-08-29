package com.jiuqi.dna.core.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import org.apache.cxf.common.util.StringUtils;
import com.jiuqi.dna.core.misc.SXElement;

final class LDAPValidator {

	LDAPValidator(final SXElement LDAPConfiguration) {
		if (LDAPConfiguration == null) {
			this.LDAPCtxFactoryClassName = DEFAULT_LDAP_CTX_FACTORY_CLASS_NAME;
			this.LDAPServerURL = null;
			this.domain = null;
			this.configuration = null;
			this.authentication = null;
			this.enable = false;
			this.entryNameList = null;
			this.type = null;
			this.userFlag = null;
			this.userSuffix = null;
		} else {
			this.enable = LDAPConfiguration.getBoolean(xml_element_ldap_enable);
			final String configLDAPCtxFactoryClassName = LDAPConfiguration.getString(xml_element_ldap_factory_initial);
			this.LDAPCtxFactoryClassName = configLDAPCtxFactoryClassName == null ? DEFAULT_LDAP_CTX_FACTORY_CLASS_NAME : configLDAPCtxFactoryClassName;
			this.LDAPServerURL = LDAPConfiguration.getString(xml_element_ldap_url);
			this.domain = LDAPConfiguration.getString(xml_element_ldap_domain);
			this.authentication = LDAPConfiguration.getString(xml_element_ldap_authentication);
			this.type = LDAPConfiguration.getString(xml_element_ldap_type);
			this.userFlag = LDAPConfiguration.getString(xml_element_ldap_user_flag);
			this.userSuffix = LDAPConfiguration.getString(xml_element_ldap_user_suffix);
			
			SXElement includeElement = LDAPConfiguration.firstChild(xml_element_ldap_include);
			this.entryNameList = new ArrayList<String>();
			if (includeElement != null) {
				Iterable<SXElement> entryElements = includeElement.getChildren(xml_element_ldap_entry);
				if (entryElements != null) {

					for (SXElement entryElement : entryElements) {
						this.entryNameList.add(entryElement.getText());
					}
				}
			}
			this.configuration = new LDAPConfigurationImplement();
			this.configuration.setEnable(this.enable);
			this.configuration.setEntryNameList(this.entryNameList);
		}
	}

	final LDAPConfigurationImplement configuration;

	private final String LDAPCtxFactoryClassName;

	private final String LDAPServerURL;

	private final String domain;

	private final String authentication;

	private final boolean enable;

	private final List<String> entryNameList;

	private final String type;

	private final String userFlag;
	
	private final String userSuffix;
	
	final boolean validate(final String user, final String password)
			throws NamingException {
		if (!this.enable) {
			throw new RuntimeException("未启用LDAP模式。");
		}
		if (this.LDAPServerURL == null) {
			throw new RuntimeException("未配置LDAP服务地址。");
		}
		if (StringUtil.isEmpty(user) || StringUtil.isEmpty(password))
			return false;
		if(LDAP_SERVER.equals(this.type)){
			return validateToLDAP(user, password);
		}else if(DC_SERVER.equals(this.type)){
			return validateToDC(user, password);
		}else{
			return validateToDC(user, password);
		}
	}
	
	final DirContext getCtx(String dn, String password) throws NamingException {
    	DirContext ctx = null;
    	
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, this.LDAPCtxFactoryClassName);
        env.put(Context.PROVIDER_URL, this.LDAPServerURL);
        env.put(Context.SECURITY_AUTHENTICATION, this.authentication);
        env.put(Context.SECURITY_PRINCIPAL, dn);//binddn 
        env.put(Context.SECURITY_CREDENTIALS, password);//bindpwd
        try {
            // 链接ldap
            ctx = new InitialDirContext(env);
        } catch (NamingException e) {
        	throw e;
        } 
        return ctx;
    }
	
	final boolean validateToDC(String userName, String password) throws NamingException{
		final Hashtable<String, String> environment = new Hashtable<String, String>();
		environment.put(Context.INITIAL_CONTEXT_FACTORY, this.LDAPCtxFactoryClassName);
		environment.put(Context.PROVIDER_URL, this.LDAPServerURL);
		environment.put(Context.SECURITY_AUTHENTICATION, this.authentication);// "none",
		// "simple",
		// "strong"
		environment.put(Context.SECURITY_PRINCIPAL, userName + "@" + this.domain);
		environment.put(Context.SECURITY_CREDENTIALS, password);
		try {
			new InitialDirContext(environment);
		} catch (final NamingException exception) {
			throw exception;
		}
		return true;
    }
	
	final boolean validateToLDAP(String userName, String password) throws NamingException{
		String[] domains = {};
		try{
			domains = this.domain.split("#");
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		for(String domain:domains){
			String userDN = buildUserDN(userName, this.userSuffix, domain);
	    	DirContext ctx = null;
	    	try{
	    		ctx = getCtx(userDN, password);
		    	if(null != ctx){
		    		return true;
		    	}
	    	}catch(Exception e){}
	    	
			if(!StringUtils.isEmpty(this.userSuffix)){
				userDN = buildUserDN(userName, "", domain);
				try{
					ctx = getCtx(userDN, password);
					if(null != ctx){
			    		return true;
			    	}
				}catch(Exception e){}
			}
		}
    	return false;
    }
	    
    final String buildUserDN(String userName, String userSuffix, String domain){
    	return this.userFlag + "=" + userName + userSuffix +"," + domain;
    }
	
	static final String xml_element_ldap = "ldap";

	private static final String xml_element_ldap_enable = "enable";

	private static final String xml_element_ldap_factory_initial = "factory-initial";

	private static final String xml_element_ldap_url = "provider-url";

	private static final String xml_element_ldap_domain = "domain";

	private static final String xml_element_ldap_include = "include";

	private static final String xml_element_ldap_entry = "entry";

	private static final String xml_element_ldap_authentication = "authentication";

	private static final String DEFAULT_LDAP_CTX_FACTORY_CLASS_NAME = "com.sun.jndi.ldap.LdapCtxFactory";
	
	private static final String xml_element_ldap_type = "type";

	private static final String xml_element_ldap_user_flag = "user-flag";
	
	private static final String xml_element_ldap_user_suffix = "user-suffix";
	
	private static final String LDAP_SERVER = "ldap";

	private static final String DC_SERVER = "dc";
	
	private static final class StringUtil {

		static final boolean isEmpty(final String string) {
			return string == null || string.equals("");
		}

		private StringUtil() {
			// to do nothing.
		}

	}

}
