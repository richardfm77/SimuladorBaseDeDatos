package mx.unam.ciencias.icc.fx;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.unam.ciencias.icc.BaseDeDatosTarjetas;
import mx.unam.ciencias.icc.Tarjeta;
import mx.unam.ciencias.icc.EventoBaseDeDatos;
import mx.unam.ciencias.icc.Lista;
import mx.unam.ciencias.icc.red.Conexion;
import mx.unam.ciencias.icc.red.Mensaje;

/**
 * Clase para el controlador de la ventana principal de la aplicación.
 */
public class ControladorInterfazTarjetas {

    /* Vista de la forma para conectarse. */
    private static final String CONECTAR_FXML = "fxml/forma-conectar.fxml";
    /* Vista de la forma para realizar búsquedas de tarjetas. */
    private static final String BUSQUEDA_TARJETAS_FXML = "fxml/forma-busqueda-tarjetas.fxml";
    /* Vista de la forma para agregar/editar tarjetas. */
    private static final String TARJETA_FXML = "fxml/forma-tarjeta.fxml";

    /* Opción de menu para conectar. */
    @FXML
    private MenuItem menuConectar;
    /* Opción de menu para desconectar. */
    @FXML
    private MenuItem menuDesconectar;
    /* Opción de menu para agregar. */
    @FXML
    private MenuItem menuAgregar;
    /* Opción de menu para editar. */
    @FXML
    private MenuItem menuEditar;
    /* Opción de menu para eliminar. */
    @FXML
    private MenuItem menuEliminar;
    /* Opción de menu para buscar. */
    @FXML
    private MenuItem menuBuscar;
    /* El botón de agregar. */
    @FXML
    private Button botonAgregar;
    /* El botón de editar. */
    @FXML
    private Button botonEditar;
    /* El botón de eliminar. */
    @FXML
    private Button botonEliminar;
    /* El botón de buscar. */
    @FXML
    private Button botonBuscar;

    /* La ventana. */
    private Stage escenario;
    /* El controlador de tabla. */
    private ControladorTablaTarjetas controladorTablaTarjetas;
    /* La base de datos. */
    private BaseDeDatosTarjetas bdd;
    /* La conexión del cliente. */
    private Conexion<Tarjeta> conexion;
    /* Si hay o no conexión. */
    private boolean conectado;
    /* Número de tarjetas seleccionadas. */
    private int seleccionados;

    /* Inicializa el controlador. */
    @FXML
    private void initialize() {
        setSeleccionados(0);
        setConectado(false);
        bdd = new BaseDeDatosTarjetas();
        bdd.agregaEscucha((e, r1, r2) -> manejaEventoBaseDeDatos(e, r1, r2));
    }

    /* Conecta el cliente con el servidor. */
    @FXML
    private void conectar(ActionEvent evento) {
        if (conectado)
            return;

        String servidor = null;
        int puerto = -1;

        try {
            FXMLLoader cargador = new FXMLLoader();
            ClassLoader cl = getClass().getClassLoader();
            cargador.setLocation(cl.getResource(CONECTAR_FXML));
            AnchorPane pagina = (AnchorPane) cargador.load();

            Stage escenario = new Stage();
            escenario.initOwner(this.escenario);
            escenario.initModality(Modality.WINDOW_MODAL);
            escenario.setTitle("Conectar a servidor");
            Scene escena = new Scene(pagina);
            escenario.setScene(escena);

            ControladorFormaConectar controlador = cargador.getController();
            controlador.setEscenario(escenario);

            escenario.setOnShown(w -> controlador.defineFoco());
            escenario.setResizable(false);
            escenario.showAndWait();
            controladorTablaTarjetas.enfocaTabla();
            if (!controlador.isAceptado())
                return;

            servidor = controlador.getServidor();
            puerto = controlador.getPuerto();
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
            String mensaje = String.format("Ocurrió un error al tratar de " +
                    "cargar el diálogo '%s'.", CONECTAR_FXML);
            dialogoError("Error al cargar interfaz", mensaje);
            return;
        }

        try {
            Socket enchufe = new Socket(servidor, puerto);
            conexion = new Conexion<Tarjeta>(bdd, enchufe);
            new Thread(() -> conexion.recibeMensajes()).start();
            conexion.agregaEscucha((c, m) -> mensajeRecibido(c, m));
            conexion.enviaMensaje(Mensaje.BASE_DE_DATOS);
        } catch (IOException ioe) {
            conexion = null;
            String mensaje = String.format("Ocurrió un error al tratar de " +
                    "conectarnos a %s:%d.\n", servidor, puerto);
            dialogoError("Error al establecer conexión", mensaje);
            return;
        }
        setConectado(true);
    }

