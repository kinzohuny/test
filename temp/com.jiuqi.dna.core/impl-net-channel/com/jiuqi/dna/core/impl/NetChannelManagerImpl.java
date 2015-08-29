package com.jiuqi.dna.core.impl;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

import com.jiuqi.dna.core.spi.work.Work;
import com.jiuqi.dna.core.spi.work.WorkingThread;

public class NetChannelManagerImpl {
	/**
	 * �������Ӽ�����·��
	 */
	static final String NET_CHANNEL_LISTENER_PATH = "dna_core/ncl";

	final ApplicationImpl application;
	final long channelVersion;
	private final HashMap<NetNodeToken, NetChannelImpl> channels = new HashMap<NetNodeToken, NetChannelImpl>();

	NetChannelManagerImpl(ApplicationImpl application) {
		this.application = application;
		this.channelVersion = application.newRECVER();
	}

	private final AtomicInteger packageIDSeed = new AtomicInteger();

	final NetChannelImpl ensureChannel(NetNodeToken info) {
		if (this.application.localNodeID.equals(info.appID)) {
			throw new IllegalArgumentException("����ͨ��:���������ӵ����ؽڵ�[" + info.ncl + "]");
		}
		NetChannelImpl channel = null;
		synchronized (this.channels) {
			for (Map.Entry<NetNodeToken, NetChannelImpl> entry : this.channels.entrySet()) {
				NetNodeToken old = entry.getKey();
				if (old.index == info.index && old.appID.equals(info.appID)) {
					channel = entry.getValue();
					break;
				}
			}
			if (channel == null) {
				channel = new NetChannelImpl(this, info);
				this.channels.put(info, channel);
			}
			channel.appendURL(info);
		}
		return channel;
	}

	final int newPackageID() {
		return this.packageIDSeed.incrementAndGet();
	}

	/**
	 * ��ȡ�������ĵ�ַ
	 * 
	 * @param url
	 * @return
	 * @throws MalformedURLException
	 */
	static final URL getListenerURL(URL url) throws MalformedURLException {
		return new URL(url, NET_CHANNEL_LISTENER_PATH);
	}

