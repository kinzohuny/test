package btw.bill.utils.paginate;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.Context;

public class Paginate<facade> {
	public Context context;
	public int page_size;
	public int current_page;
	
	public int first_line_num;
	public int last_line_num;
	
	public int records_count;
	
	public int last_page_num;
	
	private ResourceFinder finder = new ResourceFinder(){
		public List<facade> find() {
			return context.getList(facadeclass);
		}
	};
	
	public List<PaginateRender<facade>> renders=new ArrayList<PaginateRender<facade>>();
	
	private Class<facade> facadeclass;
	
	public Paginate(Class<facade> facadeclass,Context context,int page_size){
		this.facadeclass = facadeclass;
		this.context = context;
		this.page_size = page_size;
	}
	
	public void refresh(){
		do_render(current_page);
	}
	
	public void addRender(PaginateRender<facade> render){
		this.renders.add(render);
	}
	
	public void setResourceFinder(ResourceFinder finder){
		this.finder=finder;
	}
	/**
	 * 返回分页的数据集
	 * 
	 * @return List<facade>
	 */
	@SuppressWarnings("unchecked")
	public List<facade> getList(){
		return this.finder.find();
	}
	@SuppressWarnings("unchecked")
	public List<facade> getPagedList(int current_page){
		this.current_page = current_page;
		if(this.current_page < 1){
			this.current_page = 1;
		} 
		
		List<facade> templist = this.finder.find();
		
		this.records_count = templist.size();
		
		this.last_page_num = (int)((this.records_count - 0.01) / this.page_size) + 1;
		if(this.current_page>this.last_page_num){
			this.current_page = this.last_page_num;
		}
		
		
		this.first_line_num = (this.current_page - 1) * this.page_size;
		if (this.first_line_num > this.records_count) {
			int prev = this.last_page_num - 1;
			this.first_line_num = prev * this.page_size;
			this.current_page = this.last_page_num;
		}
		
		this.last_line_num = this.current_page * this.page_size - 1;
		if(this.last_line_num >= this.records_count){
			this.last_line_num = this.records_count-1;
		}
		
		List<facade> records = new ArrayList<facade>();
		for(int i=this.first_line_num;i<=this.last_line_num;i++){
			records.add(templist.get(i));
		}
		
		return records;
	}
	
	protected void to_first_page(){
		do_render(1);
	}
	
	protected void to_prev_page(){
		do_render(this.current_page-1);
	}
	
	protected void to_next_page(){
		do_render(this.current_page+1);
	}
	
	protected void to_last_page(){
		do_render(this.last_page_num);
	}
	
	protected void to_page(int page_num){
		do_render(page_num);
	}
	
	private void do_render(int page_num){
		for(int i=0;i<this.renders.size();i++){
			this.renders.get(i).render(getPagedList(page_num));
		}
	}
}
