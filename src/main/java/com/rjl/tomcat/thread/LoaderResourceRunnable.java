package com.rjl.tomcat.thread;


import com.rjl.tomcat.constant.FileConstant;
import com.rjl.tomcat.exception.IsNotImplementsHttpServletException;
import com.rjl.tomcat.inner.HttpServlet;
import com.rjl.tomcat.inner.Servlet;
import com.rjl.tomcat.inner.ServletConcurrentHashMap;
import com.rjl.tomcat.util.Utils;

import java.util.Set;

/**
 * @author ：jl.ruan
 * @date ：Created in 2020/6/1
 * @description ：初始化servlet线程
 * @version: 1.0
 */
public class LoaderResourceRunnable implements Runnable {


    @Override
    public void run() {

        try {
            //初始化Servlet容器,打包后扫描工具类失效
            initServlet();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //初始化Servlet
    public void initServlet() throws Exception {

        // 调用类扫描工具，获取类字节实例列表
        //List<Class<?>> classList = ClassScanner.getClasssFromPackage(FileConstant.SCANNER_CLASS_PATH);
        Set<Class> classSet = Utils.getClasses(FileConstant.SCANNER_CLASS_PATH);
        if (classSet != null && classSet.size() > 0) {
            // 遍历Class
            for (Class<?> aClass : classSet) {
                // 构建Servlet实例
                HttpServlet httpServlet = (HttpServlet) aClass.newInstance();

                //判断类上是否有@Servlet注解
                if (aClass.isAnnotationPresent(Servlet.class)) {

                    //判断类是否实现HttpServlet接口
                    Class<?>[] interfaces = aClass.getInterfaces();
                    for (Class clazzInfo : interfaces) {
                        if (clazzInfo == HttpServlet.class) {

                            // 获取注解属性值，即请求资源路径
                            String servletName = aClass.getAnnotation(Servlet.class).value();
                            // 存储到servlet容器中
                            ServletConcurrentHashMap.concurrentHashMap.put("/servlet/" + servletName, httpServlet);
                        } else {

                            //未实现抛出自定义异常
                            throw new IsNotImplementsHttpServletException(aClass.getName() + "is not implements HttpServlet");
                        }
                    }
                }
            }
        }

    }
}
