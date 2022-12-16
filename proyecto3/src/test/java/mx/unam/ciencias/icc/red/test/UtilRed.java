package mx.unam.ciencias.icc.red.test;

import mx.unam.ciencias.icc.BaseDeDatosTarjetas;
import mx.unam.ciencias.icc.Tarjeta;
import mx.unam.ciencias.icc.test.TestTarjeta;

/**
 * Clase con métodos estáticos utilitarios para las pruebas unitarias de red.
 */
public class UtilRed {

    /* Evitamos instanciación. */
    private UtilRed() {}

    /* Número de cuenta inicial. */
    public static final int NUM_INICIAL = 1000000;

    /* Contador de números de cuenta. */
    private static int contador;

    /**
     * Espera el número de milisegundos de forma segura.
     * @param milisegundos el número de milisegundos a esperar.
     */
    public static void espera(int milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException ie) {}
    }

    /**
     * Llena una base de datos de tarjetas.
     * @param bdd la base de datos a llenar.
     * @param total el total de tarjetas.
     */
    public static void llenaBaseDeDatos(BaseDeDatosTarjetas bdd, int total) {
        for (int i = 0; i < total; i++) {
            int c = NUM_INICIAL + i;
            bdd.agregaRegistro(TestTarjeta.tarjetaAleatoriaNumTarjeta("" + c));
        }
    }

    /**
     * Crea una tarejeta aleatoria.
     * @param total el total de tarjetas.
     * @return una tarjeta aleatorio con un número de tarjeta único.
     */
    public static Tarjeta tarjetaAleatoria(int total) {
        int nc = NUM_INICIAL + total*2 + contador++;
        return TestTarjeta.tarjetaAleatoriaNumTarjeta("" + nc);
    }

}
