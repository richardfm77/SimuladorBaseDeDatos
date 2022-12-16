package mx.unam.ciencias.icc;

/**
 * Clase para bases de datos de tarjetas.
 */
public class BaseDeDatosTarjetas
        extends BaseDeDatos<Tarjeta, CampoTarjeta> {

    /**
     * Crea una tarjeta en blanco.
     * 
     * @return un tarjeta en blanco.
     */
    @Override
    public Tarjeta creaRegistro() {
        return new Tarjeta(null, null, 0, null, 0);
    }
}
