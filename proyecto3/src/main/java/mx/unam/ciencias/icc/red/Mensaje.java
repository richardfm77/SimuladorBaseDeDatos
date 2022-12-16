package mx.unam.ciencias.icc.red;

/**
 * Enumeración para los mensajes que se transmiten en una conexión entre el
 * servidor y el cliente de bases de datos.
 */
public enum Mensaje {

    /**
     * Si el servidor recibe este mensaje, contesta enviando toda la base de
     * datos. Si el cliente recibe este mensaje, entonces comienza a cargar la
     * base de datos.
     */
    BASE_DE_DATOS,

    /**
     * El interlocutor agregó un registro. Si este mensaje es recibido por el
     * servidor o el cliente, inmediatamente después recibirá un registro que
     * debe agregar a la base de datos.
     */
    REGISTRO_AGREGADO,

    /**
     * El interlocutor eliminó un registro. Si este mensaje es recibido por el
     * servidor o el cliente, inmediatamente después recibirá un registro que
     * debe eliminar de la base de datos.
     */
    REGISTRO_ELIMINADO,

    /**
     * El interlocutor modificó un registro. Si este mensaje es recibido por el
     * servidor o el cliente, inmediatamente después recibirá dos registros: el
     * primero será el registro original, y el segundo el registro modificado.
     */
    REGISTRO_MODIFICADO,

    /**
     * El interlocutor solicita una desconexión limpia. Tanto el servidor como
     * cliente cierran la conexión correspondiente.
     */
    DESCONECTAR,

    /**
     * El servidor debe detenerse, desconectando a todos los clientes que
     * pudieran estar conectados. Los clientes ignoran este mensaje.
     */
    DETENER_SERVICIO,

    /**
     * Mensaje de eco. El servidor debe regresar el mensaje de eco cuando reciba
     * el mensaje de eco; el cliente debe ignorarlo. Es necesario para poder
     * probar el servidor.
     */
    ECO,

    /**
     * El mensaje no es reconocido.
     */
    INVALIDO;

    /* El prefijo para mensajes. */
    private static final String PREFIJO = "|=MENSAJE:";

    /**
     * Descifra un cadena recibida y la traduce a un mensaje.
     * 
     * @param mensaje la cadena de texto con el mensaje. La cadena recibida debe
     *                comenzar con "|=MENSAJE:", seguido del mensaje, de otra forma
     *                se le
     *                considerará inválido.
     * @return el mensaje correspondiente a la línea.
     */
    public static Mensaje getMensaje(String mensaje) {
         
        if (!mensaje.contains(PREFIJO))
            return INVALIDO;
        String[] array = mensaje.split(":");

        switch (array[1]) {
            case "BASE_DE_DATOS":
                return BASE_DE_DATOS;
            case "REGISTRO_AGREGADO":
                return REGISTRO_AGREGADO;
            case "REGISTRO_ELIMINADO":
                return REGISTRO_ELIMINADO;
            case "REGISTRO_MODIFICADO":
                return REGISTRO_MODIFICADO;
            case "DESCONECTAR":
                return DESCONECTAR;
            case "DETENER_SERVICIO":
                return DETENER_SERVICIO;
            case "ECO":
                return ECO;
            default:
                return INVALIDO;
        }
    }

    /**
     * Genera una cadena con un mensaje válido para {@link getMensaje}.
     * 
     * @return la cadena con el mensaje correspondiente al mensaje.
     */
    public String toString() {
         
        String cadenaFinal = PREFIJO;
        switch (this) {
            case BASE_DE_DATOS:
                cadenaFinal += "BASE_DE_DATOS";
                break;
            case REGISTRO_AGREGADO:
                cadenaFinal += "REGISTRO_AGREGADO";
                break;
            case REGISTRO_ELIMINADO:
                cadenaFinal += "REGISTRO_ELIMINADO";
                break;
            case REGISTRO_MODIFICADO:
                cadenaFinal += "REGISTRO_MODIFICADO";
                break;
            case DESCONECTAR:
                cadenaFinal += "DESCONECTAR";
                break;
            case DETENER_SERVICIO:
                cadenaFinal += "DETENER_SERVICIO";
                break;
            case ECO:
                cadenaFinal += "ECO";
                break;
            case INVALIDO:
                cadenaFinal += "INVALIDO";
                break;
        }
        return cadenaFinal;
    }
}
