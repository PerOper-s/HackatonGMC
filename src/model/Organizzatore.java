package model;


/**
 * Utente con ruolo di organizzatore.
 * <p>
 * L'organizzatore può creare hackathon e invitare giudici.
 * In questo modello alcune operazioni stampano anche messaggi in console (uso test/demo).
 *
 * @author Gruppo ...
 * @version 1.0
 * @see model.Utente
 * @see model.Hackathon
 * @see model.Giudice
 */
public class Organizzatore extends Utente {



    /**
     * Crea un organizzatore identificato dalla sua email.
     *
     * @param mail email dell'organizzatore
     */


    public Organizzatore(String mail) {
        super(mail);
    }

    /**
     * Crea un nuovo hackathon (lato modello) associandolo a questo organizzatore.
     * <p>
     * Qui ritorno l'oggetto {@link model.Hackathon} appena creato.
     * Nota: in questa classe viene anche stampato un messaggio su console.
     *
     * @param titolo titolo dell'hackathon
     * @param sede sede dell'evento
     * @param maxPartecipanti massimo numero di iscritti
     * @param maxGrandezzaTeam dimensione massima del team
     * @param inizio data/ora inizio (stringa)
     * @param inizioIscrizioni data inizio iscrizioni (stringa)
     * @param fineIscrizioni data fine iscrizioni (stringa)
     * @return hackathon creato
     * @see model.Hackathon
     */

    public Hackathon creaHackathon(String titolo, String sede, int maxPartecipanti, int maxGrandezzaTeam, String inizio, String inizioIscrizioni, String fineIscrizioni) {
        Hackathon hack = new Hackathon(titolo, sede, this, maxPartecipanti, maxGrandezzaTeam, inizio, inizioIscrizioni, fineIscrizioni);
        System.out.println("Hackathon \"" + hack.getTitolo() + "\" creato con successo.");
        return hack;
    }


    /**
     * Invita un giudice ad un hackathon (lato modello).
     * <p>
     * Aggiunge il giudice alla lista dell'hackathon e registra l'hackathon tra quelli del giudice.
     * In questa versione stampa anche una riga su console.
     *
     * @param hackathon hackathon su cui invitare il giudice
     * @param giudice giudice invitato
     * @see model.Hackathon#aggiungiGiudice(Giudice)
     * @see model.Giudice#partecipaHackathon(Hackathon)
     */

    public void invitaGiudici(Giudice giudice, Hackathon hackathon) {
        hackathon.aggiungiGiudice(giudice);
        giudice.partecipaHackathon(hackathon);
        System.out.println("Giudice " + giudice.getMail() + " invitato all'hackathon \"" + hackathon.getTitolo() + "\"");
    }
}
