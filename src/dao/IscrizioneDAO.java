package dao;

import java.time.LocalDate;


/**
 * DAO per la gestione delle iscrizioni agli hackathon.
 * <p>
 * Qui tengo i metodi minimi per:
 * verificare se un utente è iscritto, contare gli iscritti e registrare una nuova iscrizione.
 *
 * @author Gruppo ...
 * @version 1.0
 * @see java.time.LocalDate
 */

public interface IscrizioneDAO {
    /**
     * Controlla se un utente risulta iscritto ad un hackathon.
     *
     * @param hackathonId id dell'hackathon
     * @param emailUtente email dell'utente
     * @return true se è iscritto, false altrimenti
     */

    boolean isIscritto(long hackathonId, String emailUtente);
    /**
     * Conta quanti utenti sono iscritti ad un hackathon.
     *
     * @param hackathonId id dell'hackathon
     * @return numero iscritti (0 se nessuno)
     */

    int countIscritti(long hackathonId);
    /**
     * Registra l'iscrizione di un utente ad un hackathon.
     *
     * @param hackathonId id dell'hackathon
     * @param emailUtente email dell'utente
     * @param dataIscrizione data in cui avviene l'iscrizione
     */

    void iscrivi(long hackathonId, String emailUtente, LocalDate dataIscrizione);
}
