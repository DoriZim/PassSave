package bepo.passsave.controller;

import bepo.passsave.model.Settings;
import bepo.passsave.util.Serialization;
import bepo.passsave.views.GuiView;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    private GuiView guiView;
    private PassController passController;
    private Settings defaultSettings = new Settings(true, true, true, "date");
    private Settings userSettings;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    public void onControllersSet() throws Exception {
        userSettings = Serialization.deserializeSettings();

        if(userSettings == null) {
            userSettings = defaultSettings;
        }
    }

    public Settings getSettings() {
        return userSettings;
    }

    public void changeSettings(boolean categoryClose, boolean passSaveClose, boolean exportScan, String sortProperty) throws Exception {
        System.out.println("Old Settings: " + userSettings);

        userSettings.setCategoryClose(categoryClose);
        userSettings.setPassSaveClose(passSaveClose);
        userSettings.setExportScan(exportScan);
        userSettings.setSortProperty(sortProperty);

        System.out.println("New Settings: " + userSettings);

        passController.sort();
        guiView.refresh();

        Serialization.serializeSettings(userSettings);
    }

    public void resetSettings() throws Exception {
        userSettings = defaultSettings;

        Serialization.serializeSettings(userSettings);
    }

    public void setGuiView(GuiView guiView) { this.guiView = guiView; }
    public void setPassController(PassController passController) { this.passController = passController; }
}
