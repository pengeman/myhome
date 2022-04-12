package org.peng.myhome;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Setup extends HttpServlet {
    private  final String Class_Name = "org.sqlite.JDBC";
    private  final String home = System.getProperty("user.home");
    private  final String DB_URL = "jdbc:sqlite:" + home + "/MyHomePage/data/myhome";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String p = request.getParameter("p");
        if (p.equals("all")) {// 检索全部数据，跳转到显示页面
            List bookmarks = retrieveAll();
            request.setAttribute("bookmarks", bookmarks);
            request.getRequestDispatcher("setup.jsp").forward(request, response);
        }
        if (p.equals("new")){  // 跳转到新增页面
            request.setAttribute("p","new");
            request.getRequestDispatcher("setup_new.jsp").forward(request,response);
        }
        if (p.equals("update_go")){
            String id = request.getParameter("id");
            String name = request.getParameter("name");
            String url = request.getParameter("url");
            request.setAttribute("id",id);
            request.setAttribute("name",name);
            request.setAttribute("url",url);
            request.getRequestDispatcher("setup_update.jsp").forward(request,response);
        }
        if (p.equals("update_do")){
            String id = request.getParameter("id");
            String name = request.getParameter("name");
            String url = request.getParameter("url");
            request.setAttribute("id",id);
            request.setAttribute("name",name);
            request.setAttribute("url",url);
            this.update(id,name,url);
            List bookmarks = retrieveAll();
            request.setAttribute("bookmarks", bookmarks);
            request.getRequestDispatcher("setup.jsp").forward(request, response);
        }
        if (p.equals("delete")){
            String id = request.getParameter("id");
            this.delete(id);
        }
        if (p.equals("add")){ // 新增数据，保存到数据库
            String name = request.getParameter("name");
            String url = request.getParameter("url");
            this.add(name,url);
            List bookmarks = retrieveAll();
            request.setAttribute("bookmarks", bookmarks);
            request.getRequestDispatcher("setup.jsp?p=all").forward(request, response);

        }


    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private List retrieveAll() {
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
                Map cols = new java.util.HashMap<String,String>();
                long id = (long) rs.getDouble("id");
                String name = rs.getString("name");
                String url = rs.getString("url");
                System.out.println("name: " + name + " url : " + url);
                cols.put("id",id);
                cols.put("name",name);
                cols.put("url",url);
                row.add(cols);
                // 执行插入语句操作
                //statement1.executeUpdate("insert into table_name2(col2) values('" + col2_value + "')");
                // 执行更新语句
                //statement1.executeUpdate("update table_name2 set 字段名1=" +  字段值1 + " where 字段名2='" +  字段值2 + "'");
            }

        }
        catch (java.lang.Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return  row;
    }

    private void add(String name , String url ){
        // 新增数据保存到数据库
        java.sql.Connection con = null;
        String sql = " insert into cc (name , url ) values('" + name + "','" + url + "')";
        System.out.println(sql);
        try{
            Class.forName(Class_Name);
            con = java.sql.DriverManager.getConnection(DB_URL);
            int r = con.createStatement().executeUpdate(sql);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    private void update(String id , String name , String url){
        String sql = " update cc set name = '" + name + "',url='" + url + "' where id = " + id;
        java.sql.Connection con = null;
        try {
            Class.forName(Class_Name);
            con = java.sql.DriverManager.getConnection(DB_URL);
            int r = con.createStatement().executeUpdate(sql);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void delete(String id ){
        String sql = " delete from cc  where id = " + id;
        java.sql.Connection con = null;
        try {
            Class.forName(Class_Name);
            con = java.sql.DriverManager.getConnection(DB_URL);
            int r = con.createStatement().executeUpdate(sql);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
