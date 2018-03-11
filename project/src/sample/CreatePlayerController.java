package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CreatePlayerController
{
    @FXML
    TextField name;
    @FXML
    TextField birthday;
    @FXML
    TextField goals;
    @FXML
    TextArea cv;
    @FXML
    CheckBox keeper;
    @FXML
    Label errorLabel;
    @FXML
    Label nameLabel;
    @FXML
    Label birthdayLabel;
    @FXML
    Label goalsLabel;
    @FXML
    Label cvLabel;

    static String team_name;

    @FXML
    private void createPlayerClicked(ActionEvent actionEvent)
    {
        boolean problem = false;

        if (name.getText().length()==0)
        {
            problem = true;
            nameLabel.setTextFill(Color.web("#ff0000"));
        }
        else
            nameLabel.setTextFill(Color.web("#000000"));

        if (birthday.getText().length()==0)
        {
            problem = true;
            birthdayLabel.setTextFill(Color.web("#ff0000"));
        }
        else
            birthdayLabel.setTextFill(Color.web("#000000"));

        if (cv.getText().length()==0)
        {
            problem = true;
            cvLabel.setTextFill(Color.web("#ff0000"));
        }
        else
            cvLabel.setTextFill(Color.web("#000000"));

        if (isInteger(goals.getText()))
        {
            goalsLabel.setTextFill(Color.web("#000000"));
            if (Integer.parseInt(goals.getText())<0)
            {
                problem = true;
                goalsLabel.setTextFill(Color.web("#ff0000"));
            }
            else
                goalsLabel.setTextFill(Color.web("#000000"));
        }
        else
        {
            problem = true;
            goalsLabel.setTextFill(Color.web("#ff0000"));
        }

        int isKeeper;

        if (keeper.isSelected())
            isKeeper = 1;
        else
            isKeeper = 0;

        String insertQuery =
                "INSERT INTO player VALUES\n" +
                        "(NULL, '" + name.getText() + "', '"+ birthday.getText() +"', " + Integer.parseInt(goals.getText()) +", " + isKeeper + ", '" + cv.getText() + "', '" + team_name + "');";


        if (!problem)
        try
        {
            Connection c = Tools.connectToDatabase();


            Statement stmt = c.createStatement();
            stmt.executeUpdate(insertQuery);

            closeClicked(actionEvent);
        }
        catch (Exception e)
        {
            System.out.println(insertQuery);
            System.out.println("Insertion Error. Check your Data.");
            errorLabel.setText("Αποτυχία Εγγραφής. Προσπαθήστε ξανα.");
        }
    }

    @FXML
    private void closeClicked(ActionEvent actionEvent)
    {
        Node source = (Node)  actionEvent.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
}
