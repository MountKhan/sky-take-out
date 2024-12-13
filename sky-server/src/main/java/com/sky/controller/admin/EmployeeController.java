package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@Api(tags = "员工相关接口")
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录/log in
     *
     * @param employeeLoginDTO
     * @return
     */
    @ApiOperation(value = "员工登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出/log out
     *
     * @return
     */
    @ApiOperation(value = "员工退出")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工/save employee
     * @param employeeDTO
     * @return
     */
    @ApiOperation("新增员工/save employee")
    @PostMapping
    public Result save(@RequestBody EmployeeDTO employeeDTO){

        System.out.println("当前线程的id："+Thread.currentThread().getId());

        log.info("新增员工，{}",employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 员工分页查询/employee pagination query
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> paginationQuery(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("员工分页查询/employee pagination query：{}",employeePageQueryDTO);
        PageResult pageResult = employeeService.paginationQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用/禁用员工 enable/disable employee account
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用/禁用员工账号 enable/disable employee account")
    public Result startOrStop(@PathVariable Integer status,long id){
        log.info("启用/禁用员工账号 enable/disable employee account id:{}，status:{}",id,status);
        employeeService.startOrStop(status,id);
        return Result.success();
    }

    /**
     * 员工查询回显/data echo
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询员工/get employee by id")
    public Result<Employee> getEmployeeById(@PathVariable Integer id){
        log.info("根据id查询员工{}",id);
        Employee employee = employeeService.getEmployeeById(id);
        return Result.success(employee);
    }

    /**
     * 根据id更新员工信息/ update employee attributes by id
     */
    @PutMapping
    @ApiOperation("根据id更新员工信息/ update employee attributes by id")
    public Result updateEmployeeById(@RequestBody EmployeeDTO employeeDTO){
        log.info("更新员工信息，{}",employeeDTO);
        employeeService.uodateEmployeeById(employeeDTO);
        return Result.success();
    }

}
