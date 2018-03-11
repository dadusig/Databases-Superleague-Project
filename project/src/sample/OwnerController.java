package sample;


import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TextField;

import javafx.scene.control.Label;
import javafx.stage.Stage;

import javax.tools.Tool;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class OwnerController
{

    @FXML
    TextField ownerName;
    @FXML
    TextField givenTeamName;
    @FXML
    TextField curDate;
    @FXML
    Label teamName;
    @FXML
    Label simpleCount;
    @FXML
    Label simpleRevenue;
    @FXML
    Label seasonCount;
    @FXML
    Label seasonRevenue;
    @FXML
    Label totalRevenue;
    @FXML
    TableView seasonFan;
    @FXML
    TableView simpleFan;
    @FXML
    Label lbl1;
    @FXML
    Label lbl2;

    private String team_name;


    @FXML
    private void ownerBtnCliked()
    {
        seasonFan.setVisible(false);
        simpleFan.setVisible(false);
        lbl1.setVisible(false);
        lbl2.setVisible(false);

        if (curDate.getText().equals(""))
            curDate.setText("2016-12-31");

        if (ownerName.getText().equals(""))
            ownerName.setText("ΒΑΓΓΕΛΗΣ ΜΑΡΙΝΑΚΗΣ");

        if (givenTeamName.getText().equals(""))
            givenTeamName.setText("ΟΛΥΜΠΙΑΚΟΣ");

        ownerDetails();

    }

    @FXML
    private void offersButtonClicked()
    {

        seasonFan.setVisible(true);
        simpleFan.setVisible(true);
        lbl1.setVisible(true);
        lbl2.setVisible(true);

        //Node source = (Node)  ae.getSource();
        //Stage stage  = (Stage) source.getScene().getWindow();
        //stage.setHeight(900);



        String query = "SELECT fan.name AS ΟΝΟΜΑ FROM fan\n" +
                "INNER JOIN season_ticket ON fan.id = season_ticket.fan_id\n" +
                "WHERE fan.team_name = '" + team_name + "';";

        Tools.createTableViewFromQuery(simpleFan, query);

        try
        {
            query = "call fanInviteToUpgrade('"+team_name+"', '"+curDate.getText()+"')";

            System.out.println(query);

            Tools.createTableViewFromQuery(seasonFan, query);


        }
        catch (Exception e)
        {
            System.out.println("offers not found");
        }


    }

    private void ownerDetails()
    {
        try
        {
            Connection c = Tools.connectToDatabase();

            Statement stmt = null;
            String query = "select team_name" +
                    " from owner " +
                    "where name like  '" + ownerName.getText() + "' AND team_name like '"+givenTeamName.getText()+"';";

            stmt = c.createStatement();
            ResultSet myrs = stmt.executeQuery(query);


            myrs.next();

            team_name=myrs.getString("team_name");

            teamName.setText("Όνομα Ομάδας: " + myrs.getString("team_name"));

            query = "SELECT  count(*) AS tcount FROM ticket\n" +
                    "INNER JOIN fan ON ticket.fan_id = fan.id\n" +
                    "WHERE fan.team_name='"+team_name+"' AND ticket.game_date <= '"+ curDate.getText() +"';";

            System.out.println(query);



            myrs = stmt.executeQuery(query);
            myrs.next();

            simpleCount.setText("Πλήθος Απλών Εισητηρίων:  " +myrs.getInt("tcount"));
            int simple_revenue = myrs.getInt("tcount")*10;
            simpleRevenue.setText("Εισπράξεις Απλών:  " +Integer.parseInt(myrs.getString("tcount"))*10+ " €");

            query = "SELECT COUNT(*) AS 'pl' FROM season_ticket\n" +
                    "WHERE team_name='"+team_name+"';";

            //System.out.println(query);


            myrs = stmt.executeQuery(query);
            myrs.next();

            seasonCount.setText("Πλήθος Εισητηρίων Διαρκείας:  " +myrs.getString("pl"));
            seasonRevenue.setText("Εισπράξεις Διαρκείας:  " +Integer.parseInt(myrs.getString("pl"))*200 + " €");
            totalRevenue.setText("Συνολικές Εισπράξεις:  "+ (Integer.parseInt(myrs.getString("pl"))*200 + simple_revenue) + " €" );

        }
        catch (Exception e)
        {
            System.out.println("Owner not found");
            teamName.setText("Δεν βρέθηκε πρόεδρος με αυτό το όνομα");
            simpleCount.setText("Πλήθος Απλών Εισητηρίων: ");
            simpleRevenue.setText("Εισπράξεις Απλών: ");
            seasonCount.setText("Πλήθος Εισητηρίων Διαρκείας: ");
            seasonRevenue.setText("Εισπράξεις Διαρκείας: ");
            totalRevenue.setText("Συνολικές Εισπράξεις: ");
            //e.printStackTrace();

        }
    }





}
