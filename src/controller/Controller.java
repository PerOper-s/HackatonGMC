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
    private int passoTeamU = -1;
    private ArrayList<String> datiTeamU = new ArrayList<>();
    private int passoDocumentoU = -1;
    private ArrayList<String> datiDocumentoU = new ArrayList<>();
    private int passoInvitoG = -1;
    private java.util.List<String> datiInvitoG = new java.util.ArrayList<>();





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
                passoTeamU = -1;
                datiTeamU.clear();
                dashboardUtente.getAreaDiTesto().setVisible(true);
                dashboardUtente.getFieldScrittura().setVisible(true);
                dashboardUtente.getAvantiButton().setVisible(true);
                dashboardUtente.getIndietroButton().setVisible(true);
                dashboardUtente.getIscrivitiButton().setVisible(false);
                dashboardUtente.getIscrivitiButton().setText("Iscriviti");
                dashboardUtente.getMessaggioErroreOrg().setVisible(true);
                aggiornaGuidaDashboardUtente();
                dashboardUtente.getAreaDiTesto().setText("<html><b>Iscrizione</b><br><br><b>Titolo:</b> —</html>");aggiornaGuidaDashboardUtente();
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


                        LocalDate inizioHack = parseLocalDateFlex(h.getInizio());
                        if (inizioHack != null) {
                            textArea.append("Inizio hackathon: " + formatDMY(inizioHack) + "\n");
                        } else if (h.getInizio() != null) {
                            // fallback se il parse fallisce ma la stringa c'è
                            textArea.append("Inizio hackathon: " + h.getInizio() + "\n");
                        }
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

            // Focus sul campo input utente: resetta errori e rimette la guida giusta
            JTextField inputFieldU = dashboardUtente.getFieldScrittura();
            inputFieldU.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    JLabel labelInput = dashboardUtente.getMessaggioErroreOrg();

                    // Se era in errore (rosso), lo resetto alla guida normale per lo step corrente
                    if (labelInput.getForeground().equals(new Color(180, 26, 0))) {
                        aggiornaGuidaDashboardUtente();
                    } else {
                        // altrimenti lascio il testo com'è, ma mi assicuro che il colore sia bianco
                        labelInput.setForeground(Color.WHITE);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    // non facciamo niente quando esce dal campo
                }
            });



            // ===== I miei Hackaton + Creazione Team =====
            dashboardUtente.getIMieiHackaton().addActionListener(e -> {
                // 1) Nessuna sovrapposizione con l’iscrizione:
                passoIscrizioneU = -1;
                datiIscrizioneU.clear();

                // attivo il wizard team
                passoTeamU = 0;
                datiTeamU.clear();

                // 2) attivo pannello logico e scrollPane
                dashboardUtente.getPannelloLogico().setVisible(true);
                dashboardUtente.getScrollPaneVisualizza().setVisible(true);
                dashboardUtente.getAreaDiTesto().setVisible(true);
                dashboardUtente.getFieldScrittura().setVisible(true);
                dashboardUtente.getAvantiButton().setVisible(true);
                dashboardUtente.getIndietroButton().setVisible(true);
                dashboardUtente.getIscrivitiButton().setVisible(false);
                dashboardUtente.getIscrivitiButton().setText("Crea Team");

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

                // 3) Carica SOLO gli hackathon a cui l'utente è iscritto
                textArea.setText(""); // pulisci
                textArea.append("--- I miei Hackathon ---\n\n");

                HackathonDAO dao = new HackathonDAOImpl();
                java.util.List<model.Hackathon> miei = dao.findByUtenteIscritto(utente.getMail());

                if (miei.isEmpty()) {
                    textArea.append("Non sei iscritto ad alcun hackathon.\n");
                    // blocco il wizard team
                    passoTeamU = -1;
                    JLabel guida = dashboardUtente.getMessaggioErroreOrg();
                    guida.setForeground(new Color(180,26,0));
                    guida.setText("Iscriviti prima ad un hackathon per creare un team.");
                    dashboardUtente.getFieldScrittura().setVisible(false);
                    dashboardUtente.getAvantiButton().setVisible(false);
                    dashboardUtente.getIndietroButton().setVisible(false);
                    return;
                }

                for (model.Hackathon h : miei) {
                    textArea.append(
                            "Titolo: " + h.getTitolo() + "\n" +
                                    "Sede: " + h.getSede() + "\n" +
                                    "Organizzatore: " + h.getOrganizzatore() + "\n" +
                                    "Inizio: " + h.getInizio() + "\n" +
                                    "Iscrizioni: " + h.getInizioIscrizioni() + " → " + h.getFineIscrizioni() + "\n\n"
                    );
                }

                // 4) reset indicatori wizard
                aggiornaGuidaDashboardUtente();
                dashboardUtente.getAreaDiTesto().setText(
                        "<html><b>Creazione Team</b><br><br><b>Titolo hackathon:</b> —<br><b>Nome team:</b> —</html>"
                );
                dashboardUtente.getFieldScrittura().setText("");
                dashboardUtente.getFieldScrittura().requestFocusInWindow();
            });


            // ===== I miei Team =====
            dashboardUtente.getIMieiTeam().addActionListener(e -> {
                passoIscrizioneU = -1;
                datiIscrizioneU.clear();
                passoTeamU = -1;
                datiTeamU.clear();

                passoDocumentoU = 0;
                datiDocumentoU.clear();


                dashboardUtente.getPannelloLogico().setVisible(true);
                dashboardUtente.getScrollPaneVisualizza().setVisible(true);
                dashboardUtente.getAreaDiTesto().setVisible(true);
                dashboardUtente.getTextAreaVisualizza().setVisible(true);


                dashboardUtente.getFieldScrittura().setVisible(true);
                dashboardUtente.getAvantiButton().setVisible(true);
                dashboardUtente.getIndietroButton().setVisible(true);
                dashboardUtente.getIscrivitiButton().setVisible(false);
                dashboardUtente.getIscrivitiButton().setText("Carica documento");


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

                textArea.setText("");
                textArea.append("--- I miei Team ---\n\n");

                // Setup parte procedurale sopra
                dashboardUtente.getAreaDiTesto().setText(
                        "<html><b>Caricamento documento</b><br><br>" +
                                "<b>Hackathon:</b> —<br>" +
                                "<b>Team:</b> —<br>" +
                                "<b>Contenuto:</b> —</html>"
                );

                dashboardUtente.getFieldScrittura().setText("");
                aggiornaGuidaDashboardUtente();
                dashboardUtente.getFieldScrittura().requestFocusInWindow();


                dao.TeamDAO tdao = new daoImpl.TeamDAOImpl();
                java.util.List<model.TeamInfo> mieiTeam = tdao.findTeamsByUtente(utente.getMail());

                JLabel guida = dashboardUtente.getMessaggioErroreOrg();

                if (mieiTeam.isEmpty()) {
                    textArea.append("Non fai parte di alcun team.\n");

                    // disattivo il wizard documento
                    passoDocumentoU = -1;
                    datiDocumentoU.clear();


                    dashboardUtente.getFieldScrittura().setVisible(false);
                    dashboardUtente.getAvantiButton().setVisible(false);
                    dashboardUtente.getIndietroButton().setVisible(false);
                    dashboardUtente.getIscrivitiButton().setVisible(false);


                    guida.setForeground(Color.WHITE);
                    guida.setText("");


                    dashboardUtente.getAreaDiTesto().setText("<html><b>I miei team</b></html>");
                    return;
                }

                // preparo DAO extra fuori dal ciclo (sopra al for)
                dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
                dao.DocumentoDAO ddao = new daoImpl.DocumentoDAOImpl();
                dao.ProblemaDAO pdao = new daoImpl.ProblemaDAOImpl();
                dao.CommentoDAO cdao = new daoImpl.CommentoDAOImpl();
                java.util.List<model.Hackathon> tuttiHackathon = hdao.findAll();

                for (model.TeamInfo info : mieiTeam) {

                    String titoloHackathon = info.getTitoloHackathon();
                    String nomeTeam = info.getNomeTeam();

                    // --- Data di inizio hackathon ---
                    String dataInizioStr = "—";
                    Long hackathonId = null;
                    if (titoloHackathon != null) {
                        String titoloNorm = titoloHackathon.trim();

                        model.Hackathon trovato = null;
                        for (model.Hackathon h : tuttiHackathon) {
                            String t = (h.getTitolo() == null) ? "" : h.getTitolo().trim();
                            if (t.equalsIgnoreCase(titoloNorm)) {
                                trovato = h;
                                break;
                            }
                        }

                        if (trovato != null) {
                            LocalDate inizio = parseLocalDateFlex(trovato.getInizio());
                            if (inizio != null) {
                                dataInizioStr = formatDMY(inizio);
                            } else {
                                dataInizioStr = trovato.getInizio(); // fallback stringa grezza
                            }

                            hackathonId = hdao.findIdByTitolo(trovato.getTitolo());
                        }
                    }

                    textArea.append("Hackathon: " + titoloHackathon + "\n");
                    textArea.append("Inizio: " + dataInizioStr + "\n");
                    textArea.append("Team: " + nomeTeam);
                    if (info.isCreatore(utente.getMail())) {
                        textArea.append(" (creatore)");
                    }
                    textArea.append("\n");

                    // --- Membri ---
                    textArea.append("Membri (" + info.getNumeroMembri() + "): ");

                    java.util.List<String> membri = tdao.findMembriTeam(info.getId());
                    if (membri.isEmpty()) {
                        textArea.append("nessuno\n");
                    } else {
                        for (int i = 0; i < membri.size(); i++) {
                            textArea.append(membri.get(i));
                            if (i < membri.size() - 1) {
                                textArea.append(", ");
                            }
                        }
                        textArea.append("\n");
                    }

                    // --- Ultimo documento caricato (se c'è) ---
                    if (hackathonId != null) {
                        String contenuto = ddao.trovaUltimoDocumento(info.getId(), hackathonId);

                        if (contenuto == null || contenuto.trim().isEmpty()) {
                            textArea.append("Ultimo documento: nessun documento caricato.\n");
                        } else {
                            String anteprima = anteprimaDocumento(contenuto);
                            textArea.append("Ultimo documento:\n");
                            textArea.append(anteprima + "\n");
                        }
                    } else {
                        textArea.append("Ultimo documento: dati hackathon non disponibili.\n");
                    }

                    // --- Problema e commenti dei giudici ---
                    if (hackathonId != null) {
                        // PROBLEMA
                        model.Problema problema = pdao.trovaPerHackathon(hackathonId);
                        if (problema == null) {
                            textArea.append("Problema: nessun problema pubblicato.\n");
                        } else {
                            String descr = problema.getDescrizione();
                            if (descr != null && descr.length() > 150) {
                                descr = descr.substring(0, 150) + "...";
                            }
                            String emailGiudice = (problema.getGiudice() != null)
                                    ? problema.getGiudice().getMail()
                                    : "?";

                            textArea.append("Problema: \"" + descr + "\" (Giudice: " + emailGiudice + ")\n");
                        }

                        // COMMENTI
                        java.util.List<model.CommentoInfo> commenti =
                                cdao.findCommentiPerTeam(hackathonId, info.getId());

                        if (commenti.isEmpty()) {
                            textArea.append("Commenti: nessun commento.\n");
                        } else {
                            textArea.append("Commenti:\n");
                            for (model.CommentoInfo c : commenti) {
                                String testo = c.getContenuto();
                                if (testo != null && testo.length() > 150) {
                                    testo = testo.substring(0, 150) + "...";
                                }
                                textArea.append("Giudice " + c.getGiudiceEmail()
                                        + ": \"" + testo + "\"\n");
                            }
                        }
                    }

                    textArea.append("\n");


                    textArea.append("\n"); // separatore tra team
                }


                aggiornaGuidaDashboardUtente();
            });





            dashboardUtente.getAvantiButton().addActionListener(e -> {

                JLabel guida = dashboardUtente.getMessaggioErroreOrg();
                guida.setForeground(Color.WHITE);

                // --------------------------------------------------------
                // 1) WIZARD ISCRIZIONE (Hackaton Disponibili)
                // --------------------------------------------------------
                if (passoIscrizioneU >= 0) {

                    if (passoIscrizioneU == 0) {
                        String titoloInput = dashboardUtente.getFieldScrittura().getText();
                        String titolo = (titoloInput == null) ? "" : titoloInput.trim();
                        if (titolo.isEmpty()) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Il campo non può essere vuoto.");
                            return;
                        }

                        // trova hackathon per titolo (trim + ignore case) tra TUTTI quelli disponibili
                        dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
                        java.util.List<model.Hackathon> tutti = hdao.findAll();
                        model.Hackathon target = null;
                        for (model.Hackathon h : tutti) {
                            String t = (h.getTitolo() == null) ? "" : h.getTitolo().trim();
                            if (t.equalsIgnoreCase(titolo)) {
                                target = h;
                                break;
                            }
                        }
                        if (target == null) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Hackathon non trovata.");
                            return;
                        }

                        // verifica finestra iscrizioni con LocalDate
                        java.time.LocalDate oggi = today();
                        java.time.LocalDate inizio = parseLocalDateFlex(target.getInizioIscrizioni());
                        java.time.LocalDate fine = parseLocalDateFlex(target.getFineIscrizioni());
                        if (inizio == null || fine == null) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Formato date non valido in questo hackathon.");
                            return;
                        }
                        boolean apertoOggi = (!oggi.isBefore(inizio) && !oggi.isAfter(fine)); // estremi inclusi
                        if (!apertoOggi) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Iscrizioni non aperte oggi per questo hackathon.");
                            return;
                        }

                        // ID reale dal DB
                        Long hackathonId = hdao.findIdByTitolo(target.getTitolo());
                        if (hackathonId == null) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Impossibile recuperare l'ID dell'hackathon.");
                            return;
                        }

                        // controlli iscrizione (già iscritto / capienza)
                        dao.IscrizioneDAO idao = new daoImpl.IscrizioneDAOImpl();
                        if (idao.isIscritto(hackathonId, utente.getMail())) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Sei già iscritto a questo hackathon.");
                            return;
                        }
                        int iscritti = idao.countIscritti(hackathonId);
                        if (iscritti >= target.getMaxPartecipanti()) {
                            guida.setForeground(new Color(180, 26, 0));
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
                                        + "<b>Iscrizioni:</b> " + formatDMY(inizio) + " → " + formatDMY(fine) + "<br>"
                                        + "</html>"
                        );
                        aggiornaGuidaDashboardUtente();

                        dashboardUtente.getAvantiButton().setVisible(false);
                        dashboardUtente.getIscrivitiButton().setVisible(true);
                        dashboardUtente.getIscrivitiButton().setText("Iscriviti");
                        dashboardUtente.getFieldScrittura().setText("");
                    }

                    return; // fine ramo iscrizione
                }

                // --------------------------------------------------------
                // 2) WIZARD TEAM (I miei Hackaton → Crea Team)
                // --------------------------------------------------------
                if (passoTeamU >= 0) {

                    // STEP 0: inserimento titolo hackathon
                    if (passoTeamU == 0) {
                        String titoloInput = dashboardUtente.getFieldScrittura().getText();
                        String titolo = (titoloInput == null) ? "" : titoloInput.trim();

                        if (titolo.isEmpty()) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Il titolo non può essere vuoto.");
                            return;
                        }

                        // cerca l'hackathon SOLO tra quelli dove l'utente è iscritto
                        dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
                        java.util.List<model.Hackathon> miei = hdao.findByUtenteIscritto(utente.getMail());
                        model.Hackathon target = null;
                        for (model.Hackathon h : miei) {
                            String t = (h.getTitolo() == null) ? "" : h.getTitolo().trim();
                            if (t.equalsIgnoreCase(titolo)) {
                                target = h;
                                break;
                            }
                        }
                        if (target == null) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Non risulti iscritto a un hackathon con questo titolo.");
                            return;
                        }

                        Long hackathonId = hdao.findIdByTitolo(target.getTitolo());
                        if (hackathonId == null) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Impossibile recuperare l'ID dell'hackathon.");
                            return;
                        }

                        // controlla che l'utente NON abbia già un team per quell'hackathon
                        dao.TeamDAO tdao = new daoImpl.TeamDAOImpl();
                        if (tdao.utenteHaTeam(hackathonId, utente.getMail())) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Hai già un team per questo hackathon.");
                            return;
                        }

                        // passa allo step successivo (nome team)
                        passoTeamU = 1;
                        datiTeamU.clear();
                        datiTeamU.add(target.getTitolo()); // salvo titolo hackathon

                        dashboardUtente.getAreaDiTesto().setText(
                                "<html><b>Creazione Team</b><br><br>"
                                        + "<b>Titolo hackathon:</b> " + target.getTitolo() + "<br>"
                                        + "<b>Nome team:</b> —</html>"
                        );
                        aggiornaGuidaDashboardUtente();
                        dashboardUtente.getFieldScrittura().setText("");
                        dashboardUtente.getFieldScrittura().requestFocusInWindow();
                        return;
                    }

                    // STEP 1: inserimento nome team
                    if (passoTeamU == 1) {
                        if (datiTeamU.isEmpty()) return; // safety

                        String nomeTeamInput = dashboardUtente.getFieldScrittura().getText();
                        String nomeTeam = (nomeTeamInput == null) ? "" : nomeTeamInput.trim();

                        if (nomeTeam.isEmpty()) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Il nome del team non può essere vuoto.");
                            return;
                        }

                        String titoloHackathon = datiTeamU.get(0);

                        dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
                        Long hackathonId = hdao.findIdByTitolo(titoloHackathon);
                        if (hackathonId == null) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Impossibile recuperare l'ID dell'hackathon.");
                            return;
                        }

                        dao.TeamDAO tdao = new daoImpl.TeamDAOImpl();
                        if (tdao.esisteTeamConNome(hackathonId, nomeTeam)) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Esiste già un team con questo nome in questo hackathon.");
                            return;
                        }

                        // Riepilogo + mostra "Crea Team"
                        passoTeamU = 2;
                        if (datiTeamU.size() == 1) datiTeamU.add(nomeTeam);
                        else datiTeamU.set(1, nomeTeam);

                        dashboardUtente.getAreaDiTesto().setText(
                                "<html><b>Creazione Team</b><br><br>"
                                        + "<b>Titolo hackathon:</b> " + titoloHackathon + "<br>"
                                        + "<b>Nome team:</b> " + nomeTeam + "</html>"
                        );
                        aggiornaGuidaDashboardUtente();
                        dashboardUtente.getAvantiButton().setVisible(false);
                        dashboardUtente.getIscrivitiButton().setVisible(true);
                        dashboardUtente.getIscrivitiButton().setText("Crea Team");
                        dashboardUtente.getFieldScrittura().setText("");
                        return;
                    }

                    return; // fine ramo team
                }

                // --------------------------------------------------------
                // 3) WIZARD DOCUMENTO (I miei Team → Carica documento)
                // --------------------------------------------------------
                if (passoDocumentoU >= 0) {

                    // STEP 0: inserimento nome team
                    if (passoDocumentoU == 0) {
                        String nomeTeamInput = dashboardUtente.getFieldScrittura().getText();
                        String nomeTeam = (nomeTeamInput == null) ? "" : nomeTeamInput.trim();

                        if (nomeTeam.isEmpty()) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("<html>Il nome del team<br>non può essere vuoto.</html>");
                            return;
                        }

                        dao.TeamDAO tdao = new daoImpl.TeamDAOImpl();
                        java.util.List<model.TeamInfo> mieiTeam = tdao.findTeamsByUtente(utente.getMail());

                        model.TeamInfo selezionato = null;
                        for (model.TeamInfo info : mieiTeam) {
                            if (info.getNomeTeam() != null &&
                                    info.getNomeTeam().trim().equalsIgnoreCase(nomeTeam)) {
                                selezionato = info;
                                break; // assumiamo un solo team con quel nome
                            }
                        }

                        if (selezionato == null) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("<html>Nessun team trovato<br>con questo nome.</html>");
                            return;
                        }

                        String titoloHackathon = selezionato.getTitoloHackathon();

                        // Recupero l'hackathon per verificare la data di inizio
                        dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
                        java.util.List<model.Hackathon> hackathonUtente =
                                hdao.findByUtenteIscritto(utente.getMail());

                        model.Hackathon hTarget = null;
                        for (model.Hackathon h : hackathonUtente) {
                            if (h.getTitolo() != null &&
                                    h.getTitolo().trim().equalsIgnoreCase(titoloHackathon.trim())) {
                                hTarget = h;
                                break;
                            }
                        }

                        if (hTarget == null) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("<html>Impossibile recuperare<br>i dati dell'hackathon.</html>");
                            return;
                        }

                        // Controllo: documento solo da quando l'hackathon è iniziato
                        java.time.LocalDate oggi = today();
                        java.time.LocalDate inizioHackathon = parseLocalDateFlex(hTarget.getInizio());
                        if (inizioHackathon == null) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("<html>Data di inizio hackathon<br>non valida.</html>");
                            return;
                        }

                        if (oggi.isBefore(inizioHackathon)) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("<html>Puoi caricare documenti<br>solo da inizio hackathon.</html>");
                            return;
                        }

                        // Recupero ID reali dal DB
                        Long hackathonId = hdao.findIdByTitolo(titoloHackathon);
                        if (hackathonId == null) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("<html>Impossibile recuperare<br>l'ID dell'hackathon.</html>");
                            return;
                        }
                        long teamId = selezionato.getId();

                        // Salvo dati del wizard
                        datiDocumentoU.clear();
                        datiDocumentoU.add(nomeTeam);                     // 0
                        datiDocumentoU.add(titoloHackathon);              // 1
                        datiDocumentoU.add(String.valueOf(teamId));       // 2
                        datiDocumentoU.add(String.valueOf(hackathonId));  // 3

                        passoDocumentoU = 1;

                        dashboardUtente.getAreaDiTesto().setText(
                                "<html><b>Caricamento documento</b><br><br>" +
                                        "<b>Hackathon:</b> " + titoloHackathon + "<br>" +
                                        "<b>Team:</b> " + nomeTeam + "<br>" +
                                        "<b>Contenuto:</b> —</html>"
                        );
                        aggiornaGuidaDashboardUtente();
                        dashboardUtente.getFieldScrittura().setText("");
                        dashboardUtente.getFieldScrittura().requestFocusInWindow();
                        return;
                    }

                    // STEP 1: inserimento contenuto documento
                    if (passoDocumentoU == 1) {
                        if (datiDocumentoU.size() < 4) return;

                        String contenutoInput = dashboardUtente.getFieldScrittura().getText();
                        String contenuto = (contenutoInput == null) ? "" : contenutoInput.trim();

                        if (contenuto.isEmpty()) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("<html>Il contenuto del documento<br>non può essere vuoto.</html>");
                            return;
                        }

                        // salvo contenuto in posizione 4
                        if (datiDocumentoU.size() == 4) datiDocumentoU.add(contenuto);
                        else datiDocumentoU.set(4, contenuto);

                        passoDocumentoU = 2;

                        String nomeTeam = datiDocumentoU.get(0);
                        String titoloHackathon = datiDocumentoU.get(1);


                        String contenutoPreview = limitaPerAreaDiTesto(contenuto, 120);

                        dashboardUtente.getAreaDiTesto().setText(
                                "<html><b>Caricamento documento</b><br><br>" +
                                        "<b>Hackathon:</b> " + titoloHackathon + "<br>" +
                                        "<b>Team:</b> " + nomeTeam + "<br>" +
                                        "<b>Contenuto:</b> " + contenutoPreview + "</html>"
                        );


                        dashboardUtente.getAvantiButton().setVisible(false);
                        dashboardUtente.getIscrivitiButton().setVisible(true);
                        dashboardUtente.getIscrivitiButton().setText("Carica documento");
                        dashboardUtente.getFieldScrittura().setText("");
                        aggiornaGuidaDashboardUtente();
                        return;
                    }

                    return; // fine ramo documento
                }

                // nessun wizard attivo → non fare nulla
            });




            dashboardUtente.getIndietroButton().addActionListener(e -> {

                JLabel guida = dashboardUtente.getMessaggioErroreOrg();
                guida.setForeground(Color.WHITE);
                aggiornaGuidaDashboardUtente();

                // 0) wizard DOCUMENTO
                if (passoDocumentoU >= 0) {

                    if (passoDocumentoU == 1) {
                        // torna alla scelta del team
                        passoDocumentoU = 0;
                        datiDocumentoU.clear();

                        dashboardUtente.getAreaDiTesto().setText(
                                "<html><b>Caricamento documento</b><br><br>" +
                                        "<b>Hackathon:</b> —<br>" +
                                        "<b>Team:</b> —<br>" +
                                        "<b>Contenuto:</b> —</html>"
                        );
                        dashboardUtente.getIscrivitiButton().setVisible(false);
                        dashboardUtente.getAvantiButton().setVisible(true);
                        dashboardUtente.getFieldScrittura().setText("");
                        aggiornaGuidaDashboardUtente();
                        return;
                    }

                    if (passoDocumentoU == 2) {
                        // torna a inserimento contenuto
                        passoDocumentoU = 1;

                        String nomeTeam = (datiDocumentoU.size() > 0) ? datiDocumentoU.get(0) : "—";
                        String titoloHackathon = (datiDocumentoU.size() > 1) ? datiDocumentoU.get(1) : "—";
                        String contenuto = (datiDocumentoU.size() > 4) ? datiDocumentoU.get(4) : "";

                        dashboardUtente.getAreaDiTesto().setText(
                                "<html><b>Caricamento documento</b><br><br>" +
                                        "<b>Hackathon:</b> " + titoloHackathon + "<br>" +
                                        "<b>Team:</b> " + nomeTeam + "<br>" +
                                        "<b>Contenuto:</b> —</html>"
                        );

                        dashboardUtente.getIscrivitiButton().setVisible(false);
                        dashboardUtente.getAvantiButton().setVisible(true);
                        dashboardUtente.getFieldScrittura().setText(contenuto);
                        aggiornaGuidaDashboardUtente();
                        return;
                    }
                }


                // 1) wizard TEAM
                if (passoTeamU >= 0) {

                    if (passoTeamU == 1) {
                        // torna alla scelta hackathon
                        passoTeamU = 0;
                        datiTeamU.clear();

                        dashboardUtente.getAreaDiTesto().setText(
                                "<html><b>Creazione Team</b><br><br>"
                                        + "<b>Titolo hackathon:</b> —<br>"
                                        + "<b>Nome team:</b> —</html>"
                        );
                        aggiornaGuidaDashboardUtente();
                        dashboardUtente.getIscrivitiButton().setVisible(false);
                        dashboardUtente.getAvantiButton().setVisible(true);
                        dashboardUtente.getFieldScrittura().setText("");
                        dashboardUtente.getFieldScrittura().requestFocusInWindow();
                        return;
                    }

                    if (passoTeamU == 2) {
                        // torna all'inserimento nome team
                        passoTeamU = 1;
                        String titoloHackathon = datiTeamU.isEmpty() ? "—" : datiTeamU.get(0);

                        dashboardUtente.getAreaDiTesto().setText(
                                "<html><b>Creazione Team</b><br><br>"
                                        + "<b>Titolo hackathon:</b> " + titoloHackathon + "<br>"
                                        + "<b>Nome team:</b> —</html>"
                        );
                        aggiornaGuidaDashboardUtente();
                        dashboardUtente.getIscrivitiButton().setVisible(false);
                        dashboardUtente.getAvantiButton().setVisible(true);
                        dashboardUtente.getFieldScrittura().setText("");
                        dashboardUtente.getFieldScrittura().requestFocusInWindow();
                        return;
                    }
                }

                // 2) wizard ISCRIZIONE
                if (passoIscrizioneU >= 0) {
                    // torna sempre allo step 0
                    passoIscrizioneU = 0;
                    datiIscrizioneU.clear();

                    dashboardUtente.getAreaDiTesto().setText(
                            "<html><b>Iscrizione</b><br><br><b>Titolo:</b> —</html>"
                    );
                    aggiornaGuidaDashboardUtente();
                    dashboardUtente.getIscrivitiButton().setVisible(false);
                    dashboardUtente.getAvantiButton().setVisible(true);
                    dashboardUtente.getFieldScrittura().setText("");
                    dashboardUtente.getFieldScrittura().requestFocusInWindow();
                }
            });


            dashboardUtente.getIscrivitiButton().addActionListener(e -> {

                JLabel guida = dashboardUtente.getMessaggioErroreOrg();
                guida.setForeground(java.awt.Color.WHITE);

                // --------------------------------------------------------
// 0) Conferma CARICAMENTO DOCUMENTO
// --------------------------------------------------------
                if (passoDocumentoU == 2 && datiDocumentoU.size() >= 5) {

                    String nomeTeam = datiDocumentoU.get(0);
                    String titoloHackathon = datiDocumentoU.get(1);
                    long teamId = Long.parseLong(datiDocumentoU.get(2));
                    long hackathonId = Long.parseLong(datiDocumentoU.get(3));
                    String contenuto = datiDocumentoU.get(4);

                    try {
                        dao.DocumentoDAO ddao = new daoImpl.DocumentoDAOImpl();
                        ddao.salvaDocumento(teamId, hackathonId, contenuto, java.time.LocalDateTime.now());

                        javax.swing.JOptionPane.showMessageDialog(frame2,
                                "Documento caricato per il team \"" + nomeTeam +
                                        "\" nell'hackathon \"" + titoloHackathon + "\".",
                                "Documento caricato",
                                javax.swing.JOptionPane.INFORMATION_MESSAGE);

                        // reset wizard documento
                        passoDocumentoU = -1;
                        datiDocumentoU.clear();
                        dashboardUtente.getFieldScrittura().setText("");
                        dashboardUtente.getIscrivitiButton().setVisible(false);
                        dashboardUtente.getAvantiButton().setVisible(true);

                        // ricarico "I miei Team"
                        dashboardUtente.getIMieiTeam().doClick();

                    } catch (Exception ex) {
                        guida.setForeground(new java.awt.Color(180, 26, 0));
                        guida.setText("<html>Errore durante il caricamento<br>del documento: " + ex.getMessage() + "</html>");
                    }
                    return;
                }




                // --------------------------------------------------------
                // 1) Conferma CREAZIONE TEAM
                // --------------------------------------------------------
                if (passoTeamU == 2 && datiTeamU.size() >= 2) {

                    String titoloHackathon = datiTeamU.get(0);
                    String nomeTeam = datiTeamU.get(1);

                    try {
                        dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
                        Long hackathonId = hdao.findIdByTitolo(titoloHackathon);
                        if (hackathonId == null) {
                            guida.setForeground(new java.awt.Color(180, 26, 0));
                            guida.setText("Impossibile recuperare l'ID dell'hackathon.");
                            return;
                        }

                        dao.TeamDAO tdao = new daoImpl.TeamDAOImpl();

                        // sicurezza extra
                        if (tdao.utenteHaTeam(hackathonId, utente.getMail())) {
                            guida.setForeground(new java.awt.Color(180, 26, 0));
                            guida.setText("Hai già un team per questo hackathon.");
                            return;
                        }

                        tdao.creaTeam(nomeTeam, hackathonId, utente.getMail());

                        javax.swing.JOptionPane.showMessageDialog(frame2,
                                "Team \"" + nomeTeam + "\" creato con successo per \"" + titoloHackathon + "\".",
                                "Team creato",
                                javax.swing.JOptionPane.INFORMATION_MESSAGE);

                        // reset stato wizard team
                        passoTeamU = -1;
                        datiTeamU.clear();

                        guida.setText("Team creato.");
                        dashboardUtente.getIscrivitiButton().setVisible(false);
                        dashboardUtente.getAvantiButton().setVisible(true);
                        dashboardUtente.getFieldScrittura().setText("");
                        // Ricarico la schermata "I miei Hackaton"
                        dashboardUtente.getIMieiHackaton().doClick();


                    } catch (Exception ex) {
                        guida.setForeground(new java.awt.Color(180, 26, 0));
                        guida.setText("Errore durante la creazione del team: " + ex.getMessage());
                    }
                    return;
                }

                // --------------------------------------------------------
                // 2) Conferma ISCRIZIONE
                // --------------------------------------------------------
                if (passoIscrizioneU != 1 || datiIscrizioneU.isEmpty()) return;

                String titolo = datiIscrizioneU.get(0);

                try {
                    // 1) ritrova hackathon per sicurezza
                    dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
                    java.util.List<model.Hackathon> tutti = hdao.findAll();
                    model.Hackathon target = null;
                    for (model.Hackathon h : tutti) {
                        String t = (h.getTitolo() == null) ? "" : h.getTitolo().trim();
                        if (t.equalsIgnoreCase(titolo.trim())) {
                            target = h;
                            break;
                        }
                    }
                    if (target == null) {
                        guida.setForeground(new java.awt.Color(180, 26, 0));
                        guida.setText("Hackathon non trovata.");
                        return;
                    }

                    Long hackathonId = hdao.findIdByTitolo(target.getTitolo());
                    if (hackathonId == null) {
                        guida.setForeground(new java.awt.Color(180, 26, 0));
                        guida.setText("Impossibile recuperare l'ID dell'hackathon.");
                        return;
                    }

                    dao.IscrizioneDAO idao = new daoImpl.IscrizioneDAOImpl();
                    if (idao.isIscritto(hackathonId, utente.getMail())) {
                        guida.setForeground(new java.awt.Color(180, 26, 0));
                        guida.setText("Sei già iscritto.");
                        return;
                    }
                    int iscritti = idao.countIscritti(hackathonId);
                    if (iscritti >= target.getMaxPartecipanti()) {
                        guida.setForeground(new java.awt.Color(180, 26, 0));
                        guida.setText("Capienza raggiunta.");
                        return;
                    }

                    // 4) INSERT iscrizione
                    java.time.LocalDate oggi = java.time.LocalDate.now();
                    idao.iscrivi(hackathonId, utente.getMail(), oggi);

                    javax.swing.JOptionPane.showMessageDialog(frame2,
                            "Iscrizione completata per \"" + target.getTitolo() + "\".",
                            "Iscrizione completata",
                            javax.swing.JOptionPane.INFORMATION_MESSAGE);

                    // reset wizard iscrizione
                    passoIscrizioneU = -1;
                    datiIscrizioneU.clear();
                    guida.setText("Iscrizione completata.");

                    dashboardUtente.getIscrivitiButton().setVisible(false);
                    dashboardUtente.getAvantiButton().setVisible(true);
                    dashboardUtente.getFieldScrittura().setText("");
                    dashboardUtente.getHackatonDisponibili().doClick();


                } catch (Exception ex) {
                    guida.setForeground(new java.awt.Color(180, 26, 0));
                    guida.setText("Errore durante l'iscrizione: " + ex.getMessage());
                }

            });

            // ===== Classifica =====
            dashboardUtente.getClassificaButton().addActionListener(e -> {
                // disattivo tutti i wizard
                passoIscrizioneU = -1;
                datiIscrizioneU.clear();
                passoTeamU = -1;
                datiTeamU.clear();
                passoDocumentoU = -1;
                datiDocumentoU.clear();

                // mostro solo pannello logico + scrollPane
                dashboardUtente.getPannelloLogico().setVisible(true);
                dashboardUtente.getScrollPaneVisualizza().setVisible(true);
                dashboardUtente.getTextAreaVisualizza().setVisible(true);
                dashboardUtente.getAreaDiTesto().setVisible(true);

                dashboardUtente.getFieldScrittura().setVisible(false);
                dashboardUtente.getAvantiButton().setVisible(false);
                dashboardUtente.getIndietroButton().setVisible(false);
                dashboardUtente.getIscrivitiButton().setVisible(false);

                JLabel guida = dashboardUtente.getMessaggioErroreOrg();
                guida.setForeground(Color.WHITE);
                guida.setText(""); // solo lettura, niente guida wizard

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

                textArea.setText("");
                textArea.append("--- Classifiche dei miei hackathon ---\n\n");

                // 1) prendo i team dell'utente
                dao.TeamDAO tdao = new daoImpl.TeamDAOImpl();
                java.util.List<model.TeamInfo> mieiTeam = tdao.findTeamsByUtente(utente.getMail());

                if (mieiTeam.isEmpty()) {
                    textArea.append("Non fai parte di alcun team.\n");
                    return;
                }

                // 2) raggruppo per titolo hackathon
                java.util.Map<String, java.util.List<String>> mieiTeamPerHackathon = new java.util.HashMap<>();
                for (model.TeamInfo info : mieiTeam) {
                    String titoloHackathon = info.getTitoloHackathon();
                    mieiTeamPerHackathon
                            .computeIfAbsent(titoloHackathon, k -> new java.util.ArrayList<>())
                            .add(info.getNomeTeam());
                }

                dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
                dao.ClassificaDAO cdao = new daoImpl.ClassificaDAOImpl();

                // 3) per ogni hackathon, stampo la classifica
                for (String titoloHackathon : mieiTeamPerHackathon.keySet()) {
                    Long hackathonId = hdao.findIdByTitolo(titoloHackathon);
                    if (hackathonId == null) {
                        continue;
                    }

                    java.util.List<model.Classifica> classifica =
                            cdao.findClassificaByHackathon(hackathonId);

                    textArea.append("Hackathon: " + titoloHackathon + "\n");

                    if (classifica.isEmpty()) {
                        textArea.append("  Nessun voto registrato.\n\n");
                        continue;
                    }

                    java.util.List<String> mieiNomiTeam = mieiTeamPerHackathon.get(titoloHackathon);

                    int posizione = 1;
                    for (model.Classifica riga : classifica) {
                        String nomeTeam = riga.getTeam().getNome(); // usa il tuo getter
                        int punteggio = riga.getPunteggio();

                        boolean mio = mieiNomiTeam.contains(nomeTeam);

                        textArea.append("  " + posizione + ") "
                                + nomeTeam
                                + " – punteggio: " + punteggio);

                        if (mio) {
                            textArea.append("  <-- il tuo team");
                        }
                        textArea.append("\n");
                        posizione++;
                    }

                    textArea.append("\n");
                }

                scrollPane.revalidate();
                scrollPane.repaint();
            });









        }

    private void gestisciDashboardGiudice(Giudice giudice){
        JLabel messaggioBenvenuto = dashboardGiudice.getMessaggioBenvenuto();
        frame2 = new JFrame("HackatonDashboard - Giudice" );
        frame2.setContentPane(dashboardGiudice.getDashboardGiudice());
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.pack();
        frame2.setLocationRelativeTo(null);
        frame2.setVisible(true);
        frame2.setResizable(false);

        JTextField inputFieldG = dashboardGiudice.getFieldScrittura();
        if (inputFieldG != null) {
            inputFieldG.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    JLabel labelInput = dashboardGiudice.getMessaggioErroreOrg();
                    if (labelInput == null) return;

                    // Se era in errore (rosso), ripristino la guida corretta per lo step corrente
                    if (labelInput.getForeground().equals(new Color(180, 26, 0))) {
                        aggiornaGuidaDashboardGiudice();
                    } else {
                        // altrimenti mi assicuro solo che il colore sia bianco
                        labelInput.setForeground(Color.WHITE);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    // qui non serve fare nulla
                }
            });
        }
        // messaggio in alto
        if (messaggioBenvenuto != null) {
            messaggioBenvenuto.setText("Giudice, " + giudice.getMail() + " ");
        }

        // ===== BOTTONE "VISUALIZZA INVITI" =====
        dashboardGiudice.getVisualizzaInvitiButton().addActionListener(e -> {

            // reset wizard giudice
            passoInvitoG = 0;
            datiInvitoG.clear();

            JPanel pannelloLogico = dashboardGiudice.getPannelloLogico();
            if (pannelloLogico != null) {
                pannelloLogico.setVisible(true);
            }

            JScrollPane scroll = dashboardGiudice.getScrollPaneVisualizza();
            JTextArea textArea = dashboardGiudice.getTextAreaVisualizza();
            JLabel areaDiTesto = dashboardGiudice.getAreaDiTesto();
            JLabel guida = dashboardGiudice.getMessaggioErroreOrg();

            // titolo procedurale sopra
            if (areaDiTesto != null) {
                areaDiTesto.setVisible(true);
                areaDiTesto.setText(
                        "<html><b>Accettazione invito</b><br><br>" +
                                "<b>Hackathon:</b> —</html>"
                );
            }

            // guida passo 0
            if (guida != null) {
                guida.setForeground(Color.WHITE);
                guida.setText("<html>Inserisci il titolo<br>dell'hackathon che vuoi accettare.</html>");
            }

            // campo input + bottoni wizard
            JTextField field = dashboardGiudice.getFieldScrittura();
            if (field != null) {
                field.setVisible(true);
                field.setText("");
                field.requestFocusInWindow();
            }

            JButton avanti = dashboardGiudice.getAvantiButton();
            if (avanti != null) {
                avanti.setVisible(true);
            }
            JButton indietro = dashboardGiudice.getIndietroButton();
            if (indietro != null) {
                indietro.setVisible(false); // se vuoi dopo gli dai una funzione
            }
            JButton iscriviti = dashboardGiudice.getIscrivitiButton();
            if (iscriviti != null) {
                iscriviti.setVisible(false);
            }

            // prepara textArea / scroll con la lista inviti
            if (scroll != null) scroll.setVisible(true);
            if (textArea == null) return;

            Color bg = pannelloLogico != null
                    ? pannelloLogico.getBackground()
                    : dashboardGiudice.getDashboardGiudice().getBackground();

            textArea.setVisible(true);
            textArea.setEditable(false);
            textArea.setFocusable(false);
            textArea.setBackground(bg);
            textArea.setForeground(Color.WHITE);
            textArea.setLineWrap(false);
            textArea.setWrapStyleWord(false);
            if (scroll != null) {
                scroll.getViewport().setBackground(bg);
                scroll.setBorder(null);
            }

            textArea.setText("");
            textArea.append("--- Inviti come giudice ---\n\n");

            dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
            java.util.List<model.Hackathon> inviti =
                    hdao.findInvitiPerGiudice(giudice.getMail());

            if (inviti.isEmpty()) {
                textArea.append("Non hai inviti al momento.\n");
            } else {
                for (model.Hackathon h : inviti) {
                    textArea.append("Titolo: " + h.getTitolo() + "\n");
                    textArea.append("Organizzatore: " + h.getOrganizzatore() + "\n");
                    textArea.append("Sede: " + h.getSede() + "\n");
                    textArea.append("Inizio: " + h.getInizio() + "\n");
                    textArea.append("Iscrizioni: " + h.getInizioIscrizioni()
                            + " → " + h.getFineIscrizioni() + "\n");
                    textArea.append("----------------------------------------\n");
                }
            }

            if (scroll != null) {
                scroll.revalidate();
                scroll.repaint();
            }
        });

        dashboardGiudice.getAvantiButton().addActionListener(e2 -> {

            JLabel guida = dashboardGiudice.getMessaggioErroreOrg();
            if (guida != null) {
                guida.setForeground(Color.WHITE);
            }

            // se non è attivo il wizard giudice, non fare nulla
            if (passoInvitoG < 0) {
                return;
            }

            // ===== STEP 0: inserimento titolo hackathon =====
            if (passoInvitoG == 0) {
                JTextField field = dashboardGiudice.getFieldScrittura();
                if (field == null) return;

                String titoloInput = field.getText();
                String titolo = (titoloInput == null) ? "" : titoloInput.trim();

                if (titolo.isEmpty()) {
                    if (guida != null) {
                        guida.setForeground(new Color(180, 26, 0));
                        guida.setText("<html>Il titolo non può<br>essere vuoto.</html>");
                    }
                    return;
                }

                // controllo che il titolo sia tra gli inviti del giudice
                dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
                java.util.List<model.Hackathon> inviti =
                        hdao.findInvitiPerGiudice(giudice.getMail());

                model.Hackathon target = null;
                for (model.Hackathon h : inviti) {
                    if (h.getTitolo() != null &&
                            h.getTitolo().trim().equalsIgnoreCase(titolo)) {
                        target = h;
                        break;
                    }
                }

                if (target == null) {
                    if (guida != null) {
                        guida.setForeground(new Color(180, 26, 0));
                        guida.setText("<html>Non risulti invitato<br>a un hackathon con questo titolo.</html>");
                    }
                    return;
                }

                // salvo dati wizard
                datiInvitoG.clear();
                datiInvitoG.add(target.getTitolo());
                passoInvitoG = 1;

                // aggiorno area di testo (riepilogo)
                JLabel areaDiTesto = dashboardGiudice.getAreaDiTesto();
                if (areaDiTesto != null) {
                    areaDiTesto.setText(
                            "<html><b>Accettazione invito</b><br><br>" +
                                    "<b>Hackathon:</b> " + target.getTitolo() + "</html>"
                    );
                }

                // guida step 1
                if (guida != null) {
                    guida.setForeground(Color.WHITE);
                    guida.setText("<html>Clicca 'Partecipa'<br>per accettare l'invito.</html>");
                }

                // mostra il bottone "Partecipa"
                JButton avanti = dashboardGiudice.getAvantiButton();
                JButton iscriviti = dashboardGiudice.getIscrivitiButton();
                if (avanti != null) avanti.setVisible(false);
                if (iscriviti != null) {
                    iscriviti.setVisible(true);
                    iscriviti.setText("Partecipa");
                }

                field.setText("");
                return;
            }
        });

        dashboardGiudice.getIscrivitiButton().addActionListener(e3 -> {

            // se il wizard invito non è nello step giusto, ignoro
            if (passoInvitoG != 1 || datiInvitoG.isEmpty()) {
                return;
            }

            JLabel guida = dashboardGiudice.getMessaggioErroreOrg();
            if (guida != null) {
                guida.setForeground(Color.WHITE);
            }

            String titoloHackathon = datiInvitoG.get(0);

            dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
            boolean ok = hdao.accettaInvitoGiudice(titoloHackathon, giudice.getMail());

            if (!ok) {
                if (guida != null) {
                    guida.setForeground(new Color(180, 26, 0));
                    guida.setText("<html>Errore nell'accettazione<br>dell'invito.</html>");
                }
                return;
            }

            // invito accettato
            if (guida != null) {
                guida.setForeground(Color.WHITE);
                guida.setText("<html>Invito accettato!<br>Ora sei giudice di questo hackathon.</html>");
            }

            // reset wizard
            passoInvitoG = -1;
            datiInvitoG.clear();

            // aggiorno la lista inviti rifacendo click sul bottone
            dashboardGiudice.getVisualizzaInvitiButton().doClick();
        });

        dashboardGiudice.getIndietroButton().addActionListener(e -> {

            // Se il wizard invito non è attivo, non faccio nulla
            if (passoInvitoG < 0) {
                return;
            }

            JLabel guida = dashboardGiudice.getMessaggioErroreOrg();
            JLabel areaDiTesto = dashboardGiudice.getAreaDiTesto();
            JTextField field = dashboardGiudice.getFieldScrittura();
            JButton avanti = dashboardGiudice.getAvantiButton();
            JButton partecipa = dashboardGiudice.getIscrivitiButton();

            // ===== TORNA DA STEP 1 A STEP 0 =====
            if (passoInvitoG == 1) {
                passoInvitoG = 0;
                datiInvitoG.clear();

                // ripristino testo guida e area procedurale iniziale
                if (areaDiTesto != null) {
                    areaDiTesto.setText(
                            "<html><b>Accettazione invito</b><br><br>" +
                                    "<b>Hackathon:</b> —</html>"
                    );
                }
                if (guida != null) {
                    guida.setForeground(Color.WHITE);
                    guida.setText("<html>Inserisci il titolo<br>dell'hackathon che vuoi accettare.</html>");
                }

                if (field != null) {
                    field.setVisible(true);
                    field.setText("");
                    field.requestFocusInWindow();
                }
                if (avanti != null) avanti.setVisible(true);
                if (partecipa != null) partecipa.setVisible(false);

                return;
            }

            // ===== DA STEP 0 → ESCE DAL WIZARD =====
            if (passoInvitoG == 0) {
                passoInvitoG = -1;
                datiInvitoG.clear();

                if (guida != null) {
                    guida.setText("");
                }
                if (areaDiTesto != null) {
                    areaDiTesto.setText("");
                }
                if (field != null) {
                    field.setText("");
                    field.setVisible(false);
                }
                if (avanti != null) avanti.setVisible(false);
                if (partecipa != null) partecipa.setVisible(false);

                return;
            }
        });


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


    // Guida testuale per la DashboardUtente, in base allo stato dei wizard
    private void aggiornaGuidaDashboardUtente() {
        if (dashboardUtente == null) return;

        JLabel guida = dashboardUtente.getMessaggioErroreOrg();
        guida.setForeground(Color.WHITE);

        // Wizard ISCRIZIONE (Hackaton disponibili)
        if (passoIscrizioneU >= 0) {
            if (passoIscrizioneU == 0) {
                guida.setText("<html>Inserisci il titolo<br>dell'hackathon.</html>");
            } else if (passoIscrizioneU == 1) {
                guida.setText("<html>Clicca 'Iscriviti'<br>per confermare.</html>");
            }
            return;
        }

        // Wizard TEAM (I miei Hackaton → Crea Team)
        if (passoTeamU >= 0) {
            if (passoTeamU == 0) {
                guida.setText("<html>Inserisci il titolo<br>dell'hackathon.</html>");
            } else if (passoTeamU == 1) {
                guida.setText("<html>Inserisci il nome<br>del team.</html>");
            } else if (passoTeamU == 2) {
                guida.setText("<html>Clicca 'Crea Team'<br>per confermare.</html>");
            }
            return;
        }
        if (passoDocumentoU >= 0) {

            guida.setForeground(Color.WHITE);

            if (passoDocumentoU == 0) {
                guida.setText("<html>Inserisci il nome<br>del team.</html>");
            } else if (passoDocumentoU == 1) {
                guida.setText("<html>Scrivi il contenuto<br>del documento.</html>");
            } else if (passoDocumentoU == 2) {
                guida.setText("<html>Clicca 'Carica documento'<br>per confermare.</html>");
            }
            return;
        }

        // Nessun wizard attivo
        guida.setText("");
    }

    // Guida per la dashboard Giudice in base allo stato del wizard invito
    private void aggiornaGuidaDashboardGiudice() {
        if (dashboardGiudice == null) return;

        JLabel guida = dashboardGiudice.getMessaggioErroreOrg();
        if (guida == null) return;

        guida.setForeground(Color.WHITE);

        // Wizard ACCETTAZIONE INVITO (Visualizza inviti)
        if (passoInvitoG >= 0) {
            if (passoInvitoG == 0) {
                guida.setText("<html>Inserisci il titolo<br>dell'hackathon che vuoi accettare.</html>");
            } else if (passoInvitoG == 1) {
                guida.setText("<html>Clicca 'Partecipa'<br>per accettare l'invito.</html>");
            }
            return;
        }

        // Nessun wizard attivo
        guida.setText("");
    }


    // Restituisce una anteprima del documento: max 5 righe e ~300 caratteri
    private String anteprimaDocumento(String contenuto) {
        if (contenuto == null) return "";
        contenuto = contenuto.trim();
        if (contenuto.isEmpty()) return "";

        int maxRighe = 5;
        int maxChar = 300;

        String[] righe = contenuto.split("\\R"); // split su \n, \r\n, ecc.
        StringBuilder sb = new StringBuilder();
        int righeUsate = 0;

        for (String riga : righe) {
            if (righeUsate >= maxRighe) break;

            // se supero il limite di caratteri, taglio l'ultima riga
            if (sb.length() + riga.length() + 1 > maxChar) {
                int spazio = maxChar - sb.length();
                if (spazio > 0) {
                    if (sb.length() > 0) sb.append("\n");
                    if (riga.length() > spazio) {
                        sb.append(riga, 0, spazio);
                    } else {
                        sb.append(riga);
                    }
                }
                righeUsate++;
                break;
            } else {
                if (sb.length() > 0) sb.append("\n");
                sb.append(riga);
                righeUsate++;
            }
        }

        if (sb.length() < contenuto.length()) {
            sb.append("...");
        }

        return sb.toString();
    }

    private String limitaPerAreaDiTesto(String s, int max) {
        if (s == null) return "";
        String trimmed = s.trim();
        if (trimmed.length() <= max) return trimmed;
        return trimmed.substring(0, max) + "...";
    }

}
