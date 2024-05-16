package com.sky.utils;

import java.util.UUID;

public class UUIDUtil {

    //通过UUID来生成id 数据库中存储的是32位char 生成的uuid会用 "-" 来连接 ,所以需要去掉 "-"
    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
