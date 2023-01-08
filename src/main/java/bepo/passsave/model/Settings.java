package bepo.passsave.model;

public class Settings {
    private boolean categoryClose, passSaveClose, exportScan;
    private String sortProperty;

    public Settings(boolean categoryClose, boolean passSaveClose, boolean exportScan, String sortProperty) {
        this.categoryClose = categoryClose;
        this.passSaveClose = passSaveClose;
        this.exportScan = exportScan;
        this.sortProperty = sortProperty;
    }

    public boolean isCategoryClose() {
        return categoryClose;
    }

    public void setCategoryClose(boolean categoryClose) {
        this.categoryClose = categoryClose;
    }

    public boolean isPassSaveClose() {
        return passSaveClose;
    }

    public void setPassSaveClose(boolean passSaveClose) {
        this.passSaveClose = passSaveClose;
    }

    public boolean isExportScan() {
        return exportScan;
    }

    public void setExportScan(boolean exportScan) {
        this.exportScan = exportScan;
    }

    public String getSortProperty() { return sortProperty; }

    public void setSortProperty(String sortProperty) { this.sortProperty = sortProperty; }

    @Override
    public String toString() {
        return "Settings{" +
                "categoryClose=" + categoryClose +
                ", passSaveClose=" + passSaveClose +
                ", exportScan=" + exportScan +
                ", sortProperty=" + sortProperty +
                '}';
    }
}
