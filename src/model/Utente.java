package model;


/**
 * Utente base della piattaforma, identificato tramite email.
 * <p>
 * È la super-classe dei ruoli specializzati (giudice e organizzatore).
 * Nel progetto l'email è anche la chiave unica nel database.
 *
 * @author Gruppo ...
 * @version 1.0
 * @see model.Giudice
 * @see model.Organizzatore
 */

public class Utente {
    private String mail;
    private boolean registrato;


    /**
     * Crea un utente identificato dalla sua email.
     *
     * @param mail email dell'utente
     */

    public Utente(String mail) {
        this.mail = mail;
        this.registrato = false;
    }

    /**
     * @return email dell'utente
     */


    public String getMail() {
        return mail;
    }

    public boolean isRegistrato() {
        return registrato;
    }


    public void registrati() {
        if (!registrato) {
            registrato = true;
            System.out.println(mail + " registrato con successo.");
        } else {
            System.out.println(mail + " era già registrato.");
        }
    }

    // Invia una richiesta di unione a un team (join request)
    public RichiestaUnione inviaRichiestaUnione(Team team, String messaggio) {
        RichiestaUnione richiesta = new RichiestaUnione(this, team, messaggio);
        System.out.println(this.mail + " ha inviato una richiesta di unione al team.");
        return richiesta;
    }
}
