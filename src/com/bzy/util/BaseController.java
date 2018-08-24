package com.bzy.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BaseController {
protected HttpServletRequest req;
protected HttpServletResponse resp;
public HttpServletRequest getReq() {
	return req;
}
public void setReq(HttpServletRequest req) {
	this.req = req;
}
public HttpServletResponse getResp() {
	return resp;
}
public void setResp(HttpServletResponse resp) {
	this.resp = resp;
}
public void init(HttpServletRequest req,HttpServletResponse resp){
	this.req = req;
	this.resp = resp;
}
}
