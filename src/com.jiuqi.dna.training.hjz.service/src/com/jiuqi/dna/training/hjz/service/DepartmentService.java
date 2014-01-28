package com.jiuqi.dna.training.hjz.service;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.service.Publish;
import com.jiuqi.dna.core.service.Service;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.training.hjz.core.task.*;
import com.jiuqi.dna.training.hjz.core.intf.DepartmentResource;
import com.jiuqi.dna.training.hjz.service.intf.Department;
import com.jiuqi.dna.training.hjz.service.key.GetDepartmentAllChildrenList;
import com.jiuqi.dna.training.hjz.service.key.GetDepartmentById;
import com.jiuqi.dna.training.hjz.service.key.GetDepartmentChildrenList;
import com.jiuqi.dna.training.hjz.service.key.GetDepartmentNext;
import com.jiuqi.dna.training.hjz.service.key.GetDepartmentParent;
import com.jiuqi.dna.training.hjz.service.key.GetDepartmentList;
import com.jiuqi.dna.training.hjz.service.key.GetDepartmentPrevious;
import com.jiuqi.dna.training.hjz.service.task.ChangeDepartmentOrderTask;
import com.jiuqi.dna.training.hjz.service.task.CreateDepartmentTask;
import com.jiuqi.dna.training.hjz.service.task.RemoveDepartmentTask;
import com.jiuqi.dna.training.hjz.service.task.UpdateDepartmentTask;

public class DepartmentService extends Service {

	protected DepartmentService() {
		super("DepartmentService");
	}
	
	//create department
	@Publish
	protected class CreateDepartmentTaskHandler extends SimpleTaskMethodHandler<CreateDepartmentTask>{

		@Override
		protected void handle(Context context, CreateDepartmentTask task)
				throws Throwable {
			CreateDepartmentResourceTask rTask = new CreateDepartmentResourceTask();
			rTask.setName(task.getName());
			rTask.setMaster(task.getMaster());
			rTask.setNum(task.getNum());
			rTask.setDate(task.getDate());
			rTask.setParent(task.getParent());
			rTask.setRemark(task.getRemark());
			try {
				context.handle(rTask);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("create department fail");
			}
			task.setResult(getImpl(rTask.getResult()));
		}
	}
	
	//update department
	@Publish
	protected class UpdateDepartmentTaskHandler extends SimpleTaskMethodHandler<UpdateDepartmentTask>{

		@Override
		protected void handle(Context context, UpdateDepartmentTask task)
				throws Throwable {
			UpdateDepartmentResourceTask rTask = new UpdateDepartmentResourceTask();
			rTask.setId(task.getId());
			rTask.setName(task.getName());
			rTask.setMaster(task.getMaster());
			rTask.setNum(task.getNum());
			rTask.setDate(task.getDate());
			rTask.setParent(task.getParent());
			rTask.setRemark(task.getRemark());
			rTask.setOrder(task.getOrder());
			try {
				context.handle(rTask);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("update department fail");
			}
			task.setResult(getImpl(rTask.getResult()));
		}
	}
	
	//change order
	@Publish
	protected class ChangeDepartmentOrderTaskHandler extends SimpleTaskMethodHandler<ChangeDepartmentOrderTask>{

		@Override
		protected void handle(Context context, ChangeDepartmentOrderTask task)
				throws Throwable {
			Department dept1 = task.getDept1();
			Department dept2 = task.getDept2();
			long temp = dept1.getOrder();
			UpdateDepartmentTask task1 = new UpdateDepartmentTask();
			task1.setId(dept1.getId());
			task1.setName(dept1.getName());
			task1.setMaster(dept1.getMaster());
			task1.setNum(dept1.getNum());
			task1.setDate(dept1.getDate());
			task1.setParent(dept1.getParent());
			task1.setRemark(dept1.getRemark());
			task1.setOrder(dept2.getOrder());
			context.handle(task1);
			UpdateDepartmentTask task2 = new UpdateDepartmentTask();
			task2.setId(dept2.getId());
			task2.setName(dept2.getName());
			task2.setMaster(dept2.getMaster());
			task2.setNum(dept2.getNum());
			task2.setDate(dept2.getDate());
			task2.setParent(dept2.getParent());
			task2.setRemark(dept2.getRemark());
			task2.setOrder(temp);
			context.handle(task2);
		}
	}
	//delete department
	@Publish
	protected class RemoveDepartmentTaskHandler extends SimpleTaskMethodHandler<RemoveDepartmentTask>{

