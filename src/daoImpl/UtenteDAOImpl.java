package daoImpl;

import dao.UtenteDAO;
import database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementazione PostgreSQL del {@link dao.UtenteDAO}.
 * <p>
 * Gestisce la registrazione e la verifica esistenza utenti.
 * Nel progetto l'utente è identificato dalla email (vincolo univoco nel DB).
 *
 * @author Gruppo ...
 * @version 1.0
 * @see dao.UtenteDAO
 * @see model.Utente
 * @see database.Database#getConnection()
 */


public class UtenteDAOImpl implements UtenteDAO {


    /**
     * Controlla se esiste un utente nel database con una certa email.
     *
     * @param email email da cercare
     * @return true se esiste, false altrimenti
     * @throws RuntimeException se c'è un errore SQL durante la query
     */

    @Override
    public boolean esisteUtente(String email, String ruolo) {
        String query = "SELECT * FROM utente WHERE email = ? AND ruolo = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setString(2, ruolo);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Registra un nuovo utente nel database.
     * <p>
     * Se l'email esiste già (vincolo univoco), il DB può lanciare errore.
     * La gestione lato UI decide se ignorare o mostrare messaggio all'utente.
     *
     * @param u utente da registrare
     * @throws RuntimeException se c'è un errore SQL durante l'inserimento
     */

    @Override
    public void registraUtente(String email, String ruolo) {
        String insert = "INSERT INTO utente (email, ruolo) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setString(1, email);
            ps.setString(2, ruolo);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
