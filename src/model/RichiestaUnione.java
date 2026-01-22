package model;


/**
 * Richiesta di unione tra team (funzionalità opzionale del modello).
 * <p>
 * Rappresenta una richiesta inviata da un team ad un altro team per unirsi.
 * In questa versione serve soprattutto come supporto al dominio e/o per possibili estensioni.
 *
 * @author Gruppo ...
 * @version 1.0
 * @see model.Team
 */


public class RichiestaUnione {
    private Utente daUtente;
    private Team aTeam;
    private String messaggio;



    /**
     * Crea una richiesta di unione tra due team.
     *
     * @param daUtente Utente che invia la richiesta
     * @param aTeam team che riceve la richiesta
     */


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
