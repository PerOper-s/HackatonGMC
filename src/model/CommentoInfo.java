package model;

public class CommentoInfo {
    private final String giudiceEmail;
    private final String contenuto;

    public CommentoInfo(String giudiceEmail, String contenuto) {
        this.giudiceEmail = giudiceEmail;
        this.contenuto = contenuto;
    }

    public String getGiudiceEmail() {
        return giudiceEmail;
    }

    public String getContenuto() {
        return contenuto;
    }
}
