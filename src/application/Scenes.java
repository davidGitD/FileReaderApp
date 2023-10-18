package application;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Scenes {
	private File excel;
	private File rutaDestino;
	private File word;
	private ArrayList<File> pdfs = new ArrayList<>();
	private ArrayList<String> names = new ArrayList<String>();
	private ArrayList<String> frasesH = new ArrayList<String>();
	private ArrayList<String> seccion9 = new ArrayList<String>();
	private  Scene scene1;
	private Scene pdfSelectionScene;
	private File selectedFile;
	private File selectedFileWord;
	private File selectedFileExcel;
	private File selectedDirectoryWord;
	
	//Escena cambio de fila excel
	public Scene getScene2(Stage primaryStage, Scene scene) {

        VBox vbMenu = new VBox();
        vbMenu.setPadding(new Insets(10));
        vbMenu.setSpacing(10);
        
        Button volver = new Button("volver");
        volver.getStyleClass().add("go");
        String txtLastNum="El número de la proxima fila es: ";
        Label lastNum = new Label(txtLastNum);
        Label cambiarNum = new Label("En que fila quieres que empiece: ");
        TextField campoNumerico = new TextField();
        campoNumerico.setMaxWidth(50);
        //solo permite escribir numeros
        campoNumerico.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        }));
        
    	String error1="";
    	try {
    		ConfigManager configManager = new ConfigManager();
    	    int numLastRow = configManager.getLastRow();
    	    lastNum.setText(txtLastNum+(numLastRow+2));
    	    
    	} catch (IOException | URISyntaxException e1) {
    		e1.printStackTrace();
    		error1+=e1.getMessage();
    	}
    	System.out.println(error1);
    	
        //se guarda el numero
        campoNumerico.setOnAction(event -> {
        	
            String numeroIngresado = campoNumerico.getText();
            Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
            alerta.setTitle("Número ingresado");
            alerta.setHeaderText(null);
            alerta.setContentText("¿Seguro quieres modificarlo? a: " + numeroIngresado);
            Optional<ButtonType> resultado =alerta.showAndWait();
            
            if(resultado.get() == ButtonType.OK) {
            	String error="";
            	try {
            		ConfigManager configManager = new ConfigManager();
            	    int numLastRow = Integer.parseInt(numeroIngresado);
            	    configManager.setLastRow(numLastRow-2);
            	    
            	} catch (IOException | URISyntaxException e1) {
            		e1.printStackTrace();
            		error+=e1.getMessage();
            	}
            	
            	Alert info = new Alert(Alert.AlertType.INFORMATION);
            	info.setTitle("Actualizacion valor fila");
            	info.setHeaderText(null);
            	
            	if(error.length()<1) {
                   
            		info.setContentText("Valor actualizado correctamente");
            		
            	}else {
            		info.setContentText("Error al actualizar el valor "+error);
            	}
            	info.showAndWait();
            }
            
        });
        volver.setOnAction(e -> {
        	primaryStage.setScene(scene);
        });
        //escena 2
        StackPane layout2 = new StackPane();
        vbMenu.getChildren().addAll(lastNum,cambiarNum,campoNumerico,volver);
        vbMenu.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        layout2.getChildren().add(vbMenu);
        Scene scene2 = new Scene(layout2, 400, 400);
		return scene2;
    }
	//Escena principal de filtrado info FDS
	public Scene getScene1(Stage primaryStage, Scene scene) {
		
        VBox root = new VBox();
        HBox btnpack = new HBox();
        HBox confirmPack = new HBox();
       
        btnpack.setSpacing(10);
        btnpack.setAlignment(Pos.BASELINE_CENTER);
        
        confirmPack.setSpacing(10);
        confirmPack.setAlignment(Pos.BASELINE_LEFT);
        
        
        root.setPadding(new Insets(10));
        root.setSpacing(10);
        

        
        // Crear botón para seleccionar archivo
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo PDF");
        
        
        String textolb = "Archivo seleccionado: ";
        
        Button selectpdf = new Button("Seleccionar PDF");
        selectpdf.getStyleClass().add("selectpdf");
        Button selectExcel = new Button("Seleccionar Excel");
        selectExcel.getStyleClass().add("excel-btn");
        
        Button GO = new Button("GO");
        GO.getStyleClass().add("go");
        Button confirm = new Button("CONFIRM");
        confirm.setVisible(false);
        confirm.getStyleClass().add("go");
        Button link = new Button("Número de fila");
        link.getStyleClass().add("link");
        Button volver = new Button("volver");
        volver.getStyleClass().add("go");
        Button stop = new Button("stop");
        stop.getStyleClass().add("stop-btn");
        stop.setVisible(false);
        Button atras = new Button("Volver");
        atras.getStyleClass().add("volver");
        
        Label nameArchivoPdf = new Label(textolb);
        Label nameArchivoExcel = new Label(textolb);
        TextArea lbAdvertencia = new TextArea();
        lbAdvertencia.setMinHeight(40);
        lbAdvertencia.setWrapText(true);
        lbAdvertencia.setEditable(false);
        lbAdvertencia.setVisible(false);
        
        
        // Crear tabla
        TableView<pdfData> table = new TableView<>();
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        
        // Crear columnas
        TableColumn<pdfData, String> fileNameCol = new TableColumn<>("Archivo");
        fileNameCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        fileNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        
        TableColumn<pdfData, String> nameCol = new TableColumn<>("Nombre");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        
        TableColumn<pdfData, String> frasesHCol = new TableColumn<>("Frases H");
        frasesHCol.setCellValueFactory(new PropertyValueFactory<>("frasesH"));
        frasesHCol.setCellFactory(TextFieldTableCell.forTableColumn());
        
        TableColumn<pdfData, String> seccion9Col = new TableColumn<>("Sección 9");
        seccion9Col.setCellValueFactory(new PropertyValueFactory<>("seccion9"));
        seccion9Col.setCellFactory(TextFieldTableCell.forTableColumn());
        
        // Añadir columnas a la tabla
        table.getColumns().addAll(fileNameCol,nameCol, frasesHCol,seccion9Col);
        
        nameCol.setOnEditCommit(data -> {
        	
            pdfData p = data.getRowValue();
            p.setName(data.getNewValue());
            names.set(data.getTablePosition().getRow(), data.getNewValue());
            System.out.println(names);

        });
        
        frasesHCol.setOnEditCommit(data -> {

            pdfData p = data.getRowValue();
            p.setFrasesH(data.getNewValue());
            frasesH.set(data.getTablePosition().getRow(), data.getNewValue());
        });
        seccion9Col.setOnEditCommit(data -> {

            pdfData p = data.getRowValue();
            p.setFrasesH(data.getNewValue());
            seccion9.set(data.getTablePosition().getRow(), data.getNewValue());
        });
        

        link.setOnAction(e -> {
        	primaryStage.setScene(getScene2(primaryStage,scene1));
        });
        atras.setOnAction(e -> {
        	primaryStage.setScene(scene);
        });
        
        selectpdf.setOnAction(e -> {
            // Abrir ventana para seleccionar archivos
        	table.getItems().clear();
        	pdfs.clear();
            List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
            System.out.println(files);
            if (files != null && !files.isEmpty()) {
                // Procesar cada archivo PDF seleccionado
                for (File file : files) {
                    if (methods.isPDF(file)) {
   
                        pdfs.add(file);
                    }
                }

                // Actualizar el texto del Label con la cantidad de archivos procesados
                int numFiles = pdfs.size();
                nameArchivoPdf.setStyle("-fx-text-fill: black");
                nameArchivoPdf.setText("Se seleccionaron " + numFiles + " archivos");
            } else {
                nameArchivoPdf.setStyle("-fx-text-fill: red");
                nameArchivoPdf.setText("Elija uno o varios archivos PDF");
            }
        });

        
        selectExcel.setOnAction(e -> {
            // Abrir ventana para seleccionar archivo
            File file = fileChooser.showOpenDialog(primaryStage);
            
            if (file != null) {
                if(methods.isExcel(file)) {
                	excel = file;
                	nameArchivoExcel.setStyle("-fx-text-fill: black");
                	nameArchivoExcel.setText(textolb+file.getName());
                	
                }else {
                	nameArchivoExcel.setStyle("-fx-text-fill: red");
                	nameArchivoExcel.setText(textolb+"Elije un archivo EXCEL");
                	
                }
                
            }
        });
        
        
        confirm.setOnAction(e -> {
        	String lbMsg="";
        	int lastPos=0;	
        	String error="";

        	try {
        		ConfigManager configManager = new ConfigManager();
        	    int lastRow = configManager.getLastRow();

        	    configManager.setLastRow(lastRow+names.size());
        	    lastPos = lastRow;
        	} catch (IOException | URISyntaxException e1) {
        		e1.printStackTrace();
        		error+=e1.getMessage();
        	}
			if(error.length()<1) {
			    MirarFilaThread thread = new MirarFilaThread(excel, names, frasesH,seccion9, lastPos);
        		thread.start();
                try {
                    
                	thread.join();
                	lbMsg="Datos guardados en fila: "+(lastPos+2)+"("+excel.getName()+") ";
                	lbMsg+=thread.done();
            		lbAdvertencia.setText(lbMsg);
            		lbAdvertencia.setVisible(true);

                } catch (InterruptedException err) {
                    err.printStackTrace();
                }
        		

			}else {
				
				lbAdvertencia.setText("Hubo un error: "+error);
				lbAdvertencia.setVisible(true);
			}
        	
        	
        	table.getItems().clear();
        	confirm.setVisible(false);
        });

        GO.setOnAction(e -> {
        	lbAdvertencia.setVisible(false);
        	if(excel !=null && !pdfs.isEmpty()) {
        		if(table.getItems().isEmpty()) {
            		Thread miThread = new Thread(new Runnable() {
            		    @Override
            		    public void run() {

                    		names = methods.leerPDFsArray(pdfs);
            			    frasesH = methods.buscarFrasesH22(pdfs);
            				seccion9 = methods.leerPDFs9sArray(pdfs);

            				for(int i = 0; i<pdfs.size();i++) {
            					table.getItems().addAll(new pdfData(names.get(i),frasesH.get(i),seccion9.get(i),pdfs.get(i).getName()));
            			
            				}
            		        	

            		    }
            		});
            		miThread.start();
                   
        		}
        		confirm.setVisible(true);

                
        	}else if(!pdfs.isEmpty()) {
        		stop.setVisible(true);

        		// crear el hilo y asignarle un nombre
        		Thread miThread = new Thread(new Runnable() {
        		    @Override
        		    public void run() {

        		        names = methods.leerPDFsArray(pdfs);
        		        frasesH = methods.buscarFrasesH22(pdfs);
        		        seccion9 = methods.leerPDFs9sArray(pdfs);

        		        for(int i = 0; i<pdfs.size();i++) {
        		            // agregar los datos a la tabla
        		            table.getItems().addAll(new pdfData(names.get(i),frasesH.get(i),seccion9.get(i),pdfs.get(i).getName()));
        		        }
        		        stop.setVisible(false);	

        		    }
        		});

        		// iniciar el hilo
        		miThread.start();

        		// agregar evento de acción al botón "stop"
        		stop.setOnAction(eve -> {
        		    // establecer la variable booleana en falso para detener el hilo
        		    System.out.println("STOP");
        		});

        	}
        });

        btnpack.getChildren().addAll(selectpdf,selectExcel);
        confirmPack.getChildren().addAll(GO,confirm,stop,atras);
        root.getChildren().addAll(btnpack,nameArchivoPdf,nameArchivoExcel,table,link,confirmPack,lbAdvertencia);
        root.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        
        scene1 = new Scene(root, 850, 425);

        
        
		return scene1;
	}
	//Escena para organizar en combustibles/ No combustibles
	public Scene getScene3(Stage primaryStage, Scene scene) {
        VBox root = new VBox();
        HBox btnpack = new HBox();
        HBox confirmPack = new HBox();
       
        btnpack.setSpacing(10);
        btnpack.setAlignment(Pos.BASELINE_CENTER);
        
        confirmPack.setSpacing(10);
        confirmPack.setAlignment(Pos.BASELINE_LEFT);
        
        
        root.setPadding(new Insets(10));
        root.setSpacing(10);
        

        
        // Crear botón para seleccionar archivo
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo PDF");
        
        
        String textolb = "Archivo seleccionado: ";
        
        Button selectpdf = new Button("Seleccionar PDF");
        selectpdf.getStyleClass().add("selectpdf");
        Button selectDestino = new Button("Seleccionar destino");
        selectDestino.getStyleClass().add("selectdir");
        
        Button GO = new Button("GO");
        GO.getStyleClass().add("go");
        Button link = new Button("Cambiar filtro de búsqueda");
        link.getStyleClass().add("link");
        
        Button volver = new Button("volver");
        volver.getStyleClass().add("go");
        Button stop = new Button("stop");
        stop.getStyleClass().add("stop-btn");
        stop.setVisible(false);
        Button atras = new Button("Volver");
        atras.getStyleClass().add("volver");
        
        Label nameArchivoPdf = new Label(textolb);
        Label directorio = new Label("Directorio seleccionado: ");
        TextArea lbAdvertencia = new TextArea();
        lbAdvertencia.setMinHeight(-1);
        lbAdvertencia.setWrapText(true);
        lbAdvertencia.setEditable(false);
        lbAdvertencia.setVisible(false);
        

 	    
        TextArea palabrasFiltro = new TextArea();
        palabrasFiltro.setMinHeight(-1);
        palabrasFiltro.setWrapText(true);
        palabrasFiltro.setVisible(false);
        palabrasFiltro.setManaged(false);
        Button save = new Button("Save");
        save.getStyleClass().add("go");
        save.setVisible(false);

        if(selectedFile!=null) {
    	    String path = selectedFile.getPath();
    	    directorio.setText("Directorio seleccionado: "+path);
        }
        selectDestino.setOnAction(e -> {
            // Abrir ventana para seleccionar archivo
        	JFileChooser fileChooser2 = new JFileChooser();
        	fileChooser2.setDialogTitle("Seleccione la carpeta de destino");
        	fileChooser2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        	int result = fileChooser2.showSaveDialog(null);
        	
        	if (result == JFileChooser.APPROVE_OPTION) {
        	    selectedFile = fileChooser2.getSelectedFile();
        	    String path = selectedFile.getPath();
        	    System.out.println("Ruta seleccionada: " + path);
        	    directorio.setText("Directorio seleccionado: "+path);
        	}
        });
        selectpdf.setOnAction(e -> {
            // Abrir ventana para seleccionar archivos
        	lbAdvertencia.clear();
        	pdfs.clear();
            List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
            System.out.println(files);
            if (files != null && !files.isEmpty()) {
                // Procesar cada archivo PDF seleccionado
                for (File file : files) {
                    if (methods.isPDF(file)) {
   
                        pdfs.add(file);
                    }
                }

                // Actualizar el texto del Label con la cantidad de archivos procesados
                int numFiles = pdfs.size();
                nameArchivoPdf.setStyle("-fx-text-fill: black");
                nameArchivoPdf.setText("Se seleccionaron " + numFiles + " archivos");
            } else {
                nameArchivoPdf.setStyle("-fx-text-fill: red");
                nameArchivoPdf.setText("Elija uno o varios archivos PDF");
            }
        });
        link.setOnAction(e->{
            if (palabrasFiltro.isVisible()) {
            	palabrasFiltro.setVisible(false);
            	palabrasFiltro.setManaged(false);
            	save.setVisible(false);
            } else {
                try {
                	ConfigManager configManager = new ConfigManager();
                	String filtro = configManager.getFiltro();
                    palabrasFiltro.setText(filtro);
                	    
                } catch (IOException | URISyntaxException e1) {
                		e1.printStackTrace();
                }
                save.setVisible(true);
                palabrasFiltro.setVisible(true);
                palabrasFiltro.setManaged(true);
            }



        });
        save.setOnAction(e->{
        	String nuevoFiltro = palabrasFiltro.getText();
            try {
            	ConfigManager cm = new ConfigManager();
            	cm.setFiltro(nuevoFiltro);
            	    
            } catch (IOException | URISyntaxException e1) {
            		e1.printStackTrace();
            }
           palabrasFiltro.setVisible(false);
           palabrasFiltro.setManaged(false);
           save.setVisible(false);

         });

        
        GO.setOnAction(e -> {
        	lbAdvertencia.setVisible(false);
        	if(!pdfs.isEmpty() && selectedFile!=null) {
        		String txt="";
        	    for (String[] resultado : methods.buscarCombustibleFinal(pdfs)) {
        	        System.out.println("Se encontró la palabra \"" + resultado[1] + "\" en el archivo \"" + resultado[0] + "\n");
        	        txt+="Se encontró la palabra \"" + resultado[1].toUpperCase() + "\" en el archivo \"" + resultado[0] + "\n";
        	        if(resultado[1].contains("No se encontró")) {
        	        	boolean guardado=methods.guardarCopiaPDF(resultado[0],selectedFile.getPath(),2);
        	        	if(guardado) {
        	        		txt+="Archivo copiado correctamente \n";
        	        	}else {
        	        		txt+="No se pudo copiar, tal vez ya exista \n";
        	        	}
        	        }else if(resultado[1].contains("Formato no aceptado")) {
        	        	boolean guardado=methods.guardarCopiaPDF(resultado[0],selectedFile.getPath(),3);
        	        	if(guardado) {
        	        		txt+="Archivo copiado correctamente \n";
        	        	}else {
        	        		txt+="No se pudo copiar, tal vez ya exista \n";
        	        	}
        	        }else {
        	        	boolean guardado=methods.guardarCopiaPDF(resultado[0],selectedFile.getPath(),1);
        	        	if(guardado) {
        	        		txt+="Archivo copiado correctamente \n";
        	        	}else {
        	        		txt+="No se pudo copiar, tal vez ya exista \n";
        	        	}
        	        }
        	    }
        	    lbAdvertencia.setText(txt);
        	    lbAdvertencia.setVisible(true);
        	}else if(!pdfs.isEmpty()) {
        		String txt="";
        	    for (String[] resultado : methods.buscarCombustibleFinal(pdfs)) {
        	        System.out.println("Se encontró la palabra \"" + resultado[1] + "\" en el archivo \"" + resultado[0] + "\n");
        	        txt+="Se encontró la palabra \"" + resultado[1].toUpperCase() + "\" en el archivo \"" + resultado[0] + "\n";  
        	    }
        	    lbAdvertencia.setText(txt);
        	    lbAdvertencia.setVisible(true);
        	  
        	}
        });
        atras.setOnAction(e -> {
        	primaryStage.setScene(scene);
        });
        btnpack.getChildren().addAll(selectpdf,selectDestino);
        confirmPack.getChildren().addAll(GO,stop,atras);
        root.getChildren().addAll(btnpack,nameArchivoPdf,directorio,lbAdvertencia,confirmPack,link,palabrasFiltro,save);
        root.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        
        scene1 = new Scene(root, 850, 425);

        
        
		return scene1;

	}
	
	public Scene getScene4(Stage primaryStage, Scene scene) {
		//Inicio
		VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(10);

        HBox menu = new HBox();
        menu.setAlignment(javafx.geometry.Pos.CENTER);
        menu.setSpacing(10);
        Button selecWord = new Button("Seleccionar PDF");
        selecWord.getStyleClass().add("go");
        Button selectDestino = new Button("Seleccionar ruta");
        selectDestino.getStyleClass().add("go");
        Button go = new Button("GO");
        go.getStyleClass().add("go");
        Button volver = new Button("volver");
        volver.getStyleClass().add("volver");
        TextField fileName = new TextField();
        fileName.setMaxWidth(250);
        fileName.setPromptText("Nombra el doc FDG106_05.docx");
        
        Label nameFile = new Label("Archivo: ");
        Label namePath = new Label("Ruta: ");
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo");

        selecWord.setOnAction(e -> {
        	word = fileChooser.showOpenDialog(primaryStage);
        	if(word!=null) {
        		nameFile.setText("Archivo: "+word.getName());
        	}
        });
        selectDestino.setOnAction(e -> {
            // Abrir ventana para seleccionar archivo
        	JFileChooser fileChooser2 = new JFileChooser();
        	fileChooser2.setDialogTitle("Seleccione la carpeta de destino");
        	fileChooser2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        	int result = fileChooser2.showSaveDialog(null);
        	
        	if (result == JFileChooser.APPROVE_OPTION) {
        	    rutaDestino = fileChooser2.getSelectedFile();
        	    String path = rutaDestino.getPath();
        	    namePath.setText("Ruta: "+path);
        	}
        });
        go.setOnAction(e->{
        	Alert info = new Alert(Alert.AlertType.INFORMATION);
        	info.setTitle("Generar Word");
        	info.setHeaderText(null);
        	String docName = fileName.getText();
        	
            if (word != null && rutaDestino!= null) {
            	ArrayList<String> data=	methods.getDataPDF(word);
            	String result;

    			try {
    				if(!docName.isEmpty()) {
    					result = methods.setWordCopy(data,rutaDestino,docName);
    					info.setContentText(result);
    				}else {
    					info.setContentText("Asigna un nombre al archivo");
    				}
                	
    			} catch (URISyntaxException e1) {
    				info.setContentText("Error al actualizar el valor "+e1.getMessage());
    				e1.printStackTrace();
    			}
    			info.showAndWait();
            }	
            
            System.out.println(fileName.getText());
        });

        volver.setOnAction(e -> {
        	primaryStage.setScene(scene);
        });
        //escena 2
        StackPane layout = new StackPane();
        menu.getChildren().addAll(selecWord,selectDestino);
        root.getChildren().addAll(menu,nameFile,namePath,fileName,go,volver);
        root.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        layout.getChildren().add(root);
        Scene scene2 = new Scene(layout, 350, 300);
		return scene2;
	}
	
	public Scene createPDFSelectionScene(Stage primaryStage, Scene previousScene) {
	    // Root layout
	    VBox root = new VBox(10);
	    root.setPadding(new Insets(10));

	    // Menu layout
	    HBox menu = new HBox(10);
	    menu.setAlignment(Pos.CENTER);
	    menu.setSpacing(10);
	    Button selectPDFBtn = new Button("Seleccionar PDF");
	    selectPDFBtn.getStyleClass().add("selectpdf");
	    Button selectDestinationBtn = new Button("Seleccionar destino");
	    selectDestinationBtn.getStyleClass().add("selectdir");
	    

	    HBox middleMenu = new HBox(10);
	    middleMenu.setAlignment(Pos.CENTER_LEFT);
	    
	    // Text input fields
	    TextField fileNameField = new TextField();
	    fileNameField.setPromptText("Ingrese el nombre del archivo");
	    fileNameField.setMaxWidth(300);
	    HBox.setHgrow(fileNameField, Priority.ALWAYS);
	    // Labels
	    Label fileNameLabel = new Label("Asigna un nombre:");
	    middleMenu.getChildren().addAll(fileNameLabel, fileNameField);
	    HBox.setMargin(fileNameLabel, new Insets(15, 0, 0, 0));
	    HBox.setMargin(fileNameField, new Insets(15, 0, 0, 0));
	    VBox verticalMenu = new VBox(10);
	    
	    Label destinationPathLabel = new Label("Directorio seleccionado: ");
	    Label fileNameSelected = new Label("Archivo seleccionado: ");
	    verticalMenu.getChildren().addAll(destinationPathLabel,fileNameSelected);
	   
	    HBox checkMenu = new HBox(10);
	    Label checklb = new Label("¿Quieres enviar a excel?");
	    CheckBox cb = new CheckBox();
	    cb.setIndeterminate(false);
	    checkMenu.getChildren().addAll(checklb,cb);
	    

	    Label excelseleccionado  = new Label("Archico excel seleccionado: ");
	    Button selectExcelBtn = new Button("Seleccionar PDF");
	    selectExcelBtn.getStyleClass().add("excel-btn");
	    selectExcelBtn.setVisible(false);
	    selectExcelBtn.setManaged(false);
	    excelseleccionado.setVisible(false);
	    excelseleccionado.setManaged(false);
	    Button link = new Button("¿Cambiar fila del excel?");
	    link.getStyleClass().add("link");
	    link.setVisible(false);
	    link.setManaged(false);
	    
	    cb.setOnAction(e->{
	    	if(cb.isSelected()) {
	    		excelseleccionado.setVisible(true);
	    		selectExcelBtn.setManaged(true);
	    		selectExcelBtn.setVisible(true);
	    		excelseleccionado.setManaged(true);
	    	    link.setVisible(true);
	    	    link.setManaged(true);
	    	}else {
	    		excelseleccionado.setVisible(false);
	    		selectExcelBtn.setManaged(false);
	    		selectExcelBtn.setVisible(false);
	    		excelseleccionado.setManaged(false);
	    	    link.setVisible(false);
	    	    link.setManaged(false);
	    	}
	    	
	    	
	    });
        link.setOnAction(e -> {
        	primaryStage.setScene(getScene2(primaryStage,pdfSelectionScene));
        });

	    menu.getChildren().addAll(selectPDFBtn, selectDestinationBtn,selectExcelBtn);
	    HBox footerMenu = new HBox(10);
	    // Buttons
	    Button generatePDFBtn = new Button("Generar PDF");
	    generatePDFBtn.getStyleClass().add("go");
	    Button backBtn = new Button("Volver");
	    backBtn.getStyleClass().add("volver");
	    footerMenu.getChildren().addAll(generatePDFBtn, backBtn);
	    VBox.setVgrow(footerMenu,javafx.scene.layout.Priority.ALWAYS );
	    footerMenu.setAlignment(Pos.BOTTOM_LEFT);
	    
	    selectExcelBtn.setOnAction(e->{
	        FileChooser fileChooser = new FileChooser();
	        fileChooser.setTitle("Seleccionar archivo Excel");
	        File selectedFilexls = fileChooser.showOpenDialog(primaryStage);
	        
		    if (selectedFilexls != null) {
		        excelseleccionado.setText("Archivo seleccionado: "+selectedFilexls.getName());
		        selectedFileExcel = selectedFilexls;
		    }	        
	        
	    });
	    
	    selectPDFBtn.setOnAction(e -> {
	        FileChooser fileChooser = new FileChooser();
	        fileChooser.setTitle("Seleccionar archivo PDF");
	        //HACERLO TAMBIEN PARA EXCEL
	        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
	        fileChooser.getExtensionFilters().add(extFilter);

	        
	        File selectedFilew = fileChooser.showOpenDialog(primaryStage);

	       if (selectedFilew != null) {
	           System.out.println("Archivo seleccionado: " + selectedFilew.getAbsolutePath());
	           selectedFileWord = selectedFilew;
	           fileNameSelected.setText("Archivo seleccionado: "+selectedFilew.getName());
	       }
	    });

	    
	    selectDestinationBtn.setOnAction(e->{
	        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
	        fileChooser.setDialogTitle("Seleccionar un directorio");
	        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

	        int returnValue = fileChooser.showOpenDialog(null);
	        
        	if (returnValue == JFileChooser.APPROVE_OPTION) {
        		selectedDirectoryWord = fileChooser.getSelectedFile();
        	    String path = selectedDirectoryWord.getPath();
        	    destinationPathLabel.setText("Directorio seleccionado: "+path);
        	}
	    });
	    
	    generatePDFBtn.setOnAction(e->{
	    	int lastPos=0;
        	try {
        		ConfigManager configManager = new ConfigManager();
        	    int lastRow = configManager.getLastRow();

        	    configManager.setLastRow(lastRow+1);
        	    lastPos = lastRow;
        	} catch (IOException | URISyntaxException e1) {
        		e1.printStackTrace();
        	}

	        if(selectedFileWord !=null && selectedDirectoryWord!=null ) {
		        if(cb.isSelected() && selectedFileExcel!=null) {
			        ArrayList<String> result34 = methods.getDataPDF105Empresa(selectedFileWord);
			        String msg = methods.exportData105toExcel(selectedFileExcel, result34, lastPos);
	            	Alert info = new Alert(Alert.AlertType.INFORMATION);
	            	info.setTitle("Alerta");
	            	info.setHeaderText(null);
	            	info.setContentText(msg);
	            	info.showAndWait();
		        }
	    		String fileNameWord = "";
	    		String regex = "^[a-zA-Z0-9_\\-\\.\\s]+$";
	    		Boolean comprobante = true;
	    		if (!fileNameField.getText().matches(regex)) {
	    		    comprobante = false;
	    		    System.out.println("entra");
	    		}
	    			if(fileNameField.getText().isEmpty() || comprobante==false) {
		            	Alert info = new Alert(Alert.AlertType.INFORMATION);
		            	info.setTitle("Alerta");
		            	info.setHeaderText(null);
		            	info.setContentText("Escribe un nombre válido para el documento");
		            	info.showAndWait();
	    			}else {
		    			fileNameWord=fileNameField.getText();
			    		ArrayList<String> data = methods.getDataPDF(selectedFileWord);
			    		try {
							String result = methods.setWordCopy(data, selectedDirectoryWord,fileNameWord);
			            	Alert info = new Alert(Alert.AlertType.INFORMATION);
			            	info.setTitle("Creación documento");
			            	info.setHeaderText(null);
							if(result.equals("Datos guardados correctamente")) {
								info.setContentText(result);
							}else {
								info.setContentText("Selecciona un pdf válido");
							}
							info.showAndWait();
						} catch (URISyntaxException e1) {
							e1.printStackTrace();
						}
			    		fileNameField.clear();
			    		selectedFileWord=null;
			    		selectedDirectoryWord=null;
			    		fileNameSelected.setText("Archivo seleccionado: ");
			    		destinationPathLabel.setText("Directorio seleccionado: ");
	    			}
	    			
	    	}else {
		        if(cb.isSelected() && selectedFileExcel!=null && selectedFileWord !=null) {
			        ArrayList<String> result34 = methods.getDataPDF105Empresa(selectedFileWord);
			        String msg = methods.exportData105toExcel(selectedFileExcel, result34, lastPos);
	            	Alert info = new Alert(Alert.AlertType.INFORMATION);
	            	info.setTitle("Alerta");
	            	info.setHeaderText(null);
	            	info.setContentText(msg);
	            	info.showAndWait();
		        }
	    	}
	    });
	    
        backBtn.setOnAction(e -> {
        	primaryStage.setScene(previousScene);
        });
	    
	    // Add components to root layout
	    root.getChildren().addAll(menu,verticalMenu, middleMenu,checkMenu,excelseleccionado,link,
	    		footerMenu);

	    // Scene and layout
	    StackPane layout = new StackPane();
	    root.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
	    layout.getChildren().add(root);
	    pdfSelectionScene = new Scene(layout, 850, 425);



	    return pdfSelectionScene;
	}

	public Scene getScene5(Stage primaryStage, Scene scene) {
		
        VBox root = new VBox();
        HBox btnpack = new HBox();
        HBox confirmPack = new HBox();
       
        btnpack.setSpacing(10);
        btnpack.setAlignment(Pos.BASELINE_CENTER);
        
        confirmPack.setSpacing(10);
        confirmPack.setAlignment(Pos.BASELINE_LEFT);
        
        
        root.setPadding(new Insets(10));
        root.setSpacing(10);
        

        
        // Crear botón para seleccionar archivo
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo PDF");
        
        
        String textolb = "Archivo seleccionado: ";
        
        Button selectpdf = new Button("Seleccionar PDF");
        selectpdf.getStyleClass().add("selectpdf");
        Button selectExcel = new Button("Seleccionar Excel");
        selectExcel.getStyleClass().add("excel-btn");
        
        Button GO = new Button("GO");
        GO.getStyleClass().add("go");
        Button confirm = new Button("CONFIRM");
        confirm.setVisible(false);
        confirm.getStyleClass().add("go");
        Button link = new Button("Número de fila");
        link.getStyleClass().add("link");
        Button volver = new Button("volver");
        volver.getStyleClass().add("go");
        Button stop = new Button("stop");
        stop.getStyleClass().add("stop-btn");
        stop.setVisible(false);
        Button atras = new Button("Volver");
        atras.getStyleClass().add("volver");
        
        Label nameArchivoPdf = new Label(textolb);
        Label nameArchivoExcel = new Label(textolb);
        TextArea lbAdvertencia = new TextArea();
        lbAdvertencia.setMinHeight(40);
        lbAdvertencia.setWrapText(true);
        lbAdvertencia.setEditable(false);
        lbAdvertencia.setVisible(false);
        
        
        // Crear tabla
        TableView<dataFormulario> table = new TableView<>();
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        
        // Crear columnas
        TableColumn<dataFormulario, String> fileNameCol = new TableColumn<>("Archivo");
        fileNameCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        fileNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        
        TableColumn<dataFormulario, String> nameCol = new TableColumn<>("Nombre y apellidos");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        
        TableColumn<dataFormulario, String> dateCol = new TableColumn<>("Fecha");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setCellFactory(TextFieldTableCell.forTableColumn());
        
        
        // Añadir columnas a la tabla
        table.getColumns().addAll(fileNameCol,nameCol, dateCol);
        
   

        link.setOnAction(e -> {
        	primaryStage.setScene(getScene2(primaryStage,scene1));
        });
        atras.setOnAction(e -> {
        	primaryStage.setScene(scene);
        });
        
        selectpdf.setOnAction(e -> {
            // Abrir ventana para seleccionar archivos
        	table.getItems().clear();
        	pdfs.clear();
            List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
            System.out.println(files);
            if (files != null && !files.isEmpty()) {
                // Procesar cada archivo PDF seleccionado
                for (File file : files) {
                    if (methods.isPDF(file)) {
   
                        pdfs.add(file);
                    }
                }

                // Actualizar el texto del Label con la cantidad de archivos procesados
                int numFiles = pdfs.size();
                nameArchivoPdf.setStyle("-fx-text-fill: black");
                nameArchivoPdf.setText("Se seleccionaron " + numFiles + " archivos");
            } else {
                nameArchivoPdf.setStyle("-fx-text-fill: red");
                nameArchivoPdf.setText("Elija uno o varios archivos PDF");
            }
        });

        
        selectExcel.setOnAction(e -> {
            // Abrir ventana para seleccionar archivo
            File file = fileChooser.showOpenDialog(primaryStage);
            
            if (file != null) {
                if(methods.isExcel(file)) {
                	excel = file;
                	nameArchivoExcel.setStyle("-fx-text-fill: black");
                	nameArchivoExcel.setText(textolb+file.getName());
                	
                }else {
                	nameArchivoExcel.setStyle("-fx-text-fill: red");
                	nameArchivoExcel.setText(textolb+"Elije un archivo EXCEL");
                	
                }
                
            }
        });
        
        
        confirm.setOnAction(e -> {
        	String lbMsg="";
        	int lastPos=0;	
        	String error="";
	    	ArrayList<String> namesgf = methods.getFormulario(pdfs).get("names");
	    	ArrayList<String> fileNames = methods.getFormulario(pdfs).get("fileNames");
	    	ArrayList<String> dates = methods.getFormulario(pdfs).get("dates");
        	try {
        		ConfigManager configManager = new ConfigManager();
        	    int lastRow = configManager.getLastRow();

        	    configManager.setLastRow(lastRow+namesgf.size());
        	    lastPos = lastRow;
        	} catch (IOException | URISyntaxException e1) {
        		e1.printStackTrace();
        		error+=e1.getMessage();
        	}
        	
			if(error.length()<1) {
			    rellenarExcelFormularios thread = new rellenarExcelFormularios(excel, namesgf, fileNames,dates, lastPos);
			    System.out.println(excel.getName());
        		thread.start();
                try {
                    
                	thread.join();
                	lbMsg="Datos guardados en fila: "+(lastPos+2)+"("+excel.getName()+") ";
                	lbMsg+=thread.done();
            		lbAdvertencia.setText(lbMsg);
            		lbAdvertencia.setVisible(true);

                } catch (InterruptedException err) {
                    err.printStackTrace();
                }
        		

			}else {
				
				lbAdvertencia.setText("Hubo un error: "+error);
				lbAdvertencia.setVisible(true);
			}
        	
        	
        	table.getItems().clear();
        	confirm.setVisible(false);
        });

        GO.setOnAction(e -> {
        	lbAdvertencia.setVisible(false);
        	if(excel !=null && !pdfs.isEmpty()) {
        		if(table.getItems().isEmpty()) {
            		Thread miThread = new Thread(new Runnable() {
            		    @Override
            		    public void run() {

            		    	 ArrayList<String> namesgf = methods.getFormulario(pdfs).get("names");
            		    	 ArrayList<String> fileNames = methods.getFormulario(pdfs).get("fileNames");
            		    	 ArrayList<String> dates = methods.getFormulario(pdfs).get("dates");
            				for(int i = 0; i<pdfs.size();i++) {
            					table.getItems().addAll(new dataFormulario(namesgf.get(i),dates.get(i),fileNames.get(i)));
            			
            				}
            		        	

            		    }
            		});
            		miThread.start();
                   
        		}
        		confirm.setVisible(true);

                
        	}else if(!pdfs.isEmpty()) {
        		stop.setVisible(true);

        		// crear el hilo y asignarle un nombre
        		Thread miThread = new Thread(new Runnable() {
        		    @Override
        		    public void run() {

       		    	 ArrayList<String> names = methods.getFormulario(pdfs).get("names");
       		    	 ArrayList<String> fileNames = methods.getFormulario(pdfs).get("fileNames");
       		    	 ArrayList<String> dates = methods.getFormulario(pdfs).get("dates");
       				for(int i = 0; i<pdfs.size();i++) {
       					table.getItems().addAll(new dataFormulario(names.get(i),dates.get(i),fileNames.get(i)));
       			
       				}
        		        stop.setVisible(false);	

        		    }
        		});

        		// iniciar el hilo
        		miThread.start();

        		// agregar evento de acción al botón "stop"
        		stop.setOnAction(eve -> {
        		    // establecer la variable booleana en falso para detener el hilo
        		    System.out.println("STOP");
        		});

        	}
        });

        btnpack.getChildren().addAll(selectpdf,selectExcel);
        confirmPack.getChildren().addAll(GO,confirm,stop,atras);
        root.getChildren().addAll(btnpack,nameArchivoPdf,nameArchivoExcel,table,link,confirmPack,lbAdvertencia);
        root.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        
        scene1 = new Scene(root, 850, 425);

        
        
		return scene1;
	}
	

}
