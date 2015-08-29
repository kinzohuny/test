/**
 * 
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.type.DataObjectTranslator;

abstract class DataTranslatorHelper<TSourceObject, TDelegateObject> {

	private int unResolved;
	private DataObjectTranslator<TSourceObject, TDelegateObject> translator;
	private TDelegateObject delegateSave;
	private TSourceObject destSave;

	private static abstract class Feedbacker<TDestHolder> {

		TDestHolder destHolder;

		DataTranslatorHelper<?, ?> dth;

		Feedbacker(TDestHolder destHolder, DataTranslatorHelper<?, ?> dth) {
			this.destHolder = destHolder;
			if (dth != null) {
				dth.unResolveOne();
				this.dth = dth;
			}
		}

		private DataTranslatorHelper.Feedbacker<?> next;

		void feedback(Object dest) {
			this.destHolder = null;
			if (this.dth != null) {
				this.dth.resolveOne();
				this.dth = null;
			}
		}
	}

	private static class ArrayItemFeedbacker extends
			DataTranslatorHelper.Feedbacker<Object[]> {

		private final int index;

		@Override
		final void feedback(Object dest) {
			this.destHolder[this.index] = dest;
			super.feedback(dest);
		}

		ArrayItemFeedbacker(Object[] destHolder,
				DataTranslatorHelper<?, ?> dth, int index) {
			super(destHolder, dth);
			this.index = index;
		}
	}

	private static abstract class ObjectFieldFeedbackerBase<TDestHolder>
			extends DataTranslatorHelper.Feedbacker<TDestHolder> {
		ObjectFieldAccessor accessor;
		final int offset;

		ObjectFieldFeedbackerBase(TDestHolder destHolder,
				DataTranslatorHelper<?, ?> dth, ObjectFieldAccessor accessor,
				int offset) {
			super(destHolder, dth);
			this.accessor = accessor;
			this.offset = offset;
		}
	}

	private static class ObjectFieldFeedbacker extends
			DataTranslatorHelper.ObjectFieldFeedbackerBase<Object> {

		@Override
		final void feedback(Object dest) {
			this.accessor.intrenalSetObject(this.offset, this.destHolder, dest);
			this.accessor = null;
			super.feedback(dest);
		}

		ObjectFieldFeedbacker(Object destHolder,
				DataTranslatorHelper<?, ?> dth, ObjectFieldAccessor accessor,
				int offset) {
			super(destHolder, dth, accessor, offset);
		}
	}

	private static class ObjectFieldDFeedbacker extends
			DataTranslatorHelper.ObjectFieldFeedbackerBase<DynObj> {

		@Override
		final void feedback(Object dest) {
			this.accessor.intrenalSetObjectD(this.offset, this.destHolder, dest);
			this.accessor = null;
			super.feedback(dest);
		}

		ObjectFieldDFeedbacker(DynObj destHolder,
				DataTranslatorHelper<?, ?> dth, ObjectFieldAccessor accessor,
				int offset) {
			super(destHolder, dth, accessor, offset);
		}
	}

	final void unResolveOne() {
		this.unResolved++;
	}

	private final void resolveOne() {
		if (--this.unResolved == 0 && this.destSave != null) {
			this.translator.recoverData(this.destSave, this.delegateSave, this.version(), this.forSerial());
			this.translator = null;
			this.delegateSave = null;
			this.destSave = null;
		}
	}

	private DataTranslatorHelper.Feedbacker<?> feedbackers;

	private final void appendFeedbacker(
			DataTranslatorHelper.Feedbacker<?> feedbacker) {
		feedbacker.next = this.feedbackers;
		this.feedbackers = feedbacker;
	}

	final void appendObjectFieldFeedbacker(Object destHolder,
			DataTranslatorHelper<?, ?> dth, ObjectFieldAccessor accessor,
			int offset) {
		this.appendFeedbacker(new ObjectFieldFeedbacker(destHolder, dth, accessor, offset));
	}

	final void appendObjectFieldDFeedbacker(DynObj destHolder,
			DataTranslatorHelper<?, ?> dth, ObjectFieldAccessor accessor,
			int offset) {
		this.appendFeedbacker(new ObjectFieldDFeedbacker(destHolder, dth, accessor, offset));
	}

	final void appendArrayItemFeedbacker(Object[] destHolder,
			DataTranslatorHelper<?, ?> dth, int index) {
		this.appendFeedbacker(new ArrayItemFeedbacker(destHolder, dth, index));
	}

	protected abstract void destInstanceResolved(TSourceObject dest);

	protected abstract boolean forSerial();

	protected short version() {
		return this.translator.getVersion();
	}

	DataTranslatorHelper(
			DataObjectTranslator<TSourceObject, TDelegateObject> translator) {
		this.translator = translator;
	}

	final TSourceObject translate(TDelegateObject delegate,
			TSourceObject destHint) {
		final short version = this.version();
		final boolean forSerial = this.forSerial();
		final TSourceObject dest = this.translator.resolveInstance(destHint, delegate, version, forSerial);
		this.destInstanceResolved(dest);
		DataTranslatorHelper.Feedbacker<?> feedbacker = this.feedbackers;
		if (feedbacker != null) {
			this.feedbackers = null;
			do {
				feedbacker.feedback(dest);
				final DataTranslatorHelper.Feedbacker<?> next = feedbacker.next;
				feedbacker.next = null;
				feedbacker = next;
			} while (feedbacker != null);
		}
		if (this.unResolved == 0) {
			this.translator.recoverData(dest, delegate, version, forSerial);
			this.translator = null;
		} else {
			this.delegateSave = delegate;
			this.destSave = dest;
		}
		return dest;
	}

}