package bepo.passsave.views;

import bepo.passsave.controller.CategoryController;
import bepo.passsave.controller.PassController;
import bepo.passsave.controller.SettingsController;
import bepo.passsave.model.Category;
import bepo.passsave.model.PassSave;
import bepo.passsave.util.Alerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EditView implements Initializable {
    private CategoryController categoryController;
    private PassController passController;
    private SettingsController settingsController;
    private boolean edit = false;
    private String initialName = "";

    @FXML private TextField nameTextfield, usernameTextfield, mailTextfield, passwordTextField, addInfoTextfield;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<Category> categoryBox;
    @FXML private Button createButton, generatePasswordButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    public void onControllersSet() {
        ObservableList<Category> tempList = FXCollections.observableArrayList();

        tempList.add(categoryController.getPlaceholder());
        tempList.addAll(categoryController.getCategories());

        categoryBox.setItems(tempList);
    }

    public void setEdit() {
        createButton.setText("Save Changes");
        edit = true;
    }

    public void setCreate() {
        createButton.setText("Create Entry");
        edit = false;
    }

    public void onShowButtonClick(ActionEvent actionEvent) {
        if(!passwordTextField.isVisible()) {
            System.out.println("Setting Password Visible");

            passwordField.setVisible(false);
            passwordField.setDisable(true);

            passwordTextField.setText(passwordField.getText());

            passwordTextField.setVisible(true);
            passwordTextField.setDisable(false);

        } else {
            System.out.println("Setting Password Invisible");
            passwordTextField.setVisible(false);
            passwordTextField.setDisable(true);

            passwordField.setText(passwordTextField.getText());

            passwordField.setVisible(true);
            passwordField.setDisable(false);
        }
    }

    public void onCreateButtonClick() throws Exception {
        String passwordToUse = "";

        if (categoryBox.getSelectionModel().isEmpty() || categoryBox.getSelectionModel().getSelectedItem().getName().equals("* No category selected *")) {
            Alerts.infoAlert("No Category Selected", "Please select a category and try again. If no categories show up, you'll have to create one first.");
            return;
        }

        if (passwordTextField.isVisible()) {
            passwordToUse = passwordTextField.getText();
        } else {
            passwordToUse = passwordField.getText();
        }

        if (edit) {
            if(passController.edit(initialName, nameTextfield.getText(), usernameTextfield.getText(), mailTextfield.getText(), passwordToUse, categoryBox.getSelectionModel().getSelectedItem(), addInfoTextfield.getText())) {
                closeStageIfDesired();
            }
        } else {
            if(passController.createNew(nameTextfield.getText(), usernameTextfield.getText(), mailTextfield.getText(), passwordToUse, categoryBox.getSelectionModel().getSelectedItem(), addInfoTextfield.getText())) {
                closeStageIfDesired();
            }
        }
    }

    public void onGeneratePasswordClick(ActionEvent actionEvent) {
        System.out.println("Creating safe password...");
        passwordTextField.setText("password");
    }

    private void closeStageIfDesired() {
        if (settingsController.getSettings().isPassSaveClose()) {
            Stage thisStage = (Stage) createButton.getScene().getWindow();
            thisStage.close();
        }
    }

    public void clearAll() {
        nameTextfield.clear();
        usernameTextfield.clear();
        mailTextfield.clear();
        passwordField.clear();
        addInfoTextfield.clear();
        categoryBox.getSelectionModel().select(categoryController.getPlaceholder());
    }

    public void loadPassSave(PassSave passSave) {
        initialName = passSave.getName();

        nameTextfield.setText(passSave.getName());
        usernameTextfield.setText(passSave.getUsername());
        mailTextfield.setText(passSave.getMail());
        passwordField.setText(passSave.getPassword());
        addInfoTextfield.setText(passSave.getAddInfo());
        categoryBox.getSelectionModel().select(categoryController.getCategory(passSave.getCategory()));
    }

    public void setPassController(PassController passController) { this.passController = passController; }
    public void setCategoryController(CategoryController categoryController) { this.categoryController = categoryController; }
    public void setSettingsController(SettingsController settingsController) { this.settingsController = settingsController; }
}
