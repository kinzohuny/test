package btw.bill.uientry;

import com.jiuqi.dna.ui.wt.UIEntry;
import com.jiuqi.dna.ui.wt.widgets.Shell;

public class MyUiEntry implements UIEntry {

	@Override
	public void createUI(String[] args, Shell shell) {
		shell.showPage("MainPage");

	}

}
