package bepo.passsave.util;

import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Optional;

public class Alerts {
    //Creates an infoAlert
    public static void infoAlert(String header, String context) {
        Alert alert = setAlert(new Alert(Alert.AlertType.INFORMATION));
        alert.setTitle("Information Dialog");
        alert.setHeaderText(header);
        alert.setContentText(context);
        alert.showAndWait();
    }

    //Creates a confirmationAlert, that returns whether the user confirms or cancels an action
    public static boolean confirmationAlert(String header, String context) {
        Alert alert = setAlert(new Alert(Alert.AlertType.CONFIRMATION));
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(header);
        alert.setContentText(context);
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    //Creates a choiceAlert, that returns whether the user confirms or cancels an action
    public static int choiceAlert(String header, String context, String firstChoice, String secondChoice) {
        Alert alert = setAlert(new Alert(Alert.AlertType.CONFIRMATION));
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(header);
        alert.setContentText(context);

        ButtonType choice1 = new ButtonType(firstChoice);
        ButtonType choice2 = new ButtonType(secondChoice);
        ButtonType cancel = new ButtonType("Cancel");

        alert.getButtonTypes().setAll(choice1, choice2, cancel);

        Optional<ButtonType> result = alert.showAndWait();

        if(result.get() == choice1) {
            return 1;
        } else if(result.get() == choice2) {
            return 2;
        } else {
            alert.close();
            return 0;
        }
    }

    //Sets shared properties to the created alert (stage properties, stylesheet)
    private static Alert setAlert(Alert alert) {
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setAlwaysOnTop(true);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                Alerts.class.getResource("/bepo.passsave/style/alerts.css").toExternalForm());
        dialogPane.getStyleClass().add("notification");

        return alert;
    }
}
