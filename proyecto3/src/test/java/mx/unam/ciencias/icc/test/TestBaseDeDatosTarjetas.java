package mx.unam.ciencias.icc.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Random;
import mx.unam.ciencias.icc.BaseDeDatos;
import mx.unam.ciencias.icc.BaseDeDatosTarjetas;
import mx.unam.ciencias.icc.CampoTarjeta;
import mx.unam.ciencias.icc.Tarjeta;
import mx.unam.ciencias.icc.EscuchaBaseDeDatos;
import mx.unam.ciencias.icc.EventoBaseDeDatos;
import mx.unam.ciencias.icc.Lista;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

/**
 * Clase para pruebas unitarias de la clase {@link BaseDeDatosTarjetas}.
 */
public class TestBaseDeDatosTarjetas {

    /** Expiración para que ninguna prueba tarde más de 5 segundos. */
    @Rule
    public Timeout expiracion = Timeout.seconds(5);

    /* Generador de números aleatorios. */
    private Random random;
    /* Base de datos de tarjetas. */
    private BaseDeDatosTarjetas bdd;
    /* Número total de tarjetas. */
    private int total;

    /**
     * Crea un generador de números aleatorios para cada prueba y una base de
     * datos de tarjetas.
     */
    public TestBaseDeDatosTarjetas() {
        random = new Random();
        bdd = new BaseDeDatosTarjetas();
        total = 1 + random.nextInt(100);
    }

    /**
     * Prueba unitaria para {@link
     * BaseDeDatosTarjetas#BaseDeDatosTarjetas}.
     */
    @Test
    public void testConstructor() {
        Lista<Tarjeta> tarjetas = bdd.getRegistros();
        Assert.assertFalse(tarjetas == null);
        Assert.assertTrue(tarjetas.getLongitud() == 0);
        Assert.assertTrue(bdd.getNumRegistros() == 0);
        bdd.agregaEscucha((e, r1, r2) -> {
        });
    }

    /**
     * Prueba unitaria para {@link BaseDeDatos#getNumRegistros}.
     */
    @Test
    public void testGetNumRegistros() {
        Assert.assertTrue(bdd.getNumRegistros() == 0);
        for (int i = 0; i < total; i++) {
            Tarjeta t = TestTarjeta.tarjetaAleatoria();
            bdd.agregaRegistro(t);
            Assert.assertTrue(bdd.getNumRegistros() == i + 1);
        }
        Assert.assertTrue(bdd.getNumRegistros() == total);
    }

    /**
     * Prueba unitaria para {@link BaseDeDatos#getRegistros}.
     */
    @Test
    public void testGetRegistros() {
        Lista<Tarjeta> l = bdd.getRegistros();
        Lista<Tarjeta> r = bdd.getRegistros();
        Assert.assertTrue(l.equals(r));
        Assert.assertFalse(l == r);
        Tarjeta[] tarjetas = new Tarjeta[total];
        for (int i = 0; i < total; i++) {
            tarjetas[i] = TestTarjeta.tarjetaAleatoria();
            bdd.agregaRegistro(tarjetas[i]);
        }
        l = bdd.getRegistros();
        int c = 0;
        for (Tarjeta e : l)
            Assert.assertTrue(tarjetas[c++].equals(e));
        l.elimina(tarjetas[0]);
        Assert.assertFalse(l.equals(bdd.getRegistros()));
        Assert.assertFalse(l.getLongitud() == bdd.getNumRegistros());
    }

    /**
     * Prueba unitaria para {@link BaseDeDatos#agregaRegistro}.
     */
    @Test
    public void testAgregaRegistro() {
        for (int i = 0; i < total; i++) {
            Tarjeta e = TestTarjeta.tarjetaAleatoria();
            Assert.assertFalse(bdd.getRegistros().contiene(e));
            bdd.agregaRegistro(e);
            Assert.assertTrue(bdd.getRegistros().contiene(e));
            Lista<Tarjeta> l = bdd.getRegistros();
            Assert.assertTrue(l.get(l.getLongitud() - 1).equals(e));
        }
        boolean[] llamado = { false };
        bdd.agregaEscucha((e, r1, r2) -> {
            Assert.assertTrue(e == EventoBaseDeDatos.REGISTRO_AGREGADO);
            Assert.assertTrue(r1.equals(new Tarjeta("A", "B", 1, "C", 1)));
            Assert.assertTrue(r2 == null);
            llamado[0] = true;
        });
        bdd.agregaRegistro(new Tarjeta("A", "B", 1, "C", 1));
        Assert.assertTrue(llamado[0]);
    }

