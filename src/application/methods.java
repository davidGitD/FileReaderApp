package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;



public class methods {
	
	
	//COMPRUEBAN SI ES EL TIPO DE ARCHIVO QUE QUEREMOS
	public static boolean isPDF(File file) {

        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return extension.equalsIgnoreCase("PDF");
    }

    public static boolean isExcel(File file) {
        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return extension.equalsIgnoreCase("xlsx") || extension.equalsIgnoreCase("xls");
    }
    
    //METODOS PARA FORMATEAR LOS DATOS OBTENIDOS
    public static ArrayList<String> removeDuplicates(ArrayList<String> list) {
        ArrayList<String> uniqueList = new ArrayList<String>();
        for (String word : list) {
            if (!uniqueList.contains(word)) {
                uniqueList.add(word);
            }
        }
        return uniqueList;
    }

    public static String removeParentesis(String text) {
        int openParenthesisIndex = text.indexOf('(');
        int closeParenthesisIndex = text.indexOf(')');
        if (openParenthesisIndex != -1 && closeParenthesisIndex != -1) {
            return text.substring(openParenthesisIndex + 1, closeParenthesisIndex).trim();
        }
        return text;
    }

    public static String eliminarPrimerYUltimoParentesis(String cadena) {
        int lastPos=0;
        int index = cadena.indexOf('(');
        String txt="";
        // Contar el número de paréntesis en la cadena
        
        for (int i = 0; i < cadena.length(); i++) {
            if (cadena.charAt(i) == ')') {
                lastPos=i;
            }
        }
        
     
        if (index != -1 && lastPos != 0) {
            txt =  cadena.substring(index + 1, lastPos).trim();
        }
        System.out.println(txt);
        return txt;
    }


	//BUSCAN EL CONTENIDO DENTRO DE LOS PDFS

