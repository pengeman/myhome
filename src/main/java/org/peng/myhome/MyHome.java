package org.peng.myhome;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet(name = "MyHome", value = "/MyHome")
public class MyHome extends HttpServlet {
    private final String Class_Name = "org.sqlite.JDBC";
    private final String home = System.getProperty("user.home");
    private final String FIlE_URL = home + "/MyHomePage/data/";
    private final String FILE_file = "myhome";
    private final String DB_URL = "jdbc:sqlite:" + FIlE_URL + FILE_file;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int ir = this.firstone(1); // 检查是否第一次运行，返回1:是第一次运行
        List ls_row = retrieve();
        request.setAttribute("rows", ls_row);
        request.getRequestDispatcher("myhome.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private int firstone(int ver) {
        // 第一运行时执行，返回1
        java.sql.Connection con = null;
        File file = new File(FIlE_URL);
        File file2 = new File(FIlE_URL+FILE_file);
            System.out.println("mkdirs DB_url");

        try {
        if (!file.exists()){
            file.mkdirs();

            file2.createNewFile();
        }

            Class.forName(Class_Name);
            con = DriverManager.getConnection(DB_URL);
            Statement statement = con.createStatement();
            statement.setQueryTimeout(30); // set timeout to 30 sec.
            // 执行查询语句
            String sql = "select code from ver_";
            ResultSet rs = statement.executeQuery(sql);
            rs.last();
            int rows = rs.getRow(); // 得到总行数
            if (rows != 1) {
                createDB(ver);
                return 1;
            }
            int ver_ = rs.getInt(1);
            if (ver != ver_) {
                createDB(ver);
                return 1;
            }
            return 0;
        } catch (ClassNotFoundException | SQLException | IOException e) {
            createDB(ver); // 数据库不存在，表示是第一次运行程序，则创建数据库
            return 0;
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void createDB(int ver) {
        java.sql.Connection con = null;
        try {
            Class.forName(Class_Name);
            con = DriverManager.getConnection(DB_URL);
            Statement statement = con.createStatement();
            statement.setQueryTimeout(30); // set timeout to 30 sec.
            // 执行查询语句
            String sql = "CREATE TABLE `cc` (`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,`name` CHAR, `url` CHAR)";
            int rs = statement.executeUpdate(sql);
            sql = " CREATE TABLE `ver_` (`code` INTEGER NOT NULL)";
            rs = statement.executeUpdate(sql);
            sql = " insert into ver_ (`code`) values(" + ver + ")";
            rs = statement.executeUpdate(sql);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    private List retrieve() {
        List row = new ArrayList<Map>();
        java.sql.Connection con = null;
        try {
            Class.forName(Class_Name);
            con = DriverManager.getConnection(DB_URL);
            Statement statement = con.createStatement();
            statement.setQueryTimeout(30); // set timeout to 30 sec.
            // 执行查询语句
            String sql = "select id, name, url from cc";
            ResultSet rs = statement.executeQuery(sql);
            System.out.println(sql);
            while (rs.next()) {
                Map cols = new java.util.HashMap<String, String>();
                long id = (long) rs.getDouble("id");
                String name = rs.getString("name");
                String url = rs.getString("url");
                System.out.println("name: " + name + " url : " + url);
                cols.put("name", name);
                cols.put("url", url);
                row.add(cols);
                // 执行插入语句操作
                //statement1.executeUpdate("insert into table_name2(col2) values('" + col2_value + "')");
                // 执行更新语句
                //statement1.executeUpdate("update table_name2 set 字段名1=" +  字段值1 + " where 字段名2='" +  字段值2 + "'");
            }

        } catch (java.lang.Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return row;
    }
}
