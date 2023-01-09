package bepo.passsave.controller;

import bepo.passsave.model.Category;
import bepo.passsave.model.PassSave;
import bepo.passsave.util.Alerts;
import bepo.passsave.util.Serialization;
import bepo.passsave.views.GuiView;
import io.github.palexdev.materialfx.utils.SwingFXUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;

public class PassController implements Initializable {
    private GuiView guiView;
    private SettingsController settingsController;

    private static ObservableList<PassSave> passSaveList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    public void onControllersSet() {
        try {
            passSaveList = Serialization.deserialize();
        } catch (Exception e) {
            System.err.println("Error deserializing. Your data might not be accessible.");
        }

        sort();
    }

    public void sort() {
        Comparator<PassSave> comparator = null;

        switch (settingsController.getSettings().getSortProperty()) {
            case "name":
                comparator = Comparator.comparing(PassSave::getName, Comparator.nullsFirst(String::compareToIgnoreCase));
                break;
            case "username":
                comparator = Comparator.comparing(PassSave::getUsername, Comparator.nullsFirst(String::compareToIgnoreCase));
                break;
            case "mail":
                comparator = Comparator.comparing(PassSave::getMail, Comparator.nullsFirst(String::compareToIgnoreCase));
                break;
            case "addInfo":
                comparator = Comparator.comparing(PassSave::getAddInfo, Comparator.nullsFirst(String::compareToIgnoreCase));
                break;
            case "category":
                comparator = Comparator.comparing(PassSave::getCategory, Comparator.nullsFirst(String::compareToIgnoreCase));
                break;
            case "date":
                comparator = null;
                break;
        }

        if(comparator != null) {
            passSaveList.sort(comparator);
        }
    }

    public boolean createNew(String name, String username, String mail, String password, Category category, String addInfo) {
        if(nameIsTaken(name)) {
            return false;
        }

        try {
            if(!(name.isEmpty() || username.isEmpty() || mail.isEmpty() || password.isEmpty())) {
                if(addInfo.isEmpty()) {
                    passSaveList.add(new PassSave(name, username, mail, password, category.getName(), java.time.LocalDate.now()));
                } else {
                    passSaveList.add(new PassSave(name, username, mail, password, category.getName(), java.time.LocalDate.now(), addInfo));
                }

                try {
                    Serialization.serialize(passSaveList);
                } catch (Exception e) {
                    System.err.println("Could not serialize PassSaves: " + e);
                }

                guiView.refresh();
            } else {
                return false;
            }
        } catch(Exception e) {
            System.err.println("Could not create PassSave");
        }

        return true;
    }

    public void delete(Object selectedItem) throws Exception {
        if(!selectedItem.toString().isEmpty()) {
            System.out.println("Removing: " + selectedItem);

            for (PassSave passSave : passSaveList) {
                if(selectedItem.toString().equals(passSave.getName())) {
                    passSaveList.remove(passSave);
                    Serialization.serialize(passSaveList);
                    guiView.refresh();

                    return;
                }
            }
        }
    }

    public boolean edit(String initialName, String name, String username, String mail, String password, Category category, String addInfo) throws Exception {
        if(!initialName.equals(name)) {
            if (nameIsTaken(name)) {
                return false;
            }
        }

        for (PassSave passSave : passSaveList) {
            if(passSave.getName().equals(initialName)) {
                passSave.setName(name);
                passSave.setUsername(username);
                passSave.setMail(mail);
                passSave.setPassword(password);
                passSave.setCategory(category.getName());
                passSave.setAddInfo(addInfo);

                Serialization.serialize(passSaveList);
                guiView.refresh();
            }
        }

        return true;
    }

    private boolean nameIsTaken(String name) {
        for (PassSave passSave : passSaveList) {
            if(passSave.getName().equals(name)) {
                Alerts.infoAlert("Name already taken!", "Please choose a different name");
                return true;
            }
        }

        return false;
    }

    public ObservableList<PassSave> getAll() {
        return passSaveList;
    }

    public PassSave getSelectedItem(Object selectedItem) {
        if(!selectedItem.toString().isEmpty()) {
            System.out.println("Getting: " + selectedItem);

            for (PassSave passSave : passSaveList) {
                if(selectedItem.toString().equals(passSave.getName())) {
                    return passSave;
                }
            }
        }
        return null;
    }

    public ObservableList<String> getAllNames() {
        ObservableList<String> nameList = FXCollections.observableArrayList();

        for (PassSave passSave: passSaveList) {
            nameList.add(passSave.getName());
        }

        return nameList;
    }

    public void exportText() {
        try (BufferedWriter bw = new BufferedWriter(new PrintWriter("application-files/Export.txt"))) {
            for (PassSave passSave : passSaveList) {
                bw.write(passSave.toString());
                bw.newLine();
            }
            Alerts.infoAlert("Your data has been saved." , "The file containing the data can be found here: 'application-files/Export.txt'");
        } catch (IOException e) {
            System.err.println("Error exporting data.");
        }
    }

    public void exportImage() {
        String text = "";
        double scale = 0.0;

        File imageFile = new File("application-files/ExportImage.png");

        for (PassSave passSave : passSaveList) {
            text += passSave.toString() + "\n";
        }

        Text t = new Text(text);
        t.setSmooth(true);

        while(!(Math.round((t.getLayoutBounds().getWidth() * scale)) >= 1920)) {
            scale += 0.001;

            if(Math.round((t.getLayoutBounds().getWidth() * scale)) > 2000) {
                return;
            }
        }

        t.setScaleX(scale);
        t.setScaleY(scale);

        try {
            t.snapshot(null, null);
            ImageIO.write(SwingFXUtils.fromFXImage(t.snapshot(null, null), null), "png", imageFile);
            Alerts.infoAlert("Your data has been saved." , "The file containing the data can be found here: 'application-files/ExportImage.png'");
        } catch (Exception e) {
            System.err.println("Error exporting data.");
        }
    }

    public void clearData() throws Exception {
        passSaveList.clear();
        Serialization.serialize(passSaveList);
    }

    public void setGuiView(GuiView guiView) { this.guiView = guiView; }
    public void setSettingsController(SettingsController settingsController) { this.settingsController = settingsController; }
}
