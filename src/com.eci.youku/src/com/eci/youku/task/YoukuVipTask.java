package com.eci.youku.task;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.eci.youku.dao.VipRecDao;
import com.eci.youku.handle.SuyuSmsHandle;
import com.eci.youku.model.VipRecModel;
import com.eci.youku.util.StringUtils;
import com.eci.youku.vip.YoukuVip;
import com.eci.youku.vip.YoukuVipResult;
import com.taobao.api.domain.BmcResult;

public class YoukuVipTask implements Runnable{

	private static final Logger logger = Logger.getLogger(YoukuVipTask.class);
	
	private VipRecDao vipRecDao;
	
	//{还记得我们爱过！回来吧，接受这份迟来的礼物！现为您当前手机号开通优酷黄金${type}会员，初始密码${pswd}，请及时登录修改。}
	private static final Long new_templateId = 846L;
	//{优酷黄金会员${type}个月的使用权已至账，请通过登录优酷会员中心-我的账户-交易记录查询。快点分享给小伙伴吧 dwz.cn/2cnPUH}
	private static final Long recharge_templateId = 845L;
	
	public YoukuVipTask() {
		vipRecDao = new VipRecDao();
	}

	@Override
	public void run() {
		logger.info("YoukuVipTask start...");
		try {
			List<VipRecModel> list = vipRecDao.queryToRunList();
			
			//先改为发放中的状态，防止有异常时重复发放
			vipRecDao.updateVipStatus(list, 3);
			
			for(VipRecModel model : list){
				sendVip(model);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		logger.info("YoukuVipTask finish...");
	}
	
	private void sendVip(VipRecModel model) throws IOException, SQLException{
		logger.info("vip send : mobile="+model.getMobile()+",type="+model.getVip_type());
		// 发放VIP
		YoukuVip youkuVip = new YoukuVip(model.getMobile());

		try {
			if(model.getVip_type() == 1){
				youkuVip.createMonth();
			}else if(model.getVip_type() == 3){
				youkuVip.createQuarter();
			}else{
				logger.error("错误的会员类型："+JSON.toJSONString(model));
			}
			//检查发放结果并修改状态
			if(StringUtils.isNotEmpty(youkuVip.getBack())){
				YoukuVipResult result = youkuVip.getResult();
				if("1".equals(result.getError())){
					model.setVip_status(1);
				}else{
					model.setVip_status(2);
				}
				model.setVip_back(youkuVip.getBack());
				model.setVip_mobile(result.getMobile());
				model.setVip_password(result.getPassword());
				model.setVip_errmsg(result.getErrmsg());
			}else{
				model.setVip_status(2);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			model.setVip_status(2);
			model.setVip_errmsg(e.getMessage());
		}
		
		model.setVip_time(new Timestamp(System.currentTimeMillis()));
		//vip发放状态入库
		logger.info("vip vip send finish : mobile="+model.getMobile()+",type="+model.getVip_type()+",status="+model.getVip_status());
		vipRecDao.update(model);
		
		//发送通知短信
		if(1 == model.getVip_status()){
			BmcResult bmcResult = null;
			try {
				if(StringUtils.isNotEmpty(model.getVip_password())){
					//有密码是新开通用户
					bmcResult = SuyuSmsHandle.send(new_templateId, "{\"type\":\""+(model.getVip_type()==1?"月卡":"季卡")+"\",\"pswd\":\""+model.getVip_password()+"\"}", null, model.getMobile());
				}else{
					//没密码的是老用户续费
					bmcResult = SuyuSmsHandle.send(recharge_templateId, "{\"type\":\""+model.getVip_type()+"\"}", null, model.getMobile());
				}
				//修改短信状态
				model.setSms_status(bmcResult.getSuccessful()?1:2);
				model.setSms_errormsg(bmcResult.getMessage());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				model.setSms_status(2);
				model.setSms_errormsg(e.getMessage());
			}
			model.setSms_time(new Timestamp(System.currentTimeMillis()));

		}else{
			//发放不成功的用户不短信通知
			model.setSms_status(0);
		}
		//短信状态入库
		logger.info("vip sms send finish : mobile="+model.getMobile()+",type="+model.getVip_type()+",status="+model.getSms_status());
		vipRecDao.update(model);
	}
	
}
