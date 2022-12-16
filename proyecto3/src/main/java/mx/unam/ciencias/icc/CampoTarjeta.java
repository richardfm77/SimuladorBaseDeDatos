package mx.unam.ciencias.icc;

/**
 * Enumeración para los campos de una {@link Tarjeta}.
 */

public enum CampoTarjeta {

    /** El nombre del propietario. */
    NOMBRE_DEL_PROPIETARIO,
    /** El número de tarjeta. */
    NUMERO_DE_TARJETA,
    /** El código de seguridad. */
    CODIGO_DE_SEGURIDAD,
    /** La fecha de vencimiento. */
    FECHA_DE_VENCIMIENTO,
    /** El saldo de la tarjeta. */
    SALDO;

    /**
     * Regresa una representación en cadena del campo para ser usada en
     * interfaces gráficas.
     * @return una representación en cadena del campo.
     */
    @Override public String toString() {
        switch(this){
            case NOMBRE_DEL_PROPIETARIO:
                return "Nombre del propietario";
            case NUMERO_DE_TARJETA: 
                return "# Tarjeta";
            case CODIGO_DE_SEGURIDAD:
                return "Codigo de seguridad";
            case FECHA_DE_VENCIMIENTO: 
                return "Fecha de vencimiento";    
            case SALDO: 
                return "Saldo";    
            default : 
                return "";
        }
        
    }
}
