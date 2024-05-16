package com.sky.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class ShoppingCartDTO implements Serializable {

    private String dishId;
    private String setmealId;
    private String dishFlavor;

}
