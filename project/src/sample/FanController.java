package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.*;


import java.sql.Connection;
import java.sql.ResultSet;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;

public class FanController
{

    @FXML
    private TableView futureTable;

    @FXML
    private TableView pastTable;

    @FXML
    private TableView pastGames;

    @FXML
    private TableView favPlayers;

    @FXML
    private Label fanName;

    @FXML
    private TextField gameDate;

    @FXML
    private Label teamName;

    @FXML
    private TextField fanID;

    @FXML
    private Label goalsDiff;

    @FXML
    private Label hasSeason;

    @FXML
    private Label freeSeats;

    public void buttonClicked()
    {
        if (gameDate.getText().equals(""))
            gameDate.setText("2016-12-31");

        fanDetails();


        String future = "select game.game_date as 'ΗΜΕΡΟΜΗΝΙΑ', \n" +
                "game.stadium_name as 'ΓΗΠΕΔΟ',\n" +
                "game.team_host as 'ΓΗΠΕΔΟΥΧΟΣ', \n" +
                "game.team_guest as 'ΦΙΛΟΞΕΝΟΥΜΕΝΟΣ',\n" +
                "a.name as 'ΔΙΑΙΤΗΤΗΣ', b.name as '1ος', \n" +
                "c.name as '2ος', \n" +
                "d.name as 'ΤΕΤΑΡΤΟΣ', \n" +
                "e.name as 'ΠΑΡΑΤΗΡΗΤΗΣ'\n" +
                "from game\n" +
                "inner join fan on (game.team_host = fan.team_name or game.team_guest = fan.team_name)\n" +
                "inner join referee as a on a.id = game.referee \n" +
                "inner join referee as b on b.id = game.ref1\n" +
                "inner join referee as c on c.id = game.ref2\n" +
                "inner join referee as d on d.id = game.fourth\n" +
                "inner join referee as e on e.id = game.observer\n" +
                "where fan.id = " + fanID.getText() + " and game.game_date >= '"+ gameDate.getText()+"';";

        String past = "select game.game_date as 'ΗΜΕΡΟΜΗΝΙΑ', game.stadium_name as 'ΓΗΠΕΔΟ', game.team_host as 'ΓΗΠΕΔΟΥΧΟΣ', game.team_guest as ' ΦΙΛΟΞΕΝΟΥΜΕΝΟΣ', game.score_host as 'Σ1'," +
                " game.score_guest as 'Σ2', game.description as 'ΠΕΡΙΓΡΑΦΗ' \n" +
                "from game\n" +
                "inner join fan on (game.team_host = fan.team_name or game.team_guest = fan.team_name)\n" +
                "where fan.id = " + fanID.getText() + " and game.game_date <= '" + gameDate.getText() +"' ;" ;

        String favPlayersOfFan = "select player.name as 'ΟΝΟΜΑ', player.goals as GOALS, player.team_name as 'ΟΜΑΔΑ'\n" +
                "from player\n" +
                "inner join fan_admires on (player.id = fan_admires.player_id)\n"+
                "where fan_admires.fan_id = " + fanID.getText() + ";" ;

        String pastGamesOfFan = "select game.game_date as 'ΗΜΕΡΟΜΗΝΙΑ', game.stadium_name as 'ΓΗΠΕΔΟ', game.team_host as 'ΓΗΠΕΔΟΥΧΟΣ', game.team_guest as ' ΦΙΛΟΞΕΝΟΥΜΕΝΟΣ', game.score_host as 'Σ1',\n" +
                "game.score_guest as 'Σ2'\n" +
                "from game\n" +
                "inner join ticket on (game.stadium_name = ticket.stadium_name and game.game_date = ticket.game_date)\n" +
                "where ticket.fan_id = " + fanID.getText() + " and game.game_date <= '" + gameDate.getText() +"'\n" +
                "UNION\n" +
                "select game.game_date as 'ΗΜΕΡΟΜΗΝΙΑ', game.stadium_name as 'ΓΗΠΕΔΟ', game.team_host as 'ΓΗΠΕΔΟΥΧΟΣ', game.team_guest as ' ΦΙΛΟΞΕΝΟΥΜΕΝΟΣ', game.score_host as 'Σ1',\n" +
                "game.score_guest as 'Σ2'\n" +
                "from game\n" +
                "inner join season_ticket_history on (game.stadium_name = season_ticket_history.stadium_name and game.game_date = season_ticket_history.game_date)\n" +
                "inner join season_ticket on (season_ticket_history.ticket_id = season_ticket.id)\n" +
                "where season_ticket.fan_id = " + fanID.getText() + " and game.game_date <= '" + gameDate.getText() +"';";

        try{

            Tools.createTableViewFromQuery(futureTable, future);
            Tools.createTableViewFromQuery(pastTable, past);
            Tools.createTableViewFromQuery(favPlayers, favPlayersOfFan);
            Tools.createTableViewFromQuery(pastGames, pastGamesOfFan);

        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }

    private void fanDetails()
    {
        try
        {
            Connection c = Tools.connectToDatabase();

            Statement stmt = null;
            String query = "select name, team_name " +
                    "from fan " +
                    "where id = " + fanID.getText() + ";";

            stmt = c.createStatement();
            ResultSet myrs = stmt.executeQuery(query);

            myrs.next();
            String team_name = myrs.getString("team_name");
            fanName.setText("Όνομα Φιλάθλου: " + myrs.getString("name"));
            teamName.setText("Ομάδα: " + myrs.getString("team_name"));


            query =  "select (goalsIn - goalsOut) as diafora\n" +
                    "from team\n" +
                    "where name = '"+ team_name +"';";

            myrs = stmt.executeQuery(query);

            myrs.next();

            goalsDiff.setText("Διαφορά Τερμάτων Ομάδας:  " + myrs.getInt("diafora"));

            query = "SELECT id FROM season_ticket\n" +
                    "WHERE fan_id = "+ fanID.getText() +" AND team_name = '"+team_name+"';";

            myrs = stmt.executeQuery(query);

            hasSeason.setVisible(true);
            hasSeason.setText("Διαρκείας:  ΟΧΙ");
            if(myrs.next())
            {
                hasSeason.setText("Διαρκείας:  ΝΑΙ");
                freeSeats.setVisible(true);


                query = "SELECT stadium.capacity-count(ticket.id) AS 'free_seats'\n" +
                        "FROM game\n" +
                        "INNER JOIN ticket ON (game.stadium_name = ticket.stadium_name AND game.game_date = ticket.game_date)\n" +
                        "INNER JOIN stadium ON game.stadium_name = stadium.name\n" +
                        "WHERE game.team_guest = '"+team_name+"' AND game.game_date >= '"+gameDate.getText()+"' ORDER BY game.game_date ASC\n" +
                        "limit 0,1;";

                myrs = stmt.executeQuery(query);

                if(myrs.next())
                {
                    freeSeats.setText("Διαθέσιμες Θέσεις: " + myrs.getString("free_seats"));
                }
            }

        }
        catch (Exception e)
        {
            System.out.println("Fan not found");
            fanName.setText("Δεν βρέθηκε φίλαθλος με αυτό το id");
            teamName.setText(" ");
            goalsDiff.setText(" ");
            hasSeason.setText(" ");
            freeSeats.setText(" ");

        }
    }
}
