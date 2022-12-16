package mx.unam.ciencias.icc.test;

import java.util.Random;
import mx.unam.ciencias.icc.CampoTarjeta;
import mx.unam.ciencias.icc.Tarjeta;
import mx.unam.ciencias.icc.ExcepcionLineaInvalida;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

/**
 * Clase para pruebas unitarias de la clase {@link Tarjeta}.
 */
public class TestTarjeta {

    /** Expiración para que ninguna prueba tarde más de 5 segundos. */
    @Rule
    public Timeout expiracion = Timeout.seconds(5);

    /* Nombres. */
    private static final String[] NOMBRES = {
            "José Arcadio", "Úrsula", "Aureliano", "Amaranta", "Rebeca",
            "Remedios", "Aureliano José", "Gerinaldo", "Mauricio", "Petra"
    };

    /* Apellidos. */
    private static final String[] APELLIDOS = {
            "Buendía", "Iguarán", "Cotes", "Ternera", "Moscote",
            "Brown", "Carpio", "Piedad", "Crespi", "Babilonia"
    };

    /* Generador de números aleatorios. */
    private static Random random;

    /**
     * Genera un nombre aleatorio.
     * 
     * @return un nombre aleatorio.
     */
    public static String nombreAleatorio() {
        int n = random.nextInt(NOMBRES.length);
        int ap = random.nextInt(APELLIDOS.length);
        int am = random.nextInt(APELLIDOS.length);
        return NOMBRES[n] + " " + APELLIDOS[ap] + " " + APELLIDOS[am];
    }

    /**
     * Genera un número de tarjeta aleatorio.
     * 
     * @return un número de tarjeta aleatorio.
     */
    public static String numeroDeTarjetaAleatorio() {
        return (1000000000 + random.nextInt(100000000)) + "000000";
    }

    /**
     * Genera un número de código de seguridad aleatorio.
     * 
     * @return un número de código de seguridad aleatorio.
     */
    public static int codigoDeSeguridadAleatorio() {
        return 100 + random.nextInt(100);
    }

    /**
     * Genera una fecha de vencimiento aleatoria.
     * 
     * @return una fecha de vencimiento aleatoria.
     */
    public static String fechaAleatoria() {

        int x1 = 1 + random.nextInt(11);
        int x2 = random.nextInt(99);
        return ((x1 < 10 ? ("0" + x1) : x1)
                + "/" +
                (1 + (x2 < 21 ? (x2 + 21) : x2)));
    }

    /**
     * Genera un saldo aleatorio.
     * 
     * @return un saldo aleatorio.
     */
    public static double saldoAleatorio() {
        return random.nextInt(200000) / 10.0;
    }

    /**
     * Genera una tarjeta aleatorio.
     * 
     * @return una tarejeta aleatoria.
     */
    public static Tarjeta tarjetaAleatoria() {
        return new Tarjeta(nombreAleatorio(),
                numeroDeTarjetaAleatorio(),
                codigoDeSeguridadAleatorio(),
                fechaAleatoria(),
                saldoAleatorio());
    }

    /**
     * Genera una tarjeta aleatoria con un número de tarjeta dado.
     * 
     * @param numeroDeTarjeta el número de tarjeta de la nueva tarjeta.
     * @return una tarjeta aleatoria.
     */
    public static Tarjeta tarjetaAleatoriaNumTarjeta(String numeroDeTarjeta) {
        return new Tarjeta(nombreAleatorio(),
                numeroDeTarjeta,
                codigoDeSeguridadAleatorio(),
                fechaAleatoria(),
                saldoAleatorio());
    }

    /* La tarjeta. */
    private Tarjeta tarjeta;

    /**
     * Prueba unitaria para {@link
     * Tarjeta#Tarjeta(String,String,int,String,double)}.
     */
    @Test
    public void testConstructor() {
        String nombre = nombreAleatorio();
        String num = numeroDeTarjetaAleatorio();
        int codigo = codigoDeSeguridadAleatorio();
        String fecha = fechaAleatoria();
        double saldo = saldoAleatorio();
        tarjeta = new Tarjeta(nombre, num, codigo, fecha, saldo);
        Assert.assertTrue(tarjeta.getNombreDelPropietario().equals(nombre));
        Assert.assertTrue(tarjeta.getNumeroDeTarjeta().equals(num));
        Assert.assertTrue(tarjeta.getCodigoDeSeguridad() == codigo);
        Assert.assertTrue(tarjeta.getFechaDeVencimiento().equals(fecha));
        Assert.assertTrue(tarjeta.getSaldo() == saldo);
    }

