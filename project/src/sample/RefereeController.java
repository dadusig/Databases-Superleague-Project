package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class RefereeController
{

    static  String referee_name;

    @FXML
    Label refereeName;
    @FXML
    TableView futureGames;
    @FXML
    TextField refereeID;
    @FXML
    TextField currentDate;

    @FXML
    private void refereeBtnCliked()
    {
        if (currentDate.getText().equals(""))
            currentDate.setText("2016-12-31");

        refereeDetails();

        String future = "select game.game_date as 'ΗΜΕΡΟΜΗΝΙΑ', \n" +
                "game.stadium_name as 'ΓΗΠΕΔΟ',\n" +
                "game.team_host as 'ΓΗΠΕΔΟΥΧΟΣ', \n" +
                "game.team_guest as 'ΦΙΛΟΞΕΝΟΥΜΕΝΟΣ',\n" +
                "a.name as 'ΔΙΑΙΤΗΤΗΣ', b.name as '1ος', \n" +
                "c.name as '2ος', \n" +
                "d.name as 'ΤΕΤΑΡΤΟΣ', \n" +
                "e.name as 'ΠΑΡΑΤΗΡΗΤΗΣ'\n" +
                "from game\n" +
                "inner join referee as a on a.id = game.referee \n" +
                "inner join referee as b on b.id = game.ref1\n" +
                "inner join referee as c on c.id = game.ref2\n" +
                "inner join referee as d on d.id = game.fourth\n" +
                "inner join referee as e on e.id = game.observer\n" +
                "where (a.id = " + refereeID.getText() + " or " +
                "b.id = " + refereeID.getText() + " or " +
                "c.id = " + refereeID.getText() + " or " +
                "d.id = " + refereeID.getText() + " or " +
                "e.id = " + refereeID.getText() + ") and game.game_date >= '"+ currentDate.getText()+"';";

        Tools.createTableViewFromQuery(futureGames, future);

    }

    private void refereeDetails()
    {
        try
        {
            Connection c = Tools.connectToDatabase();

            Statement stmt = null;
            String query = "select name" +
                    " from referee " +
                    "where id = " + refereeID.getText() + ";";

            stmt = c.createStatement();
            ResultSet myrs = stmt.executeQuery(query);

            myrs.next();
            referee_name = myrs.getString("name");
            refereeName.setText("Όνομα Διαιτητή: " + myrs.getString("name"));

        }
        catch (Exception e)
        {
            System.out.println("Referee not found");
            refereeName.setText("Δεν βρέθηκε διαιτητής με αυτό το id");
        }
    }
}
