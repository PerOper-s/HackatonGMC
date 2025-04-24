package model;

public class RichiestaUnione {
    private Utente daUtente;
    private Team aTeam;
    private String messaggio;

    public RichiestaUnione(Utente daUtente, Team aTeam, String messaggio) {
        this.daUtente = daUtente;
        this.aTeam = aTeam;
        this.messaggio = messaggio;
    }

    public void accetta() {
        if (aTeam != null && daUtente != null) {
            if (!aTeam.èPieno()) {
                aTeam.aggiungiMembro(daUtente);
                System.out.println("Richiesta accettata: " + daUtente.getMail() + " ora fa parte del team.");
            } else {
                System.out.println("Richiesta non accettata: il team è pieno.");
            }
        }
    }

    public void rifiuta() {
        System.out.println("Richiesta da " + (daUtente != null ? daUtente.getMail() : "utente") + " rifiutata.");
    }
}
