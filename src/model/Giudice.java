package model;

import java.util.ArrayList;
import java.util.List;

public class Giudice extends Utente {
    // Un giudice può essere invitato a più hackathon
    private List<Hackathon> hackathonInvitations;

    public Giudice(String mail) {
        super(mail);
        this.hackathonInvitations = new ArrayList<>();
    }

    // Il giudice accetta l'invito a un hackathon (aggiunge l'hackathon alla sua lista)
    public void partecipaHackathon(Hackathon hackathon) {
        if (!hackathonInvitations.contains(hackathon)) {
            hackathonInvitations.add(hackathon);
        }
    }

    // Pubblica un problema (versione senza parametri, come da UML)
    public void pubblicaProblema() {
        if (!hackathonInvitations.isEmpty()) {
            // Pubblica un problema predefinito sul primo hackathon disponibile
            Hackathon hack = hackathonInvitations.get(0);
            Problema problema = new Problema("Problema di prova", this);
            hack.aggiungiProblema(problema);
            System.out.println("Giudice " + this.getMail() + " ha pubblicato un problema nell'hackathon \"" + hack.getTitolo() + "\"");
        } else {
            System.out.println("Nessun hackathon disponibile per pubblicare un problema.");
        }
    }

    // Pubblica un problema specificando descrizione e hackathon
    public void pubblicaProblema(String descrizione, Hackathon hackathon) {
        Problema problema = new Problema(descrizione, this);
        hackathon.aggiungiProblema(problema);
        System.out.println("Giudice " + this.getMail() + " ha pubblicato il problema: \"" + descrizione + "\" nell'hackathon \"" + hackathon.getTitolo() + "\"");
    }

    // Commenta i progressi di un team
    public void commentaProgresso(Team team, String messaggio) {
        // Per semplicità, stampiamo solo un messaggio che indica il commento
        System.out.println("Giudice " + this.getMail() + " commenta i progressi del team: " + messaggio);
    }

    // Assegna un voto (versione senza specificare il team, come da UML)
    public void assegnaVoto(int voto) {
        System.out.println("Giudice " + this.getMail() + " assegna un voto: " + voto);
    }

    // Assegna un voto finale a uno specifico team
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
