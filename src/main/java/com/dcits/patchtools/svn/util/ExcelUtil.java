package com.dcits.patchtools.svn.util;

import com.dcits.patchtools.svn.model.LogInfo;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Kevin
 * @date 2018-05-04 15:47.
 * @desc Excel文件操作类
 * @email chenkunh@dcits.com
 */
public class ExcelUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    /**
     * 生成送测清单
     * @param map
     * @param dir
     */
    public static void genExcel(Map<LogInfo, Set<String>> map, String dir) {
        //获取当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = df.format(new Date());

        //存储路径--获取桌面位置
        //存储Excel的路径
        String path = dir + "/送测清单-" + date + ".xlsx";
        logger.info(path);
        try {
            // 创建工作薄
            XSSFWorkbook wb = new XSSFWorkbook();
            // 创建工作表
            XSSFSheet sheet = wb.createSheet("送测清单");
            // 数据转换
            map2sheet(map, sheet);

            //输出流,下载时候的位置
//            FileWriter outputStream1 = new FileWriter(path);
            FileOutputStream outputStream = new FileOutputStream(path);
            wb.write(outputStream);
            outputStream.flush();
            outputStream.close();
            logger.info("送测清单生成成功！");
        } catch (Exception e) {
            logger.info("送测清单生成失败！");
            e.printStackTrace();
        }
    }

    /**
     * 将Map数据转换为Excel的sheet数据
     * @param map
     * @param sheet
     */
    private static void map2sheet(Map<LogInfo, Set<String>> map, XSSFSheet sheet) {
        // 添加表头数据
        XSSFRow headRow = sheet.createRow(0); //行
        headRow.createCell(0).setCellValue("送测内容");
        headRow.createCell(1).setCellValue("文件列表");
        headRow.createCell(2).setCellValue("提交人");
        headRow.createCell(3).setCellValue("提交时间");

        // 循环数据写入
        Iterator<Map.Entry<LogInfo, Set<String>>> iterator = map.entrySet().iterator();
        for (int i = 1; iterator.hasNext(); i++) {
            XSSFRow row = sheet.createRow(i);
            Map.Entry<LogInfo, Set<String>> entry = iterator.next();
            LogInfo info = entry.getKey();
            Set<String> fileSet = entry.getValue();
            if (fileSet.size() > 1) {
                int lastRow = i + fileSet.size() - 1;
                sheet.addMergedRegion(new CellRangeAddress(i, lastRow, 0, 0));
                sheet.addMergedRegion(new CellRangeAddress(i, lastRow, 2, 2));
                sheet.addMergedRegion(new CellRangeAddress(i, lastRow, 3, 3));
            }
            row.createCell(0).setCellValue(info.getCommitInfo());
            row.createCell(2).setCellValue(info.getAuthor());
            row.createCell(3).setCellValue(info.getTimestamp());
            Iterator<String> it = fileSet.iterator();
            int j = i;
            for (; it.hasNext(); j++) {
                if (i != j) row = sheet.createRow(j);
                row.createCell(1).setCellValue(it.next());
            }
            i = j - 1;
        }
    }

}