    /**
     * Prueba unitaria para {@link Tarjeta#getNombreDelPropietario}.
     */
    @Test
    public void testGetNombreDelPropietario() {
        String nombre = nombreAleatorio();
        String num = numeroDeTarjetaAleatorio();
        int codigo = codigoDeSeguridadAleatorio();
        String fecha = fechaAleatoria();
        double saldo = saldoAleatorio();
        tarjeta = new Tarjeta(nombre, num, codigo, fecha, saldo);
        Assert.assertTrue(tarjeta.getNombreDelPropietario().equals(nombre));
        Assert.assertFalse(tarjeta.getNombreDelPropietario().equals(nombre + " XyZ"));
    }

    /**
     * Prueba unitaria para {@link Tarjeta#setNombreDelPropietario}.
     */
    @Test
    public void testSetNombreDelPropietario() {
        String nombre = nombreAleatorio();
        String nuevoNombre = nombre + " XyZ";
        String num = numeroDeTarjetaAleatorio();
        int codigo = codigoDeSeguridadAleatorio();
        String fecha = fechaAleatoria();
        double saldo = saldoAleatorio();
        tarjeta = new Tarjeta(nombre, num, codigo, fecha, saldo);
        Assert.assertTrue(tarjeta.getNombreDelPropietario().equals(nombre));
        Assert.assertFalse(tarjeta.getNombreDelPropietario().equals(nuevoNombre));
        tarjeta.setNombreDelPropietario(nuevoNombre);
        Assert.assertFalse(tarjeta.getNombreDelPropietario().equals(nombre));
        Assert.assertTrue(tarjeta.getNombreDelPropietario().equals(nuevoNombre));
    }

    /**
     * Prueba unitaria para {@link Tarjeta#nombreDelPropietarioProperty}.
     */
    @Test
    public void testnombreDelPropietarioProperty() {
        String nombre = nombreAleatorio();
        String num = numeroDeTarjetaAleatorio();
        int codigo = codigoDeSeguridadAleatorio();
        String fecha = fechaAleatoria();
        double saldo = saldoAleatorio();
        tarjeta = new Tarjeta(nombre, num, codigo, fecha, saldo);
        Assert.assertTrue(tarjeta.nombreDelPropietarioProperty().get().equals(nombre));
    }

    /**
     * Prueba unitaria para {@link Tarjeta#getNumeroDeTarjeta}.
     */
    @Test public void testGetNumeroDeTarjeta() {
        String nombre = nombreAleatorio();
        String num = numeroDeTarjetaAleatorio();
        int codigo = codigoDeSeguridadAleatorio();
        String fecha = fechaAleatoria();
        double saldo = saldoAleatorio();
        tarjeta = new Tarjeta(nombre, num, codigo, fecha, saldo);
        Assert.assertTrue(tarjeta.getNumeroDeTarjeta().equals(num));
        Assert.assertFalse(tarjeta.getNumeroDeTarjeta().equals(num + "123"));
    }

    /**
     * Prueba unitaria para {@link Tarjeta#setNumeroDeTarjeta}.
     */
    @Test public void testSetNumeroDeTarjeta() {
        String nombre = nombreAleatorio();
        String num = numeroDeTarjetaAleatorio();
        String nuevoNum = num + "123";
        int codigo = codigoDeSeguridadAleatorio();
        String fecha = fechaAleatoria();
        double saldo = saldoAleatorio();
        tarjeta = new Tarjeta(nombre, num, codigo, fecha, saldo);
        Assert.assertTrue(tarjeta.getNumeroDeTarjeta().equals(num));
        Assert.assertFalse(tarjeta.getNumeroDeTarjeta().equals(nuevoNum));
        tarjeta.setNumeroDeTarjeta(nuevoNum);
        Assert.assertFalse(tarjeta.getNumeroDeTarjeta().equals(num));
        Assert.assertTrue(tarjeta.getNumeroDeTarjeta().equals(nuevoNum));
    }

