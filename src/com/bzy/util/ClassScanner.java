package com.bzy.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 用来扫描指定包下的类
 * @author bzy
 *
 */
public class ClassScanner {
	//为什么是Map<String,Class<?>>类型呢？  因为String存储类名，Class对象存储反射生成的类对象
	 //basePackage为传入的包名
	public static Map<String,Class<?>> scannerClass(String basePackage) { 
		Map<String,Class<?>> results = new HashMap<>();
		//通过包将 . 替换成/ 
		String filePath = basePackage.replace(".","/");    
		try {
			//返回当前正在执行的线程对象的引用   Thread.currentThread()
			Thread currentThread = Thread.currentThread();
			//返回当前对象上的类装载器
			ClassLoader contextClassLoader = currentThread.getContextClassLoader(); 
			//拿到资源
			URL resource = contextClassLoader.getResource(filePath); 
			//拿到资源的路径   /E:/TestSpringMVC/build/classes/com/bzy
			String rootPath = resource.getPath();  
			if(rootPath != null){
				//filePath为com/bzy rootPath为/E:/TestSpringMVC/build/classes/com/bzy 为什么不直接将filePath赋值给rootPath呢？
				//这里是为了确保传入的路径存在      得到rootPath = com/bzy
				rootPath = rootPath.substring(rootPath.lastIndexOf(filePath)); 	   
			}
			//查找具有给定名称的所有资源
			Enumeration<URL> dirs = contextClassLoader.getResources(rootPath);	
			while(dirs.hasMoreElements()){
				URL url = dirs.nextElement(); // url: file:/E:/TestSpringMVC/build/classes/com/bzy    
				//根据url 判定是否是文件对象
				if(url.getProtocol().equals("file")){
					File file = new File(url.getPath().substring(1));   //把头上的   /  去掉
					scannerFile(file, rootPath, results);	 // 将文件传入文件扫描器			
				}   
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}
	
	private static void scannerFile(File folder,String rootPath,Map<String,Class<?>> classes) throws Exception{
		//拿到这个folder下的所有文件对象
		File[] files = folder.listFiles();
		for(int i=0;files!=null && i<files.length;i++){
			File file = files[i]; 
			//如果是文件夹，进行递归
			if(file.isDirectory()){    
				scannerFile(file, rootPath+file.getName()+"/",classes);
			}else{
				//拿到文件的绝对路径  E:\Tomcat01\apache-tomee-plume-7.0.1\apache-tomee-plume-7.0.1\webapps\TestSpringMVC\WEB-INF\classes\com\bzy\servlet\DispacherServlet.class
				String path = file.getAbsolutePath(); 
				//判断文件是不是一个类文件 
				if(path.endsWith(".class")){            
					 // 将windows中的 \ 替换掉   E:/Tomcat01/apache-tomee-plume-7.0.1/apache-tomee-plume-7.0.1/webapps/TestSpringMVC/WEB-INF/classes/com/bzy/servlet/DispacherServlet.class
					path = path.replace("\\","/");     
					String className = rootPath+path.substring(path.lastIndexOf("/")+1,path.indexOf(".class"));   // com/bzy/servlet/DispacherServlet
					className = className.replace("/","."); 			// com.bzy.servlet.DispacherServlet
					classes.put(className,Class.forName(className));    //  最后通过反射生成Class对象，添加到集合中
				}
			}
		}
		
	}
	
}






