package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordEditFailedException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.UUIDUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.util.PackageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //对前端传过来的明文密码进行md5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (Objects.equals(employee.getStatus(), StatusConstant.DISABLE)) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * @param employeeDTO
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        //在持久层做业务最好是使用实体类
        Employee employee = new Employee();
        //这里因为DTO中的属性和实体类中的属性相同，所以可以用对象属性拷贝来简化代码(必须是属性名一致才可以)
        BeanUtils.copyProperties(employeeDTO,employee);//从前一个参数 拷贝给 后一个参数
        //设置剩余参数
        //设置id
        employee.setId(UUIDUtil.getUUID());
        //设置账号状态(1 表示正常 0 表示锁定)
        employee.setStatus(StatusConstant.ENABLE);
        //设置密码，新增员工默认是123456
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        //现在使用了AOP,所以可以手动赋值
        /*//设置当前记录创建人id和修改人id
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());
        //设置创建时间
        employee.setCreateTime(LocalDateTime.now());
        //设置修改时间
        employee.setUpdateTime(LocalDateTime.now());*/

        //调用service
        employeeMapper.insertUser(employee);
    }

    /**
     * 分页条件查询员工
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        //模糊查询到所有信息
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        //处理信息
        long total = page.getTotal();
        List<Employee> records = page.getResult();

        return new PageResult(total,records);
    }

    /**
     * 修改员工
     * @param employee
     */
    @Override
    public void startOrStop(Employee employee) {
        //调用mapper修改信息
        employeeMapper.updateEmployee(employee);
    }

    /**
     * 根据id查询员工具体信息
     * @param id
     * @return
     */
    @Override
    public Employee getDetailById(String id) {

        Employee employee = employeeMapper.selectEmployeeDetailById(id);
        //设置密码为******,防止密码泄露
        employee.setPassword("******");

        return employee;
    }

    /**
     * 编辑员工信息
     * @param employeeDTO
     * @return
     */
    @Override
    public int update(EmployeeDTO employeeDTO){
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);
        //设置修改时间和修改人
        //现在使用了AOP，所有就可以不用手动设置
        /*employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(BaseContext.getCurrentId());*/

        return employeeMapper.updateEmployee(employee);
    }

    /**
     * 修改员工密码
     * @param passwordEditDTO
     * @return
     */
    @Override
    public int editPassword(PasswordEditDTO passwordEditDTO) {
        //根据id先查询员工密码，看是否与oldPassword一致
        String password = employeeMapper.selectEmployeePasswordById(passwordEditDTO.getEmpId());
        //因为后台的数据是加密处理过的，所以必须加密之后比对
        String oldPassword = DigestUtils.md5DigestAsHex(passwordEditDTO.getOldPassword().getBytes());
        if(!password.equals(oldPassword)){
            throw new PasswordEditFailedException(MessageConstant.PASSWORD_EDIT_FAILED);
        }

        //封装employee
        Employee employee = new Employee();
        //加密新的密码
        String newPassword = DigestUtils.md5DigestAsHex(passwordEditDTO.getNewPassword().getBytes());
        employee.setPassword(newPassword);
        employee.setId(passwordEditDTO.getEmpId());
        //现在使用了AOP来统一给公共字段赋值，所以这里不需要再手动赋值
        /*employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(BaseContext.getCurrentId());*/

        return employeeMapper.updateEmployee(employee);
    }
}
