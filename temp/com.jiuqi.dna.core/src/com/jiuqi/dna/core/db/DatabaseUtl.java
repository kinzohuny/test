package com.jiuqi.dna.core.db;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.auth.UnauthorizedAccessException;
import com.jiuqi.dna.core.impl.DbBackup;
import com.jiuqi.dna.core.system.SystemPrivilege;
import com.jiuqi.dna.core.system.SystemPrivilegeOperation;
import com.jiuqi.dna.core.type.GUID;

public final class DatabaseUtl {

	private DatabaseUtl() {
	}

	public static final Lock lock = new ReentrantLock();

	/**
	 * 完全备份数据库
	 * 
	 * <p>
	 * 在同一时间只允许一个备份操作进行。
	 * 
	 * @param context
	 *            已经登陆的上下文，需要登陆用户具有数据库备份权限。
	 * @param output
	 *            备份数据的输出流。
	 * @param print
	 *            备份日志的打印流。
	 */
	public static final void backup(Context context, OutputStream output,
			PrintStream print) {
		if (context == null) {
			throw new NullPointerException("上下文为空。");
		}
		if (output == null) {
			throw new NullPointerException("输出流为空。");
		}
		if (context.getLogin().getUser().getName().equals("admin") || context.find(SystemPrivilegeOperation.EXECUTE, SystemPrivilege.class, SystemPrivilege.DB_BACKUP.getKey()) != null) {
			if (lock.tryLock()) {
				try {
					DbBackup.backup(context, output, print);
				} catch (Throwable e) {
					throw new DatabaseBackupException("数据库备份失败：" + e.getMessage(), e);
				} finally {
					lock.unlock();
				}
			} else {
				throw new DatabaseBackupException("已经有备份数据库的操作正在进行中。");
			}
		} else {
			throw new UnauthorizedAccessException("当前用户没有数据库的备份权限。");
		}
	}
	
	
	/**
	 * 完全备份数据库
	 * 
	 * <p>
	 * 在同一时间只允许一个备份操作进行。
	 * 
	 * @param context
	 *            已经登陆的上下文，需要登陆用户具有数据库备份权限。
	 * @param output
	 *            备份数据的输出流。
	 * @param print
	 *            备份日志的打印流。
	 */
	public static  final void backup(Context context, OutputStream output,
			PrintStream print,IDatabaseCallback<GUID , String> callBack) {
		if (context == null) {
			throw new NullPointerException("上下文为空。");
		}
		if (output == null) {
			throw new NullPointerException("输出流为空。");
		}
		if (context.getLogin().getUser().getName().equals("admin") || context.find(SystemPrivilegeOperation.EXECUTE, SystemPrivilege.class, SystemPrivilege.DB_BACKUP.getKey()) != null) {
			if (lock.tryLock()) {
				try {
					DbBackup.backup(context, output, print,callBack);
				} catch (Throwable e) {
					//e.printStackTrace();
					throw new DatabaseBackupException("数据库备份失败：" + e.getMessage(), e);
				} finally {
					lock.unlock();
				}
			} else {
				throw new DatabaseBackupException("已经有备份数据库的操作正在进行中。");
			}
		} else {
			throw new UnauthorizedAccessException("当前用户没有数据库的备份权限。");
		}
	}
}