    /**
     * Prueba unitaria para {@link BaseDeDatos#eliminaRegistro}.
     */
    @Test
    public void testEliminaRegistro() {
        int ini = random.nextInt(1000000);
        for (int i = 0; i < total; i++) {
            Tarjeta e = TestTarjeta.tarjetaAleatoriaNumTarjeta("" + ini + i);
            bdd.agregaRegistro(e);
        }
        while (bdd.getNumRegistros() > 0) {
            int i = random.nextInt(bdd.getNumRegistros());
            Tarjeta t = bdd.getRegistros().get(i);
            Assert.assertTrue(bdd.getRegistros().contiene(t));
            bdd.eliminaRegistro(t);
            Assert.assertFalse(bdd.getRegistros().contiene(t));
        }
        boolean[] llamado = { false };
        Tarjeta tarjeta = new Tarjeta("A", "B", 1, "C", 1);
        bdd.agregaRegistro(tarjeta);
        bdd.agregaEscucha((e, r1, r2) -> {
            Assert.assertTrue(e == EventoBaseDeDatos.REGISTRO_ELIMINADO);
            Assert.assertTrue(r1.equals(new Tarjeta("A", "B", 1, "C", 1)));
            Assert.assertTrue(r2 == null);
            llamado[0] = true;
        });
        bdd.eliminaRegistro(tarjeta);
        Assert.assertTrue(llamado[0]);
        bdd = new BaseDeDatosTarjetas();
        llamado[0] = false;
        bdd.agregaEscucha((e, r1, r2) -> {
            Assert.assertTrue(e == EventoBaseDeDatos.REGISTRO_ELIMINADO);
            Assert.assertTrue(r1.equals(new Tarjeta("A", "B", 1, "C", 1)));
            Assert.assertTrue(r2 == null);
            llamado[0] = true;
        });
        bdd.eliminaRegistro(tarjeta);
        Assert.assertTrue(llamado[0]);
    }

    /**
     * Prueba unitaria para {@link BaseDeDatos#modificaRegistro}.
     */
    @Test
    public void testModificaRegistro() {
        for (int i = 0; i < total; i++) {
            Tarjeta t = TestTarjeta.tarjetaAleatoriaNumTarjeta("" + total + i);
            Assert.assertFalse(bdd.getRegistros().contiene(t));
            bdd.agregaRegistro(t);
            Assert.assertTrue(bdd.getRegistros().contiene(t));
            Lista<Tarjeta> l = bdd.getRegistros();
            Assert.assertTrue(l.get(l.getLongitud() - 1).equals(t));
        }
        Tarjeta a = new Tarjeta("A", "A", 1, "A", 1);
        Tarjeta b = new Tarjeta("B", "B", 2, "B", 2);
        bdd.agregaRegistro(a);
        boolean[] llamado = { false };
        bdd.agregaEscucha((e, r1, r2) -> {
            Assert.assertTrue(e == EventoBaseDeDatos.REGISTRO_MODIFICADO);
            Assert.assertTrue(r1.equals(new Tarjeta("A", "A", 1, "A", 1)));
            Assert.assertTrue(r2.equals(new Tarjeta("B", "B", 2, "B", 2)));
            llamado[0] = true;
        });
        Tarjeta c = new Tarjeta("A", "A", 1, "A", 1);
        bdd.modificaRegistro(c, b);
        Assert.assertTrue(llamado[0]);
        Assert.assertTrue(c.equals(new Tarjeta("A", "A", 1, "A", 1)));
        Assert.assertTrue(b.equals(new Tarjeta("B", "B", 2, "B", 2)));
        int ca = 0, cb = 0;
        for (Tarjeta e : bdd.getRegistros()) {
            ca += e.equals(c) ? 1 : 0;
            cb += e.equals(b) ? 1 : 0;
        }
        Assert.assertTrue(ca == 0);
        Assert.assertTrue(cb == 1);
        bdd = new BaseDeDatosTarjetas();
        a = new Tarjeta("A", "A", 1, "A", 1);
        b = new Tarjeta("B", "B", 2, "B", 2);
        bdd.agregaRegistro(a);
        bdd.agregaEscucha((e, r1, r2) -> {
            Assert.assertTrue(e == EventoBaseDeDatos.REGISTRO_MODIFICADO);
            Assert.assertTrue(r1.equals(new Tarjeta("A", "A", 1, "A", 1)));
            Assert.assertTrue(r2.equals(new Tarjeta("B", "B", 2, "B", 2)));
            llamado[0] = true;
        });
        bdd.modificaRegistro(a, b);
        Assert.assertTrue(a.equals(b));
        Assert.assertTrue(llamado[0]);
        bdd = new BaseDeDatosTarjetas();
        llamado[0] = false;
        bdd.agregaEscucha((e, r1, r2) -> {
            Assert.assertTrue(e == EventoBaseDeDatos.REGISTRO_MODIFICADO);
            llamado[0] = true;
        });
        bdd.modificaRegistro(new Tarjeta("A", "A", 1, "A", 1),
                            new Tarjeta("B", "B", 2, "B", 2));
        Assert.assertFalse(llamado[0]);
    }

