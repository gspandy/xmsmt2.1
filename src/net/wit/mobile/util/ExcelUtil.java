package net.wit.mobile.util;

import net.wit.entity.Member;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import jodd.util.StringUtil;

/**
 * Created with IntelliJ IDEA.
 * User: ab
 * Date: 15-9-7
 * Time: 下午8:37
 * To change this template use File | Settings | File Templates.
 */
public class ExcelUtil {

    public static String getFileType(String fileName) {
        int index = fileName.lastIndexOf(".");

        if (index == -1) {
            return "";
        }

        return fileName.substring(index + 1, fileName.length());
    }

    public static void  readExcel(InputStream fis,List<String> colNames, List<Object[]> listData) throws IOException {
        HSSFWorkbook w = new HSSFWorkbook(new POIFSFileSystem(fis));

        HSSFSheet sheet = w.getSheetAt(0);

        int rowCount = sheet.getLastRowNum();


        HSSFRow hssfRow;
        HSSFCell hssfCell;
        String value = "";
        String [] colData;
        for (int row = 0; row <= rowCount; row ++) {
            hssfRow = sheet.getRow(row);

            int cellCount = hssfRow.getLastCellNum();
            colData = new String[cellCount];
            for (int cellIndex = 0; cellIndex < cellCount; cellIndex ++) {
                hssfCell = hssfRow.getCell(cellIndex);


                if (hssfCell != null) {

                    hssfCell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    value = hssfCell.getRichStringCellValue().getString();
                    if (StringUtils.isEmpty(value)) {
                        value = null;
                    }
                    colData[cellIndex] = value;

                    //标题行
                    if (row == 0) {
                        if (StringUtils.isNotEmpty(value)) {
                            colNames.add(value.substring(value.indexOf("(") + 1, value.length() - 1));
                        }
                    }
                }
            }

            //空行继续读
            if (isEmptyLine(colData)) {
                continue;
            }

            //数据行
            if (row > 0) {
                listData.add(colData);
            }

        }

    }

    public static boolean isEmptyLine(String [] colData) {
        boolean flag = true;

        if (colData == null || colData.length == 0) {
            return flag;
        }

        for (String data : colData) {
            if (StringUtils.isNotEmpty(data)) {
                flag = false;
                break;
            }
        }

        return flag;
    }
    public static String getMemberName(Member member){
    	String name = "";
    	name = member.getName();
    	if(StringUtil.isEmpty(name))name = member.getNickName();
    	if(StringUtil.isEmpty(name))name = "指帮用户";
    	return name;
    }
}