    public static String buscaFrasesH(File file, String[] searchWords) {

        String name = "No se pudo encontrar";
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            int currentPage = 0;
            boolean wordFound = false;

            // Recorre cada página del documento
            while (currentPage < document.getNumberOfPages() && !wordFound) {
                currentPage++;
                stripper.setStartPage(currentPage);
                stripper.setEndPage(currentPage);

                String text = stripper.getText(document);
                ArrayList<String> lines = new ArrayList<>();
                ArrayList<String> frasesH = new ArrayList<>();
                String searchWord = "";

                // Recorre cada palabra de búsqueda hasta encontrar una coincidencia o terminar de recorrer todas las palabras
                for (int i = 0; i < searchWords.length && !wordFound; i++) {
                    searchWord = searchWords[i];
                    // Busca la palabra en el texto
                    if (text.toLowerCase().contains(searchWord)) {
                        stripper.setStartPage(currentPage);
                        stripper.setEndPage(currentPage+1);
                        text = stripper.getText(document);
                        text = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
                        wordFound=true;
                        System.out.println("La palabra \"" + searchWord + "\" se encuentra en la página " + currentPage + " del documento.");


                        try (BufferedReader br = new BufferedReader(new StringReader(text))) {
                            String line;
                            boolean encontrado = false;
                            int contador = 0;

                            while ((line = br.readLine()) != null) {

                                if (encontrado) {
                                    lines.add(line);
                                    contador++;
                                    if (contador == 20) {
                                        break;
                                    }
                                } else if (line.toLowerCase().contains(searchWord)) {
                                    encontrado = true;
                                    lines.add(line);
                                }
                            }
                            br.close();

                            //ver el texto del ARRAY
                            for(String linea: lines) {

                                Pattern pattern = Pattern.compile("\\bH\\d{3}\\b"); // Expresión regular para buscar palabras que empiezan por H y tienen 3 números
                                Matcher matcher = pattern.matcher(linea);

                                while (matcher.find()) {

                                    frasesH.add(matcher.group());
                                }
                            }

                            frasesH = removeDuplicates(frasesH);
                            if(frasesH.size()>0) {
                                String separadoPorComas = String.join(",", frasesH);
                                System.out.println("CADENA: "+separadoPorComas);
                                name = separadoPorComas;
                            }
                        }

                    } else {
                        System.out.println("La palabra \"" + searchWord + "\" no se encontró en la página " + currentPage + " del documento.");
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        }

        return name;
    }

	public static ArrayList<String> buscarFrasesH22(ArrayList<File> files) {

	    ArrayList<String> allfrasesH = new ArrayList<>();

	    // Palabras a buscar, en orden de preferencia
	    String[] searchWords = {"indicaciones de peligro", "elementos de la etiqueta","2.1","2.2","hazard identification","label elements"};

	    for (File file : files) {
	        boolean wordFound = false;
	        String name = "No se pudo encontrar";
	        ArrayList<String> frasesH = new ArrayList<>();
	        int i = 0;
	        while (!wordFound && i < searchWords.length) {
	            String searchWord = searchWords[i];
	            System.out.println("PALABRA DE BUSQUEDA-->"+searchWord);
	            try {
	                FileReader fileReader = new FileReader(file);
	                BufferedReader bufferedReader = new BufferedReader(fileReader);

	                String line;
	                int lineNumber = 1;
	                ArrayList<String> lines = new ArrayList<>();
	                boolean encontrado = false;
	                int contador = 0;

	                while ((line = bufferedReader.readLine()) != null && !wordFound) {
	                    if (line.toLowerCase().contains(searchWord.toLowerCase())) {
	                        System.out.println("ENCONTRADA!!!!! \"" + searchWord + "\" en la línea " + lineNumber + ": " + line);
	                    }
	                    if (encontrado) {
	                        lines.add(line);
	                        contador++;
	                        if (contador == 50) {
	                            wordFound=true;
	                        }
	                    } else if (line.toLowerCase().contains(searchWord)) {
	                        encontrado = true;
	                        lines.add(line);
	                    }
	                    lineNumber++;
	                }

	                bufferedReader.close();
	                fileReader.close();

	                for(String linea: lines) {
	                    Pattern pattern = Pattern.compile("\\bH\\d{3}\\b"); // Expresión regular para buscar palabras que empiezan por H y tienen 3 números
	                    Matcher matcher = pattern.matcher(linea);

	                    while (matcher.find()) {
	                        frasesH.add(matcher.group());
	                    }
	                }

	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	            frasesH = removeDuplicates(frasesH);
	            if(frasesH.size()>0) {
	                String separadoPorComas = String.join(",", frasesH);
	                System.out.println("CADENA: "+separadoPorComas);
	                name = separadoPorComas;
	                frasesH.add(name);
	                wordFound = true;
	            }
	            i++;
	        }
            if(name.toLowerCase().contains("no se pudo encontrar")) {
            	System.out.println("ENTRA AQUI PARA MIRAR COMO TXT-----------------------------------");
            	name = buscaFrasesH(file,searchWords);
            }
	        allfrasesH.add(name);
	        System.out.println("-->"+name);
	    }



	    return allfrasesH;
	}
	
	public static ArrayList<String> leerPDFsArray(ArrayList<File> files) {
 	    ArrayList<String> names = new ArrayList<>();
	    String[] searchWords= {"1.1","nombre comercial","identification of the substance","product name"};
	    for (File file : files) {
	        String name = "No se pudo encontrar";

	        try (PDDocument document = PDDocument.load(file)) {
	            PDFTextStripper stripper = new PDFTextStripper();
	            stripper.setSortByPosition(true);

	            int currentPage = 0;
	            boolean wordFound = false;

	            // Recorre cada página del documento
	            while (currentPage < document.getNumberOfPages() && !wordFound) {
	                currentPage++;
	                stripper.setStartPage(currentPage);
	                stripper.setEndPage(currentPage);

	                String text = stripper.getText(document);
	                text = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}", "");

	                ArrayList<String> lines = new ArrayList<>();
	                
	                // Busca la palabra en el texto
	                for (String searchWord : searchWords) {
	                    if (text.toLowerCase().contains(searchWord)) {
	                        wordFound = true;
	                        System.out.println("La palabra \"" + searchWord + "\" se encuentra en la página " + currentPage + " del documento.");

	                        try (BufferedReader br = new BufferedReader(new StringReader(text))) {
	                            String line;
	                            boolean encontrado = false;
	                            int contador = 0;

	                            while ((line = br.readLine()) != null) {
	                                if (encontrado) {
	                                    lines.add(line);
	                                    contador++;
	                                    if (contador == 5) {
	                                        break;
	                                    }
	                                } else if (line.toLowerCase().contains(searchWord)) {
	                                    encontrado = true;
	                                    lines.add(line);
	                                }
	                            }
	                            br.close();

	                            //ver el texto del ARRAY
	                            for (String linea : lines) {
	                                String regex = "\\b[A-Z]+[0-9]*\\b";
	                                Pattern pattern = Pattern.compile(regex);
	                                Matcher matcher = pattern.matcher(linea);

	                                if (matcher.find()) {

	                                    int indiceInicio = matcher.start();
	                                    String subtexto = linea.substring(indiceInicio);

	                                    name = subtexto.trim();
	                                    break;
	                                }
	                            }
	                        }
	                    }
	                }
	            }

	        } catch (IOException e) {
	            e.printStackTrace();
	            System.out.println(e);
	        }
	        if(name.toLowerCase().contains("no se pudo encontrar")) {
	            name = leerTXTArray(file,searchWords);
	        }
	        names.add(name);
	    }
	    System.out.println("el nombre es:"+names);
	    return names;
	}

    public static String leerTXT(File file) {
        String searchWord = "1.1";
        boolean wordFound = false;
        String name = "No se pudo encontrar";
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            String line;
            int lineNumber = 1;
            ArrayList<String> lines = new ArrayList<>();
            boolean encontrado = false;
            int contador = 0;
            
            while ((line = bufferedReader.readLine()) != null && !wordFound) {
            	
                if (line.toLowerCase().contains(searchWord.toLowerCase())) {
                	
                    System.out.println("ENCONTRADA!!!!! \"" + searchWord + "\" en la línea " + lineNumber + ": " + line);
                }
                if (encontrado) {
                    lines.add(line);
                    contador++;
                    if (contador == 15) {
                        wordFound=true;
                    }
                } else if (line.toLowerCase().contains(searchWord)) {
                    encontrado = true;
                    lines.add(line);
                }
                lineNumber++;
            }
            
            for(String frase:lines) {

            	String regex = "\\([A-Z0-9 ]+\\)";
                Pattern pattern = Pattern.compile(regex); 
                Matcher matcher = pattern.matcher(frase);
                
                if(matcher.find()) {
                    String palabraMayuscula = matcher.group();
                    name= removeParentesis(palabraMayuscula);
 
                    break;
                }
            }
            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }

    public static String leerTXTArray(File file, String[] searchWords) {
        boolean wordFound = false;
        String name = "No se pudo encontrar";
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            int lineNumber = 1;
            ArrayList<String> lines = new ArrayList<>();
            boolean encontrado = false;
            int contador = 0;

            while ((line = bufferedReader.readLine()) != null && !wordFound) {
                for (String searchWord : searchWords) {
                    if (line.toLowerCase().contains(searchWord.toLowerCase())) {
                        System.out.println("ENCONTRADA!!!!! \"" + searchWord + "\" en la línea " + lineNumber + ": " + line);
                    }
                    if (encontrado) {
                        lines.add(line);
                        contador++;
                        if (contador == 15) {
                            wordFound = true;
                        }
                    } else if (line.toLowerCase().contains(searchWord)) {
                        encontrado = true;
                        lines.add(line);
                    }
                }
                lineNumber++;
            }

            for (String frase : lines) {
                String regex = "\\([A-Z0-9 ]+\\)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(frase);

                if (matcher.find()) {
                    String palabraMayuscula = matcher.group();
                    name = removeParentesis(palabraMayuscula);

                    break;
                }
            }
            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }

    
    public static String leerTXT9(File file) {
        String searchWord = "seccion 9";
        boolean wordFound = false;
        String name = "No se pudo encontrar";
        
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            String line;
            int lineNumber = 1;
            ArrayList<String> lines = new ArrayList<>();
            ArrayList<String> sinParentesis = new ArrayList<>();
            boolean encontrado = false;
            
            
            while ((line = bufferedReader.readLine()) != null && !wordFound) {
                if (line.toLowerCase().contains(searchWord.toLowerCase())) {
                	
                    System.out.println("ENCONTRADA!!!!! \"" + searchWord + "\" en la línea " + lineNumber + ": " + line);
                }
                if (encontrado) {
                	lines.add(line);
                    
                    if (line.toLowerCase().contains("seccion 10")) {
                    	lines.remove(lines.size()-1);
                        break;
                    }
                } else if (line.toLowerCase().contains(searchWord)) {
                    encontrado = true;

                }
                lineNumber++;
            }
            if(lines.size()>1) {
            	int i=0;
                for(String frase : lines) {
                	if(frase.indexOf("(")!=-1) {
                		sinParentesis.add(eliminarPrimerYUltimoParentesis(lines.get(i)));
                	}
                	i++;
                }

            }


            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("eSTOY EN LA ULTIMA CAPA DE SECCION 9 ESTE MENSAJE: "+name);
        return name;
    }
    
    public static String leerTXT9Array(File file,String[] searchWord) {
        boolean wordFound = false;
        String name = "No se pudo encontrar";
        
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            String line;
            int lineNumber = 1;
            ArrayList<String> lines = new ArrayList<>();
            ArrayList<String> sinParentesis = new ArrayList<>();
            boolean encontrado = false;
            
            
            while ((line = bufferedReader.readLine()) != null && !wordFound) {
                for (String word : searchWord) {
                    if (line.toLowerCase().contains(word.toLowerCase())) {
                        System.out.println("ENCONTRADA!!!!! \"" + word + "\" en la línea " + lineNumber + ": " + line);
                        wordFound = true; // si se encuentra alguna palabra del array, se marca como encontrada
                        break; // se sale del loop para evitar seguir buscando
                    }
                }
                if (encontrado) {
                    lines.add(line);
                    
                    if (line.toLowerCase().contains("seccion 10") || line.toLowerCase().contains("10.")) {
                        lines.remove(lines.size()-1);
                        break;
                    }
                } else if (wordFound) { // si se encontró alguna palabra del array, se marca como encontrado
                    encontrado = true;
                }
                lineNumber++;
            }
            if(lines.size()>1) {
                int i=0;
                for(String frase : lines) {
                    if(frase.indexOf("(")!=-1) {
                        sinParentesis.add(eliminarPrimerYUltimoParentesis(lines.get(i)));
                    }
                    i++;
                }

            }


            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("eSTOY EN LA ULTIMA CAPA DE SECCION 9 ESTE MENSAJE: "+name);
        return name;
    }
    
    
    public static ArrayList<String> leerPDFs9s(ArrayList<File> files) {
        ArrayList<String> result = new ArrayList<>();

        for (File file : files) {
            String name = "No se pudo encontrar";
            String separadoPorComas = "";
            ArrayList<String> seccion9 = new ArrayList<>();
            System.out.println("ESTAMOS: " + file.getName());
            try (PDDocument document = PDDocument.load(file)) {
                String searchWord = "seccion 9";
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);

                int currentPage = 0;
                int pagina = 0;
                boolean wordFound = false;

                // Recorre cada página del documento
                while (currentPage < document.getNumberOfPages() && !wordFound) {
                    currentPage++;
                    pagina++;
                    stripper.setStartPage(currentPage);
                    stripper.setEndPage(currentPage);

                    String text = stripper.getText(document);
                    text = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}", "");

                    // Busca la palabra en el texto
                    if (text.toLowerCase().contains(searchWord)) {

                        stripper.setStartPage(currentPage);
                        stripper.setEndPage(currentPage + 1);
                        text = stripper.getText(document);
                        text = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
                        wordFound = true;

                        System.out.println("La palabra \"" + searchWord + "\" se encuentra en la página " + currentPage + " del documento.");

                        try (BufferedReader br = new BufferedReader(new StringReader(text))) {
                            String line;
                            boolean encontrado = false;

                            while ((line = br.readLine()) != null) {
                                if (encontrado) {
                                    seccion9.add(line);

                                    if (line.toLowerCase().contains("seccion 10")) {
                                        seccion9.remove(seccion9.size() - 1);
                                        break;
                                    }
                                } else if (line.toLowerCase().contains(searchWord)) {
                                    encontrado = true;
                                }
                            }
                        }
                    } else {
                        System.out.println("La palabra \"" + searchWord + "\" no se encontró en la página: " + pagina);
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e);
            }
            if (seccion9.size() > 0) {
                separadoPorComas = String.join(",", seccion9);
                System.out.println("CADENA: " + separadoPorComas);
                name = separadoPorComas;
            }
            if (name.toLowerCase().contains("no se pudo encontrar")) {
                System.out.println("ENTRA O AQUI O NO? SECCION9??");
                name = leerTXT9(file);
            }

            System.out.println("->" + name);
            result.add(name);
        }

        return result;
    }
    
    public static ArrayList<String> leerPDFs9sArray(ArrayList<File> files) {
        ArrayList<String> result = new ArrayList<>();
        String[] searchWords = {"seccion 9","9.1","propiedades fisicas","physical and chemical properties"};
        for (File file : files) {
            String name = "No se pudo encontrar";
            String separadoPorComas = "";
            ArrayList<String> seccion9 = new ArrayList<>();
            System.out.println("ESTAMOS: " + file.getName());
            try (PDDocument document = PDDocument.load(file)) {
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);

                int currentPage = 0;
                int pagina = 0;
                boolean wordsFound = false;

                // Recorre cada página del documento
                while (currentPage < document.getNumberOfPages() && !wordsFound) {
                    currentPage++;
                    pagina++;
                    stripper.setStartPage(currentPage);
                    stripper.setEndPage(currentPage);

                    String text = stripper.getText(document);
                    text = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}", "");

                    // Busca las palabras en el texto
                    for (String searchWord : searchWords) {
                        if (text.toLowerCase().contains(searchWord)) {

                            stripper.setStartPage(currentPage);
                            stripper.setEndPage(currentPage + 2);
                            text = stripper.getText(document);
                            text = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
                            wordsFound = true;

                            System.out.println("La palabra \"" + searchWord + "\" se encuentra en la página " + currentPage + " del documento.");

                            try (BufferedReader br = new BufferedReader(new StringReader(text))) {
                                String line;
                                boolean encontrado = false;

                                while ((line = br.readLine()) != null) {
                                    if (encontrado) {
                                        seccion9.add(line);

                                        if (line.toLowerCase().contains("seccion 10") || line.toLowerCase().contains("10.") ) {
                                            seccion9.remove(seccion9.size() - 1);
                                            break;
                                        }
                                    } else if (line.toLowerCase().contains(searchWord)) {
                                        encontrado = true;
                                    }
                                }
                            }
                        } else {
                            System.out.println("La palabra \"" + searchWord + "\" no se encontró en la página: " + pagina);
                        }
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e);
            }
            if (seccion9.size() > 0) {
                separadoPorComas = String.join(",", seccion9);
                System.out.println("CADENA: " + separadoPorComas);
                name = separadoPorComas;
            }
            if (name.toLowerCase().contains("no se pudo encontrar")) {
                System.out.println("ENTRA O AQUI O NO? SECCION9??");
                name = leerTXT9Array(file,searchWords);
            }

            System.out.println("->" + name);
            result.add(name);
        }

        return result;
    }
    //pdf formulario -> word
    public static String setWordCopy(ArrayList<String> data,File path,String fileName) throws URISyntaxException {
    	String file2 = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath() + "/wordInterno.docx";
        String result = "Datos guardados correctamente";
    	try(XWPFDocument documento = new XWPFDocument(new FileInputStream(file2))) {
        	
            // Obtener la primera tabla del documento
            XWPFTable tabla = documento.getTables().get(0);
            
            // Modificar los datos de la tabla
            XWPFTableCell celda = tabla.getRow(1).getCell(2);
            celda.removeParagraph(0);
            XWPFParagraph nuevoParrafo = celda.addParagraph();
            XWPFRun run = nuevoParrafo.createRun();
            run.setText("Formaciones para "+data.get(0));
            run.setFontFamily("Arial");
            
            
            tabla.getRow(2).getCell(2).removeParagraph(0);
            tabla.getRow(2).getCell(2).setText("\n Responsable: "+data.get(0));
            
            tabla.getRow(3).getCell(2).removeParagraph(0);
            tabla.getRow(3).getCell(2).setText("\n Responsable: "+data.get(1));
            if(!fileName.contains(".docx")) {
            	fileName+=".docx";
            }
            File archivo = new File(path+"\\"+fileName);
            if(archivo.exists()) {
                System.out.println("El archivo de salida ya existe");
                result="El archivo ya existe, prueba con un nuevo nombre";
            } else {
                System.out.println("El archivo de salida no existe");
                FileOutputStream archivoSalida = new FileOutputStream(archivo);
                documento.write(archivoSalida);
                archivoSalida.close();
            }

        } catch (Exception err) {
            err.printStackTrace();
            result = err.getMessage();
        }
    	return result;
    }
    //COMPROBAR SI EXISTEN O ES FORMULARIO!!!!!
    public static ArrayList<String> getDataPDF(File file){
    	 ArrayList<String> result = new ArrayList<>();
    	try {
			PDDocument doc = PDDocument.load(file);
			if(doc.getDocumentCatalog().getAcroForm()==null) {
				result.add("El archivo no es un formulario válido");
			}else {
	            PDAcroForm acroForm = doc.getDocumentCatalog().getAcroForm();

	            PDField fnombreApellido = acroForm.getField("nombreApellido");
	            String value = fnombreApellido.getValueAsString();
	            result.add(value);
	            PDField fnombreEmpresa = acroForm.getField("nombreEmpresa1");
	            String value02 = fnombreEmpresa.getValueAsString();
	            result.add(value02);
			}

			doc.close();
		} catch (IOException e1) {
			result.add(e1.getMessage());
		}
    	return result;
    	
    }
  
