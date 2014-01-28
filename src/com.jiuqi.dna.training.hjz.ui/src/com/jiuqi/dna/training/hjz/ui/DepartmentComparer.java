package com.jiuqi.dna.training.hjz.ui;

import com.jiuqi.dna.training.hjz.service.intf.Department;
import com.jiuqi.dna.ui.wt.viewers.IElementComparer;

public class DepartmentComparer implements IElementComparer {

	public boolean equals(Object a, Object b) {
	    if(a!=null && b!=null && a instanceof Department && b instanceof Department){
	    	Department temp1=(Department)a;
	    	Department temp2=(Department)b;
	        if(temp1.getId().equals(temp2.getId())){
	        	return true;
	        }
	      }
		return false;
	}

	public int hashCode(Object element) {
	    if(element != null && element instanceof Department){
	        return ((Department)element).getHashCode();
	      }else{
	          return 0;
	      }
	}

}
