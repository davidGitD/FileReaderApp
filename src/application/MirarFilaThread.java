package application;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MirarFilaThread extends Thread {
    private File file;
    private ArrayList<String> nombre;
    private ArrayList<String> frasesH;
    private ArrayList<String> seccion9;
    private int lastPos;
    private String  msg;

    public MirarFilaThread(File file,ArrayList<String> nombre, ArrayList<String> frasesH, ArrayList<String> seccion9, int lastPos) {
        this.file = file;
        this.nombre = nombre;
        this.frasesH = frasesH;
        this.lastPos = lastPos;
        this.seccion9 = seccion9;
    }

    public String done() {
    	return msg;
    }

    @Override
    public void run() {
        
        long startTime = System.currentTimeMillis(); // Inicio del contador de tiempo
        try {
            // Abrir el archivo Excel
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));

            // Obtener la hoja de trabajo
            Sheet sheet = workbook.getSheetAt(0); // la primera hoja

            // Obtener la última fila con datos
            int lastRowNum = lastPos + 1;
            System.out.println("VALOR DE LA FILA QUE LLEGA 1 -->: "+lastRowNum);
            // Insertar una nueva fila debajo de la última fila
            
         
            // Crear las celdas en la misma fila


	         // Escribir los datos en las celdas correspondientes
	         for(int i = 0; i < nombre.size(); i++) {
	        	 Row newRow = sheet.createRow(lastRowNum++);
	        	 
	             Cell cell1 = newRow.createCell(0);
	             Cell cell2 = newRow.createCell(3);
	             Cell cell3 = newRow.createCell(4);
	             
	             cell1.setCellValue(nombre.get(i).toUpperCase());
	             cell2.setCellValue(frasesH.get(i).toUpperCase());
	             cell3.setCellValue(seccion9.get(i));
	            
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

