/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.technician;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

	@Transactional(readOnly = false)
	public void save(ServiceTechnicianHoliday serviceTechnicianHoliday) {
		//最后休假日期List
		List<ServiceTechnicianHoliday> list = new ArrayList<ServiceTechnicianHoliday>();
		//获取服务人员工作时间
		List<ServiceTechnicianHoliday> workTimes = serviceTechnicianHolidayDao.getServiceTechnicianWorkTime(serviceTechnicianHoliday);
		//请假List   00:00-23:59 周几
		holidays = new ArrayList<ServiceTechnicianHoliday>();
		getHolidays(serviceTechnicianHoliday.getStartTime(), serviceTechnicianHoliday.getEndTime());

		//循环请假时间
		for (ServiceTechnicianHoliday holiday : holidays) {
			int weekDay = Integer.parseInt(holiday.getHoliday());//当前是周几
			//获取周几的工作时间
			List<ServiceTechnicianHoliday> weekDayWorkTimes = getWeekDayWorkTimes(weekDay, workTimes);
			//循环工作时间
			for (ServiceTechnicianHoliday weekDayWorkTime : weekDayWorkTimes) {
				ServiceTechnicianHoliday info = new ServiceTechnicianHoliday();
				//判断请假时间是否在工作时间内，并返回在工作时间内的数据
				info = getLastHolidays(weekDayWorkTime, holiday);
				if (null != info) {//请假时间在工作时间内
					info.setTechId(serviceTechnicianHoliday.getTechId());//技师ID
					info.setRemark(serviceTechnicianHoliday.getRemark());//备注
					list.add(info);
				}
			}
		}
		//循环插入
		for (ServiceTechnicianHoliday saveInfo : list) {
			super.save(saveInfo);
		}
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
		Timestamp workStartTime = weekDayWorkTime.getStartTime();//工作开始时间
		Timestamp workEndTime = weekDayWorkTime.getEndTime();//工作结束时间
		Timestamp holidayStartTime = holiday.getStartTime();//休假开始时间
		Timestamp holidayEndTime = holiday.getEndTime();//休假结束时间

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
	private void getHolidays(Timestamp workStartTime, Timestamp workEndTime) {
		ServiceTechnicianHoliday holiday = new ServiceTechnicianHoliday();
		DateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String newEndStr = ymd.format(workStartTime) + " 23:59:59"; //获取开始时间日最后时间
		Timestamp newEndTime = Timestamp.valueOf(newEndStr);
		int weekDay = getWeek(workStartTime);

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

	@Transactional(readOnly = false)
	public void delete(ServiceTechnicianHoliday serviceTechnicianHoliday) {
		super.delete(serviceTechnicianHoliday);
	}

	public int getOrderTechRelationHoliday(ServiceTechnicianHoliday info) {
		return serviceTechnicianHolidayDao.getOrderTechRelationHoliday(info);
	}

	public int getHolidayHistory(ServiceTechnicianHoliday info) {
		return serviceTechnicianHolidayDao.getHolidayHistory(info);
	}
	/**
	 * 获取传入时间是周几
	 *
	 * @param today
	 * @return
	 */
	public int getWeek(Timestamp today) {
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		int weekday = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (0 == weekday) {
			weekday = 7;
		}
		return weekday;
	}

}