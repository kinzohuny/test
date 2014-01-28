package com.jiuqi.dna.training.hjz.ui;

//import com.jiuqi.dna.bap.common.constants.BapImages;
//import com.jiuqi.dna.bap.common.constants.Images;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.cxf.common.util.StringUtils;

import com.jiuqi.dna.bap.common.constants.BapImages;
import com.jiuqi.dna.bap.common.control.SearchBar;
import com.jiuqi.dna.bap.common.paginate.Paginate;
import com.jiuqi.dna.bap.common.paginate.PaginateBar;
import com.jiuqi.dna.bap.common.paginate.ResourceFinder;
import com.jiuqi.dna.bap.common.paginate.render.PaginateRender;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.training.hjz.service.intf.Department;
import com.jiuqi.dna.training.hjz.service.key.GetDepartmentAllChildrenList;
import com.jiuqi.dna.training.hjz.service.key.GetDepartmentById;
import com.jiuqi.dna.training.hjz.service.key.GetDepartmentParent;
import com.jiuqi.dna.training.hjz.service.key.GetDepartmentPrevious;
import com.jiuqi.dna.training.hjz.service.key.GetDepartmentNext;
import com.jiuqi.dna.training.hjz.service.task.ChangeDepartmentOrderTask;
import com.jiuqi.dna.training.hjz.service.task.RemoveDepartmentTask;
import com.jiuqi.dna.ui.common.constants.JWT;
import com.jiuqi.dna.ui.template.launch.TemplateLauncher;
import com.jiuqi.dna.ui.template.launch.TemplateWindow;
import com.jiuqi.dna.ui.viewpart.MsgClose;
import com.jiuqi.dna.ui.wt.events.ActionEvent;
import com.jiuqi.dna.ui.wt.events.ActionListener;
import com.jiuqi.dna.ui.wt.events.SelectionEvent;
import com.jiuqi.dna.ui.wt.events.SelectionListener;
import com.jiuqi.dna.ui.wt.graphics.ImageDescriptor;
import com.jiuqi.dna.ui.wt.grid2.Grid2;
import com.jiuqi.dna.ui.wt.grid2.GridModel;
import com.jiuqi.dna.ui.wt.layouts.FillLayout;
import com.jiuqi.dna.ui.wt.layouts.GridData;
import com.jiuqi.dna.ui.wt.layouts.GridLayout;
import com.jiuqi.dna.ui.wt.viewers.TreeViewer;
import com.jiuqi.dna.ui.wt.widgets.Button;
import com.jiuqi.dna.ui.wt.widgets.Composite;
import com.jiuqi.dna.ui.wt.widgets.MessageDialog;
import com.jiuqi.dna.ui.wt.widgets.Page;
import com.jiuqi.dna.ui.wt.widgets.SashForm;
import com.jiuqi.dna.ui.wt.widgets.ToolBar;
import com.jiuqi.dna.ui.wt.widgets.ToolItem;

public class MainPage extends Page {

	public MainPage(Composite parent) {
		super(parent);
		initPage();
		setCurDept(getContext().find(Department.class, new GetDepartmentById(GUID.emptyID)));
		initListener();
		refresh();
	}
	
	//refresh tree and set tree selection
	public void refresh() {

		tree.refresh();
		tree.setSelection(curDept);
	}