    public static ArrayList<String> getDataPDF105Empresa(File file){
   	 ArrayList<String> result = new ArrayList<>();
   	 System.out.println(file.getName());
   	try {
			PDDocument doc = PDDocument.load(file);
			if(doc.getDocumentCatalog().getAcroForm()==null) {
				result.add("El archivo no es un formulario válido");
			}else {
	            PDAcroForm acroForm = doc.getDocumentCatalog().getAcroForm();

	            PDField fnombreEmpresa = acroForm.getField("nombreEmpresa");
	            String nombreEmpresa = fnombreEmpresa.getValueAsString();
	            result.add(nombreEmpresa);
	            PDField fCif = acroForm.getField("cif");
	            String cif = fCif.getValueAsString();
	            result.add(cif);
	            
	            PDField fdia = acroForm.getField("dia");
	            String dia = fdia.getValueAsString();
	           
	            PDField fmes = acroForm.getField("mes");
	            String mes = fmes.getValueAsString();
	           
	            PDField fanyo = acroForm.getField("anyo");
	            String anyo = fanyo.getValueAsString();
	            String fecha=dia+"/"+mes+"/"+anyo;
	            result.add(fecha);
	           
			}

			doc.close();
		} catch (IOException e1) {
			result.add(e1.getMessage());
		}
   	return result;
   	
   }
    
