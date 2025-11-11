package controller;

import gui.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.text.ParseException;
import java.util.Date;
import dao.UtenteDAO;
import daoImpl.UtenteDAOImpl;
import dao.HackathonDAO;
import daoImpl.HackathonDAOImpl;
import java.util.List;
import model.Hackathon;


import model.*;
public class Controller {


    private static JFrame frame;
    private static JFrame frame2;
    private Home loginFrame;
    private ButtonGroup gruppoRuoli;
    private JRadioButton utenteRadioButton;
    private JRadioButton organizzatoreRadioButton;
    private JRadioButton giudiceRadioButton;
    private DashboardUtente dashboardUtente;
    private DashboardGiudice dashboardGiudice;
    private DashboardOrganizzatore dashboardOrganizzatore;
    private int passoCreazione;
    private ArrayList<String> datiHackaton;
    private ArrayList<Hackathon> listaHackathon = new ArrayList<>();
    private int passoInvitoGiudice;
    private ArrayList<String> datiInvitoGiudice;
    private Hackathon hackathonSelezionato;
    private int passoIscrizioneU = -1;
    private ArrayList<String> datiIscrizioneU = new ArrayList<>();





    public Controller () {
    frame = new JFrame("Hackaton");
    loginFrame = new Home();
    frame.setContentPane(loginFrame.getPanel1());
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    aggiungiListeners();
    loginFrame.getPanel1().setFocusable(true);
    frame.requestFocusInWindow();
    frame.setResizable(false);

    gruppoRuoli = new ButtonGroup();
    utenteRadioButton = loginFrame.getUtenteRadioButton();
    organizzatoreRadioButton = loginFrame.getOrganizzatoreRadioButton();
    giudiceRadioButton = loginFrame.getGiudiceRadioButton();

    gruppoRuoli.add(utenteRadioButton);
    gruppoRuoli.add(organizzatoreRadioButton);
    gruppoRuoli.add(giudiceRadioButton);

    }

