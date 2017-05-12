package net.wit.controller.admin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.wit.Message;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.*;
import net.wit.entity.Charge.Status;
import net.wit.plugin.PaymentPlugin;
import net.wit.plugin.alipayMobile.config.AlipayConfig;
import net.wit.plugin.alipayMobile.util.AlipayTrans;
import net.wit.service.*;
import net.wit.util.DateUtil;
import net.wit.vo.OrderSettlementAdapter;
import net.wit.vo.OrderSettlementVO;
import net.wit.vo.ShareOrderSettlementVO;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminChargeController")
@RequestMapping({ "/admin/charge" })
public class ChargeController extends BaseController {

	@Resource(name = "chargeServiceImpl")
	private ChargeService chargeService;

	@Resource(name = "orderSettlementServiceImpl")
	private OrderSettlementService orderSettlementService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	@Resource(name = "memberBankServiceImpl")
	private MemberBankService memberBankService;
	
	@Resource(name = "tenantBonusSetServiceImpl")
	private  TenantBonusSetService tenantBonusSetService;

	@Resource(name = "orderServiceImpl")
	private OrderService orderService;
	
	@Resource(name = "vipLevelServiceImpl")
	private VipLevelService vipLevelService;
	@Autowired
	private PluginService pluginService;

	/**
	 * 分享佣金明细
	 * @param orderStatusParam
	 * @param time
	 * @param username
	 * @param pageable
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "shareChargeList")
	public String shareChargeList(String orderStatusParam, String time,
			String username, Pageable pageable, ModelMap model) {
		// Date month,
		// List<OrderSettlement.SettlementStatus> status, Member owner, Boolean
		// isCharged
		queryCommonList(orderStatusParam, time,username, pageable, model,true);
		return "admin/charge/shareChargeList";
	}
	
	/**
	 * 邀请奖金明细
	 * @param orderStatusParam
	 * @param time
	 * @param username
	 * @param pageable
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "recommendChargeList")
	public String recommendChargeList(String orderStatusParam, String time,
			String username, Pageable pageable, ModelMap model) {
		// Date month,
		// List<OrderSettlement.SettlementStatus> status, Member owner, Boolean
		// isCharged
		queryCommonList(orderStatusParam, time,username, pageable, model,false);
		return "admin/charge/recommendChargeList";
	}
	/**
	 * 邀请奖金月报表
	 * @param orderStatusParam
	 * @param time
	 * @param username
	 * @param pageable
	 * @param model
	 * @return8
	 */
	@RequestMapping(value = "recommendChargeListMonthReport")
	public String recommendChargeListMonthReport(String orderStatusParam, String time,
			String username, Pageable pageable, ModelMap model,String mobile) {
		
		OrderSettlementAdapter orderSettlementAdapter =null;
		try {
			if (null != username) {
				username = new String(username.getBytes("ISO-8859-1"), "UTF-8");
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Tenant tenant = adminService.getCurrent().getTenant();
		orderSettlementAdapter = orderSettlementService
				.recommendChargeListMonthReport(time, username,pageable,null, mobile,orderStatusParam,tenant.getId());
		Page<OrderSettlementVO> list = orderSettlementAdapter.getPage();
		Calendar calendar = Calendar.getInstance();
		int nowYear = calendar.get(Calendar.YEAR);
		int nowMonth = calendar.get(Calendar.MONTH)+1;
		calendar.set(Calendar.YEAR,2015);
		calendar.set(Calendar.MONTH,7);
		int startYear = calendar.get(Calendar.YEAR);
		int startMonth = calendar.get(Calendar.MONTH)+1;
		List<String> queryRealTime = new LinkedList<>();
		List<String> queryShowTime = new LinkedList<>();
		for (int i = startYear; i <= nowYear; i++) {
			if (i!=2015){
				startMonth=1;
			}
			for (int j = startMonth; j <= nowMonth; j++) {
				String month = j<=9?("0"+j):j+"";
				queryRealTime.add(i+"-"+month);
				queryShowTime.add(month+"月 "+i+"年");
			}
		}
		model.addAttribute("page", list);
		model.addAttribute("time", time);
		Collections.reverse(queryRealTime);
		Collections.reverse(queryShowTime);
		model.addAttribute("queryRealTime", queryRealTime);
		model.addAttribute("queryShowTime", queryShowTime);
		model.addAttribute("totalAmount", orderSettlementAdapter.getTotalAmount());
		model.addAttribute("totalSettleCharge",orderSettlementAdapter.getTotalSettleCharge());
		model.addAttribute("username", username);
		model.addAttribute("mobile", mobile);
		model.addAttribute("orderStatusParam", orderStatusParam);
		return "admin/charge/recommendChargeMonthList";
	}
	/**
	 * 分享佣金报表
	 * 导出未开票发票Excel表格
	 */
	@RequestMapping(value = { "/recommendChargeMonthExportExcel" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public void recommendChargeMonthExportExcel(HttpServletResponse response,String orderStatusParam, String time,
			String username, Pageable pageable, ModelMap model,String mobile,String ids) {
		try {
			if (null != username) {
				username = new String(username.getBytes("ISO-8859-1"), "UTF-8");
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet();

		Row nRow = null;
		Cell nCell = null;

		int rowNo = 0;
		int cellNo = 1;

		sheet.setColumnWidth(0, 4 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(1, 8 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(2, 15 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(3, 25 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(4, 10 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(5, 10 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(6, 25 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(7, 10 * 300); // 设置列的列宽 0代表列的索引

		// 打印大标题
		nRow = sheet.createRow(rowNo++);
		nRow.setHeightInPoints(36); // 行高
		nCell = nRow.createCell(cellNo); // 在第一行的第二列添加一个单元格

		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DATE);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);
		
		nCell.setCellValue("邀请奖金报表" + "（导出日期：" + String.valueOf(year) + "年"
				+ String.valueOf(month) + "月" + String.valueOf(day) + "日"
				+ String.valueOf(hour) + ":" + String.valueOf(minute) + ":"
				+ String.valueOf(second) + "）");

		nCell.setCellStyle(this.bigTitle(wb)); // 设置样式
		// 设置 第一行的第二列要合并单元格
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 7));

		

		// 打印大标题
		nRow = sheet.createRow(rowNo++);
		nRow.setHeightInPoints(22); // 行高
		nCell = nRow.createCell(cellNo); // 在第一行的第二列添加一个单元格
		
		
		
		// 设置 第一行的第二列要合并单元格
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 7));
		OrderSettlementAdapter list = null;
		Tenant tenant = adminService.getCurrent().getTenant();
		//1为明细
		if("1".equals(ids)){
			list = orderSettlementService
						.recommendChargeListMonthReport(time, username,pageable,null,mobile,orderStatusParam,tenant.getId());
		}else{
			time=year+"-"+month;
			 list = orderSettlementService
						.recommendChargeListMonthReport(time, null,null,null,null,null,tenant.getId());
		}
		
		nCell.setCellValue("订单合计金额：" +list.getTotalAmount());
		
		

		// 打印大标题
		nRow = sheet.createRow(rowNo++);
		nRow.setHeightInPoints(22); // 行高
		nCell = nRow.createCell(cellNo); // 在第一行的第二列添加一个单元格
		
		
		
		// 设置 第一行的第二列要合并单元格
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 7));
		nCell.setCellValue("邀请奖金合计金额：" +list.getTotalSettleCharge());
		
		// 打印小标题
		String titles[] = { "序号", "结算月份","店长姓名", "店长手机号", "关联店长销售总金额（¥）",
				"邀请奖金金额（¥）","结算状态" };

		// 第二行
		nRow = sheet.createRow(rowNo++);
		nRow.setHeightInPoints(26);

		// 循环打印出小标题
		for (String title : titles) {
			nCell = nRow.createCell(cellNo++);
			nCell.setCellValue(title);
			nCell.setCellStyle(this.title(wb));
		}

		// 打印数据
		// Tenant tenant = new Tenant();
		// tenant.setId(Long.parseLong(tenantId));
	/*	List<InvoiceManagement> invoiceManagementList = invoiceManagementService
				.findInvoiceManagementList(tenant);*/
	
		//List<InvoiceManagement> invoiceManagementList =null;
		int i = 1; // 预定义一个序列号, 作为Excel表格中数据的序列号
		for (OrderSettlementVO invoiceManage : list.getPage().getContent()) {

				cellNo = 1;
				nRow = sheet.createRow(rowNo++);
				nRow.setHeightInPoints(24);// 设置行高

				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(String.valueOf(i++)); // 每条数据的序列号
				nCell.setCellStyle(this.text(wb)); // 设置 样式

				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(invoiceManage.getChargeDate()); // 结算月份
				nCell.setCellStyle(this.text(wb)); // 设置 样式
				
				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(invoiceManage.getOwnerName()); // 订单编号
				nCell.setCellStyle(this.text(wb)); // 设置 样式

				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(invoiceManage.getMobile()); // 企业名称
				nCell.setCellStyle(this.text(wb)); // 设置 样式

			

				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(invoiceManage.getOrderAmount()+""); // 发票抬头
				nCell.setCellStyle(this.text(wb)); // 设置 样式
				
				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(invoiceManage.getSettleCharge()+""); // 发票抬头
				nCell.setCellStyle(this.text(wb)); // 设置 样式
				
				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(invoiceManage.getShareStatus()+""); // 发票抬头
				nCell.setCellStyle(this.text(wb)); // 设置 样式

			

		}

		try {

			// 导出Excel
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			wb.write(baos);
			baos.flush();
			baos.close();
			this.download(baos, response, "邀请奖金报表.xls");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	/**
	 * 分享佣金月报表
	 * @param orderStatusParam
	 * @param time
	 * @param username
	 * @param pageable
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "shareChargeMonthReport")
	public String shareChargeMonthReport(String orderStatusParam, String time,
			String username, Pageable pageable, ModelMap model,String mobile) {
		// Date month,
		// List<OrderSettlement.SettlementStatus> status, Member owner, Boolean
		// isCharged
		try {
			if (null != username) {
				username = new String(username.getBytes("ISO-8859-1"), "UTF-8");
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OrderSettlementAdapter orderSettlementAdapter =null;
		Tenant tenant = adminService.getCurrent().getTenant();
			orderSettlementAdapter = orderSettlementService
					.queryShareChargeMonthReportByCondition(time, username,pageable,null,mobile,orderStatusParam,tenant.getId());


		Page<OrderSettlementVO> list = orderSettlementAdapter.getPage();
		
		
		Calendar calendar = Calendar.getInstance();
		int nowYear = calendar.get(Calendar.YEAR);
		int nowMonth = calendar.get(Calendar.MONTH)+1;
		calendar.set(Calendar.YEAR,2015);
		calendar.set(Calendar.MONTH,7);
		int startYear = calendar.get(Calendar.YEAR);
		int startMonth = calendar.get(Calendar.MONTH)+1;
		List<String> queryRealTime = new LinkedList<>();
		List<String> queryShowTime = new LinkedList<>();
		for (int i = startYear; i <= nowYear; i++) {
			if (i!=2015){
				startMonth=1;
			}
			for (int j = startMonth; j <= nowMonth; j++) {
				String month = j<=9?("0"+j):j+"";
				queryRealTime.add(i+"-"+month);
				queryShowTime.add(month+"月 "+i+"年");
			}
		}
		model.addAttribute("page", list);
		model.addAttribute("time", time);
		Collections.reverse(queryRealTime);
		Collections.reverse(queryShowTime);
		model.addAttribute("queryRealTime", queryRealTime);
		model.addAttribute("queryShowTime", queryShowTime);
		model.addAttribute("totalAmount", orderSettlementAdapter.getTotalAmount());
		model.addAttribute("totalSettleCharge",orderSettlementAdapter.getTotalSettleCharge());
		model.addAttribute("username", username);
		model.addAttribute("mobile", mobile);
		model.addAttribute("orderStatusParam", orderStatusParam);
		return "admin/charge/shareChargeMonthList";
	}
	

	/**
	 * 分享佣金报表
	 * 导出未开票发票Excel表格
	 */
	@RequestMapping(value = { "/shareChargeMonthExportExcel" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public void shareChargeMonthExportExcel(HttpServletResponse response,String orderStatusParam, String time,
			String username, Pageable pageable, ModelMap model,String mobile,String ids) {
		try {
			if (null != username) {
				username = new String(username.getBytes("ISO-8859-1"), "UTF-8");
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Tenant tenant = adminService.getCurrent().getTenant();
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet();

		Row nRow = null;
		Cell nCell = null;

		int rowNo = 0;
		int cellNo = 1;

		sheet.setColumnWidth(0, 4 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(1, 8 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(2, 15 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(3, 25 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(4, 10 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(5, 10 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(6, 25 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(7, 10 * 300); // 设置列的列宽 0代表列的索引

		// 打印大标题
		nRow = sheet.createRow(rowNo++);
		nRow.setHeightInPoints(36); // 行高
		nCell = nRow.createCell(cellNo); // 在第一行的第二列添加一个单元格

		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DATE);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);
		
		nCell.setCellValue("分享佣金报表" + "（导出日期：" + String.valueOf(year) + "年"
				+ String.valueOf(month) + "月" + String.valueOf(day) + "日"
				+ String.valueOf(hour) + ":" + String.valueOf(minute) + ":"
				+ String.valueOf(second) + "）");

		nCell.setCellStyle(this.bigTitle(wb)); // 设置样式
		// 设置 第一行的第二列要合并单元格
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 7));

		

		// 打印大标题
		nRow = sheet.createRow(rowNo++);
		nRow.setHeightInPoints(22); // 行高
		nCell = nRow.createCell(cellNo); // 在第一行的第二列添加一个单元格
		
		
		
		// 设置 第一行的第二列要合并单元格
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 7));
		OrderSettlementAdapter list = null;
		
		//1为明细
		if("1".equals(ids)){
			list = orderSettlementService
						.queryShareChargeMonthReportByCondition(time, username,pageable,null,mobile,orderStatusParam,tenant.getId());
		}else{
			time=year+"-"+month;
			 list = orderSettlementService
						.queryShareChargeMonthReportByCondition(time, null,null,null,null,null,tenant.getId());
		}
		
		nCell.setCellValue("订单合计金额：" +list.getTotalAmount());
		
		

		// 打印大标题
		nRow = sheet.createRow(rowNo++);
		nRow.setHeightInPoints(22); // 行高
		nCell = nRow.createCell(cellNo); // 在第一行的第二列添加一个单元格
		
		
		
		// 设置 第一行的第二列要合并单元格
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 7));
		nCell.setCellValue("分享佣金合计金额：" +list.getTotalSettleCharge());
		
		// 打印小标题
		String titles[] = { "序号", "结算月份","店长姓名", "店长手机号", "订单金额（¥）",
				"分享佣金金额（¥）","结算状态" };

		// 第二行
		nRow = sheet.createRow(rowNo++);
		nRow.setHeightInPoints(26);

		// 循环打印出小标题
		for (String title : titles) {
			nCell = nRow.createCell(cellNo++);
			nCell.setCellValue(title);
			nCell.setCellStyle(this.title(wb));
		}

		// 打印数据
		// Tenant tenant = new Tenant();
		// tenant.setId(Long.parseLong(tenantId));
	/*	List<InvoiceManagement> invoiceManagementList = invoiceManagementService
				.findInvoiceManagementList(tenant);*/
	
		//List<InvoiceManagement> invoiceManagementList =null;
		int i = 1; // 预定义一个序列号, 作为Excel表格中数据的序列号
		for (OrderSettlementVO invoiceManage : list.getPage().getContent()) {

				cellNo = 1;
				nRow = sheet.createRow(rowNo++);
				nRow.setHeightInPoints(24);// 设置行高

				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(String.valueOf(i++)); // 每条数据的序列号
				nCell.setCellStyle(this.text(wb)); // 设置 样式

				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(invoiceManage.getChargeDate()); // 结算月份
				nCell.setCellStyle(this.text(wb)); // 设置 样式
				
				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(invoiceManage.getOwnerName()); // 订单编号
				nCell.setCellStyle(this.text(wb)); // 设置 样式

				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(invoiceManage.getMobile()); // 企业名称
				nCell.setCellStyle(this.text(wb)); // 设置 样式

			

				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(invoiceManage.getOrderAmount()+""); // 发票抬头
				nCell.setCellStyle(this.text(wb)); // 设置 样式
				
				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(invoiceManage.getSettleCharge()+""); // 发票抬头
				nCell.setCellStyle(this.text(wb)); // 设置 样式
				
				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(invoiceManage.getShareStatus()+""); // 发票抬头
				nCell.setCellStyle(this.text(wb)); // 设置 样式

			

		}

		try {

			// 导出Excel
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			wb.write(baos);
			baos.flush();
			baos.close();
			this.download(baos, response, "分享资金报表.xls");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private void queryCommonList(String orderStatusParam, String time,
			String username, Pageable pageable, ModelMap model,Boolean isShareCharte){
		
		try {
			if (null != username) {
				username = new String(username.getBytes("ISO-8859-1"), "UTF-8");
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OrderSettlementAdapter orderSettlementAdapter =null;
		Tenant tenant = adminService.getCurrent().getTenant();
		if(isShareCharte){
			orderSettlementAdapter = orderSettlementService
					.queryOrderSettlementsByCondition(time, username,
							orderStatusParam, pageable,null,tenant.getId());
		}else{
			orderSettlementAdapter = orderSettlementService
					.queryOrderRecommonSettlementsByCondition(time, username,
							orderStatusParam, pageable,null,tenant.getId());
		}


		Page<OrderSettlementVO> list = orderSettlementAdapter.getPage();
		
/*		BigDecimal totalAmount = new BigDecimal(0);
		Map<Long,TenantBonusSet> bonusSet = new HashMap<>();
		TenantBonusSet tenantBonusSet= null;
		List<OrderSettlementVO> listOrder = list.getContent();
		for (OrderSettlementVO orderSettlementVO : listOrder) {
			totalAmount = totalAmount.add(orderSettlementVO.getOrderAmount());
			Long key = orderSettlementVO.getTenant().longValue();
			if(bonusSet.containsKey(key)){
				 tenantBonusSet = bonusSet.get(key);
			}else{
				tenantBonusSet = tenantBonusSetService.getRegularTenantBonusSetByTenantId(key);
				bonusSet.put(key, tenantBonusSet);
			}
			BigDecimal recommonAmount =BigDecimal.ZERO;
			if(null!=tenantBonusSet.getRelativeSellBonusRate()&&tenantBonusSet.getRelativeSellBonusRate()!=0){
				recommonAmount = orderSettlementVO.getOrderSettleAmt().divide(new BigDecimal(tenantBonusSet.getRelativeSellBonusRate()), 2, BigDecimal.ROUND_HALF_UP);
			}
			orderSettlementVO.setRecommonAmount(recommonAmount);
		}*/

		Calendar calendar = Calendar.getInstance();
		int nowYear = calendar.get(Calendar.YEAR);
		int nowMonth = calendar.get(Calendar.MONTH)+1;
		calendar.set(Calendar.YEAR,2015);
		calendar.set(Calendar.MONTH,7);
		int startYear = calendar.get(Calendar.YEAR);
		int startMonth = calendar.get(Calendar.MONTH)+1;
		List<String> queryRealTime = new LinkedList<>();
		List<String> queryShowTime = new LinkedList<>();
		for (int i = startYear; i <= nowYear; i++) {
			if (i!=2015){
				startMonth=1;
			}
			for (int j = startMonth; j <= nowMonth; j++) {
				String month = j<=9?("0"+j):j+"";
				queryRealTime.add(i+"-"+month);
				queryShowTime.add(month+"月 "+i+"年");
			}
		}
		model.addAttribute("page", list);
		model.addAttribute("time", time);
		Collections.reverse(queryRealTime);
		Collections.reverse(queryShowTime);
		model.addAttribute("queryRealTime", queryRealTime);
		model.addAttribute("queryShowTime", queryShowTime);
		model.addAttribute("totalAmount", orderSettlementAdapter.getTotalAmount());
		model.addAttribute("totalSettleCharge",orderSettlementAdapter.getTotalSettleCharge());
		model.addAttribute("username", username);
		model.addAttribute("orderStatusParam", orderStatusParam);
	}

	/**
	 * 导出未开票发票Excel表格
	 */
	@RequestMapping(value = { "/export" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public void managementExportExcel(HttpServletResponse response,
			String ids,String orderStatusParam, String time,
			String username) {
		try {
			if (null != username) {
				username = new String(username.getBytes("ISO-8859-1"), "UTF-8");
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Tenant tenant = adminService.getCurrent().getTenant();
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet();

		Row nRow = null;
		Cell nCell = null;

		int rowNo = 0;
		int cellNo = 1;

		sheet.setColumnWidth(0, 4 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(1, 8 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(2, 15 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(3, 25 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(4, 10 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(5, 10 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(6, 25 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(7, 10 * 300); // 设置列的列宽 0代表列的索引

		// 打印大标题
		nRow = sheet.createRow(rowNo++);
		nRow.setHeightInPoints(36); // 行高
		nCell = nRow.createCell(cellNo); // 在第一行的第二列添加一个单元格

		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DATE);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);
		
		nCell.setCellValue("分享佣金明细" + "（导出日期：" + String.valueOf(year) + "年"
				+ String.valueOf(month) + "月" + String.valueOf(day) + "日"
				+ String.valueOf(hour) + ":" + String.valueOf(minute) + ":"
				+ String.valueOf(second) + "）");

		nCell.setCellStyle(this.bigTitle(wb)); // 设置样式
		// 设置 第一行的第二列要合并单元格
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 7));

		

		// 打印大标题
		nRow = sheet.createRow(rowNo++);
		nRow.setHeightInPoints(22); // 行高
		nCell = nRow.createCell(cellNo); // 在第一行的第二列添加一个单元格
		
		
		
		// 设置 第一行的第二列要合并单元格
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 7));
		OrderSettlementAdapter list = null;
		
		//1为明细
		if("1".equals(ids)){
			 list = orderSettlementService
						.queryOrderSettlementsByCondition(time, username,
								orderStatusParam, null, null,tenant.getId());
		}else{
			time=year+"-"+month;
			list = orderSettlementService
					.queryOrderSettlementsByCondition(time, null,
							null, null, null,tenant.getId());
		}
		
		nCell.setCellValue("订单合计金额：" +list.getTotalAmount());
		
		

		// 打印大标题
		nRow = sheet.createRow(rowNo++);
		nRow.setHeightInPoints(22); // 行高
		nCell = nRow.createCell(cellNo); // 在第一行的第二列添加一个单元格
		
		
		
		// 设置 第一行的第二列要合并单元格
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 7));
		nCell.setCellValue("分享佣金合计金额：" +list.getTotalSettleCharge());
		
		// 打印小标题
		String titles[] = { "序号", "店长姓名", "店长手机号", "订单编号", "付款日期", "订单金额（¥）",
				"分享佣金金额（¥）","结算状态" };

		// 第二行
		nRow = sheet.createRow(rowNo++);
		nRow.setHeightInPoints(26);

		// 循环打印出小标题
		for (String title : titles) {
			nCell = nRow.createCell(cellNo++);
			nCell.setCellValue(title);
			nCell.setCellStyle(this.title(wb));
		}

		// 打印数据
		// Tenant tenant = new Tenant();
		// tenant.setId(Long.parseLong(tenantId));
	/*	List<InvoiceManagement> invoiceManagementList = invoiceManagementService
				.findInvoiceManagementList(tenant);*/
	
		//List<InvoiceManagement> invoiceManagementList =null;
		int i = 1; // 预定义一个序列号, 作为Excel表格中数据的序列号
		for (OrderSettlementVO invoiceManage : list.getPage().getContent()) {

				cellNo = 1;
				nRow = sheet.createRow(rowNo++);
				nRow.setHeightInPoints(24);// 设置行高

				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(String.valueOf(i++)); // 每条数据的序列号
				nCell.setCellStyle(this.text(wb)); // 设置 样式

				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(invoiceManage.getOwnerName()); // 订单编号
				nCell.setCellStyle(this.text(wb)); // 设置 样式

				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(invoiceManage.getMobile()); // 企业名称
				nCell.setCellStyle(this.text(wb)); // 设置 样式

				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(invoiceManage.getSn()); // 发票内容
				nCell.setCellStyle(this.text(wb)); // 设置 样式

				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(invoiceManage.getPaymentDate()==null?"":invoiceManage.getPaymentDate() +""); // 发票金额
				nCell.setCellStyle(this.text(wb)); // 设置 样式

				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(invoiceManage.getOrderAmount()+""); // 发票抬头
				nCell.setCellStyle(this.text(wb)); // 设置 样式
				
				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(invoiceManage.getSettleCharge()+""); // 发票抬头
				nCell.setCellStyle(this.text(wb)); // 设置 样式
				
				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(invoiceManage.getStatus()+""); // 发票抬头
				nCell.setCellStyle(this.text(wb)); // 设置 样式

			

		}

		try {

			// 导出Excel
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			wb.write(baos);
			baos.flush();
			baos.close();
			this.download(baos, response, "分享资金明细.xls");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	/**
	 * 导出未开票发票Excel表格
	 */
	@RequestMapping(value = { "/recommendExport" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public void recommendExportExportExcel(HttpServletResponse response,
			String ids,String orderStatusParam, String time,
			String username) {
		try {
			if (null != username) {
				username = new String(username.getBytes("ISO-8859-1"), "UTF-8");
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet();
		
		Row nRow = null;
		Cell nCell = null;
		
		int rowNo = 0;
		int cellNo = 1;
		
		sheet.setColumnWidth(0, 4 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(1, 8 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(2, 15 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(3, 25 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(4, 10 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(5, 10 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(6, 25 * 300); // 设置列的列宽 0代表列的索引
		sheet.setColumnWidth(7, 10 * 300); // 设置列的列宽 0代表列的索引
		
		// 打印大标题
		nRow = sheet.createRow(rowNo++);
		nRow.setHeightInPoints(36); // 行高
		nCell = nRow.createCell(cellNo); // 在第一行的第二列添加一个单元格
		
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DATE);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);
		//time=year+"-"+month;
		nCell.setCellValue("邀请奖金明细" + "（导出日期：" + String.valueOf(year) + "年"
				+ String.valueOf(month) + "月" + String.valueOf(day) + "日"
				+ String.valueOf(hour) + ":" + String.valueOf(minute) + ":"
				+ String.valueOf(second) + "）");
		
		nCell.setCellStyle(this.bigTitle(wb)); // 设置样式
		// 设置 第一行的第二列要合并单元格
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 7));
		
		

		// 打印大标题
		nRow = sheet.createRow(rowNo++);
		nRow.setHeightInPoints(22); // 行高
		nCell = nRow.createCell(cellNo); // 在第一行的第二列添加一个单元格
		// 设置 第一行的第二列要合并单元格
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 7));
		OrderSettlementAdapter list = null;
		Tenant tenant = adminService.getCurrent().getTenant();
		//1为明细
		if("1".equals(ids)){
			 list = orderSettlementService
						.queryOrderRecommonSettlementsByCondition(time, username,
								orderStatusParam, null, null,tenant.getId());
		}else{
			time=year+"-"+month;
			list = orderSettlementService
					.queryOrderRecommonSettlementsByCondition(time, null,
							null, null, null,tenant.getId());
		}

//		nCell.setCellValue("分享佣金合计金额：" +list.getTotalSettleCharge());
		nCell.setCellValue("分享佣金合计金额：" +list.getTotalAmount());
		

		// 打印小标题
		String titles[] = { "序号", "店长姓名","邀请的店长", "店长手机号", "订单编号", "付款日期", "订单金额（¥）",
				"邀请奖金金额（¥）","结算状态" };
		
		// 第二行
		nRow = sheet.createRow(rowNo++);
		nRow.setHeightInPoints(26);
		
		// 循环打印出小标题
		for (String title : titles) {
			nCell = nRow.createCell(cellNo++);
			nCell.setCellValue(title);
			nCell.setCellStyle(this.title(wb));
		}
		
		// 打印数据
		// Tenant tenant = new Tenant();
		// tenant.setId(Long.parseLong(tenantId));
//		Tenant tenant = adminService.getCurrent().getTenant();
		/*	List<InvoiceManagement> invoiceManagementList = invoiceManagementService
				.findInvoiceManagementList(tenant);*/
		

		//List<InvoiceManagement> invoiceManagementList =null;
		int i = 1; // 预定义一个序列号, 作为Excel表格中数据的序列号
		for (OrderSettlementVO invoiceManage : list.getPage().getContent()) {
			
			cellNo = 1;
			nRow = sheet.createRow(rowNo++);
			nRow.setHeightInPoints(24);// 设置行高
			
			nCell = nRow.createCell(cellNo++); // 产生第一个单元格
			nCell.setCellValue(String.valueOf(i++)); // 每条数据的序列号
			nCell.setCellStyle(this.text(wb)); // 设置 样式
			
			nCell = nRow.createCell(cellNo++); // 产生第一个单元格
			nCell.setCellValue(invoiceManage.getOwnerName()); // 订单编号
			nCell.setCellStyle(this.text(wb)); // 设置 样式

			nCell = nRow.createCell(cellNo++); // 产生第一个单元格
			nCell.setCellValue(invoiceManage.getRecommendName()); // 订单编号
			nCell.setCellStyle(this.text(wb)); // 设置 样式

			nCell = nRow.createCell(cellNo++); // 产生第一个单元格
			nCell.setCellValue(invoiceManage.getMobile()); // 企业名称
			nCell.setCellStyle(this.text(wb)); // 设置 样式
			
			nCell = nRow.createCell(cellNo++); // 产生第一个单元格
			nCell.setCellValue(invoiceManage.getSn()); // 发票内容
			nCell.setCellStyle(this.text(wb)); // 设置 样式
			
			nCell = nRow.createCell(cellNo++); // 产生第一个单元格
			nCell.setCellValue(invoiceManage.getPaymentDate()==null?"":invoiceManage.getPaymentDate() +""); // 发票金额
			nCell.setCellStyle(this.text(wb)); // 设置 样式
			
			nCell = nRow.createCell(cellNo++); // 产生第一个单元格
			nCell.setCellValue(invoiceManage.getOrderAmount()+""); // 发票抬头
			nCell.setCellStyle(this.text(wb)); // 设置 样式
			
			nCell = nRow.createCell(cellNo++); // 产生第一个单元格
			nCell.setCellValue(invoiceManage.getRecommonAmount()+""); // 发票抬头
			nCell.setCellStyle(this.text(wb)); // 设置 样式
			
			nCell = nRow.createCell(cellNo++); // 产生第一个单元格
			nCell.setCellValue(invoiceManage.getStatus()+""); // 发票抬头
			nCell.setCellStyle(this.text(wb)); // 设置 样式
			
			
			
		}
		
		try {
			
			// 导出Excel
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			wb.write(baos);
			baos.flush();
			baos.close();
			this.download(baos, response, "邀请资金明细.xls");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}


	// 小标题的样式
	public CellStyle title(Workbook wb) {
		
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setFontName("黑体");
		font.setFontHeightInPoints((short) 12);

		style.setFont(font);

		style.setAlignment(CellStyle.ALIGN_CENTER); // 横向居中
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER); // 纵向居中

		style.setBorderTop(CellStyle.BORDER_THIN); // 上细线
		style.setBorderBottom(CellStyle.BORDER_THIN); // 下细线
		style.setBorderLeft(CellStyle.BORDER_THIN); // 左细线
		style.setBorderRight(CellStyle.BORDER_THIN); // 右细线

		return style;
		
	}

	// 大标题的样式
	public CellStyle bigTitle(Workbook wb) {
		
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 16);
		font.setBoldweight(Font.BOLDWEIGHT_BOLD); // 字体加粗

		style.setFont(font);

		style.setAlignment(CellStyle.ALIGN_CENTER); // 横向居中
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER); // 纵向居中

		return style;
		
	}
	
	
	
	// 文字样式
	public CellStyle text(Workbook wb) {

		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setFontName("Times New Roman");
		font.setFontHeightInPoints((short) 10);

		style.setFont(font);

		style.setAlignment(CellStyle.ALIGN_LEFT); // 横向居左
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER); // 纵向居中

		style.setBorderTop(CellStyle.BORDER_THIN); // 上细线
		style.setBorderBottom(CellStyle.BORDER_THIN); // 下细线
		style.setBorderLeft(CellStyle.BORDER_THIN); // 左细线
		style.setBorderRight(CellStyle.BORDER_THIN); // 右细线

		return style;

	}

	public void download(ByteArrayOutputStream byteArrayOutputStream,
			HttpServletResponse response, String returnName) throws IOException {

		response.setContentType("application/octet-stream;charset=utf-8");
		returnName = response.encodeURL(new String(returnName.getBytes(),
				"iso8859-1")); // 保存的文件名, 必须和页面编码一致, 否则乱码
		response.addHeader("Content-Disposition", "attachment;filename="
				+ returnName);
		response.setContentLength(byteArrayOutputStream.size());

		ServletOutputStream outputstream = response.getOutputStream(); // 取得输出流
		byteArrayOutputStream.writeTo(outputstream); // 写到输出流
		byteArrayOutputStream.close(); // 关闭
		outputstream.flush(); // 刷数据

	}


	/**
	 * 查询奖金列表
	 * 
	 * @param
	 *
	 * @param pageable
	 *            分页
	 * @param model
	 * @return
	 * @Resource(name = "orderSettlementServiceImpl") private
	 *                OrderSettlementService orderSettlementService;
	 * 
	 *                /** 分页展示提现信息
	 */
	@RequestMapping("/list")
	public String list(String tenantId, String status, Pageable pageable,
			Model model) {
		System.out.print("----------------------------");
		Charge charge = new Charge();
		if (status == null || "".equals(status) || "receiving".equals(status)) {
			charge.setStatus(Status.receiving);
		} else if ("notReceive".equals(status)) {
			charge.setStatus(Status.notReceive);
		} else if ("received".equals(status)) {
			charge.setStatus(Status.received);
		} else if ("returned".equals(status)) {
			charge.setStatus(Status.returned);
		} else if ("freezed".equals(status)) {
			charge.setStatus(Status.freezed);
		} else if ("1".equals(status)) {
			charge.setStatus(null);
		}
		Tenant tenant = adminService.getCurrent().getTenant();
		charge.setTenant(tenant);
		charge.setType(Charge.Type.bonus);
		model.addAttribute("page",
				this.chargeService.findPage(charge, pageable));
		return "admin/charge/list";
	}

	/**
	 * 分页展示提现信息
	 */
	@RequestMapping(value = "charge_list")
	public String chargeList(String status, Pageable pageable, Model model,
			RedirectAttributes redirectAttributes) {

		Charge charge = new Charge();
		if (status == null || "".equals(status) || "receiving".equals(status)) {
			charge.setStatus(Status.receiving);
		} else if ("notReceive".equals(status)) {
			charge.setStatus(Status.notReceive);
		} else if ("received".equals(status)) {
			charge.setStatus(Status.received);
		} else if ("returned".equals(status)) {
			charge.setStatus(Status.returned);
		} else if ("freezed".equals(status)) {
			charge.setStatus(Status.freezed);
		} else if ("1".equals(status)) {
			charge.setStatus(null);
		}

		Tenant tenant = adminService.getCurrent().getTenant();
		charge.setTenant(tenant);
		charge.setType(Charge.Type.charge);
		model.addAttribute("page",
				this.chargeService.findPage(charge, pageable));
		return "admin/charge/chargeList";

	}

	/**
	 * 提现查看详情
	 */
	@RequestMapping(value = "charge_detail", method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String chargeDetail(String id, Pageable pageable, Model model) {

		model.addAttribute("page", this.orderSettlementService.findByChargeId(
				Long.parseLong(id), pageable));
		return "/admin/charge/chargeDetail";

	}

	/**
	 * 提现确认, 审批通过
	 */
	@RequestMapping(value = "charge_ok")
	public String chargeOk(Long[] ids, ModelMap model,
			RedirectAttributes redirectAttributes) {

		try {
			Admin admin = this.adminService.getCurrent();
			Tenant tenant = admin.getTenant();
			// Set<MemberBank> memberBankSet = tenant.getMemberBanks();
			// MemberBank memberBank = new MemberBank();
			// if (memberBankSet != null && memberBankSet.iterator() != null
			// && memberBankSet.iterator().hasNext()) {
			// memberBank = memberBankSet.iterator().next();
			// }
			MemberBank memberBank = this.memberBankService
					.getMemberBankByTenantId(tenant.getId(),
							MemberBank.Type.credit);// 获取付款支付宝账号
			String detail_data = "";
			BigDecimal fee = new BigDecimal(0);
			List<Charge> list = chargeService.findList(ids);
			for (int i = 0; i < list.size(); i++) {
				Charge charge = list.get(i);
				if (!charge.getStatus().equals(Charge.Status.receiving)) {
					addFlashMessage(redirectAttributes,
							Message.error("存在已发放的记录", new Object[0]));
					return "redirect:charge_list.jhtml";
				}
				Member member = charge.getMember();
				MemberBank bank = new MemberBank();
				Set<MemberBank> set = member.getMemberBanks();
				if (set != null && set.iterator() != null
						&& set.iterator().hasNext()) {
					bank = set.iterator().next();
				}
				// 格式：流水号1^收款方帐号1^真实姓名^付款金额1^备注说明1|流水号2^收款方帐号2^真实姓名^付款金额2^备注说明2....
				if (i == 0) {// 第一条记录前面不需要“|”
					detail_data = detail_data + charge.getTxNo() + "^"
							+ bank.getCardNo() + "^" + bank.getDepositUser()
							+ "^" + charge.getCharge() + "^" + "分享佣金提现";
				} else {
					detail_data = detail_data + "|" + charge.getTxNo() + "^"
							+ bank.getCardNo() + "^" + bank.getDepositUser()
							+ "^" + charge.getCharge() + "^" + "分享佣金提现";
				}
				fee = fee.add(charge.getCharge());

			}
			// 服务器异步通知页面路径
			PaymentPlugin paymentPlugin = pluginService
					.getPaymentPlugin("alipayMobilePlugin");
			String refundNotifyUrl = paymentPlugin
					.getAttribute("cashNotifyUrl");
			// 需http://格式的完整路径，不允许加?id=123这类自定义参数
			// 付款账号
			String email = memberBank.getCardNo();
			// 必填
			// 付款账户名
			String account_name = memberBank.getDepositUser();
			// 必填，个人支付宝账号是真实姓名公司支付宝账号是公司名称
			// 付款当天日期
			String pay_date = new SimpleDateFormat("yyyyMMdd")
					.format(new Date());
			// 必填，格式：年[4位]月[2位]日[2位]，如：20100801
			// 批次号
			String batch_no = new SimpleDateFormat("yyyyMMddhhmmss")
					.format(new Date());
			// 必填，格式：当天日期[8位]+序列号[3至16位]，如：201008010000001
			// 付款总金额
			String batch_fee = fee.toString();
			// 必填，即参数detail_data的值中所有金额的总和

			// 付款笔数
			String batch_num = String.valueOf(ids.length);//
			// 必填，即参数detail_data的值中，“|”字符出现的数量加1，最大支持1000笔（即“|”字符出现的数量999个）

			// 付款详细数据
			// String detail_data = new
			// String(request.getParameter("WIDdetail_data").getBytes("ISO-8859-1"),"UTF-8");
			// 必填，格式：流水号1^收款方帐号1^真实姓名^付款金额1^备注说明1|流水号2^收款方帐号2^真实姓名^付款金额2^备注说明2....
			String partner = memberBank.getBankProvince();
			// 把请求参数打包成数组
			Map<String, String> sParaTemp = new HashMap<String, String>();

			sParaTemp.put("service",
					new String("batch_trans_notify".getBytes("ISO-8859-1"),
							"UTF-8"));
			sParaTemp.put("partner", new String(partner.getBytes("ISO-8859-1"),
					"UTF-8"));
			sParaTemp
					.put("_input_charset", new String(
							AlipayConfig.input_charset.getBytes("ISO-8859-1"),
							"UTF-8"));
			sParaTemp
					.put("notify_url",
							new String(refundNotifyUrl.getBytes("ISO-8859-1"),
									"UTF-8"));
			sParaTemp.put("email", new String(email.getBytes("ISO-8859-1"),
					"UTF-8"));
			// sParaTemp.put("account_name",
			// new String(account_name.getBytes("ISO-8859-1"), "UTF-8"));
			sParaTemp.put("account_name", account_name);
			sParaTemp.put("pay_date",
					new String(pay_date.getBytes("ISO-8859-1"), "UTF-8"));
			sParaTemp.put("batch_no",
					new String(batch_no.getBytes("ISO-8859-1"), "UTF-8"));
			sParaTemp.put("batch_fee",
					new String(batch_fee.getBytes("ISO-8859-1"), "UTF-8"));
			sParaTemp.put("batch_num",
					new String(batch_num.getBytes("ISO-8859-1"), "UTF-8"));
			// sParaTemp.put("detail_data",
			// new String(detail_data.getBytes("ISO-8859-1"), "UTF-8"));
			sParaTemp.put("detail_data", detail_data);

			String sHtmlText = AlipayTrans.buildRequest(sParaTemp, "get", "确认",
					memberBank.getDepositBank());

			model.addAttribute("sHtmlText", sHtmlText);
			addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
			return "/admin/refund_confirm/payment";
		} catch (Exception e) {
			addFlashMessage(redirectAttributes, ERROR_MESSAGE);
			return "redirect:charge_list.jhtml";
		}

	}

	/**
	 * 奖金提现确认, 审批通过
	 */
	@RequestMapping(value = "/Confirm")
	public String Confirm(Long[] ids, ModelMap model,
			RedirectAttributes redirectAttributes) {
		try {

			Admin admin = this.adminService.getCurrent();
			Tenant tenant = admin.getTenant();
			// Set<MemberBank> memberBankSet = tenant.getMemberBanks();
			// MemberBank memberBank = new MemberBank();
			// if (memberBankSet != null && memberBankSet.iterator() != null
			// && memberBankSet.iterator().hasNext()) {
			// memberBank = memberBankSet.iterator().next();
			// }
			MemberBank memberBank = this.memberBankService
					.getMemberBankByTenantId(tenant.getId(),
							MemberBank.Type.credit);// 获取付款支付宝账号

			String detail_data = "";
			BigDecimal fee = new BigDecimal(0);
			List<Charge> list = chargeService.findList(ids);
			for (int i = 0; i < list.size(); i++) {
				Charge charge = list.get(i);
				if (!charge.getStatus().equals(Charge.Status.receiving)) {
					addFlashMessage(redirectAttributes,
							Message.error("存在已发放的记录", new Object[0]));
					return "redirect:list.jhtml";
				}
				Member member = charge.getMember();
				MemberBank bank = new MemberBank();
				Set<MemberBank> set = member.getMemberBanks();
				if (set != null && set.iterator() != null
						&& set.iterator().hasNext()) {
					bank = set.iterator().next();
				}
				// 格式：流水号1^收款方帐号1^真实姓名^付款金额1^备注说明1|流水号2^收款方帐号2^真实姓名^付款金额2^备注说明2....
				if (i == 0) {// 第一条记录前面不需要“|”
					detail_data = detail_data + charge.getTxNo() + "^"
							+ bank.getCardNo() + "^" + bank.getDepositUser()
							+ "^" + charge.getCharge() + "^" + "分享佣金提现";
				} else {
					detail_data = detail_data + "|" + charge.getTxNo() + "^"
							+ bank.getCardNo() + "^" + bank.getDepositUser()
							+ "^" + charge.getCharge() + "^" + "分享佣金提现";
				}
				fee = fee.add(charge.getCharge());

			}
			// 服务器异步通知页面路径
			PaymentPlugin paymentPlugin = pluginService
					.getPaymentPlugin("alipayMobilePlugin");
			String refundNotifyUrl = paymentPlugin
					.getAttribute("cashNotifyUrl");
			// 需http://格式的完整路径，不允许加?id=123这类自定义参数
			// 付款账号
			String email = memberBank.getCardNo();
			// 必填
			// 付款账户名
			String account_name = memberBank.getDepositUser();
			// 必填，个人支付宝账号是真实姓名公司支付宝账号是公司名称
			// 付款当天日期
			String pay_date = new SimpleDateFormat("yyyyMMdd")
					.format(new Date());
			// 必填，格式：年[4位]月[2位]日[2位]，如：20100801
			// 批次号
			String batch_no = new SimpleDateFormat("yyyyMMddhhmmss")
					.format(new Date());
			// 必填，格式：当天日期[8位]+序列号[3至16位]，如：201008010000001
			// 付款总金额
			String batch_fee = fee.toString();
			// 必填，即参数detail_data的值中所有金额的总和

			// 付款笔数
			String batch_num = String.valueOf(ids.length);//
			// 必填，即参数detail_data的值中，“|”字符出现的数量加1，最大支持1000笔（即“|”字符出现的数量999个）

			// 付款详细数据
			// String detail_data = new
			// String(request.getParameter("WIDdetail_data").getBytes("ISO-8859-1"),"UTF-8");
			// 必填，格式：流水号1^收款方帐号1^真实姓名^付款金额1^备注说明1|流水号2^收款方帐号2^真实姓名^付款金额2^备注说明2....
			String partner = memberBank.getBankProvince();
			// 把请求参数打包成数组
			Map<String, String> sParaTemp = new HashMap<String, String>();

			sParaTemp.put("service",
					new String("batch_trans_notify".getBytes("ISO-8859-1"),
							"UTF-8"));
			sParaTemp.put("partner", new String(partner.getBytes("ISO-8859-1"),
					"UTF-8"));
			sParaTemp
					.put("_input_charset", new String(
							AlipayConfig.input_charset.getBytes("ISO-8859-1"),
							"UTF-8"));
			sParaTemp
					.put("notify_url",
							new String(refundNotifyUrl.getBytes("ISO-8859-1"),
									"UTF-8"));
			sParaTemp.put("email", new String(email.getBytes("ISO-8859-1"),
					"UTF-8"));
			// sParaTemp.put("account_name",
			// new String(account_name.getBytes("ISO-8859-1"), "UTF-8"));
			sParaTemp.put("account_name", account_name);
			sParaTemp.put("pay_date",
					new String(pay_date.getBytes("ISO-8859-1"), "UTF-8"));
			sParaTemp.put("batch_no",
					new String(batch_no.getBytes("ISO-8859-1"), "UTF-8"));
			sParaTemp.put("batch_fee",
					new String(batch_fee.getBytes("ISO-8859-1"), "UTF-8"));
			sParaTemp.put("batch_num",
					new String(batch_num.getBytes("ISO-8859-1"), "UTF-8"));
			// sParaTemp.put("detail_data",
			// new String(detail_data.getBytes("ISO-8859-1"), "UTF-8"));
			sParaTemp.put("detail_data", detail_data);

			String sHtmlText = AlipayTrans.buildRequest(sParaTemp, "get", "确认",
					memberBank.getDepositBank());

			model.addAttribute("sHtmlText", sHtmlText);
			addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
			return "/admin/refund_confirm/payment";
		} catch (Exception e) {
			addFlashMessage(redirectAttributes, ERROR_MESSAGE);
			return "redirect:list.jhtml";
		}

	}
		
}
