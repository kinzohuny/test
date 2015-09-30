package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.ObjectQuerier;
import com.jiuqi.dna.core.def.DNASqlType;
import com.jiuqi.dna.core.def.NamedDefine;
import com.jiuqi.dna.core.internal.db.datasource.DataSourceRef;
import com.jiuqi.dna.core.misc.MissingObjectException;
import com.jiuqi.dna.core.situation.Situation;

import static com.jiuqi.dna.core.impl.ServiceInvokeeBase.MASK_DEFINE;
import static com.jiuqi.dna.core.impl.ServiceInvokeeBase.MASK_ELEMENT;
import static com.jiuqi.dna.core.impl.ServiceInvokeeBase.MASK_ELEMENT_META;
import static com.jiuqi.dna.core.impl.ServiceInvokeeBase.MASK_DECLARE_SCRIPT;
import static com.jiuqi.dna.core.impl.InvokeeQueryMode.IN_SITE;

abstract class SpaceNode {
	/**
	 * �����ռ�
	 */
	Space space;
	/**
	 * ����վ��
	 */
	Site site;

	/**
	 * ����
	 */
	abstract void doDispose(ContextImpl<?, ?, ?> context);

	/**
	 * ���Ի�ȡ��ǰ�ڵ������Դ
	 */
	abstract DataSourceRef tryGetDataSourceRef();

	/**
	 * ��ȡ��ǰ�ڵ������Դ
	 */
	final DataSourceRef getDataSourceRef() {
		DataSourceRef dsr = this.tryGetDataSourceRef();
		if (dsr == null) {
			throw new MissingObjectException("��ǰ�ڵ�����Դ��Ч");
		}
		return dsr;
	}

	final boolean isDBValid() {
		return this.tryGetDataSourceRef() != null;
	}

	final void setSpace(Space space) {
		this.space = space;
		this.site = space.site;
	}

	/**
	 * �����������еĵ�ǰģ����Ϣ����Դ��������Ҫ���أ��Ը��µ�ǰ��Դ������
	 */
	SpaceNode updateContextSpace(ContextImpl<?, ?, ?> context) {
		SpaceNode occorAt = context.occorAt;
		context.occorAt = this;
		context.occorAtResourceService = null;
		return occorAt;
	}

	/**
	 * ���ҵ�����
	 * 
	 * @param targetClass
	 *            �������Ľ������
	 * @param key1Class
	 *            �����������
	 * @param key2Class
	 *            �����������
	 * @param key3Class
	 *            �����������
	 * @param mask
	 *            ������������
	 * @param mode
	 *            ��ѯģʽ
	 * @return
	 */
	ServiceInvokeeBase findInvokeeBase(Class<?> targetClass,
			Class<?> key1Class, Class<?> key2Class, Class<?> key3Class,
			int mask, InvokeeQueryMode mode) {
		return this.space.findInvokeeBase(targetClass, key1Class, key2Class, key3Class, mask, mode);
	}

	/**
	 * ��ȡ��������
	 * 
	 * <p>
	 * û���������������׳��쳣��
	 */
	ServiceInvokeeBase getTaskHandler(Class<?> taskClass, Enum<?> taskMethod,
			InvokeeQueryMode mode) {
		return this.space.getTaskHandler(taskClass, taskMethod, mode);
	}

	/**
	 * ��ý���б��ṩ�����Ҳ������׳��쳣
	 */
	<TResult> ServiceInvokeeBase<TResult, Context, Object, Object, Object> findResultListProvider(
			Class<TResult> resultClass, Object key1, Object key2, Object key3,
			InvokeeQueryMode mode) {
		return this.space.findResultListProvider(resultClass, key1, key2, key3, mode);
	}

	<TResult> ServiceInvokeeBase<TResult, Context, Object, Object, Object> findResultProvider(
			Class<TResult> resultClass, Object key1, Object key2, Object key3,
			InvokeeQueryMode mode) {
		return this.space.findResultProvider(resultClass, key1, key2, key3, mode);
	}

