package model;

/**
 * Oggetto "info" per mostrare un commento in UI.
 * <p>
 * Contiene solo i dati utili alla visualizzazione:
 * email del giudice + testo del commento.
 * Esempio formato UI: "Giudice email: commento".
 *
 * @author Gruppo ...
 * @version 1.0
 */


public class CommentoInfo {
    private final String giudiceEmail;
    private final String contenuto;


    /**
     * Oggetto "info" per mostrare un commento in UI.
     * <p>
     * Contiene solo i dati utili alla visualizzazione:
     * email del giudice + testo del commento.
     * Esempio formato UI: "Giudice email: commento".
     *
     * @author Gruppo ...
     * @version 1.0
     */

    public CommentoInfo(String giudiceEmail, String contenuto) {
        this.giudiceEmail = giudiceEmail;
        this.contenuto = contenuto;
    }

    /** @return email del giudice che ha scritto il commento */

    public String getGiudiceEmail() {
        return giudiceEmail;
    }

    /** @return contenuto del commento */

    public String getContenuto() {
        return contenuto;
    }
}
