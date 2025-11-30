package model;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private List<Utente> membri;
    private List<Documento> progressi;
    private int votoFinale;
    private Hackathon hackathon;
    private String nome;

    public Team(Hackathon hackathon) {
        this.membri = new ArrayList<>();
        this.progressi = new ArrayList<>();
        this.votoFinale = 0;
        this.hackathon = hackathon;
        if (hackathon != null) {
            hackathon.aggiungiTeam(this);
        }
    }

    public Team(Utente primoMembro, Hackathon hackathon) {
        this(hackathon);
        if (primoMembro != null) {
            membri.add(primoMembro);
        }
    }

    // Verifica se il team ha raggiunto il numero massimo di membri
    public boolean èPieno() {
        if (hackathon == null) return false;
        return membri.size() >= hackathon.getMaxGrandezzaTeam();
    }

    // Aggiunge un membro al team
    public void aggiungiMembro(Utente utente) {
        if (utente == null) return;
        if (membri.contains(utente)) {
            System.out.println("Utente " + utente.getMail() + " è già nel team.");
            return;
        }
        if (èPieno()) {
            System.out.println("Il team è al completo, impossibile aggiungere nuovi membri.");
        } else {
            membri.add(utente);
            System.out.println("Utente " + utente.getMail() + " aggiunto al team.");
        }
    }

    // Aggiunge un documento di progresso al team
    public void aggiungiDocumento(Documento doc) {
        if (doc != null) {
            progressi.add(doc);
        }
    }

    public Hackathon getHackathon() {
        return hackathon;
    }

    public int getNumeroMembri() {
        return membri.size();
    }

    public int getVotoFinale() {
        return votoFinale;
    }

    public void setVotoFinale(int votoFinale) {
        this.votoFinale = votoFinale;
    }
    // 🔹 NUOVO: getter/setter per il nome del team
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}


