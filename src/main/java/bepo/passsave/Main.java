package bepo.passsave;

import bepo.passsave.controller.*;
import bepo.passsave.util.Alerts;
import bepo.passsave.util.Serialization;
import bepo.passsave.views.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;

public class Main extends Application {
    private HashMap<Class<?extends Initializable>, Node> rootNodes = new HashMap<>();

    //Controller
    private CategoryController categoryController = new CategoryController();
    private PassController passController = new PassController();
    private PinController pinController = new PinController();
    private SettingsController settingsController = new SettingsController();

    //Views
    private CategoryView categoryView = new CategoryView();
    private EditView editView = new EditView();
    private PinView pinView = new PinView();
    private SettingsView settingsView = new SettingsView();
    private AdvancedSettingsView advancedSettingsView = new AdvancedSettingsView();
    private GuiView guiView;

    @Override
    public void start(Stage stage) throws Exception {
        Serialization.createApplicationFiles();

        //Sets rootNodeFetcher
        RootNodeFetcher rootNodeFetcher = (clazz) -> this.rootNodes.get(clazz);

        //Sets GUI View
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/bepo.passsave/views/gui.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 562.5);
        this.guiView = fxmlLoader.getController();

        fxmlLoader.setControllerFactory((type) -> this.guiView);
        this.rootNodes.put(GuiView.class, null);

        //Assigns classes to RootNodeFetcher
        FXMLLoader fxmlLoaderCategory = new FXMLLoader(Main.class.getResource("/bepo.passsave/views/category-view.fxml"));
        fxmlLoaderCategory.setControllerFactory((type) -> this.categoryView);
        this.rootNodes.put(CategoryView.class, fxmlLoaderCategory.load());

        FXMLLoader fxmlLoaderEdit = new FXMLLoader(Main.class.getResource("/bepo.passsave/views/edit-view.fxml"));
        fxmlLoaderEdit.setControllerFactory((type) -> this.editView);
        this.rootNodes.put(EditView.class, fxmlLoaderEdit.load());

        FXMLLoader fxmlLoaderPin = new FXMLLoader(Main.class.getResource("/bepo.passsave/views/pin-view.fxml"));
        fxmlLoaderPin.setControllerFactory((type) -> this.pinView);
        this.rootNodes.put(PinView.class, fxmlLoaderPin.load());

        FXMLLoader fxmlLoaderSettings = new FXMLLoader(Main.class.getResource("/bepo.passsave/views/settings-view.fxml"));
        fxmlLoaderSettings.setControllerFactory((type) -> this.settingsView);
        this.rootNodes.put(SettingsView.class, fxmlLoaderSettings.load());

        FXMLLoader fxmlLoaderAdvancedSettings = new FXMLLoader(Main.class.getResource("/bepo.passsave/views/advancedSettings-view.fxml"));
        fxmlLoaderAdvancedSettings.setControllerFactory((type) -> this.advancedSettingsView);
        this.rootNodes.put(AdvancedSettingsView.class, fxmlLoaderAdvancedSettings.load());

        this.guiView.setRootNodeFetcher(rootNodeFetcher);
        this.settingsView.setRootNodeFetcher(rootNodeFetcher);
        this.advancedSettingsView.setRootNodeFetcher(rootNodeFetcher);

        //Setting controllers in controllers
        this.categoryController.setPassController(this.passController);
        this.passController.setSettingsController(this.settingsController);
        this.settingsController.setPassController(this.passController);

        //Setting controllers in views
        this.categoryView.setCategoryController(this.categoryController);
        this.editView.setCategoryController(this.categoryController);
        this.editView.setPassController(this.passController);
        this.guiView.setPassController(this.passController);
        this.pinView.setPinController(this.pinController);
        this.settingsView.setSettingsController(this.settingsController);
        this.editView.setSettingsController(this.settingsController);
        this.categoryView.setSettingsController(this.settingsController);
        this.advancedSettingsView.setPinController(this.pinController);
        this.advancedSettingsView.setSettingsController(this.settingsController);
        this.advancedSettingsView.setPassController(this.passController);
        this.advancedSettingsView.setCategoryController(this.categoryController);

        //Setting views in controllers
        this.categoryController.setGuiView(this.guiView);
        this.passController.setGuiView(this.guiView);
        this.settingsController.setGuiView(this.guiView);

        //Setting views in views
        this.guiView.setCategoryView(this.categoryView);
        this.guiView.setEditView(this.editView);
        this.guiView.setSettingsView(this.settingsView);
        this.advancedSettingsView.setGuiView(this.guiView);
        this.advancedSettingsView.setSettingsView(this.settingsView);

        //Calls onControllerSet of these classes early because the rest of the program depends on them
        settingsController.onControllersSet();
        pinView.onControllersSet();

        //Setting guiStage
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("PassSave");
        stage.setScene(scene);
        stage.show();

        //Setting pinStage
        Stage pinStage = new Stage();
        Scene pinScene = new Scene((Parent) rootNodeFetcher.get(PinView.class), 320, 400);
        pinStage.setScene(pinScene);
        pinStage.setTitle("Enter PIN");
        pinStage.initStyle(StageStyle.UNDECORATED);
        pinStage.initModality(Modality.APPLICATION_MODAL);
        pinStage.showAndWait();

        //Once all controllers are set and the program is unlocked all data is loaded
        if (this.pinController.getLoginState()) {
            System.out.println("Login succeeded");

            //Detects old export files (and deletes them)
            if(settingsController.getSettings().isExportScan()) {
                File exportText = new File("application-files/Export.txt");
                File exportImage = new File("application-files/ExportImage.png");

                if (Files.exists(exportText.toPath()) || Files.exists(exportImage.toPath())) {
                    try {
                        if (Alerts.confirmationAlert("Old export file found.", "To keep your PassSaves safe you should delete this file.")) {
                            Files.deleteIfExists(exportText.toPath());
                            Files.deleteIfExists(exportImage.toPath());

                            System.out.println("Export file deleted successfully");
                        }
                    } catch (Exception e) {
                        Alerts.infoAlert("Failed to delete file.", "The file could no longer be found.");
                    }
                }
            }

            this.categoryController.onControllersSet();
            this.categoryView.onControllersSet();
            this.passController.onControllersSet();
            this.guiView.onControllersSet();
            this.editView.onControllersSet();
        }
    }

    public static void main(String[] args) { launch(); }
}