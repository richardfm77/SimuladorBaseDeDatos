package mx.unam.ciencias.icc;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Clase para representar tarjetas bancarias de débito. Una tarjeta tiene nombre
 * de propietario, número de
 * tarjeta, código de seguridad, fecha de vencimiento y saldo. La clase
 * implementa {@link Registro}, por lo que
 * puede serializarse en una línea de texto y deserializarse de una línea de
 * texto; además de determinar si sus campos cazan valores arbitrarios y
 * actualizarse con los valores de otra tarjeta.
 */
public class Tarjeta implements Registro<Tarjeta, CampoTarjeta> {

    /* Nombre del propietario. */
    private StringProperty nombreDelPropietario;
    /* Número de tarjeta. */
    private StringProperty numeroDeTarjeta;
    /* Código de seguridad. */
    private IntegerProperty codigoDeSeguridad;
    /* Fecha de vencimiento. */
    private StringProperty fechaDeVencimiento;
    /* Saldo de la tarjeta. */
    private DoubleProperty saldo;

    /**
     * Define el estado inicial de una tarjeta.
     * 
     * @param nombreDelPropietario el nombre del propietario.
     * @param numeroDeTarjeta      el número de tarjeta.
     * @param codigoDeSeguridad    el código de seguridad.
     * @param fechaDeVencimiento   la fecha de vencimiento.
     * @param saldo                el saldo de la tarjeta.
     */
    public Tarjeta(String nombreDelPropietario,
            String numeroDeTarjeta,
            int codigoDeSeguridad,
            String fechaDeVencimiento,
            double saldo) {

        this.nombreDelPropietario = new SimpleStringProperty(nombreDelPropietario);
        this.numeroDeTarjeta = new SimpleStringProperty(numeroDeTarjeta);
        this.codigoDeSeguridad = new SimpleIntegerProperty(codigoDeSeguridad);
        this.fechaDeVencimiento = new SimpleStringProperty(fechaDeVencimiento);
        this.saldo = new SimpleDoubleProperty(saldo);
    }

    /**
     * Regresa el nombre del propietario.
     * 
     * @return el nombre del propietario.
     */
    public String getNombreDelPropietario() {
        return nombreDelPropietario.get();
    }

    /**
     * Define el nombre del propietario.
     * 
     * @param nombreDelPropietario el nuevo nombre del propietario.
     */
    public void setNombreDelPropietario(String nombreDelPropietario) {
        this.nombreDelPropietario.set(nombreDelPropietario);
    }

    /**
     * Regresa la propiedad del nombre del propietario.
     * 
     * @return la propiedad del nombre del propietario.
     */
    public StringProperty nombreDelPropietarioProperty() {
        return nombreDelPropietario;
    }

    /**
     * Regresa el número de tarjeta.
     * 
     * @return el número de tarjeta.
     */
    public String getNumeroDeTarjeta() {
        return numeroDeTarjeta.get();
    }

    /**
     * Define el número de tarjeta.
     * 
     * @param numeroDeTarjeta el nuevo número de tarjeta.
     */
    public void setNumeroDeTarjeta(String numeroDeTarjeta) {
        this.numeroDeTarjeta.set(numeroDeTarjeta);
    }

    /**
     * Regresa la propiedad del número de tarjeta.
     * 
     * @return la propiedad del número de tarjeta.
     */
    public StringProperty numeroDeTarjetaProperty() {
        return numeroDeTarjeta;
    }

    /**
     * Regresa el código de seguridad de la tarjeta.
     * 
     * @return el código de seguridad.
     */
    public int getCodigoDeSeguridad() {
        return codigoDeSeguridad.get();
    }

    /**
     * Define código de seguridad de la tarjeta.
     * 
     * @param codigoDeSeguridad el nuevo código de seguridad.
     */
    public void setCodigoDeSeguridad(int codigoDeSeguridad) {
        this.codigoDeSeguridad.set(codigoDeSeguridad);
    }

    /**
     * Regresa la propiedad del código de seguridad de la tarjeta.
     * 
     * @return la propiedad del código de seguridad de la tarjeta.
     */
    public IntegerProperty codigoDeSeguridadProperty() {
        return codigoDeSeguridad;
    }

