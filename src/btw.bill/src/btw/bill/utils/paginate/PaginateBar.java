package btw.bill.utils.paginate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jiuqi.dna.bap.common.constants.BapContextProvider;
import com.jiuqi.dna.bap.lang.infogroup.BAPPaginate;
import com.jiuqi.dna.ui.template.WT;
import com.jiuqi.dna.ui.wt.events.ActionEvent;
import com.jiuqi.dna.ui.wt.events.ActionListener;
import com.jiuqi.dna.ui.wt.layouts.GridData;
import com.jiuqi.dna.ui.wt.widgets.Button;
import com.jiuqi.dna.ui.wt.widgets.Composite;
import com.jiuqi.dna.ui.wt.widgets.Label;
import com.jiuqi.dna.ui.wt.widgets.MessageDialog;
import com.jiuqi.dna.ui.wt.widgets.Text;

public class PaginateBar {
	
	/**
	 * pagenate容器
	 */
	private Composite paginate_composite;
	/**
	 * 首页按钮
	 */
	public Button button_first_page;
	/**
	 * 上页按钮
	 */
	public Button button_prev_page;
	/**
	 * 下页按钮
	 */
	public Button button_next_page;
	/**
	 * 尾页按钮
	 */
	public Button button_last_page;
	/**
	 * 当前页
	 */
	public Label label_current_page;
	/**
	 * 到
	 */
	private Label label_to;
	/**
	 * 转到页面输入框
	 */
	public Text text_to_page;
	/**
	 * 页
	 */
	private Label label_page;
	/**
	 * GO按钮
	 */
	public Button button_go;
	
	/**
	 * Paginate变量
	 */
	@SuppressWarnings("rawtypes")
	private Paginate paginate;
	
	@SuppressWarnings("rawtypes")
	public PaginateBar(Composite parent_composite,Paginate paginate){
		
		this.paginate = paginate;
		
		this.paginate_composite=new Composite(parent_composite,0);
		this.paginate_composite.setHeight(30);
		this.paginate_composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.paginate_composite.setLayout(WT.newGridLayout(11, false, 0, 0, 0, 0, 5, 5));
		
		this.button_first_page=new Button(paginate_composite,0);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gridData.grabExcessHorizontalSpace = true;
		this.button_first_page.setLayoutData(gridData);
		this.button_first_page.setText(BapContextProvider.getContext().localize(BapContextProvider.getContext().get(BAPPaginate.class).item_0));
		this.button_first_page.addActionListener(first_page_listener);
		
		this.button_prev_page=new Button(paginate_composite,0);
		this.button_prev_page.setLayoutData(new GridData());
		this.button_prev_page.setText(BapContextProvider.getContext().localize(BapContextProvider.getContext().get(BAPPaginate.class).item_1));
		this.button_prev_page.addActionListener(prev_page_listener);
		
		this.button_next_page=new Button(paginate_composite,0);
		this.button_next_page.setLayoutData(new GridData());
		this.button_next_page.setText(BapContextProvider.getContext().localize(BapContextProvider.getContext().get(BAPPaginate.class).item_2));
		this.button_next_page.addActionListener(next_page_listener);
		
		this.button_last_page=new Button(paginate_composite,0);
		this.button_last_page.setLayoutData(new GridData());
		this.button_last_page.setText(BapContextProvider.getContext().localize(BapContextProvider.getContext().get(BAPPaginate.class).item_3));
		this.button_last_page.addActionListener(last_page_listener);
		
		this.label_current_page=new Label(paginate_composite,0);
		this.label_current_page.setLayoutData(new GridData());
		set_current_page_text();
		
		this.label_to=new Label(paginate_composite,0);
		this.label_to.setLayoutData(new GridData());
		this.label_to.setText(BapContextProvider.getContext().localize(BapContextProvider.getContext().get(BAPPaginate.class).item_4));
		
		this.text_to_page=new Text(paginate_composite,0);
		this.text_to_page.setWidth(10);
		this.text_to_page.setLayoutData(WT.newGridData(1,16777216,0,0,1,1,22,16,false,false,0,0,false));
		this.text_to_page.setRegExp("^\\+?[1-9][0-9]*$");
		
		this.label_page=new Label(paginate_composite,0);
		this.label_page.setLayoutData(new GridData());
		this.label_page.setText(BapContextProvider.getContext().localize(BapContextProvider.getContext().get(BAPPaginate.class).item_5));
		
		this.button_go=new Button(paginate_composite,0);
		this.button_go.setLayoutData(new GridData());
		this.button_go.setText(BapContextProvider.getContext().localize(BapContextProvider.getContext().get(BAPPaginate.class).item_6));
		this.button_go.addActionListener(to_page_listener);
	}
	
