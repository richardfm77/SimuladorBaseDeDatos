package mx.unam.ciencias.icc.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import mx.unam.ciencias.icc.Tarjeta;

/**
 * Clase para el controlador del contenido del diálogo para editar y crear
 * tarjetas.
 */
public class ControladorFormaTarjeta extends ControladorForma {

    /* La entrada verificable para el nombre. */
    @FXML
    private EntradaVerificable entradaNombre;
    /* La entrada verificable para el número de tarjeta. */
    @FXML
    private EntradaVerificable entradaNumTarjeta;
    /* La entrada verificable para el código de seguridad. */
    @FXML
    private EntradaVerificable entradaCodigo;
    /* La entrada verificable para la fecha de vencimiento. */
    @FXML
    private EntradaVerificable entradaFecha;
    /* La entrada verificable para el saldo. */
    @FXML
    private EntradaVerificable entradaSaldo;

    /* El valor del nombre. */
    private String nombre;
    /* El valor del número de tarjeta. */
    private String numTarjeta;
    /* El valor del código. */
    private int codigo;
    /* El valor de la fecha. */
    private String fecha;
    /* El valor del saldo. */
    private double saldo;

    /* La tarjeta creada o editada. */
    private Tarjeta tarjeta;

    /* Inicializa el estado de la forma. */
    @FXML
    private void initialize() {
        entradaNombre.setVerificador(n -> verificaNombre(n));
        entradaNumTarjeta.setVerificador(nt -> verificaNumTarjeta(nt));
        entradaCodigo.setVerificador(cs -> verificaCodigo(cs));
        entradaFecha.setVerificador(f -> verificaFecha(f));
        entradaSaldo.setVerificador(a -> verificaSaldo(a));

        entradaNombre.textProperty().addListener(
                (o, v, n) -> verificaTarjeta());
        entradaNumTarjeta.textProperty().addListener(
                (o, v, n) -> verificaTarjeta());
        entradaCodigo.textProperty().addListener(
                (o, v, n) -> verificaTarjeta());
        entradaFecha.textProperty().addListener(
                (o, v, n) -> verificaTarjeta());
        entradaSaldo.textProperty().addListener(
                (o, v, n) -> verificaTarjeta());
    }

    /* Manejador para cuando se activa el botón aceptar. */
    @FXML
    private void aceptar(ActionEvent evento) {
        actualizaTarjeta();
        aceptado = true;
        escenario.close();
    }

    /**
     * Define la tarjeta del diálogo.
     * 
     * @param tarjeta la nueva tarjeta del diálogo.
     */
    public void setTarjeta(Tarjeta tarjeta) {
        this.tarjeta = tarjeta;
        if (tarjeta == null)
            return;
        this.tarjeta = new Tarjeta(null, null, 0, null, 0);
        this.tarjeta.actualiza(tarjeta);
        entradaNombre.setText(tarjeta.getNombreDelPropietario());
        entradaNumTarjeta.setText(tarjeta.getNumeroDeTarjeta());
        String c = String.format("%3d", tarjeta.getCodigoDeSeguridad());
        entradaCodigo.setText(c);
        entradaFecha.setText(tarjeta.getFechaDeVencimiento());
        entradaSaldo.setText(String.valueOf(tarjeta.getSaldo()));
    }

    /* Verifica que los cinco campos sean válidos. */
    private void verificaTarjeta() {
        boolean n = entradaNombre.esValida();
        boolean nt = entradaNumTarjeta.esValida();
        boolean c = entradaCodigo.esValida();
        boolean f = entradaFecha.esValida();
        boolean s = entradaSaldo.esValida();
        botonAceptar.setDisable(!n || !nt || !c || !f || !s);
    }

    /* Verifica que el nombre sea válido. */
    private boolean verificaNombre(String n) {
        if (n == null || n.isEmpty())
            return false;
        nombre = n;
        return true;
    }

    /* Verifica que el número de targeta sea válido. */
    private boolean verificaNumTarjeta(String nt) {
        if (nt == null || nt.isEmpty() || nt.length() != 16)
            return false;
        try {
            for (int i = 0; i < 16; i++) {
                Integer.parseInt(nt.substring(i, i + 1));
            }
        } catch (NumberFormatException e) {
            return false;
        }

        numTarjeta = nt;
        return true;
    }

    /* Verifica que el código sea válido. */
    private boolean verificaCodigo(String c) {
        if (c == null || c.isEmpty())
            return false;
        try {
            codigo = Integer.parseInt(c);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return codigo >= 100 && codigo <= 999;
    }

    /* Verifica que la fecha sea válida. */
    private boolean verificaFecha(String f) {
        if (f == null || f.isEmpty())
            return false;
        String fech[] = f.split("/");
        if (fech.length != 2)
            return false;
        int m, a;
        try {
            m = Integer.parseInt(fech[0]);
            a = Integer.parseInt(fech[1]);
        } catch (NumberFormatException nfe) {
            return false;
        }
        if (m < 0 || m > 12 || a < 21 || a > 100)
            return false;
        fecha = (m < 10 ? "0" + m : m) + "/" + a;
        return true;
    }

    /* Verifica que el saldo sea válido. */
    private boolean verificaSaldo(String s) {
        if (s == null || s.isEmpty())
            return false;
        try{
            saldo = Double.parseDouble(s);
        }catch(NumberFormatException e){
            return false;
        }
        return saldo >= 0;
    }

    /* Actualiza la tarjeta, o la crea si no existe. */
    private void actualizaTarjeta() {
        if (tarjeta != null) {
            tarjeta.setNombreDelPropietario(nombre);
            tarjeta.setNumeroDeTarjeta(numTarjeta);
            tarjeta.setCodigoDeSeguridad(codigo);
            tarjeta.setFechaDeVencimiento(fecha);
            tarjeta.setSaldo(saldo);
        } else {
            tarjeta = new Tarjeta(nombre, numTarjeta,codigo,fecha,saldo);
        }
    }

    /**
     * Regresa la tarjeta del diálogo.
     * 
     * @return la tarjeta del diálogo.
     */
    public Tarjeta getTarjeta() {
        return tarjeta;
    }

    /**
     * Define el verbo del botón de aceptar.
     * 
     * @param verbo el nuevo verbo del botón de aceptar.
     */
    public void setVerbo(String verbo) {
        botonAceptar.setText(verbo);
    }

    /**
     * Define el foco incial del diálogo.
     */
    @Override
    public void defineFoco() {
        entradaNombre.requestFocus();
    }
}
