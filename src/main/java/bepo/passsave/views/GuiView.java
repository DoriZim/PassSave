package bepo.passsave.views;

import bepo.passsave.Main;
import bepo.passsave.RootNodeFetcher;
import bepo.passsave.components.PassBox;
import bepo.passsave.controller.PassController;
import bepo.passsave.model.PassSave;
import bepo.passsave.util.Alerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;

public class GuiView implements Initializable {
    private RootNodeFetcher rootNodeFetcher;
    private CategoryView categoryView;
    private EditView editView;
    private SettingsView settingsView;
    private PassController passController;

    @FXML private AnchorPane anchorPane;
    @FXML private VBox vBox;
    @FXML private ScrollPane scrollPane;
    @FXML private ComboBox<String> passSaveComboBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scrollPane.setFitToWidth(true);

        //Speeds up the scroll speed of the ScrollPane
        scrollPane.getContent().setOnScroll(scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * 0.01;
            scrollPane.setVvalue(scrollPane.getVvalue() - deltaY);
        });
    }

    public void onControllersSet() {
        //anchorPane.getScene().getStylesheets().add(Main.class.getResource("/bepo.passsave/style/general.css").toExternalForm());

        passSaveComboBox.setItems(passController.getAllNames());

        for (PassSave passSave : passController.getAll()) {
            PassBox passBox = new PassBox();
            passBox.setAllLabels(passSave.getName(), passSave.getUsername(), passSave.getMail(), passSave.getPassword(), passSave.getCategory(), passSave.getAddInfo(), passSave.getDate());
            vBox.getChildren().add(passBox);
        }
    }

    public void refresh() {
        passSaveComboBox.getItems().clear();
        passSaveComboBox.getEditor().clear();
        vBox.getChildren().clear();

        onControllersSet();

        //System.out.println("Refreshed guiView content");
    }

    @FXML
    private void onCreateButtonClick() {
        passSaveComboBox.getSelectionModel().clearSelection();
        editView.clearAll();
        editView.onControllersSet();
        editView.setCreate();

        Parent root = new AnchorPane(rootNodeFetcher.get(EditView.class));
        Scene editScene = new Scene(root);

        Stage editStage = new Stage();
        editStage.setScene(editScene);
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.initStyle(StageStyle.UNDECORATED);
        editStage.setTitle("Modifying PassSaves");

        editStage.show();
    }

    @FXML
    private void onDeleteButtonClick() throws Exception {
        if(!passSaveComboBox.getSelectionModel().getSelectedItem().isEmpty() && passSaveComboBox.getSelectionModel().getSelectedItem() != null) {
            if (Alerts.confirmationAlert("Deleting Entry", "Are you sure you want to delete this entry: " + passSaveComboBox.getSelectionModel().getSelectedItem() + "?")) {
                passController.delete(passSaveComboBox.getSelectionModel().getSelectedItem());
            }
        }
        passSaveComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void onEditButtonClick() {
        if((passSaveComboBox.getSelectionModel().getSelectedItem() != null) && (!passSaveComboBox.getSelectionModel().getSelectedItem().isEmpty())) {
            editView.setEdit();
            editView.onControllersSet();
            editView.loadPassSave(passController.getSelectedItem(passSaveComboBox.getSelectionModel().getSelectedItem()));

            Parent root = new AnchorPane(rootNodeFetcher.get(EditView.class));
            Scene editScene = new Scene(root);

            Stage editStage = new Stage();
            editStage.setScene(editScene);
            editStage.initModality(Modality.APPLICATION_MODAL);
            editStage.initStyle(StageStyle.UNDECORATED);
            editStage.setTitle("Modifying PassSaves");

            editStage.show();

            passSaveComboBox.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void onCategoryButtonClick() {
        categoryView.onControllersSet();
        categoryView.clearAll();

        Parent root = new AnchorPane(rootNodeFetcher.get(CategoryView.class));
        Scene categoryScene = new Scene(root);

        Stage categoryStage = new Stage();
        categoryStage.setScene(categoryScene);
        categoryStage.initModality(Modality.APPLICATION_MODAL);
        categoryStage.initStyle(StageStyle.UNDECORATED);
        categoryStage.setTitle("Categories");

        categoryStage.show();
    }

    @FXML
    private void onExportButtonClick() {
        int result = Alerts.choiceAlert("Are you sure?", "Choose your desired export format or 'Cancel' ", "Image", "Text");

        if(result == 1) {
            passController.exportImage();
        } else if(result == 2) {
            passController.exportText();
        }
    }

    @FXML
    private void onSettingsButtonClick() {
        settingsView.loadSettings();

        Parent root = new AnchorPane(rootNodeFetcher.get(SettingsView.class));
        Scene settingsScene = new Scene(root);

        Stage settingsStage = new Stage();
        settingsStage.setScene(settingsScene);
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.initStyle(StageStyle.UNDECORATED);
        settingsStage.setTitle("Settings");

        settingsStage.show();
    }

    @FXML
    private void onNameLabelClicked() {
        Comparator<PassSave> nameComparator = Comparator.comparing(PassSave::getName, Comparator.nullsFirst(String::compareToIgnoreCase));
        sort(nameComparator);
    }

    @FXML
    private void onUsernameLabelClicked() {
        Comparator<PassSave> usernameComparator = Comparator.comparing(PassSave::getUsername, Comparator.nullsFirst(String::compareToIgnoreCase));
        sort(usernameComparator);
    }

    @FXML
    private void onMailLabelClicked() {
        Comparator<PassSave> mailComparator = Comparator.comparing(PassSave::getMail, Comparator.nullsFirst(String::compareToIgnoreCase));
        sort(mailComparator);
    }

    @FXML
    private void onAddInfoLabelClicked() {
        Comparator<PassSave> addInfoComparator = Comparator.comparing(PassSave::getAddInfo, Comparator.nullsFirst(String::compareToIgnoreCase));
        sort(addInfoComparator);
    }

    @FXML
    private void onCategoryLabelClicked() {
        Comparator<PassSave> categoryComparator = Comparator.comparing(PassSave::getCategory, Comparator.nullsFirst(String::compareToIgnoreCase));
        sort(categoryComparator);
    }

    @FXML
    private void onDateLabelClicked() {
        sort(null);
    }

    private void sort(Comparator<PassSave> comparator) {
        ObservableList<PassSave> sortList = FXCollections.observableArrayList();
        sortList.addAll(passController.getAll());

        if(comparator != null) {
            sortList.sort(comparator);
        }

        vBox.getChildren().clear();

        for (PassSave passSave : sortList) {
            PassBox passBox = new PassBox();
            passBox.setAllLabels(passSave.getName(), passSave.getUsername(), passSave.getMail(), passSave.getPassword(), passSave.getCategory(), passSave.getAddInfo(), passSave.getDate());
            vBox.getChildren().add(passBox);
        }

        System.out.println("List sorted");
    }

    public void setRootNodeFetcher(RootNodeFetcher rootNodeFetcher) {this.rootNodeFetcher = rootNodeFetcher; }
    public void setCategoryView(CategoryView categoryView) { this.categoryView = categoryView; }
    public void setEditView(EditView editView) { this.editView = editView; }
    public void setSettingsView(SettingsView settingsView) { this.settingsView = settingsView; }
    public void setPassController(PassController passController) { this.passController = passController; }
}