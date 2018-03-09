/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.technician;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.DateUtilsEntity;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianHoliday;
import com.thinkgem.jeesite.modules.service.dao.technician.ServiceTechnicianHolidayDao;

/**
 * 服务技师休假时间Service
 * @author a
 * @version 2017-11-29
 */
@Service
@Transactional(readOnly = true)
public class ServiceTechnicianHolidayService extends CrudService<ServiceTechnicianHolidayDao, ServiceTechnicianHoliday> {
	@Autowired
	ServiceTechnicianHolidayDao serviceTechnicianHolidayDao;

	public ServiceTechnicianHoliday get(String id) {
		return super.get(id);
	}

	public List<ServiceTechnicianHoliday> findList(ServiceTechnicianHoliday serviceTechnicianHoliday) {
		return super.findList(serviceTechnicianHoliday);
	}

	public Page<ServiceTechnicianHoliday> findPage(Page<ServiceTechnicianHoliday> page, ServiceTechnicianHoliday serviceTechnicianHoliday) {
		serviceTechnicianHoliday.getSqlMap().put("dsf", dataStatioRoleFilter(UserUtils.getUser(), "b"));
		return super.findPage(page, serviceTechnicianHoliday);
	}
	//app获取技师休假列表
	public Page<ServiceTechnicianHoliday> appFindPage(Page<ServiceTechnicianHoliday> page, ServiceTechnicianHoliday serviceTechnicianHoliday) {
		serviceTechnicianHoliday.setPage(page);
		if (StringUtils.isNotBlank(serviceTechnicianHoliday.getTechId())){
			List<ServiceTechnicianHoliday> serviceTechnicianHolidays = dao.appFindPage(serviceTechnicianHoliday);
			if (serviceTechnicianHolidays != null && serviceTechnicianHolidays.size()>0) {
				for (ServiceTechnicianHoliday holi : serviceTechnicianHolidays) {
					Date startTime = holi.getStartTime();
					Date date = new Date();
					if (startTime.after(date)) {
						holi.setIsExpire("no");
					}
				}
			}
			page.setList(serviceTechnicianHolidays);
		}
		return page;
	}
	//获取用户工作时间
	public List<ServiceTechnicianHoliday> getServiceTechnicianWorkTime(ServiceTechnicianHoliday serviceTechnicianHoliday){
		List<ServiceTechnicianHoliday> serviceTechnicianWorkTime = serviceTechnicianHolidayDao.getServiceTechnicianWorkTime(serviceTechnicianHoliday);
		return serviceTechnicianWorkTime;
	}

