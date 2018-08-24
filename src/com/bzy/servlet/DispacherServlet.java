package com.bzy.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bzy.util.BaseController;
import com.bzy.util.ClassScanner;
import com.interfac.annotation.Controller;
import com.interfac.annotation.RequestMapping;
 
//配置映射路径与初始化参数
@WebServlet(urlPatterns={"*.do"},initParams={@WebInitParam(name="basePackage",value="com.bzy")})
public class DispacherServlet extends HttpServlet {
	private Map<String,Object> controllers = new HashMap<>(); //存储含有RequestMapping注解与Controller注解的controller实例
	private Map<String,Method> methods = new HashMap<>();     //存储含有RequestMapping注解的方法
public void init(){
	 ServletConfig servletConfig = this.getServletConfig();
	 String basePackage = servletConfig.getInitParameter("basePackage");
	 try {
		Map<String,Class<?>> cons = ClassScanner.scannerClass(basePackage); //将com.bzy传入，得到其下面的所有类的集合
		Iterator<String> itor = cons.keySet().iterator();      // 迭代器遍历集合 
		while (itor.hasNext()) {
			 String className = itor.next();   //拿到每一个的类名与类对象
			 Class clazz = cons.get(className);
			 String path = "";
				//判断是否包含requestMapping注解与Controller注解
				if(clazz.isAnnotationPresent(RequestMapping.class) && clazz.isAnnotationPresent(Controller.class)){
					RequestMapping reqAnno = (RequestMapping)clazz.getAnnotation(RequestMapping.class);  //反射拿到注解类
					path = reqAnno.value(); //拿到注解类中的路径
				controllers.put(className,clazz.newInstance());  // 添加到新的集合中
				Method[] ms = clazz.getDeclaredMethods(); //拿到Controller类中所有的方法
				for (Method method : ms) {
					if(!method.isAnnotationPresent(RequestMapping.class)){    //如果方法上没有RequestMapping注解则continue
						continue;
					}
					methods.put(path+method.getAnnotation(RequestMapping.class).value(),method);  //将方法的路径与方法对象添加到methods中
				}
			}		 
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
}


		protected void service(HttpServletRequest req, HttpServletResponse resp) {
			String uri = req.getRequestURI();
			String contextPath = req.getContextPath();
			int value = uri.indexOf(contextPath)+contextPath.length();
			String mappingPath = uri.substring(value,uri.indexOf(".do"));   
			Method method = methods.get(mappingPath);
			try {
			if(method == null){  //传入的路径不存在
				resp.getWriter().println("<font style='size:100px'>404 404 404 404 404 </font>");
				return;
			}
			    Class<?> declaringClass = method.getDeclaringClass();
				BaseController controller = (BaseController)controllers.get(declaringClass.getName());
				controller.init(req, resp);
				Object invoke = method.invoke(controller);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	}
}
