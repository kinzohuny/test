package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.None;

final class FloatModelPropAccessor extends ModelPropAccessor {
	@SuppressWarnings("unchecked")
	@Override
	float getPropValueAsFloat(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		if (context == null || mo == null) {
			throw new NullPointerException();
		}
		if (propAccessDefine.script != null && propAccessDefine.script.scriptCallable()) {
			return Convert.toFloat(propAccessDefine.script.executeScriptAsGetter(context, mo, propAccessDefine));
		} else if (propAccessDefine.accessor != null) {
			SpaceNode occorAtSave = propAccessDefine.accessor.getService().updateContextSpace(context);
			try {
				return propAccessDefine.accessor.doGetFloat(context, mo, propAccessDefine.ownerProperty);
			} catch (Throwable e) {
				throw Utils.tryThrowException(e);
			} finally {
				occorAtSave.updateContextSpace(context);
			}
		} else if (propAccessDefine.field != null) {
			return propAccessDefine.field.getFieldValueAsFloat(mo);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	void setPropValueAsFloat(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, float value) {
		if (context == null || mo == null) {
			throw new NullPointerException();
		}
		if (propAccessDefine.ownerProperty.hasBeforeInspects()) {
			propAccessDefine.ownerProperty.callBeforeInspects(context, mo, None.NONE, value);
		}
		try {
			if ((propAccessDefine.script == null || !propAccessDefine.script.executeScriptAsSetter(context, mo, value, propAccessDefine)) && propAccessDefine.accessor != null) {
				SpaceNode occorAtSave = propAccessDefine.accessor.getService().updateContextSpace(context);
				try {
					propAccessDefine.accessor.doSetFloat(context, mo, value, propAccessDefine.ownerProperty);
				} catch (Throwable e) {
					throw Utils.tryThrowException(e);
				} finally {
					occorAtSave.updateContextSpace(context);
				}
			} else if (propAccessDefine.field != null) {
				propAccessDefine.field.setFieldValueAsFloat(mo, value);
			} else {
				throw new UnsupportedOperationException();
			}
			if (propAccessDefine.ownerProperty.hasAfterInspects()) {
				propAccessDefine.ownerProperty.callAfterInspects(context, mo, None.NONE, value);
			}
		} finally {
			if (propAccessDefine.ownerProperty.hasBeforeInspects()) {
				propAccessDefine.ownerProperty.callFinallyInspects(context, mo, None.NONE, value);
			}
		}
	}

	@Override
	void setPropValueAsBoolean(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, boolean value) {
		this.setPropValueAsFloat(context, propAccessDefine, mo, Convert.toFloat(value));
	}

	@Override
	boolean getPropValueAsBoolean(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toBoolean(this.getPropValueAsFloat(context, propAccessDefine, mo));
	}

	@Override
	void setPropValueAsByte(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, byte value) {
		this.setPropValueAsFloat(context, propAccessDefine, mo, Convert.toFloat(value));
	}

	@Override
	byte getPropValueAsByte(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toByte(this.getPropValueAsFloat(context, propAccessDefine, mo));
	}

	@Override
	void setPropValueAsShort(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, short value) {
		this.setPropValueAsFloat(context, propAccessDefine, mo, Convert.toFloat(value));
	}

	@Override
	short getPropValueAsShort(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toShort(this.getPropValueAsFloat(context, propAccessDefine, mo));
	}

	@Override
	void setPropValueAsInt(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, int value) {
		this.setPropValueAsFloat(context, propAccessDefine, mo, Convert.toFloat(value));
	}

	@Override
	int getPropValueAsInt(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toInt(this.getPropValueAsFloat(context, propAccessDefine, mo));
	}

	@Override
	void setPropValueAsLong(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, long value) {
		this.setPropValueAsFloat(context, propAccessDefine, mo, Convert.toFloat(value));
	}

	@Override
	long getPropValueAsLong(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toLong(this.getPropValueAsFloat(context, propAccessDefine, mo));
	}

	@Override
	void setPropValueAsDate(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, long value) {
		this.setPropValueAsFloat(context, propAccessDefine, mo, Convert.toFloat(value));
	}

	@Override
	long getPropValueAsDate(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toDate(this.getPropValueAsFloat(context, propAccessDefine, mo));
	}

	@Override
	void setPropValueAsDouble(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, double value) {
		this.setPropValueAsFloat(context, propAccessDefine, mo, Convert.toFloat(value));
	}

	@Override
	double getPropValueAsDouble(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toDouble(this.getPropValueAsFloat(context, propAccessDefine, mo));
	}

	@Override
	void setPropValueAsString(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, String value) {
		this.setPropValueAsFloat(context, propAccessDefine, mo, Convert.toFloat(value));
	}

	@Override
	String getPropValueAsString(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toString(this.getPropValueAsFloat(context, propAccessDefine, mo));
	}

	@Override
	void setPropValueAsGUID(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, GUID value) {
		this.setPropValueAsFloat(context, propAccessDefine, mo, Convert.toFloat(value));
	}

	@Override
	GUID getPropValueAsGUID(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toGUID(this.getPropValueAsFloat(context, propAccessDefine, mo));
	}

	@Override
	void setPropValueAsBytes(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, byte[] value) {
		this.setPropValueAsFloat(context, propAccessDefine, mo, Convert.toFloat(value));
	}

	@Override
	byte[] getPropValueAsBytes(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toBytes(this.getPropValueAsFloat(context, propAccessDefine, mo));
	}

	@Override
	void setPropValueAsObject(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, Object value) {
		this.setPropValueAsFloat(context, propAccessDefine, mo, Convert.toFloat(value));
	}

	@Override
	Object getPropValueAsObject(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toObject(this.getPropValueAsFloat(context, propAccessDefine, mo));
	}

	private FloatModelPropAccessor() {
	}

	static final FloatModelPropAccessor ACCESSOR = new FloatModelPropAccessor();
}
