package application;

public class pdfData {
	
    private String name;
    private String frasesH;
    private String seccion9;
    private String fileName;

    public pdfData(String name, String frasesH, String seccion9,String fileName) {
        this.name = name;
        this.frasesH = frasesH;
        this.seccion9 = seccion9;
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }
    public void setName(String n){
 
        this.name = n;
    }
    public String getFrasesH() {
        return frasesH;
    }
    public void setFrasesH(String n){
    	 
        this.frasesH = n;
    }
	public String getSeccion9() {
		return seccion9;
	}
	public void setSeccion9(String seccion9) {
		this.seccion9 = seccion9;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
    

}
