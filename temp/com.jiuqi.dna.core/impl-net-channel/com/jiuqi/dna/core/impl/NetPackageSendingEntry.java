package com.jiuqi.dna.core.impl;

final class NetPackageSendingEntry<TAttachment> implements
		AsyncIOStub<TAttachment> {
	enum State {
		/**
		 * 等待发送列队有空缺
		 */
		QUEUING,
		/**
		 * 构造并且发送Fragment
		 */
		BUILDING_AND_SENDING,
		/**
		 * 构造完成，等待发送
		 */
		BUILDING_COMPLETE,
		/**
		 * 发送完成，等待确认
		 */
		WAITING_RESOLVE,
		/**
		 * 成功完成，结束。
		 */
		RESOLVED,
		/**
		 * 构造途中出现错误，结束。
		 */
		ERROR
	}

	/**
	 * 数据包ID
	 */
	final int packageID;

	private final NetChannelImpl channel;
	private final DataFragmentBuilder<? super TAttachment> builder;
	private final TAttachment attachment;
	/**
	 * 发送者代
	 */
	private int senderGeneration;
	/**
	 * 状态
	 */
	private State state;
	/**
	 * 开始发送数据
	 */
	private boolean beginSending;

	NetPackageSendingEntry(NetChannelImpl channel,
			DataFragmentBuilder<? super TAttachment> handler,
			TAttachment attachment) {
		this.channel = channel;
		this.packageID = channel.newPackageID();
		this.builder = handler;
		this.attachment = attachment;
	}

	public void cancel() {
		this.channel.breakSend(this.packageID);
		this.channel.postBreakReceivePackageCtrl(this.packageID);
	}

	public void suspend() {
		throw new UnsupportedOperationException();
	}

	public void resume() {
		throw new UnsupportedOperationException();
	}

	public final TAttachment getAttachment() {
		return this.attachment;
	}

	final State getState() {
		return this.state;
	}

	final void setState(State state) {
		this.state = state;
	}

	/**
	 * 判断是否需要重置数据包
	 */
	final boolean needResetPackage(int senderGeneration) {
		if (!this.beginSending) {
			this.beginSending = true;
			this.senderGeneration = senderGeneration;
		} else if (this.senderGeneration < senderGeneration) {
			// 需要尝试重置数据包
			return true;
		}
		return false;
	}

	/**
	 * 重置数据包，准备重新发送，返回是否重置了数据包
	 */
	final void tryResetPackage() {
		if (NetDebug.TRACE_FAULT()) {
			this.channel.trace("网络通信：重置正在发送的数据包[" + this.packageID + "]");
		}
		if (this.builder.tryResetPackage(this.attachment)) {
			this.beginSending = false;
			this.channel.tryStartFragmentBuild(this);
		}
	}

	final void setResolved(boolean done) {
		if (done) {
			this.state = State.RESOLVED;
			this.builder.onFragmentOutFinished(this.attachment);
		} else {
			this.state = State.ERROR;
			this.builder.onFragmentOutError(this.attachment);
		}
	}

	/**
	 * 构造并投递Fragment
	 * 
	 * @throws Throwable
	 */
	final void buildAndPostFragmentToSend() throws Throwable {
		// 获得Fragment并构造内容
		final DataFragment fragment = this.channel.allocDataFragment();
		final int ctrlFlagPos = fragment.getPosition();
		final byte ctrlFlag = this.beginSending ? NetChannelImpl.CTRL_FLAG_PACKAGE : (NetChannelImpl.CTRL_FLAG_PACKAGE_FIRST | NetChannelImpl.CTRL_FLAG_PACKAGE);
		fragment.writeByte(ctrlFlag);
		fragment.writeInt(this.packageID);
		try {
			if (this.builder.buildFragment(fragment, this.attachment)) {
				final int endPos = fragment.getPosition();
				fragment.setPosition(ctrlFlagPos);
				fragment.writeByte((byte) (ctrlFlag | NetChannelImpl.CTRL_FLAG_PACKAGE_LAST));
				fragment.setPosition(endPos);
				this.state = State.BUILDING_COMPLETE;
			} else {
				this.state = State.BUILDING_AND_SENDING;
			}
		} catch (Throwable e) {
			if (NetDebug.TRACE_EXCEPTION()) {
				e.printStackTrace();
			}
			this.channel.releaseDataFragment(fragment);
			this.channel.breakSend(this.packageID);
			if (this.beginSending) {
				// 已经有片段发送，发送终止包控制信息
				this.channel.postBreakReceivePackageCtrl(this.packageID);
			}
			return;
		}
		// 发送Fragment
		this.channel.postDataFragmentToSend(this, fragment);
	}
}
