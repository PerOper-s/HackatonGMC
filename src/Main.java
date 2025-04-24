import model.*;

public class Main {
    public static void main(String[] args) {
        // Creazione di un organizzatore
        Organizzatore org = new Organizzatore("organizzatore@mail.com");
        // Creazione di un hackathon tramite l'organizzatore
        Hackathon hack = org.creaHackathon();
        // Creazione di un giudice e invito all'hackathon
        Giudice judge = new Giudice("giudice@mail.com");
        org.invitaGiudici(judge, hack);
        // Creazione di due utenti generici
        Utente user1 = new Utente("utente1@mail.com");
        Utente user2 = new Utente("utente2@mail.com");
        // Registrazione degli utenti al sistema
        user1.registrati();
        user2.registrati();
        // Iscrizione degli utenti all'hackathon
        hack.aggiungiPartecipante(user1, "2025-04-24");
        hack.aggiungiPartecipante(user2, "2025-04-24");
        // Creazione di un team da parte del primo utente
        Team teamA = new Team(user1, hack);
        System.out.println("TeamA è pieno? " + teamA.èPieno());
        // Il secondo utente invia una richiesta di unione al teamA
        RichiestaUnione richiesta = user2.inviaRichiestaUnione(teamA, "Posso unirmi al team?");
        // Accettazione della richiesta di unione
        richiesta.accetta();
        System.out.println("TeamA è pieno? " + teamA.èPieno());
        // Il giudice pubblica un problema nell'hackathon
        judge.pubblicaProblema("Nuovo problema da risolvere", hack);
        // Il giudice commenta i progressi del teamA
        judge.commentaProgresso(teamA, "Buoni progressi!");
        // Il giudice assegna un voto finale al teamA
        judge.assegnaVoto(teamA, 8);
        // Stampa della classifica finale dell'hackathon
        hack.stampaClassifica();
    }
}
