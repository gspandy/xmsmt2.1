package net.wit.mobile.controller;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: linli
 * Date: 14-12-3
 * Time: 下午9:08
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseController {

    public static final String SUCCESS_FIELD = "success";

    public static final String  MESSAGE_FIELD = "message";

    public static final String RESULT_VALUE_FIELD = "resultValue";

    public static final String TOKEN = "token";

    public static final String CODE = "code";

    public void handleJsonTokenResponse(HttpServletResponse response,boolean success,String message) throws IOException {
        JSONObject result = new JSONObject();
        result.put(SUCCESS_FIELD,String.valueOf(success));
        if(message!=null){
            result.put(MESSAGE_FIELD,message);
        }else{
            result.put(MESSAGE_FIELD,"");
        }
        result.put(CODE, "5000");
        response.setHeader("Content-Type","text/html; charset=UTF-8");
        response.getWriter().write(result.toString());

    }

    public void handleJsonResponse(HttpServletResponse response,boolean success,String message) throws IOException {
        JSONObject result = new JSONObject();
        result.put(SUCCESS_FIELD,String.valueOf(success));
        if(message!=null){
            result.put(MESSAGE_FIELD,message);
        }else{
            result.put(MESSAGE_FIELD,"");
        }
//        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(response.getOutputStream());
//        outputStreamWriter.write(result.toString());
        response.setHeader("Content-Type","text/html; charset=UTF-8");
        response.getWriter().write(result.toString());

    }

    public void handleJsonArrayResponse(HttpServletResponse response,boolean success,String message,JSONArray jsonArray) throws IOException {
        JSONObject result = new JSONObject();
        result.put(SUCCESS_FIELD,String.valueOf(success));
        if(message!=null){
            result.put(MESSAGE_FIELD,message);
        }else{
            result.put(MESSAGE_FIELD,"");
        }
        if(jsonArray!=null){
            result.put(RESULT_VALUE_FIELD,jsonArray);
        }else{
            result.put(RESULT_VALUE_FIELD,"");
        }
//        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(response.getOutputStream());
//        outputStreamWriter.write(result.toString());
        response.setHeader("Content-Type","text/html; charset=UTF-8");
        response.getWriter().write(result.toString());

    }

    public void handleJsonResponse(HttpServletResponse response,boolean success,String message,JSONObject resultValue) throws IOException {
        JSONObject result = new JSONObject();
        result.put(SUCCESS_FIELD,String.valueOf(success));
        if(message!=null){
            result.put(MESSAGE_FIELD,message);
        }else{
            result.put(MESSAGE_FIELD,"");
        }
        if(resultValue!=null){
            result.put(RESULT_VALUE_FIELD,resultValue);
        }else{
            result.put(RESULT_VALUE_FIELD,"");
        }
//        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(response.getOutputStream());
//        outputStreamWriter.write(result.toString());
        response.setHeader("Content-Type","text/html; charset=UTF-8");
        response.getWriter().write(result.toString());

    }

    public void handleJsonResponse(HttpServletResponse response,boolean success,String message,JSONArray resultValue) throws IOException {
        JSONObject result = new JSONObject();
        result.put(SUCCESS_FIELD,String.valueOf(success));
        if(message!=null){
            result.put(MESSAGE_FIELD,message);
        }else{
            result.put(MESSAGE_FIELD,"");
        }
        if(resultValue!=null){
            result.put(RESULT_VALUE_FIELD,resultValue);
        }else{
            result.put(RESULT_VALUE_FIELD,"");
        }

//        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(response.getOutputStream());
//        outputStreamWriter.write(result.toString());
        response.setHeader("Content-Type","text/html; charset=UTF-8");
        response.getWriter().write(result.toString());

    }
}