	/**
	 * PaginateBar初始化，并定位在第一页
	 */
	public PaginateBar init(){
		paginate.to_first_page();
		after_change_page();
		return this;
	}
	
	/**
	 * PaginateBar初始化，并尝试定位在指定页
	 */
	public PaginateBar init(int page_num){
		paginate.to_page(page_num);
		after_change_page();
		return this;
	}
	
	private ActionListener first_page_listener=new ActionListener(){
		public void actionPerformed(ActionEvent arg0){
			paginate.to_first_page();
			after_change_page();
		}
	};
	
	private ActionListener prev_page_listener=new ActionListener(){
		public void actionPerformed(ActionEvent arg0){
			paginate.to_prev_page();
			after_change_page();
		}
	};
	
	private ActionListener next_page_listener=new ActionListener(){
		public void actionPerformed(ActionEvent arg0){
			paginate.to_next_page();
			after_change_page();
		}
	};
	
	private ActionListener last_page_listener=new ActionListener(){
		public void actionPerformed(ActionEvent arg0){
			paginate.to_last_page();
			after_change_page();
		}
	};
	/**
	* 文本校验
	* @param str 要验证的字符串
	* @param regex 正则表达式串
	* @return boolean 如果匹配则返回true，反之为false。如果传来的正则表达式为null，则返回false
	*/
	public static boolean textChecker(String str, String regex){
		if(null != regex){
			if(str == null){
				str = "";
			}
			str = str.trim();
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(str);
			if(matcher.find()){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}

	/**
	 * 正整数
	 */
	//public static final String Int = "^[0-9]*[1-9][0-9]*$";
	public static final String Int = "^[0-9]";
	private ActionListener to_page_listener=new ActionListener(){
		public void actionPerformed(ActionEvent arg0){
			String num = text_to_page.getText();
			if(null==num){
				num="";
			}else{
				num = num.trim();
			}
			if(textChecker(num, Int)){
				int page_num =0;
				try {
					page_num = Integer.parseInt(num);
				} catch (Exception e) {
					page_num = paginate.last_page_num;
					text_to_page.setText(""+page_num);
				}
				if(page_num>paginate.last_page_num){
					page_num = paginate.last_page_num;
					text_to_page.setText(""+page_num);
				}
				paginate.to_page(page_num);
				after_change_page();
			}else{
				MessageDialog.alert(BapContextProvider.getContext().localize(BapContextProvider.getContext().get(BAPPaginate.class).item_7),BapContextProvider.getContext().localize(BapContextProvider.getContext().get(BAPPaginate.class).item_8));
			}
			
		}
	};
	
	private void after_change_page(){
		set_current_page_text();
		set_buttons_status();
	}
	
	private void set_current_page_text(){
		this.label_current_page.setText(BapContextProvider.getContext().localize(BapContextProvider.getContext().get(BAPPaginate.class).item_9)+this.paginate.current_page+"/"+this.paginate.last_page_num+BapContextProvider.getContext().localize(BapContextProvider.getContext().get(BAPPaginate.class).item_10));
		paginate_composite.layout();
	}
	
	private void set_buttons_status(){
		this.button_first_page.setEnabled(true);
		this.button_prev_page.setEnabled(true);
		this.button_next_page.setEnabled(true);
		this.button_last_page.setEnabled(true);
		this.button_go.setEnabled(true);
		
		if(paginate.current_page <= 1){
			this.button_first_page.setEnabled(false);
			this.button_prev_page.setEnabled(false);
			if(paginate.current_page < 1){
				this.button_go.setEnabled(false);
			}
		}
		
		if(paginate.current_page == paginate.last_page_num){
			this.button_last_page.setEnabled(false);
			this.button_next_page.setEnabled(false);
		}
	}
	
	public void refresh(){
		paginate.refresh();
		after_change_page();
	}
	
	public void to_first_page(){
		paginate.to_first_page();
		after_change_page();
	}
	
	public void to_prev_page(){
		paginate.to_prev_page();
		after_change_page();
	}
	
	public void to_next_page(){
		paginate.to_next_page();
		after_change_page();
	}
	
	public void to_last_page(){
		paginate.to_last_page();
		after_change_page();
	}
	
	public void to_page(int page_num){
		paginate.to_page(page_num);
		after_change_page();
	}
	/**
	 * 根据行号定位到指定的page页
	 * 
	 * @param row void
	 */
	public void to_localrow_page(int row){
		int page = 	(int)((row-0.01) / 	this.paginate.page_size) + 1;
		to_page(page);
	
	}
}
