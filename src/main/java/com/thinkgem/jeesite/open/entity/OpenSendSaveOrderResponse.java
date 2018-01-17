package com.thinkgem.jeesite.open.entity;

/**
 * 更新订单信息ResponseEntity
 * @author a
 * @version 2017-12-11
 */
public class OpenSendSaveOrderResponse<T extends Object> {

    private static final long serialVersionUID = 1L;

    private int code;
    private T data;
    private String message;
}