    /**
     * Prueba unitaria para {@link Tarjeta#numeroDeTarjetaProperty}.
     */
    @Test
    public void testNumeroDeTarjetaProperty() {
        String nombre = nombreAleatorio();
        String num = numeroDeTarjetaAleatorio();
        int codigo = codigoDeSeguridadAleatorio();
        String fecha = fechaAleatoria();
        double saldo = saldoAleatorio();
        tarjeta = new Tarjeta(nombre, num, codigo, fecha, saldo);
        Assert.assertTrue(tarjeta.numeroDeTarjetaProperty().get().equals(num));
    }

    /**
     * Prueba unitaria para {@link Tarjeta#getCodigoDeSeguridad}.
     */
    @Test
    public void testGetCodigoDeSeguridad() {
        String nombre = nombreAleatorio();
        String num = numeroDeTarjetaAleatorio();
        int codigo = codigoDeSeguridadAleatorio();
        String fecha = fechaAleatoria();
        double saldo = saldoAleatorio();
        tarjeta = new Tarjeta(nombre, num, codigo, fecha, saldo);
        Assert.assertTrue(tarjeta.getCodigoDeSeguridad() == codigo);
        Assert.assertFalse(tarjeta.getCodigoDeSeguridad() == codigo + 1);
    }

    /**
     * Prueba unitaria para {@link Tarjeta#setCodigoDeSeguridad}.
     */
    @Test
    public void testSetCodigoDeSeguridad() {
        String nombre = nombreAleatorio();
        String num = numeroDeTarjetaAleatorio();
        int codigo = codigoDeSeguridadAleatorio();
        int nuevoCodigo = codigo + 1;
        String fecha = fechaAleatoria();
        double saldo = saldoAleatorio();
        tarjeta = new Tarjeta(nombre, num, codigo, fecha, saldo);
        Assert.assertTrue(tarjeta.getCodigoDeSeguridad() == codigo);
        Assert.assertFalse(tarjeta.getCodigoDeSeguridad() == nuevoCodigo);
        tarjeta.setCodigoDeSeguridad(nuevoCodigo);
        Assert.assertFalse(tarjeta.getCodigoDeSeguridad() == codigo);
        Assert.assertTrue(tarjeta.getCodigoDeSeguridad() == nuevoCodigo);
    }

    /**
     * Prueba unitaria para {@link Tarjeta#codigoDeSeguridadProperty}.
     */
    @Test
    public void testnombreCodigoDeSeguridadProperty() {
        String nombre = nombreAleatorio();
        String num = numeroDeTarjetaAleatorio();
        int codigo = codigoDeSeguridadAleatorio();
        String fecha = fechaAleatoria();
        double saldo = saldoAleatorio();
        tarjeta = new Tarjeta(nombre, num, codigo, fecha, saldo);
        Assert.assertTrue(tarjeta.codigoDeSeguridadProperty().get() == codigo);
    }

    /**
     * Prueba unitaria para {@link Tarjeta#getFechaDeVencimiento}.
     */
    @Test
    public void testGetFechaDeVencimiento() {
        String nombre = nombreAleatorio();
        String num = numeroDeTarjetaAleatorio();
        int codigo = codigoDeSeguridadAleatorio();
        String fecha = fechaAleatoria();
        double saldo = saldoAleatorio();
        tarjeta = new Tarjeta(nombre, num, codigo, fecha, saldo);
        Assert.assertTrue(tarjeta.getFechaDeVencimiento().equals(fecha));
        Assert.assertFalse(tarjeta.getFechaDeVencimiento().equals(fecha + " XyZ"));
    }