    /* Desconecta el cliente del servidor. */
    @FXML
    private void desconectar(ActionEvent evento) {
        if (!conectado)
            return;
        setConectado(false);
        conexion.desconecta();
        conexion = null;
        bdd.limpia();
    }

    /* Cambia la interfaz gráfica dependiendo de hay o no conexión. */
    public void setConectado(boolean conectado) {
        this.conectado = conectado;
        menuConectar.setDisable(conectado);
        menuDesconectar.setDisable(!conectado);
        menuAgregar.setDisable(!conectado);
        menuBuscar.setDisable(!conectado);
        botonAgregar.setDisable(!conectado);
        botonBuscar.setDisable(!conectado);
    }

    /**
     * Termina el programa.
     * 
     * @param evento el evento que generó la acción.
     */
    @FXML
    public void salir(ActionEvent evento) {
        desconectar(evento);
        Platform.exit();
    }

    /* Agrega una nueva tarjeta. */
    @FXML
    private void agregaTarjeta(ActionEvent evento) {
        ControladorFormaTarjeta controlador = construyeDialogoTarjeta("Agregar tarjeta", null);
        if (controlador == null)
            return;
        controlador.setVerbo("Agregar");
        controlador.getEscenario().showAndWait();
        controladorTablaTarjetas.enfocaTabla();
        if (!controlador.isAceptado())
            return;
        bdd.agregaRegistro(controlador.getTarjeta());
        try {
            conexion.enviaMensaje(Mensaje.REGISTRO_AGREGADO);
            conexion.enviaRegistro(controlador.getTarjeta());
        } catch (IOException ioe) {
            dialogoError("Error con el servidor",
                    "No se pudo enviar una tarjeta a agregar.");
        }
    }

    /* Edita una tarjeta. */
    @FXML
    private void editaTarjeta(ActionEvent evento) {
        Tarjeta tarjeta = controladorTablaTarjetas.getRenglonSeleccionado();
        ControladorFormaTarjeta controlador = construyeDialogoTarjeta("Editar tarjeta", tarjeta);
        if (controlador == null)
            return;
        controlador.setVerbo("Actualizar");
        controlador.getEscenario().showAndWait();
        controladorTablaTarjetas.enfocaTabla();
        if (!controlador.isAceptado())
            return;
        try {
            conexion.enviaMensaje(Mensaje.REGISTRO_MODIFICADO);
            conexion.enviaRegistro(tarjeta);
            conexion.enviaRegistro(controlador.getTarjeta());
        } catch (IOException ioe) {
            dialogoError("Error con el servidor",
                    "No se pudieron enviar tarjetas a modificar.");
        }
        bdd.modificaRegistro(tarjeta, controlador.getTarjeta());
    }

    /* Elimina una o varias tarjetas. */
    @FXML
    private void eliminaTarjetas(ActionEvent evento) {
        String sujeto = (seleccionados == 1) ? "tarjeta" : "tarjetas";
        String titulo = "Eliminar " + sujeto;
        String mensaje = (seleccionados == 1) ? "Esto eliminará a la tarjeta seleccionada"
                : "Esto eliminará a las tarjetas seleccionadas";
        if (!dialogoDeConfirmacion(titulo, mensaje, "¿Está seguro?",
                "Eliminar " + sujeto,
                "Conservar " + sujeto))
            return;
        for (Tarjeta tarjeta : controladorTablaTarjetas.getSeleccion()) {
            bdd.eliminaRegistro(tarjeta);
            try {
                conexion.enviaMensaje(Mensaje.REGISTRO_ELIMINADO);
                conexion.enviaRegistro(tarjeta);
            } catch (IOException ioe) {
                dialogoError("Error con el servidor",
                        "No se pudo enviar una tarjeta a eliminar.");
            }
        }
    }

    /* Busca tarjetas. */
    @FXML
    private void buscaTarjetas(ActionEvent evento) {
        try {
            ClassLoader cl = getClass().getClassLoader();
            FXMLLoader cargador = new FXMLLoader(cl.getResource(BUSQUEDA_TARJETAS_FXML));
            AnchorPane pagina = (AnchorPane) cargador.load();

            Stage escenario = new Stage();
            escenario.setTitle("Buscar tarjetas");
            escenario.initOwner(this.escenario);
            escenario.initModality(Modality.WINDOW_MODAL);
            Scene escena = new Scene(pagina);
            escenario.setScene(escena);

            ControladorFormaBusquedaTarjetas controlador;
            controlador = cargador.getController();
            controlador.setEscenario(escenario);

            escenario.setOnShown(w -> controlador.defineFoco());
            escenario.setResizable(false);
            escenario.showAndWait();
            controladorTablaTarjetas.enfocaTabla();
            if (!controlador.isAceptado())
                return;

            Lista<Tarjeta> resultados = bdd.buscaRegistros(controlador.getCampo(),
                    controlador.getValor());

            controladorTablaTarjetas.seleccionaRenglones(resultados);
        } catch (IOException | IllegalStateException e) {
            String mensaje = String.format("Ocurrió un error al tratar de " +
                    "cargar el diálogo '%s'.",
                    BUSQUEDA_TARJETAS_FXML);
            dialogoError("Error al cargar interfaz", mensaje);
        }
    }

