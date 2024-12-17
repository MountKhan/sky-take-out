package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {

    /**
     * 新增地址
     * add address
     */
    void addAddress(AddressBook addressBook);

    /**
     * 查询当前用户的所有地址
     * query current user's all address
     */
    List<AddressBook> getAllAddress();

    /**
     * 查询默认地址
     * query default address
     */
    AddressBook getDefaultAddress();

    /**
     * 根据id查询地址
     * query address by id
     */
    AddressBook getAddressById(Long id);

    /**
     * 根据id修改地址
     * update address by id
     */
    void updateById(AddressBook addressBook);

    /**
     * 根据id删除地址
     * delete address by id
     */
    void deleteById(Long id);

    /**
     *设置默认地址
     * set default address
     */
    void setDefaultAddress(AddressBook addressBook);
}
