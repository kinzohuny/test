package com.jiuqi.dna.core.internal.management;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.jiuqi.dna.core.impl.ApplicationImpl;
import com.jiuqi.dna.core.impl.ConsoleLog;
import com.jiuqi.dna.core.log.DNALogManager;
import com.jiuqi.dna.core.spi.application.AppUtil;

public final class Managements {

	public static final String DOMAIN = "com.jiuqi.dna";

	public static final void registerMBean(Object object, String type) {
		final ObjectName on;
		try {
			on = new ObjectName(DOMAIN, "type", type);
		} catch (MalformedObjectNameException e) {
			ApplicationImpl app = (ApplicationImpl) AppUtil.tryGetDefaultApp();
			if (null != app) {
				app.catcher.catchException(e, app);			
				return;
			} else {
				throw new RuntimeException("registerMBean faild.domain="+DOMAIN+";type=type"+";value="+type,e);
			}
			
		} catch (Throwable th){
//			FIXME 在websphere中部署war包会有问题，为了能在websphere中启动，所以吞掉异常，仅记录日志.
			DNALogManager.getLogger("core").logError(null, th, false);
			ConsoleLog.debugError("注册ManagementBean失败(type=\"" + type + "\").");
			return;
		}
		register(object, on);
	}

	private static final void register(Object object, ObjectName name) {
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			mbs.registerMBean(object, name);
		} catch (Throwable e) {
			ApplicationImpl app = (ApplicationImpl) AppUtil.tryGetDefaultApp();
			if (null != app) {
				app.catcher.catchException(e, app);			
				return;
			} else {
				throw new RuntimeException("register faild.object="+object+";name="+name,e);
			}
		}
	}

	public static final void registerMBean(Object object, String type,
			String name) {
		final ObjectName on;
		try {
			on = new ObjectName(DOMAIN + ":type=" + type + ",name=" + name);
		} catch (MalformedObjectNameException e) {
			ApplicationImpl app = (ApplicationImpl) AppUtil.tryGetDefaultApp();
			if (null != app) {
				app.catcher.catchException(e, app);			
				return;
			} else {
				throw new RuntimeException("registerMBean faild.domain="+DOMAIN+";type="+type+";value="+type,e);
			}
		} catch (Throwable th){
//			FIXME 在websphere中部署war包会有问题，为了能在websphere中启动，所以吞掉异常，仅记录日志.
			DNALogManager.getLogger("core").logError(null, th, false);
			ConsoleLog.debugError("注册ManagementBean失败(type=\"" + type + "\").");
			return;
		}
		register(object, on);
	}
}