    /**
     * Prueba unitaria para {@link Tarjeta#setFechaDeVencimiento}.
     */
    @Test
    public void testSetFechaDeVencimiento() {
        String nombre = nombreAleatorio();
        String num = numeroDeTarjetaAleatorio();
        int codigo = codigoDeSeguridadAleatorio();
        String fecha = fechaAleatoria();
        String nuevaFecha = fecha + " XyZ";
        double saldo = saldoAleatorio();
        tarjeta = new Tarjeta(nombre, num, codigo, fecha, saldo);
        Assert.assertTrue(tarjeta.getFechaDeVencimiento().equals(fecha));
        Assert.assertFalse(tarjeta.getFechaDeVencimiento().equals(nuevaFecha));
        tarjeta.setFechaDeVencimiento(nuevaFecha);
        Assert.assertFalse(tarjeta.getFechaDeVencimiento().equals(fecha));
        Assert.assertTrue(tarjeta.getFechaDeVencimiento().equals(nuevaFecha));
    }

    /**
     * Prueba unitaria para {@link Tarjeta#fechaDeVencimientoProperty}.
     */
    @Test
    public void testFechaDeVencimientoProperty() {
        String nombre = nombreAleatorio();
        String num = numeroDeTarjetaAleatorio();
        int codigo = codigoDeSeguridadAleatorio();
        String fecha = fechaAleatoria();
        double saldo = saldoAleatorio();
        tarjeta = new Tarjeta(nombre, num, codigo, fecha, saldo);
        Assert.assertTrue(tarjeta.fechaDeVencimientoProperty().get().equals(fecha));
    }    

    /**
     * Prueba unitaria para {@link Tarjeta#getSaldo}.
     */
    @Test
    public void testGetSaldo() {
        String nombre = nombreAleatorio();
        String num = numeroDeTarjetaAleatorio();
        int codigo = codigoDeSeguridadAleatorio();
        String fecha = fechaAleatoria();
        double saldo = saldoAleatorio();
        tarjeta = new Tarjeta(nombre, num, codigo, fecha, saldo);
        Assert.assertTrue(tarjeta.getSaldo() == saldo);
        Assert.assertFalse(tarjeta.getSaldo() == saldo + 50);
    }

    /**
     * Prueba unitaria para {@link Tarjeta#setSaldo}.
     */
    @Test
    public void testSetSaldo() {
        String nombre = nombreAleatorio();
        String num = numeroDeTarjetaAleatorio();
        int codigo = codigoDeSeguridadAleatorio();
        String fecha = fechaAleatoria();
        double saldo = saldoAleatorio();
        double nuevoSaldo = saldo + 80.0;
        tarjeta = new Tarjeta(nombre, num, codigo, fecha, saldo);
        Assert.assertTrue(tarjeta.getSaldo() == saldo);
        Assert.assertFalse(tarjeta.getSaldo() == nuevoSaldo);
        tarjeta.setSaldo(nuevoSaldo);
        Assert.assertFalse(tarjeta.getSaldo() == saldo);
        Assert.assertTrue(tarjeta.getSaldo() == nuevoSaldo);
    }

    /**
     * Prueba unitaria para {@link Tarjeta#saldoProperty}.
     */
    @Test
    public void testSaldoProperty() {
        String nombre = nombreAleatorio();
        String num = numeroDeTarjetaAleatorio();
        int codigo = codigoDeSeguridadAleatorio();
        String fecha = fechaAleatoria();
        double saldo = saldoAleatorio();
        tarjeta = new Tarjeta(nombre, num, codigo, fecha, saldo);
        Assert.assertTrue(tarjeta.saldoProperty().get() == saldo);
    }

    /**
     * Prueba unitaria para {@link Tarjeta#toString}.
     */
    @Test public void testToString() {
        String nombre = nombreAleatorio();
        String num = numeroDeTarjetaAleatorio();
        int codigo = codigoDeSeguridadAleatorio();
        String fecha = fechaAleatoria();
        double saldo = saldoAleatorio();
        tarjeta = new Tarjeta(nombre, num, codigo, fecha, saldo);
        String cadena = String.format("Nombre del propietario : %s\n" +
                                      "Número de tarjeta      : %s\n" +
                                      "Código de seguridad    : %03d\n" +
                                      "Fecha de vencimiento   : %s\n" +
                                      "Saldo                  : %2.2f",
                                      nombre, num, codigo, fecha, saldo);
        Assert.assertTrue(tarjeta.toString().equals(cadena));
        codigo = 213;
        saldo = 80.99;
        tarjeta.setCodigoDeSeguridad(codigo);
        tarjeta.setSaldo(saldo);
        cadena = String.format("Nombre del propietario : %s\n" +
                                      "Número de tarjeta      : %s\n" +
                                      "Código de seguridad    : %03d\n" +
                                      "Fecha de vencimiento   : %s\n" +
                                      "Saldo                  : %2.2f",
                                      nombre, num, codigo, fecha, saldo);
        Assert.assertTrue(tarjeta.toString().equals(cadena));
    }