    public static String exportData105toExcel(File file,ArrayList<String> data, int lastPos) {
        long startTime = System.currentTimeMillis(); // Inicio del contador de tiempo
        String msg="No se han podido añadir";
        try {

            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
            Sheet sheet = workbook.getSheetAt(0); 

            // Obtener la última fila con datos
            int lastRowNum = lastPos + 1;


	         // Escribir los datos en las celdas correspondientes
	        Row newRow = sheet.getRow(lastRowNum);
            CellStyle estilo = workbook.createCellStyle();
            estilo.setAlignment(HorizontalAlignment.CENTER);
            
	        Cell cell1 = newRow.createCell(0);
	        cell1.setCellStyle(estilo);
	        Cell cell2 = newRow.createCell(1);
	        cell2.setCellStyle(estilo);
	        Cell cell3 = newRow.createCell(3);
	        cell3.setCellStyle(estilo);
	       
            cell1.setCellValue(data.get(0));
            cell2.setCellValue(data.get(1));
            cell3.setCellValue(data.get(2));
         
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();

            msg = "Datos añadidos";
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis(); 
        long totalTime = endTime - startTime; 
        msg += " (Tiempo de ejecución: " + totalTime + " ms)"; 
        System.out.println(msg);
    	
    	
		return msg;
    }
    
    public static Map<String, ArrayList<String>> getFormulario(ArrayList<File> files){

	   	Map<String, ArrayList<String>> result = new HashMap<>();
	    ArrayList<String> names = new ArrayList<>();
	    ArrayList<String> dates = new ArrayList<>();
	    ArrayList<String> fileNames = new ArrayList<>();
	
		for(File file: files) {
		   	 try {
				PDDocument doc = PDDocument.load(file);
				if(doc.getDocumentCatalog().getAcroForm()==null) {
					names.add("El archivo no es un formulario válido");
				}else {
			        PDAcroForm acroForm = doc.getDocumentCatalog().getAcroForm();
			        PDField fnombreApellido = acroForm.getField("nombreApellidos");
			        if(fnombreApellido==null) {
			        	names.add("Campo no encontrado");
			        }else {
			        	String value = fnombreApellido.getValueAsString();
				        names.add(value);
			        }
			        
			        PDField fnombreEmpresa = acroForm.getField("fecha");
			        if(fnombreEmpresa==null) {
			        	dates.add("Campo no encontrado");
			        }else {
			        	String value = fnombreEmpresa.getValueAsString();
				        dates.add(value);
			        }

			        fileNames.add(file.getName());

						
				}
					doc.close();
				} catch (IOException e1) {
					names.add(e1.getMessage());
				}
	   		}
			result.put("names", names);
			result.put("dates", dates);
			result.put("fileNames", fileNames);
			
	   	return result;
   	
   }
    //Metodos para combustibles
 	public static ArrayList<String[]> buscarCombustibleFinal(ArrayList<File> files) {

		boolean noLectura = false;
		
	    ArrayList<String[]> resultados = new ArrayList<String[]>();
	        try {
	        	ConfigManager cm = new ConfigManager();
	        	String filtro = cm.getFiltro();
	        	String[] palabras = filtro.split(",");
	        	
	 		   for (File archivo : files) {
	 		        try (PDDocument document = PDDocument.load(archivo)) {

	 		                PDFTextStripper stripper = new PDFTextStripper();
	 		                String texto = stripper.getText(document);
	 		                texto = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
	 		                System.out.println(texto);
	 		                if(texto.trim().isEmpty()) {
	 		                	System.out.println("Entra?");
	 		                	noLectura=true;
	 		                }
	 		                boolean encontrado = false;
	 		                for (String palabra : palabras) {
	 		                    if (texto.toLowerCase().contains(palabra.toLowerCase())) {
	 		                        String[] resultado = new String[2];
	 		                        resultado[0] = archivo.getPath();
	 		                        resultado[1] = palabra;
	 		                        resultados.add(resultado);
	 		                        encontrado = true;
	 		                        break;
	 		                    }
	 		                }
	 		                if (!encontrado) {
	 		                	String newSearch = buscarCombustibleTxt(archivo);
	 		                	if(newSearch.contains("No se encontró")&&noLectura==true) {
	 		                		String[] resultado = new String[2];
	 			                    resultado[0] = archivo.getPath();
	 			                    resultado[1] = "Formato no aceptado, tal vez sea una imagen";
	 			                    noLectura=false;
	 			                    resultados.add(resultado);
	 		                	}else {
	 		                		String[] resultado = new String[2];
	 			                    resultado[0] = archivo.getPath();
	 			                    resultado[1] = newSearch;
	 			                    resultados.add(resultado);
	 		                	}


	 		                }
	 		            
	 		        } catch (IOException e) {
	 		            e.printStackTrace();
	 		        }
	 		    }
	        } catch (IOException | URISyntaxException e1) {
	        		e1.printStackTrace();
	        }
		   



	    return resultados;
	}
	
	public static String buscarCombustibleTxt(File file) {
		String result="No se encontró";
		try {
        	ConfigManager cm = new ConfigManager();
        	String filtro = cm.getFiltro();
        	String[] palabras = filtro.split(",");

	    
	    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	        	line = Normalizer.normalize(line, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
	            for (String palabra : palabras) {
	                if (line.toLowerCase().contains(palabra.toLowerCase())) {
	                    result = palabra;
	                }
	            }
	        }
	    } catch (IOException e) {
	       
	        e.getMessage();
	        System.out.println(e.getMessage());
	    }
        } catch (IOException | URISyntaxException e1) {
    		e1.printStackTrace();
    }
	    return result;
	}
	
