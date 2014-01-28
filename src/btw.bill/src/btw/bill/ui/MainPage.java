package btw.bill.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.cxf.common.util.StringUtils;


import btw.bill.impl.Bill;
import btw.bill.task.BillTask;
import btw.bill.utils.FormatUtil;
import btw.bill.utils.paginate.Paginate;
import btw.bill.utils.paginate.PaginateBar;
import btw.bill.utils.paginate.PaginateRender;
import btw.bill.utils.paginate.ResourceFinder;

import com.jiuqi.dna.bap.common.constants.BapImages;
import com.jiuqi.dna.bap.common.util.Regexes;
import com.jiuqi.dna.ui.common.constants.JWT;
import com.jiuqi.dna.ui.wt.events.ActionEvent;
import com.jiuqi.dna.ui.wt.events.ActionListener;
import com.jiuqi.dna.ui.components.SearchBar;
import com.jiuqi.dna.ui.wt.graphics.ImageDescriptor;
import com.jiuqi.dna.ui.wt.grid2.Grid2;
import com.jiuqi.dna.ui.wt.grid2.GridEnums.EditMode;
import com.jiuqi.dna.ui.wt.grid2.GridModel;
import com.jiuqi.dna.ui.wt.layouts.FillLayout;
import com.jiuqi.dna.ui.wt.layouts.GridData;
import com.jiuqi.dna.ui.wt.layouts.GridLayout;
import com.jiuqi.dna.ui.wt.widgets.Button;
import com.jiuqi.dna.ui.wt.widgets.Composite;
import com.jiuqi.dna.ui.wt.widgets.Page;
import com.jiuqi.dna.ui.wt.widgets.Text;

public class MainPage extends Page {

	public MainPage(Composite parent) {
		super(parent);
		initPage();
		initListener();
		initFoucs();
		refreshData();
	}

	private Text text;
	private Button print;
	private Composite cmp_search;
	private SearchBar searchBar;
	private Grid2 grid2;
	private GridModel model;
	private Paginate<Bill> paginate;
	private PaginateBar paginateBar;
	private List<Bill> billList;;


	private void refreshData() {

		billList = getContext().getList(Bill.class);
		Collections.sort(billList);
		refreshPaginate();
	}

	private void initFoucs() {
		
		print.setFocusTraversalIndex(1);
		text.setFocusTraversalIndex(2);
		searchBar.setFocusTraversalIndex(-1);
		grid2.setFocusTraversalIndex(-1);
		paginateBar.button_first_page.setFocusTraversalIndex(-1);
		paginateBar.button_prev_page.setFocusTraversalIndex(-1);
		paginateBar.button_next_page.setFocusTraversalIndex(-1);
		paginateBar.button_last_page.setFocusTraversalIndex(-1);
		paginateBar.text_to_page.setFocusTraversalIndex(-1);
		paginateBar.button_go.setFocusTraversalIndex(-1);
		
	}
	
	private void initListener() {
		print.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				searchBar.getTextControl().setText(null);
				int num = 1;
				try {
					num =  Integer.valueOf(text.getText());
				} catch (Exception e) {
					
				}
				BillTask task = new BillTask();
				for(int i=0;i<num;i++){
					getContext().handle(task);
				}
				refreshData();
				text.setText("1");
			}
		});
		searchBar = new SearchBar(cmp_search, "请输入要搜索的文字") {
			
			@Override
			protected void searchTextChanged(String text) {
				if(StringUtils.isEmpty(text)){
					paginate.setResourceFinder(new ResourceFinder() {
						public List<Bill> find() {
							return billList;
						}
					});
					refreshPaginate();
				}else{
					final List<Bill> list = new ArrayList<Bill>(billList);
					for(Iterator<Bill> it = list.iterator(); it.hasNext();){
						Bill b = (Bill)it.next();
						if(FormatUtil.timeToCompleteString(b.dt).contains(text)||b.sn.contains(text)){
							
						}else{
							it.remove();
						}
					}
					paginate.setResourceFinder(new ResourceFinder() {
						
						public List<Bill> find() {
							return list;
						}
					});
					refreshPaginate();
				}
			}
		};
		text.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				print.forceFocus();
				
			}
		});
	}
	
	private void initPage() {
		GridLayout gridLayout = new GridLayout(2);
		gridLayout.horizontalSpacing = gridLayout.verticalSpacing = 10;
		gridLayout.marginBottom = gridLayout.marginLeft = gridLayout.marginRight = gridLayout.marginTop = 10;
		this.setLayout(gridLayout);
		GridData horizontalGridData = new GridData();
		horizontalGridData.horizontalSpan = 2;
		GridData fillGridData = new GridData();
		fillGridData.grabExcessHorizontalSpace = fillGridData.grabExcessVerticalSpace = true;
		fillGridData.verticalAlignment = fillGridData.horizontalAlignment = JWT.FILL;
		fillGridData.horizontalSpan = 2;
		text = new Text(this);
		text.setText("1");
		text.setRegExp(Regexes.Int);
		text.setMaximumLength(1);
		print = new Button(this);
		print.setText("打印");
		print.setImage(getContext().find(ImageDescriptor.class,	BapImages.ico_print));
		cmp_search = new Composite(this);
		cmp_search.setLayoutData(horizontalGridData);
		cmp_search.setLayout(new FillLayout());
		grid2 = new Grid2(this);
		grid2.setLayoutData(fillGridData);
		Composite cmp_paginate = new Composite(this);
		cmp_paginate.setLayoutData(horizontalGridData);
		cmp_paginate.setLayout(new FillLayout());
		paginate = new Paginate<Bill>(Bill.class, getContext(), 30);
		paginateBar = new PaginateBar(cmp_paginate, paginate);
		paginate.addRender(new PaginateRender<Bill>() {
			
			public void render(List<Bill> records) {
				refreshGrid(records);
			}
		});
		paginate.setResourceFinder(new ResourceFinder() {
			
			public List<Bill> find() {
				return billList;
			}
		});
		model = grid2.getModel();
		model.setEditMode(EditMode.READONLY);
		model.setColumnCount(3);
		model.getGridCell(0, 0).setShowText("序号");
		model.getGridCell(1, 0).setShowText("打票时间");
		model.getGridCell(2, 0).setShowText("序列号");
		model.setColumnWidth(0, 50);
		model.setColumnWidth(1, 150);
		model.setColumnWidth(2, 150);
	}
	
	private void refreshPaginate(){
		paginate.refresh();
		paginateBar.refresh();
		paginateBar.to_first_page();
	}
	
	private void refreshGrid(List<Bill> list){
		model.setRowCount(list.size() + 1);
		for (int i = 1; i < list.size() + 1; i++) {
			Bill b = list.get(i - 1);
			model.getGridCell(1, i).setShowText(FormatUtil.timeToCompleteString(b.dt));
			model.getGridCell(2, i).setShowText(b.sn);
		}
	}
	
}
