package model;

public class Iscrizione {
    private String data;
    private Utente utente;
    private Hackathon hackathon;

    public Iscrizione(String data, Utente utente, Hackathon hackathon) {
        this.data = data;
        this.utente = utente;
        this.hackathon = hackathon;
    }

    public String getData() {
        return data;
    }

    public Utente getUtente() {
        return utente;
    }

    public Hackathon getHackathon() {
        return hackathon;
    }
}
