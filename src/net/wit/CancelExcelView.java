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

import net.wit.entity.OrderItem;
import net.wit.util.DateUtil;
import net.wit.util.ExcelUtil;

/**
 * @ClassName：SMTExcelView @Description：
 * @author：Chenlf
 * @date：2015年9月27日 下午12:30:08
 */
public class CancelExcelView extends AbstractExcelView {

	/** 数据 */
	private Collection<net.wit.entity.Order> data;

	private String filename;

	public CancelExcelView(String filename, Collection<net.wit.entity.Order> data) {
		this.filename = filename;
		this.data = data;
	}

	@Override
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HSSFSheet sheet = workbook.createSheet();
		HSSFRow header = sheet.createRow(0);
		header.setHeight((short) 400);
		HSSFCell headercell0 = header.createCell(0);
		headercell0.setCellValue("订单编号");
		HSSFCell headercell1 = header.createCell(1);
		headercell1.setCellValue("订单状态");
		HSSFCell headercell2 = header.createCell(2);
		headercell2.setCellValue("买家账号");
		HSSFCell headercell3 = header.createCell(3);
		headercell3.setCellValue("店长");
		HSSFCell headercell4 = header.createCell(4);
		headercell4.setCellValue("店长推荐人");
		HSSFCell headercell5 = header.createCell(5);
		headercell5.setCellValue("商品名称");
		HSSFCell headercell6 = header.createCell(6);
		headercell6.setCellValue("规格");
		HSSFCell headercell7 = header.createCell(7);
		headercell7.setCellValue("购买数量");
		HSSFCell headercell8 = header.createCell(8);
		headercell8.setCellValue("商品单价");
		HSSFCell headercell9 = header.createCell(9);
		headercell9.setCellValue("订单金额");
		HSSFCell headercell10 = header.createCell(10);
		headercell10.setCellValue("分享佣金");
		HSSFCell headercell11 = header.createCell(11);
		headercell11.setCellValue("邀请奖金");
		HSSFCell headercell12 = header.createCell(12);
		headercell12.setCellValue("收货人姓名");
		HSSFCell headercell13 = header.createCell(13);
		headercell13.setCellValue("联系电话");
		HSSFCell headercell14 = header.createCell(14);
		headercell14.setCellValue("收货地址（地区+地址+邮编）");
		HSSFCell headercell15 = header.createCell(15);
		headercell15.setCellValue("发票抬头");
		HSSFCell headercell16 = header.createCell(16);
		headercell16.setCellValue("下单时间");
		HSSFCell headercell17 = header.createCell(17);
		headercell17.setCellValue("关闭时间");
		HSSFCell headercell18 = header.createCell(18);
		headercell18.setCellValue("备注");

		int count = 1;

		Iterator<net.wit.entity.Order> iterator = data.iterator();
		while (iterator.hasNext()) {
			net.wit.entity.Order order = iterator.next();
			HSSFRow rowItem = sheet.createRow(count);

			HSSFCell rowCell0 = rowItem.createCell(0);
			HSSFCell rowCell1 = rowItem.createCell(1);
			HSSFCell rowCell2 = rowItem.createCell(2);
			HSSFCell rowCell3 = rowItem.createCell(3);
			HSSFCell rowCell4 = rowItem.createCell(4);
			HSSFCell rowCell5 = rowItem.createCell(5);
			HSSFCell rowCell6 = rowItem.createCell(6);
			HSSFCell rowCell7 = rowItem.createCell(7);
			HSSFCell rowCell8 = rowItem.createCell(8);
			HSSFCell rowCell9 = rowItem.createCell(9);
			HSSFCell rowCell10 = rowItem.createCell(10);
			HSSFCell rowCell11 = rowItem.createCell(11);
			HSSFCell rowCell12 = rowItem.createCell(12);
			HSSFCell rowCell13 = rowItem.createCell(13);
			HSSFCell rowCell14 = rowItem.createCell(14);
			HSSFCell rowCell15 = rowItem.createCell(15);
			HSSFCell rowCell16 = rowItem.createCell(16);
			HSSFCell rowCell17 = rowItem.createCell(17);
			HSSFCell rowCell18 = rowItem.createCell(18);
			
			rowCell0.setCellValue(order.getSn());
			rowCell1.setCellValue("待发货");
			rowCell2.setCellValue(order.getMember().getMobile());
			rowCell3.setCellValue(order.getOwner().getMobile());
			if(null!=order.getShopkeeper()&&order.getShopkeeper().getRecommendMember()!=null){
				rowCell4.setCellValue(order.getShopkeeper().getRecommendMember().getMobile());
			}else{
				rowCell4.setCellValue("");
			}

			rowCell9.setCellValue(order.getAmountPaid().setScale(2).toString());
			rowCell10.setCellValue(order.getChargeAmt().setScale(2).toString());
			rowCell11.setCellValue(order.getBonus().setScale(2).toString());
			rowCell12.setCellValue(order.getConsignee());
			rowCell13.setCellValue(order.getPhone());
			rowCell14.setCellValue(order.getAreaName() + order.getAddress() + order.getZipCode());
			rowCell15.setCellValue(order.getInvoiceTitle());
			rowCell16.setCellValue(DateUtil.changeDateToStr(order.getCreateDate(), DateUtil.CN_DISPLAY_DATE_FULL));
			rowCell17.setCellValue(DateUtil.changeDateToStr(order.getModifyDate(), DateUtil.CN_DISPLAY_DATE_FULL));
			rowCell18.setCellValue(order.getMemo());
			
			
			Integer startCount=0;
			int orderSize =  order.getOrderItems().size();
			for (int i = 0; i < orderSize; i++) {
				if (i > 0) {
					count++;
					HSSFRow _rowItem = sheet.createRow(count);
					rowCell5 = _rowItem.createCell(5);
					rowCell6 = _rowItem.createCell(6);
					rowCell7 = _rowItem.createCell(7);
					rowCell8 = _rowItem.createCell(8);
					
				}else{
					startCount = count;
				}
				if(i==orderSize-1&&orderSize>1){
					ExcelUtil.setCellStyle(workbook, sheet, startCount,count,0,4);
					ExcelUtil.setCellStyle(workbook, sheet, startCount,count,9,17);
				}
				OrderItem ot = order.getOrderItems().get(i);
				rowCell5.setCellValue(ot.getName() );
				rowCell6.setCellValue(ot.getSpecificationValue());
				rowCell7.setCellValue(ot.getQuantity());
				rowCell8.setCellValue(ot.getPrice().setScale(2)+"");
			}
			count++;
			
		}
		// 自适应宽度
		for (int i = 0; i <= 18; i++) {
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
