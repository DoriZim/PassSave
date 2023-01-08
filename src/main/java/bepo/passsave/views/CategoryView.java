package bepo.passsave.views;

import bepo.passsave.controller.CategoryController;
import bepo.passsave.controller.SettingsController;
import bepo.passsave.model.Category;
import bepo.passsave.util.Alerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class CategoryView implements Initializable {
    private CategoryController categoryController;
    private SettingsController settingsController;
    @FXML private Button createButton, deleteButton;
    @FXML private ComboBox<Category> categoryBox;
    @FXML private ColorPicker colorPicker;
    @FXML private TextField nameTextField, descriptionTextField;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoryBox.setOnAction((event) -> this.changeContent());
    }

    public void onControllersSet() {
        ObservableList<Category> tempList = FXCollections.observableArrayList();

        tempList.add(categoryController.getPlaceholder());
        tempList.addAll(categoryController.getCategories());

        categoryBox.setItems(tempList);
    }

    public void clearAll() {
        nameTextField.clear();
        descriptionTextField.clear();
        colorPicker.setValue(Color.BLACK);

        categoryBox.getSelectionModel().select(categoryController.getPlaceholder());
    }

    private void changeContent() {
        if(!categoryBox.getSelectionModel().isEmpty() && !categoryBox.getSelectionModel().getSelectedItem().equals(categoryController.getPlaceholder())) {
            createButton.setText("Edit Category");

            nameTextField.setText(categoryBox.getSelectionModel().getSelectedItem().getName());
            descriptionTextField.setText(categoryBox.getSelectionModel().getSelectedItem().getDescription());
            colorPicker.setValue(categoryBox.getSelectionModel().getSelectedItem().getColor());
        }
        if(categoryBox.getSelectionModel().isEmpty() || categoryBox.getSelectionModel().getSelectedItem().equals(categoryController.getPlaceholder())) {
            createButton.setText("Create Category");

            nameTextField.clear();
            descriptionTextField.clear();
            colorPicker.setValue(Color.BLACK);
        }
    }

    @FXML
    private void onBackButtonClick() {
        Stage thisStage = (Stage) createButton.getScene().getWindow();
        thisStage.close();
    }

    @FXML
    private void onDeleteButtonClick() throws Exception {
        if(!categoryBox.getSelectionModel().isEmpty() && !categoryBox.getSelectionModel().getSelectedItem().equals(categoryController.getPlaceholder())) {
            if(Alerts.confirmationAlert("Are you sure?", "You are about to delete this category. This action cannot be undone.")) {
                categoryController.deleteCategory(categoryBox.getSelectionModel().getSelectedItem());

                nameTextField.clear();
                descriptionTextField.clear();
                colorPicker.setValue(Color.BLACK);

                if(settingsController.getSettings().isCategoryClose()) {
                    Stage thisStage = (Stage) createButton.getScene().getWindow();
                    thisStage.close();
                } else {
                    onControllersSet();
                    clearAll();
                }
            }
        }
    }

    @FXML
    private void onCreateButtonClick() throws Exception {
        if(categoryBox.getSelectionModel().isEmpty() || categoryBox.getSelectionModel().getSelectedItem().equals(categoryController.getPlaceholder())) {
            System.out.println("Create category");
            if(!nameTextField.getText().isEmpty()) {
                String name = nameTextField.getText();
                if(categoryController.createCategory(name, descriptionTextField.getText(), colorPicker.getValue())) {
                    if(settingsController.getSettings().isCategoryClose()) {
                        Stage thisStage = (Stage) createButton.getScene().getWindow();
                        thisStage.close();
                    } else {
                        onControllersSet();
                        try {
                            categoryBox.getSelectionModel().select(categoryController.getCategory(name));
                        } catch (Exception e) {
                            System.err.println("Couldn't find category");
                        }
                    }
                }
            }
        } else if(!categoryBox.getSelectionModel().isEmpty()  && !categoryBox.getSelectionModel().getSelectedItem().equals(categoryController.getPlaceholder())) {
            System.out.println("Edit category");
            if(!nameTextField.getText().isEmpty()) {
                if(categoryController.editCategory(categoryBox.getSelectionModel().getSelectedItem(), nameTextField.getText(), descriptionTextField.getText(), colorPicker.getValue())) {
                    if(settingsController.getSettings().isCategoryClose()) {
                        Stage thisStage = (Stage) createButton.getScene().getWindow();
                        thisStage.close();
                    } else {
                        onControllersSet();
                        clearAll();
                    }
                }
            }
        }
    }

    public void setCategoryController(CategoryController categoryController) {this.categoryController = categoryController; }
    public void setSettingsController(SettingsController settingsController) { this.settingsController = settingsController; }
}
