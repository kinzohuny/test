package btw.bill.service;


import btw.bill.impl.Bill;
import btw.bill.task.BillTask;
import btw.bill.task.SNTask;
import btw.bill.utils.FormatUtil;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.da.DBCommand;
import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.resource.ResourceContext;
import com.jiuqi.dna.core.resource.ResourceInserter;
import com.jiuqi.dna.core.resource.ResourceService;
import com.jiuqi.dna.core.service.Publish;
import com.jiuqi.dna.core.type.GUID;

public class BillService extends
		ResourceService<Bill, Bill, Bill> {

	protected BillService() {
		super("BillService");
	}
	
	@Override
	protected void initResources(Context context,
			ResourceInserter<Bill, Bill, Bill> initializer) throws Throwable {
		super.initResources(context, initializer);
		String dnaSql = "define query selectBill()"
				+ " begin"
				+ " select b.recid as id,b.dt as dt,b.sn as sn"
				+ " from bill as b"
				+ " where 1=1"
				+ " order by b.dt"
				+ " end";
	
		DBCommand dbCommand = null;
		try {
			dbCommand = context.prepareStatement(dnaSql);
			RecordSet rs = dbCommand.executeQuery();
			while(rs.next()){
				Bill b = new Bill();
				b.id = (rs.getFields().get(0).getGUID());
				b.dt = (rs.getFields().get(1).getLong());
				b.sn = (rs.getFields().get(2).getString());
				initializer.putResource(b);
			}
		} finally {
			if(dbCommand!=null){
				dbCommand.unuse();
			}
		}
	}
	
	@Publish
	protected class AddBillTaskHandler extends	SimpleTaskMethodHandler<BillTask>{

		@Override
		protected void handle(ResourceContext<Bill, Bill, Bill> context,
				BillTask task) throws Throwable {
			task.id = context.newRECID();
			task.dt = FormatUtil.getNow();
			SNTask snTask = new SNTask(task.dt);
			context.handle(snTask);
			task.sn = (FormatUtil.timeToDate(task.dt)+"-"+FormatUtil.formatSN(snTask.sn));
			
			Bill b = new Bill();
			b.id = task.id;
			b.dt = task.dt;
			b.sn = task.sn;
			context.putResource(b);
			
			String dnaSql = "define insert insertBill(@id guid,@dt long,@sn string)"
					+ " begin"
					+ " insert into BILL(RECID,DT,SN) values (@id,@dt,@sn)"
					+ " end";
			DBCommand dbCommand = null;
			try {
				dbCommand = context.prepareStatement(dnaSql);
				dbCommand.setArgumentValues(b.id,b.dt,b.sn);
				dbCommand.executeUpdate();
			}catch(Exception e){
				e.printStackTrace();
				throw new RuntimeException();
			}
			finally {
				if(dbCommand!=null){
					dbCommand.unuse();
				}
			}
		}
	}
	
	@Publish
	protected class ByIdProvider extends OneKeyResourceProvider<GUID>{

		@Override
		protected GUID getKey1(Bill keysHolder) {
			return keysHolder.id;
		}
	}
}
