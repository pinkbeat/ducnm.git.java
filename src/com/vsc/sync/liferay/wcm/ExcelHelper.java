package com.vsc.sync.liferay.wcm;

import java.io.File;
/*
 * author ducnm
 * 
 */
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;

import com.vsc.util.FileUtil;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelHelper {
	private final static String XSSF_TYPE = "xlsx";
	private final static String HSSF_TYPE = "xls";		
	private static int[] index;
	private static String[] listVNPArticle = {"NEWS_CAT_ID", "VN_TITLE", "PREVIEW_IMG_URL", "VN_CONTENT", "VN_PREVIEW", "TYPE", "STRUCTUREID","TEMPLATEID", "LAYOUTUUID", "FOLDERID","CATEGORYID","GROUPID"};
	public ExcelHelper() {
	}
	private static void writeExcelFile(List<Row> rows, String fileName){
		//Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();          
        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("NA");
		//Iterate over data and write to sheet        
        int rownum = 1;
        int cellnum = 0;        
        Cell cell = null;
        Row rownew = null;
        Iterator<Cell> celliterator=null;
        Cell oldcell= null;
        for (Row oldrow : rows)
        {
        	rownew = sheet.createRow(rownum++);            
            cellnum=0;
            for(int i=0;i<oldrow.getLastCellNum();i++)
            {            	
				oldcell = oldrow.getCell(i);
				cell = rownew.createCell(cellnum++);
				if (!isCellEmpty(oldcell)) {
					switch (oldcell.getCellType()) {
					case Cell.CELL_TYPE_NUMERIC:
						cell.setCellValue(oldcell.getNumericCellValue());
						break;
					case Cell.CELL_TYPE_STRING:
						cell.setCellValue(oldcell.getStringCellValue());
						break;
					case Cell.CELL_TYPE_BOOLEAN:
						cell.setCellValue(oldcell.getBooleanCellValue());
						break;
					case Cell.CELL_TYPE_FORMULA:
						cell.setCellValue(oldcell.getCellFormula());
						break;
					case Cell.CELL_TYPE_ERROR:
						cell.setCellErrorValue(oldcell.getErrorCellValue());
						break;
					default:
						cell.setCellValue(" ");
						break;
					}
				} else
					cell.setCellValue(" ");

			}
        }
        try
        {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File(fileName));
            workbook.write(out);
            out.close();
            System.out.println(fileName+" written successfully on disk.");
        } 
        catch (Exception e) 
        {
        	System.out.println(fileName+" written UNsuccessfully on disk.");
            e.printStackTrace();            
        }
	}
	public static void generateErrorFile(String logFile, String srcExcelFile, String desExcelFile, String ext){
		String fileTmp = FileUtil.readFile(new File(logFile));
		JSONArray array = new JSONArray(fileTmp);
		Workbook wb = null;
		int size = array.length();
		Row row = null;
		List<Row> list=null;
		try(
				FileInputStream inputStream = new FileInputStream(new File(srcExcelFile));
			) {
			if (ext.equals(XSSF_TYPE))
				wb = new XSSFWorkbook(inputStream);
			else if (ext.equals(HSSF_TYPE)) {
				wb = new HSSFWorkbook(inputStream);
			}
			Sheet sheet = wb.getSheetAt(0);
			list = new ArrayList<>();
			for(int i=0;i<size;i++){
				row=sheet.getRow(array.getJSONObject(i).getInt("rownum"));
				list.add(row);				
			}
			System.out.println(list.size());
			writeExcelFile(list, desExcelFile);
		} catch (IOException e) {
			System.out.println("Can not find excel file");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Getted error while extracting excel file");
			e.printStackTrace();
		} finally {
			try {
				if(wb!=null)
					wb.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static boolean isCellEmpty(Cell cell) {
	    if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
	        return true;
	    }
	    if (cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().isEmpty()) {
	        return true;
	    }
	    return false;
	}	
	private static int checkColumnName(Row row, String[] arrayCol) {
		int j, k=0, i = 0;
		String tmp = "";
		Cell cell = null;
		index = new int[arrayCol.length];
		for (; i < row.getLastCellNum(); i++) {
			cell = row.getCell(i);
			if(cell!=null){
				tmp = cell.getStringCellValue().trim();				
				j = 0;
				if (tmp != null) {
					for (; j < arrayCol.length; j++) {					
						if (tmp.equals(arrayCol[j])) {							
							index[k++]=i;
//							System.out.println(i);
							break;
						} 
					}					
				}
			}
		}
		if(index.length < arrayCol.length) return -1;
		return 1;
	}
	public static List<ArticleDTO> extractContent(String excelFile, String[] arrayCols, String ext){
		
		Workbook wb = null;
		int r = 0;
		Row row = null;
		List<ArticleDTO> list =null;
//		List<Row> listerror =null;
		try(
				FileInputStream inputStream = new FileInputStream(new File(excelFile));
			) {
			if (ext.equals(XSSF_TYPE))
				wb = new XSSFWorkbook(inputStream);
			else if (ext.equals(HSSF_TYPE)) {
				wb = new HSSFWorkbook(inputStream);
			}
			Sheet sheet = wb.getSheetAt(0);
			row = sheet.getRow(0);
			int rowCount = sheet.getLastRowNum();
			int colCount = row.getLastCellNum();

			if (rowCount == 0) {
				r = -1;
			} else if (colCount == 0) {
				r = -2;
			} else if (colCount < arrayCols.length) {
				r = -3;
			}else
				r=checkColumnName(row, arrayCols);
			if(r>0){
				list = new ArrayList<>();
//				listerror = new ArrayList<>();
				for(int i=1;i<rowCount;i++){
					row=sheet.getRow(i);
					ArticleDTO dto = mapContent(row);					
					if(dto!=null)
						list.add(dto);
//					else
//						listerror.add(row);
				}
//				if(listerror.size()!=0)
//					writeExcelFile(listerror, excelErrorFile);
			}else{
				System.out.println("Excel is not enough necessary columns");
			}
			
		} catch (IOException e) {
			System.out.println("Can not find excel file");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Getted error while extracting excel file");
			e.printStackTrace();
		} finally {
			try {
				if(wb!=null)
					wb.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}
	private static ArticleDTO mapContent(Row row) {
		// TODO Auto-generated method stub
		
		Cell cell=row.getCell(index[0]);

		int i =(int)cell.getNumericCellValue();

		if( i!= 1 && i!=4) return null;

		Calendar yesterday = Calendar.getInstance();
	    yesterday.add(Calendar.DAY_OF_YEAR, -1);
	    Calendar nextWeek = Calendar.getInstance();
	    nextWeek.add(Calendar.WEEK_OF_YEAR, 1);
	    ArticleDTO dto = new ArticleDTO();
	    dto.setRownum(row.getRowNum());
	    dto.setGroupId(row.getCell(index[11])!=null?(int)row.getCell(index[11]).getNumericCellValue()+"":"21237");	    
	    dto.setFolderId(row.getCell(index[9])!=null?(int)row.getCell(index[9]).getNumericCellValue()+"":"");
	    dto.setCat_id(row.getCell(index[10])!=null?(int)row.getCell(index[10]).getNumericCellValue()+"":"");
	    dto.setClassNameId("0");
	    dto.setClassPK("0");
	    dto.setArticleId("");
	    dto.setAutoArticleId("true");	    
	    dto.setTitleMap(row.getCell(index[1])!=null?row.getCell(index[1]).getStringCellValue():"Không có tiêu đề");	    
	    dto.setDescriptionMap("");
	    dto.setMota(row.getCell(index[4])!=null?row.getCell(index[4]).getStringCellValue():"");
	    dto.setImages(row.getCell(index[2])!=null?row.getCell(index[2]).getStringCellValue():"");
	    dto.setContent(row.getCell(index[3])!=null?row.getCell(index[3]).getStringCellValue():"");
	    dto.setType(row.getCell(index[5])!=null?row.getCell(index[5]).getStringCellValue():"");
	    dto.setDdmStructureKey(row.getCell(index[6])!=null?(int)row.getCell(index[6]).getNumericCellValue()+"":"");
	    dto.setDdmTemplateKey(""+row.getCell(index[7])!=null?(int)row.getCell(index[7]).getNumericCellValue()+"":"");
	    dto.setLayoutUuid(row.getCell(index[8])!=null?row.getCell(index[8]).getStringCellValue():"");
	    dto.setNeverExpire("true");
	    dto.setNeverReview("true");
	    dto.setIndexable("true");
	    dto.setArticleURL("articleURL");
	    dto.setServiceContext("{addGuestPermissions:true}");	    	
	    dto.setDisplayDateMonth(yesterday.get(Calendar.MONTH));
	    dto.setDisplayDateDay(yesterday.get(Calendar.DAY_OF_MONTH));
	    dto.setDisplayDateYear(yesterday.get(Calendar.YEAR));
	    dto.setDisplayDateHour(yesterday.get(Calendar.HOUR_OF_DAY));
	    dto.setDisplayDateMinute(yesterday.get(Calendar.MINUTE));
	    dto.setExpirationDateMonth(yesterday.get(Calendar.MONTH));
	    dto.setExpirationDateDay(nextWeek.get(Calendar.DAY_OF_MONTH));
	    dto.setExpirationDateYear(nextWeek.get(Calendar.YEAR));
	    dto.setExpirationDateHour(nextWeek.get(Calendar.HOUR_OF_DAY));
	    dto.setExpirationDateMinute(nextWeek.get(Calendar.MINUTE));
	    dto.setReviewDateMonth(nextWeek.get(Calendar.MONTH));
	    dto.setReviewDateDay(nextWeek.get(Calendar.DAY_OF_MONTH));
	    dto.setReviewDateYear(nextWeek.get(Calendar.YEAR));
	    dto.setReviewDateHour(nextWeek.get(Calendar.HOUR_OF_DAY));
	    dto.setReviewDateMinute(nextWeek.get(Calendar.MINUTE));	    
	    return dto;
	}
}
