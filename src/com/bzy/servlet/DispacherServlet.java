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
 
//����ӳ��·�����ʼ������
@WebServlet(urlPatterns={"*.do"},initParams={@WebInitParam(name="basePackage",value="com.bzy")})
public class DispacherServlet extends HttpServlet {
	private Map<String,Object> controllers = new HashMap<>(); //�洢����RequestMappingע����Controllerע���controllerʵ��
	private Map<String,Method> methods = new HashMap<>();     //�洢����RequestMappingע��ķ���
public void init(){
	 ServletConfig servletConfig = this.getServletConfig();
	 String basePackage = servletConfig.getInitParameter("basePackage");
	 try {
		Map<String,Class<?>> cons = ClassScanner.scannerClass(basePackage); //��com.bzy���룬�õ��������������ļ���
		Iterator<String> itor = cons.keySet().iterator();      // �������������� 
		while (itor.hasNext()) {
			 String className = itor.next();   //�õ�ÿһ���������������
			 Class clazz = cons.get(className);
			 String path = "";
				//�ж��Ƿ����requestMappingע����Controllerע��
				if(clazz.isAnnotationPresent(RequestMapping.class) && clazz.isAnnotationPresent(Controller.class)){
					RequestMapping reqAnno = (RequestMapping)clazz.getAnnotation(RequestMapping.class);  //�����õ�ע����
					path = reqAnno.value(); //�õ�ע�����е�·��
				controllers.put(className,clazz.newInstance());  // ��ӵ��µļ�����
				Method[] ms = clazz.getDeclaredMethods(); //�õ�Controller�������еķ���
				for (Method method : ms) {
					if(!method.isAnnotationPresent(RequestMapping.class)){    //���������û��RequestMappingע����continue
						continue;
					}
					methods.put(path+method.getAnnotation(RequestMapping.class).value(),method);  //��������·���뷽��������ӵ�methods��
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
			if(method == null){  //�����·��������
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
