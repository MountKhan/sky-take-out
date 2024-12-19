package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Value("${sky.shop.address}")
    private String shopAddress;

    @Value("${sky.baidu.ak}")
    private String ak;

    /**
     * 用户下单
     * suer submit order
     */
    @Transactional
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //处理各种业务异常
        //Handle various business exceptions.
        //地址是否为空？
        //whether address is null？
        AddressBook addressBook = addressBookMapper.selectById(ordersSubmitDTO.getAddressBookId());
        if(addressBook == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        //购物车是否为空？
        //whether shopping cart is null？
        Long userID = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(userID).build();
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if(list == null || list.isEmpty()){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //用户地点是否超出配送距离？
        //Whether the user's location exceed the delivery distance?
        String userAddress = addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail();
        judgeDistance(userAddress);

        //向订单表插入1条数据
        //insert into orders schema (1 item)
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);//支付状态：待付款  payment status:unpaid
        orders.setStatus(Orders.PENDING_PAYMENT);//订单状态：待付款  order status:pending payment
        orders.setNumber(String.valueOf(System.currentTimeMillis())+ UUID.randomUUID());
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(userID);

        //拼接地址
        //get address String
        String address = addressBook.getProvinceName()+addressBook.getCityName()+addressBook.getDetail();
        orders.setAddress(address);

        orderMapper.insert(orders);

        //向订单明细表插入n条数据
        //insert into order_detail schema (multiple items)

        List<OrderDetail> orderDetailList = new ArrayList<>();

        for (ShoppingCart cart : list) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);

            //补全订单编号
            //set orderId
            orderDetail.setOrderId(orders.getId());

            orderDetailList.add(orderDetail);
        }

        //将数据批量插入订单明细表
        //insert into order_detail schema in batches
        orderDetailMapper.insertBatch(orderDetailList);


        //清空用户购物车
        //cleat the shopping cart
        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(userID);
        shoppingCartMapper.delete(cart);

        //封装返回数据
        //Encapsulate the return data
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime()).build();

        return orderSubmitVO;
    }

    /**
     * 查询历史订单（分页查询）
     * query history order（pagination）
     */
    @Override
    public PageResult paginationHistoryOrder(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());

        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        Page<Orders> page = orderMapper.select(ordersPageQueryDTO);

        List<OrderVO> list = new ArrayList<>();

        if(page != null && !page.isEmpty()){
            for (Orders orders : page) {
                List<OrderDetail> orderDetailList = orderDetailMapper.selectByOrderId(orders.getId());
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders,orderVO);
                orderVO.setOrderDetailList(orderDetailList);
                list.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(),list);
    }

    /**
     * 通过id查询订单
     * query order by id
     */
    @Override
    public OrderVO queryById(Long id) {
        //查询订单表
        //select from order schema
        Orders orders = orderMapper.selectById(id);

        //查询订单详情表
        //select from order detail schema
        List<OrderDetail> orderDetailList = orderDetailMapper.selectByOrderId(id);

        //封装返回数据
        //Encapsulate the return data
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(orderDetailList);

        return orderVO;
    }

    /**
     * 取消订单
     * cancel order
     */
    @Override
    public void userCancelById(Long id) {
        //查询订单状态
        //query this order by id
        Orders orders = orderMapper.selectById(id);

        //如果订单不存在，抛异常
        //if order not exist，throw Exception
        if(orders == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        //order status
        // 1 pending payment
        // 2 to be confirmed
        // 3 confirmed
        // 4 delivery in progress
        // 5 completed
        // 6 cancelled
        Integer status = orders.getStatus();
        if(status>2){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //当订单状态为待接单时 需要进行退款
        //if order status is “2 to be confirmed” , refund
        if(status == Orders.TO_BE_CONFIRMED){
            //此处是退款相关代码
            //Here is the code for the refund logic.
            //ps :Due to the payment-related services of WeChat
            // requiring government-issued business qualifications
            // and related documents,
            // I have to skip the payment-related content here.


            //支付状态修改为 退款
            //update order's pay status to "refund"
            orders.setPayStatus(Orders.REFUND);
        }
        // 更新订单状态、取消原因、取消时间
        // update order status,cancel reason,cancel time
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消订单");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 再来一单
     * reorder
     */
    @Override
    public void repetition(Long id) {
        //查询当前订单
        Orders orders = orderMapper.selectById(id);
        Long userId = BaseContext.getCurrentId();

        List<ShoppingCart> shoppingCartList = new ArrayList<>();

        //查询订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.selectByOrderId(orders.getId());
        for (OrderDetail orderDetail : orderDetailList) {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail,shoppingCart);
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartList.add(shoppingCart);
        }

        shoppingCartMapper.insertInBatches(shoppingCartList);
    }

    /**
     * 订单查询
     * order query
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        Page<Orders> page = orderMapper.select(ordersPageQueryDTO);

        List<OrderVO> orderVOList = getOrderVOList(page);

        return new PageResult(page.getTotal(),orderVOList);
    }

    /**
     * 各个订单状态数量统计
     * Statistics of the order count for each status.
     */
    @Override
    public OrderStatisticsVO statistic() {
        Integer toBeConfirmed = orderMapper.statusCount(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.statusCount(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.statusCount(Orders.DELIVERY_IN_PROGRESS);

        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);

        return orderStatisticsVO;
    }

    /**
     * 接单
     * confirm order
     */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = Orders.builder().id(ordersConfirmDTO.getId()).status(Orders.CONFIRMED).build();
        orderMapper.update(orders);
    }

    /**
     * 拒单
     * reject order
     */
    @Override
    public void reject(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders = orderMapper.selectById(ordersRejectionDTO.getId());

        //只有状态为 2 待接单 的订单才能拒单
        //can only reject order which status is 2 (TO_BE_CONFIRMED = 2)
        if(orders == null || orders.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //如果用户已付款，需要退款
        //if order is paid，refund money
        if(Objects.equals(orders.getPayStatus(), Orders.PAID)){
            //这里是退款逻辑
            //here is refund logic
        }

        //根据订单id更新订单状态、拒单原因、取消时间
        //update order status, rejection reason,cancel time
        Orders ordersForUpdate = new Orders();
        ordersForUpdate.setId(orders.getId());
        ordersForUpdate.setStatus(Orders.CANCELLED);
        ordersForUpdate.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        ordersForUpdate.setCancelTime(LocalDateTime.now());

        orderMapper.update(ordersForUpdate);
    }

    /**
     * 取消订单
     * cancel order
     */
    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        Orders ordersC = orderMapper.selectById(ordersCancelDTO.getId());

        //如果用户已付款，需要退款
        //if order is paid，refund money
        if(Objects.equals(ordersC.getPayStatus(), Orders.PAID)){
            //这里是退款逻辑
            //here is refund logic
        }

        //根据订单id更新订单状态、拒单原因、取消时间
        //update order status, rejection reason,cancel time
        Orders ordersForUpdate = new Orders();
        ordersForUpdate.setId(ordersC.getId());
        ordersForUpdate.setStatus(Orders.CANCELLED);
        ordersForUpdate.setCancelReason(ordersCancelDTO.getCancelReason());
        ordersForUpdate.setCancelTime(LocalDateTime.now());

        orderMapper.update(ordersForUpdate);
    }

    /**
     * 查询订单详情
     * Query Order Details
     */
    @Override
    public OrderVO queryDetail(Long id) {
        Orders orders = orderMapper.selectById(id);
        List<OrderDetail> orderDetailList = orderDetailMapper.selectByOrderId(orders.getId());

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        String orderDishes = getOrderDishes(orders);
        orderVO.setOrderDishes(orderDishes);

        return orderVO;
    }

    /**
     * 派送订单
     * deliver order
     */
    @Override
    public void deliver(Long id) {
        Orders orders = orderMapper.selectById(id);

        if(orders == null || !orders.getStatus().equals(Orders.CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders ordersForUpdate = new Orders();
        ordersForUpdate.setId(orders.getId());
        ordersForUpdate.setStatus(Orders.DELIVERY_IN_PROGRESS);

        orderMapper.update(ordersForUpdate);
    }

    /**
     * 完成订单
     * complete order
     */
    @Override
    public void complete(Long id) {
        Orders orders = orderMapper.selectById(id);

        if(orders == null || !orders.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders ordersForUpdate = new Orders();
        ordersForUpdate.setId(orders.getId());
        ordersForUpdate.setStatus(Orders.COMPLETED);

        orderMapper.update(ordersForUpdate);
    }

    /**
     * 封装OrderVO数据
     * Encapsulate the data into OrderVO objects
     */
    private List<OrderVO> getOrderVOList(Page<Orders> page) {
        List<Orders> ordersList = page.getResult();

        List<OrderVO> orderVOList = new ArrayList<>();

        if (orderVOList != null && !ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                List<OrderDetail> orderDetailList = orderDetailMapper.selectByOrderId(orders.getId());
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders,orderVO);

                String orderDishes = getOrderDishes(orders);
                orderVO.setOrderDetailList(orderDetailList);

                orderVOList.add(orderVO);
            }
        }
        return orderVOList;
    }

    /**
     * 根据订单id拼接订单的菜品内容字符串
     * "Concatenate the dish content of the order based on the order ID."
     */
    private String getOrderDishes(Orders orders) {

        //获取订单中的菜品信息
        List<OrderDetail> orderDetailList = orderDetailMapper.selectByOrderId(orders.getId());

        //拼接orderDishes字符串
        List<String> orderDishList = orderDetailList.stream().map(od -> {
            return od.getName() + "*" + od.getNumber() + ";";
        }).collect(Collectors.toList());

        return String.join("",orderDishList);
    }

    /**
     * 检查用户收货地址是否超过配送距离（配送距离5公里）
     * Check whether the user's delivery address exceeds the delivery distance (5 kilometers).
     */
    public void judgeDistance(String userAddress){
        HashMap<String,String> shopMap = new HashMap();
        shopMap.put("address",shopAddress);
        shopMap.put("output","json");
        shopMap.put("ak",ak);
        //获取店铺的经纬度坐标
        //Get the shop's latitude and longitude coordinates.
        String shopCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", shopMap);
        JSONObject shopJsonObject = JSON.parseObject(shopCoordinate);
        if(!shopJsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("店铺位置信息解析失败/Parsing of shop location information failed");
        }

        JSONObject shopLocation = shopJsonObject.getJSONObject("result").getJSONObject("location");
        String shopLatitude = formatLongitude(shopLocation.getString("lat"));
        String shopLongitude = formatLongitude(shopLocation.getString("lng"));
        String shopLatLong = shopLatitude + "," + shopLongitude;


        HashMap<String,String> userMap = new HashMap<>();
        userMap.put("address",userAddress);
        userMap.put("output","json");
        userMap.put("ak",ak);
        //获取客户的经纬度坐标
        //Get the customer's latitude and longitude coordinates.
        String userCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", userMap);
        JSONObject userJsonObject = JSON.parseObject(userCoordinate);
        if(!userJsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("用户位置信息解析失败/Parsing of user location information failed");
        }
        JSONObject userLocation = userJsonObject.getJSONObject("result").getJSONObject("location");
        String userLatitude = formatLongitude(userLocation.getString("lat"));
        String userLongitude = formatLongitude(userLocation.getString("lng"));
        String userLatLong = userLatitude + "," + userLongitude;

        //判断是否超出距离
        //Check if the delivery address is beyond the delivery range.
        HashMap<String,String> judgeDistance = new HashMap<>();
        judgeDistance.put("origin",shopLatLong);
        judgeDistance.put("destination",userLatLong);
        judgeDistance.put("ak",ak);
        String distanceJson = HttpClientUtil.doGet("https://api.map.baidu.com/directionlite/v1/walking", judgeDistance);
        JSONObject distanceObject = JSON.parseObject(distanceJson);
        if(!distanceObject.getString("status").equals("0")){
            throw new OrderBusinessException("距离估算失败/distance estimation failure");
        }


        JSONObject result = distanceObject.getJSONObject("result");
        JSONArray routes = result.getJSONArray("routes");
        Integer distance = (Integer)((JSONObject)routes.get(0)).get("distance");
        log.info("距离为：{}米",distance);
        if(distance > 5000){
            throw new OrderBusinessException("超出配送范围/distance too far!");
        }

    }

    /**
     * 将输入的经纬度字符串保留6位小数，并舍去后面的部分
     * This method retains 6 decimal places for the input longitude string and truncates the rest.
     */
    public static String formatLongitude(String latLong) {
        // 将字符串转换为BigDecimal类型，保证精度
        // Convert the string to BigDecimal to ensure precision
        BigDecimal res = new BigDecimal(latLong);

        // 保留6位小数，并舍去后面的部分
        // Retain 6 decimal places and truncate the remaining part
        res = res.setScale(6, BigDecimal.ROUND_DOWN);

        // 返回格式化后的字符串
        // Return the formatted string
        return res.toString();
    }

}
