package net.wit;

import java.net.URLEncoder;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import net.wit.entity.OrderItem;
import net.wit.util.DateUtil;

/**
 * @ClassName：SMTExcelView @Description：
 * @author：Chenlf
 * @date：2015年9月27日 下午12:30:08
 */
public class SMTExcelView extends AbstractExcelView {

	/** 数据 */
	private Collection<net.wit.entity.Order> data;

	private String filename;

	public SMTExcelView(String filename, Collection<net.wit.entity.Order> data) {
		this.filename = filename;
		this.data = data;
	}

	@Override
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Iterator<net.wit.entity.Order> iterator = data.iterator();
		while (iterator.hasNext()) {
			net.wit.entity.Order order = iterator.next();
			HSSFSheet sheet = workbook.createSheet();
			HSSFRow header = sheet.createRow(0);
			header.setHeight((short) 400);
			HSSFCell headercell = header.createCell(0);
			sheet.addMergedRegion(new Region(0, (short) 0, 0, (short) 7));
			headercell.setCellValue(order.getTenant().getName() + "指帮内购发货单");

			HSSFRow row1 = sheet.createRow(1);
			HSSFCell cell11 = row1.createCell(0);
			cell11.setCellValue("订单号：" + order.getSn());
			sheet.addMergedRegion(new Region(1, (short) 0, 1, (short) 3));
			HSSFCell cell12 = row1.createCell(1);
			cell12.setCellValue("配送方式：");
			HSSFCell cell13 = row1.createCell(2);
			cell13.setCellValue(order.getShippingMethodName());
			HSSFCell cell14 = row1.createCell(4);
			cell14.setCellValue("导出时间：");
			HSSFCell cell15 = row1.createCell(5);
			cell15.setCellValue(net.wit.util.DateUtil.changeDateToStr(new Date(), net.wit.util.DateUtil.LINK_DISPLAY_DATE_FULL));

			HSSFRow row2 = sheet.createRow(2);
			HSSFCell cell21 = row2.createCell(0);
			cell21.setCellValue("店长");
			HSSFCell cell22 = row2.createCell(1);
			cell22.setCellValue(order.getOwner().getName());
			HSSFCell cell23 = row2.createCell(4);
			cell23.setCellValue("下单时间：");
			HSSFCell cell24 = row2.createCell(5);
			cell24.setCellValue(DateUtil.changeDateToStr(order.getCreateDate(), DateUtil.LINK_DISPLAY_DATE_FULL));

			HSSFRow row3 = sheet.createRow(4);
			HSSFCell cell31 = row3.createCell(0);
			cell31.setCellValue("收货人:");
			HSSFCell cell32 = row3.createCell(1);
			sheet.addMergedRegion(new Region(4, (short) 1, 4, (short) 3));
			cell32.setCellValue(order.getConsignee());
			HSSFCell cell33 = row3.createCell(4);
			cell33.setCellValue("联系电话:");
			HSSFCell cell34 = row3.createCell(5);
			cell34.setCellValue(order.getPhone());

			HSSFRow row4 = sheet.createRow(5);
			HSSFCell cell41 = row4.createCell(0);
			cell41.setCellValue("收货地址:");
			HSSFCell cell42 = row4.createCell(1);
			sheet.addMergedRegion(new Region(5, (short) 1, 5, (short) 7));
			cell42.setCellValue(order.getAreaName() + order.getAddress());

			HSSFRow row5 = sheet.createRow(7);
			HSSFCell cell51 = row5.createCell(0);
			cell51.setCellValue("买家备注:");
			HSSFCell cell52 = row5.createCell(1);
			sheet.addMergedRegion(new Region(7, (short) 1, 7, (short) 7));
			cell52.setCellValue(order.getMemo());

			HSSFRow row6 = sheet.createRow(8);
			HSSFCell cell61 = row6.createCell(0);
			cell61.setCellValue("卖家备注:");
			HSSFCell cell62 = row6.createCell(1);
			sheet.addMergedRegion(new Region(8, (short) 1, 8, (short) 7));
			cell62.setCellValue(order.getMemo());

			HSSFRow row7 = sheet.createRow(10);
			HSSFCell cell71 = row7.createCell(0);
			cell71.setCellValue("序号");
			HSSFCell cell72 = row7.createCell(1);
			sheet.addMergedRegion(new Region(10, (short) 1, 10, (short) 5));
			cell72.setCellValue("商品名称");
			HSSFCell cell73 = row7.createCell(6);
			cell73.setCellValue("规格");
			HSSFCell cell74 = row7.createCell(7);
			cell74.setCellValue("数量");

			int count = 1;
			for (OrderItem orderItem : order.getOrderItems()) {
				HSSFRow rowItem = sheet.createRow(10 + count);
				HSSFCell cellrowItem1 = rowItem.createCell(0);
				cellrowItem1.setCellValue(count);
				HSSFCell cellrowItem2 = rowItem.createCell(1);
				sheet.addMergedRegion(new Region(10 + count, (short) 1, 10 + count, (short) 5));
				cellrowItem2.setCellValue(orderItem.getName());
				HSSFCell cellrowItem3 = rowItem.createCell(6);
				cellrowItem3.setCellValue(orderItem.getProduct().getSpecification_value().toString());
				HSSFCell cellrowItem4 = rowItem.createCell(7);
				cellrowItem4.setCellValue(orderItem.getQuantity());
				count++;
			}

		}

		response.setContentType("application/force-download");

		if (StringUtils.isNotEmpty(filename)) {
			response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
		} else {
			response.setHeader("Content-disposition", "attachment");
		}
	}

}