    /**
     * Prueba unitaria para {@link BaseDeDatos#limpia}.
     */
    @Test
    public void testLimpia() {
        for (int i = 0; i < total; i++) {
            Tarjeta t = TestTarjeta.tarjetaAleatoria();
            bdd.agregaRegistro(t);
        }
        boolean[] llamado = { false };
        bdd.agregaEscucha((e, r1, r2) -> {
            Assert.assertTrue(e == EventoBaseDeDatos.BASE_LIMPIADA);
            Assert.assertTrue(r1 == null);
            Assert.assertTrue(r2 == null);
            llamado[0] = true;
        });
        Lista<Tarjeta> registros = bdd.getRegistros();
        Assert.assertFalse(registros.esVacia());
        Assert.assertFalse(registros.getLongitud() == 0);
        bdd.limpia();
        registros = bdd.getRegistros();
        Assert.assertTrue(registros.esVacia());
        Assert.assertTrue(registros.getLongitud() == 0);
        Assert.assertTrue(llamado[0]);
    }

    /**
     * Prueba unitaria para {@link BaseDeDatos#guarda}.
     */
    @Test
    public void testGuarda() {
        for (int i = 0; i < total; i++) {
            Tarjeta e = TestTarjeta.tarjetaAleatoria();
            bdd.agregaRegistro(e);
        }
        String guardado = "";
        try {
            StringWriter swOut = new StringWriter();
            BufferedWriter out = new BufferedWriter(swOut);
            bdd.guarda(out);
            out.close();
            guardado = swOut.toString();
        } catch (IOException ioe) {
            Assert.fail();
        }
        String[] lineas = guardado.split("\n");
        Assert.assertTrue(lineas.length == total);
        Lista<Tarjeta> l = bdd.getRegistros();
        int c = 0;
        for (Tarjeta t : l) {
            String tl = String.format("%s\t%s\t%d\t%s\t%2.2f",
                            t.getNombreDelPropietario(),
                            t.getNumeroDeTarjeta(),
                            t.getCodigoDeSeguridad(),
                            t.getFechaDeVencimiento(),
                            t.getSaldo());
            Assert.assertTrue(lineas[c++].equals(tl));
        }
    }

