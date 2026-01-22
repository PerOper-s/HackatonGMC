package model;

/**
 * Problema pubblicato da un giudice per un hackathon.
 * <p>
 * Contiene descrizione, data pubblicazione (stringa) e il giudice che lo ha pubblicato.
 *
 * @author Gruppo ...
 * @version 1.0
 * @see model.Giudice
 */


public class Problema {
    private String descrizione;
    private String dataPubblicazione;
    private Giudice giudice;

    /**
     * Crea un problema con descrizione e giudice, lasciando la data pubblicazione vuota.
     *
     * @param descrizione testo del problema
     * @param giudice giudice che pubblica
     */

    public Problema(String descrizione, Giudice giudice) {
        this.descrizione = descrizione;
        this.giudice = giudice;
        this.dataPubblicazione = "";
    }

    /**
     * Crea un problema con descrizione, data pubblicazione e giudice.
     *
     * @param descrizione testo del problema
     * @param dataPubblicazione data di pubblicazione (stringa)
     * @param giudice giudice che pubblica
     */

    public Problema(String descrizione, String dataPubblicazione, Giudice giudice) {
        this.descrizione = descrizione;
        this.dataPubblicazione = dataPubblicazione;
        this.giudice = giudice;
    }

    /** @return descrizione del problema */


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