		@Override
		protected void handle(Context context, RemoveDepartmentTask task)
				throws Throwable {
			RemoveDepartmentResourceTask rtask = new RemoveDepartmentResourceTask(task.getId());
			try {
				context.handle(rtask);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("delete department fail");
			}
		}
	}
	
	//get department by id
	@Publish
	protected class GetDepartmentByIdProvider extends OneKeyResultProvider<Department, GetDepartmentById> {

		@Override
		protected Department provide(Context context, GetDepartmentById key)
				throws Throwable {
			DepartmentResource resource = context.get(DepartmentResource.class,key.getId());
			return getImpl(resource);
		}
	}
	
	//get department list
	@Publish
	protected class GetDepartmentListProvider extends OneKeyResultListProvider<Department, GetDepartmentList> {

		@Override
		protected void provide(Context context, GetDepartmentList key,
				List<Department> resultList) throws Throwable {
			List<DepartmentResource> resourceList = context.getList(DepartmentResource.class);
			for(DepartmentResource dr : resourceList){
				resultList.add(getImpl(dr));
			}
		}
	}
	
	//getChildrenList
	@Publish
	protected class GetDepartmentChildrenListProvider extends OneKeyResultListProvider<Department, GetDepartmentChildrenList> {

		@Override
		protected void provide(Context context, GetDepartmentChildrenList key,
				List<Department> resultList) throws Throwable {
			List<Department> tempList = new ArrayList<Department>();
			for (DepartmentResource d : context.getList(DepartmentResource.class)) {
				if (key.getParentId() == null && d.getParent() == null) {
					tempList.add(getImpl(d));
				} else if (key.getParentId()!=null&&key.getParentId().equals(d.getParent())) {
					tempList.add(getImpl(d));
				}
			}
			resultList.addAll(sortDept(tempList));
		}
	}
	
	//get all Children List
	@Publish
	protected class GetDepartmentAllChildrenListProvider extends OneKeyResultListProvider<Department, GetDepartmentAllChildrenList> {

		@Override
		protected void provide(Context context, GetDepartmentAllChildrenList key,
				List<Department> resultList) throws Throwable {
			for (Department d : context.getList(Department.class, new GetDepartmentChildrenList(key.getParentId()))) {
				resultList.add(d);
				resultList.addAll(context.getList(Department.class, new GetDepartmentAllChildrenList(d.getId())));
			}
		}
	}
	
	//get parent
	@Publish
	protected class GetDepartmentParentProvider extends OneKeyResultProvider<Department, GetDepartmentParent>{

		@Override
		protected Department provide(Context context, GetDepartmentParent key)
				throws Throwable {
			GUID parentId = key.getDept().getParent();
			if(parentId==null){
				return null;
			}else{
				return context.get(Department.class, new GetDepartmentById(parentId));
			}
		}
	}
	
	//get previous
	@Publish
	protected class GetDepartmentPreviousProvider extends OneKeyResultProvider<Department, GetDepartmentPrevious>{

		@Override
		protected Department provide(Context context, GetDepartmentPrevious key)
				throws Throwable {
			Department dept = key.getDept();
			List<Department> list = context.getList(Department.class, new GetDepartmentChildrenList(dept.getParent()));
			for(int i=list.size()-1;i>=0;i--){
				if(list.get(i).getOrder()<dept.getOrder())
					return list.get(i);
			}
		    return null;
		}
	}

	//get next
	@Publish
	protected class GetDepartmentNextProvider extends OneKeyResultProvider<Department, GetDepartmentNext>{

		@Override
		protected Department provide(Context context, GetDepartmentNext key)
				throws Throwable {
			Department dept = key.getDept();
			List<Department> list = context.getList(Department.class, new GetDepartmentChildrenList(dept.getParent()));
			for(int i=0;i<list.size();i++){
				if(list.get(i).getOrder()>dept.getOrder())
					return list.get(i);
			}
		    return null;
		}
	}
	
	private static List<Department> sortDept(List<Department> list){
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
	
	private DepartmentImpl getImpl(DepartmentResource resource){
		DepartmentImpl impl = new DepartmentImpl();
		impl.setId(resource.getId());
		impl.setName(resource.getName());
		impl.setMaster(resource.getMaster());
		impl.setNum(resource.getNum());
		impl.setDate(resource.getDate());
		impl.setParent(resource.getParent());
		impl.setRemark(resource.getRemark());
		impl.setOrder(resource.getOrder());
		impl.setHashCode(resource.hashCode());
		return impl;
	}
	
}