    /**
     * Regresa la fecha de vencimiento de la tarjeta.
     * 
     * @return la fecha de vencimiento.
     */
    public String getFechaDeVencimiento() {
        return fechaDeVencimiento.get();
    }

    /**
     * Define la fecha de vencimiento de la tarjeta.
     * 
     * @param fechaDeVencimiento la nueva fecha de vencimiento.
     */
    public void setFechaDeVencimiento(String fechaDeVencimiento) {
        this.fechaDeVencimiento.set(fechaDeVencimiento);
    }

    /**
     * Regresa la propiedad de la FechaDeVencimiento.
     * 
     * @return la propiedad de la FechaDeVencimiento.
     */
    public StringProperty fechaDeVencimientoProperty() {
        return fechaDeVencimiento;
    }

    /**
     * Regresa el saldo de la tarjeta.
     * 
     * @return el saldo de la tarjeta.
     */
    public double getSaldo() {
        return saldo.get();
    }

    /**
     * Define el saldo de la tarjeta.
     * 
     * @param promedio el nuevo saldo de la tarjeta.
     */
    public void setSaldo(double saldo) {
        this.saldo.set(saldo);
    }

    /**
     * Regresa la propiedad del salso.
     * 
     * @return la propiedad del saldo.
     */
    public DoubleProperty saldoProperty() {
         
        return saldo;
    }

    /**
     * Regresa una representación en cadena de la tarjeta.
     * 
     * @return una representación en cadena de la tarjeta.
     */
    @Override
    public String toString() {

        String cadena = String.format("Nombre del propietario : %s\n" +
                "Número de tarjeta      : %s\n" +
                "Código de seguridad    : %03d\n" +
                "Fecha de vencimiento   : %s\n" +
                "Saldo                  : %2.2f",
                nombreDelPropietario.get(),
                numeroDeTarjeta.get(),
                codigoDeSeguridad.get(),
                fechaDeVencimiento.get(),
                saldo.get());
        return cadena;
    }

    /**
     * Nos dice si el objeto recibido es una tarjeta igual a la que manda llamar el
     * método.
     * 
     * @param objeto el objeto con el que la tarjeta se comparará.
     * @return <code>true</code> si el objeto recibido es una tarjeta con las
     *         mismas propiedades que el objeto que manda llamar al método,
     *         <code>false</code> en otro caso.
     */
    @Override
    public boolean equals(Object objeto) {

        if (!(objeto instanceof Tarjeta))
            return false;

        Tarjeta tNew = (Tarjeta) objeto;

        return (tNew.nombreDelPropietario.get().equals(nombreDelPropietario.get())
                && tNew.numeroDeTarjeta.get().equals(numeroDeTarjeta.get())
                && tNew.codigoDeSeguridad.get() == codigoDeSeguridad.get()
                && tNew.fechaDeVencimiento.get().equals(fechaDeVencimiento.get())
                && tNew.saldo.get() == saldo.get());
    }

    /**
     * Regresa la tarjeta serializada en una línea de texto. La línea de texto
     * que este método regresa debe ser aceptada por el método
     * {@link Tarjeta#deserializa}.
     * 
     * @return la serialización de la tarjeta en una línea de texto.
     */
    @Override
    public String serializa() {
        String linea = String.format("%s\t%s\t%d\t%s\t%2.2f\n",
                nombreDelPropietario.get(),
                numeroDeTarjeta.get(),
                codigoDeSeguridad.get(),
                fechaDeVencimiento.get(),
                saldo.get());
        return linea;
    }

