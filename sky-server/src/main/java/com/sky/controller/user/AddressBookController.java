package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "C端-地址相关接口")
@RequestMapping("/user/addressBook")
@RestController
@Slf4j
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址
     * add address
     */
    @PostMapping
    @ApiOperation(value = "新增地址")
    public Result addAddress(@RequestBody AddressBook addressBook){
        log.info("新增地址，{}",addressBook);
        addressBookService.addAddress(addressBook);
        return Result.success();
    }

    /**
     * 查询当前用户的所有地址
     * query current user's all address
     */
    @GetMapping("/list")
    @ApiOperation(value = "查询当前用户的所有地址")
    public Result<List<AddressBook>> getAllAddress(){
        log.info("查询当前用户的所有地址");
        List<AddressBook>list = addressBookService.getAllAddress();
        return Result.success(list);
    }

    /**
     * 查询默认地址
     * query default address
     */
    @GetMapping("/default")
    @ApiOperation(value = "查询默认地址")
    public Result<AddressBook> getDefaultAddress(){
        log.info("查询默认地址");
        AddressBook addressBook = addressBookService.getDefaultAddress();
        return Result.success(addressBook);
    }

    /**
     * 根据id查询地址
     * query address by id
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询地址")
    public Result<AddressBook> getAddressById(@PathVariable Long id){
        log.info("根据id查询地址，{}",id);
        AddressBook addressBook = addressBookService.getAddressById(id);
        return Result.success(addressBook);
    }

    /**
     * 根据id修改地址
     * update address by id
     */
    @PutMapping
    @ApiOperation(value = "根据id修改地址")
    public Result updateById(@RequestBody AddressBook addressBook){
        log.info("根据id修改地址,{}",addressBook);
        addressBookService.updateById(addressBook);
        return Result.success();
    }

    /**
     * 根据id删除地址
     * delete address by id
     */
    @DeleteMapping
    @ApiOperation(value = "根据id删除地址")
    public Result deleteById(Long id){
        addressBookService.deleteById(id);
        return Result.success();
    }

    /**
     *设置默认地址
     * set default address
     */
    @PutMapping("/default")
    @ApiOperation(value = "设置默认地址")
    public Result setDefaultAddress(@RequestBody AddressBook addressBook){
        log.info("设置默认地址，{}",addressBook);
        addressBookService.setDefaultAddress(addressBook);
        return Result.success();
    }
}