	@Transactional(readOnly = false)
	public int savePc(ServiceTechnicianHoliday serviceTechnicianHoliday) {
		int i=0;
		//最后休假日期List
		List<ServiceTechnicianHoliday> list = new ArrayList<ServiceTechnicianHoliday>();
		//获取服务人员工作时间
		List<ServiceTechnicianHoliday> workTimes = serviceTechnicianHolidayDao.getServiceTechnicianWorkTime(serviceTechnicianHoliday);
		//请假List   00:00-23:59 周几
		List<ServiceTechnicianHoliday> holidaysList = getHolidaysList(serviceTechnicianHoliday.getStartTime(), serviceTechnicianHoliday.getEndTime());

		//循环请假时间
		for (ServiceTechnicianHoliday holiday : holidaysList) {
			int weekDay = Integer.parseInt(holiday.getHoliday());//当前循环的休假时间是周几
			//获取周几的工作时间
			List<ServiceTechnicianHoliday> weekDayWorkTimes = getWeekDayWorkTimes(weekDay, workTimes);
			//循环工作时间
			for (ServiceTechnicianHoliday weekDayWorkTime : weekDayWorkTimes) {

				//判断请假时间是否在工作时间内，并返回在工作时间内的数据
				DateUtilsEntity entity = DateUtils.findDatesRepeatTime(weekDayWorkTime.getStartTime(),weekDayWorkTime.getEndTime(),
						holiday.getStartTime(),holiday.getEndTime());
				if (null != entity) {//请假时间在工作时间内
					ServiceTechnicianHoliday info = new ServiceTechnicianHoliday();
					info.setStartTime(entity.getStartTime());
					info.setEndTime(entity.getEndTime());
					info.setTechId(serviceTechnicianHoliday.getTechId());//技师ID
					info.setRemark(serviceTechnicianHoliday.getRemark());//备注
					list.add(info);
				}
			}
		}
		//循环插入
		for (ServiceTechnicianHoliday saveInfo : list) {
			i = super.savePc(saveInfo);
		}
		return i;
	}
	//app新增休假
	@Transactional(readOnly = false)
	public int appSave(ServiceTechnicianHoliday serviceTechnicianHoliday) {
		int i = 0;
		//服务人员在请假时间内是否有未完成的订单
		int orderTechRelationHoliday = serviceTechnicianHolidayDao.getOrderTechRelationHoliday(serviceTechnicianHoliday);
		if (orderTechRelationHoliday > 0) {
			throw new ServiceException("服务人员有未完成订单,不可请假");
		}
		//服务人员在请假时间内是否有请假
		List<ServiceTechnicianHoliday> hoList = serviceTechnicianHolidayDao.getHolidayHistory(serviceTechnicianHoliday);
		int num = 0;
		if (null != hoList && hoList.size() > 0) {
			for (ServiceTechnicianHoliday holiday : hoList) {
				if (!DateUtils.checkDatesRepeat(holiday.getStartTime(), holiday.getEndTime(), serviceTechnicianHoliday.getStartTime(), serviceTechnicianHoliday.getEndTime())) {
					num++;
				}
			}
		}
		if (num > 0) {
			throw new ServiceException("请假时间冲突");
		}

		//最后休假日期List
		List<ServiceTechnicianHoliday> list = new ArrayList<ServiceTechnicianHoliday>();
		//获取服务人员工作时间
		List<ServiceTechnicianHoliday> workTimes = serviceTechnicianHolidayDao.getServiceTechnicianWorkTime(serviceTechnicianHoliday);
		//请假List   00:00-23:59 周几
		List<ServiceTechnicianHoliday> holidaysList = getHolidaysList(serviceTechnicianHoliday.getStartTime(), serviceTechnicianHoliday.getEndTime());
		//循环请假时间
		if (holidaysList != null && holidaysList.size() > 0){
			for (ServiceTechnicianHoliday holiday : holidaysList) {
				int weekDay = Integer.parseInt(holiday.getHoliday());//当前循环的休假时间是周几
				//获取周几的工作时间
				List<ServiceTechnicianHoliday> weekDayWorkTimes = getWeekDayWorkTimes(weekDay, workTimes);
				//循环工作时间
				for (ServiceTechnicianHoliday weekDayWorkTime : weekDayWorkTimes) {

					//判断请假时间是否在工作时间内，并返回在工作时间内的数据
					DateUtilsEntity entity = DateUtils.findDatesRepeatTime(weekDayWorkTime.getStartTime(), weekDayWorkTime.getEndTime(),
							holiday.getStartTime(), holiday.getEndTime());
					if (null != entity) {//请假时间在工作时间内
						ServiceTechnicianHoliday info = new ServiceTechnicianHoliday();
						info.setStartTime(entity.getStartTime());
						info.setEndTime(entity.getEndTime());
						info.setTechId(serviceTechnicianHoliday.getTechId());//技师ID
						info.setRemark(serviceTechnicianHoliday.getRemark());//备注
						list.add(info);
					}
				}
			}
		}
		//循环插入
		if (list!=null && list.size()>0) {
			for (ServiceTechnicianHoliday saveInfo : list) {
				i = super.saveAPP(saveInfo);
			}
		}
		return i;
	}
	/**
	 * 获取休假时间
	 *
	 * @param weekDayWorkTime
	 * @param holiday
	 * @return
	 */
	private ServiceTechnicianHoliday getLastHolidays(ServiceTechnicianHoliday weekDayWorkTime, ServiceTechnicianHoliday holiday) {
		ServiceTechnicianHoliday info = new ServiceTechnicianHoliday();
		Date workStartTime = weekDayWorkTime.getStartTime();//工作开始时间
		Date workEndTime = weekDayWorkTime.getEndTime();//工作结束时间
		Date holidayStartTime = holiday.getStartTime();//休假开始时间
		Date holidayEndTime = holiday.getEndTime();//休假结束时间

		//数据库中工作时间只有时分秒 拼接年月日
		DateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat hms = new SimpleDateFormat("HH:mm:ss");
		String newWorkStartTimeStr = ymd.format(holidayStartTime) + " " + hms.format(workStartTime);
		String newWorkEndTimeStr = ymd.format(holidayStartTime) + " " + hms.format(workEndTime);
		workStartTime = Timestamp.valueOf(newWorkStartTimeStr);
		workEndTime = Timestamp.valueOf(newWorkEndTimeStr);

		//1.工作开始时间早于休假开始时间
		if (holidayStartTime.after(workStartTime)) {
			//1.1工作结束时间早于或等于休假开始时间
			if (holidayStartTime.after(workEndTime) || holidayStartTime.equals(workEndTime)) {
				//没有休假
				return null;
			} else if ((holidayStartTime.before(workEndTime) && holidayEndTime.after(workEndTime)) || holidayStartTime.equals(workEndTime)) {//1.2工作结束时间晚于休假开始时间并且早于或等于休假结束时间 即：在休假时间内
				//有休假 起：休假开始时间 止：工作结束时间
				info.setStartTime(holidayStartTime);
				info.setEndTime(workEndTime);
			} else if (holidayEndTime.before(workEndTime)) {//1.3工作结束时间晚于休假结束时间
				//有休假 起：休假开始时间 止：休假结束时间
				info.setStartTime(holidayStartTime);
				info.setEndTime(holidayEndTime);
			}
		} else if (holidayStartTime.equals(workStartTime) || (holidayStartTime.before(workStartTime) && holidayEndTime.after(workStartTime))) {//2工作开始时间晚于或等于休假开始时间并且早于休假结束时间 即：在休假时间内
			//2.1工作结束时间早于或等于休假结束时间
			if (holidayEndTime.after(workEndTime) || holidayEndTime.equals(workEndTime)) {
				//有休假 起：工作开始时间 止：工作结束时间
				info.setStartTime(workStartTime);
				info.setEndTime(workEndTime);
			} else if (holidayEndTime.before(workEndTime)) {
				//2.2工作结束时间晚于休假结束时间
				//有休假 起：工作开始时间 止：休假结束时间
				info.setStartTime(workStartTime);
				info.setEndTime(holidayEndTime);
			}
		} else if (holidayEndTime.before(workStartTime) || holidayEndTime.equals(workStartTime)) {//3工作开始时间晚于或等于休假结束时间
			//没有休假
			return null;
		}

		return info;
	}

