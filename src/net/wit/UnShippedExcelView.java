package net.wit;

import java.net.URLEncoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractExcelView;

/**
 * 打印发货单时导出的excel文件
 * @ClassName： UnShippedExcelView
 * @Description：
 * @author： Teddy
 * @date：2015年10月21日 下午11:45:08
 */
public class UnShippedExcelView extends AbstractExcelView {

	/** 数据 */
	private Collection<net.wit.entity.Order> data;

	private String filename;

	public UnShippedExcelView(String filename, Collection<net.wit.entity.Order> data) {
		this.filename = filename;
		this.data = data;
	}

	@Override
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HSSFSheet sheet = workbook.createSheet();
		HSSFRow header = sheet.createRow(0);
		header.setHeight((short) 400);
		HSSFCell headercell0 = header.createCell(0);// 序号
		headercell0.setCellValue("序号");
		HSSFCell headercell1 = header.createCell(1);// 订单编号
		headercell1.setCellValue("订单编号");
		HSSFCell headercell2 = header.createCell(2);// 收件人公司（即收货人名称）
		headercell2.setCellValue("收件人公司");
		HSSFCell headercell3 = header.createCell(3);// 收件人姓名
		headercell3.setCellValue("收件人姓名");
		HSSFCell headercell4 = header.createCell(4);// 收件人手机
		headercell4.setCellValue("收件人手机");
		HSSFCell headercell5 = header.createCell(5);// 收件人电话
		headercell5.setCellValue("收件人电话");
		HSSFCell headercell6 = header.createCell(6);// 收件人邮编
		headercell6.setCellValue("收件人邮编");
		HSSFCell headercell7 = header.createCell(7);// 收件人地址
		headercell7.setCellValue("收件人地址");
		HSSFCell headercell8 = header.createCell(8);// 配送方式
		headercell8.setCellValue("配送方式");
		HSSFCell headercell9 = header.createCell(9);// 物流公司
		headercell9.setCellValue("快递公司");
		HSSFCell headercell10 = header.createCell(10);// 快递单号：用于反写回数据库
		headercell10.setCellValue("快递单号");

		int count = 1;

		Iterator<net.wit.entity.Order> iterator = data.iterator();
		while (iterator.hasNext()) {
			net.wit.entity.Order order = iterator.next();
			HSSFRow rowItem = sheet.createRow(count);
			
			HSSFCell rowCell0 = rowItem.createCell(0);
			rowCell0.setCellValue(count);// 序号
			HSSFCell rowCell1 = rowItem.createCell(1);
			rowCell1.setCellValue(order.getSn());// 订单编号
			HSSFCell rowCell2 = rowItem.createCell(2);
			rowCell2.setCellValue(order.getConsignee());// 收件人公司（即收货人名称）
			HSSFCell rowCell3 = rowItem.createCell(3);
			rowCell3.setCellValue(order.getConsignee());// 收件人姓名
			HSSFCell rowCell4 = rowItem.createCell(4);
			rowCell4.setCellValue(order.getPhone());// 收件人手机
			HSSFCell rowCell5 = rowItem.createCell(5);
			rowCell5.setCellValue(order.getPhone());// 收件人电话
			HSSFCell rowCell6 = rowItem.createCell(6);
			rowCell6.setCellValue(order.getZipCode());// 收件人邮编
			HSSFCell rowCell7 = rowItem.createCell(7);
			rowCell7.setCellValue(order.getAreaName() + order.getAddress());// 收件人地址
			HSSFCell rowCell8 = rowItem.createCell(8);
			rowCell8.setCellValue("");// 配送方式
			HSSFCell rowCell9 = rowItem.createCell(9);
			rowCell9.setCellValue("");// 物流公司
			HSSFCell rowCell10 = rowItem.createCell(10);
			rowCell10.setCellValue("");// 快递单号：用于反写回数据库
			count++;
		}
		// 自适应宽度
		for (int i = 0; i <= 8; i++) {
			sheet.autoSizeColumn((short) i, true);
		}

		response.setContentType("application/force-download");

		if (StringUtils.isNotEmpty(filename)) {
			response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
		} else {
			response.setHeader("Content-disposition", "attachment");
		}
	}

}
