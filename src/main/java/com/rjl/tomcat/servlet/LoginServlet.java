package com.rjl.tomcat.servlet;


import com.rjl.tomcat.domain.Body;
import com.rjl.tomcat.domain.HttpRequest;
import com.rjl.tomcat.domain.HttpResponse;
import com.rjl.tomcat.domain.User;
import com.rjl.tomcat.inner.HttpServlet;
import com.rjl.tomcat.inner.Servlet;
import com.rjl.tomcat.util.DruidUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.util.List;

/**
 * @author ：jl.ruan
 * @date ：Created in 2020/6/1
 * @description ：登录处理类（待优化）
 * @version: 1.0
 */
@Servlet("LoginServlet")
public class LoginServlet implements HttpServlet {

    private String username;
    private String password;

    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {

        List<Body> bodies = httpRequest.getBodies();
        //get方式获取值
        String requestURI = httpRequest.getRequestURI();
        String[] split1 = requestURI.split("[?]");
        if(split1.length>1){

            String[] split2 = split1[1].split("&");
            for (String str : split2) {
                String[] split = str.split("=");
                if (split[0].equals("username"))
                    username = split[1];
                else if (split[0].equals("password"))
                    password = split[1];
            }
        }else{
            //否则使用post方式赋值
            for (Body body : bodies) {
                String key = body.getKey();
                if(key.equals("username"))
                    username=body.getValue();
                if(key.equals("password"))
                    password=body.getValue();
            }
        }



        try {
            // 2 根据用户名和密码查询用户信息
            QueryRunner queryRunner = new QueryRunner(DruidUtils.getDataSource());
            String sql = "select * from user where username=? and password=?";
            User user = queryRunner.query(sql, new BeanHandler<User>(User.class), username, password);
            // 3 判断
            if (user != null) {

                // 处理完毕以后给出客户端响应
                httpResponse.setContentType("text/html;charset=UTF-8");
                httpResponse.write("登录成功..........");
            } else {
                // 处理完毕以后给出客户端响应
                httpResponse.setContentType("text/html;charset=UTF-8");
                httpResponse.write("登录失败..........");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("LoginServlet....service...方法执行了.....");
    }

}
