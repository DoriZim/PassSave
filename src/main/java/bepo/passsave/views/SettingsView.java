package bepo.passsave.views;

import bepo.passsave.RootNodeFetcher;
import bepo.passsave.controller.SettingsController;
import bepo.passsave.model.Settings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SettingsView implements Initializable {
    private SettingsController settingsController;
    private RootNodeFetcher rootNodeFetcher;
    Settings userSettings;
    @FXML private Button applyButton;
    @FXML private CheckBox categoryCloseToggle, passSaveCloseToggle, scanForExportFileToggle;
    @FXML private ComboBox<String> sortComboBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> sortOptions = FXCollections.observableList(List.of("name", "username", "mail", "addInfo", "category", "date"));

        sortComboBox.setItems(sortOptions);
    }

    public void loadSettings() {
        userSettings = settingsController.getSettings();

        categoryCloseToggle.setSelected(userSettings.isCategoryClose());
        passSaveCloseToggle.setSelected(userSettings.isPassSaveClose());
        scanForExportFileToggle.setSelected(userSettings.isExportScan());
        sortComboBox.getSelectionModel().select(userSettings.getSortProperty());
    }

    public void onResetSettingsButtonClick() throws Exception {
        System.out.println("Resetting settings...");

        settingsController.resetSettings();

        loadSettings();
    }

    //Open advanced Settings
    public void onAdvancedButtonClick() {
        Parent root = new AnchorPane(rootNodeFetcher.get(AdvancedSettingsView.class));
        Scene advancedSettingsScene = new Scene(root);

        Stage advancedSettingsStage = new Stage();
        advancedSettingsStage.setScene(advancedSettingsScene);
        advancedSettingsStage.initModality(Modality.APPLICATION_MODAL);
        advancedSettingsStage.initStyle(StageStyle.UNDECORATED);
        advancedSettingsStage.setTitle("Settings");

        advancedSettingsStage.show();
    }

    public void onApplyButtonClick() throws Exception {
        System.out.println("Settings applied");
        settingsController.changeSettings(categoryCloseToggle.isSelected(), passSaveCloseToggle.isSelected(),scanForExportFileToggle.isSelected(), sortComboBox.getSelectionModel().getSelectedItem() );
        userSettings = settingsController.getSettings();

        Stage thisStage = (Stage) applyButton.getScene().getWindow();
        thisStage.close();
    }

    public void setSettingsController(SettingsController settingsController) { this.settingsController = settingsController; }
    public void setRootNodeFetcher(RootNodeFetcher rootNodeFetcher) { this.rootNodeFetcher = rootNodeFetcher; }
}