	private void initListener() {

		ti_new.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("MainPage", MainPage.this);
				map.put("isNew", true);
				TemplateWindow infoPage = TemplateLauncher.openTemplateWindow(
						MainPage.this, "HjzInfoPage", map, JWT.MODAL
								| JWT.CLOSE, JWT.NONE);
				infoPage.setTitle("新增部门");
				infoPage.setIcon(getContext().find(ImageDescriptor.class,BapImages.ico_create));
			}
		});

		ti_modify.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("MainPage", MainPage.this);
				map.put("isNew", false);
				TemplateWindow infoPage = TemplateLauncher.openTemplateWindow(
						MainPage.this, "HjzInfoPage", map, JWT.MODAL
								| JWT.CLOSE, JWT.NONE);
				infoPage.setTitle("修改部门");
				infoPage.setIcon(getContext().find(ImageDescriptor.class,BapImages.ico_modify));
			}
		});

		ti_remove.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				final Department dept = (Department) tree
						.getSelection();
				boolean hasChildren = provider.hasChildren(dept);
				final MessageDialog dialog = MessageDialog.confirm("删除部门",
						"确认删除部门[" + dept.getName() + "]"+(hasChildren?"及其下级":"")+"？");
				dialog.setIcon(getContext().find(ImageDescriptor.class,BapImages.ico_delete));
				dialog.addSelectionListener(new SelectionListener() {

					public void widgetSelected(SelectionEvent e) {
						if (JWT.OK == dialog.getReturnCode()) {
							if (getContext().find(Department.class, new GetDepartmentPrevious(dept)) != null) {
								curDept = getContext().find(Department.class, new GetDepartmentPrevious(dept));
							} else if (getContext().find(Department.class, new GetDepartmentNext(dept)) != null) {
								curDept = getContext().find(Department.class, new GetDepartmentNext(dept));
							} else {
								curDept = getContext().find(Department.class, new GetDepartmentParent(dept));
							}
							getContext().handle(new RemoveDepartmentTask(dept.getId()));
							refresh();
						}
					}
				});

			}
		});

		ti_up.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Department dept = getContext().get(Department.class, new GetDepartmentPrevious(curDept));
				ChangeDepartmentOrderTask task = new ChangeDepartmentOrderTask(curDept, dept);
				getContext().handle(task);
				curDept = getContext().get(Department.class, new GetDepartmentById(curDept.getId()));
				refresh();
			}
		});

		ti_down.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Department dept = getContext().get(Department.class, new GetDepartmentNext(curDept));
				ChangeDepartmentOrderTask task = new ChangeDepartmentOrderTask(curDept, dept);
				getContext().handle(task);
				refresh();
			}
		});

		ti_close.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				getContext().bubbleMessage(new MsgClose());
			}
		});
		
		tree.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if(tree.getSelection()!=null)
					setCurDept((Department)tree.getSelection()); 
				buttonControl();
				searchBar.getControlText().setText(null);
				refreshNaginate();
				tree.setExpandedState(curDept, true);
			}
		});
		
		searchBar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(StringUtils.isEmpty(searchBar.getText().trim())){
					paginate.setResourceFinder(new ResourceFinder() {
						public List<Department> find() {
							return departmentList;
						}
					});
					paginateBar.refresh();
					paginate.refresh();
				}else{
					final List<Department> list = new ArrayList<Department>(departmentList);
					String t = searchBar.getText();
					for(Iterator<Department> it = list.iterator(); it.hasNext();){
						Department d = (Department)it.next();
						if(d.getName()!=null&&d.getName().contains(t)||d.getMaster()!=null&&d.getMaster().contains(t)||d.getRemark()!=null&&d.getRemark().contains(t)){
							
						}else{
							it.remove();
						}
					}
					paginate.setResourceFinder(new ResourceFinder() {
						
						public List<Department> find() {
							return list;
						}
					});
					paginateBar.refresh();
					paginate.refresh();
				}
			}
		});
		
	}

	private ToolBar toolbar;
	private Composite cmp_tree;
	private TreeViewer tree;
	private TreeViewerProvider provider;
	private DepartmentComparer comparer;
	private SearchBar searchBar;
	private Grid2 grid;
	private GridModel model;
	private ToolItem ti_new;
	private ToolItem ti_modify;
	private ToolItem ti_remove;
	private ToolItem ti_up;
	private ToolItem ti_down;
	private ToolItem ti_close;
	private Paginate<Department> paginate;
	private PaginateBar paginateBar;
	private Department curDept;
	private List<Department> departmentList;
	private List<Button> checkList = new ArrayList<Button>();

	private void initPage() {
		initFrame();
		initToolBar();
		initTree();
		initGrid2();
		initPaginate();
	}

	//paginate
	private void initPaginate() {
		paginate.addRender(new PaginateRender<Department>() {
			
			public void render(List<Department> records) {
				grid.setData(records);
				gridShowData();
			}
		});
		paginate.setResourceFinder(new ResourceFinder() {
			
			public List<Department> find() {
				return departmentList;
			}
		});
		
	}
	
	private void refreshNaginate() {
		paginate.setResourceFinder(new ResourceFinder() {
			public List<Department> find() {
				return departmentList;
			}
		});
		paginateBar.refresh();
		paginate.refresh();
	}

	private void initFrame() {
		// 1列grid布局
		GridLayout gridLayout = new GridLayout(1);
		// 填充抢占gridData
		GridData fillGridData = new GridData();
		fillGridData.grabExcessHorizontalSpace = fillGridData.grabExcessVerticalSpace = true;
		fillGridData.horizontalAlignment = fillGridData.verticalAlignment = JWT.FILL;
		// 水平填充抢占gridData
		GridData horizontalGridData = new GridData();
		horizontalGridData.grabExcessHorizontalSpace = true;
		horizontalGridData.horizontalAlignment = JWT.FILL;

		this.setLayout(gridLayout);
		
		toolbar = new ToolBar(this, JWT.RIGHT);
		toolbar.setLayoutData(horizontalGridData);
		
		SashForm sashForm = new SashForm(this);
		sashForm.setLayoutData(fillGridData);
		sashForm.setWeights("1:4");
		sashForm.getFirstComposite().setLayout(new FillLayout());
		sashForm.getSecondComposite().setLayout(gridLayout);
		cmp_tree = sashForm.getFirstComposite();
		Composite cmp_search = new Composite(sashForm.getSecondComposite());
		cmp_search.setLayout(new FillLayout());
		Composite cmp_grid = new Composite(sashForm.getSecondComposite());
		cmp_grid.setLayoutData(fillGridData);
		cmp_grid.setLayout(new FillLayout());
		Composite cmp_paginate = new Composite(sashForm.getSecondComposite());

		tree = new TreeViewer(cmp_tree);
		searchBar = new SearchBar(cmp_search);
		grid = new Grid2(cmp_grid);
		cmp_paginate.setLayout(gridLayout);
		paginate = new Paginate<Department>(Department.class, getContext(), 20);
		paginateBar = new PaginateBar(cmp_paginate,paginate);

	}

	private void initToolBar() {
		ti_new = new ToolItem(toolbar);
		ti_new.setImage(getContext().find(ImageDescriptor.class,
				BapImages.ico_create));
		ti_new.setText("新增");
		ti_modify = new ToolItem(toolbar);
		ti_modify.setImage(getContext().find(ImageDescriptor.class,
				BapImages.ico_modify));
		ti_modify.setText("修改");
		ti_remove = new ToolItem(toolbar);
		ti_remove.setImage(getContext().find(ImageDescriptor.class,
				BapImages.ico_delete));
		ti_remove.setText("删除");
		ti_up = new ToolItem(toolbar);
		ti_up.setImage(getContext().find(ImageDescriptor.class,
				BapImages.ico_moveup));
		ti_up.setText("上移");
		ti_down = new ToolItem(toolbar);
		ti_down.setImage(getContext().find(ImageDescriptor.class,
				BapImages.ico_movedown));
		ti_down.setText("下移");
		ti_close = new ToolItem(toolbar);
		ti_close.setImage(getContext().find(ImageDescriptor.class,
				BapImages.ico_close_window));
		ti_close.setText("关闭");

	}

	private String getTimeStr(long l) {
		Date date = new Date(l);
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
		return format.format(date);
	}

	private void initTree() {
		provider = new TreeViewerProvider(getContext());
		comparer = new DepartmentComparer();
		tree.setContentProvider(provider);
		tree.setLabelProvider(provider);
		tree.setComparer(comparer);
		tree.setInput(null);
		cmp_tree.layout();
	}

	private void initGrid2() {
		int rowNum = 11;
		model = grid.getModel();
		model.setColumnCount(8);
		model.setRowCount(rowNum);
		model.getGridCell(0, 0).setShowText("序号");
		model.getGridCell(1, 0).setShowText("勾选");
		model.getGridCell(2, 0).setShowText("部门名称");
		model.getGridCell(3, 0).setShowText("部门经理");
		model.getGridCell(4, 0).setShowText("部门人数");
		model.getGridCell(5, 0).setShowText("创建日期");
		model.getGridCell(6, 0).setShowText("上级部门");
		model.getGridCell(7, 0).setShowText("备注");
		model.setColumnWidth(0, 50);
		model.setColumnWidth(1, 50);
		model.setColumnWidth(7, 200);

	}

	private void gridShowData() {
		@SuppressWarnings("unchecked")
		List<Department> list = (List<Department>) grid
				.getData();
		model.setRowCount(list.size() + 1);
		for (int i = 1; i < list.size() + 1; i++) {
			Department d = list.get(i - 1);
			Composite c = new Composite(grid);
			GridLayout layout = new GridLayout();
			c.setLayout(layout);
			Button button = new Button(c, JWT.CHECK);
			GridData data = new GridData();
			data.horizontalAlignment = GridData.CENTER;
			data.verticalAlignment = GridData.CENTER;
			data.grabExcessHorizontalSpace = true;
			data.grabExcessVerticalSpace = true;
			button.setLayoutData(data);
			button.setData(d);
			checkList.add(button);
			model.getGridCell(1, i).setControl(c);
			;
			model.getGridCell(2, i).setShowText(d.getName());
			model.getGridCell(3, i).setShowText(d.getMaster());
			model.getGridCell(4, i).setShowText(String.valueOf(d.getNum()));
			model.getGridCell(5, i).setShowText(getTimeStr(d.getDate()));
			Department parent = getContext().find(Department.class, new GetDepartmentParent(d));
			model.getGridCell(6, i).setShowText(parent != null ? parent.getName() : null);
			model.getGridCell(7, i).setShowText(d.getRemark());
			model.setRowHeight(i, 22);
		}
	}

	private void buttonControl() {
		ti_modify.setEnabled(true);
		ti_remove.setEnabled(true);
		ti_up.setEnabled(true);
		ti_down.setEnabled(true);

		if (curDept.getId().equals(GUID.emptyID)) {
			ti_modify.setEnabled(false);
			ti_remove.setEnabled(false);
			ti_up.setEnabled(false);
			ti_down.setEnabled(false);
		}

		if (getContext().find(Department.class, new GetDepartmentPrevious(curDept)) == null) {
			ti_up.setEnabled(false);
		}

		if (getContext().find(Department.class, new GetDepartmentNext(curDept)) == null) {
			ti_down.setEnabled(false);
		}
	}

	public Department getCurDept() {
		return curDept;
	}

	public void setCurDept(Department curDept) {
		this.curDept = curDept;
		departmentList = getContext().getList(Department.class, new GetDepartmentAllChildrenList(this.curDept.getId()));
		departmentList.add(0, this.curDept);
	}

	public TreeViewerProvider getProvider() {
		return provider;
	}

	public DepartmentComparer getComparer() {
		return comparer;
	}
}
