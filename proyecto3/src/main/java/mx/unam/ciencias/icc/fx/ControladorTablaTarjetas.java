package mx.unam.ciencias.icc.fx;

import javafx.collections.ListChangeListener.Change;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import mx.unam.ciencias.icc.Tarjeta;
import mx.unam.ciencias.icc.Lista;

/**
 * Clase para el controlador de la tabla para mostrar la base de datos.
 */
public class ControladorTablaTarjetas {

    /* La tabla. */
    @FXML
    private TableView<Tarjeta> tabla;

    /* La columna del nombre de propietario. */
    @FXML
    private TableColumn<Tarjeta, String> columnaNombre;
    /* La columna del número de tarjeta. */
    @FXML
    private TableColumn<Tarjeta, String> columnaNumTarjeta;
    /* La columna del código de seguridad. */
    @FXML
    private TableColumn<Tarjeta, Number> columnaCodigo;
    /* La columna de la fecha de vencimiento. */
    @FXML
    private TableColumn<Tarjeta, String> columnaFecha;
    /* La columna del saldo. */
    @FXML
    private TableColumn<Tarjeta, Number> columnaSaldo;

    /* El modelo de la selección. */
    TableView.TableViewSelectionModel<Tarjeta> modeloSeleccion;
    /* La selección. */
    private ObservableList<TablePosition> seleccion;
    /* Lista de escuchas de selección. */
    private Lista<EscuchaSeleccion> escuchas;
    /* Los renglones en la tabla. */
    private ObservableList<Tarjeta> renglones;

    /* Inicializa el controlador. */
    @FXML
    private void initialize() {
        renglones = tabla.getItems();
        modeloSeleccion = tabla.getSelectionModel();
        modeloSeleccion.setSelectionMode(SelectionMode.MULTIPLE);
        seleccion = modeloSeleccion.getSelectedCells();
        ListChangeListener<TablePosition> lcl = c -> cambioEnSeleccion();
        seleccion.addListener(lcl);
        columnaNombre.setCellValueFactory(c -> c.getValue().nombreDelPropietarioProperty());
        columnaNumTarjeta.setCellValueFactory(c -> c.getValue().numeroDeTarjetaProperty());
        columnaCodigo.setCellValueFactory(c -> c.getValue().codigoDeSeguridadProperty());
        columnaFecha.setCellValueFactory(c -> c.getValue().fechaDeVencimientoProperty());
        columnaSaldo.setCellValueFactory(
                c -> c.getValue().saldoProperty());
        escuchas = new Lista<EscuchaSeleccion>();
    }

    /* Notifica a los escuchas que hubo un cambio en la selección. */
    private void cambioEnSeleccion() {
        for (EscuchaSeleccion escucha : escuchas)
            escucha.renglonesSeleccionados(seleccion.size());
    }

    /**
     * Limpia la tabla.
     */
    public void limpiaTabla() {
        renglones.clear();
    }

    /**
     * Agrega un renglón a la tabla.
     * 
     * @param tarjeta el renglón a agregar.
     */
    public void agregaRenglon(Tarjeta tarjeta) {
        renglones.add(tarjeta);
        tabla.sort();
    }

    /**
     * Elimina un renglón de la tabla.
     * 
     * @param tarjeta el renglón a eliminar.
     */
    public void eliminaRenglon(Tarjeta tarjeta) {
        renglones.remove(tarjeta);
        tabla.sort();
    }

    /**
     * Selecciona renglones de la tabla.
     * 
     * @param tarjetas los renglones a seleccionar.
     */
    public void seleccionaRenglones(Lista<Tarjeta> tarjetas) {
        modeloSeleccion.clearSelection();
        for (Tarjeta tarjeta : tarjetas)
            modeloSeleccion.select(tarjeta);
    }

    /**
     * Regresa una lista con los registros seleccionados en la tabla.
     * 
     * @return una lista con los registros seleccionados en la tabla.
     */
    public Lista<Tarjeta> getSeleccion() {
        Lista<Tarjeta> seleccionados = new Lista<Tarjeta>();
        for (TablePosition tp : seleccion) {
            int r = tp.getRow();
            seleccionados.agregaFinal(renglones.get(r));
        }
        return seleccionados;
    }

    /**
     * Regresa la tarjeta seleccionada en la tabla.
     * 
     * @return la tarjeta seleccionada en la tabla.
     */
    public Tarjeta getRenglonSeleccionado() {
        int r = seleccion.get(0).getRow();
        return renglones.get(r);
    }

    /**
     * Agrega un escucha de selección.
     * 
     * @param escucha el escucha a agregar.
     */
    public void agregaEscuchaSeleccion(EscuchaSeleccion escucha) {
        escuchas.agregaFinal(escucha);
    }

    /**
     * Fuerza un reordenamiento de la tabla.
     */
    public void reordena() {
        tabla.sort();
    }

    /**
     * Enfoca la tabla.
     */
    public void enfocaTabla() {
        tabla.requestFocus();
    }
}
