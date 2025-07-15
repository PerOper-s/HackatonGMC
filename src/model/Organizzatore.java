package model;

public class Organizzatore extends Utente {

    public Organizzatore(String mail) {
        super(mail);
    }

    public Hackathon creaHackathon(String titolo, int maxPartecipanti, int maxGrandezzaTeam, String sede, String inizio, String inizioIscrizioni, String fineIscrizioni) {
        Hackathon hackathon = new Hackathon(titolo, this, maxPartecipanti, maxGrandezzaTeam, sede, inizio, inizioIscrizioni, fineIscrizioni);
        System.out.println("Hackathon \"" + hackathon.getTitolo() + "\" creato con successo");
        return hackathon;
    }

    public void invitaGiudici(Giudice giudice, Hackathon hackathon) {
        hackathon.aggiungiGiudice(giudice);
        giudice.partecipaHackathon(hackathon);
        System.out.println("Giudice " + giudice.getMail() + " invitato all'hackathon \"" + hackathon.getTitolo() + "\"");
    }
}
