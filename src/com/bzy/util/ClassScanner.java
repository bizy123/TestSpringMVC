package com.bzy.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * ����ɨ��ָ�����µ���
 * @author bzy
 *
 */
public class ClassScanner {
	//Ϊʲô��Map<String,Class<?>>�����أ�  ��ΪString�洢������Class����洢�������ɵ������
	 //basePackageΪ����İ���
	public static Map<String,Class<?>> scannerClass(String basePackage) { 
		Map<String,Class<?>> results = new HashMap<>();
		//ͨ������ . �滻��/ 
		String filePath = basePackage.replace(".","/");    
		try {
			//���ص�ǰ����ִ�е��̶߳��������   Thread.currentThread()
			Thread currentThread = Thread.currentThread();
			//���ص�ǰ�����ϵ���װ����
			ClassLoader contextClassLoader = currentThread.getContextClassLoader(); 
			//�õ���Դ
			URL resource = contextClassLoader.getResource(filePath); 
			//�õ���Դ��·��   /E:/TestSpringMVC/build/classes/com/bzy
			String rootPath = resource.getPath();  
			if(rootPath != null){
				//filePathΪcom/bzy rootPathΪ/E:/TestSpringMVC/build/classes/com/bzy Ϊʲô��ֱ�ӽ�filePath��ֵ��rootPath�أ�
				//������Ϊ��ȷ�������·������      �õ�rootPath = com/bzy
				rootPath = rootPath.substring(rootPath.lastIndexOf(filePath)); 	   
			}
			//���Ҿ��и������Ƶ�������Դ
			Enumeration<URL> dirs = contextClassLoader.getResources(rootPath);	
			while(dirs.hasMoreElements()){
				URL url = dirs.nextElement(); // url: file:/E:/TestSpringMVC/build/classes/com/bzy    
				//����url �ж��Ƿ����ļ�����
				if(url.getProtocol().equals("file")){
					File file = new File(url.getPath().substring(1));   //��ͷ�ϵ�   /  ȥ��
					scannerFile(file, rootPath, results);	 // ���ļ������ļ�ɨ����			
				}   
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}
	
	private static void scannerFile(File folder,String rootPath,Map<String,Class<?>> classes) throws Exception{
		//�õ����folder�µ������ļ�����
		File[] files = folder.listFiles();
		for(int i=0;files!=null && i<files.length;i++){
			File file = files[i]; 
			//������ļ��У����еݹ�
			if(file.isDirectory()){    
				scannerFile(file, rootPath+file.getName()+"/",classes);
			}else{
				//�õ��ļ��ľ���·��  E:\Tomcat01\apache-tomee-plume-7.0.1\apache-tomee-plume-7.0.1\webapps\TestSpringMVC\WEB-INF\classes\com\bzy\servlet\DispacherServlet.class
				String path = file.getAbsolutePath(); 
				//�ж��ļ��ǲ���һ�����ļ� 
				if(path.endsWith(".class")){            
					 // ��windows�е� \ �滻��   E:/Tomcat01/apache-tomee-plume-7.0.1/apache-tomee-plume-7.0.1/webapps/TestSpringMVC/WEB-INF/classes/com/bzy/servlet/DispacherServlet.class
					path = path.replace("\\","/");     
					String className = rootPath+path.substring(path.lastIndexOf("/")+1,path.indexOf(".class"));   // com/bzy/servlet/DispacherServlet
					className = className.replace("/","."); 			// com.bzy.servlet.DispacherServlet
					classes.put(className,Class.forName(className));    //  ���ͨ����������Class������ӵ�������
				}
			}
		}
		
	}
	
}






