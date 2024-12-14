package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询关联的套餐数量
     * Query the number of packages associated by category id
     * @param id
     * @return
     */
    @Select("select count(0) from setmeal where category_id=#{id}")
    Integer countByCategoryId(Integer id);

    /**
     * 新增套餐
     * add setmeal
     */
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 分页查询套餐
     * setmeal pagination query
     */

    Page<SetmealVO> setmealPaginationQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id查询套餐
     * query setmeal by id
     */
    @Select("select s.*,c.name categoryName from setmeal s left join category c on s.category_id=c.id" +
            " where s.id = #{id}")
    Setmeal selectById(Long id);

    /**
     * 修改套餐
     * update setmeal
     */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 批量删除套餐
     * delete setmeal in batches
     */
    void deleteInBatches(Long[] ids);
}
