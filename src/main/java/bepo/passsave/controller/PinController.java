package bepo.passsave.controller;

import bepo.passsave.util.Alerts;
import bepo.passsave.util.Serialization;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Handles unlocking the programm through a locally set PIN.
 * This class has methods to let PinView check whether entered PINs match the saved PIN or not.
 */
public class PinController implements Initializable {
    static int count = 0;
    private String pin;
    private boolean login = false;
    private boolean edit = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    /* Gets the PIN the user entered and checks whether it's correct or not.
    After 3 unsuccessful tries an eMail is sent to the account owner and the program is closed */
    public boolean checkPIN(String enteredPIN) {
        if (enteredPIN.equals(pin)) {
            login = true;
            return true;
        } else {
            if(!edit) {
                count += 1;
                System.out.println("Times PIN has been entered wrong: " + count);
            }
        }

        if (count >= 3) {
            System.out.println("Out of tries. Closing Application...");

            Platform.exit();
            System.exit(0);
        }
        return false;
    }

    //This method is called when no PIN has been saved locally (manual user login/ new user register)
    public boolean confirmPIN(String setPin) throws Exception {
        if(Alerts.confirmationAlert("Confirm PIN?", "Click CONFIRM to confirm your PIN or CANCEL to edit it.")) {
            pin = setPin;
            login = true;

            Serialization.serializePin(pin);

            return true;
        }
        return false;
    }

    public boolean getLoginState() throws Exception {
        pin = Serialization.deserializePin();

        if((pin != null && (pin.length() == 4))) {
            login = true;
        } else {
            login = false;
        }

        return login;
    }

    public boolean getEdit() {
        return edit;
    }

    public void setEdit(boolean value) {
        edit = value;
    }

    public void clearPin() throws Exception {
        pin = "";
        Serialization.serializePin(pin);

        login = false;
    }
}
