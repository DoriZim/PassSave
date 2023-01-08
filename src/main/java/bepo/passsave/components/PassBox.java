package bepo.passsave.components;

import bepo.passsave.util.Alerts;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.time.LocalDate;

/**
 * The PositionBox component is used to load hBoxes for each position, containing their information and a delete button.
 */
public class PassBox extends HBox {
    //private PositionController positionController;
    private String actualPassword = "";
    @FXML private Label name, username, mail, password, category, addInfo, date;
    @FXML private Button showButton;

    /**
     * Loads the positionBox.fxml and sets it as the root and controller of the PositionBox.
     */
    public PassBox() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/bepo.passsave/components/passBox.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        //If loading fails the program is closed
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();

            Platform.exit();
            System.exit(1);
        }

        //Adds eventHandler so that deleteButton clicks can be recognized
        showButton.setOnAction((event) -> this.onShowButtonClick(event));

        //Adds eventHandlers to all labels so that their content gets copied when clicked
        name.setOnMouseClicked((event) -> this.copyToClipboard(event, name.getText()));
        username.setOnMouseClicked((event) -> this.copyToClipboard(event, username.getText()));
        mail.setOnMouseClicked((event) -> this.copyToClipboard(event, mail.getText()));
        password.setOnMouseClicked((event) -> this.copyToClipboard(event, password.getText()));
        category.setOnMouseClicked((event) -> this.copyToClipboard(event, category.getText()));
        addInfo.setOnMouseClicked((event) -> this.copyToClipboard(event, addInfo.getText()));
        date.setOnMouseClicked((event) -> this.copyToClipboard(event, date.getText()));
    }

    //Gets called when creating a PassBox to set the labels
    public void setAllLabels(String name, String username, String mail, String password, String category, String addInfo, LocalDate date) {
        actualPassword = password;

        this.name.setText(name);
        this.username.setText(username);
        this.mail.setText(mail);
        this.password.setText("********");
        this.category.setText(category);
        this.addInfo.setText(addInfo);
        this.date.setText(date.toString());
    }

    private void onShowButtonClick(ActionEvent event) {
        if(password.getText().equals(actualPassword)) {
            password.setText("********");
        } else {
            password.setText(actualPassword);
        }
    }

    private void copyToClipboard(MouseEvent event, String text) {
        if(!text.equals("********")) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
            System.out.println("Copied to clipboard");
        }
    }
}