    /* Muestra un diálogo con información del programa. */
    @FXML
    private void acercaDe(ActionEvent evento) {
        Alert dialogo = new Alert(AlertType.INFORMATION);
        dialogo.initOwner(escenario);
        dialogo.initModality(Modality.WINDOW_MODAL);
        dialogo.setTitle("Acerca de Administrador de Tarjetas Bancarias.");
        dialogo.setHeaderText(null);
        dialogo.setContentText("Aplicación para administrar tarjetas bancarias.\n" +
                "Copyright © Proyecto 3 ICC Facultad de Ciencias, UNAM.");
        dialogo.showAndWait();
        controladorTablaTarjetas.enfocaTabla();
    }

    /**
     * Define el controlador de tabla.
     * 
     * @param controladorTablaTarjeta el controlador de tabla.
     */
    public void setControladorTablaTarjetas(ControladorTablaTarjetas controladorTablaTarjeta) {
        this.controladorTablaTarjetas = controladorTablaTarjeta;

        controladorTablaTarjeta.agregaEscuchaSeleccion(
                n -> setSeleccionados(n));
    }

    /**
     * Define el escenario.
     * 
     * @param escenario el escenario.
     */
    public void setEscenario(Stage escenario) {
        this.escenario = escenario;
    }

    /* Maneja un evento de cambio en la base de datos. */
    private void manejaEventoBaseDeDatos(EventoBaseDeDatos evento,
            Tarjeta tarjeta1,
            Tarjeta tarjeta2) {
        switch (evento) {
            case BASE_LIMPIADA:
                Platform.runLater(() -> limpiaTabla());
                break;
            case REGISTRO_AGREGADO:
                Platform.runLater(() -> agregaTarjeta(tarjeta1));
                break;
            case REGISTRO_ELIMINADO:
                Platform.runLater(() -> eliminaTarjeta(tarjeta1));
                break;
            case REGISTRO_MODIFICADO:
                Platform.runLater(() -> reordenaTabla());
                break;
        }
    }

    /*
     * Actualiza la interfaz dependiendo del número de renglones
     * seleccionados.
     */
    private void setSeleccionados(int seleccionados) {
        this.seleccionados = seleccionados;
        menuEliminar.setDisable(seleccionados == 0);
        menuEditar.setDisable(seleccionados != 1);
        botonEliminar.setDisable(seleccionados == 0);
        botonEditar.setDisable(seleccionados != 1);
    }

    /* Crea un diálogo con una pregunta que hay que confirmar. */
    private boolean dialogoDeConfirmacion(String titulo,
            String mensaje, String pregunta,
            String aceptar, String cancelar) {
        Alert dialogo = new Alert(AlertType.CONFIRMATION);
        dialogo.setTitle(titulo);
        dialogo.setHeaderText(mensaje);
        dialogo.setContentText(pregunta);

        ButtonType si = new ButtonType(aceptar);
        ButtonType no = new ButtonType(cancelar, ButtonData.CANCEL_CLOSE);
        dialogo.getButtonTypes().setAll(si, no);

        Optional<ButtonType> resultado = dialogo.showAndWait();
        controladorTablaTarjetas.enfocaTabla();
        return resultado.get() == si;
    }

    /* Recibe los mensajes de la conexión. */
    private void mensajeRecibido(Conexion<Tarjeta> conexion, Mensaje mensaje) {
        switch (mensaje) {
            case BASE_DE_DATOS:
                manejaBaseDeDatos(conexion);
                break;
            case REGISTRO_AGREGADO:
                manejaRegistroAlterado(conexion, mensaje);
                break;
            case REGISTRO_ELIMINADO:
                manejaRegistroAlterado(conexion, mensaje);
                break;
            case REGISTRO_MODIFICADO:
                manejaRegistroModificado(conexion);
                break;
            case DESCONECTAR:
                Platform.runLater(() -> desconectar(null));
                break;
            case DETENER_SERVICIO:
                // Se ignora.
                break;
            case ECO:
                // Se ignora.
                break;
            case INVALIDO:
                Platform.runLater(() -> dialogoError("Error con el servidor",
                        "Mensaje inválido recibido. " +
                                "Se finalizará la conexión."));
                break;
        }
    }

