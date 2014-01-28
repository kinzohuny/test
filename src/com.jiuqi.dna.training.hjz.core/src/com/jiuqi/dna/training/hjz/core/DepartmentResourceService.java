package com.jiuqi.dna.training.hjz.core;

import java.util.Date;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.da.DBCommand;
import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.resource.ResourceContext;
import com.jiuqi.dna.core.resource.ResourceInserter;
import com.jiuqi.dna.core.resource.ResourceService;
import com.jiuqi.dna.core.service.Publish;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.training.hjz.core.intf.DepartmentResource;
import com.jiuqi.dna.training.hjz.core.task.CreateDepartmentResourceTask;
import com.jiuqi.dna.training.hjz.core.task.RemoveDepartmentResourceTask;
import com.jiuqi.dna.training.hjz.core.task.UpdateDepartmentResourceTask;

public class DepartmentResourceService extends
		ResourceService<DepartmentResource, DepartmentResourceImpl, DepartmentResourceImpl> {

	protected DepartmentResourceService() {
		super("DepartmentResourceService");
	}

	@Override
	protected void initResources(
			Context context,
			ResourceInserter<DepartmentResource, DepartmentResourceImpl, DepartmentResourceImpl> initializer)
			throws Throwable {
		super.initResources(context, initializer);
		DepartmentResourceImpl deptImpl = new DepartmentResourceImpl();
		deptImpl.setId(GUID.emptyID);
		deptImpl.setName("久其软件");
		deptImpl.setMaster("赵福君");
		deptImpl.setNum(2000);
		deptImpl.setDate(873561600000L);
		deptImpl.setParent(null);
		deptImpl.setRemark("");
		initializer.putResource(deptImpl);
		
		//数据库中加载缓存
		String dnaSql = "define query selectDept()"
				+ " begin"
				+ " select d.recid as id,d.deptname as deptname,d.deptmaster as deptmaster,d.deptnum as deptnum,d.deptdate as deptdate,d.parent as parent,d.remark as remark,d.deptorder as deptorder "
				+ " from Department_Hjz as d"
				+ " where 1=1"
				+ " end";
	
		DBCommand dbCommand = null;
		try {
			dbCommand = context.prepareStatement(dnaSql);
			RecordSet rs = dbCommand.executeQuery();
			while(rs.next()){
				DepartmentResourceImpl Impl = new DepartmentResourceImpl();
				Impl.setId(rs.getFields().get(0).getGUID());
				Impl.setName(rs.getFields().get(1).getString());
				Impl.setMaster(rs.getFields().get(2).getString());
				Impl.setNum(rs.getFields().get(3).getInt());
				Impl.setDate(rs.getFields().get(4).getDate());
				Impl.setParent(rs.getFields().get(5).getGUID());
				Impl.setRemark(rs.getFields().get(6).getString());
				Impl.setOrder(rs.getFields().get(7).getLong());
				initializer.putResource(Impl);
			}
		} finally {
			if(dbCommand!=null){
				dbCommand.unuse();
			}
		}
	}
	
	//新增部门
	@Publish
	protected class CreateDepartmentResourceTaskHandler extends
			SimpleTaskMethodHandler<CreateDepartmentResourceTask> {

		@Override
		protected void handle(
				ResourceContext<DepartmentResource, DepartmentResourceImpl, DepartmentResourceImpl> context,
				CreateDepartmentResourceTask task) throws Throwable {
			DepartmentResourceImpl deptImpl = new DepartmentResourceImpl();
			deptImpl.setId(context.newRECID());
			deptImpl.setName(task.getName());
			deptImpl.setMaster(task.getMaster());
			deptImpl.setNum(task.getNum());
			deptImpl.setDate(task.getDate());
			deptImpl.setParent(task.getParent());
			deptImpl.setRemark(task.getRemark());
			deptImpl.setOrder(new Date().getTime());
			context.putResource(deptImpl);

			//持久化
			String dnaSql = "define insert insertDept(@recid guid,@name string,@master string,@num int,@date date,@parent guid,@remark string,@order long)"
					+ " begin"
					+ " insert into Department_Hjz(RECID,DEPTNAME,DEPTMASTER,DEPTNUM,DEPTDATE,PARENT,REMARK,DEPTORDER) values (@recid,@name,@master,@num,@date,@parent,@remark,@order)"
					+ " end";
			DBCommand dbCommand = null;
			try {
				dbCommand = context.prepareStatement(dnaSql);
				dbCommand.setArgumentValues(deptImpl.getId(),deptImpl.getName(),deptImpl.getMaster(),deptImpl.getNum(),deptImpl.getDate(),deptImpl.getParent(),deptImpl.getRemark(),deptImpl.getOrder());
				dbCommand.executeUpdate();
			} finally {
				if(dbCommand!=null){
					dbCommand.unuse();
				}
			}
			task.setResult(deptImpl);		
		}

	}
	
	//修改部门
	@Publish
	protected class UpdateDepartmentResourceTaskHandler extends
			SimpleTaskMethodHandler<UpdateDepartmentResourceTask> {

		@Override
		protected void handle(
				ResourceContext<DepartmentResource, DepartmentResourceImpl, DepartmentResourceImpl> context,
				UpdateDepartmentResourceTask task) throws Throwable {
			//modify resource
			DepartmentResourceImpl deptImpl = (DepartmentResourceImpl)context.get(DepartmentResource.class, task.getId());
			deptImpl.setName(task.getName());
			deptImpl.setMaster(task.getMaster());
			deptImpl.setNum(task.getNum());
			deptImpl.setDate(task.getDate());
			deptImpl.setParent(task.getParent());
			deptImpl.setRemark(task.getRemark());
			deptImpl.setOrder(task.getOrder());
			context.putResource(deptImpl);
			
			//update db
			String dnaSql = "define update updateDept(@recid guid,@name string,@master string,@num int,@date date,@parent guid,@remark string,@order long)"
					+ " begin"
					+ " update Department_hjz as d set DEPTNAME=@name,DEPTMASTER=@master,DEPTNUM=@num,DEPTDATE=@date,PARENT=@parent,REMARK=@remark,DEPTORDER=@order"
					+ " where d.recid = @recid"
					+ " end";
			DBCommand dbCommand = null;
			try {
				dbCommand = context.prepareStatement(dnaSql);
				dbCommand.setArgumentValues(deptImpl.getId(),deptImpl.getName(),deptImpl.getMaster(),deptImpl.getNum(),deptImpl.getDate(),deptImpl.getParent(),deptImpl.getRemark(),deptImpl.getOrder());
				dbCommand.executeUpdate();
			} finally {
				if(dbCommand!=null){
					dbCommand.unuse();
				}
			}
			task.setResult(deptImpl);		
		}

	}
	
	//删除部门
	@Publish
	protected class RemoveDepartmentResourceTaskHandler extends
	SimpleTaskMethodHandler<RemoveDepartmentResourceTask> {
		
		@Override
		protected void handle(
				ResourceContext<DepartmentResource, DepartmentResourceImpl, DepartmentResourceImpl> context,
				RemoveDepartmentResourceTask task) throws Throwable {
			GUID id = task.getId();
			context.removeResource(id);
			
			//持久化
			String dnaSql = "define delete deleteDept(@recid guid)"
						+ " begin"
						+ " delete from Department_Hjz as d"
						+ " where d.recid=@recid"
						+ " end";
			
			DBCommand dbCommand = null;
			try {
				dbCommand = context.prepareStatement(dnaSql);
				dbCommand.setArgumentValue(0, id);;
				dbCommand.executeUpdate();
			} finally {
				if(dbCommand!=null){
					dbCommand.unuse();
				}
			}
		}
	}
	
	//根据id查找部门
	@Publish
	protected class ByIdProvider extends OneKeyResourceProvider<GUID>{

		@Override
		protected GUID getKey1(
				DepartmentResourceImpl keysHolder) {
			return keysHolder.getId();
		}
	}
}
