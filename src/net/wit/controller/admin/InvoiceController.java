package net.wit.controller.admin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.wit.Pageable;
import net.wit.entity.InvoiceContent;
import net.wit.entity.InvoiceManagement;
import net.wit.entity.Order;
import net.wit.entity.InvoiceManagement.InvoiceStat;
import net.wit.entity.Tenant;
import net.wit.service.AdminService;
import net.wit.service.InvoiceContentService;
import net.wit.service.InvoiceManagementService;
import net.wit.util.DateUtil;

@Controller("adminInvoiceController")
@RequestMapping({ "admin/invoice" })
public class InvoiceController extends BaseController {

	@Resource(name = "invoiceContentServiceImpl")
	private InvoiceContentService invoiceContentService;

	@Resource(name = "invoiceManagementServiceImpl")
	private InvoiceManagementService invoiceManagementService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	/**
	 * 查询发票内容列表
	 */
	@RequestMapping(value = { "/invoice_content" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String content(Pageable pageable, Model model) {

		Tenant tenant = adminService.getCurrent().getTenant();
		String searchValue = null;
		try {
			//searchValue = new String(request.getParameter("searchValue").getBytes("ISO-8859-1"));
			searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
			pageable.setSearchValue(searchValue);
		} catch (Exception localException) {
		}

		// if (null == tenant) {
		// tenant = new Tenant();
		// tenant.setId(14l);
		//
		// }

		model.addAttribute("page", this.invoiceContentService.findInvoiceContentsPage(tenant, pageable));
		return "/admin/invoice/invoice_content";

	}

	/**
	 * 去发票内容增加页面
	 */
	@RequestMapping(value = "/invoice_toAdd")
	public String toAdd() {

		return "/admin/invoice/invoice_add";

	}

	/**
	 * 发票内容增加
	 */
	@RequestMapping(value = "/invoice_add", method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	// @ResponseBody
	public String add(String content) {

		Tenant tenant = adminService.getCurrent().getTenant();

		// if (null == tenant) {
		// tenant = new Tenant();
		// tenant.setId(14l);
		//
		// }

		InvoiceContent invoiceContent = new InvoiceContent();
		invoiceContent.setTenant(tenant);
		invoiceContent.setContent(content);
		invoiceContent.setCreateDate(new Date());
		invoiceContentService.save(invoiceContent);

		return "redirect:/admin/invoice/invoice_content.jhtml";

	}

	/**
	 * 发票内容删除
	 */
	@RequestMapping(value = "/invoice_delete")
	public String delete(Pageable pageable, String ids) {

		invoiceContentService.delete(Long.parseLong(ids));
		return "redirect:/admin/invoice/invoice_content.jhtml";

	}

	/**
	 * 发票管理
	 */
	@RequestMapping(value = { "/invoice_management_find" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String mangementFind(Pageable pageable,InvoiceManagement.InvoiceStat invoiceStat,  Model model, RedirectAttributes redirectAttributes) {

		Tenant tenant = adminService.getCurrent().getTenant();
		model.addAttribute("page", this.invoiceManagementService.findInvoiceManagementsPage(tenant, pageable));
		return "/admin/invoice/invoice_management";

	}

	/**
	 * 点击确定开票按钮将发票状态由未开票改为已开票
	 */
	@RequestMapping(value = { "/invoice_management_updateStat" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String managementUpdateStat(Long[] ids, RedirectAttributes redirectAttributes) {
		/***
		 * 修改过
		 */
		if (null != ids && ids.length > 0) {

			for (int i = 0; i < ids.length; i++) {

				InvoiceManagement invoiceManagement = invoiceManagementService.find(ids[i]);
				invoiceManagement.setInvoiceStat(InvoiceStat.yes);
				invoiceManagementService.update(invoiceManagement);

			}

		}
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:/admin/invoice/invoice_management_find.jhtml";

	}
	
	/**
	 * 点击取消开票按钮将发票状态改为未开票
	 */
	@RequestMapping(value = { "/invoice_management_editStat" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String managementEditStat(Long[] ids, RedirectAttributes redirectAttributes) {
		/***
		 * 修改过
		 */
		if (null != ids && ids.length > 0) {

			for (int i = 0; i < ids.length; i++) {

				InvoiceManagement invoiceManagement = invoiceManagementService.find(ids[i]);
				invoiceManagement.setInvoiceStat(InvoiceStat.cancel);
				invoiceManagementService.update(invoiceManagement);

			}

		}
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:/admin/invoice/invoice_management_find.jhtml";

	}

	/**
	 * 导出未开票发票Excel表格
	 */
	@RequestMapping(value = { "/invoice_management_exportExcel" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public void managementExportExcel(HttpServletResponse response, String tenantId) {

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

		nCell.setCellValue("未开票发票" + "（导出日期：" + String.valueOf(year) + "年" + String.valueOf(month) + "月" + String.valueOf(day) + "日" + String.valueOf(hour) + ":" + String.valueOf(minute) + ":" + String.valueOf(second) + "）");

		nCell.setCellStyle(this.bigTitle(wb)); // 设置样式
		// 设置 第一行的第二列要合并单元格
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 7));

		// 打印小标题
		String titles[] = { "序列号", "订单编号", "开票企业", "发票内容", "发票金额", "发票抬头", "发票状态" };

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
		Tenant tenant = adminService.getCurrent().getTenant();
		List<InvoiceManagement> invoiceManagementList = invoiceManagementService.findInvoiceManagementList(tenant);

		int i = 1; // 预定义一个序列号, 作为Excel表格中数据的序列号

		for (InvoiceManagement invoiceManage : invoiceManagementList) {

			if (invoiceManage.getInvoiceStat().equals(InvoiceStat.no)) {

				cellNo = 1;
				nRow = sheet.createRow(rowNo++);
				nRow.setHeightInPoints(24);// 设置行高

				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(String.valueOf(i++)); // 每条数据的序列号
				nCell.setCellStyle(this.text(wb)); // 设置 样式

				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(invoiceManage.getOrder().getSn()); // 订单编号
				nCell.setCellStyle(this.text(wb)); // 设置 样式

				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(invoiceManage.getTenant().getName()); // 企业名称
				nCell.setCellStyle(this.text(wb)); // 设置 样式

				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(invoiceManage.getInvoiceContent().getContent()); // 发票内容
				nCell.setCellStyle(this.text(wb)); // 设置 样式

				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(String.valueOf(invoiceManage.getInvoiceValue()).substring(0, 6)); // 发票金额
				nCell.setCellStyle(this.text(wb)); // 设置 样式

				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue(invoiceManage.getInvoiceTitle()); // 发票抬头
				nCell.setCellStyle(this.text(wb)); // 设置 样式

				nCell = nRow.createCell(cellNo++); // 产生第一个单元格
				nCell.setCellValue("未开票"); // 发票状态
				nCell.setCellStyle(this.text(wb)); // 设置 样式

			}

		}

		try {

			// 导出Excel
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			wb.write(baos);
			baos.flush();
			baos.close();
			this.download(baos, response, "未开票发票.xls");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void download(ByteArrayOutputStream byteArrayOutputStream, HttpServletResponse response, String returnName) throws IOException {

		response.setContentType("application/octet-stream;charset=utf-8");
		returnName = response.encodeURL(new String(returnName.getBytes(), "iso8859-1")); // 保存的文件名, 必须和页面编码一致, 否则乱码
		response.addHeader("Content-Disposition", "attachment;filename=" + returnName);
		response.setContentLength(byteArrayOutputStream.size());

		ServletOutputStream outputstream = response.getOutputStream(); // 取得输出流
		byteArrayOutputStream.writeTo(outputstream); // 写到输出流
		byteArrayOutputStream.close(); // 关闭
		outputstream.flush(); // 刷数据

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
	
	//组合条件查询订单信息
	@RequestMapping(value = { "/invoice_management_queryByFilterCriteria" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String queryByFilterCriteria(String createDateParam, String orderStatusParam, 
										String invoiceStatusParam, Pageable pageable, ModelMap model) {
		
		String searchValue = null;
		Tenant tenantSearch = null;
		
		try {
			searchValue = new String(pageable.getSearchValue().getBytes(
					"ISO-8859-1"), "UTF-8");
			pageable.setSearchValue(searchValue);
			if (pageable.getSearchProperty().equals("tenant")) {
				pageable.setSearchProperty(null);
				tenantSearch = new Tenant();
				tenantSearch.setShortName(pageable.getSearchValue());
			}

		} catch (Exception localException) {}
		
		Tenant tenant = adminService.getCurrent().getTenant();
		model.addAttribute("createDateParam", createDateParam);
		model.addAttribute("orderStatusParam", orderStatusParam);
		model.addAttribute("invoiceStatusParam", invoiceStatusParam);
		
		try {
			model.addAttribute("tenant",tenant);
			if(tenantSearch==null){
				List<Order.OrderStatus> orderStatuses = null;
				List<Order.PaymentStatus> paymentStatuses = null;
				List<Order.ShippingStatus> shippingStatuses = null;
				if (orderStatusParam != null && !"".equals(orderStatusParam)) {
					orderStatuses = new ArrayList<Order.OrderStatus>();
					paymentStatuses = new ArrayList<Order.PaymentStatus>();
					shippingStatuses = new ArrayList<Order.ShippingStatus>();
				}
				List<InvoiceManagement.InvoiceStat> invoiceStat = null;
				if (invoiceStatusParam != null && !"".equals(invoiceStatusParam)) {
					invoiceStat = new ArrayList<InvoiceManagement.InvoiceStat>();
				}
				this.convertStatus(orderStatusParam, orderStatuses, paymentStatuses, shippingStatuses);
				this.convertStat(invoiceStatusParam, invoiceStat);
				
				Date firstDayOfMonth = null;
				Date lastDayOfMonth = null;
				if (null != createDateParam) {
					DateFormat df = new SimpleDateFormat("yyyy-MM");
					Date createTime = df.parse(createDateParam);
					firstDayOfMonth = DateUtil.getFirstDayOfMonth(createTime);
					lastDayOfMonth = DateUtil.getLastDayOfMonth(createTime);
				}
				
				//重新定义方法
				model.addAttribute("page", invoiceManagementService.findPageByCriteria(tenant, orderStatuses, 
						paymentStatuses, shippingStatuses, invoiceStat, firstDayOfMonth, lastDayOfMonth, pageable));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/admin/invoice/invoice_management";
		
	}
	
	/**
	 * 订单状态转换
	 */
	private void convertStatus(String orderStatusParam,
			List<Order.OrderStatus> orderStatuses,
			List<Order.PaymentStatus> paymentStatuses,
			List<Order.ShippingStatus> shippingStatuses) {
		if (orderStatusParam != null && !"".equals(orderStatusParam)) {
			if ("unpaid".equals(orderStatusParam)) {// 待付款
				orderStatuses.add(Order.OrderStatus.confirmed);
				paymentStatuses.add(Order.PaymentStatus.unpaid);
				shippingStatuses.add(Order.ShippingStatus.unshipped);
			} else if ("unshipped".equals(orderStatusParam)) {// 待发货
				orderStatuses.add(Order.OrderStatus.confirmed);
				paymentStatuses.add(Order.PaymentStatus.paid);
				shippingStatuses.add(Order.ShippingStatus.unshipped);
			} else if ("shipped".equals(orderStatusParam)) {// 已发货
				orderStatuses.add(Order.OrderStatus.confirmed);
				paymentStatuses.add(Order.PaymentStatus.paid);
				shippingStatuses.add(Order.ShippingStatus.shipped);
			} else if ("completed".equals(orderStatusParam)) {// 交易成功
				orderStatuses.add(Order.OrderStatus.completed);
				paymentStatuses.add(Order.PaymentStatus.paid);
				shippingStatuses.add(Order.ShippingStatus.accept);
			} else if ("apply".equals(orderStatusParam)) {// 退货中
				orderStatuses.add(Order.OrderStatus.confirmed);
				paymentStatuses.add(Order.PaymentStatus.paid);
				shippingStatuses.add(Order.ShippingStatus.apply);
			} else if ("refundapply".equals(orderStatusParam)) {// 退款中
				orderStatuses.add(Order.OrderStatus.confirmed);
				paymentStatuses.add(Order.PaymentStatus.refundapply);
				shippingStatuses.add(Order.ShippingStatus.unshipped);
			} else if ("refunded".equals(orderStatusParam)) {// 已退款
				orderStatuses.add(Order.OrderStatus.confirmed);
				orderStatuses.add(Order.OrderStatus.completed);
				paymentStatuses.add(Order.PaymentStatus.refunded);
				
				shippingStatuses.add(Order.ShippingStatus.unshipped);
				shippingStatuses.add(Order.ShippingStatus.returned);
			} else if ("cancelled".equals(orderStatusParam)) {// 交易关闭
				orderStatuses.add(Order.OrderStatus.cancelled);
				paymentStatuses.add(Order.PaymentStatus.unpaid);
				shippingStatuses.add(Order.ShippingStatus.unshipped);
			}else if("accept".equals(orderStatusParam)){
				orderStatuses.add(Order.OrderStatus.confirmed);
				paymentStatuses.add(Order.PaymentStatus.paid);
				shippingStatuses.add(Order.ShippingStatus.accept);
			}
		}
	}
	
	/**
	 * 发票状态转换
	 */
	private void convertStat(String invoiceStatusParam,
			                   List<InvoiceManagement.InvoiceStat> invoiceStat) {
		
		if (null != invoiceStatusParam && !"".equals(invoiceStatusParam)) {
			if ("no".equals(invoiceStatusParam)) {
				invoiceStat.add(InvoiceManagement.InvoiceStat.no);
			}
			if ("yes".equals(invoiceStatusParam)) {
				invoiceStat.add(InvoiceManagement.InvoiceStat.yes);
			}
			if ("cancel".equals(invoiceStatusParam)) {
				invoiceStat.add(InvoiceManagement.InvoiceStat.cancel);
			}
		}
		
	}
	

}
