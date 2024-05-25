package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrdersCancelDTO implements Serializable {

    private String id;
    //订单取消原因
    private String cancelReason;

}
