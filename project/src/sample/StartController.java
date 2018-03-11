package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import javafx.event.ActionEvent;

public class StartController
{
    @FXML
    Button fanBtn;

    @FXML
    private void fanBtnClicked() throws Exception
    {
        Parent rootFan = FXMLLoader.load(getClass().getResource("FanView.fxml"));
        Stage fanStage = new Stage();
        fanStage.setScene(new Scene(rootFan, 980, 600));
        fanStage.setTitle("Φίλαθλος");
        fanStage.show();
    }

    @FXML
    private void refereeBtnClicked() throws Exception
    {
        Parent rootReferee = FXMLLoader.load(getClass().getResource("RefereeView.fxml"));
        Stage refereeStage = new Stage();
        refereeStage.setScene(new Scene(rootReferee, 800, 300));
        refereeStage.setTitle("Διαιτητής");
        refereeStage.show();
    }

    @FXML
    private void coachBtnClicked() throws Exception
    {
        Parent rootCoach = FXMLLoader.load(getClass().getResource("CoachView.fxml"));
        Stage coachStage = new Stage();
        coachStage.setScene(new Scene(rootCoach, 1100, 770));
        coachStage.setTitle("Προπονητής");
        coachStage.show();
    }

    @FXML
    private void ownerBtnClicked() throws Exception
    {
        Parent rootOwner = FXMLLoader.load(getClass().getResource("OwnerView.fxml"));
        Stage ownerStage = new Stage();
        ownerStage.setScene(new Scene(rootOwner, 800, 600));
        ownerStage.setTitle("Πρόεδρος");
        ownerStage.show();
    }
}
