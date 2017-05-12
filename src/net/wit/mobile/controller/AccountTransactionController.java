package net.wit.mobile.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/accountTransaction")
public class AccountTransactionController extends BaseController {

	@RequestMapping(value = "/accountBalance")
	public void accountBalance(HttpServletResponse response,@RequestParam("token") String token
			) throws Exception {}
	
	
	@RequestMapping(value = "/cashDetail")
	public void cashDetail(HttpServletResponse response
			,@RequestParam("token") String token
			) throws Exception {}
}
