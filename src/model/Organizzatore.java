package model;

public class Organizzatore extends Utente {

    public Organizzatore(String mail) {
        super(mail);
    }

    // Crea un nuovo Hackathon
    public Hackathon creaHackathon(String titolo, String sede, int maxPartecipanti, int maxGrandezzaTeam, String inizio, String inizioIscrizioni, String fineIscrizioni) {
        Hackathon hack = new Hackathon(titolo, sede, this, maxPartecipanti, maxGrandezzaTeam, inizio, inizioIscrizioni, fineIscrizioni);
        System.out.println("Hackathon \"" + hack.getTitolo() + "\" creato con successo.");
        return hack;
    }

    // Invita i giudici
    public void invitaGiudici() {
        // In un caso reale si inviterebbero giudici specifici; qui stampa un messaggio di esempio
        System.out.println("Giudici invitati all'hackathon.");
    }

    // Invita uno specifico giudice a un determinato hackathon
    public void invitaGiudici(Giudice giudice, Hackathon hackathon) {
        hackathon.aggiungiGiudice(giudice);
        giudice.partecipaHackathon(hackathon);
        System.out.println("Giudice " + giudice.getMail() + " invitato all'hackathon \"" + hackathon.getTitolo() + "\"");
    }
}
