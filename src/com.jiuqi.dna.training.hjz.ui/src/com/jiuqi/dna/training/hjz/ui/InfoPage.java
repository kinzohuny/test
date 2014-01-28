package com.jiuqi.dna.training.hjz.ui;

import java.util.Date;
import java.util.Map;

import com.jiuqi.dna.bap.common.util.Regexes;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.training.hjz.service.intf.Department;
import com.jiuqi.dna.training.hjz.service.key.GetDepartmentParent;
import com.jiuqi.dna.training.hjz.service.task.CreateDepartmentTask;
import com.jiuqi.dna.training.hjz.service.task.UpdateDepartmentTask;
import com.jiuqi.dna.ui.common.constants.JWT;
import com.jiuqi.dna.ui.custom.combo.DatePicker;
import com.jiuqi.dna.ui.template.launch.TemplateWindow.CloseMessage;
import com.jiuqi.dna.ui.wt.events.ActionEvent;
import com.jiuqi.dna.ui.wt.events.ActionListener;
import com.jiuqi.dna.ui.wt.events.PanelEvent;
import com.jiuqi.dna.ui.wt.events.PopupListener;
import com.jiuqi.dna.ui.wt.events.SelectionEvent;
import com.jiuqi.dna.ui.wt.events.SelectionListener;
import com.jiuqi.dna.ui.wt.layouts.FillLayout;
import com.jiuqi.dna.ui.wt.layouts.GridData;
import com.jiuqi.dna.ui.wt.layouts.GridLayout;
import com.jiuqi.dna.ui.wt.viewers.TreeViewer;
import com.jiuqi.dna.ui.wt.widgets.Button;
import com.jiuqi.dna.ui.wt.widgets.ComboPanel;
import com.jiuqi.dna.ui.wt.widgets.Composite;
import com.jiuqi.dna.ui.wt.widgets.Label;
import com.jiuqi.dna.ui.wt.widgets.MessageDialog;
import com.jiuqi.dna.ui.wt.widgets.Page;
import com.jiuqi.dna.ui.wt.widgets.Text;

public class InfoPage extends Page {

	public InfoPage(Composite parent, Map<String, Object> map) {
		super(parent);
		mainPage = (MainPage) map.get("MainPage");
		isNew = (Boolean) map.get("isNew");
		initPage();
		initListener();
		initData();
		initTree();
	}

	private MainPage mainPage;
	private Button btn_SaveAndAdd;
	private Button btn_Save;
	private Button btn_Cancel;
	private Text txt_name;
	private Text txt_master;
	private Text txt_num;
	private DatePicker txt_date;
	private ComboPanel txt_parent;
	private Text txt_remark;
	private boolean isNew;
	private TreeViewer tree;
	private Composite cmp_tree;

	private void initTree() {
		tree.setContentProvider(mainPage.getProvider());
		tree.setLabelProvider(mainPage.getProvider());
		tree.setComparer(mainPage.getComparer());
		tree.setInput(null);
		cmp_tree.layout();

	}

	private void initData() {
		Department info = mainPage.getCurDept();
		if (isNew) {
			txt_name.setText("");
			;
			txt_master.setText("");
			txt_num.setText("0");
			txt_date.setDate(new Date());
			txt_remark.setText("");
			if (txt_parent.getData() == null) {
				txt_parent.setData(info);
				txt_parent.setText(info.getName());
			}
		} else {
			setInfo(info);
		}
	}

