package model;

public class Utente {
    private String mail;
    private boolean registrato;

    public Utente(String mail) {
        this.mail = mail;
        this.registrato = false;
    }

    public String getMail() {
        return mail;
    }

    public boolean isRegistrato() {
        return registrato;
    }

    // Metodo per registrare l'utente al sistema (imposta lo stato a registrato)
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
