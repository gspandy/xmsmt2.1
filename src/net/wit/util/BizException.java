package net.wit.util;


public class BizException extends Exception{
	    public BizException()  {}                //用来创建无参数对象
	    public BizException(String message) {        //用来创建指定参数对象
	        super(message);                             //调用超类构造器
	    }
}