	public static boolean guardarCopiaPDF(String origen, String destino,int i) {
		boolean exito=true;
		Path rutaOrigen;
		Path rutaDestino;
		switch (i) {
	    case 1:
		    rutaOrigen = Paths.get(origen);
		    destino += "/" + "copia-"+rutaOrigen.getFileName();

		    rutaDestino = Paths.get(destino);
		    
		    try {
		        Files.copy(rutaOrigen, rutaDestino);
		        System.out.println("Archivo copiado correctamente.");

		    } catch (IOException e) {
		        e.printStackTrace();
		        exito = false;
		    }	
		    
	        break;
	    case 2:
		    rutaOrigen = Paths.get(origen);
		    destino += "/NoCombustibles/" + "copia-"+rutaOrigen.getFileName();

		    rutaDestino = Paths.get(destino);
		    Path rutaCarpetaDestino = rutaDestino.getParent();

		    // Verificar si la carpeta destino no existe y crearla si es necesario
		    if (!Files.exists(rutaCarpetaDestino)) {
		        try {
		            Files.createDirectories(rutaCarpetaDestino);
		            System.out.println("Carpeta creada correctamente.");
		        } catch (IOException e) {
		            e.printStackTrace();
		            exito = false;
		        }
		    }

		    try {
		        // Copiar el archivo
		        Files.copy(rutaOrigen, rutaDestino);
		        System.out.println("Archivo copiado correctamente.");

		    } catch (IOException e) {
		        e.printStackTrace();
		        exito = false;
		    }	
	        break;
	    case 3:
		    rutaOrigen = Paths.get(origen);
		    destino += "/NoLeidos/" + "copia-"+rutaOrigen.getFileName();

		    rutaDestino = Paths.get(destino);
		    System.out.println("destino: " + rutaDestino);

		    // Obtener la ruta de la carpeta destino
		    Path rutaCarpetaDestino2 = rutaDestino.getParent();

		    // Verificar si la carpeta destino no existe y crearla si es necesario
		    if (!Files.exists(rutaCarpetaDestino2)) {
		        try {
		            Files.createDirectories(rutaCarpetaDestino2);
		            System.out.println("Carpeta creada correctamente.");
		        } catch (IOException e) {
		            e.printStackTrace();
		            exito = false;
		        }
		    }

		    try {
		        // Copiar el archivo
		        Files.copy(rutaOrigen, rutaDestino);
		        System.out.println("Archivo copiado correctamente.");

		    } catch (IOException e) {
		        e.printStackTrace();
		        exito = false;
		    }	
	        break;
	    default:
	        System.out.println("fallo");
	}

	

	   return exito;
	}

}