    public void aggiungiListeners() {
       JTextField campoEmail = loginFrame.getEmailTextField();
       JPanel loginPanel = loginFrame.getPanel1();
       JButton loginButton = loginFrame.getLoginBtn();
       JLabel messaggioErrore = loginFrame.getMessaggioErrore();


       campoEmail.addFocusListener(new FocusListener() {

           @Override
           public void focusGained(FocusEvent e) {
               messaggioErrore.setText("");
               if (campoEmail.getText().equals("Email")) {
                   campoEmail.setText("");
                   campoEmail.setForeground(new java.awt.Color(255, 255, 255));
               }
           }
           @Override
            public void focusLost(FocusEvent e) {
                if (campoEmail.getText().isEmpty()) {
                    campoEmail.setText("Email");
                    campoEmail.setForeground(new java.awt.Color(255, 255, 255));
                }
            }
       });

        loginButton.addActionListener(e -> {
            String email = campoEmail.getText().trim();
            String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

            if (gruppoRuoli.getSelection() == null) {
                messaggioErrore.setText("Seleziona un ruolo");
                return;
            }

            if (email.equals("Email") || email.isEmpty()) {
                messaggioErrore.setText("Inserisci un'email valida");
                return;
            }

            if (!email.matches(emailRegex)) {
                messaggioErrore.setText("Formato email non valido.");
                return;
            }

            String ruolo = null;
            if (utenteRadioButton.isSelected()) ruolo = "utente";
            else if (organizzatoreRadioButton.isSelected()) ruolo = "organizzatore";
            else if (giudiceRadioButton.isSelected()) ruolo = "giudice";

            dao.UtenteDAO dao = new daoImpl.UtenteDAOImpl();
            if (!dao.esisteUtente(email, ruolo)) {
                dao.registraUtente(email, ruolo);
                System.out.println("Utente registrato nel DB.");
            } else {
                System.out.println("Utente già esistente.");
            }

            frame.dispose();

            if (ruolo.equals("utente")) {
                Utente nuovoUtente = new Utente(email);
                dashboardUtente = new DashboardUtente(nuovoUtente.getMail());
                gestisciDashboardUtente(nuovoUtente);
            }

            if (ruolo.equals("organizzatore")) {
                Organizzatore nuovoUtente = new Organizzatore(email);
                dashboardOrganizzatore = new DashboardOrganizzatore(nuovoUtente.getMail());
                gestisciDashboardOrganizzatore(nuovoUtente);
            }

            if (ruolo.equals("giudice")) {
                Giudice nuovoUtente = new Giudice(email);
                dashboardGiudice = new DashboardGiudice(nuovoUtente.getMail());
                gestisciDashboardGiudice(nuovoUtente);
            }
        });


        loginPanel.addMouseListener(new MouseAdapter(){

           @Override
           public void mousePressed(MouseEvent e){
               loginPanel.requestFocusInWindow();

           }


        });

        }
        private void gestisciDashboardUtente(Utente utente){
            JLabel messaggioBenvenuto = dashboardUtente.getMessaggioBenvenuto();
            frame2 = new JFrame("HackatonDashboard - Utente" );
            frame2.setContentPane(dashboardUtente.getDashboardUtente());
            frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame2.pack();
            frame2.setLocationRelativeTo(null);
            frame2.setVisible(true);
            frame2.setResizable(false);

            messaggioBenvenuto.setText("Utente, " + utente.getMail() + " ");



            // Hackaton Disponibili (stessa logica del "Visualizza Hackathon" dell'organizzatore)
            // ===== Hackaton Disponibili (versione pulita, senza riassegnazioni dopo la stampa) =====
            dashboardUtente.getHackatonDisponibili().addActionListener(e -> {
                // Mostra pannello elenco + mini-wizard (solo setup essenziale, nessuna setText su textArea oltre all'intestazione)
                dashboardUtente.getPannelloLogico().setVisible(true);
                dashboardUtente.getScrollPaneVisualizza().setVisible(true);

                // Wizard iscrizione: step iniziale
                passoIscrizioneU = 0;
                datiIscrizioneU.clear();
                dashboardUtente.getAreaDiTesto().setVisible(true);
                dashboardUtente.getFieldScrittura().setVisible(true);
                dashboardUtente.getAvantiButton().setVisible(true);
                dashboardUtente.getIndietroButton().setVisible(true);
                dashboardUtente.getIscrivitiButton().setVisible(false);
                dashboardUtente.getIscrivitiButton().setText("Iscriviti");
                dashboardUtente.getMessaggioErroreOrg().setVisible(true);
                dashboardUtente.getMessaggioErroreOrg().setForeground(Color.WHITE);
                dashboardUtente.getMessaggioErroreOrg().setText("Inserisci il titolo dell'hackathon.");
                dashboardUtente.getAreaDiTesto().setText("<html><b>Iscrizione</b><br><br><b>Titolo:</b> —</html>");
                dashboardUtente.getFieldScrittura().setText("");
                dashboardUtente.getFieldScrittura().requestFocusInWindow();

                // Configura textArea una volta
                JTextArea textArea = dashboardUtente.getTextAreaVisualizza();
                JScrollPane scrollPane = dashboardUtente.getScrollPaneVisualizza();
                Color bg = dashboardUtente.getPannelloLogico().getBackground();
                textArea.setEditable(false);
                textArea.setFocusable(false);
                textArea.setBackground(bg);
                textArea.setForeground(Color.WHITE);
                textArea.setLineWrap(false);
                textArea.setWrapStyleWord(false);
                scrollPane.getViewport().setBackground(bg);
                scrollPane.setBorder(null);

                // *** QUI: unica inizializzazione della textArea ***
                textArea.setText(""); // pulisci
                textArea.append("--- Hackathon Disponibili (iscrizioni aperte oggi) ---\n\n");

                // Carica e filtra con LocalDate helpers (estremi inclusi)
                HackathonDAO dao = new HackathonDAOImpl();
                java.util.List<model.Hackathon> tutti = dao.findAll();

                LocalDate oggi = today();
                boolean trovato = false;

                for (model.Hackathon h : tutti) {
                    LocalDate inizio = parseLocalDateFlex(h.getInizioIscrizioni());
                    LocalDate fine   = parseLocalDateFlex(h.getFineIscrizioni());
                    if (inizio == null || fine == null) {
                        // se vuoi debug, decommenta:
                        // textArea.append("⚠ Date non valide per \""+h.getTitolo()+"\" ("+h.getInizioIscrizioni()+" → "+h.getFineIscrizioni()+")\n");
                        continue;
                    }
                    boolean apertoOggi = (!oggi.isBefore(inizio) && !oggi.isAfter(fine));
                    if (apertoOggi) {
                        trovato = true;
                        textArea.append("Titolo: " + h.getTitolo() + "\n");
                        textArea.append("Organizzatore: " + h.getOrganizzatore() + "\n");
                        textArea.append("Iscrizioni: " + formatDMY(inizio) + " → " + formatDMY(fine) + "\n");
                        textArea.append("-------------------------------------\n");
                    }
                }

                if (!trovato) {
                    // NOTA: qui NON azzero la textArea con setText(...). Aggiungo solo il messaggio.
                    textArea.append("Al momento non ci sono hackathon con iscrizioni aperte.\n");
                }

                // refresh layout
                scrollPane.revalidate();
                scrollPane.repaint();
            });



            dashboardUtente.getAvantiButton().addActionListener(e -> {
                if (passoIscrizioneU < 0) return; // non nel flusso iscrizione

                JLabel guida = dashboardUtente.getMessaggioErroreOrg();
                guida.setForeground(Color.WHITE);

                if (passoIscrizioneU == 0) {
                    String titoloInput = dashboardUtente.getFieldScrittura().getText();
                    String titolo = (titoloInput == null) ? "" : titoloInput.trim();
                    if (titolo.isEmpty()) {
                        guida.setForeground(new Color(180,26,0));
                        guida.setText("Il campo non può essere vuoto.");
                        return;
                    }

                    // trova hackathon per titolo (trim + ignore case)
                    HackathonDAO hdao = new HackathonDAOImpl();
                    java.util.List<model.Hackathon> tutti = hdao.findAll();
                    model.Hackathon target = null;
                    for (model.Hackathon h : tutti) {
                        String t = (h.getTitolo() == null) ? "" : h.getTitolo().trim();
                        if (t.equalsIgnoreCase(titolo)) { target = h; break; }
                    }
                    if (target == null) {
                        guida.setForeground(new Color(180,26,0));
                        guida.setText("<html>Hackathon non trovata<br>o titolo errato.</html>");
                        return;
                    }

                    // === NUOVO: controlli finestra iscrizioni con LocalDate (stessi helper della lista) ===
                    LocalDate inizio = parseLocalDateFlex(target.getInizioIscrizioni());
                    LocalDate fine   = parseLocalDateFlex(target.getFineIscrizioni());
                    LocalDate oggi   = today();
                    if (inizio == null || fine == null) {
                        guida.setForeground(new Color(180,26,0));
                        guida.setText("Formato date non valido in questo hackathon.");
                        return;
                    }
                    boolean apertoOggi = (!oggi.isBefore(inizio) && !oggi.isAfter(fine)); // estremi inclusi
                    if (!apertoOggi) {
                        guida.setForeground(new Color(180,26,0));
                        guida.setText("Iscrizioni non aperte oggi per questo hackathon.");
                        return;
                    }

                    // ID reale dal DB (niente getId sul model)
                    Long hackathonId = hdao.findIdByTitolo(target.getTitolo());
                    if (hackathonId == null) {
                        guida.setForeground(new Color(180,26,0));
                        guida.setText("Impossibile recuperare l'ID dell'hackathon.");
                        return;
                    }

                    // capienza e doppia iscrizione
                    dao.IscrizioneDAO idao = new daoImpl.IscrizioneDAOImpl();
                    if (idao.isIscritto(hackathonId, utente.getMail())) {
                        guida.setForeground(new Color(180,26,0));
                        guida.setText("Risulti già iscritto a questo hackathon.");
                        return;
                    }
                    int iscritti = idao.countIscritti(hackathonId);
                    if (iscritti >= target.getMaxPartecipanti()) {
                        guida.setForeground(new Color(180,26,0));
                        guida.setText("Capienza raggiunta: impossibile iscriversi.");
                        return;
                    }

                    // riepilogo + mostra "Iscriviti"
                    datiIscrizioneU.clear();
                    datiIscrizioneU.add(target.getTitolo());
                    passoIscrizioneU = 1;

                    dashboardUtente.getAreaDiTesto().setText(
                            "<html><b>Iscrizione</b><br><br>"
                                    + "<b>Titolo:</b> " + target.getTitolo() + "<br>"
                                    + "<b>Organizzatore:</b> " + target.getOrganizzatore() + "<br>"
                                    + "<b>Iscrizioni:</b> " + formatDMY(inizio) + " → " + formatDMY(fine)
                                    + "</html>"
                    );
                    guida.setForeground(Color.WHITE);
                    guida.setText("Clicca 'Iscriviti' per confermare.");

                    dashboardUtente.getAvantiButton().setVisible(false);
                    dashboardUtente.getIscrivitiButton().setVisible(true);
                    dashboardUtente.getIscrivitiButton().setText("Iscriviti");
                    dashboardUtente.getFieldScrittura().setText("");
                }
            });



            dashboardUtente.getIndietroButton().addActionListener(e -> {
                if (passoIscrizioneU < 0) return; // non nel flusso iscrizione

                // torna allo step 0
                passoIscrizioneU = 0;
                datiIscrizioneU.clear();

                dashboardUtente.getAreaDiTesto().setText("<html><b>Iscrizione</b><br><br><b>Titolo:</b> —</html>");
                dashboardUtente.getMessaggioErroreOrg().setForeground(Color.WHITE);
                dashboardUtente.getMessaggioErroreOrg().setText("Inserisci il titolo dell'hackathon.");

                dashboardUtente.getIscrivitiButton().setVisible(false);
                dashboardUtente.getAvantiButton().setVisible(true);
                dashboardUtente.getFieldScrittura().setText("");
                dashboardUtente.getFieldScrittura().requestFocusInWindow();
            });

            dashboardUtente.getIscrivitiButton().addActionListener(e -> {
                // deve essere il passo di conferma
                if (passoIscrizioneU != 1 || datiIscrizioneU.isEmpty()) return;

                JLabel guida = dashboardUtente.getMessaggioErroreOrg();
                guida.setForeground(java.awt.Color.WHITE);

                String titolo = datiIscrizioneU.get(0);

                try {
                    // 1) ritrova hackathon per sicurezza
                    dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
                    java.util.List<model.Hackathon> tutti = hdao.findAll();
                    model.Hackathon target = null;
                    for (model.Hackathon h : tutti) {
                        String t = (h.getTitolo() == null) ? "" : h.getTitolo().trim();
                        if (t.equalsIgnoreCase(titolo.trim())) { target = h; break; }
                    }
                    if (target == null) {
                        guida.setForeground(new java.awt.Color(180,26,0));
                        guida.setText("Hackathon non trovata.");
                        return;
                    }

                    // 2) ID reale dal DB
                    Long hackathonId = hdao.findIdByTitolo(target.getTitolo());
                    if (hackathonId == null) {
                        guida.setForeground(new java.awt.Color(180,26,0));
                        guida.setText("Impossibile recuperare l'ID dell'hackathon.");
                        return;
                    }

                    // 3) doppia sicurezza su capienza e doppie iscrizioni
                    dao.IscrizioneDAO idao = new daoImpl.IscrizioneDAOImpl();
                    if (idao.isIscritto(hackathonId, utente.getMail())) {
                        guida.setForeground(new java.awt.Color(180,26,0));
                        guida.setText("Sei già iscritto.");
                        return;
                    }
                    int iscritti = idao.countIscritti(hackathonId);
                    if (iscritti >= target.getMaxPartecipanti()) {
                        guida.setForeground(new java.awt.Color(180,26,0));
                        guida.setText("Capienza raggiunta.");
                        return;
                    }

                    // 4) INSERT
                    java.time.LocalDate oggi = java.time.LocalDate.now();
                    idao.iscrivi(hackathonId, utente.getMail(), oggi);

                    javax.swing.JOptionPane.showMessageDialog(frame2,
                            "Iscrizione completata per \"" + target.getTitolo() + "\".",
                            "Successo", javax.swing.JOptionPane.INFORMATION_MESSAGE);

                    // === RESET UI: riparte come se avessimo appena cliccato "Hackaton Disponibili"
                    passoIscrizioneU = -1;
                    datiIscrizioneU.clear();
                    dashboardUtente.getHackatonDisponibili().doClick(); // riusa la stessa logica del bottone

                } catch (Exception ex) {
                    guida.setForeground(new java.awt.Color(180,26,0));
                    guida.setText("Operazione non riuscita.");
                }
            });







        }

