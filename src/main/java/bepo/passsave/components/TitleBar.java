package bepo.passsave.components;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Custom titleBar that handles window dragging, minimizing the window and closing the program.
 * It's loaded into the other fxml files as a component.
 */
public class TitleBar extends HBox {
    private static TitleBar obj = null; //Create empty controller object
    @FXML private HBox bigHBox;
    @FXML private Button minimizeButton, closeButton;
    public double X, Y;

    //Gets called by FXML when loading its components (including the titleBar)
    public TitleBar() {
        obj = this; //Assign already created controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/bepo.passsave/components/titleBar.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Creating the mouse event handler
        EventHandler<MouseEvent> eventHandler =
                e -> {
                    if(String.valueOf(e.getEventType()).equals("MOUSE_PRESSED")) {
                        mousePress(e.getScreenX(), e.getScreenY());
                    }
                    if(String.valueOf(e.getEventType()).equals("MOUSE_DRAGGED")) {
                        mouseDrag(e.getScreenX(), e.getScreenY());
                    }
                };

        //Adding the event handler to the hBoxes the titleBar consists of
        bigHBox.addEventHandler(MouseEvent.MOUSE_PRESSED, eventHandler);
        bigHBox.addEventHandler(MouseEvent.MOUSE_DRAGGED, eventHandler);

        //Adds eventHandler so that closeButton and minimizeButton clicks can be recognized
        closeButton.setOnAction((event) -> this.onCloseButtonClick(event));
        minimizeButton.setOnAction((event) -> this.onMinimizeButtonClick(event));
    }

    //Saves position where the mouse is pressed
    private void mousePress(double screenX, double screenY) {
        X = (bigHBox.getScene().getWindow().getX() - screenX);
        Y = (bigHBox.getScene().getWindow().getY() - screenY);
    }

    //Sets where window moves
    private void mouseDrag(double screenX, double screenY) {
        bigHBox.getScene().getWindow().setX(screenX + X);
        bigHBox.getScene().getWindow().setY(screenY + Y);
    }

    //Handles minimize-Button
    @FXML
    private void onMinimizeButtonClick(ActionEvent actionEvent) {
        ((Stage)((Button)actionEvent.getSource()).getScene().getWindow()).setIconified(true);
    }

    //Handles close-Button by closing the program
    @FXML
    private void onCloseButtonClick(ActionEvent actionEvent) {
        if(((Stage)((Button)actionEvent.getSource()).getScene().getWindow()).getTitle().equals("Modifying PassSaves") ||
                ((Stage)((Button)actionEvent.getSource()).getScene().getWindow()).getTitle().equals("Categories") ||
                    ((Stage)((Button)actionEvent.getSource()).getScene().getWindow()).getTitle().equals("Settings")) {
            ((Stage)((Button)actionEvent.getSource()).getScene().getWindow()).close();
            return;
        }

        Platform.exit();
        System.exit(0);
    }
}