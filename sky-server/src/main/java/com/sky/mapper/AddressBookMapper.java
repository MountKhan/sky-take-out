package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    /**
     * 新增地址
     * add address
     */
    @Insert("insert into address_book (user_id, consignee, sex," +
            " phone, province_code, province_name, " +
            "city_code, city_name, district_code," +
            " district_name, detail, label, is_default) values (#{userId},#{consignee},#{sex}," +
            "#{phone},#{provinceCode},#{provinceName}," +
            "#{cityCode},#{cityName},#{districtCode}," +
            "#{districtName},#{detail},#{label},#{isDefault})")
    void insert(AddressBook addressBook);

    /**
     * 查询当前用户的所有地址
     * query current user's all address
     */
    @Select("select * from address_book where user_id = #{userId}")
    List<AddressBook> getAddressByUserId(Long userId);

    /**
     * 查询地址
     * query address
     */
    AddressBook select(AddressBook.AddressBookBuilder addressBook);

    /**
     * 根据id查询地址
     * query address by id
     */
    @Select("select * from address_book where id = #{id}")
    AddressBook selectById(Long id);

    /**
     * 根据id修改地址
     * update address by id
     */
    void update(AddressBook addressBook);

    /**
     * 根据id删除地址
     * delete address by id
     */
    @Delete("delete from address_book where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据用户id更新默认地址状态
     * Update the default address status based on the user ID
     */
    @Update("update address_book set is_default = #{isDefault} where user_id = #{userId}")
    void setIsDefaultByUserId(AddressBook addressBook);
}
