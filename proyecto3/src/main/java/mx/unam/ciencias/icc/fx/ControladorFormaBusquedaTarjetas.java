package mx.unam.ciencias.icc.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import mx.unam.ciencias.icc.CampoTarjeta;

/**
 * Clase para el controlador del contenido del diálogo para buscar tarjetas.
 */
public class ControladorFormaBusquedaTarjetas extends ControladorForma {

    /* El combo del campo. */
    @FXML private ComboBox<CampoTarjeta> opcionesCampo;
    /* El campo de texto para el valor. */
    @FXML private EntradaVerificable entradaValor;

    /* Inicializa el estado de la forma. */
    @FXML private void initialize() {
        entradaValor.setVerificador(s -> verificaValor(s));
        entradaValor.textProperty().addListener(
            (o, v, n) -> revisaValor(null));
    }

    /* Revisa el valor después de un cambio. */
    @FXML private void revisaValor(ActionEvent evento) {
        Tooltip.install(entradaValor, getTooltip());
        String s = entradaValor.getText();
        botonAceptar.setDisable(!entradaValor.esValida());
    }

    /* Manejador para cuando se activa el botón aceptar. */
    @FXML private void aceptar(ActionEvent evento) {
        aceptado = true;
        escenario.close();
    }

    /* Obtiene la pista. */
    private Tooltip getTooltip() {
        String m = "";
        switch (opcionesCampo.getValue()) {
        case NOMBRE_DEL_PROPIETARIO:
            m = "Buscar por nombre necesita al menos un carácter";
            break;
        case NUMERO_DE_TARJETA:
            m = "Buscar por número de tarjeta necesita un número con 16 dígitos.";
            break;
        case CODIGO_DE_SEGURIDAD:
            m = "Buscar por código de seguridad necesita  un número con 3 dígitos";
            break;
        case FECHA_DE_VENCIMIENTO:
            m = "Buscar por fecha necesita el formato m/a.";
            break;
        case SALDO:
            m = "Buscar por saldo necesita un número mayor o igual a 0.";
            break;
        }
        return new Tooltip(m);
    }

    /* Verifica el valor. */
    private boolean verificaValor(String s) {
        switch (opcionesCampo.getValue()) {
        case NOMBRE_DEL_PROPIETARIO:   return verificaNombre(s);
        case NUMERO_DE_TARJETA:   return verificaNumTarjeta(s);
        case CODIGO_DE_SEGURIDAD:   return verificaCodigo(s);
        case FECHA_DE_VENCIMIENTO: return verificaFecha(s);
        case SALDO:     return verificaSaldo(s);
        default:       return false;
        }
    }

    /* Verifica que el nombre sea válido. */
    private boolean verificaNombre(String n) {
        if (n == null || n.isEmpty())
            return false;
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
        return true;
    }

    /* Verifica que el código sea válido. */
    private boolean verificaCodigo(String c) {
        if (c == null || c.isEmpty())
            return false;
        int codigo;
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
        return true;
    }

    /* Verifica que el saldo sea válido. */
    private boolean verificaSaldo(String s) {
        if (s == null || s.isEmpty())
            return false;
        double saldo;
        try{
            saldo = Double.parseDouble(s);
        }catch(NumberFormatException e){
            return false;
        }
        return saldo >= 0;
    }

    /**
     * Regresa el campo seleccionado.
     * @return el campo seleccionado.
     */
    public CampoTarjeta getCampo() {
        return opcionesCampo.getValue();
    }

    /**
     * Regresa el valor ingresado.
     * @return el valor ingresado.
     */
    public Object getValor() {
        switch (opcionesCampo.getValue()) {
        case NOMBRE_DEL_PROPIETARIO:   return entradaValor.getText();
        case NUMERO_DE_TARJETA:   return entradaValor.getText();
        case CODIGO_DE_SEGURIDAD:     return Integer.parseInt(entradaValor.getText());
        case FECHA_DE_VENCIMIENTO: return entradaValor.getText();
        case SALDO:     return Double.parseDouble(entradaValor.getText());
        default:       return entradaValor.getText(); // No debería ocurrir.
        }
    }

    /**
     * Define el foco incial del diálogo.
     */
    @Override public void defineFoco() {
        entradaValor.requestFocus();
    }
}
