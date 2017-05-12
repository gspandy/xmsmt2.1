package net.wit.controller.admin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Admin;
import net.wit.entity.Member;
import net.wit.entity.Tenant;
import net.wit.entity.TenantShopkeeper;
import net.wit.entity.Ticket;
import net.wit.service.AdminService;
import net.wit.service.MemberAttributeService;
import net.wit.service.MemberRankService;
import net.wit.service.MemberService;
import net.wit.service.TenantCategoryService;
import net.wit.service.TenantShopkeeperService;
import net.wit.service.TicketCacheService;
import net.wit.service.TicketService;
import net.wit.service.TicketSetService;
import net.wit.util.DateUtil;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("tickeController")
@RequestMapping({ "/admin/ticket" })
public class TicketController extends BaseController {

	@Resource(name = "tenantCategoryServiceImpl")
	private TenantCategoryService tenantCategoryService;

	@Resource(name = "memberRankServiceImpl")
	private MemberRankService memberRankService;

	@Resource(name = "memberAttributeServiceImpl")
	private MemberAttributeService memberAttributeService;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	@Resource(name = "ticketSetServiceImpl")
	private TicketSetService ticketSetService;

	@Resource(name = "ticketServiceImpl")
	private TicketService ticketService;

	@Resource(name = "ticketCacheServiceImpl")
	private TicketCacheService ticketCacheService;
	//
	@Resource(name = "tenantShopkeeperServiceImpl")
	private TenantShopkeeperService tenantShopkeeperService;

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(String searchValue, Pageable pageable, ModelMap model,
			RedirectAttributes redirectAttributes) {
		Admin admin = adminService.getCurrent();
		List<Long> members = new ArrayList<Long>();
		List<TenantShopkeeper> list = tenantShopkeeperService
				.findShopKeeperByTenantId(admin.getTenant().getId());
		for (TenantShopkeeper shop : list) {

			members.add(shop.getMember().getId());

		}
		Page<Member> member = this.memberService.findPage(members, searchValue,
				pageable);
		model.addAttribute("tenantCategoryTree",
				this.tenantCategoryService.findTree());
		model.addAttribute("memberRanks", this.memberRankService.findAll());
		model.addAttribute("memberAttributes",
				this.memberAttributeService.findAll());
		model.addAttribute("page", member);
		if (member.getTotal() == 0) {
			addFlashMessage(redirectAttributes, ERROR_MESSAGE);
		}
		return "/admin/ticket/add";
	}

