package bepo.passsave.views;

import bepo.passsave.RootNodeFetcher;
import bepo.passsave.controller.CategoryController;
import bepo.passsave.controller.PassController;
import bepo.passsave.controller.PinController;
import bepo.passsave.controller.SettingsController;
import bepo.passsave.model.Settings;
import bepo.passsave.util.Alerts;
import bepo.passsave.util.Serialization;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdvancedSettingsView implements Initializable {
    private RootNodeFetcher rootNodeFetcher;
    private PinController pinController;
    private SettingsController settingsController;
    private PassController passController;
    private CategoryController categoryController;
    private GuiView guiView;
    private SettingsView settingsView;
    @FXML private Button clearDataButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    @FXML
    private void onChangePinButtonClick() {
        if(Alerts.confirmationAlert("Are you sure?", "You are about to set a new PIN. You cannot access the program without it.")) {
            pinController.setEdit(true);

            callPinStage();

            pinController.setEdit(false);
        }
    }

    //todo - make all controllers clear their temporary data!
    @FXML
    private void onClearDataButtonClick() throws Exception {
        if(Alerts.confirmationAlert("Are you sure?", "You are about to delete all saved application data.")) {
            if(Alerts.confirmationAlert("Are you really sure?", "All data will be deleted. It can not be recovered!")) {
                int choice = Alerts.choiceAlert("Are you really really sure?", "Please choose whether the program should be closed afterwards you plan on continue using it: ", "Close Program", "Continue");
                pinController.clearPin();
                passController.clearData();
                categoryController.clearData();
                settingsController.resetSettings();

                if(choice == 1) {
                    Platform.exit();
                    System.exit(1);
                } else if(choice == 2) {
                    settingsView.loadSettings();
                    guiView.refresh();

                    Stage thisStage = (Stage) clearDataButton.getScene().getWindow();
                    thisStage.close();

                    callPinStage();
                }

                /*
                if(Serialization.deleteAllFiles()) {
                    settingsController.onControllersSet();
                    //todo - trigger entering new PIN (maybe ask user if he wants to exit or not)
                    pinController.getLoginState(); //Ignore outcome (just want to trigger refresh of loginState)
                    passController.onControllersSet();
                    categoryController.onControllersSet();

                    settingsView.loadSettings();
                    guiView.refresh();
                }
                 */
                //todo - find out why its still being used by another process (maybe forgot to change file to path?) - could also try to just delete the directory
                //todo - !!! maybe just overwrite the data, no delete the files itself :(
            }
        }
    }

    private void callPinStage() {
        Parent root = new AnchorPane(rootNodeFetcher.get(PinView.class));
        Scene pinScene = new Scene(root, 320, 400);

        Stage pinStage = new Stage();
        pinStage.setScene(pinScene);
        pinStage.setTitle("Enter PIN");
        pinStage.initStyle(StageStyle.UNDECORATED);
        pinStage.initModality(Modality.APPLICATION_MODAL);
        pinStage.showAndWait();
    }

    public void setRootNodeFetcher(RootNodeFetcher rootNodeFetcher) { this.rootNodeFetcher = rootNodeFetcher; }
    public void setPinController(PinController pinController) { this.pinController = pinController; }
    public void setSettingsController(SettingsController settingsController) { this.settingsController = settingsController; }
    public void setPassController(PassController passController) { this.passController = passController; }
    public void setCategoryController(CategoryController categoryController) { this.categoryController = categoryController; }
    public void setGuiView(GuiView guiView) { this.guiView = guiView; }
    public void setSettingsView(SettingsView settingsView) { this.settingsView = settingsView; }
}
