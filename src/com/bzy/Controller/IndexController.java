package com.bzy.Controller;

import com.bzy.util.BaseController;
import com.interfac.annotation.Controller;
import com.interfac.annotation.RequestMapping;
  
@Controller
@RequestMapping("/IndexController")
public class IndexController extends BaseController{
	@RequestMapping("/index")
	public void index() {
		System.out.println("index运行"+req.getParameter("userName"));
}
	
	@RequestMapping("/find666666")
	public void find() {
		System.out.println("find运行"+req.getParameter("userName"));
}
}
