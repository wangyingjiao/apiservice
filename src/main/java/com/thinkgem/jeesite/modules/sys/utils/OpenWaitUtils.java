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
		System.out.println("-- OpenWaitUtils---saveSendWait---" + wait.toString());
		new SaveWaitThread(wait, null, null).start();
	}

	public static void delSendWait(SysJointWait wait){
		System.out.println("-- OpenWaitUtils---delSendWait---" + wait.toString());
		new DelWaitThread(wait, null, null).start();
	}

	public static void updateNumSendWait(SysJointWait wait) {
		System.out.println("-- OpenWaitUtils---updateNumSendWait---" + wait.toString());
		new UpdateWaitThread(wait, null, null).start();
	}

	public static void updateGoodsEshopJointStatus(List<SerItemCommodityEshop> goodsEshopList) {
		if(null != goodsEshopList){
			for(SerItemCommodityEshop goodsEshop : goodsEshopList){
				System.out.println("-- OpenWaitUtils---updateGoodsEshopJointStatus---" + goodsEshop.toString());
				new UpdateGoodsEshopJointStatusThread(goodsEshop, null, null).start();
			}
		}
	}

	public static void updateGoodsEshopEnabledStatus(List<SerItemCommodityEshop> goodsEshopList) {
		if(null != goodsEshopList){
			for(SerItemCommodityEshop goodsEshop : goodsEshopList){
				System.out.println("-- OpenWaitUtils---updateGoodsEshopEnabledStatus---" + goodsEshop.toString());
				new UpdateGoodsEshopEnabledStatusThread(goodsEshop, null, null).start();
			}
		}
	}

	public static void updateGoodsEshopJointStatusAndCode(List<SerItemCommodityEshop> goodsEshopList) {
		if(null != goodsEshopList){
			for(SerItemCommodityEshop goodsEshop : goodsEshopList){
				System.out.println("-- OpenWaitUtils---updateGoodsEshopJointStatusAndCode---" + goodsEshop.toString());
				new UpdateGoodsEshopJointStatusAndCodeThread(goodsEshop, null, null).start();
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
			System.out.println("-- OpenWaitUtils---SaveWaitThread---run---" + wait.toString());
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
			System.out.println("-- OpenWaitUtils---DelWaitThread---run---" + wait.toString());
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
			System.out.println("-- OpenWaitUtils---UpdateWaitThread---run---" + wait.toString());
			sysJointWaitDao.update(wait);
		}
	}

	public static class UpdateGoodsEshopJointStatusThread extends Thread{
		private SerItemCommodityEshop goodsEshop;
		private Object handler;
		private Exception ex;
		public UpdateGoodsEshopJointStatusThread(SerItemCommodityEshop goodsEshop, Object handler, Exception ex){
			super(UpdateGoodsEshopJointStatusThread.class.getSimpleName());
			this.goodsEshop = goodsEshop;
			this.handler = handler;
			this.ex = ex;
		}
		@Override
		public void run() {
			System.out.println("-- OpenWaitUtils---UpdateGoodsEshopJointStatusThread---run---" + goodsEshop.toString());
			sysJointWaitDao.updateGoodsEshopJointStatus(goodsEshop);
		}
	}
	public static class UpdateGoodsEshopEnabledStatusThread extends Thread{
		private SerItemCommodityEshop goodsEshop;
		private Object handler;
		private Exception ex;
		public UpdateGoodsEshopEnabledStatusThread(SerItemCommodityEshop goodsEshop, Object handler, Exception ex){
			super(UpdateGoodsEshopEnabledStatusThread.class.getSimpleName());
			this.goodsEshop = goodsEshop;
			this.handler = handler;
			this.ex = ex;
		}
		@Override
		public void run() {
			System.out.println("-- OpenWaitUtils---UpdateGoodsEshopEnabledStatusThread---run---" + goodsEshop.toString());
			sysJointWaitDao.updateGoodsEshopEnabledStatus(goodsEshop);
		}
	}
	public static class UpdateGoodsEshopJointStatusAndCodeThread extends Thread{
		private SerItemCommodityEshop goodsEshop;
		private Object handler;
		private Exception ex;
		public UpdateGoodsEshopJointStatusAndCodeThread(SerItemCommodityEshop goodsEshop, Object handler, Exception ex){
			super(UpdateGoodsEshopJointStatusAndCodeThread.class.getSimpleName());
			this.goodsEshop = goodsEshop;
			this.handler = handler;
			this.ex = ex;
		}
		@Override
		public void run() {
			System.out.println("-- OpenWaitUtils---UpdateGoodsEshopJointStatusAndCodeThread---run---" + goodsEshop.toString());
			sysJointWaitDao.updateGoodsEshopJointStatusAndCode(goodsEshop);
		}
	}
}