    /**
     * Deserializa una línea de texto en las propiedades de la tarjeta. La
     * serialización producida por el método {@link Tarjeta#serializa} debe ser
     * aceptada por este método.
     * 
     * @param linea la línea a deserializar.
     * @throws ExcepcionLineaInvalida si la línea recibida es nula, vacía o no es
     *                                una serialización válida de una tarjeta.
     */
    @Override
    public void deserializa(String linea) {

        if (linea == null || linea.equals(""))
            throw new ExcepcionLineaInvalida();
        try {

            String[] Newliena = linea.split("\t");

            if (Newliena.length != 5)
                throw new ExcepcionLineaInvalida();

            nombreDelPropietario.set(Newliena[0].strip());
            numeroDeTarjeta.set(Newliena[1].strip());
            codigoDeSeguridad.set(Integer.parseInt(Newliena[2].strip()));
            fechaDeVencimiento.set(Newliena[3].strip());
            saldo.set(Double.parseDouble(Newliena[4].strip()));

        } catch (NumberFormatException e) {
            throw new ExcepcionLineaInvalida();
        }
    }

    /**
     * Actualiza los valores de la tarjeta con los de la tarjeta recibida.
     * 
     * @param t la tarjeta con la cual actualizar los valores.
     * @throws IllegalArgumentException si la tarjeta es <code>null</code>.
     */
    @Override
    public void actualiza(Tarjeta t) {

        if (t == null)
            throw new IllegalArgumentException();

        nombreDelPropietario.set(t.nombreDelPropietario.get());
        numeroDeTarjeta.set(t.numeroDeTarjeta.get());
        codigoDeSeguridad.set(t.codigoDeSeguridad.get());
        fechaDeVencimiento.set(t.fechaDeVencimiento.get());
        saldo.set(t.saldo.get());
    }

    /**
     * Nos dice si la tarjeta caza el valor dado en el campo especificado.
     * 
     * @param campo el campo que hay que cazar.
     * @param valor el valor con el que debe cazar el campo del registro.
     * @return <code>true</code> si:
     *         <ul>
     *         <li><code>campo</code> es {@link CampoTarjeta#NOMBRE_DEL_PROPIETARIO}
     *         y
     *         <code>valor</code> es instancia de {@link String} y es una subcadena
     *         del nombre del propietario de la tarjeta.</li>
     *         <li><code>campo</code> es {@link CampoTarjeta#NUMERO_DE_TARJETA} y
     *         <code>valor</code> es instancia de {@link String} y es una subcadena
     *         del número de tarjeta.</li>
     *         <li><code>campo</code> es {@link CampoTarjeta#CODIGO_DE_SEGURIDAD} y
     *         <code>valor</code> es instancia de {@link Integer} y su valor entero
     *         es menor o igual al del código de seguridad de la tarjeta.</li>
     *         </ul>
     *         <li><code>campo</code> es {@link CampoTarjeta#FECHA_DE_VENCIMIENTO} y
     *         <code>valor</code> es instancia de {@link String} y es una subcadena
     *         de la fecha de vencimiento de la tarjeta.</li>
     *         <li><code>campo</code> es {@link CampoTarjeta#SALDO} y
     *         <code>valor</code> es instancia de {@link Double} y su valor doble es
     *         menor o igual al saldo de la tarjeta.</li>
     *         <code>false</code> en otro caso.
     * @throws IllegalArgumentException si el campo no es instancia de
     *                                  {@link CampoTarjeta}.
     */
    @Override
    public boolean caza(CampoTarjeta campo, Object valor) {
        if (campo == null)
            throw new IllegalArgumentException();
        if (valor == null)
            return false;

        CampoTarjeta enumeracion = (CampoTarjeta) campo;

        switch (enumeracion) {

            case NOMBRE_DEL_PROPIETARIO:
                return (valor instanceof String
                        && !valor.equals("")
                        && this.nombreDelPropietario.get().contains((String) valor));
            case NUMERO_DE_TARJETA:
                return (valor instanceof String
                        && !valor.equals("")
                        && this.numeroDeTarjeta.get().contains((String) valor));
            case CODIGO_DE_SEGURIDAD:
                return (valor instanceof Integer && ((Integer) valor) <= this.codigoDeSeguridad.get());
            case FECHA_DE_VENCIMIENTO:
                return (valor instanceof String
                        && !valor.equals("")
                        && this.fechaDeVencimiento.get().contains((String) valor));
            case SALDO:
                return (valor instanceof Double && ((Double) valor) <= this.saldo.get());
            default:
                return false;
        }
    }
}
