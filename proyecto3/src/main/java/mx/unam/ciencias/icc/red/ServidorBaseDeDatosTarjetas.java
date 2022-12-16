package mx.unam.ciencias.icc.red;

import java.io.IOException;
import mx.unam.ciencias.icc.BaseDeDatos;
import mx.unam.ciencias.icc.BaseDeDatosTarjetas;
import mx.unam.ciencias.icc.CampoTarjeta;
import mx.unam.ciencias.icc.Tarjeta;

/**
 * Clase para servidores de bases de datos de tarjetas bancarias de débito.
 */
public class ServidorBaseDeDatosTarjetas
    extends ServidorBaseDeDatos<Tarjeta> {

    /**
     * Construye un servidor de base de datos de tarjetas.
     * @param puerto el puerto dónde escuchar por conexiones.
     * @param archivo el archivo en el disco del cual cargar/guardar la base de
     *                datos.
     * @throws IOException si ocurre un error de entrada o salida.
     */
    public ServidorBaseDeDatosTarjetas(int puerto, String archivo)
        throws IOException {
         
        super(puerto, archivo);
    }

    /**
     * Crea una base de datos de tarjetas.
     * @return una base de datos de tarjetas.
     */
    @Override public
    BaseDeDatos<Tarjeta, CampoTarjeta> creaBaseDeDatos() {
         
        return new BaseDeDatosTarjetas();
    }
}
