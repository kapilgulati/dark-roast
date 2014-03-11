package org.kg.keva;

import java.awt.Container;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.kg.keva.listeners.CatalogClient;

public class FXMLController implements Initializable {

    private WebSocketContainer container;
    private Session session;
    private CatalogClient client = new CatalogClient();

    @FXML
    private Label label;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        try {
            System.out.println("You clicked me!");
            client.saveCatalog("test", "test1");
            label.setText("Hello World!");
        } catch (IOException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        label.setText("Trying to connnect to server... ");
        container = ContainerProvider.getWebSocketContainer();
        try {
            URI uri = new URI("ws://localhost:8080/tiger/catalog");
            session = container.connectToServer(client, uri);
        } catch (Exception ex) {
            label.setText("Unable to connnect to server: " + ex.getMessage());
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void disconnect() throws IOException {
        session.close();
    }
}
