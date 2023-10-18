package application;

public class dataFormulario {
	
    private String name;
    private String fileName;
    private String date;

    public dataFormulario(String name, String date, String fileName) {
        this.name = name;
        this.fileName = fileName;
        this.date = date;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
