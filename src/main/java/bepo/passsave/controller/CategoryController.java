package bepo.passsave.controller;

import bepo.passsave.model.Category;
import bepo.passsave.model.PassSave;
import bepo.passsave.util.Alerts;
import bepo.passsave.util.Serialization;
import bepo.passsave.views.GuiView;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.util.Comparator;

//todo - think about whether real categories should be stored in the PassSaves or not (would make setting color and viewing description easier)
public class CategoryController {
    private GuiView guiView;
    private PassController passController;
    private ObservableList<Category> categoryList;
    private final Category placeholder = new Category("* No category selected *", "", null);

    public void onControllersSet() throws Exception {
        categoryList = Serialization.deserializeCategories();

        if(categoryList.isEmpty()) {
            categoryList.add(new Category("Standard", "This is the default category", Color.BLACK));
        }

        Comparator<Category> nameComparator = Comparator.comparing(Category::getName, Comparator.nullsFirst(String::compareToIgnoreCase));
        categoryList.sort(nameComparator);
    }

    public ObservableList<Category> getCategories() {
        return categoryList;
    }

    public void deleteCategory(Category category) throws Exception {
        System.out.println("Deleting category" + category);

        categoryList.remove(category);
        changePassSaveCategory(category.getName(), "");

        Serialization.serializeCategories(categoryList);
    }

    public boolean createCategory(String name, String description, Color color) throws Exception {
        System.out.println("Creating category " + name);

        if(nameIsTaken(name)) {
            return false;
        }

        categoryList.add(new Category(name, description, color));
        Serialization.serializeCategories(categoryList);

        return true;
    }

    public boolean editCategory(Category editCategory, String name, String description, Color color) throws Exception {
        String oldName = editCategory.getName();

        if(!oldName.equals(name)) {
            if (nameIsTaken(name)) {
                return false;
            }
        }

        editCategory.setName(name);
        editCategory.setDescription(description);
        editCategory.setColor(color);

        if(!oldName.equals(name)) {
            System.out.println("Changing " + oldName + " to " + name);
            changePassSaveCategory(oldName, name);
        }

        Serialization.serializeCategories(categoryList);

        return true;
    }

    public Category getCategory(String categoryName) {
        for (Category category : categoryList) {
            if(category.getName().equals(categoryName)) {
                return category;
            }
        }
        return null;
    }

    public Category getPlaceholder() {
        return placeholder;
    }

    private void changePassSaveCategory(String oldName, String newName) throws Exception {
        for (PassSave passSave : passController.getAll()) {
            if(passSave.getCategory().equals(oldName)) {
                System.out.println("found");
                passSave.setCategory(newName);
            }
        }
        Serialization.serialize(passController.getAll());
        guiView.refresh();
    }

    private boolean nameIsTaken(String name) {
        for (Category category : categoryList) {
            if(category.getName().equals(name)) {
                Alerts.infoAlert("Name already taken!", "Please choose a different name");
                return true;
            }
        }

        return false;
    }

    public void setGuiView(GuiView guiView) { this.guiView = guiView; }
    public void setPassController(PassController passController) { this.passController = passController; }
}
