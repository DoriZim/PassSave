package bepo.passsave.views;

import bepo.passsave.RootNodeFetcher;
import bepo.passsave.controller.PinController;
import bepo.passsave.util.Alerts;
import bepo.passsave.util.Serialization;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdvancedSettingsView implements Initializable {
    private RootNodeFetcher rootNodeFetcher;
    private PinController pinController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    public void onChangePinButtonClick() {
        if(Alerts.confirmationAlert("Are you sure?", "You are about to set a new PIN. You cannot access the program without it.")) {
            pinController.setEdit(true);

            Parent root = new AnchorPane(rootNodeFetcher.get(PinView.class));
            Scene pinScene = new Scene(root, 320, 400);

            Stage pinStage = new Stage();
            pinStage.setScene(pinScene);
            pinStage.setTitle("Enter PIN");
            pinStage.initStyle(StageStyle.UNDECORATED);
            pinStage.initModality(Modality.APPLICATION_MODAL);
            pinStage.showAndWait();

            pinController.setEdit(false);
        }
    }

    public void onClearDataButtonClick() throws IOException {
        if(Alerts.confirmationAlert("Are you sure?", "You are about to delete all saved application data.")) {
            if(Alerts.confirmationAlert("Are you really sure?", "All data will be deleted. It can not be recovered!")) {
                Serialization.deleteAllFiles();
            }
        }
    }

    public void setRootNodeFetcher(RootNodeFetcher rootNodeFetcher) { this.rootNodeFetcher = rootNodeFetcher; }
    public void setPinController(PinController pinController) { this.pinController = pinController; }
}
