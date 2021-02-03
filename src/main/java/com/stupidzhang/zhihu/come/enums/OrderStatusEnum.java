package com.stupidzhang.zhihu.come.enums;

/**
 * 订单枚举
 */
public enum OrderStatusEnum {
    NOT(-1, "无效", "未知"),
    TWO(2, "无效", "拆单"),

    THREE(3, "无效", "取消"),

    FOUR(4, "无效", "京东帮帮主订单"),

    FIVE(5, "无效", "账号异常"),

    SIX(6, "无效", "赠品类目不返佣"),

    SEVEN(7, "无效", "校园订单"),

    EIGHT(8, "无效", "企业订单"),

    NINE(9, "无效", "团购订单"),

    TEN(10, "无效", "村推广员下单"),

    ELEVEN(11, "无效", "违规订单"),

    THIRTEEN(13, "无效", "违规订单"),

    FOURTEEN(14, "无效", "来源与备案网址不符"),

    FIFTEEN(15, "待付款", ""),

    SIXTEEN(16, "已付款", ""),

    SEVENTEEN(17, "已完成", ""),

    TWENTY(20, "无效", "此复购订单对应的首购订单无效"),

    TWENTY_ONE(21, "无效", "云店订单"),

    TWENTY_TWO(22, "无效", "PLUS会员佣金比例为0");

    private final Integer code;
    private final String state;
    private final String reason;

    OrderStatusEnum(Integer code, String state, String reason) {
        this.code = code;
        this.state = state;
        this.reason = reason;
    }

    public static OrderStatusEnum getOrderStatus(Integer code) {
        for (OrderStatusEnum statusEnum : OrderStatusEnum.values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return NOT;
    }

    public String getState() {
        return state;
    }

    public Integer getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }


}
