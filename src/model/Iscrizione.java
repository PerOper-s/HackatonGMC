package model;


/**
 * Iscrizione di un utente ad un hackathon.
 * <p>
 * Contiene: data di iscrizione (stringa), utente iscritto e hackathon scelto.
 *
 * @author Gruppo ...
 * @version 1.0
 * @see model.Utente
 * @see model.Hackathon
 */

public class Iscrizione {
    private String data;
    private Utente utente;
    private Hackathon hackathon;


    /**
     * Crea una nuova iscrizione.
     *
     * @param data data dell'iscrizione (stringa)
     * @param utente utente che si iscrive
     * @param hackathon hackathon a cui si iscrive
     */

    public Iscrizione(String data, Utente utente, Hackathon hackathon) {
        this.data = data;
        this.utente = utente;
        this.hackathon = hackathon;
    }

    /** @return data dell'iscrizione (stringa) */

    public String getData() {
        return data;
    }

    /** @return utente iscritto */


    public Utente getUtente() {
        return utente;
    }

    /** @return hackathon a cui l'utente risulta iscritto */


    public Hackathon getHackathon() {
        return hackathon;
    }
}