	<TResult> ServiceInvokeeBase<TResult, Context, Object, Object, Object> findTreeNodeProvider(
			Class<TResult> elementClass, Object key1, Object key2, Object key3,
			InvokeeQueryMode mode) {
		return this.space.findTreeNodeProvider(elementClass, key1, key2, key3, mode);
	}

	<TResult, TKey1, TKey2, TKey3> boolean tryFillList(List<TResult> list,
			Class<TResult> resultClass, Object key1, Object key2, Object key3,
			InvokeeQueryMode mode) {
		return this.space.tryFillList(list, resultClass, key1, key2, key3, mode);
	}

	<TResult, TKey1, TKey2, TKey3> int tryFillTree(TreeNodeImpl<TResult> root,
			Class<TResult> resultClass, Object key1, Object key2, Object key3,
			InvokeeQueryMode mode) {
		return this.space.tryFillTree(root, resultClass, key1, key2, key3, mode);
	}

	<TResult, TKey1, TKey2, TKey3> TResult tryFindResult(
			Class<TResult> resultClass, Object key1, Object key2, Object key3,
			InvokeeQueryMode mode) {
		return this.space.tryFindResult(resultClass, key1, key2, key3, mode);
	}

	ResourceServiceBase findResourceService(Class<?> facadeClass,
			InvokeeQueryMode mode) {
		return this.space.findResourceService(facadeClass, mode);
	}

