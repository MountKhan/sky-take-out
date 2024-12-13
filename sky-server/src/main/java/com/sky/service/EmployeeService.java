package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录/login employee
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工/new employee
     * @param employeeDTO
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 员工分页查询/employee pagination query
     * @param employeePageQueryDTO
     * @return
     */
    PageResult paginationQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     *启用/禁用员工账号 enable/disable employee account
     * @param status
     * @param id
     */
    void startOrStop(Integer status, long id);

    /**
     * 根据id查询员工/get employee by id
     * @param id
     * @return
     */
    Employee getEmployeeById(Integer id);

    /**
     * 根据id更新员工信息/ update employee attributes by id
     * @param employeeDTO
     */
    void uodateEmployeeById(EmployeeDTO employeeDTO);
}
