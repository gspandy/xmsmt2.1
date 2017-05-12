package net.wit.mobile.controller;

import net.wit.mobile.service.IParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: ab
 * Date: 15-9-5
 * Time: 下午6:05
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/param")
public class ParamController  extends BaseController {

    @RequestMapping(value = "/refreshAll")
    public void refreshAll(HttpServletResponse response) throws Exception{

    }
}