    /**
     * Prueba unitaria para {@link BaseDeDatos#carga}.
     */
    @Test
    public void testCarga() {
        int ini = random.nextInt(1000000);
        String entrada = "";
        Tarjeta[] tarjetas = new Tarjeta[total];
        for (int i = 0; i < total; i++) {
            tarjetas[i] = TestTarjeta.tarjetaAleatoriaNumTarjeta("" + ini + i);
            entrada += String.format("%s\t%s\t%d\t%s\t%2.2f\n",
                            tarjetas[i].getNombreDelPropietario(),
                            tarjetas[i].getNumeroDeTarjeta(),
                            tarjetas[i].getCodigoDeSeguridad(),
                            tarjetas[i].getFechaDeVencimiento(),
                            tarjetas[i].getSaldo());
            bdd.agregaRegistro(tarjetas[i]);
        }
        int[] contador = { 0 };
        boolean[] llamado = { false };
        bdd.agregaEscucha((e, r1, r2) -> {
            if (e == EventoBaseDeDatos.BASE_LIMPIADA)
                llamado[0] = true;
            if (e == EventoBaseDeDatos.REGISTRO_AGREGADO) {
                contador[0]++;
                Assert.assertTrue(r1 != null);
                Assert.assertTrue(r2 == null);
            }
        });
        try {
            StringReader srInt = new StringReader(entrada);
            BufferedReader in = new BufferedReader(srInt, 8192);
            bdd.carga(in);
            in.close();
        } catch (IOException ioe) {
            Assert.fail();
        }
        Lista<Tarjeta> l = bdd.getRegistros();
        Assert.assertTrue(l.getLongitud() == total);
        int c = 0;
        for (Tarjeta t : l)
            Assert.assertTrue(tarjetas[c++].equals(t));
        Assert.assertTrue(llamado[0]);
        Assert.assertTrue(contador[0] == total);
        contador[0] = 0;
        llamado[0] = false;
        entrada = String.format("%s\t%s\t%d\t%s\t%2.2f\n",
                        tarjetas[0].getNombreDelPropietario(),
                        tarjetas[0].getNumeroDeTarjeta(),
                        tarjetas[0].getCodigoDeSeguridad(),
                        tarjetas[0].getFechaDeVencimiento(),
                        tarjetas[0].getSaldo());
        entrada += " \n";
        entrada += String.format("%s\t%s\t%d\t%s\t%2.2f\n",
                        tarjetas[1].getNombreDelPropietario(),
                        tarjetas[1].getNumeroDeTarjeta(),
                        tarjetas[1].getCodigoDeSeguridad(),
                        tarjetas[1].getFechaDeVencimiento(),
                        tarjetas[1].getSaldo());
        try {
            StringReader srInt = new StringReader(entrada);
            BufferedReader in = new BufferedReader(srInt, 8192);
            bdd.carga(in);
            in.close();
        } catch (IOException ioe) {
            Assert.fail();
        }
        Assert.assertTrue(bdd.getNumRegistros() == 1);
        Assert.assertTrue(llamado[0]);
        Assert.assertTrue(contador[0] == 1);
        entrada = "";
        try {
            StringReader srInt = new StringReader(entrada);
            BufferedReader in = new BufferedReader(srInt, 8192);
            bdd.carga(in);
            in.close();
        } catch (IOException ioe) {
            Assert.fail();
        }
        Assert.assertTrue(bdd.getNumRegistros() == 0);
    }

    /**
     * Prueba unitaria para {@link BaseDeDatosTarjetas#creaRegistro}.
     */
    @Test
    public void testCreaRegistro() {
        Tarjeta t = (Tarjeta) bdd.creaRegistro();
        Assert.assertTrue(t.getNombreDelPropietario() == null);
        Assert.assertTrue(t.getNumeroDeTarjeta() == null);
        Assert.assertTrue(t.getCodigoDeSeguridad() == 0);
        Assert.assertTrue(t.getFechaDeVencimiento() == null);
        Assert.assertTrue(t.getSaldo() == 0.0);
    }

