package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 服务项目保存ResponseEntity
 * @author a
 * @version 2017-12-11
 */
public class OpenSendSaveItemResponse<T extends Object> {

    private static final long serialVersionUID = 1L;

    private int code;
    private T data;
    private String message;
}