    /* Maneja el mensaje BASE_DE_DATOS. */
    private void manejaBaseDeDatos(Conexion<Tarjeta> conexion) {
        try {
            conexion.recibeBaseDeDatos();
        } catch (IOException ioe) {
            String m = "No se pudo recibir la base de datos. " +
                    "Se finalizará la conexión.";
            Platform.runLater(() -> dialogoError("Error con el servidor", m));
            return;
        }
    }

    /* Maneja los mensajes REGISTRO_AGREGADO y REGISTRO_MODIFICADO. */
    private void manejaRegistroAlterado(Conexion<Tarjeta> conexion,
            Mensaje mensaje) {
        Tarjeta e;
        try {
            e = conexion.recibeRegistro();
        } catch (IOException ioe) {
            String m = "No se pudo recibir un registro. " +
                    "Se finalizará la conexión.";
            Platform.runLater(() -> dialogoError("Error con el servidor", m));
            return;
        }
        if (mensaje == Mensaje.REGISTRO_AGREGADO)
            bdd.agregaRegistro(e);
        else
            bdd.eliminaRegistro(e);
    }

    /* Maneja el mensaje REGISTRO_MODIFICADO. */
    private void manejaRegistroModificado(Conexion<Tarjeta> conexion) {
        Tarjeta e1, e2;
        try {
            e1 = conexion.recibeRegistro();
            e2 = conexion.recibeRegistro();
        } catch (IOException ioe) {
            String m = "No se pudieron recibir registros." +
                    "Se finalizará la conexión.";
            Platform.runLater(() -> dialogoError("Error con el servidor", m));
            return;
        }
        bdd.modificaRegistro(e1, e2);
    }

    /* Construye un diálogo para crear o editar una tarjeta. */
    private ControladorFormaTarjeta construyeDialogoTarjeta(String titulo,
            Tarjeta tarjeta) {
        try {
            ClassLoader cl = getClass().getClassLoader();
            FXMLLoader cargador = new FXMLLoader(cl.getResource(TARJETA_FXML));
            AnchorPane pagina = (AnchorPane) cargador.load();

            Stage escenario = new Stage();
            escenario.setTitle(titulo);
            escenario.initOwner(this.escenario);
            escenario.initModality(Modality.WINDOW_MODAL);
            Scene escena = new Scene(pagina);
            escenario.setScene(escena);

            ControladorFormaTarjeta controlador = cargador.getController();
            controlador.setEscenario(escenario);
            controlador.setTarjeta(tarjeta);

            escenario.setOnShown(w -> controlador.defineFoco());
            escenario.setResizable(false);
            return controlador;
        } catch (IOException | IllegalStateException e) {
            String mensaje = String.format("Ocurrió un error al tratar de cargar " +
                    "el diálogo '%s'.", TARJETA_FXML);
            dialogoError("Error al cargar interfaz", mensaje);
            return null;
        }
    }

    /* Muestra un diálogo de advertencia. */
    private void dialogoAdvertencia(String titulo,
            String mensaje,
            boolean limpia) {
        Alert dialogo = new Alert(AlertType.WARNING);
        dialogo.setTitle(titulo);
        dialogo.setHeaderText(null);
        dialogo.setContentText(mensaje);
        if (limpia)
            dialogo.setOnCloseRequest(e -> limpiaTabla());
        dialogo.show();
        controladorTablaTarjetas.enfocaTabla();
    }

    /* Muestra un diálogo de error. */
    private void dialogoError(String titulo, String mensaje) {
        if (conectado)
            desconectar(null);
        Alert dialogo = new Alert(AlertType.ERROR);
        dialogo.setTitle(titulo);
        dialogo.setHeaderText(null);
        dialogo.setContentText(mensaje);
        dialogo.setOnCloseRequest(e -> limpiaTabla());
        dialogo.show();
        controladorTablaTarjetas.enfocaTabla();
    }

    /* Agrega una tarjeta a la tabla. */
    private void agregaTarjeta(Tarjeta tarjeta) {
        controladorTablaTarjetas.agregaRenglon(tarjeta);
    }

    /* Elimina una tarjeta de la tabla. */
    private void eliminaTarjeta(Tarjeta tarjeta) {
        controladorTablaTarjetas.eliminaRenglon(tarjeta);
    }

    /* Reordena la tabla. */
    private void reordenaTabla() {
        controladorTablaTarjetas.reordena();
    }

    /* Limpia la tabla. */
    private void limpiaTabla() {
        controladorTablaTarjetas.limpiaTabla();
    }
}