    /**
     * Prueba unitaria para {@link BaseDeDatosTarjetas#buscaRegistros}.
     */
    @Test
    public void testBuscaRegistros() {
        Tarjeta[] tarjetas = new Tarjeta[total];
        int ini = 1000000 + random.nextInt(999999);
        for (int i = 0; i < total; i++) {
            Tarjeta t = new Tarjeta(String.valueOf(ini + i),
                    String.valueOf(ini + i),
                    i, String.valueOf(ini + i),
                    (i * 10.0) / total);
            tarjetas[i] = t;
            bdd.agregaRegistro(t);
        }

        Tarjeta tarjeta;
        Lista<Tarjeta> l;
        int i;

        for (int k = 0; k < total / 10 + 3; k++) {
            i = random.nextInt(total);
            tarjeta = tarjetas[i];

            String nombre = tarjeta.getNombreDelPropietario();
            l = bdd.buscaRegistros(CampoTarjeta.NOMBRE_DEL_PROPIETARIO, nombre);
            Assert.assertTrue(l.getLongitud() > 0);
            Assert.assertTrue(l.contiene(tarjeta));
            for (Tarjeta t : l)
                Assert.assertTrue(t.getNombreDelPropietario().indexOf(nombre) > -1);

            int n = nombre.length();
            String bn = nombre.substring(random.nextInt(2),
                    2 + random.nextInt(n - 2));
            l = bdd.buscaRegistros(CampoTarjeta.NOMBRE_DEL_PROPIETARIO, bn);
            Assert.assertTrue(l.getLongitud() > 0);
            Assert.assertTrue(l.contiene(tarjeta));
            for (Tarjeta t : l)
                Assert.assertTrue(t.getNombreDelPropietario().indexOf(bn) > -1);

            String num = tarjeta.getNumeroDeTarjeta();
            l = bdd.buscaRegistros(CampoTarjeta.NUMERO_DE_TARJETA, num);
            Assert.assertTrue(l.getLongitud() > 0);
            Assert.assertTrue(l.contiene(tarjeta));
            for (Tarjeta t : l)
                Assert.assertTrue(t.getNombreDelPropietario().indexOf(num) > -1);

            int x = num.length();
            String bn2 = num.substring(random.nextInt(2),
                    2 + random.nextInt(x - 2));
            l = bdd.buscaRegistros(CampoTarjeta.NUMERO_DE_TARJETA, bn2);
            Assert.assertTrue(l.getLongitud() > 0);
            Assert.assertTrue(l.contiene(tarjeta));
            for (Tarjeta t : l)
                Assert.assertTrue(t.getNombreDelPropietario().indexOf(bn2) > -1);

            Integer codigo = Integer.valueOf(tarjeta.getCodigoDeSeguridad());
            l = bdd.buscaRegistros(CampoTarjeta.CODIGO_DE_SEGURIDAD, codigo);
            Assert.assertTrue(l.getLongitud() > 0);
            Assert.assertTrue(l.contiene(tarjeta));
            for (Tarjeta t : l)
                Assert.assertTrue(t.getCodigoDeSeguridad() >= codigo.intValue());

            Integer bc = Integer.valueOf(codigo.intValue() - 10);
            l = bdd.buscaRegistros(CampoTarjeta.CODIGO_DE_SEGURIDAD, bc);
            Assert.assertTrue(l.getLongitud() > 0);
            Assert.assertTrue(l.contiene(tarjeta));
            for (Tarjeta t : l)
                Assert.assertTrue(t.getCodigoDeSeguridad() >= bc.intValue());

            String fecha = tarjeta.getFechaDeVencimiento();
            l = bdd.buscaRegistros(CampoTarjeta.FECHA_DE_VENCIMIENTO, fecha);
            Assert.assertTrue(l.getLongitud() > 0);
            Assert.assertTrue(l.contiene(tarjeta));
            for (Tarjeta t : l)
                Assert.assertTrue(t.getFechaDeVencimiento().indexOf(fecha) > -1);

            int y = fecha.length();
            String bn3 = fecha.substring(random.nextInt(2),
                    2 + random.nextInt(y - 2));
            l = bdd.buscaRegistros(CampoTarjeta.FECHA_DE_VENCIMIENTO, bn3);
            Assert.assertTrue(l.getLongitud() > 0);
            Assert.assertTrue(l.contiene(tarjeta));
            for (Tarjeta t : l)
                Assert.assertTrue(t.getFechaDeVencimiento().indexOf(bn3) > -1);

            Double saldo = Double.valueOf(tarjeta.getSaldo());
            l = bdd.buscaRegistros(CampoTarjeta.SALDO, saldo);
            Assert.assertTrue(l.getLongitud() > 0);
            Assert.assertTrue(l.contiene(tarjeta));
            for (Tarjeta t : l)
                Assert.assertTrue(t.getSaldo() >= saldo.doubleValue());

            Double bp = Double.valueOf(saldo.doubleValue() - 5.0);
            l = bdd.buscaRegistros(CampoTarjeta.SALDO, bp);
            Assert.assertTrue(l.getLongitud() > 0);
            Assert.assertTrue(l.contiene(tarjeta));
            for (Tarjeta t : l)
                Assert.assertTrue(t.getSaldo() >= bp.doubleValue());
        }

        l = bdd.buscaRegistros(CampoTarjeta.NOMBRE_DEL_PROPIETARIO,
                "xxx-nombre");
        Assert.assertTrue(l.esVacia());
        l = bdd.buscaRegistros(CampoTarjeta.NUMERO_DE_TARJETA,
                "XXX");
        Assert.assertTrue(l.esVacia());
        l = bdd.buscaRegistros(CampoTarjeta.CODIGO_DE_SEGURIDAD,
                Integer.valueOf(127));
        Assert.assertTrue(l.esVacia());
        l = bdd.buscaRegistros(CampoTarjeta.FECHA_DE_VENCIMIENTO,
                "XXX");
        Assert.assertTrue(l.esVacia());
        l = bdd.buscaRegistros(CampoTarjeta.SALDO,
                Double.valueOf(97.12));
        Assert.assertTrue(l.esVacia());

        l = bdd.buscaRegistros(CampoTarjeta.NOMBRE_DEL_PROPIETARIO, "");
        Assert.assertTrue(l.esVacia());
        l = bdd.buscaRegistros(CampoTarjeta.NUMERO_DE_TARJETA, "");
        Assert.assertTrue(l.esVacia());
        l = bdd.buscaRegistros(CampoTarjeta.CODIGO_DE_SEGURIDAD,
                Integer.valueOf(Integer.MAX_VALUE));
        Assert.assertTrue(l.esVacia());
        l = bdd.buscaRegistros(CampoTarjeta.FECHA_DE_VENCIMIENTO, "");
        Assert.assertTrue(l.esVacia());
        l = bdd.buscaRegistros(CampoTarjeta.SALDO,
                Double.valueOf(Double.MAX_VALUE));
        Assert.assertTrue(l.esVacia());

        l = bdd.buscaRegistros(CampoTarjeta.NOMBRE_DEL_PROPIETARIO, null);
        Assert.assertTrue(l.esVacia());
        l = bdd.buscaRegistros(CampoTarjeta.NUMERO_DE_TARJETA, null);
        Assert.assertTrue(l.esVacia());
        l = bdd.buscaRegistros(CampoTarjeta.CODIGO_DE_SEGURIDAD, null);
        Assert.assertTrue(l.esVacia());
        l = bdd.buscaRegistros(CampoTarjeta.FECHA_DE_VENCIMIENTO, null);
        Assert.assertTrue(l.esVacia());
        l = bdd.buscaRegistros(CampoTarjeta.SALDO, null);
        Assert.assertTrue(l.esVacia());

        try {
            l = bdd.buscaRegistros(null, null);
            Assert.fail();
        } catch (IllegalArgumentException iae) {
        }
    }

