package controller;

import gui.Home;
import javax.swing.*;
import java.awt.event.*;

public class Controller {

    private static JFrame frame;
    private Home loginFrame;
    private ButtonGroup gruppoRuoli;

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
    JRadioButton utenteRadioButton = loginFrame.getUtenteRadioButton();
    JRadioButton organizzatoreRadioButton = loginFrame.getOrganizzatoreRadioButton();
    JRadioButton giudiceRadioButton = loginFrame.getGiudiceRadioButton();

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

       loginButton.addActionListener(new ActionListener () {

           public void actionPerformed(ActionEvent e) {
               String emailInserita = campoEmail.getText();
               String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
               boolean emailValida = false;
               boolean ruoloSelezionato = false;

               if (gruppoRuoli.getSelection() == null) {
                   messaggioErrore.setText("Seleziona un ruolo");
                   return;
               } else {
                   messaggioErrore.setText("");
                   ruoloSelezionato = true;
               }

               if (emailInserita.equals("Email") || emailInserita.isEmpty()){
                   messaggioErrore.setText("Inserisci un'email valida");
                   return;
               }
               if (!emailInserita.matches(emailRegex)) {
                   messaggioErrore.setText("Formato email non valido.");
                   return;
                }

               else {
                     messaggioErrore.setText("");
                     emailValida = true;
                     return;
                }
           }


       });

       loginPanel.addMouseListener(new MouseAdapter(){

           @Override
           public void mousePressed(MouseEvent e){
               loginPanel.requestFocusInWindow();

           }


        });







    }


}