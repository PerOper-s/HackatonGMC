package controller;

import gui.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
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

                    String[] domande = {
                            "Inserisci il titolo.",
                            "Inserisci la sede.",
                            "Inserisci il n° max di partecipanti.",
                            "Inserisci la grandezza max del team.",
                            "Inserisci la data di inizio (GG/MM/AAAA).",
                            "Data inizio iscrizioni (GG/MM/AAAA).",
                            "Data fine iscrizioni (GG/MM/AAAA)."
                    };

                    if (passoCreazione < domande.length) {
                        labelInput.setText(domande[passoCreazione]);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                }
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

                    // Campo vuoto → errore
                    if (inputUtente.isEmpty()) {
                        dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                        dashboardOrganizzatore.getMessaggioErroreOrg().setText("Il campo non può essere vuoto.");
                        return;
                    }

                    // === Flusso INVITO GIUDICE attivo? ===
                    if (passoInvitoGiudice >= 0) {
                        if (passoInvitoGiudice == 0) {
                            // Step 0: verifica che il titolo esista tra i TUOI hackathon
                            Hackathon scelto = null;
                            for (Hackathon h : listaHackathon) {
                                if (h.getTitolo().equalsIgnoreCase(inputUtente) &&
                                        h.getOrganizzatore().equals(organizzatore.getMail())) {
                                    scelto = h; break;
                                }
                            }
                            if (scelto == null) {
                                dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                                dashboardOrganizzatore.getMessaggioErroreOrg().setText("Hackathon non trovata o non appartiene all'organizzatore.");
                                return;
                            }
                            // ok → memorizza titolo e passa allo step 1 (email giudice)
                            if (datiInvitoGiudice.size() == 0) datiInvitoGiudice.add(inputUtente);
                            else datiInvitoGiudice.set(0, inputUtente);

                            // Riepilogo live + guida step successivo
                            gestisciPannelloLogicoInvitoGiudice();
                            dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(Color.WHITE);
                            dashboardOrganizzatore.getMessaggioErroreOrg().setText("Inserisci la email del giudice da invitare");

                            passoInvitoGiudice = 1;
                            dashboardOrganizzatore.getFieldScrittura().setText("");
                            return;
                        }

                        if (passoInvitoGiudice == 1) {
                            // Step 1: valida email + "Giudice non esistente"
                            String emailGiudice = inputUtente;
                            String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
                            if (!emailGiudice.matches(emailRegex)) {
                                dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                                dashboardOrganizzatore.getMessaggioErroreOrg().setText("Formato email non valido.");
                                return;
                            }
                            // Controllo su DB che esista un utente con ruolo 'giudice'
                            UtenteDAO udao = new UtenteDAOImpl();
                            boolean esisteGiudice = udao.esisteUtente(emailGiudice, "giudice");
                            if (!esisteGiudice) {
                                dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                                dashboardOrganizzatore.getMessaggioErroreOrg().setText("Giudice non esistente: registrare l'utente con ruolo 'giudice'.");
                                return;
                            }
                            // ok → memorizza email
                            if (datiInvitoGiudice.size() == 1) datiInvitoGiudice.add(emailGiudice);
                            else if (datiInvitoGiudice.size() >= 2) datiInvitoGiudice.set(1, emailGiudice);

                            // Porta il wizard allo stato "conferma" (step 2) e aggiorna la maschera
                            passoInvitoGiudice = 2;
                            gestisciPannelloLogicoInvitoGiudice(); // qui AreaDiTesto mostra titolo+email e bottone "Invita"
                            dashboardOrganizzatore.getFieldScrittura().setText("");
                            return;
                        }

                        // Se per qualche motivo si preme Avanti quando è già in stato conferma, non fare nulla.
                        return;
                    }

                    // === Altrimenti: flusso CREAZIONE HACKATHON (già esistente) ===
                    if ((passoCreazione == 2 || passoCreazione == 3) && !inputUtente.matches("\\d+")) {
                        dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                        dashboardOrganizzatore.getMessaggioErroreOrg().setText("Errore: inserisci un valore numerico.");
                        return;
                    }
                    if ((passoCreazione == 4 || passoCreazione == 5 || passoCreazione == 6) && !isDateValid(inputUtente)) {
                        dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                        dashboardOrganizzatore.getMessaggioErroreOrg().setText("Formato data non valido. Usa GG/MM/AAAA.");
                        return;
                    }
                    try {
                        if (passoCreazione == 4) {
                            if (new SimpleDateFormat("dd/MM/yyyy").parse(inputUtente).before(getOggiSenzaOrario())) {
                                dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                                dashboardOrganizzatore.getMessaggioErroreOrg().setText("La data dell'hackathon non può essere nel passato.");
                                return;
                            }
                        }
                        if (passoCreazione == 5) {
                            Date dataInizioIscrizioni = new SimpleDateFormat("dd/MM/yyyy").parse(inputUtente);
                            Date dataInizioHackathon = new SimpleDateFormat("dd/MM/yyyy").parse(datiHackaton.get(4));
                            if (dataInizioIscrizioni.before(getOggiSenzaOrario())) {
                                dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                                dashboardOrganizzatore.getMessaggioErroreOrg().setText("L'inizio delle iscrizioni non può essere nel passato.");
                                return;
                            }
                            if (!dataInizioIscrizioni.before(dataInizioHackathon)) {
                                dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                                dashboardOrganizzatore.getMessaggioErroreOrg().setText("Le iscrizioni devono iniziare prima dell'inizio dell'hackathon.");
                                return;
                            }
                        }
                        if (passoCreazione == 6) {
                            Date dataFineIscrizioni = new SimpleDateFormat("dd/MM/yyyy").parse(inputUtente);
                            Date dataInizioIscrizioni = new SimpleDateFormat("dd/MM/yyyy").parse(datiHackaton.get(5));
                            Date dataInizioHackathon = new SimpleDateFormat("dd/MM/yyyy").parse(datiHackaton.get(4));
                            if (!dataFineIscrizioni.after(dataInizioIscrizioni)) {
                                dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                                dashboardOrganizzatore.getMessaggioErroreOrg().setText("La data di fine iscrizioni deve essere successiva all'inizio delle iscrizioni.");
                                return;
                            }
                            if (!dataFineIscrizioni.before(dataInizioHackathon)) {
                                dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                                dashboardOrganizzatore.getMessaggioErroreOrg().setText("Le iscrizioni devono concludersi prima dell'inizio dell'hackathon.");
                                return;
                            }
                        }
                    } catch (ParseException ex) {
                        return;
                    }

                    // Salva e avanza (creazione hackathon)
                    datiHackaton.add(inputUtente);
                    passoCreazione++;
                    gestisciPannelloLogicoDashboardOrganizzatore();
                }
            });



            dashboardOrganizzatore.getIndietroButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (passoInvitoGiudice >= 0) {
                        // Flusso invito giudici
                        if (passoInvitoGiudice > 0) {
                            // ripristina ultimo dato e torna indietro
                            int lastIndex = datiInvitoGiudice.size() - 1;
                            String valorePrecedente = "";
                            if (lastIndex >= 0) {
                                valorePrecedente = datiInvitoGiudice.remove(lastIndex);
                            }
                            passoInvitoGiudice--;
                            gestisciPannelloLogicoInvitoGiudice(); // aggiorna guida/riepilogo/bottoni
                            dashboardOrganizzatore.getFieldScrittura().setText(valorePrecedente);
                            dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(Color.WHITE);
                        } else {
                            // passoInvitoGiudice == 0 → esci dal mini-wizard inviti
                            dashboardOrganizzatore.getPannelloLogico().setVisible(false);
                            passoInvitoGiudice = -1;
                            datiInvitoGiudice.clear();
                        }
                        return;
                    }

                    // Flusso creazione hackathon (già esistente)
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
                    // Mostra i pannelli necessari
                    dashboardOrganizzatore.getPannelloLogico().setVisible(true);
                    dashboardOrganizzatore.getScrollPaneVisualizza().setVisible(true);
                    dashboardOrganizzatore.getAreaDiTesto().setVisible(true);
                    dashboardOrganizzatore.getFieldScrittura().setVisible(true);
                    dashboardOrganizzatore.getAvantiButton().setVisible(true);
                    dashboardOrganizzatore.getIndietroButton().setVisible(true);
                    dashboardOrganizzatore.getCreateButton().setVisible(false);
                    dashboardOrganizzatore.getMessaggioErroreOrg().setVisible(true);
                    dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(Color.WHITE);
                    dashboardOrganizzatore.getMessaggioErroreOrg().setText("Inserisci il titolo del tuo hackathon");

                    // Carico "I tuoi hackathon" dal DB e li mostro SOLO come titoli
                    HackathonDAO dao = new HackathonDAOImpl();
                    List<Hackathon> miei = dao.findByOrganizzatore(organizzatore.getMail());
                    listaHackathon.clear();
                    listaHackathon.addAll(miei);

                    JTextArea textArea = dashboardOrganizzatore.getTextAreaVisualizza();
                    JScrollPane scrollPane = dashboardOrganizzatore.getScrollPaneVisualizza();
                    textArea.setEditable(false);
                    textArea.setFocusable(false);
                    textArea.setText("");
                    Color sfondo = dashboardOrganizzatore.getPannelloLogico().getBackground();
                    textArea.setBackground(sfondo);
                    textArea.setForeground(Color.WHITE);
                    scrollPane.getViewport().setBackground(sfondo);
                    scrollPane.setBorder(null);

                    if (miei.isEmpty()) {
                        textArea.setText("Non hai ancora creato hackathon.");
                    } else {
                        textArea.append("--- I tuoi Hackathon ---\n\n");
                        for (Hackathon h : miei) {
                            textArea.append(h.getTitolo() + "\n");  // SOLO TITOLO
                        }
                    }

                    // (ri)inizializza il mini-wizard invito
                    datiInvitoGiudice = new ArrayList<>();
                    passoInvitoGiudice = 0;
                    hackathonSelezionato = null;

                    // Aggiorna la maschera (AreaDiTesto in alto con il riepilogo)
                    gestisciPannelloLogicoInvitoGiudice();
                    dashboardOrganizzatore.getFieldScrittura().setText("");
                }
            });








        }
                private boolean isDateValid(String date) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    sdf.setLenient(false);
                    try {
                        sdf.parse(date);
                        return true;
                    } catch (java.text.ParseException e) {
                        return false;
                    }

    }

                private Date getOggiSenzaOrario() {

                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
                    cal.set(java.util.Calendar.MINUTE, 0);
                    cal.set(java.util.Calendar.SECOND, 0);
                    cal.set(java.util.Calendar.MILLISECOND, 0);
                    return cal.getTime();
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
