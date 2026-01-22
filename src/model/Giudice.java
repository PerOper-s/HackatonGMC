package model;

import java.util.ArrayList;
import java.util.List;


/**
 * Utente con ruolo di giudice.
 * <p>
 * Un giudice può essere invitato a più hackathon e, durante l'evento,
 * può pubblicare problemi, commentare progressi e assegnare voti ai team.
 *
 * @author Gruppo ...
 * @version 1.0
 * @see model.Utente
 * @see model.Hackathon
 * @see model.Team
 */

public class Giudice extends Utente {
    // Un giudice può essere invitato a più hackathon
    private List<Hackathon> hackathonInvitations;

    /**
     * Utente con ruolo di giudice.
     * <p>
     * Un giudice può essere invitato a più hackathon e, durante l'evento,
     * può pubblicare problemi, commentare progressi e assegnare voti ai team.
     *
     * @author Gruppo ...
     * @version 1.0
     * @see model.Utente
     * @see model.Hackathon
     * @see model.Team
     */

    public Giudice(String mail) {
        super(mail);
        this.hackathonInvitations = new ArrayList<>();
    }

    /**
     * Aggiunge un hackathon alla lista degli hackathon a cui il giudice partecipa/è invitato.
     * Evita duplicati.
     *
     * @param hackathon hackathon da aggiungere
     */

    public void partecipaHackathon(Hackathon hackathon) {
        if (!hackathonInvitations.contains(hackathon)) {
            hackathonInvitations.add(hackathon);
        }
    }




    /**
     * Pubblica un problema specifico su un hackathon.
     *
     * @param descrizione testo del problema
     * @param hackathon hackathon su cui pubblicare
     * @see model.Hackathon#aggiungiProblema(Problema)
     */

    public void pubblicaProblema(String descrizione, Hackathon hackathon) {
        Problema problema = new Problema(descrizione, this);
        hackathon.aggiungiProblema(problema);
        System.out.println("Giudice " + this.getMail() + " ha pubblicato il problema: \"" + descrizione + "\" nell'hackathon \"" + hackathon.getTitolo() + "\"");
    }

    /**
     * Commenta i progressi di un team.
     * <p>
     * In questa versione del modello stampa solo un messaggio su console (stub).
     *
     * @param team team commentato
     * @param messaggio testo del commento
     */

    public void commentaProgresso(Team team, String messaggio) {

        System.out.println("Giudice " + this.getMail() + " commenta i progressi del team: " + messaggio);
    }


    public void assegnaVoto(int voto) {
        System.out.println("Giudice " + this.getMail() + " assegna un voto: " + voto);
    }


    public void assegnaVoto(Team team, int voto) {
        team.setVotoFinale(voto);
        // Aggiorna anche la classifica dell'hackathon
        Hackathon hack = team.getHackathon();
        if (hack != null) {
            hack.aggiornaPunteggio(team, voto);
        }
        System.out.println("Giudice " + this.getMail() + " assegna al team un voto finale di " + voto);
    }
}
