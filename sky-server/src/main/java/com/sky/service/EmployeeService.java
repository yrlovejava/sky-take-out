package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

import java.util.Map;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 分页查询员工信息
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 修改状态
     * @param employee
     */
    void startOrStop(Employee employee);

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    Employee getDetailById(String id);

    /**
     * 更新员工信息
     * @param employeeDTO
     * @return
     */
    int update(EmployeeDTO employeeDTO);

    /**
     * 修改员工密码
     * @param passwordEditDTO
     * @return
     */
    int editPassword(PasswordEditDTO passwordEditDTO);
}
