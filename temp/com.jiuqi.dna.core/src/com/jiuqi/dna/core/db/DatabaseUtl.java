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
	 * ��ȫ�������ݿ�
	 * 
	 * <p>
	 * ��ͬһʱ��ֻ����һ�����ݲ������С�
	 * 
	 * @param context
	 *            �Ѿ���½�������ģ���Ҫ��½�û��������ݿⱸ��Ȩ�ޡ�
	 * @param output
	 *            �������ݵ��������
	 * @param print
	 *            ������־�Ĵ�ӡ����
	 */
	public static final void backup(Context context, OutputStream output,
			PrintStream print) {
		if (context == null) {
			throw new NullPointerException("������Ϊ�ա�");
		}
		if (output == null) {
			throw new NullPointerException("�����Ϊ�ա�");
		}
		if (context.getLogin().getUser().getName().equals("admin") || context.find(SystemPrivilegeOperation.EXECUTE, SystemPrivilege.class, SystemPrivilege.DB_BACKUP.getKey()) != null) {
			if (lock.tryLock()) {
				try {
					DbBackup.backup(context, output, print);
				} catch (Throwable e) {
					throw new DatabaseBackupException("���ݿⱸ��ʧ�ܣ�" + e.getMessage(), e);
				} finally {
					lock.unlock();
				}
			} else {
				throw new DatabaseBackupException("�Ѿ��б������ݿ�Ĳ������ڽ����С�");
			}
		} else {
			throw new UnauthorizedAccessException("��ǰ�û�û�����ݿ�ı���Ȩ�ޡ�");
		}
	}
	
	
	/**
	 * ��ȫ�������ݿ�
	 * 
	 * <p>
	 * ��ͬһʱ��ֻ����һ�����ݲ������С�
	 * 
	 * @param context
	 *            �Ѿ���½�������ģ���Ҫ��½�û��������ݿⱸ��Ȩ�ޡ�
	 * @param output
	 *            �������ݵ��������
	 * @param print
	 *            ������־�Ĵ�ӡ����
	 */
	public static  final void backup(Context context, OutputStream output,
			PrintStream print,IDatabaseCallback<GUID , String> callBack) {
		if (context == null) {
			throw new NullPointerException("������Ϊ�ա�");
		}
		if (output == null) {
			throw new NullPointerException("�����Ϊ�ա�");
		}
		if (context.getLogin().getUser().getName().equals("admin") || context.find(SystemPrivilegeOperation.EXECUTE, SystemPrivilege.class, SystemPrivilege.DB_BACKUP.getKey()) != null) {
			if (lock.tryLock()) {
				try {
					DbBackup.backup(context, output, print,callBack);
				} catch (Throwable e) {
					//e.printStackTrace();
					throw new DatabaseBackupException("���ݿⱸ��ʧ�ܣ�" + e.getMessage(), e);
				} finally {
					lock.unlock();
				}
			} else {
				throw new DatabaseBackupException("�Ѿ��б������ݿ�Ĳ������ڽ����С�");
			}
		} else {
			throw new UnauthorizedAccessException("��ǰ�û�û�����ݿ�ı���Ȩ�ޡ�");
		}
	}
}