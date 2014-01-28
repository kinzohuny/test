package btw.bill.store;

import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableFieldDeclare;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.type.TypeFactory;

public class TB_SN extends TableDeclarator {

	public static final String TABLE_NAME ="SN";

	public final TableFieldDefine f_DT;
	public final TableFieldDefine f_SN;

	public static final String FN_DT ="DT";
	public static final String FN_SN ="SN";

	//不可调用该构造方法.当前类只能由框架实例化.
	private TB_SN() {
		super(TABLE_NAME);
		this.table.setTitle("串号");
		TableFieldDeclare field;
		this.f_DT = field = this.table.newField(FN_DT, TypeFactory.NVARCHAR(30));
		field.setTitle("日期");
		this.f_SN = field = this.table.newField(FN_SN, TypeFactory.INT);
		field.setTitle("种号");
	}

}
