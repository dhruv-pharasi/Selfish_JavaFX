import javafx.scene.control.Alert;

public class AlertBox {

    private String msg = "Invalid file name";

    public AlertBox() {
    }

    public AlertBox(String msg) {
        this.msg = msg;
    }

    public void showAlertBox() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(GameApp.GAMENAME);
        alert.setHeaderText("Error");
        alert.setContentText(this.msg);
        alert.showAndWait();
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

}