    /**
     * Prueba unitaria para {@link Tarjeta#equals}.
     */
    @Test public void testEquals() {
        String nombre = nombreAleatorio();
        String num = numeroDeTarjetaAleatorio();
        int codigo = codigoDeSeguridadAleatorio();
        String fecha = fechaAleatoria();
        double saldo = saldoAleatorio();
        tarjeta = new Tarjeta(nombre, num, codigo, fecha, saldo);
        Tarjeta igual = new Tarjeta(new String(nombre),
                                            new String(num), codigo
                                            , new String(fecha), saldo);
        Assert.assertTrue(tarjeta.equals(igual));
        String otroNombre = nombre + " Segundo";
        String otroNum = num + " 800";
        int otroCodigo = codigo + 1;
        String otraFecha = fecha + " 800";
        double otroSaldo = saldo + 80;
    
        Tarjeta distinto =
            new Tarjeta(otroNombre, otroNum, otroCodigo, otraFecha, otroSaldo);
        Assert.assertFalse(tarjeta.equals(distinto));
        distinto = new Tarjeta(nombre, otroNum, otroCodigo, fecha, saldo);
        Assert.assertFalse(tarjeta.equals(distinto));
        distinto = new Tarjeta(nombre, num, codigo, otraFecha, saldo);
        Assert.assertFalse(tarjeta.equals(distinto));
        distinto = new Tarjeta(otroNombre, num, codigo, fecha, saldo);
        Assert.assertFalse(tarjeta.equals(distinto));
        distinto = new Tarjeta(otroNombre, otroNum,
                                otroCodigo, otraFecha, otroSaldo);
        Assert.assertFalse(tarjeta.equals(distinto));
        Assert.assertFalse(tarjeta.equals("Una cadena"));
        Assert.assertFalse(tarjeta.equals(null));
    }

    /**
     * Prueba unitaria para {@link Tarjeta#serializa}.
     */
    @Test public void testSerializa() {
        String nombre = nombreAleatorio();
        String num = numeroDeTarjetaAleatorio();
        int codigo = codigoDeSeguridadAleatorio();
        String fecha = fechaAleatoria();
        double saldo = saldoAleatorio();
        tarjeta = new Tarjeta(nombre, num, codigo, fecha, saldo);
        String linea = String.format("%s\t%s\t%d\t%s\t%2.2f\n",
                                     nombre, num, codigo, fecha, saldo);
        Assert.assertTrue(tarjeta.serializa().equals(linea));
    }

    /**
     * Prueba unitaria para {@link Tarjeta#deserializa}.
     */
    @Test public void testDeserializa() {
        tarjeta = new Tarjeta(null, null, 0, null, 0);

        String nombre = nombreAleatorio();
        String num = numeroDeTarjetaAleatorio();
        int codigo = codigoDeSeguridadAleatorio();
        String fecha = fechaAleatoria();
        double saldo = saldoAleatorio();

        String linea = String.format("%s\t%s\t%d\t%s\t%2.2f\n",
                                     nombre, num, codigo, fecha, saldo);

        try {
            tarjeta.deserializa(linea);
        } catch (ExcepcionLineaInvalida eli) {
            Assert.fail();
        }

        Assert.assertTrue(tarjeta.getNombreDelPropietario().equals(nombre));
        Assert.assertTrue(tarjeta.getNumeroDeTarjeta().equals(num));
        Assert.assertTrue(tarjeta.getCodigoDeSeguridad() == codigo);
        Assert.assertTrue(tarjeta.getFechaDeVencimiento().equals(fecha));
        Assert.assertTrue(tarjeta.getSaldo() == saldo);

        String[] invalidas = {"", " ", "\t", "  ", "\t\t",
                              " \t", "\t ", "\n", "a\ta\ta",
                              "a\ta\ta\ta"};

        for (int i = 0; i < invalidas.length; i++) {
            linea = invalidas[i];
            try {
                tarjeta.deserializa(linea);
                Assert.fail();
            } catch (ExcepcionLineaInvalida eli) {}
        }
    }

