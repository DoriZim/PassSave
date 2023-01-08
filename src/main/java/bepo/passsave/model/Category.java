package bepo.passsave.model;

import javafx.scene.paint.Color;
import java.io.Serializable;

public class Category implements Serializable {
    String name, description;
    Color color;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Category(String name, String description, Color color) {
        this.name = name;
        this.description = description;
        this.color = color;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
