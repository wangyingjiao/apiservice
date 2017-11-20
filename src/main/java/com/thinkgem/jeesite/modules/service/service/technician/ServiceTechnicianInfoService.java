/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.technician;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.technician.*;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianImages;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 服务技师基础信息Service
 *
 * @author a
 * @version 2017-11-16
 */
@Service
@Transactional(readOnly = true)
public class ServiceTechnicianInfoService extends CrudService<ServiceTechnicianInfoDao, ServiceTechnicianInfo> {


    @Autowired
    private ServiceTechnicianInfoDao technicianInfoDao;

    @Autowired
    private ServiceTechnicianFamilyMembersDao familyMembersDao;

    @Autowired
    private ServiceTechnicianServiceInfoDao serviceInfoDao;

    @Autowired
    private ServiceTechnicianWorkTimeDao workTimeDao;


    public ServiceTechnicianInfo get(String id) {
        return super.get(id);
    }

    public List<ServiceTechnicianInfo> findList(ServiceTechnicianInfo serviceTechnicianInfo) {
        return super.findList(serviceTechnicianInfo);
    }

    public Page<ServiceTechnicianInfo> findPage(Page<ServiceTechnicianInfo> page, ServiceTechnicianInfo serviceTechnicianInfo) {
        return super.findPage(page, serviceTechnicianInfo);
    }

    @Transactional(readOnly = false)
    public void save(ServiceTechnicianInfo serviceTechnicianInfo) {
        super.save(serviceTechnicianInfo);
    }

    @Transactional(readOnly = false)
    public void delete(ServiceTechnicianInfo serviceTechnicianInfo) {
        super.delete(serviceTechnicianInfo);
    }

    @Autowired
    private ServiceTechnicianImagesDao imagesDao;

    /**
     * 保存图片
     * 使用循环方式保存
     *
     * @param info
     */
    public void saveImages(ServiceTechnicianInfo info) {
        List<ServiceTechnicianImages> images = info.getImages();
        if (null != images && images.size() > 0) {
            for (ServiceTechnicianImages image : images) {
                image.setTechId(info.getId());
                image.setTechName(info.getTechName());
                saveImage(image);
            }
        }
    }

    private void saveImage(ServiceTechnicianImages image) {
        if (StringUtils.isBlank(image.getId())) {
            image.preInsert();
            image.setSort("0");
            imagesDao.insert(image);
        }else {
            image.preUpdate();
            imagesDao.update(image);
        }
    }

    /**
     * 删除图片
     *
     */
    public void deleteImage(ServiceTechnicianImages image){
        if (StringUtils.isNotBlank(image.getId())) {
            imagesDao.delete(image);
        }
    }
    /**
     * 获取图片
     */

    public ServiceTechnicianImages getImage(ServiceTechnicianImages image){
        ServiceTechnicianImages images = new ServiceTechnicianImages();
        if (StringUtils.isNotBlank(image.getId())) {
             images = imagesDao.get(image);
        }
        return images;
    }
}