    /**
     * Prueba unitaria para {@link Tarjeta#actualiza}.
     */
    @Test
    public void testActualiza() {
        tarjeta = new Tarjeta("A", "B", 1,"C", 1.0);
        Tarjeta t = new Tarjeta("D", "E", 2, "F", 2.0);
        Assert.assertFalse(tarjeta == t);
        Assert.assertFalse(tarjeta.equals(t));
        tarjeta.actualiza(t);
        Assert.assertFalse(tarjeta == t);
        Assert.assertTrue(tarjeta.equals(t));
        Assert.assertTrue(tarjeta.getNombreDelPropietario().equals("D"));
        Assert.assertFalse(tarjeta.nombreDelPropietarioProperty() == t.nombreDelPropietarioProperty());
        Assert.assertFalse(tarjeta.numeroDeTarjetaProperty() == t.numeroDeTarjetaProperty());
        Assert.assertFalse(tarjeta.codigoDeSeguridadProperty() == t.codigoDeSeguridadProperty());
        Assert.assertFalse(tarjeta.fechaDeVencimientoProperty() == t.fechaDeVencimientoProperty());
        Assert.assertFalse(tarjeta.saldoProperty() == t.saldoProperty());
        try {
            tarjeta.actualiza(null);
            Assert.fail();
        } catch (IllegalArgumentException iae) {
        }
    }

