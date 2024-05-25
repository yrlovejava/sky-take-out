package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("sky.shop")
@Data
public class ShopProperties {

    /**
     * 商家的地址
     */
    private String address;
}
