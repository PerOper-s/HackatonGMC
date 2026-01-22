package dao;

import model.Problema;

import java.util.List;

/**
 * DAO per la gestione dei problemi pubblicati dai giudici per un hackathon.
 * <p>
 * Un hackathon può avere uno o più "problemi" pubblicati (es. Problema 1, Problema 2...),
 * che poi vengono mostrati in dashboard.
 *
 * @author Gruppo ...
 * @version 1.0
 * @see model.Problema
 */

public interface ProblemaDAO {


    /**
     * Recupera un problema associato ad un hackathon (se presente).
     * <p>
     * Se nel DB esistono più problemi per lo stesso hackathon, questa chiamata ritorna
     * quello "principale" secondo la logica dell'implementazione (es. l'ultimo inserito).
     *
     * @param hackathonId id dell'hackathon
     * @return un problema trovato, oppure {@code null} se non esiste nulla
     */


    Problema trovaPerHackathon(long hackathonId);

    /**
     * Pubblica (salva) un nuovo problema per un hackathon.
     *
     * @param hackathonId id dell'hackathon
     * @param giudiceEmail email del giudice che pubblica
     * @param descrizione testo/descrizione del problema
     */


    void pubblicaProblema(long hackathonId, String giudiceEmail, String descrizione);

    /**
     * Recupera tutti i problemi pubblicati per un hackathon (es. Problema 1, Problema 2, ...).
     *
     * @param hackathonId id dell'hackathon
     * @return lista problemi (vuota se non ce ne sono)
     */


    List<Problema> trovaTuttiPerHackathon(long hackathonId);



}
