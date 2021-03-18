package com.stupidzhang.zhihu.come.service.api;

import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.domain.kplunion.OrderService.request.query.OrderRowReq;
import com.jd.open.api.sdk.domain.kplunion.OrderService.response.query.OrderRowQueryResult;
import com.jd.open.api.sdk.domain.kplunion.OrderService.response.query.OrderRowResp;
import com.jd.open.api.sdk.request.kplunion.UnionOpenOrderRowQueryRequest;
import com.jd.open.api.sdk.response.kplunion.UnionOpenOrderRowQueryResponse;
import com.stupidzhang.zhihu.come.constant.JdConstants;
import com.stupidzhang.zhihu.come.constant.WxMessageConstants;
import com.stupidzhang.zhihu.come.enums.OrderStatusEnum;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxRuntimeException;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 京粉查询订单并组装订单
 */
@Slf4j
public class JingFenApiClient {

    /**
     * client 对象
     */
    private final JdClient jdClient;
    /**
     * 发送对象
     */
    private final String toUser;


    public JingFenApiClient(JdClient jdClient, String toUser) {
        this.jdClient = jdClient;
        this.toUser = toUser;
    }

    public String getToUser() {
        return toUser;
    }

    public List<WxMpTemplateData> execute(String startTime, String endTime) throws WxRuntimeException {
        UnionOpenOrderRowQueryRequest request = buildRequest(startTime, endTime);
        UnionOpenOrderRowQueryResponse response;
        try {
            log.info("【{}~{}】期间请求数据", startTime, endTime);
            response = this.jdClient.execute(request);
        } catch (Exception exception) {
            log.error("【{}~{}】期间请求数据出错了！！！", startTime, endTime);
            throw new WxRuntimeException("请求数据出错了", exception);
        }
        if (!"0".equals(response.getCode())) {
            log.error("【{}~{}】期间请求数据出错了，错误原因：{}", startTime, endTime, response.getZhDesc());
            throw new WxRuntimeException(response.getZhDesc());
        }
        OrderRowQueryResult queryResult = response.getQueryResult();
        if (queryResult.getData() != null) {
            List<OrderRowResp> originList = Arrays.asList(queryResult.getData());
            // 过滤无效的子订单
            List<OrderRowResp> orderList = originList.stream()
                    .filter(order -> !(order.getParentId() == 0 && OrderStatusEnum.TWO.getCode().equals(order.getValidCode())))
                    .collect(Collectors.toList());
            if (orderList.isEmpty()) {
                log.warn("非常遗憾，新增数据为无效数据，详情见APP");
                return Collections.emptyList();
            }
            log.warn("【{}~{}】期间新增收入啦", startTime, endTime);
            return buildOrder(orderList);
        }
        return Collections.emptyList();
    }

    /**
     * 构建请求实体
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return omit
     */
    private UnionOpenOrderRowQueryRequest buildRequest(String startTime, String endTime) {
        UnionOpenOrderRowQueryRequest request = new UnionOpenOrderRowQueryRequest();
        OrderRowReq orderReq = new OrderRowReq();
        orderReq.setStartTime(startTime);
        orderReq.setEndTime(endTime);
        orderReq.setPageIndex(1);
        orderReq.setPageSize(50);
        orderReq.setType(1);
        request.setOrderReq(orderReq);
        request.setVersion(JdConstants.VERSION);
        return request;
    }

    /**
     * 构建实体，用于发送订单消息
     *
     * @param orderList 订单列表
     * @return omit
     */
    private List<WxMpTemplateData> buildOrder(List<OrderRowResp> orderList) {

        List<WxMpTemplateData> dataList = new ArrayList<>();
        // 订单数量
        long orderNum = orderList.stream().map(OrderRowResp::getOrderId).distinct().count();
        double estimateFee = orderList.stream()
                .filter(orderRowResp ->
                        OrderStatusEnum.SIXTEEN.getCode().equals(orderRowResp.getValidCode())
                                || OrderStatusEnum.SEVENTEEN.getCode().equals(orderRowResp.getValidCode())
                )
                .mapToDouble(OrderRowResp::getEstimateFee).sum();
        double eEstimateCosPrice = orderList.stream().mapToDouble(OrderRowResp::getEstimateCosPrice).sum();
        dataList.add(new WxMpTemplateData(WxMessageConstants.MESSAGE_FIRST, String.valueOf(orderNum), "#5d6375"));
        dataList.add(new WxMpTemplateData(WxMessageConstants.MESSAGE_KEYWORD_ONE, String.format("%.2f", estimateFee), "#9e0606"));
        dataList.add(new WxMpTemplateData(WxMessageConstants.MESSAGE_KEYWORD_TWO, String.format("%.2f", eEstimateCosPrice), "#5d6375"));
        dataList.add(new WxMpTemplateData(WxMessageConstants.MESSAGE_KEYWORD_THREE, orderItems(orderList), "#5d6375"));
        return dataList;
    }

    /**
     * 拼装订单详情
     *
     * @param orderList 订单列表
     * @return 订单详情
     */
    private String orderItems(List<OrderRowResp> orderList) {
        StringBuilder contentBuilder = new StringBuilder();
        AtomicInteger orderSeq = new AtomicInteger(1);
        orderList.forEach(orderRowResp -> {
            String skuName = orderRowResp.getSkuName();
            Integer plus = orderRowResp.getPlus();
            Integer traceType = orderRowResp.getTraceType();
            OrderStatusEnum orderStatus = OrderStatusEnum.getOrderStatus(orderRowResp.getValidCode());
            String traceTypeName = traceType == 2 ? "同店" : "跨店";
            // 订单类型
            contentBuilder.append(orderSeq.getAndIncrement()).append("、");
            contentBuilder.append("【").append(traceTypeName).append("-").append(orderStatus.getState());
            // 无效原因
            if (StringUtils.isNotBlank(orderStatus.getReason())) {
                contentBuilder.append("-").append(orderStatus.getReason());
            }
            if (plus == 1) {
                contentBuilder.append("-").append("PLUS");
            }
            contentBuilder.append("】");
            contentBuilder.append(StringUtils.substring(skuName, 0, Math.min(skuName.length(), 30)))
                    .append("...").append("\n");
        });
        return contentBuilder.toString();
    }
}