	@RequestMapping(value = { "/query" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String query(String searchValue, Pageable pageable, ModelMap model,
			RedirectAttributes redirectAttributes) {
		try {
			searchValue = new String(searchValue.getBytes("ISO-8859-1"),
					"UTF-8");
		} catch (Exception localException) {
		}
		Admin admin = adminService.getCurrent();
		List<Long> members = new ArrayList<Long>();
		List<TenantShopkeeper> list = tenantShopkeeperService
				.findShopKeeperByTenantId(admin.getTenant().getId());
		for (TenantShopkeeper shop : list) {
			members.add(shop.getMember().getId());
		}
		Page<Member> member = this.memberService.findPage(members, searchValue,
				pageable);
		model.addAttribute("tenantCategoryTree",
				this.tenantCategoryService.findTree());
		model.addAttribute("memberRanks", this.memberRankService.findAll());
		model.addAttribute("memberAttributes",
				this.memberAttributeService.findAll());
		model.addAttribute("page", member);
		return "/admin/ticket/add";
	}

	/**
	 * 分页查询内购券明细
	 * 
	 * @param shopkeeperMobile
	 * @param ticketStatusParam
	 * @param ticketModifyDate
	 * @param pageable
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(String shopkeeperMobile, String ticketStatusParam,
			String ticketModifyDate, Pageable pageable, ModelMap model) {
		Member member = null;
		if (shopkeeperMobile != null && !"".equals(shopkeeperMobile)) {
			member = memberService.findByTel(shopkeeperMobile);
			model.addAttribute("shopkeeperMobile", shopkeeperMobile);
		}
		Date lastDayOfMonth = null;
		Date firstDayOfMonth = null;
		if (ticketModifyDate != null && !"".equals(ticketModifyDate)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			Date date;
			try {
				date = sdf.parse(ticketModifyDate);
				lastDayOfMonth = DateUtil.getLastDayOfMonth(date);
				firstDayOfMonth = DateUtil.getFirstDayOfMonth(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}
		Admin admin = adminService.getCurrent();
		Long tenantId = null;
		if (admin != null && !"".equals(admin)) {
			Tenant tenant = admin.getTenant();
			if (tenant != null && !"".equals(tenant)) {
				tenantId = tenant.getId();
			}
		}
		Page<Ticket> page = ticketService.findPage(tenantId, member,
				ticketStatusParam, firstDayOfMonth, lastDayOfMonth, pageable);
		model.addAttribute("page", page);
		model.addAttribute("ticketStatusParam", ticketStatusParam);
		model.addAttribute("ticketModifyDate", ticketModifyDate);
		return "/admin/ticket_get/list";
	}

	@RequestMapping(value = { "/sendAll" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String sendAll(String num, RedirectAttributes redirectAttributes) {
		Admin admin = adminService.getCurrent();
		Long tenantId = admin.getTenant().getId();
		List<TenantShopkeeper> list = tenantShopkeeperService
				.findShopKeeperByTenantId(admin.getTenant().getId());
		List<Long> memberIdList = new ArrayList<Long>();
		for (TenantShopkeeper e : list) {
			memberIdList.add(e.getMember().getId());
		}
		ticketCacheService.batchSaveTicketCacheByMemberListOnManual(
				memberIdList, tenantId, Integer.parseInt(num));
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:add.jhtml";
	}

	@RequestMapping(value = { "/send" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String send(String num, String day, String sendAll, Long[] ids,
			RedirectAttributes redirectAttributes) {
		if (ids == null) {
			addFlashMessage(redirectAttributes, NOT_SELECTED);
			return "redirect:add.jhtml";
		}
		Admin admin = adminService.getCurrent();
		Long tenantId = admin.getTenant().getId();
		List<Long> memberIdList = new ArrayList<Long>();
		for (long e : ids) {
			memberIdList.add(e);
		}
		ticketCacheService.batchSaveTicketCacheByMemberListOnManual(
				memberIdList, tenantId, Integer.parseInt(num));
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:add.jhtml";
	}

	/**
	 * 导出Excel
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/exportExcel")
	public void exportExcel(HttpServletResponse response,
			String shopkeeperMobile, String ticketStatusParam,
			String ticketModifyDate, ModelMap model) {

		try {
			// 新建工作薄
			Workbook wb = new HSSFWorkbook();
			CellStyle titleStyle = wb.createCellStyle();
			Font font1 = wb.createFont();
			font1.setFontName("黑体");
			font1.setFontHeightInPoints((short) 12);

			titleStyle.setFont(font1);

			titleStyle.setAlignment(CellStyle.ALIGN_CENTER); // 横向居中
			titleStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER); // 纵向居中

			titleStyle.setBorderTop(CellStyle.BORDER_THIN); // 上细线
			titleStyle.setBorderBottom(CellStyle.BORDER_THIN); // 下细线
			titleStyle.setBorderLeft(CellStyle.BORDER_THIN); // 左细线
			titleStyle.setBorderRight(CellStyle.BORDER_THIN); // 右细线

			CellStyle bigTitle = wb.createCellStyle();
			Font font2 = wb.createFont();
			font2.setFontName("宋体");
			font2.setFontHeightInPoints((short) (16));
			// 加粗
			font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
			bigTitle.setFont(font2);
			// 横向居中
			bigTitle.setAlignment(CellStyle.ALIGN_CENTER);
			// 纵向居中
			bigTitle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

			CellStyle textStyle = wb.createCellStyle();
			Font font3 = wb.createFont();
			font3.setFontName("Times New Roman");
			font3.setFontHeightInPoints((short) 10);

			textStyle.setFont(font3);

			textStyle.setBorderTop(CellStyle.BORDER_THIN); // 上细线
			textStyle.setBorderBottom(CellStyle.BORDER_THIN); // 下细线
			textStyle.setBorderLeft(CellStyle.BORDER_THIN); // 左细线
			textStyle.setBorderRight(CellStyle.BORDER_THIN); // 右细线
			// 横向居中
			textStyle.setAlignment(CellStyle.ALIGN_CENTER);
			// 纵向居中
			textStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			Member member = null;
			if (shopkeeperMobile != null && !"".equals(shopkeeperMobile)) {
				member = memberService.findByTel(shopkeeperMobile);
				model.addAttribute("shopkeeperMobile", shopkeeperMobile);
			}
			Date lastDayOfMonth = null;
			Date firstDayOfMonth = null;
			if (ticketModifyDate != null && !"".equals(ticketModifyDate)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
				Date date;
				try {
					date = sdf.parse(ticketModifyDate);
					lastDayOfMonth = DateUtil.getLastDayOfMonth(date);
					firstDayOfMonth = DateUtil.getFirstDayOfMonth(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
			Admin admin = adminService.getCurrent();
			Long tenantId = null;
			if (admin != null && !"".equals(admin)) {
				Tenant tenant = admin.getTenant();
				if (tenant != null && !"".equals(tenant)) {
					tenantId = tenant.getId();
				}
			}

			List<Ticket> ticketList = ticketService.findList(tenantId, member,
					ticketStatusParam, firstDayOfMonth, lastDayOfMonth);
				// 建立新的sheet对象
				Sheet sheet = wb.createSheet();
				Row nRow = null;
				Cell nCell = null;

				int rowNo = 0;
				int cellNo = 1;

				// 设置列的列宽 0代表列的索引
				sheet.setColumnWidth(0, 4 * 300);
				sheet.setColumnWidth(1, 10 * 300);
				sheet.setColumnWidth(2, 20 * 300);
				sheet.setColumnWidth(3, 20 * 300);
				sheet.setColumnWidth(4, 20 * 300);
				sheet.setColumnWidth(5, 20 * 300);
				sheet.setColumnWidth(6, 20 * 300);
				sheet.setColumnWidth(7, 20 * 300);

				// 打印大标题
				nRow = sheet.createRow(rowNo++);
				// 行高
				nRow.setHeightInPoints(36);
				nCell = nRow.createCell(cellNo);

				nCell.setCellValue("领券报表");
				nCell.setCellStyle(bigTitle);

				// 第一行 第二列要合并单元格
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 7));

				if (ticketList != null && ticketList.size() > 0) {
					// 打印小标题
					String titles[] = { "序号", "领券时间", "领取人手机", "领取人状态", "券券状态",
							"店长姓名", "店长手机" };

					// 第二行
					nRow = sheet.createRow(rowNo++);
					nRow.setHeightInPoints(26);

					// 循环打印小标题
					for (String title : titles) {
						nCell = nRow.createCell(cellNo++);
						nCell.setCellValue(title);
						nCell.setCellStyle(titleStyle);
					}

					// 预定义一个序列号，作为Excel表格中数据的序列号
					int i = 1;
					// 打印数据
					for (Ticket ticket : ticketList) {
						cellNo = 1;
						nRow = sheet.createRow(rowNo++);
						nRow.setHeightInPoints(24);

						// 序列号
						nCell = nRow.createCell(cellNo++);
						nCell.setCellValue(String.valueOf(i++));
						nCell.setCellStyle(textStyle);

						// 领券时间
						nCell = nRow.createCell(cellNo++);
						SimpleDateFormat df = new SimpleDateFormat(
								"yyyy-MM-dd HH:MM:ss");
						String strDate = df.format(ticket.getModifyDate());
						if (strDate != null) {
							nCell.setCellValue(strDate);
						} else {
							nCell.setCellValue("");
						}
						nCell.setCellStyle(textStyle);

						// 领取人手机
						nCell = nRow.createCell(cellNo++);
						if (ticket.getMember() != null
								&& ticket.getMember().getMobile() != null) {
							nCell.setCellValue(ticket.getMember().getMobile());
						} else {
							nCell.setCellValue("");
						}

						nCell.setCellStyle(textStyle);

						// 领取人状态
						nCell = nRow.createCell(cellNo++);
						if (ticket.getMember() != null) {
							if (ticket.getMember().getLoginDate() != null) {
								nCell.setCellValue("已注册");
							} else {
								nCell.setCellValue("未注册");
							}
						} else {
							nCell.setCellValue("");
						}
						nCell.setCellStyle(textStyle);

						// 券券状态
						nCell = nRow.createCell(cellNo++);
						if ("nouse".equals(ticket.getStatus().toString())) {
							nCell.setCellValue("未使用");
						} else if ("recevied".equals(ticket.getStatus()
								.toString())) {
							nCell.setCellValue("已领取");
						} else if ("used".equals(ticket.getStatus().toString())) {
							nCell.setCellValue("已使用");
						} else if ("expired".equals(ticket.getStatus()
								.toString())) {
							nCell.setCellValue("已失效");
						} else if (ticket.getStatus().toString() == null) {
							nCell.setCellValue("");
						}
						nCell.setCellStyle(textStyle);

						// VIP姓名
						nCell = nRow.createCell(cellNo++);
						if (ticket.getShopkeeper() != null) {
							nCell.setCellValue(ticket.getShopkeeper().getName());
						} else {
							nCell.setCellValue("");
						}
						nCell.setCellStyle(textStyle);

						// VIP手机
						nCell = nRow.createCell(cellNo++);
						if (ticket.getShopkeeper() != null) {
							nCell.setCellValue(ticket.getShopkeeper()
									.getMobile());
						} else {
							nCell.setCellValue("");
						}
						nCell.setCellStyle(textStyle);
					}
				} else {
					// 第二行
					nRow = sheet.createRow(rowNo++);
					// 行高
					nRow.setHeightInPoints(26);
					nCell = nRow.createCell(cellNo++);

					nCell.setCellValue("相关数据不存在");
					nCell.setCellStyle(titleStyle);

					// 第一行 第二列要合并单元格
					sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 7));
					
				}
				try {
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					// 导出Excel
					wb.write(byteArrayOutputStream);
					byteArrayOutputStream.flush();
					byteArrayOutputStream.close();
					download(byteArrayOutputStream, response, "领券报表.xls");
				} catch (IOException e) {
					e.printStackTrace();
				}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 下载
	public static void download(ByteArrayOutputStream byteArrayOutputStream,
			HttpServletResponse response, String returnName) throws IOException {
		response.setContentType("application/octet-stream;charset=utf-8");
		returnName = response.encodeURL(new String(returnName.getBytes(),
				"iso8859-1"));
		response.addHeader("Content-Disposition", "attachment;filename="
				+ returnName);
		response.setContentLength(byteArrayOutputStream.size());

		// 获取输出流
		ServletOutputStream outPutStream = response.getOutputStream();
		// 写到输出流
		byteArrayOutputStream.writeTo(outPutStream);
		// 关闭
		byteArrayOutputStream.close();
		// 刷新数据
		outPutStream.flush();
	}
}