    /**
     * Prueba unitaria para {@link Tarjeta#caza}.
     */
    @Test public void testCaza() {
        String nombre = nombreAleatorio();
        String num = numeroDeTarjetaAleatorio();
        int codigo = codigoDeSeguridadAleatorio();
        String fecha = fechaAleatoria();
        double saldo = saldoAleatorio();
        tarjeta = new Tarjeta(nombre, num, codigo, fecha, saldo);
        String n;
        int m;

        n = tarjeta.getNombreDelPropietario();
        m = tarjeta.getNombreDelPropietario().length();
        Assert.assertTrue(tarjeta.caza(CampoTarjeta.NOMBRE_DEL_PROPIETARIO, n));
        n = tarjeta.getNombreDelPropietario().substring(0, m/2);
        Assert.assertTrue(tarjeta.caza(CampoTarjeta.NOMBRE_DEL_PROPIETARIO, n));
        n = tarjeta.getNombreDelPropietario().substring(m/2, m);
        Assert.assertTrue(tarjeta.caza(CampoTarjeta.NOMBRE_DEL_PROPIETARIO, n));
        n = tarjeta.getNombreDelPropietario().substring(m/3, 2*(m/3));
        Assert.assertTrue(tarjeta.caza(CampoTarjeta.NOMBRE_DEL_PROPIETARIO, n));
        Assert.assertFalse(tarjeta.caza(CampoTarjeta.NOMBRE_DEL_PROPIETARIO, ""));
        Assert.assertFalse(tarjeta.caza(CampoTarjeta.NOMBRE_DEL_PROPIETARIO, "XXX"));
        Assert.assertFalse(tarjeta.caza(CampoTarjeta.NOMBRE_DEL_PROPIETARIO,
                                           Integer.valueOf(1000)));
        Assert.assertFalse(tarjeta.caza(CampoTarjeta.NOMBRE_DEL_PROPIETARIO, null));
        
        
        n = tarjeta.getNumeroDeTarjeta();
        m = tarjeta.getNumeroDeTarjeta().length();
        Assert.assertTrue(tarjeta.caza(CampoTarjeta.NUMERO_DE_TARJETA, n));
        n = tarjeta.getNumeroDeTarjeta().substring(0, m/2);
        Assert.assertTrue(tarjeta.caza(CampoTarjeta.NUMERO_DE_TARJETA, n));
        n = tarjeta.getNumeroDeTarjeta().substring(m/2, m);
        Assert.assertTrue(tarjeta.caza(CampoTarjeta.NUMERO_DE_TARJETA, n));
        n = tarjeta.getNumeroDeTarjeta().substring(m/3, 2*(m/3));
        Assert.assertTrue(tarjeta.caza(CampoTarjeta.NUMERO_DE_TARJETA, n));
        Assert.assertFalse(tarjeta.caza(CampoTarjeta.NUMERO_DE_TARJETA, ""));
        Assert.assertFalse(tarjeta.caza(CampoTarjeta.NUMERO_DE_TARJETA, "XXX"));
        Assert.assertFalse(tarjeta.caza(CampoTarjeta.NUMERO_DE_TARJETA,
                                           Integer.valueOf(1000)));
        Assert.assertFalse(tarjeta.caza(CampoTarjeta.NUMERO_DE_TARJETA, null));

        Integer c = Integer.valueOf(tarjeta.getCodigoDeSeguridad());
        Assert.assertTrue(tarjeta.caza(CampoTarjeta.CODIGO_DE_SEGURIDAD, c));
        c = Integer.valueOf(tarjeta.getCodigoDeSeguridad() - 1);
        Assert.assertTrue(tarjeta.caza(CampoTarjeta.CODIGO_DE_SEGURIDAD, c));
        c = Integer.valueOf(tarjeta.getCodigoDeSeguridad() + 1);
        Assert.assertFalse(tarjeta.caza(CampoTarjeta.CODIGO_DE_SEGURIDAD, c));
        Assert.assertFalse(tarjeta.caza(CampoTarjeta.CODIGO_DE_SEGURIDAD, "XXX"));
        Assert.assertFalse(tarjeta.caza(CampoTarjeta.CODIGO_DE_SEGURIDAD, null));

        n = tarjeta.getFechaDeVencimiento();
        m = tarjeta.getFechaDeVencimiento().length();
        Assert.assertTrue(tarjeta.caza(CampoTarjeta.FECHA_DE_VENCIMIENTO, n));
        n = tarjeta.getFechaDeVencimiento().substring(0, m/2);
        Assert.assertTrue(tarjeta.caza(CampoTarjeta.FECHA_DE_VENCIMIENTO, n));
        n = tarjeta.getFechaDeVencimiento().substring(m/2, m);
        Assert.assertTrue(tarjeta.caza(CampoTarjeta.FECHA_DE_VENCIMIENTO, n));
        n = tarjeta.getFechaDeVencimiento().substring(m/3, 2*(m/3));
        Assert.assertTrue(tarjeta.caza(CampoTarjeta.FECHA_DE_VENCIMIENTO, n));
        Assert.assertFalse(tarjeta.caza(CampoTarjeta.FECHA_DE_VENCIMIENTO, ""));
        Assert.assertFalse(tarjeta.caza(CampoTarjeta.FECHA_DE_VENCIMIENTO, "XXX"));
        Assert.assertFalse(tarjeta.caza(CampoTarjeta.FECHA_DE_VENCIMIENTO,
                                           Integer.valueOf(1000)));
        Assert.assertFalse(tarjeta.caza(CampoTarjeta.FECHA_DE_VENCIMIENTO, null));

        Double p = Double.valueOf(tarjeta.getSaldo());
        Assert.assertTrue(tarjeta.caza(CampoTarjeta.SALDO, p));
        p = Double.valueOf(tarjeta.getSaldo() - 5.0);
        Assert.assertTrue(tarjeta.caza(CampoTarjeta.SALDO, p));
        p = Double.valueOf(tarjeta.getSaldo() + 5.0);
        Assert.assertFalse(tarjeta.caza(CampoTarjeta.SALDO, p));
        Assert.assertFalse(tarjeta.caza(CampoTarjeta.SALDO, "XXX"));
        Assert.assertFalse(tarjeta.caza(CampoTarjeta.SALDO, null));

        try {
            tarjeta.caza(null, null);
            Assert.fail();
        } catch (IllegalArgumentException iae) {}
    }

    /* Inicializa el generador de números aleatorios. */
    static {
        random = new Random();
    }
}