	/**
	 * 每天工作时间
	 *
	 * @param weekDay
	 * @param workTimes
	 * @return
	 */
	private List<ServiceTechnicianHoliday> getWeekDayWorkTimes(int weekDay, List<ServiceTechnicianHoliday> workTimes) {
		List<ServiceTechnicianHoliday> weekDayWorkTimes = new ArrayList<ServiceTechnicianHoliday>();
		for (ServiceTechnicianHoliday info : workTimes) {
			if (weekDay == Integer.valueOf(info.getHoliday())) {
				weekDayWorkTimes.add(info);
			}
		}
		return weekDayWorkTimes;
	}

	List<ServiceTechnicianHoliday> holidays;

	/**
	 * 请假List
	 *
	 * @param workStartTime
	 * @param workEndTime
	 * @return
	 */
	private void getHolidays(Date workStartTime, Date workEndTime) {
		ServiceTechnicianHoliday holiday = new ServiceTechnicianHoliday();
		DateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String newEndStr = ymd.format(workStartTime) + " 23:59:59"; //获取开始时间日最后时间
		Timestamp newEndTime = Timestamp.valueOf(newEndStr);
		int weekDay = DateUtils.getWeekNum(workStartTime);

		if (workEndTime.before(newEndTime)) {//最后时间大于结束时间
			holiday.setStartTime(workStartTime);
			holiday.setEndTime(workEndTime);
			holiday.setHoliday(String.valueOf(weekDay));
			holidays.add(holiday);
		} else {//最后时间小于结束时间
			holiday.setStartTime(workStartTime);
			holiday.setEndTime(newEndTime);
			holiday.setHoliday(String.valueOf(weekDay));
			holidays.add(holiday);

			//最后时间加一秒作为开始时间 递归调用
			Calendar ca = Calendar.getInstance();
			ca.setTime(newEndTime);
			ca.add(Calendar.SECOND, 1);
			newEndTime = Timestamp.valueOf(ymdhms.format(ca.getTime()));
			getHolidays(newEndTime, workEndTime);
		}
	}

