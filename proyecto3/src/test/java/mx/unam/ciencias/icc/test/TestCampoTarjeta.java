package mx.unam.ciencias.icc.test;

import mx.unam.ciencias.icc.CampoTarjeta;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

/**
 * Clase para pruebas unitarias de la enumeración {@link CampoTarjeta}.
 */
public class TestCampoTarjeta {

    /** Expiración para que ninguna prueba tarde más de 5 segundos. */
    @Rule public Timeout expiracion = Timeout.seconds(5);

    /**
     * Prueba unitaria para {@link CampoTarjeta#toString}.
     */
    @Test public void testToString() {
        String s = CampoTarjeta.NOMBRE_DEL_PROPIETARIO.toString();
        Assert.assertTrue(s.equals("Nombre del propietario"));
        s = CampoTarjeta.NUMERO_DE_TARJETA.toString();
        Assert.assertTrue(s.equals("# Tarjeta"));
        s = CampoTarjeta.CODIGO_DE_SEGURIDAD.toString();
        Assert.assertTrue(s.equals("Codigo de seguridad"));
        s = CampoTarjeta.FECHA_DE_VENCIMIENTO.toString();
        Assert.assertTrue(s.equals("Fecha de vencimiento"));
        s = CampoTarjeta.SALDO.toString();
        Assert.assertTrue(s.equals("Saldo"));
    }
}
