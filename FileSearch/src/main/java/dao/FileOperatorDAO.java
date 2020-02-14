package dao;

import app.FileMeta;
import util.DBUtil;
import util.Pinyin4jUtil;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FileOperatorDAO {

    public static List<FileMeta> query(String dirPath) {

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<FileMeta> metas = new ArrayList<>();
        try {

            connection = DBUtil.getConnection();
            String sql = "select name,path,size,last_modified,is_directory" +
                    " from file_meta where path=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, dirPath);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String path = resultSet.getString("path");
                long size = resultSet.getLong("size");
                long last_modified = resultSet.getLong("last_modified");
                boolean is_directory = resultSet.getBoolean("is_directory");
                FileMeta meta = new FileMeta(name, path, size, last_modified, is_directory);
                metas.add(meta);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection, statement, resultSet);
        }
        return metas;
    }

//    public static void main(String[] args) {
//        //System.out.println(query("C:\\Users\\c\\Desktop\\java\\笔记\\MySQL"));
//        delete((new FileMeta("Desktop",
//                                "C:\\Users\\c",
//                                0L,
//                                0L,
//                                true)));
//    }

    public static void insert(FileMeta localMeta) {
        Connection connection = null;
        PreparedStatement statement = null;
        try{
            try {
                //获取连接
                connection = DBUtil.getConnection();
                String sql = "insert into file_meta" +
                        " (name,path,is_directory," +
                        " pinyin,pinyin_first," +
                        " size,last_modified)" +
                        " values (?,?,?,?,?,?,?)";
                //获取命令
                statement = connection.prepareStatement(sql);
                statement.setString(1,localMeta.getName());
                statement.setString(2,localMeta.getPath());
                statement.setBoolean(3,localMeta.getDirectory());
                String pinyin = null;
                String pinyin_first = null;
                System.out.println("insert:" + localMeta.getName() + File.separator
                + localMeta.getPath());
                if (Pinyin4jUtil.containsChinese(localMeta.getName())){
                    String[] pinyins = Pinyin4jUtil.get(localMeta.getName());
                    pinyin = pinyins[0];
                    pinyin_first = pinyins[1];
                }
                statement.setString(4,pinyin);
                statement.setString(5,pinyin_first);
                statement.setLong(6,localMeta.getSize());
                statement.setTimestamp(7,
                        new Timestamp(localMeta.getLastModified()));
                //执行sql语句
                statement.executeUpdate();
            } finally {
                DBUtil.close(connection,statement);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void delete(FileMeta meta) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            //获取连接
            connection = DBUtil.getConnection();
            connection.setAutoCommit(false);
            String sql = "delete from file_meta where" +
                    " name=? and path=? and is_directory=?";
            //获取命令
            statement = connection.prepareStatement(sql);
            statement.setString(1, meta.getName());
            statement.setString(2, meta.getPath());
            statement.setBoolean(3, meta.getDirectory());
            statement.executeUpdate();
            if (meta.getDirectory()) {
                sql =  "delete from file_meta where path=? or path like ?";
                statement = connection.prepareStatement(sql);
                String path =meta.getPath() + File.separator + meta.getName();
                        statement.setString(1,
                                path);
                        statement.setString(2,
                                path + File.separator + "%");
                statement.executeUpdate();
            }
            connection.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            DBUtil.close(connection, statement);
        }
    }

    public static List<FileMeta> search(String src, String search) {

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<FileMeta> metas = new ArrayList<>();
        try {

            connection = DBUtil.getConnection();
            boolean empty = src==null||src.trim().length() == 0;
            String sql = "select name,path,size,last_modified,is_directory" +
                    " from file_meta where " +
                    "name like ? or pinyin like ? or pinyin_first like ?" +
                    (empty ? "" : "and (path=? or path like ?)");
            statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + search + "%");
            statement.setString(2, "%" + search + "%");
            statement.setString(3, "%" + search+  "%");
            if (!empty) {
                statement.setString(4, src);
                statement.setString(5, src + File.separator + "%");
            }
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String path = resultSet.getString("path");
                long size = resultSet.getLong("size");
                long last_modified = resultSet.getLong("last_modified");
                boolean is_directory = resultSet.getBoolean("is_directory");
                FileMeta meta = new FileMeta(name, path, size,
                        last_modified, is_directory);
                System.out.println("search:" + name + "," + path);
                metas.add(meta);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection, statement, resultSet);
        }
        return metas;

    }
}
