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
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.technician.ServiceTechnicianInfoDao;
import com.thinkgem.jeesite.modules.service.dao.technician.TechScheduleDao;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.TechScheduleInfo;
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

	@Autowired
	TechScheduleDao techScheduleDao;

    @Autowired
    private ServiceTechnicianInfoDao serviceTechnicianInfoDao;

	public ServiceTechnicianHoliday get(String id) {
		return super.get(id);
	}

	public List<ServiceTechnicianHoliday> findList(ServiceTechnicianHoliday serviceTechnicianHoliday) {
		return super.findList(serviceTechnicianHoliday);
	}
	//后台休假列表
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

	//审核未通过的休假详情
	public ServiceTechnicianHoliday getHolidayById(ServiceTechnicianHoliday serviceTechnicianHoliday){
		serviceTechnicianHoliday.setReviewStatus("no");
		ServiceTechnicianHoliday holidayById = serviceTechnicianHolidayDao.getHolidayById(serviceTechnicianHoliday);
		return holidayById;
	}


	//审核app的休假
    @Transactional(readOnly = false)
    public int reviewedHoliday(ServiceTechnicianHoliday serviceTechnicianHoliday) {
        //根据id查询出对应的休假
        ServiceTechnicianHoliday getHoliday = dao.get(serviceTechnicianHoliday.getId());
        if (getHoliday == null){
            throw new ServiceException("未找到该休假信息，请重新输入");
        }
        //审核状态是yes 理由清空传“”
        if ("yes".equals(serviceTechnicianHoliday.getReviewStatus())){
			serviceTechnicianHoliday.setFailReason("");
		}
        serviceTechnicianHoliday.preUpdate();
        int i = dao.updateHoliday(serviceTechnicianHoliday);
        //增加排期表
        if (i > 0){
            //查询数据库中 排期表是否有限制
            ServiceTechnicianHoliday holiday1 = dao.get(serviceTechnicianHoliday.getId());
			//如果传过来的是审核通过 查排期表是否有订单有则抛异常  没有就增加排期表
            if ("yes".equals(serviceTechnicianHoliday.getReviewStatus())) {
            	//查询是否有排期表 有则
				TechScheduleInfo techScheduleInfo=new TechScheduleInfo();
				techScheduleInfo.setStartTime(holiday1.getStartTime());
				techScheduleInfo.setEndTime(holiday1.getEndTime());
				techScheduleInfo.setTechId(holiday1.getTechId());
				List<TechScheduleInfo> scheduleByTechId = techScheduleDao.getScheduleByTechId(techScheduleInfo);
				if (scheduleByTechId !=null && scheduleByTechId.size()>0){
					throw new ServiceException("已有排期，不可休假");
				}
                //getHolidaysList 方法传入开始和结束时间  返回一个list是两个时间中间的时间集合
                List<ServiceTechnicianHoliday> holidaysList = getHolidaysList(holiday1.getStartTime(), holiday1.getEndTime());
                if (holidaysList != null && holidaysList.size() > 0) {
                    for (ServiceTechnicianHoliday holiday : holidaysList) {
                        int weekDay = Integer.parseInt(holiday.getHoliday());//当前循环的休假时间是周几
                        TechScheduleInfo info = new TechScheduleInfo();
                        info.setTechId(holiday1.getTechId());
                        info.setScheduleWeek(weekDay);
                        Date dateFirstTime = DateUtils.getDateFirstTime(holiday.getStartTime());
                        info.setScheduleDate(dateFirstTime);
                        info.setStartTime(holiday.getStartTime());
                        info.setEndTime(holiday.getEndTime());
                        info.setTypeId(holiday1.getId());
                        info.setType("holiday");
                        info.setRemark(holiday1.getRemark());
                        info.preInsert();
                        //将排期表插入数据库
                        i = techScheduleDao.insertSchedule(info);
                    }
                }
            }
        }
        return i;
    }
	//后台休假新增
    @Transactional(readOnly = false)
	public int savePc(ServiceTechnicianHoliday serviceTechnicianHoliday) {
		int i=0;
		//查询排期表是否有数据
		TechScheduleInfo scheduleInfo1=new TechScheduleInfo();
		scheduleInfo1.setTechId(serviceTechnicianHoliday.getTechId());
		scheduleInfo1.setStartTime(serviceTechnicianHoliday.getStartTime());
		scheduleInfo1.setEndTime(serviceTechnicianHoliday.getEndTime());
		List<TechScheduleInfo> scheduleByTechId = techScheduleDao.getScheduleByTechId(scheduleInfo1);
		if (scheduleByTechId != null && scheduleByTechId.size() >0){
			throw new ServiceException("已有休假或者订单 不可休假");
		}
		//插入到数据库中
		serviceTechnicianHoliday.setReviewStatus("yes");
		serviceTechnicianHoliday.setSource("sys");
		int a = super.savePc(serviceTechnicianHoliday);
		//将排期表插入到数据库中
		if (a > 0){
			ServiceTechnicianHoliday getHoliday = serviceTechnicianHolidayDao.get(serviceTechnicianHoliday.getId());
			List<ServiceTechnicianHoliday> holidaysList = getHolidaysList(getHoliday.getStartTime(), getHoliday.getEndTime());
			if (holidaysList != null && holidaysList.size() > 0) {
				for (ServiceTechnicianHoliday holiday : holidaysList) {
					TechScheduleInfo scheduleInfo=new TechScheduleInfo();
					//当前循环的休假时间是周几
					int weekDay = Integer.parseInt(holiday.getHoliday());
					scheduleInfo.setScheduleWeek(weekDay);
					Date dateFirstTime = DateUtils.getDateFirstTime(holiday.getStartTime());
					scheduleInfo.setTechId(serviceTechnicianHoliday.getTechId());
					scheduleInfo.setScheduleDate(dateFirstTime);
					scheduleInfo.setStartTime(holiday.getStartTime());
					scheduleInfo.setEndTime(holiday.getEndTime());
					scheduleInfo.setType("holiday");
					scheduleInfo.setTypeId(getHoliday.getId());
					scheduleInfo.setRemark(getHoliday.getRemark());
					scheduleInfo.preInsert();
					i = techScheduleDao.insertSchedule(scheduleInfo);
				}
			}
		}
		return i;
	}
	//app新增休假
	@Transactional(readOnly = false)
	public int appSave(ServiceTechnicianHoliday serviceTechnicianHoliday) {
		int i = 0;
		//已经离职的技师不可休假
		ServiceTechnicianInfo info=new ServiceTechnicianInfo();
		info.setId(serviceTechnicianHoliday.getTechId());
		ServiceTechnicianInfo serviceTechnicianInfo = serviceTechnicianInfoDao.appFindTech(info);
		String jobStatus = serviceTechnicianInfo.getJobStatus();
		String jobNature = serviceTechnicianInfo.getJobNature();
		if (StringUtils.isNotBlank(jobStatus) && "leave".equals(jobStatus)){
			throw new ServiceException("技师已经离职,不可请假");
		}
		if (StringUtils.isNotBlank(jobNature) && "part_time".equals(jobNature)){
			throw new ServiceException("兼职技师,不可请假");
		}
		//转存对象为排期表
		TechScheduleInfo techScheduleInfo=new TechScheduleInfo();
		techScheduleInfo.setTechId(serviceTechnicianHoliday.getTechId());
		techScheduleInfo.setStartTime(serviceTechnicianHoliday.getStartTime());
		techScheduleInfo.setEndTime(serviceTechnicianHoliday.getEndTime());
		int holidayList = serviceTechnicianHolidayDao.getHolidayList(serviceTechnicianHoliday);
		if (holidayList > 0){
			throw new ServiceException("服务人员已有休假,不可再次请假");
		}
		//查询服务技师排期表 是否有数据  有则不可请假
		List<TechScheduleInfo> scheduleByTechId = techScheduleDao.getScheduleByTechId(techScheduleInfo);
		if (scheduleByTechId != null && scheduleByTechId.size() > 0){
			throw new ServiceException("服务人员有排期,不可请假");
		}
		//将假期插入数据库 app添加休假状态为submit
		serviceTechnicianHoliday.setReviewStatus("submit");
		serviceTechnicianHoliday.setSource("app");
		i = super.saveAPP(serviceTechnicianHoliday);
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
	public int appDelete(ServiceTechnicianHoliday serviceTechnicianHoliday) {
        int i = 0;
        ServiceTechnicianHoliday holiday = dao.get(serviceTechnicianHoliday.getId());
        if (holiday == null) {
            throw new ServiceException("未找到该休假信息");
        }
		List<TechScheduleInfo> orderSchedule =null;
		TechScheduleInfo info = new TechScheduleInfo();
        //如果是已经审核通过的需要去判断排期表
        if ("yes".equals(holiday.getReviewStatus())) {
			info.setTechId(serviceTechnicianHoliday.getTechId());
			info.setType("holiday");
			info.setTypeId(serviceTechnicianHoliday.getId());
			orderSchedule = techScheduleDao.getOrderSchedule(info);
			if (orderSchedule == null || orderSchedule.size() < 1) {
				throw new ServiceException("未在排期表中找到该休假信息");
			}
		}
        //删除休假表（修改有效状态）
		serviceTechnicianHoliday.appPreUpdate();
        i = dao.delete(serviceTechnicianHoliday);
        //如果是删除审核通过的休假 还需要把排期表删除
		if (i > 0) {
	    	if ("yes".equals(holiday.getReviewStatus())) {
				//删除排期表（修改有效状态）
				for (TechScheduleInfo scheduleInfo:orderSchedule){
					scheduleInfo.appPreUpdate();
					techScheduleDao.deleteSchedule(scheduleInfo);
				}
            }
        }else {
			throw new ServiceException("删除休假表失败");
		}
        return i;
    }
    //删除休假
	@Transactional(readOnly = false)
	public void deleteHoliday(ServiceTechnicianHoliday serviceTechnicianHoliday) {
		//先删除休假表 再删除排期表
		ServiceTechnicianHoliday holiday = serviceTechnicianHolidayDao.get(serviceTechnicianHoliday.getId());
		serviceTechnicianHoliday.preUpdate();
		int i = dao.delete(serviceTechnicianHoliday);
		if (i > 0){
			//如果删除的是已经审核通过的休假 则需要把对应的排期表删除
			if (StringUtils.isNotBlank(holiday.getReviewStatus()) && "yes".equals(holiday.getReviewStatus())){
				TechScheduleInfo info=new TechScheduleInfo();
				info.setType("holiday");
				info.setTypeId(holiday.getId());
				info.preUpdate();
				techScheduleDao.deleteScheduleByTypeId(info);
			}
		}
	}
	//app编辑休假
	@Transactional(readOnly = false)
	public int saveHoliday(ServiceTechnicianHoliday serviceTechnicianHoliday) {
		int i=0;
		//去数据库中查询出这个休假
		ServiceTechnicianHoliday holiday = dao.get(serviceTechnicianHoliday.getId());
		if (holiday == null){
			throw new ServiceException("未找到该休假信息");
		}
		String reviewStatus = holiday.getReviewStatus();
		//转存对象位排期表
		TechScheduleInfo techScheduleInfo=new TechScheduleInfo();
		techScheduleInfo.setTechId(serviceTechnicianHoliday.getTechId());
		techScheduleInfo.setStartTime(serviceTechnicianHoliday.getStartTime());
		techScheduleInfo.setEndTime(serviceTechnicianHoliday.getEndTime());

		List<TechScheduleInfo> scheduleByTechId = techScheduleDao.getScheduleByTechId(techScheduleInfo);
		if (scheduleByTechId != null && scheduleByTechId.size() > 0){
			throw new ServiceException("服务人员有排期,不可请假");
		}
		//修改 休假表 状态改为审核中
		if (StringUtils.isNotBlank(reviewStatus) && "no".equals(reviewStatus)){
			serviceTechnicianHoliday.setSource("app");
			serviceTechnicianHoliday.setReviewStatus("submit");
			serviceTechnicianHoliday.appPreUpdate();
			i = dao.update(serviceTechnicianHoliday);
		}
		return i;
	}

	public int getOrderTechRelationHoliday(ServiceTechnicianHoliday info) {
		return serviceTechnicianHolidayDao.getOrderTechRelationHoliday(info);
	}
	//app根据id查看休假详情
	// public ServiceTechnicianHoliday getHolidaty(ServiceTechnicianHoliday info) {
	// 	ServiceTechnicianHoliday holiday = serviceTechnicianHolidayDao.get(info.getId());
	// 	if (holiday == null){
	// 		throw new ServiceException("未找到该休假信息，请重新查询");
	// 	}
	// 	if (!holiday.getTechId().equals(info.getTechId())){
	// 		throw new ServiceException("查询错误，改休假与用户不绑定，请重新查询");
	// 	}
	// 	return holiday;
	// }

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