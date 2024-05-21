package com.sky.controller.admin;

import com.sky.constant.RedisConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关接口")
@Slf4j
public class ShopController {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 设置营业状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺状态: {}",status == 1 ? "营业中" : "休息中");
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(RedisConstant.REDIS_SHOP_STATUS_KEY,status);
        return Result.success();
    }

    /**
     * 查询店铺营业状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("查询店铺营业状态")
    public Result<Integer> getStatus(){
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Integer status = (Integer) valueOperations.get(RedisConstant.REDIS_SHOP_STATUS_KEY);
        log.info("查询店铺营业状态: {}",status == 1 ? "营业中" : "休息中");

        return Result.success(status);
    }


}
