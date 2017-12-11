package com.thinkgem.jeesite.modules;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

public class BaseEntity<T> extends Model<BaseEntity<T>> {

    private static final long serialVersionUID = 1L;
    /**
     * 主id
     */
    @TableId(value = "id", type = IdType.UUID)
    protected String id;
    /**
     * 创建时间
     */
    @TableField("create_date")
    protected Date createDate;
    /**
     * 创建者
     */
    @TableField("create_by")
    protected String createBy;
    /**
     * 更新者
     */
    @TableField("update_by")
    protected String updateBy;
    /**
     * 更新时间
     */
    @TableField("update_date")
    protected Date updateDate;
    /**
     * 删除标记
     */
    @TableLogic
    @TableField("del_flag")
    protected String delFlag;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }
}