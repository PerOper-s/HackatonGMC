package model;

public class Organizzatore extends Utente {

    public Organizzatore(String mail) {
        super(mail);
    }

    // Crea un nuovo Hackathon (con valori di esempio per semplicità)
    public Hackathon creaHackathon() {
        Hackathon hack = new Hackathon("Hackathon di " + this.getMail(), this, 100, 5);
        System.out.println("Hackathon \"" + hack.getTitolo() + "\" creato dall'organizzatore " + this.getMail());
        return hack;
    }

    // Invita i giudici (versione senza parametri, come da UML)
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
