package com.jiuqi.dna.core;

import com.jiuqi.dna.core.exception.UnsupportedContextKindException;

/**
 * վ���״̬
 * 
 * <pre>
 * 
 *          WAITING_LOAD_METADATA
 *               ��    ��    ��        
 *     INITING �� ACTIVE �� DISPOSING �� DISPOSED
 *          ��              ��
 *          LOADING_METADATA
 * 
 * </pre>
 * 
 * @author gaojingxin
 * 
 */
public enum SiteState {
	/**
	 * ��ʼ״̬
	 */
	INITING {
		@Override
		public void checkContextKind(SessionKind sessionKind,
				ContextKind contextKind) {
			switch (contextKind) {
			case DISPOSER:
			case INITER:
			case INTERNAL:
				return;
			case TRANSIENT:
			case SITUATION:
			case NORMAL:
				throw new UnsupportedContextKindException(this, sessionKind,
						contextKind);
			default:
				ContextKind.throwIllegalContextKind(contextKind);
			}
		}
	},
	/**
	 * �״̬�������ṩ����
	 */
	ACTIVE {
		@Override
		public final void checkContextKind(SessionKind sessionKind,
				ContextKind contextKind) {
			switch (contextKind) {
			case TRANSIENT:
			case SITUATION:
			case DISPOSER:
			case NORMAL:
			case INTERNAL:
				return;
			case INITER:
				throw new UnsupportedContextKindException(this, sessionKind,
						contextKind);
			default:
				ContextKind.throwIllegalContextKind(contextKind);
			}
		}
	},
	/**
	 * רΪװ��ԭ���ݶ����õ�վ�㣬װ����ɺ������
	 */
	LOADING_METADATA {
		@Override
		public final void checkContextKind(SessionKind sessionKind,
				ContextKind contextKind) {
			switch (contextKind) {
			case DISPOSER:
			case TRANSIENT:
			case NORMAL:
			case INITER:
			case SITUATION:
				throw new UnsupportedContextKindException(this, sessionKind,
						contextKind);
			case INTERNAL:
				break;
			default:
				ContextKind.throwIllegalContextKind(contextKind);
			}
		}
	},
	/**
	 * �ȴ�ԭ����װ�ص�վ�㣬<br>
	 * ���װ�سɹ����վ�㽫������������ص�ACTIVE״̬
	 */
	WAITING_LOAD_METADATA {
		@Override
		public final void checkContextKind(SessionKind sessionKind,
				ContextKind contextKind) {
			switch (contextKind) {
			case TRANSIENT:
			case SITUATION:
			case DISPOSER:
			case NORMAL:
			case INTERNAL:
				return;
			case INITER:
				throw new UnsupportedContextKindException(this, sessionKind,
						contextKind);
			default:
				ContextKind.throwIllegalContextKind(contextKind);
			}
		}
	},
	/**
	 * �����е�վ��
	 */
	DISPOSING {
		@Override
		public final void checkContextKind(SessionKind sessionKind,
				ContextKind contextKind) {
			switch (contextKind) {
			case DISPOSER:
			case INTERNAL:
				return;
			case TRANSIENT:
			case SITUATION:
			case NORMAL:
			case INITER:
				throw new UnsupportedContextKindException(this, sessionKind,
						contextKind);
			default:
				ContextKind.throwIllegalContextKind(contextKind);
			}
		}
	},
	/**
	 * �����˵�վ��
	 */
	DISPOSED {
		@Override
		public final void checkContextKind(SessionKind sessionKind,
				ContextKind contextKind) {
			switch (contextKind) {
			case DISPOSER:
			case INTERNAL:
				return;
			case TRANSIENT:
			case SITUATION:
			case NORMAL:
			case INITER:
				throw new UnsupportedContextKindException(this, sessionKind,
						contextKind);
			default:
				ContextKind.throwIllegalContextKind(contextKind);
			}
		}
	};

	/**
	 * ��������������Ƿ����
	 * 
	 * @param sessionKind
	 *            �Ự����
	 * @param contextKind
	 *            ����������
	 */
	public abstract void checkContextKind(SessionKind sessionKind,
			ContextKind contextKind);

	/**
	 * ������ݿ��������
	 */
	public final void checkDBAccess(boolean writeOrReadonly) {
		if (this == DISPOSED) {
			throw new IllegalStateException("վ���Ѿ����٣���֧�����ݿ����");
		} else if (writeOrReadonly && this == DISPOSING) {
			throw new IllegalStateException("վ���������٣���֧�������޸Ĳ���");
		}
	}
}
