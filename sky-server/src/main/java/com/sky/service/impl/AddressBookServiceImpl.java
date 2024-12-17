package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 新增地址
     * add address
     */
    @Override
    public void addAddress(AddressBook addressBook) {
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBook.setIsDefault(0);
        addressBookMapper.insert(addressBook);
    }

    /**
     * 查询当前用户的所有地址
     * query current user's all address
     */
    @Override
    public List<AddressBook> getAllAddress() {
        Long userId = BaseContext.getCurrentId();
        return addressBookMapper.getAddressByUserId(userId);
    }

    /**
     * 查询默认地址
     * query default address
     */
    @Override
    public AddressBook getDefaultAddress() {
        AddressBook.AddressBookBuilder addressBook = AddressBook.builder()
                .userId(BaseContext.getCurrentId())
                .isDefault(1);
        return addressBookMapper.select(addressBook);
    }

    /**
     * 根据id查询地址
     * query address by id
     */
    @Override
    public AddressBook getAddressById(Long id) {
        return addressBookMapper.selectById(id);
    }

    /**
     * 根据id修改地址
     * update address by id
     */
    @Override
    public void updateById(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    /**
     * 根据id删除地址
     * delete address by id
     */
    @Override
    public void deleteById(Long id) {
        addressBookMapper.deleteById(id);
    }

    /**
     *设置默认地址
     * set default address
     */
    @Transactional
    @Override
    public void setDefaultAddress(AddressBook addressBook) {
        //将用户所有地址设置为非默认地址
        //Set all user addresses to non-default.
        addressBook.setIsDefault(0);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.setIsDefaultByUserId(addressBook);
        //将当前地址设置为默认地址
        //Set the current address as the default.
        addressBook.setIsDefault(1);
        addressBookMapper.update(addressBook);
    }
}