	final NetNodeToken[] queryRemoteNodeInfo(URL host, Proxy proxy) {
		try {
			final DnaHttpClient hc = new DnaHttpClient(getListenerURL(host), proxy, this.application.netNodeManager.thisCluster.thisClusterNodeIndex, this.application.localNodeID);
			try {
				return hc.queryNodeInfo();
			} finally {
				hc.closeServer();
			}
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	final NetNodeToken findCachedToken(URL address) {
		try {
			address = getListenerURL(address);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
		synchronized (this.channels) {
			for (NetNodeToken n : this.channels.keySet()) {
				if (address.equals(n.ncl)) {
					return n;
				}
			}
		}
		return null;
	}

	public DataPackageReceiver getNetIOHandler() {
		return this.receiver;
	}

	final int getVersion() {
		return 1;
	}

	final void unuseChannel(NetChannelImpl channel) {
		synchronized (this.channels) {
			channel.clearURLs(this.channels);
		}
		this.receiver.channelDisabled(channel);
	}

	// /////////////////////////////////////////////////
	// //////////////�����߳����////////////////////////
	private final Queue<NetPackageSendingEntry<?>> waitingBuildingPackages = new LinkedList<NetPackageSendingEntry<?>>();
	/**
	 * �����̸߳���
	 */
	private volatile int buildingThreadCount;
	/**
	 * �����̵߳�������
	 */
	private int maxBuildingThreadCount = Runtime.getRuntime().availableProcessors();

	/**
	 * �����̵߳����
	 * 
	 * @throws Throwable
	 */
	private final void buildThreadRun() throws Throwable {
		for (;;) {
			final NetPackageSendingEntry<?> pkgToBuild;
			synchronized (this.waitingBuildingPackages) {
				if (!this.waitingBuildingPackages.isEmpty()) {
					pkgToBuild = this.waitingBuildingPackages.poll();
				} else {
					this.buildingThreadCount--;
					return;
				}
			}
			try {
				pkgToBuild.buildAndPostFragmentToSend();
			} catch (Throwable e) {
				final int newThreads;
				synchronized (this.waitingBuildingPackages) {
					this.buildingThreadCount--;
					newThreads = this.preNewBuildThreadNoSync();
				}
				this.newBuildThread(newThreads);
				throw e;
			}
		}
	}

	/**
	 * �����̹߳���
	 */
	private final class BuildFragmentWork extends Work {
		@Override
		protected final void doWork(WorkingThread thread) throws Throwable {
			NetChannelManagerImpl.this.buildThreadRun();
		}
	}

	private final int preNewBuildThreadNoSync() {
		final int wbpc = this.waitingBuildingPackages.size();
		if (wbpc == 0) {
			return 0;
		}
		int needThread = this.maxBuildingThreadCount - this.buildingThreadCount;
		if (needThread == 0) {
			return 0;
		}
		if (needThread > wbpc) {
			needThread = wbpc;
		}
		this.buildingThreadCount += needThread;
		return needThread;
	}

	private final void newBuildThread(int count) {
		while (count-- > 0) {
			this.application.overlappedManager.startWork(new BuildFragmentWork());
		}
	}

	private final void newResolveThread(int count) {
		while (count-- > 0) {
			this.application.overlappedManager.startWork(new ResolveFragmentWork());
		}
	}

	/**
	 * �������빹���жӲ����������߳�
	 * 
	 * @param pkgToBuild
	 */
	final void offerFragmentBuild(NetPackageSendingEntry<?> pkgToBuild) {
		final int newThreads;
		synchronized (this.waitingBuildingPackages) {
			this.waitingBuildingPackages.offer(pkgToBuild);
			newThreads = this.preNewBuildThreadNoSync();
		}
		this.newBuildThread(newThreads);
	}

	// //////////////�����߳����////////////////////////
	// /////////////////////////////////////////////////
	// /////////////////////////////////////////////////
	// //////////////��ԭ�߳����////////////////////////
	private DataPackageReceiver receiver;
	/**
	 * �ȴ���ԭ�İ�
	 */
	private final Queue<NetPackageReceivingEntry<?>> waitingResolvingPackages = new LinkedList<NetPackageReceivingEntry<?>>();
	/**
	 * ���ȴ���ԭ���ĸ���
	 */
	private int maxWaitingResolvingPackageCount = 512;
	/**
	 * ��ԭ�̸߳���
	 */
	private volatile int resolvingThreadCount;
	/**
	 * ��ԭ�̵߳�������
	 */
	private int maxResolvingThreadCount = Runtime.getRuntime().availableProcessors();

	private final int preNewResolveThreadNoSync() {
		final int wbpc = this.waitingResolvingPackages.size();
		if (wbpc == 0) {
			return 0;
		}
		int needThread = this.maxResolvingThreadCount - this.resolvingThreadCount;
		if (needThread == 0) {
			return 0;
		}
		if (needThread > wbpc) {
			needThread = wbpc;
		}
		this.resolvingThreadCount += needThread;
		return needThread;
	}

	public final DataPackageReceiver setNetIOHandler(
			DataPackageReceiver receiver) {
		final DataPackageReceiver old = this.receiver;
		this.receiver = receiver;
		return old;
	}

	final void offerFragmentResolve(NetPackageReceivingEntry<?> rpe)
			throws InterruptedException {
		final int newThreads;
		synchronized (this.waitingResolvingPackages) {
			while (this.waitingResolvingPackages.size() >= this.maxWaitingResolvingPackageCount) {
				this.waitingResolvingPackages.wait();
			}
			this.waitingResolvingPackages.offer(rpe);
			newThreads = this.preNewResolveThreadNoSync();
		}
		this.newResolveThread(newThreads);
	}

	/**
	 * �����̵߳����
	 * 
	 * @throws Throwable
	 */
	private final void resolveThreadRun() throws Throwable {
		for (;;) {
			final NetPackageReceivingEntry<?> pkgToResolve;
			synchronized (this.waitingResolvingPackages) {
				if (!this.waitingResolvingPackages.isEmpty()) {
					pkgToResolve = this.waitingResolvingPackages.poll();
				} else {
					this.resolvingThreadCount--;
					return;
				}
			}
			try {
				pkgToResolve.resolveDataFragment();
			} catch (Throwable e) {
				final int newThreads;
				synchronized (this.waitingResolvingPackages) {
					this.resolvingThreadCount--;
					newThreads = this.preNewResolveThreadNoSync();
				}
				this.newResolveThread(newThreads);
				throw e;
			}
		}
	}

	private final class ResolveFragmentWork extends Work {
		@Override
		protected final void doWork(WorkingThread thread) throws Throwable {
			NetChannelManagerImpl.this.resolveThreadRun();
		}
	}

	final void offerPackageReceiving(NetPackageReceivingEntry<?> rpe,
			DataInputFragment fragment) {
		final DataPackageReceiver receiver = this.receiver;
		if (receiver != null) {
			try {
				receiver.packageArriving(rpe.channel, fragment, rpe);
			} catch (Throwable e) {
				rpe.startReceivingPackage(null, null);
			}
		}
	}
	// //////////////��ԭ�߳����////////////////////////
	// /////////////////////////////////////////////////
}
