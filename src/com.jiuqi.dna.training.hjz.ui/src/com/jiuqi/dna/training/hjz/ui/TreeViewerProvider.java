package com.jiuqi.dna.training.hjz.ui;

import java.util.List;

import com.jiuqi.dna.bap.common.constants.BapImages;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.training.hjz.service.intf.Department;
import com.jiuqi.dna.training.hjz.service.key.GetDepartmentChildrenList;
import com.jiuqi.dna.training.hjz.service.key.GetDepartmentParent;
import com.jiuqi.dna.ui.wt.graphics.ImageDescriptor;
import com.jiuqi.dna.ui.wt.provider.LabelProvider;
import com.jiuqi.dna.ui.wt.provider.TreeContentProvider;

public class TreeViewerProvider implements TreeContentProvider, LabelProvider {

	public TreeViewerProvider(Context context){
		this.context = context;
	}
	
	Context context;
	
	public Object[] getElements(Object inputElement) {
		if (inputElement==null) {
			List<Department> root = context.getList(Department.class, new GetDepartmentChildrenList(null)); 
			return root.toArray();
		} else {
			return null;
		}
	}

	public ImageDescriptor getImage(Object element) {
		if(hasChildren(element)){
			return context.find(ImageDescriptor.class, BapImages.ico_grid2_tree_root);
		}
		return context.find(ImageDescriptor.class, BapImages.ico_new);
	}

	public String getText(Object element) {
		return ((Department)element).getName();
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Department) {
			Department parent = (Department) parentElement;
			List<Department> children = context.getList(Department.class, new GetDepartmentChildrenList(parent.getId()));
			return sortDept(children).toArray();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if(this.getChildren(element)!=null&&this.getChildren(element).length>0){
			return true;
		}
		return false;
	}

	public Object getParent(Object element) {
			return context.find(Department.class, new GetDepartmentParent((Department)element));
	}

	private List<Department> sortDept(List<Department> list){
		for(int i=0;i<list.size();i++){
			for(int j=i;j<list.size();j++){
				if(list.get(i).getOrder()>list.get(j).getOrder()){
					Department temp = list.get(i);
					list.set(i, list.get(j));
					list.set(j, temp);
				}
			}
		}
		return list;
	}
}
