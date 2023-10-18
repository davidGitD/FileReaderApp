package application;
	
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;



public class Main extends Application {
	private Scene scene;
    public static void main(String[] args) {
        launch(args);
    }


	@Override
    public void start(Stage primaryStage) {        	
		Scenes newScene = new Scenes();
		
		//Inicio
		VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(10);
        root.setAlignment(javafx.geometry.Pos.CENTER);
        
        Button filtrarFDS = new Button("Filtrar FDS");
        filtrarFDS.getStyleClass().add("go");
        Button filtrarCombustibles = new Button("Filtrar por combustibles");
        filtrarCombustibles.getStyleClass().add("go");
        Button pdftoWord = new Button("PDF 105 a word/Excel");
        pdftoWord.getStyleClass().add("go");
        Button registroContratos = new Button("Registro contratos firmados");
        registroContratos.getStyleClass().add("go");
        
        filtrarFDS.setOnAction(e -> {
        	primaryStage.setScene(newScene.getScene1(primaryStage,scene));
        });
        filtrarCombustibles.setOnAction(e -> {
        	primaryStage.setScene(newScene.getScene3(primaryStage,scene));
        });
        pdftoWord.setOnAction(e -> {
        	primaryStage.setScene(newScene.createPDFSelectionScene(primaryStage,scene));
        });
        registroContratos.setOnAction(e -> {
        	primaryStage.setScene(newScene.getScene5(primaryStage, scene));
        });
        
        root.getChildren().addAll(filtrarFDS,filtrarCombustibles,pdftoWord,registroContratos);
        root.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        scene = new Scene(root, 350, 300);
        
        primaryStage.getIcons().add(new Image("/R.png"));
        primaryStage.setTitle("Automatización PDF");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

