package btw.bill.service;


import btw.bill.impl.SN;
import btw.bill.task.SNTask;
import btw.bill.utils.FormatUtil;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.da.DBCommand;
import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.resource.ResourceContext;
import com.jiuqi.dna.core.resource.ResourceInserter;
import com.jiuqi.dna.core.resource.ResourceService;
import com.jiuqi.dna.core.service.Publish;

public class SNService extends ResourceService<SN, SN, SN> {

	protected SNService() {
		super("SNService");
	}
	
	@Override
	protected void initResources(Context context,
			ResourceInserter<SN, SN, SN> initializer) throws Throwable {
		super.initResources(context, initializer);
		String dnaSql = "define query selectSn()"
				+ " begin"
				+ " select s.recid as id,s.dt as dt,s.sn as sn"
				+ " from sn as s"
				+ " where 1=1"
				+ " end";
	
		DBCommand dbCommand = null;
		try {
			dbCommand = context.prepareStatement(dnaSql);
			RecordSet rs = dbCommand.executeQuery();
			while(rs.next()){
				SN s = new SN();
				s.id = (rs.getFields().get(0).getGUID());
				s.dt = (rs.getFields().get(1).getString());
				s.sn = (rs.getFields().get(2).getInt());
				initializer.putResource(s);
			}
		} finally {
			if(dbCommand!=null){
				dbCommand.unuse();
			}
		}
	}
	
	@Publish
	protected class SNTaskHandler extends	SimpleTaskMethodHandler<SNTask>{

		@Override
		protected void handle(ResourceContext<SN, SN, SN> context, SNTask task)
				throws Throwable {
			boolean isNew = false;
			long now = task.time;
			task.dt = FormatUtil.timeToDate(now);
			SN sn = context.find(SN.class, task.dt);
			if(sn==null){
				isNew = true;
				sn = new SN();
				sn.id = context.newRECID();
				sn.dt = task.dt;
				sn.sn = 188;
			}else{
				sn.sn++;
			}
			context.putResource(sn);
			
			String dnaSql = "";
			if(isNew){
				dnaSql = "define insert insertSN(@id guid,@dt string,@sn int)"
						+ " begin"
						+ " insert into SN(RECID,DT,SN) values (@id,@dt,@sn)"
						+ " end";
			}else{
				dnaSql = "define update updateSN(@id guid,@dt string,@sn int)"
						+ " begin"
						+ " update SN as s" 
						+ " set recid=@id,sn=@sn"
						+ " where s.dt=@dt and 1=1"
						+ " end";
			}
			DBCommand dbCommand = null;
			try {
				dbCommand = context.prepareStatement(dnaSql);
				dbCommand.setArgumentValues(sn.id,sn.dt,sn.sn);
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
			task.sn = sn.sn;
		}
	}
	
	@Publish
	protected class ByIdProvider extends OneKeyResourceProvider<String>{

		@Override
		protected String getKey1(SN keysHolder) {
			return keysHolder.dt;
		}
		
	}

}