    /**
     * Prueba unitaria para {@link BaseDeDatos#agregaEscucha}.
     */
    @Test
    public void testAgregaEscucha() {
        int[] c = new int[total];
        for (int i = 0; i < total; i++) {
            final int j = i;
            bdd.agregaEscucha((e, r1, r2) -> c[j]++);
        }
        bdd.agregaRegistro(new Tarjeta("A", "A", 1, "A", 1));
        for (int i = 0; i < total; i++)
            Assert.assertTrue(c[i] == 1);
    }

    /**
     * Prueba unitaria para {@link BaseDeDatos#eliminaEscucha}.
     */
    @Test
    public void testEliminaEscucha() {
        int[] c = new int[total];
        Lista<EscuchaBaseDeDatos<Tarjeta>> escuchas = new Lista<EscuchaBaseDeDatos<Tarjeta>>();
        for (int i = 0; i < total; i++) {
            final int j = i;
            EscuchaBaseDeDatos<Tarjeta> escucha = (e, r1, r2) -> c[j]++;
            bdd.agregaEscucha(escucha);
            escuchas.agregaFinal(escucha);
        }
        int i = 0;
        while (!escuchas.esVacia()) {
            bdd.agregaRegistro(TestTarjeta.tarjetaAleatoriaNumTarjeta("" + i++));
            EscuchaBaseDeDatos<Tarjeta> escucha = escuchas.eliminaPrimero();
            bdd.eliminaEscucha(escucha);
        }
        for (i = 0; i < total; i++)
            Assert.assertTrue(c[i] == i + 1);
    }
}
