package dao;

public interface UtenteDAO {
    boolean esisteUtente(String email, String ruolo);
    void registraUtente(String email, String ruolo);
}