	/**
	 * 把请假时间按天分割
	 * 如：2018-01-01 09:00:00  2018-01-03 18:00:00
	 * 返回三条数据：
	 * 	2018-01-01 09:00:00 2018-01-01 23:59:59
	 * 	2018-01-02 00:00:00 2018-01-02 23:59:59
	 * 	2018-01-03 00:00:00 2018-01-03 18:00:00
	 *
	 * @param workStartTime
	 * @param workEndTime
	 * @return
	 */
	private static List<ServiceTechnicianHoliday>  getHolidaysList(Date workStartTime, Date workEndTime) {
		if(workStartTime != null && workEndTime != null){//开始结束时间不为空
			List<ServiceTechnicianHoliday> holidayList = new ArrayList<>();
			Date newFirstTime = workStartTime;
			Date newEndTime = DateUtils.getDateLastTime(workStartTime);
			while (workEndTime.after(newEndTime)){//结束时间在新的结束时间之后
				ServiceTechnicianHoliday holiday = new ServiceTechnicianHoliday();
				int weekDay = DateUtils.getWeekNum(newFirstTime);//周几
				holiday.setStartTime(newFirstTime);
				holiday.setEndTime(newEndTime);
				holiday.setHoliday(String.valueOf(weekDay));
				holidayList.add(holiday);

				newFirstTime = DateUtils.getDateFirstTime(DateUtils.addDays(newFirstTime,1));//新的开始时间
				newEndTime = DateUtils.addDays(newEndTime,1);//新的结束时间

			}
			if(newFirstTime.getTime() != workEndTime.getTime()) {//最后一天的数据(2018-01-03 00:00:00 2018-01-03 18:00:00) 如果只有00:00:00 则不考虑
				ServiceTechnicianHoliday holiday = new ServiceTechnicianHoliday();
				int weekDay = DateUtils.getWeekNum(newFirstTime);//周几
				holiday.setStartTime(newFirstTime);
				holiday.setEndTime(workEndTime);
				holiday.setHoliday(String.valueOf(weekDay));
				holidayList.add(holiday);
			}

			return holidayList;
		}else {
			return null;
		}
	}
	//app删除休假
	@Transactional(readOnly = false)
	public int delete1(ServiceTechnicianHoliday serviceTechnicianHoliday) {
		return dao.delete(serviceTechnicianHoliday);
	}
	//修改中
	//app修改休假
	@Transactional(readOnly = false)
	public int saveHoliday(ServiceTechnicianHoliday serviceTechnicianHoliday) {
		int i=0;
		//去数据库中查询出这个休假
		ServiceTechnicianHoliday holiday = dao.get(serviceTechnicianHoliday.getId());
		String reviewStatus = holiday.getReviewStatus();
		if (StringUtils.isNotBlank(reviewStatus) && "no".equals(reviewStatus)){
			i = dao.update(serviceTechnicianHoliday);
		}
		return i;
	}

	public int getOrderTechRelationHoliday(ServiceTechnicianHoliday info) {
		return serviceTechnicianHolidayDao.getOrderTechRelationHoliday(info);
	}

	public int getHolidayHistory(ServiceTechnicianHoliday info) {
		List<ServiceTechnicianHoliday> list = serviceTechnicianHolidayDao.getHolidayHistory(info);
		int num = 0;
		if(null != list && list.size()>0){
			for(ServiceTechnicianHoliday holiday : list){
				if(!DateUtils.checkDatesRepeat(holiday.getStartTime(),holiday.getEndTime(),info.getStartTime(),info.getEndTime())){
					num++;
				}
			}
		}
		return num;
	}
}