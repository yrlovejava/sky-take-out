package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @PostMapping()
    @ApiOperation("新增套餐")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐: {}",setmealDTO);
        setmealService.save(setmealDTO);
        //新增操作本来也需要清除缓存，但是我们新增套餐时，设置默认状态为停售，所以不影响前端页面的相应，所以只需在启用和停售的controller中清除缓存就可以了
        return Result.success();
    }

    /**
     * 根据条件分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("根据条件分页查询套餐")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("根据条件分页查询套餐: {}",setmealPageQueryDTO);
        PageResult pageResult = setmealService.page(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用或停售套餐
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用或停售套餐")
    //因为这里只有套餐的id，但是我们的缓存是根据categoryId来作为key的，所以为了方便，直接清除所有缓存
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result startOrStop(@PathVariable Integer status,String id){
        log.info("启用和停售套餐: {},{}",status == 1 ? "启用" : "停售",id);
        setmealService.startOrStop(status,id);
        return Result.success();
    }

    /**
     * 根据id查询套餐，用于修改页面回显数据
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getById(@PathVariable String id) {
        log.info("根据id查询套餐: {}",id);
        SetmealVO setmealVO = setmealService.getByIdWithDish(id);
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐
     * @param setmealDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改套餐")
    //因为可能涉及套餐的变换，不好判断是否只涉及一个套餐的缓存，所以直接全部清理
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐: {}",setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }

    @DeleteMapping()
    @ApiOperation("批量删除套餐")
    //直接清除所有缓存数据
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result delete(@RequestParam List<String> ids) {
        log.info("批量删除套餐: {}",ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }
}
