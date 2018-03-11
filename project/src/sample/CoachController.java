package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.tools.Tool;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CoachController
{

    @FXML
    Label coachName;
    @FXML
    Label coaches;
    @FXML
    Label wins;
    @FXML
    Label defeats;
    @FXML
    Label ties;
    @FXML
    TableView playerTable;
    @FXML
    TableView futureGames;
    @FXML
    TableView pastWins;
    @FXML
    TableView pastDefeats;
    @FXML
    TableView pastTies;
    @FXML
    TextField coachId;
    @FXML
    TextField currentDate;
    @FXML
    Button createBtn;
    @FXML
    private TableView pastTeams;

    String team_name;

    @FXML
    private void coachBtnCliked()
    {
        pastDefeats.getColumns().clear();
        pastWins.getColumns().clear();
        pastTies.getColumns().clear();
        playerTable.getColumns().clear();
        futureGames.getColumns().clear();

        createBtn.setVisible(false);

        if (currentDate.getText().equals(""))
            currentDate.setText("2016-12-31");

        boolean coachFound = coachDetails();

        String future= " SELECT DISTINCT game.game_date as 'ΗΜΕΡΟΜΗΝΙΑ', game.stadium_name as 'ΓΗΠΕΔΟ',\n" +
                "  game.team_host as 'ΓΗΠΕΔΟΥΧΟΣ', game.team_guest as 'ΦΙΛΟΞΕΝΟΥΜΕΝΟΣ'\n" +
                "FROM game\n" +
                "INNER JOIN coach ON (coach.team_name=game.team_guest or coach.team_name=game.team_host)\n" +
                "WHERE (game.team_guest LIKE '"+ team_name +"' or coach.team_name LIKE '"+ team_name +"' ) and game.game_date>= '"+ currentDate.getText() +"';";

        String pastWinsQuery = "SELECT CAST(game.game_date as DATE) as ΗΜΕΡΟΜΗΝΙΑ, game.team_guest as 'ΑΝΤΙΠΑΛΟΣ', game.score_host AS Σ1, game.score_guest as Σ2\n" +
                "FROM game\n" +
                "WHERE result LIKE 'win' AND  team_host LIKE '"+ team_name +"' AND game_date <= '"+ currentDate.getText() +"'\n" +
                "UNION\n" +
                "SELECT CAST(game.game_date as DATE) as ΗΜΕΡΟΜΗΝΙΑ, game.team_host as 'ΑΝΤΙΠΑΛΟΣ', game.score_host AS Σ1, game.score_guest as Σ2\n" +
                "FROM game\n" +
                "WHERE result LIKE 'defeat' AND  team_guest LIKE '"+ team_name +"' AND game_date <= '"+ currentDate.getText() +"';";

        String pastDefeatsQuery = "SELECT CAST(game.game_date as DATE) as ΗΜΕΡΟΜΗΝΙΑ, game.team_guest as 'ΑΝΤΙΠΑΛΟΣ', game.score_host AS Σ1, game.score_guest as Σ2\n" +
                "FROM game\n" +
                "WHERE result LIKE 'defeat' AND  team_host LIKE '"+ team_name +"' AND game_date <= '"+ currentDate.getText() +"'\n" +
                "UNION\n" +
                "SELECT CAST(game.game_date as DATE) as ΗΜΕΡΟΜΗΝΙΑ, game.team_host as 'ΑΝΤΙΠΑΛΟΣ', game.score_host AS Σ1, game.score_guest as Σ2\n" +
                "FROM game\n" +
                "WHERE result LIKE 'win' AND  team_guest LIKE '"+ team_name +"' AND game_date <= '"+ currentDate.getText() +"';";

        String pastTiesQuery = "SELECT CAST(game.game_date as DATE) as ΗΜΕΡΟΜΗΝΙΑ, game.team_guest as 'ΑΝΤΙΠΑΛΟΣ', game.score_host AS Σ1, game.score_guest as Σ2\n" +
                "FROM game\n" +
                "WHERE result LIKE 'tie' AND  team_host LIKE '"+ team_name +"' AND game_date <= '"+ currentDate.getText() +"'\n" +
                "UNION\n" +
                "SELECT CAST(game.game_date as DATE) as ΗΜΕΡΟΜΗΝΙΑ, game.team_host as 'ΑΝΤΙΠΑΛΟΣ', game.score_host AS Σ1, game.score_guest as Σ2\n" +
                "FROM game\n" +
                "WHERE result LIKE 'tie' AND  team_guest LIKE '"+ team_name +"' AND game_date <= '"+ currentDate.getText() +"';";
        String playersQuery = "select id as ID, name as ΟΝΟΜΑ, goals as GOALS , cv as 'ΠΛΗΡΟΦΟΡΙΕΣ' from player where team_name like '" + team_name +"';";

        String pastTeamsQuery = "SELECT DISTINCT team_name as ΟΜΑΔΕΣ\n" +
                "FROM coach_history\n" +
                "WHERE coach_id = "+ coachId.getText() +";";
        if (coachFound)
        {
            Tools.createTableViewFromQuery(futureGames, future);
            Tools.createTableViewFromQuery(pastWins, pastWinsQuery);
            Tools.createTableViewFromQuery(pastDefeats, pastDefeatsQuery);
            Tools.createTableViewFromQuery(pastTies, pastTiesQuery);
            Tools.createTableViewFromQuery(playerTable, playersQuery);
            Tools.createTableViewFromQuery(pastTeams, pastTeamsQuery);
        }

    }

    private boolean coachDetails()
    {
        boolean success=false;
        try
        {
            Connection c = Tools.connectToDatabase();

            Statement stmt = null;
            String query = "select name, team_name, wins, ties, defeats" +
                    " from coach " +
                    "where id = " + coachId.getText() + ";";

            stmt = c.createStatement();
            ResultSet myrs = stmt.executeQuery(query);

            myrs.next();
            coachName.setText("Όνομα Προπονητή: " + myrs.getString("name"));
            coaches.setText("Προπονεί: " + myrs.getString("team_name"));
            wins.setText("Νίκες: " + myrs.getString("wins"));
            defeats.setText("Ήττες: " + myrs.getString("defeats"));
            ties.setText("Ισοπαλίες: " + myrs.getString("ties"));
            team_name = myrs.getString("team_name");

            if (!myrs.getString("team_name").equals(""))
            {
                createBtn.setVisible(true);
                success=true;
            }

        }
        catch (Exception e)
        {
            System.out.println("Coach not found");
            coachName.setText("Δεν βρέθηκε προπονητής με αυτό το id");
            coaches.setText(" ");
            wins.setText("Νίκες: ");
            defeats.setText("Ήττες: ");
            ties.setText("Ισοπαλίες: " );
        }

        return success;
    }


    @FXML
    private void createPlayerClicked() throws Exception
    {
        CreatePlayerController.team_name = team_name;
        Parent root = FXMLLoader.load(getClass().getResource("CreatePlayerView.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root, 488, 524));
        stage.setTitle("Προσθήκη Παίκτη");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.show();
    }



}
