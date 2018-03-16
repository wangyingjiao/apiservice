/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.utils;

import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodityEshop;
import com.thinkgem.jeesite.modules.sys.dao.SysJointWaitDao;
import com.thinkgem.jeesite.modules.sys.entity.SysJointWait;

import java.util.List;

/**
 * 对接待执行记录表
 * @author ThinkGem
 * @version 2014-11-7
 */
public class OpenWaitUtils {
	
	private static SysJointWaitDao sysJointWaitDao = SpringContextHolder.getBean(SysJointWaitDao.class);

	public static void saveSendWait(SysJointWait wait){
		new SaveWaitThread(wait, null, null).start();
	}

	public static void delSendWait(SysJointWait wait){
		new DelWaitThread(wait, null, null).start();
	}

	public static void updateNumSendWait(SysJointWait wait) {
		new UpdateWaitThread(wait, null, null).start();
	}

	public static void delGoodsEshop(String eshopCode, List<String> jointGoodsCodes) {
		if(null != jointGoodsCodes){
			SerItemCommodityEshop goodsEshop = new SerItemCommodityEshop();
			for(String code : jointGoodsCodes){
				goodsEshop = new SerItemCommodityEshop();
				goodsEshop.setEshopCode(eshopCode);
				goodsEshop.setJointGoodsCode(code);
				new DelGoodsEshopThread(goodsEshop, null, null).start();
			}
		}
	}

	public static void updateGoodsEshopJointStatus(String eshopCode, List<String> jointGoodsCodes, String remove_fail) {
		if(null != jointGoodsCodes){
			SerItemCommodityEshop goodsEshop = new SerItemCommodityEshop();
			for(String code : jointGoodsCodes){
				goodsEshop = new SerItemCommodityEshop();
				goodsEshop.setEshopCode(eshopCode);
				goodsEshop.setJointGoodsCode(code);
				goodsEshop.setJointStatus(remove_fail);
				new UpdateGoodsEshopThread(goodsEshop, null, null).start();
			}
		}
	}

	public static class SaveWaitThread extends Thread{
		private SysJointWait wait;
		private Object handler;
		private Exception ex;
		public SaveWaitThread(SysJointWait wait, Object handler, Exception ex){
			super(SaveWaitThread.class.getSimpleName());
			this.wait = wait;
			this.handler = handler;
			this.ex = ex;
		}
		@Override
		public void run() {
			wait.preInsert();
			sysJointWaitDao.insert(wait);
		}
	}

	public static class DelWaitThread extends Thread{
		private SysJointWait wait;
		private Object handler;
		private Exception ex;
		public DelWaitThread(SysJointWait wait, Object handler, Exception ex){
			super(DelWaitThread.class.getSimpleName());
			this.wait = wait;
			this.handler = handler;
			this.ex = ex;
		}
		@Override
		public void run() {
			sysJointWaitDao.delete(wait);
		}
	}

	public static class UpdateWaitThread extends Thread{
		private SysJointWait wait;
		private Object handler;
		private Exception ex;
		public UpdateWaitThread(SysJointWait wait, Object handler, Exception ex){
			super(UpdateWaitThread.class.getSimpleName());
			this.wait = wait;
			this.handler = handler;
			this.ex = ex;
		}
		@Override
		public void run() {
			wait.preUpdate();
			sysJointWaitDao.update(wait);
		}
	}

	public static class DelGoodsEshopThread extends Thread{
		private SerItemCommodityEshop goodsEshop;
		private Object handler;
		private Exception ex;
		public DelGoodsEshopThread(SerItemCommodityEshop goodsEshop, Object handler, Exception ex){
			super(DelGoodsEshopThread.class.getSimpleName());
			this.goodsEshop = goodsEshop;
			this.handler = handler;
			this.ex = ex;
		}
		@Override
		public void run() {
			sysJointWaitDao.deleteGoodsEshop(goodsEshop);
		}
	}

	public static class UpdateGoodsEshopThread extends Thread{
		private SerItemCommodityEshop goodsEshop;
		private Object handler;
		private Exception ex;
		public UpdateGoodsEshopThread(SerItemCommodityEshop goodsEshop, Object handler, Exception ex){
			super(UpdateGoodsEshopThread.class.getSimpleName());
			this.goodsEshop = goodsEshop;
			this.handler = handler;
			this.ex = ex;
		}
		@Override
		public void run() {
			sysJointWaitDao.updateGoodsEshop(goodsEshop);
		}
	}
}
