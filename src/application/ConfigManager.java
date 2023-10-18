package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

public class ConfigManager {
    private Properties properties;
    String filePath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath() + "/config.properties";
    //String filePath ="C:/Users/A232971/eclipse-workspace/javafx_beta/src/application/resource/config.properties";

    public ConfigManager() throws IOException, URISyntaxException {
        // Cargar el archivo de configuración
    	
        properties = new Properties();
        FileInputStream in = new FileInputStream(filePath);
        properties.load(in);
        in.close();
    }

    public int getLastRow() {
        // Obtener el valor de la clave "lastRow"
        String lastRowStr = properties.getProperty("ultima_fila");
        int lastRow = Integer.parseInt(lastRowStr);
        return lastRow;
    }

    public void setLastRow(int lastRow) throws IOException {
        // Actualizar el valor de la clave "lastRow"

        properties.setProperty("ultima_fila", Integer.toString(lastRow));
        FileOutputStream out = new FileOutputStream(filePath);
        properties.store(out, null);
        out.close();
    }
    
    public String getFiltro() {
        // Obtener el valor de la clave "lastRow"
        String lastRowStr = properties.getProperty("palabras_filtro_busqueda");
        return lastRowStr;
    }

    public void setFiltro(String filtro) throws IOException {
        // Actualizar el valor de la clave "lastRow"

        properties.setProperty("palabras_filtro_busqueda", filtro);
        FileOutputStream out = new FileOutputStream(filePath);
        properties.store(out, null);
        out.close();
    }
}