	final NamedDefineImpl findNamedDefine(Class<?> defineIntfClass, String name) {
		for (NamedDefineBroker<?> broker = (NamedDefineBroker<?>) this.findInvokeeBase(defineIntfClass, String.class, null, null, MASK_DEFINE, IN_SITE); broker != null; broker = broker.upperMatchBroker()) {
			final NamedDefineImpl define = broker.findDefine(name);
			if (define != null) {
				return define;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	final <TDefine extends NamedDefine> boolean fillRuntimeDefines(
			Class<TDefine> defineIntfClass, List<TDefine> list) {
		boolean filled = false;
		for (NamedDefineBroker broker = (NamedDefineBroker) this.findInvokeeBase(defineIntfClass, String.class, null, null, MASK_DEFINE, IN_SITE); broker != null; broker = broker.upperMatchBroker()) {
			broker.fetchDefines(list);
			filled = true;
		}
		return filled;
	}

	@SuppressWarnings("unchecked")
	final <TElementMeta extends NamedFactoryElement> TElementMeta findNamedFactoryElement(
			NamedFactoryElementGather<?, TElementMeta> factory, String alias) {
		for (NamedFactoryElementBroker<?> broker = (NamedFactoryElementBroker<?>) this.findInvokeeBase(factory.getClass(), String.class, null, null, MASK_ELEMENT_META, IN_SITE); broker != null; broker = broker.upperMatchBroker()) {
			final NamedFactoryElement meta = broker.findNamedFactoryElement(alias);
			if (meta != null) {
				return (TElementMeta) meta;
			}
		}
		return null;
	}

	final URL findDeclareScript(String declareName, DNASqlType type) {
		for (DeclareScriptBroker broker = (DeclareScriptBroker) this.findInvokeeBase(URL.class, String.class, DNASqlType.class, null, MASK_DECLARE_SCRIPT, IN_SITE); broker != null; broker = broker.upperMatchBroker()) {
			final URL url = broker.findDeclareScriptURL(declareName, type);
			if (url != null) {
				return url;
			}
		}
		return null;
	}

	/**
	 * ��ö�Ӧ�������ű�Reader
	 */
	final Reader openDeclareScriptReader(String declareName, DNASqlType type) {
		final URL url = this.findDeclareScript(declareName, type);
		if (url == null) {
			throw new MissingObjectException("�Ҳ�����Ϊ[" + declareName + "],����Ϊ[" + type + "]��Ӧ�������ű�");
		}
		try {
			final InputStream stream = url.openStream();
			try {
				return new InputStreamReader(stream, "UTF8");
			} catch (UnsupportedEncodingException e) {
				try {
					stream.close();
				} catch (IOException e1) {
				}
				throw e;
			}
		} catch (IOException e) {
			throw new UnsupportedOperationException("����Ϊ[" + declareName + "],����Ϊ[" + type + "]�����ű�ʱ����", e);
		}

	}

	final <TElementMeta extends NamedFactoryElement> ArrayList<TElementMeta> fillNamedFactoryElements(
			NamedFactoryElementGather<?, TElementMeta> factory,
			ArrayList<TElementMeta> metas) {
		for (NamedFactoryElementBroker<?> broker = (NamedFactoryElementBroker<?>) this.findInvokeeBase(factory.getClass(), String.class, null, null, MASK_ELEMENT_META, IN_SITE); broker != null; broker = broker.upperMatchBroker()) {
			metas = broker.fetchNamedFactoryElements(metas);
		}
		return metas;
	}

	@SuppressWarnings("unchecked")
	final <T> T findElement(Class<T> clazz) {
		SpaceElementBroker<T> broker = (SpaceElementBroker<T>) this.findInvokeeBase(clazz, null, null, null, MASK_ELEMENT, IN_SITE);
		if (broker != null) {
			return (T) broker.getElement();
		}
		return null;
	}

	private final Object findParm(Class<?> pType, Context context,
			Object[] adArgs) {
		if (pType == Context.class) {
			return context;
		} else if (pType == Situation.class) {
			return context instanceof Situation ? context : null;
		} else if (pType.isInstance(this)) {
			return this;
		} else {
			if (adArgs != null && adArgs.length > 0) {
				for (Object a : adArgs) {
					if (a != null && pType == a.getClass()) {
						return a;
					}
				}
			}
			Object param = this.findElement(pType);
			if (param != null) {
				return param;
			}
			if (adArgs != null && adArgs.length > 0) {
				for (Object a : adArgs) {
					if (a != null && pType.isInstance(a)) {
						return a;
					}
				}
			}
		}
		if (context != null && pType == ObjectQuerier.class) {
			return context;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	final <T> T newObjectInNode(Class<T> clazz, Context context, Object[] adArgs) {
		if (clazz == null) {
			throw new NullPointerException();
		}
		Constructor<?>[] constructors = Utils.getConstructorParamCountOrder(clazz);
		Object[] params = null;
		for (Constructor<?> oneConstructor : constructors) {
			testOneConstructor: {
				Class<?>[] paramTypes = Utils.getParameterTypes(oneConstructor);
				if (params == null || params.length != paramTypes.length) {
					params = new Object[paramTypes.length];
				}
				for (int j = 0; j < paramTypes.length; j++) {
					if ((params[j] = this.findParm(paramTypes[j], context, adArgs)) == null) {
						if (constructors.length == 1) {
							throw new UnsupportedOperationException("�޷�Ϊ" + clazz + "�Ĺ��캯��" + oneConstructor + "׼��ȫ������");
						}
						break testOneConstructor;
					}
				}
				Utils.publicAccessibleObject(oneConstructor);
				try {
					return (T) oneConstructor.newInstance(params);
				} catch (InvocationTargetException e) {
					throw Utils.tryThrowException(e.getTargetException());
				} catch (Throwable e) {
					throw Utils.tryThrowException(e);
				}
			}
		}
		throw new UnsupportedOperationException("û�п�������ȫ�������Ĺ��췽�����ࣺ" + clazz);
	}

	EventListenerChain collectEvent(Class<?> eventClass, Object key1,
			Object key2, Object key3, InvokeeQueryMode mode) {
		return this.space.collectEvent(eventClass, key1, key2, key3, mode);
	}
}