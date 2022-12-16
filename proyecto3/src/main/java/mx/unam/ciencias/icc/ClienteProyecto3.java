package mx.unam.ciencias.icc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import mx.unam.ciencias.icc.fx.ControladorInterfazTarjetas;
import mx.unam.ciencias.icc.fx.ControladorTablaTarjetas;

/**
 * ClienteProyecto3: Parte del cliente para el proyecto 3: Hilos de ejecución y
 * enchufes.
 */
public class ClienteProyecto3 extends Application {

    /* Vista de la interfaz tarjetas. */
    private static final String INTERFAZ_TARJETAS_FXML = "fxml/interfaz-tarjetas.fxml";
    /* Vista de la tabla de tarjetas. */
    private static final String TABLA_TARJETAS_FXML = "fxml/tabla-tarjetas.fxml";
    /* Ícono de la Facultad de Ciencias. */
    private static final String ICONO_CIENCIAS = "icons/ciencias.png";

    /**
     * Inicia la aplicación.
     * 
     * @param escenario la ventana principal de la aplicación.
     * @throws Exception si algo sale mal. Literalmente así está definido.
     */
    @Override
    public void start(Stage escenario) throws Exception {
        ClassLoader cl = getClass().getClassLoader();
        String url = cl.getResource(ICONO_CIENCIAS).toString();
        escenario.getIcons().add(new Image(url));
        escenario.setTitle("Administrador de Tarjetas");
        FXMLLoader cargador = new FXMLLoader(cl.getResource(TABLA_TARJETAS_FXML));
        GridPane tablaTarjeta = (GridPane) cargador.load();
        ControladorTablaTarjetas controladorTablaTarjetas = cargador.getController();
        cargador = new FXMLLoader(cl.getResource(INTERFAZ_TARJETAS_FXML));
        BorderPane interfazTarjeta = (BorderPane) cargador.load();
        interfazTarjeta.setCenter(tablaTarjeta);
        ControladorInterfazTarjetas controladorInterfazTarjeta = cargador.getController();
        controladorInterfazTarjeta.setEscenario(escenario);

        controladorInterfazTarjeta.setControladorTablaTarjetas(
                controladorTablaTarjetas);

        Scene escena = new Scene(interfazTarjeta);
        escenario.setScene(escena);
        escenario.setOnCloseRequest(e -> controladorInterfazTarjeta.salir(null));
        escenario.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
