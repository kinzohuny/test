package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.misc.SXElement;

interface DefineXML<TDefine extends DefineBaseImpl> {

	void render(TDefine define, SXElement element);

	void merge(TDefine define, SXElement element);
}