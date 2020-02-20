package Servlet;

import common.OrderStatus;
import entity.Goods;
import entity.Order;
import entity.OrderItem;
import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/buyGoodsServlet")
public class buyGoodsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("doGet");
            doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("doPost");
        HttpSession session = req.getSession();
        Order order = (Order) session.getAttribute("order");
        List<Goods> goodsList  = (List<Goods>)session.getAttribute("goodsList");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd");
        order.setFinish_time(LocalDateTime.now().format(formatter));

        order.setOrder_status(OrderStatus.OK);
        boolean effect = commitOrder(order);
        System.out.println("effect :" + effect);
        if (effect) {
            for (Goods goods : goodsList) {
                boolean isUpdate = updateAfterBuy(goods, goods.getBuyGoodsNum());
                System.out.println("isupdate:" + isUpdate);
                if (isUpdate) {
                    System.out.println("OK");
                } else {
                    System.out.println("NO");
                }
            }
            resp.sendRedirect("buyGoodsSuccess.html");
        }
    }

    private boolean updateAfterBuy(Goods goods, int buyGoodsNum) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean effect = false;
        try {
            String sql = "update goods set stock=? where id=?";
            connection = DBUtil.getConnection(true);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,goods.getStock() - buyGoodsNum);
            preparedStatement.setInt(2,goods.getId());
            if (preparedStatement.executeUpdate() == 1) {
                effect = true;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return effect;
    }

    private boolean commitOrder(Order order) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            String insertOrder = "insert into `order`(id,account_id,account_name," +
                    "create_time,finish_time,actual_amount,total_money,order_status) " +
                    "values(?,?,?,?,?,?,?,?)";
            String insertOrderItem = "insert into order_item(order_id,goods_id,goods_name," +
                    "goods_introduce,goods_num,goods_unit,goods_price,goods_discount) " +
                    "values(?,?,?,?,?,?,?,?)";
            connection = DBUtil.getConnection(false);
            preparedStatement = connection.prepareStatement(insertOrder);
            preparedStatement.setString(1,order.getId());
            preparedStatement.setInt(2,order.getAccount_id());
            preparedStatement.setString(3,order.getAccount_name());
            preparedStatement.setString(4,order.getCreate_time());
            preparedStatement.setString(5,order.getFinish_time());
            preparedStatement.setInt(6,order.getActualAmountInt());
            preparedStatement.setInt(7,order.getTotalMoneyInt());
            preparedStatement.setInt(8,order.getOrder_status().getFlg());
            if (preparedStatement.executeUpdate() == 0) {
                throw new RuntimeException("插入失败");
            }
            preparedStatement = connection.prepareStatement(insertOrderItem);
            for (OrderItem orderItem : order.orderItemsList) {
                preparedStatement.setString(1,order.getId());
                preparedStatement.setInt(2,orderItem.getGoodsId());
                preparedStatement.setString(3,orderItem.getGoodsName());
                preparedStatement.setString(4,orderItem.getGoodsIntroduce());
                preparedStatement.setInt(5,orderItem.getGoodsNum());
                preparedStatement.setString(6,orderItem.getGoodsUnit());
                preparedStatement.setInt(7,orderItem.getGoodsPriceInt());
                preparedStatement.setInt(8,orderItem.getGoodsDiscount());

                preparedStatement.addBatch();
            }

            int[] effects = preparedStatement.executeBatch();
            for (int i : effects) {
                if (i == 0) {
                    throw new RuntimeException("插入订单失败");
                }
            }
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (connection != null ){
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            return false;
        }finally {
            DBUtil.close(connection,preparedStatement,null);
        }
        return true;
    }
}
