package net.wit.controller.admin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Admin;
import net.wit.entity.Tenant;
import net.wit.service.AdminService;
import net.wit.service.TicketCacheService;
import net.wit.util.DateUtil;
import net.wit.vo.TicketCacheVO;

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

@Controller("ticketCacheController")
@RequestMapping({ "/admin/ticketCache" })
public class TicketCacheController extends BaseController {
	
	@Resource(name = "ticketCacheServiceImpl")
	private TicketCacheService ticketCacheService;
	
	 @Resource(name="adminServiceImpl")
	 private AdminService adminService;
	
	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(String sendModel, String memberName, String pageDate, Pageable pageable, ModelMap model) throws Exception  {
		String name=null;
		try {
			if(memberName!=null && !"".equals(memberName)){
			name=new String(memberName.getBytes("ISO-8859-1"),"UTF-8");
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		Date lastDayOfMonth =null;
		Date firstDayOfMonth =null;
		if("".equals(sendModel)){
			sendModel=null;
		}
		if("".equals(name)){
			memberName=null;
		}
		if("".equals(pageDate)){
			pageDate=null;
		}
		if(pageDate!=null && !"".equals(pageDate)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			Date date;
			try {
				date = sdf.parse(pageDate);
				lastDayOfMonth = DateUtil.getLastDayOfMonth(date);
				firstDayOfMonth = DateUtil.getFirstDayOfMonth(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		Admin admin = adminService.getCurrent();
		Long tenantId=null;
		if(admin!=null && !"".equals(admin)){
			Tenant tenant = admin.getTenant();
			if(tenant!=null && !"".equals(tenant))
			{
				tenantId=tenant.getId();
			}
		}
		Page<TicketCacheVO> page = ticketCacheService.findTickeCacheByPage(tenantId, firstDayOfMonth, lastDayOfMonth, sendModel, name, pageable);
		model.addAttribute("page", page);
		model.addAttribute("pageDate", pageDate);
		model.addAttribute("sendModel", sendModel);
		model.addAttribute("memberName", name);
		return "/admin/ticket_cache/list";
	}

	/**
	 * 导出Excel
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/exportExcel")
	public void exportExcel(HttpServletResponse response,
			String sendModel, String memberName, String pageDate, ModelMap model) {

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
			String name=null;
			try {
				if(memberName!=null && !"".equals(memberName)){
				name=new String(memberName.getBytes("ISO-8859-1"),"UTF-8");
				}
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			Date lastDayOfMonth =null;
			Date firstDayOfMonth =null;
			if("".equals(sendModel)){
				sendModel=null;
			}
			if("".equals(name)){
				memberName=null;
			}
			if("".equals(pageDate)){
				pageDate=null;
			}
			if(pageDate!=null && !"".equals(pageDate)){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
				Date date;
				try {
					date = sdf.parse(pageDate);
					lastDayOfMonth = DateUtil.getLastDayOfMonth(date);
					firstDayOfMonth = DateUtil.getFirstDayOfMonth(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			Admin admin = adminService.getCurrent();
			Long tenantId=null;
			if(admin!=null && !"".equals(admin)){
				Tenant tenant = admin.getTenant();
				if(tenant!=null && !"".equals(tenant))
				{
					tenantId=tenant.getId();
				}
			}
			List<TicketCacheVO> ticketCacheVOList = ticketCacheService.findTickeCacheByList(tenantId, firstDayOfMonth, lastDayOfMonth, sendModel, name);
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
				sheet.setColumnWidth(6, 20 * 300);

				// 打印大标题
				nRow = sheet.createRow(rowNo++);
				// 行高
				nRow.setHeightInPoints(36);
				nCell = nRow.createCell(cellNo);

				nCell.setCellValue("领券报表");
				nCell.setCellStyle(bigTitle);

				// 第一行 第二列要合并单元格
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 6));

				if (ticketCacheVOList != null && ticketCacheVOList.size() > 0) {
					// 打印小标题
					String titles[] = { "序号", "发放时间", "发放类型", "发放数量",
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
					for (TicketCacheVO ticketCacheVO : ticketCacheVOList) {
						cellNo = 1;
						nRow = sheet.createRow(rowNo++);
						nRow.setHeightInPoints(24);

						// 序列号
						nCell = nRow.createCell(cellNo++);
						nCell.setCellValue(String.valueOf(i++));
						nCell.setCellStyle(textStyle);

						// 发放时间
						nCell = nRow.createCell(cellNo++);
						SimpleDateFormat df = new SimpleDateFormat(
								"yyyy-MM-dd HH:MM:ss");
						String strDate = df.format(ticketCacheVO.getCreateDate());
						if (strDate != null) {
							nCell.setCellValue(strDate);
						} else {
							nCell.setCellValue("");
						}
						nCell.setCellStyle(textStyle);

						// 发放类型
						nCell = nRow.createCell(cellNo++);
						if ("0".equals(ticketCacheVO.getModel().toString())) {
							nCell.setCellValue("店长月初定额");
						} else if ("1".equals(ticketCacheVO.getModel()
								.toString())) {
							nCell.setCellValue("新开通店长");
						} else if ("2".equals(ticketCacheVO.getModel().toString())) {
							nCell.setCellValue("申请发放");
						} else if ("3".equals(ticketCacheVO.getModel()
								.toString())) {
							nCell.setCellValue("定向发放");
						} else if (ticketCacheVO.getModel().toString() == null) {
							nCell.setCellValue("");
						}
						nCell.setCellStyle(textStyle);

						// 发放数量
						nCell = nRow.createCell(cellNo++);
						if (ticketCacheVO.getTicketNum() != null) {
								nCell.setCellValue(ticketCacheVO.getTicketNum());
						} else {
							nCell.setCellValue("");
						}
						nCell.setCellStyle(textStyle);

						// VIP姓名
						nCell = nRow.createCell(cellNo++);
						if (ticketCacheVO.getName() != null) {
							nCell.setCellValue(ticketCacheVO.getName());
						} else {
							nCell.setCellValue("");
						}
						nCell.setCellStyle(textStyle);

						// VIP手机
						nCell = nRow.createCell(cellNo++);
						if (ticketCacheVO.getMobile() != null) {
							nCell.setCellValue(ticketCacheVO.getMobile());
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
					download(byteArrayOutputStream, response, "发放报表.xls");
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