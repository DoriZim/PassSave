module bepo.passsave {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires MaterialFX;

    opens bepo.passsave to javafx.fxml;
    exports bepo.passsave;
    exports bepo.passsave.controller;
    opens bepo.passsave.controller to javafx.fxml;
    exports bepo.passsave.views;
    opens bepo.passsave.views to javafx.fxml;
    opens bepo.passsave.components to javafx.fxml;
}