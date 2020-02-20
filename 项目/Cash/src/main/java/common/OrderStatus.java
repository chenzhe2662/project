package common;

public enum OrderStatus {
    PLAYING(1,"待支付"),OK(2,"支付完成");
    private int flg;
    private String desc;

    OrderStatus(int flg, String desc) {
        this.flg = flg;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "OrderStatus{" +
                "flg=" + flg +
                ", desc='" + desc + '\'' +
                '}';
    }

    public int getFlg() {
        return flg;
    }

    public String getDesc() {
        return desc;
    }

    public static OrderStatus valueOf(int flg) {
        for (OrderStatus orderStatus:OrderStatus.values()){
            if (orderStatus.flg == flg) {
                return orderStatus;
            }
        }
        throw new RuntimeException("OrderStatus is not fount");
    }
}
