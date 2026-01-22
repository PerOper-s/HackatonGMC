package dao;


/**
 * DAO per la gestione degli utenti della piattaforma.
 * <p>
 * Qui tengo le operazioni base legate all'identità dell'utente (registrazione e verifica esistenza).
 * Nel progetto l'utente è identificato principalmente tramite email.
 *
 * @author Gruppo ...
 * @version 1.0
 * @see model.Utente
 */

public interface UtenteDAO {

    /**
     * Controlla se esiste un utente con una certa email.
     *
     * @param email email da verificare
     * @return true se l'utente esiste, false altrimenti
     */

    boolean esisteUtente(String email, String ruolo);

    /**
     * Registra un nuovo utente nel database.
     *
     * @param email oggetto utente da salvare
     */

    void registraUtente(String email, String ruolo);
}