	private void initListener() {

		btn_SaveAndAdd.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				DepartmentInfo info = getInfo();
				Department result = null;
				try {
					if(isNew){
						CreateDepartmentTask task = new CreateDepartmentTask();
						task.setName(info.name);
						task.setMaster(info.master);
						task.setNum(info.num);
						task.setDate(info.date);
						task.setParent(info.parent);
						task.setRemark(info.remark);
						getContext().handle(task);
						result = task.getResult();
					}else{
						UpdateDepartmentTask task = new UpdateDepartmentTask();
						task.setId(mainPage.getCurDept().getId());
						task.setName(info.name);
						task.setMaster(info.master);
						task.setNum(info.num);
						task.setDate(info.date);
						task.setParent(info.parent);
						task.setRemark(info.remark);
						task.setOrder(mainPage.getCurDept().getOrder());
						getContext().handle(task);
						result = task.getResult();
						isNew = true;
					}
				} catch (Exception e2) {
					if (e2.getMessage() != null) {
						MessageDialog.alert(e2.getMessage());
						return;
					} else {
						e2.printStackTrace();
						MessageDialog.alert("错误", "保存部门异常！");
						return;
					}
				}
				initData();
				mainPage.setCurDept(getContext().get(Department.class, new GetDepartmentParent(result)));
				mainPage.refresh();
				txt_name.forceFocus();
			}
		});

		btn_Save.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				DepartmentInfo info = getInfo();

				Department result = null;
				try {
					if (isNew) {
						CreateDepartmentTask task = new CreateDepartmentTask();
						task.setName(info.name);
						task.setMaster(info.master);
						task.setNum(info.num);
						task.setDate(info.date);
						task.setParent(info.parent);
						task.setRemark(info.remark);
						getContext().handle(task);
						result = task.getResult();
					}else{
						UpdateDepartmentTask task = new UpdateDepartmentTask();
						task.setId(mainPage.getCurDept().getId());
						task.setName(info.name);
						task.setMaster(info.master);
						task.setNum(info.num);
						task.setDate(info.date);
						task.setParent(info.parent);
						task.setRemark(info.remark);
						task.setOrder(mainPage.getCurDept().getOrder());
						getContext().handle(task);
						result = task.getResult();
					}
				} catch (Exception e2) {
					if (e2.getMessage() != null) {
						MessageDialog.alert(e2.getMessage());
						return;
					} else {
						e2.printStackTrace();
						MessageDialog.alert("错误", "保存部门异常！");
						return;
					}
				}
				if(result!=null){
					mainPage.setCurDept(result);
				}
				mainPage.refresh();
				getContext().bubbleMessage(new CloseMessage());
			}
		});
		
		btn_Cancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				getContext().bubbleMessage(new CloseMessage());
			}
		});

		txt_parent.addPopupListener(new PopupListener() {

			public void panelPopup(PanelEvent e) {
				if (txt_parent.getData() != null) {
					tree.setSelection(txt_parent.getData());
				}
			}

			public void panelClose(PanelEvent e) {

			}
		});

		tree.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				txt_parent.setData(tree.getSelection());
				txt_parent.setText(((Department) tree.getSelection())
						.getName());
			}
		});
	}

	private void setInfo(Department info) {
		txt_name.setText(info.getName());
		txt_master.setText(info.getMaster());
		txt_num.setText(String.valueOf(info.getNum()));
		txt_date.setDate(new Date(info.getDate()));
		Department parent = getContext().get(Department.class, new GetDepartmentParent(info));
		txt_parent.setData(parent);
		txt_parent.setText(parent.getName());
		txt_remark.setText(info.getRemark());
	}

	private DepartmentInfo getInfo() {
		DepartmentInfo info = new DepartmentInfo();
		info.name = txt_name.getText();
		info.master = (txt_master.getText());
		info.num = Integer.valueOf(txt_num.getText());
		info.date = txt_date.getDate().getTime();
		info.parent = ((Department) txt_parent.getData()).getId();
		info.remark = (txt_remark.getText());
		return info;
	}

	private void initPage() {
		// 水平抢占
		GridData grabGridData = new GridData();
		grabGridData.grabExcessHorizontalSpace = true;
		grabGridData.horizontalAlignment = JWT.FILL;
		// 水平填充
		GridData fillGridData = new GridData();
		fillGridData.horizontalAlignment = JWT.FILL;
		// 页面布局
		GridLayout gridLayout = new GridLayout(1);
		gridLayout.verticalSpacing = 20;
		gridLayout.marginTop = gridLayout.marginBottom = 10;
		gridLayout.marginLeft = gridLayout.marginRight = 30;
		this.setLayout(gridLayout);
		Composite cmp_info = new Composite(this);
		Composite cmp_button = new Composite(this);
		cmp_info.setLayout(new GridLayout(3));
		cmp_info.setLayoutData(grabGridData);
		cmp_button.setLayout(new GridLayout(4));
		cmp_button.setLayoutData(grabGridData);

		Label lbl_name = new Label(cmp_info);
		lbl_name.setText("部门名称：");
		txt_name = new Text(cmp_info);
		txt_name.setMaximumLength(30);
		Label lbl1_name = new Label(cmp_info);
		lbl1_name.setText("");

		Label lbl_master = new Label(cmp_info);
		lbl_master.setText("部门经理：");
		txt_master = new Text(cmp_info);
		txt_master.setMaximumLength(30);
		Label lbl1_master = new Label(cmp_info);
		lbl1_master.setText("");

		Label lbl_num = new Label(cmp_info);
		lbl_num.setText("部门人数：");
		txt_num = new Text(cmp_info);
		txt_num.setRegExp(Regexes.Int);
		txt_num.setMaximumLength(9);
		Label lbl1_num = new Label(cmp_info);
		lbl1_num.setText("人");

		Label lbl_date = new Label(cmp_info);
		lbl_date.setText("成立日期");
		txt_date = new DatePicker(cmp_info);
		txt_date.setLayoutData(fillGridData);
		Label lbl1_date = new Label(cmp_info);
		lbl1_date.setText("");

		Label lbl_parent = new Label(cmp_info);
		lbl_parent.setText("上级部门：");
		txt_parent = new ComboPanel(cmp_info);
		txt_parent.setLayoutData(fillGridData);
		txt_parent.setEditable(false);
		Label lbl1_parent = new Label(cmp_info);
		lbl1_parent.setText("");
		cmp_tree = txt_parent.getComposite();
		cmp_tree.setLayout(new FillLayout());
		tree = new TreeViewer(cmp_tree);

		Label lbl_remark = new Label(cmp_info);
		lbl_remark.setText("备注：");
		GridData lbl_remarkGridData = new GridData();
		lbl_remarkGridData.verticalAlignment = JWT.BEGINNING;
		lbl_remarkGridData.horizontalAlignment = JWT.RIGHT;
		lbl_remark.setLayoutData(lbl_remarkGridData);
		txt_remark = new Text(cmp_info, JWT.MULTI | JWT.V_SCROLL);
		GridData remarkGridData = new GridData();
		remarkGridData.horizontalSpan = 2;
		remarkGridData.widthHint = 300;
		txt_remark.setLayoutData(remarkGridData);

		Label lbl_space = new Label(cmp_button);
		lbl_space.setLayoutData(grabGridData);
		btn_SaveAndAdd = new Button(cmp_button);
		btn_SaveAndAdd.setText("保存并新增");
		btn_Save = new Button(cmp_button);
		btn_Save.setText("保存");
		btn_Cancel = new Button(cmp_button);
		btn_Cancel.setText("取消");
	}
	
	private class DepartmentInfo{
		
		public String name;
		public String master;
		public int num;
		public long date;
		public GUID parent;
		public String remark;
	}
}
