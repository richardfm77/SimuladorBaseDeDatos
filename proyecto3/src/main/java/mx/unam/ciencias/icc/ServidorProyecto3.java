package mx.unam.ciencias.icc;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import mx.unam.ciencias.icc.red.ServidorBaseDeDatosTarjetas;

/**
 * ServidorProyecto3: Parte del servidor para el proyecto 3: Hilos de
 * ejecución y enchufes.
 */
public class ServidorProyecto3 {

    /* Imprime un mensaje de cómo usar el programa. */
    private static void uso() {
        System.out.println("Uso: ./bin/cliente-proyecto3 " +
                           "puerto [archivo]");
        System.exit(0);
    }

    /* Bitácora del servidor. */
    private static void bitacora(String formato, Object ... argumentos) {
        ZonedDateTime now = ZonedDateTime.now();
        String ts = now.format(DateTimeFormatter.RFC_1123_DATE_TIME);
        System.err.printf(ts + " " + formato + "\n", argumentos);
    }

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2)
            uso();

        int puerto = 1234;
        try {
            puerto = Integer.parseInt(args[0]);
        } catch (NumberFormatException nfe) {
            uso();
        }

        String archivo = (args.length == 2) ? archivo = args[1] : null;

        try {
            ServidorBaseDeDatosTarjetas servidor;
            servidor = new ServidorBaseDeDatosTarjetas(puerto, archivo);
            servidor.agregaEscucha((f, p) -> bitacora(f, p));
            servidor.sirve();
        } catch (IOException ioe) {
            bitacora("Error al crear el servidor.");
        }
    }
}