        private void gestisciDashboardGiudice(Giudice giudice){
            JLabel messaggioBenvenuto1 = dashboardGiudice.getMessaggioBenvenuto1();
            frame2 = new JFrame("HackatonDashboard - Giudice" );
            frame2.setContentPane(dashboardGiudice.getDashboardGiudice());
            frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame2.pack();
            frame2.setLocationRelativeTo(null);
            frame2.setVisible(true);
            frame2.setResizable(false);

            messaggioBenvenuto1.setText("Giudice, " + giudice.getMail() + " ");
        }

        private void gestisciDashboardOrganizzatore(Organizzatore organizzatore){
            JLabel messaggioBenvenuto2 = dashboardOrganizzatore.getMessaggioBenvenuto2();
            frame2 = new JFrame("HackatonDashboard - Organizzatore" );
            frame2.setContentPane(dashboardOrganizzatore.getDashboardOrganizzatore());
            frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame2.pack();
            frame2.setLocationRelativeTo(null);
            frame2.setVisible(true);
            frame2.setResizable(false);
            dashboardOrganizzatore.getDashboardOrganizzatore().requestFocusInWindow();

            messaggioBenvenuto2.setText("Organizzatore, " + organizzatore.getMail() + " ");
            JTextField inputField = dashboardOrganizzatore.getFieldScrittura();

            inputField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    JLabel labelInput = dashboardOrganizzatore.getMessaggioErroreOrg();
                    labelInput.setForeground(Color.WHITE);

                    // ---> INVITA GIUDICI: usa le sue domande
                    if (passoInvitoGiudice >= 0) {
                        String[] domandeInvito = {
                                "Inserisci il titolo dell'hackathon.",
                                "Inserisci l'email del giudice."
                        };
                        if (passoInvitoGiudice < domandeInvito.length) {
                            labelInput.setText(domandeInvito[passoInvitoGiudice]);
                        }
                        return;
                    }

                    // ---> CREA HACKATHON: domande originali
                    String[] domandeCrea = {
                            "Inserisci il titolo.",
                            "Inserisci la sede.",
                            "Inserisci il n° max di partecipanti.",
                            "Inserisci la grandezza max del team.",
                            "Inserisci la data di inizio (GG/MM/AAAA).",
                            "Data inizio iscrizioni (GG/MM/AAAA).",
                            "Data fine iscrizioni (GG/MM/AAAA)."
                    };
                    if (passoCreazione < domandeCrea.length) {
                        labelInput.setText(domandeCrea[passoCreazione]);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) { /* no-op */ }
            });


            dashboardOrganizzatore.getCreaHackaton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dashboardOrganizzatore.getPannelloLogico().setVisible(true);
                    dashboardOrganizzatore.getScrollPaneVisualizza().setVisible(false);
                    dashboardOrganizzatore.getAreaDiTesto().setVisible(true);
                    dashboardOrganizzatore.getFieldScrittura().setVisible(true);
                    dashboardOrganizzatore.getIndietroButton().setVisible(true);
                    dashboardOrganizzatore.getMessaggioErroreOrg().setVisible(true);
                    passoCreazione = 0;
                    datiHackaton = new ArrayList<>();
                    gestisciPannelloLogicoDashboardOrganizzatore();
                    passoInvitoGiudice = -1;
                    datiInvitoGiudice.clear();

                }
            });


            // Listener per il pulsante "Create" nella dashboard dell'organizzatore
            dashboardOrganizzatore.getCreateButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    // === Flusso INVITO GIUDICE (quando abbiamo titolo + email) ===
                    if (passoInvitoGiudice >= 0 && datiInvitoGiudice.size() == 2) {
                        String titolo = datiInvitoGiudice.get(0);
                        String emailGiudice = datiInvitoGiudice.get(1);

                        // Verifica che l'hackathon appartenga all'organizzatore (usando lista caricata dal DB)
                        Hackathon selezionato = null;
                        for (Hackathon h : listaHackathon) {
                            if (h.getTitolo().equalsIgnoreCase(titolo) &&
                                    h.getOrganizzatore().equals(organizzatore.getMail())) {
                                selezionato = h; break;
                            }
                        }
                        if (selezionato == null) {
                            JOptionPane.showMessageDialog(frame2, "Hackathon inesistente o non tua.", "Errore", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        HackathonDAO dao = new HackathonDAOImpl();
                        // Duplicati su DB
                        List<String> giaInvitati = dao.listaGiudiciInvitati(titolo, organizzatore.getMail());
                        if (giaInvitati.stream().anyMatch(s -> s.equalsIgnoreCase(emailGiudice))) {
                            JOptionPane.showMessageDialog(frame2, "Giudice già invitato a \"" + titolo + "\".", "Attenzione", JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        // Inserimento invito su DB
                        dao.invitaGiudice(titolo, organizzatore.getMail(), emailGiudice);

                        // (facoltativo) anche nel model in RAM
                        Giudice nuovoGiudice = new Giudice(emailGiudice);
                        organizzatore.invitaGiudici(nuovoGiudice, selezionato);

                        JOptionPane.showMessageDialog(frame2, "Giudice " + emailGiudice + " invitato a \"" + titolo + "\"!", "Successo", JOptionPane.INFORMATION_MESSAGE);

                        // Reset mini-wizard invito (rimani nella sezione Invita)
                        passoInvitoGiudice = 0;            // ← torna allo step del titolo
                        datiInvitoGiudice.clear();
                        dashboardOrganizzatore.getFieldScrittura().setText("");
                        dashboardOrganizzatore.getCreateButton().setVisible(false);
                        dashboardOrganizzatore.getAvantiButton().setVisible(true);
                        dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(Color.WHITE);
                        dashboardOrganizzatore.getMessaggioErroreOrg().setText("Inserisci il titolo del tuo hackathon");
                        gestisciPannelloLogicoInvitoGiudice();
                        // refresh elenco titoli nello scroll
                        HackathonDAO dao2 = new HackathonDAOImpl();
                        List<Hackathon> mieiRefresh = dao2.findByOrganizzatore(organizzatore.getMail());
                        listaHackathon.clear();
                        listaHackathon.addAll(mieiRefresh);

                        JTextArea textArea = dashboardOrganizzatore.getTextAreaVisualizza();
                        textArea.setText("");
                        if (mieiRefresh.isEmpty()) {
                            textArea.setText("Non hai ancora creato hackathon.");
                        } else {
                            textArea.append("--- I tuoi Hackathon ---\n\n");
                            for (Hackathon hh : mieiRefresh) {
                                textArea.append(hh.getTitolo() + "\n");
                            }
                        }
                        return;
                    }

                    // === Flusso CREAZIONE HACKATHON (esistente, NON toccare) ===
                    if (passoCreazione == 7) {
                        String titolo = datiHackaton.get(0);
                        String sede = datiHackaton.get(1);
                        int maxPartecipanti = Integer.parseInt(datiHackaton.get(2));
                        int maxTeamSize = Integer.parseInt(datiHackaton.get(3));
                        String dataInizio = datiHackaton.get(4);
                        String inizioIscr = datiHackaton.get(5);
                        String fineIscr = datiHackaton.get(6);

                        Hackathon h = new Hackathon(titolo, sede, organizzatore, maxPartecipanti, maxTeamSize, dataInizio, inizioIscr, fineIscr);
                        HackathonDAO dao = new HackathonDAOImpl();
                        dao.creaHackathon(h);  // salvataggio nel model/DAO
                        listaHackathon.add(h);

                        JOptionPane.showMessageDialog(frame2, "Hackathon creato con successo!");
                        datiHackaton.clear();
                        passoCreazione = 0;
                        dashboardOrganizzatore.getPannelloLogico().setVisible(false);
                        dashboardOrganizzatore.getMessaggioErroreOrg().setText("");
                    }
                }
            });




            dashboardOrganizzatore.getAvantiButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String inputUtente = dashboardOrganizzatore.getFieldScrittura().getText().trim();

                    if (inputUtente.isEmpty()) {
                        dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                        dashboardOrganizzatore.getMessaggioErroreOrg().setText("Il campo non può essere vuoto.");
                        return;
                    }

                    // === INVITA GIUDICE ===
                    if (passoInvitoGiudice >= 0) {
                        if (passoInvitoGiudice == 0) {
                            // valida titolo ∈ tuoi hackathon
                            Hackathon scelto = null;
                            for (Hackathon h : listaHackathon) {
                                if (h.getTitolo().equalsIgnoreCase(inputUtente) &&
                                        h.getOrganizzatore().equals(organizzatore.getMail())) { scelto = h; break; }
                            }
                            if (scelto == null) {
                                dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                                dashboardOrganizzatore.getMessaggioErroreOrg().setText("<html>Hackathon non trovata<br>o non appartiene all'organizzatore.</html>");
                                return;
                            }
                            if (datiInvitoGiudice.size() == 0) datiInvitoGiudice.add(inputUtente);
                            else datiInvitoGiudice.set(0, inputUtente);

                            passoInvitoGiudice = 1;
                            dashboardOrganizzatore.getFieldScrittura().setText("");

                            // guida/riepilogo: delega al pannello logico
                            dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(Color.WHITE);
                            gestisciPannelloLogicoInvitoGiudice();
                            return;
                        }

                        if (passoInvitoGiudice == 1) {
                            String emailGiudice = inputUtente;
                            String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
                            if (!emailGiudice.matches(emailRegex)) {
                                dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                                dashboardOrganizzatore.getMessaggioErroreOrg().setText("Formato email non valido.");
                                return;
                            }
                            // GIUDICE ESISTENTE
                            UtenteDAO udao = new UtenteDAOImpl();
                            boolean esisteGiudice = udao.esisteUtente(emailGiudice, "giudice");
                            if (!esisteGiudice) {
                                dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                                dashboardOrganizzatore.getMessaggioErroreOrg().setText("Giudice non esistente");
                                return;
                            }

                            if (datiInvitoGiudice.size() == 1) datiInvitoGiudice.add(emailGiudice);
                            else if (datiInvitoGiudice.size() >= 2) datiInvitoGiudice.set(1, emailGiudice);

                            passoInvitoGiudice = 2; // stato conferma ("Invita")
                            dashboardOrganizzatore.getFieldScrittura().setText("");

                            // guida/riepilogo: delega al pannello logico (mostrerà 'Invita')
                            dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(Color.WHITE);
                            gestisciPannelloLogicoInvitoGiudice();
                            return;
                        }

                        // se si preme Avanti in stato conferma, ignora
                        return;
                    }

                    // === CREA HACKATHON (codice esistente, invariato) ===
                    // ** Flusso creazione hackathon (esistente) **
                    if ((passoCreazione == 2 || passoCreazione == 3) && !inputUtente.matches("\\d+")) {
                        dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                        dashboardOrganizzatore.getMessaggioErroreOrg().setText("Errore: inserisci un valore numerico.");
                        return;
                    }

// ✅ nuovo check formato data con java.time
                    if ((passoCreazione == 4 || passoCreazione == 5 || passoCreazione == 6) && !isDateValidFlex(inputUtente)) {
                        dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                        dashboardOrganizzatore.getMessaggioErroreOrg().setText("Formato data non valido. Usa GG/MM/AAAA.");
                        return;
                    }

// ✅ nuove regole con LocalDate (niente ParseException qui)
                    if (passoCreazione == 4) {
                        // Data inizio hackathon: NON nel passato
                        LocalDate dataInizioHackathon = parseLocalDateFlex(inputUtente);
                        if (dataInizioHackathon.isBefore(today())) {
                            dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                            dashboardOrganizzatore.getMessaggioErroreOrg().setText("La data dell'hackathon non può essere nel passato.");
                            return;
                        }
                    }

                    if (passoCreazione == 5) {
                        // Inizio iscrizioni:
                        // - NON nel passato
                        // - Può coincidere col giorno dell'evento
                        // - NON può essere DOPO l'inizio evento
                        LocalDate dataInizioIscr = parseLocalDateFlex(inputUtente);
                        LocalDate dataInizioHackathon = parseLocalDateFlex(datiHackaton.get(4));

                        if (dataInizioIscr.isBefore(today())) {
                            dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                            dashboardOrganizzatore.getMessaggioErroreOrg().setText("L'inizio delle iscrizioni non può essere nel passato.");
                            return;
                        }
                        if (dataInizioIscr.isAfter(dataInizioHackathon)) { // ← nuova regola
                            dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                            dashboardOrganizzatore.getMessaggioErroreOrg().setText("Le iscrizioni non possono iniziare dopo l'inizio dell'hackathon.");
                            return;
                        }
                    }

                    if (passoCreazione == 6) {

                        LocalDate dataFineIscr = parseLocalDateFlex(inputUtente);
                        LocalDate dataInizioIscr = parseLocalDateFlex(datiHackaton.get(5));
                        LocalDate dataInizioHackathon = parseLocalDateFlex(datiHackaton.get(4));

                        if (!dataFineIscr.isAfter(dataInizioIscr)) {
                            dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                            dashboardOrganizzatore.getMessaggioErroreOrg().setText("La fine iscrizioni deve essere successiva all'inizio iscrizioni.");
                            return;
                        }
                        if (!dataFineIscr.isBefore(dataInizioHackathon)) {
                            dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                            dashboardOrganizzatore.getMessaggioErroreOrg().setText("Le iscrizioni devono chiudersi prima dell'inizio dell'hackathon.");
                            return;
                        }
                    }

// (il resto del tuo codice rimane uguale: salvataggio del dato, passoCreazione++, ecc.)


                    datiHackaton.add(inputUtente);
                    passoCreazione++;
                    gestisciPannelloLogicoDashboardOrganizzatore();
                }
            });




            dashboardOrganizzatore.getIndietroButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (passoInvitoGiudice >= 0) {
                        if (passoInvitoGiudice > 0) {
                            int lastIndex = datiInvitoGiudice.size() - 1;
                            String valorePrecedente = "";
                            if (lastIndex >= 0) valorePrecedente = datiInvitoGiudice.remove(lastIndex);
                            passoInvitoGiudice--;

                            // reset eventuale rosso e delega a pannello logico per guida/riepilogo
                            dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(Color.WHITE);
                            gestisciPannelloLogicoInvitoGiudice();

                            dashboardOrganizzatore.getFieldScrittura().setText(valorePrecedente);
                        } else {
                            dashboardOrganizzatore.getPannelloLogico().setVisible(false);
                            passoInvitoGiudice = -1;
                            datiInvitoGiudice.clear();
                        }
                        return;
                    }

                    // CREA HACKATHON (esistente)
                    if (passoCreazione > 0) {
                        String valorePrecedente = datiHackaton.get(datiHackaton.size() - 1);
                        datiHackaton.remove(datiHackaton.size() - 1);
                        passoCreazione--;
                        gestisciPannelloLogicoDashboardOrganizzatore();
                        dashboardOrganizzatore.getFieldScrittura().setText(valorePrecedente);
                    }
                }
            });




            dashboardOrganizzatore.getVisualizzaHackathon().addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                   dashboardOrganizzatore.getPannelloLogico().setVisible(true);
                   dashboardOrganizzatore.getAvantiButton().setVisible(false);
                   dashboardOrganizzatore.getIndietroButton().setVisible(false);
                   dashboardOrganizzatore.getCreateButton().setVisible(false);
                   dashboardOrganizzatore.getFieldScrittura().setVisible(false);
                   dashboardOrganizzatore.getAreaDiTesto().setVisible(false);
                   dashboardOrganizzatore.getMessaggioErroreOrg().setVisible(false);
                   dashboardOrganizzatore.getScrollPaneVisualizza().setVisible(true);

                   JTextArea textArea = dashboardOrganizzatore.getTextAreaVisualizza();
                   JScrollPane scrollPane = dashboardOrganizzatore.getScrollPaneVisualizza();
                   textArea.setEditable(false);
                   textArea.setFocusable(false);
                   textArea.setText(""); // Pulisci l'area di testo
                   textArea.setLineWrap(true);
                   textArea.setWrapStyleWord(true);
                   scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                   scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                   Color coloreSfondo = dashboardOrganizzatore.getPannelloLogico().getBackground();

                   textArea.setEditable(false);
                   textArea.setBackground(coloreSfondo);
                   textArea.setForeground(Color.WHITE);
                   scrollPane.getViewport().setBackground(coloreSfondo);
                   scrollPane.setBorder(null);

                   HackathonDAO dao = new HackathonDAOImpl();
                   List<Hackathon> listaDalDB = dao.findAll();

                   if (listaDalDB.isEmpty()) {
                       textArea.setText("Nessun hackathon è stato ancora creato.");
                   } else {
                       textArea.setText("--- Hackathon Disponibili ---\n\n");
                       for (Hackathon hack : listaDalDB) {
                           textArea.append("Titolo: " + hack.getTitolo() + "\n");
                           textArea.append("Sede: " + hack.getSede() + "\n");
                           textArea.append("Max Partecipanti: " + hack.getMaxPartecipanti() + "\n");
                           textArea.append("Max Grandezza Team: " + hack.getMaxGrandezzaTeam() + "\n");
                           textArea.append("Data Inizio: " + hack.getInizio() + "\n");
                           textArea.append("Inizio Iscrizioni: " + hack.getInizioIscrizioni() + "\n");
                           textArea.append("Fine Iscrizioni: " + hack.getFineIscrizioni() + "\n");
                           textArea.append("Organizzatore: " + hack.getOrganizzatore() + "\n");
                           textArea.append("-------------------------------------\n");
                       }
                   }

               }

           });

            dashboardOrganizzatore.getInvitaGiudiciButton().addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    // mostra i pannelli necessari
                    dashboardOrganizzatore.getPannelloLogico().setVisible(true);
                    dashboardOrganizzatore.getScrollPaneVisualizza().setVisible(true);
                    dashboardOrganizzatore.getAreaDiTesto().setVisible(true);
                    dashboardOrganizzatore.getFieldScrittura().setVisible(true);
                    dashboardOrganizzatore.getAvantiButton().setVisible(true);
                    dashboardOrganizzatore.getIndietroButton().setVisible(true);
                    dashboardOrganizzatore.getCreateButton().setVisible(false);
                    dashboardOrganizzatore.getMessaggioErroreOrg().setVisible(true);
                    

                    // stile guida
                    JLabel guida = dashboardOrganizzatore.getMessaggioErroreOrg();
                    guida.setForeground(Color.WHITE);

                    // carica SOLO i titoli dei tuoi hackathon
                    HackathonDAO dao = new HackathonDAOImpl();
                    List<Hackathon> miei = dao.findByOrganizzatore(organizzatore.getMail());
                    listaHackathon.clear();
                    listaHackathon.addAll(miei);

                    JTextArea textArea = dashboardOrganizzatore.getTextAreaVisualizza();
                    JScrollPane scrollPane = dashboardOrganizzatore.getScrollPaneVisualizza();

                    textArea.setEditable(false);
                    textArea.setFocusable(false);
                    textArea.setText("");

                    // evita barre inutili
                    textArea.setLineWrap(true);
                    textArea.setWrapStyleWord(true);
                    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

                    Color sfondo = dashboardOrganizzatore.getPannelloLogico().getBackground();
                    textArea.setBackground(sfondo);
                    textArea.setForeground(Color.WHITE);
                    scrollPane.getViewport().setBackground(sfondo);
                    scrollPane.setBorder(null);

                    if (miei.isEmpty()) {
                        textArea.setText("Non hai ancora creato hackathon.");
                    } else {
                        textArea.append("--- I tuoi Hackathon ---\n\n");
                        for (Hackathon h : miei) textArea.append(h.getTitolo() + "\n");
                    }

                    // (ri)inizializza il mini-wizard
                    datiInvitoGiudice = new ArrayList<>();
                    passoInvitoGiudice = 0;
                    hackathonSelezionato = null;
                    dashboardOrganizzatore.getFieldScrittura().setText("");

                    // lascia che sia il pannello logico a impostare guida e riepilogo
                    gestisciPannelloLogicoInvitoGiudice();
                }
            });








        }

    private static final DateTimeFormatter DMY = DateTimeFormatter.ofPattern("d/M/uuuu");
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    private LocalDate today() {
        return LocalDate.now(ZoneId.systemDefault());
    }

    private LocalDate parseLocalDateFlex(String s) {
        if (s == null) return null;
        s = s.trim();
        try {
            return LocalDate.parse(s, DMY);
        } catch (DateTimeParseException ignore) {
            try {
                return LocalDate.parse(s, ISO);
            } catch (DateTimeParseException ignore2) {
                return null;
            }
        }
    }

    private boolean isDateValidFlex(String s) {
        return parseLocalDateFlex(s) != null;
    }

    private String formatDMY(LocalDate d) {
        return d == null ? "" : d.format(DateTimeFormatter.ofPattern("dd/MM/uuuu"));
    }


    private Date getOggiSenzaOrario() {
        LocalDate ld = today();
        return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }



    private void gestisciPannelloLogicoDashboardOrganizzatore() {
                JLabel labelIstruzioni = dashboardOrganizzatore.getAreaDiTesto();
                JLabel labelInput = dashboardOrganizzatore.getMessaggioErroreOrg();
                JTextField inputField = dashboardOrganizzatore.getFieldScrittura();
                JButton avantiBtn = dashboardOrganizzatore.getAvantiButton();
                JButton indietroBtn = dashboardOrganizzatore.getIndietroButton();
                JButton creaBtn = dashboardOrganizzatore.getCreateButton();

                labelInput.setForeground(Color.WHITE);
                indietroBtn.setVisible(passoCreazione > 0);
                avantiBtn.setVisible(true);
                creaBtn.setVisible(false);
                inputField.setText("");
                inputField.requestFocusInWindow();

                String[] etichetteCampi = {"Titolo:", "Sede:", "Max Partecipanti:", "Max Grandezza Team:", "Data Inizio:", "Inizio Iscrizioni:", "Fine Iscrizioni:"};
                String[] domande = {"Inserisci il titolo.", "Inserisci la sede.", "Inserisci il n° max di partecipanti.", "Inserisci la grandezza max del team.", "Inserisci la data di inizio (GG/MM/AAAA).", "Data inizio iscrizioni (GG/MM/AAAA).", "Data fine iscrizioni (GG/MM/AAAA)."};

                String formText = "<html>Creazione Hackathon<br><br>";
                for (int i = 0; i < etichetteCampi.length; i++) {
                    formText += etichetteCampi[i] + " ";
                    if (i < datiHackaton.size()) {
                        formText += datiHackaton.get(i);
                    }
                    formText += "<br>";
                }
                formText += "</html>";
                labelIstruzioni.setText(formText);

                if (passoCreazione < domande.length) {
                    labelInput.setText(domande[passoCreazione]);
                } else {
                    labelInput.setText("Clicca 'Crea' per confermare.");
                    avantiBtn.setVisible(false);
                    creaBtn.setVisible(true);
                }
            }

            
    private void gestisciPannelloLogicoInvitoGiudice() {

        JLabel labelIstruzioni = dashboardOrganizzatore.getAreaDiTesto();
        JLabel labelInput = dashboardOrganizzatore.getMessaggioErroreOrg();
        JTextField inputField = dashboardOrganizzatore.getFieldScrittura();
        JButton avantiBtn = dashboardOrganizzatore.getAvantiButton();
        JButton indietroBtn = dashboardOrganizzatore.getIndietroButton();
        JButton creaBtn = dashboardOrganizzatore.getCreateButton();


        labelInput.setForeground(Color.WHITE);
        inputField.setText("");
        inputField.requestFocusInWindow();

        indietroBtn.setVisible(passoInvitoGiudice > 0);
        avantiBtn.setVisible(true);
        creaBtn.setVisible(false);


        String[] etichetteCampi = {"Hackathon:", "Email Giudice:"};
        String[] domande = {
                "Inserisci il titolo dell'hackathon.",
                "Inserisci l'email del giudice."
        };

        String formText = "<html>Invito Giudice<br><br>";
        for (int i = 0; i < etichetteCampi.length; i++) {
            formText += etichetteCampi[i] + " ";
            if (i < datiInvitoGiudice.size()) {
                formText += datiInvitoGiudice.get(i);
            }
            formText += "<br>";
        }
        formText += "</html>";
        labelIstruzioni.setText(formText);


        if (passoInvitoGiudice < domande.length) {
            labelInput.setText(domande[passoInvitoGiudice]);
        } else {

            labelInput.setText("Clicca 'Invita' per confermare.");
            avantiBtn.setVisible(false);
            creaBtn.setVisible(true);
            creaBtn.setText("Invita");
        }
    }

}
