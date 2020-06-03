package com.rjl.tomcat.util;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

/**
 * @description ：连接池工具类
 */
public class DruidUtils {

    private static DataSource ds;

    static {
        try {
            // 1.1 读取配置文件内容
            Properties props = new Properties();
            // 1.2 加载文件
            InputStream in = DruidUtils.class.getClassLoader().getResourceAsStream("druid.properties");
            props.load(in);
            // 1.3 获取连接池 druid连接池
            ds = DruidDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1 获取连接
    public static Connection getConnection() throws Exception {
        return  ds.getConnection();
    }

    // 2 获取连接池 数据源, 将来给jdbc工具类使用, 如 DBUtils, mybatis
    public static DataSource getDataSource() throws Exception {
        return ds;
    }
}
