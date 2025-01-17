package View;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main_Menu_Controller {
    @FXML
    private void openPlayerSelect(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Parent root= FXMLLoader.load(getClass().getResource("player_select.fxml"));
        stage.setScene(new Scene(root, 1280, 720));
        stage.setFullScreen(false);
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    private void openOptions(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Parent root= FXMLLoader.load(getClass().getResource("options.fxml"));
        stage.setScene(new Scene(root, 1280, 720));
        stage.setFullScreen(false);
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    private void openCredits(ActionEvent event)throws IOException{
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Parent root= FXMLLoader.load(getClass().getResource("credits.fxml"));
        stage.setScene(new Scene(root, 1280, 720));
        stage.setFullScreen(false);
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    private void openHowToPlay(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("how_to_play.fxml"));
        stage.setScene(new Scene(root, 1280, 720));
        stage.setFullScreen(false);
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    private void exit(){
        Platform.exit();
    }
}
