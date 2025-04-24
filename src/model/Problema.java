package model;

public class Problema {
    private String descrizione;
    private String dataPubblicazione;
    private Giudice giudice;

    public Problema(String descrizione, Giudice giudice) {
        this.descrizione = descrizione;
        this.giudice = giudice;
        this.dataPubblicazione = "";
    }

    public Problema(String descrizione, String dataPubblicazione, Giudice giudice) {
        this.descrizione = descrizione;
        this.dataPubblicazione = dataPubblicazione;
        this.giudice = giudice;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getDataPubblicazione() {
        return dataPubblicazione;
    }

    public Giudice getGiudice() {
        return giudice;
    }
}
