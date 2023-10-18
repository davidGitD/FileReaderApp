package application;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class rellenarExcelFormularios extends Thread {
    private File file;
    private ArrayList<String> name;
    private ArrayList<String> fileName;
    private ArrayList<String> date;
    private int lastPos;
    private String  msg;

    public rellenarExcelFormularios(File file,ArrayList<String> name, ArrayList<String> fileName,ArrayList<String> date, int lastPos) {
        this.name = name;
        this.date = date;
        this.fileName = fileName;
        this.lastPos = lastPos;
        this.file = file;

    }

    public String done() {
    	return msg;
    }

    @Override
    public void run() {
        
        long startTime = System.currentTimeMillis(); // Inicio del contador de tiempo
        try {
        	System.out.println("dentro del metodo: "+file.getName());
            // Abrir el archivo Excel
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));

            // Obtener la hoja de trabajo
            Sheet sheet = workbook.getSheetAt(0); // la primera hoja

            // Obtener la última fila con datos
            int lastRowNum = lastPos + 1;
            CellStyle estilo = workbook.createCellStyle();
            estilo.setAlignment(HorizontalAlignment.RIGHT);
            

	         // Escribir los datos en las celdas correspondientes
	         for(int i = 0; i < name.size(); i++) {
	        	 Row newRow = sheet.createRow(lastRowNum++);
	        	 
	             Cell cell1 = newRow.createCell(0);
	             Cell cell2 = newRow.createCell(1);
	             Cell cell3 = newRow.createCell(2);
	             Cell cell4 = newRow.createCell(6);
	             
	             cell3.setCellStyle(estilo);
	             cell1.setCellValue(name.get(i).toUpperCase());
	             cell2.setCellValue(fileName.get(i));
	             cell3.setCellValue(date.get(i));
	             cell4.setCellValue("Otros");
	         }
	
            
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();

            msg = "Datos añadidos";
        } catch (IOException e) {
            e.printStackTrace();
            msg = "No se han podido añadir";
        }
        long endTime = System.currentTimeMillis(); 
        long totalTime = endTime - startTime; 
        msg += " (Tiempo de ejecución: " + totalTime + " ms)"; 
        System.out.println(msg);
    }
}