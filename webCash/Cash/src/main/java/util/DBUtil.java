package util;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {
    // 数据库连接url地址
    private static final String URL = "jdbc:mysql://localhost:3306/cash";
    // 数据库账号
    private static final String USERNAME = "root";
    // 数据库密码
    private static final String PASSWORD = "045510";

    private static volatile DataSource DATASOURCE;

    public static DataSource getDATASOURCE() {
        if (DATASOURCE == null) {
            synchronized (DBUtil.class) {
                if (DATASOURCE == null) {
                    DATASOURCE = new MysqlDataSource();
                    ((MysqlDataSource) DATASOURCE).setURL(URL);
                    ((MysqlDataSource) DATASOURCE).setUser(USERNAME);
                    ((MysqlDataSource) DATASOURCE).setPassword(PASSWORD);
                }
            }
        }
        return DATASOURCE;
    }
    public static Connection getConnection(boolean autoCommit) {
        try {
            Connection connection = getDATASOURCE().getConnection();
            connection.setAutoCommit(autoCommit);
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("获取连接失败");
        }
    }
    public static void close(Connection connection ,
                             PreparedStatement preparedStatement,
                             ResultSet resultSet) {
        try {
            if (resultSet != null ) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
