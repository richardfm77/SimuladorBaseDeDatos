package mx.unam.ciencias.icc.red.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Random;
import mx.unam.ciencias.icc.BaseDeDatos;
import mx.unam.ciencias.icc.BaseDeDatosTarjetas;
import mx.unam.ciencias.icc.CampoTarjeta;
import mx.unam.ciencias.icc.Tarjeta;
import mx.unam.ciencias.icc.ExcepcionLineaInvalida;
import mx.unam.ciencias.icc.Lista;
import mx.unam.ciencias.icc.red.Mensaje;
import mx.unam.ciencias.icc.red.ServidorBaseDeDatos;
import mx.unam.ciencias.icc.red.ServidorBaseDeDatosTarjetas;
import mx.unam.ciencias.icc.test.TestTarjeta;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;

/**
 * Clase para pruebas unitarias de la clase {@link
 * ServidorBaseDeDatosTarjetas}.
 */
public class TestServidorBaseDeDatosTarjetas {

    /** Expiración para que ninguna prueba tarde más de 5 segundos. */
    @Rule
    public Timeout expiracion = Timeout.seconds(5);
    /** Directorio para archivos temporales. */
    @Rule
    public TemporaryFolder directorio = new TemporaryFolder();

    /* Clase interna para manejar una conexión de pruebas. */
    private class Cliente {

        /* El enchufe. */
        private Socket enchufe;
        /* La entrada. */
        private BufferedReader in;
        /* La salida. */
        private BufferedWriter out;

        /* Construye una nueva conexión en el puerto. */
        private Cliente(int puerto) {
            try {
                enchufe = new Socket("localhost", puerto);
                in = new BufferedReader(
                        new InputStreamReader(
                                enchufe.getInputStream()));
                out = new BufferedWriter(
                        new OutputStreamWriter(
                                enchufe.getOutputStream()));
            } catch (IOException ioe) {
                Assert.fail();
            }
        }

        /* Envía un mensaje por la conexión. */
        private void enviaMensaje(Mensaje mensaje) {
            try {
                out.write(mensaje.toString());
                out.newLine();
                out.flush();
                UtilRed.espera(10);
            } catch (IOException ioe) {
                Assert.fail();
            }
        }

        /* Envía una tarjeta por la conexión. */
        public void enviaTarjeta(Tarjeta tarjeta) {
            try {
                out.write(tarjeta.serializa());
                out.flush();
                UtilRed.espera(10);
            } catch (IOException ioe) {
                Assert.fail();
            }
        }

        /* Envía una cadena por la conexión. */
        public void enviaCadena(String cadena) {
            try {
                out.write(cadena);
                out.newLine();
                out.flush();
                UtilRed.espera(10);
            } catch (IOException ioe) {
                Assert.fail();
            }
        }

        /* Envía una nueva línea por la conexión. */
        public void enviaLinea() {
            try {
                out.newLine();
                out.flush();
                UtilRed.espera(10);
            } catch (IOException ioe) {
                Assert.fail();
            }
        }

        /* Recibe un mensaje por la conexión. */
        public Mensaje recibeMensaje() {
            return Mensaje.getMensaje(recibeCadena());
        }

        /* Recibe una cadena por la conexión. */
        public String recibeCadena() {
            try {
                return in.readLine();
            } catch (IOException ioe) {
                Assert.fail();
            }
            /* Inalcanzable. */
            return null;
        }

        /* Recibe una base de datos por la conexión. */
        public BaseDeDatosTarjetas recibeBaseDeDatos() {
            BaseDeDatosTarjetas bdd = new BaseDeDatosTarjetas();
            try {
                bdd.carga(in);
                return bdd;
            } catch (IOException ioe) {
                Assert.fail();
            }
            /* Inalcanzable. */
            return null;
        }

        /* Recibe una tarjeta por la conexión. */
        public Tarjeta recibeTarjeta() {
            Tarjeta e = new Tarjeta(null, null, 0, null, 0);
            try {
                e.deserializa(in.readLine());
                return e;
            } catch (IOException | ExcepcionLineaInvalida ex) {
                Assert.fail();
            }
            /* Inalcanzable. */
            return null;
        }
    }

    /* Compara dos tarjetas. */
    private int compara(Tarjeta e1, Tarjeta e2) {
        if (!e1.getNombreDelPropietario().equals(e2.getNombreDelPropietario()))
            return e1.getNumeroDeTarjeta().compareTo(e2.getNumeroDeTarjeta());
        if (!e1.getNumeroDeTarjeta().equals(e2.getNumeroDeTarjeta()))
            return e1.getNumeroDeTarjeta().compareTo(e2.getNumeroDeTarjeta());
        if (e1.getCodigoDeSeguridad() != e2.getCodigoDeSeguridad())
            return e1.getCodigoDeSeguridad() - e2.getCodigoDeSeguridad();
        if (!e1.getNumeroDeTarjeta().equals(e2.getNumeroDeTarjeta()))
            return e1.getNumeroDeTarjeta().compareTo(e2.getNumeroDeTarjeta());

        return (e1.getSaldo() < e2.getSaldo()) ? -1 : 1;
    }

    /* Valida el archivo de la base de datos. */
    private void validaArchivo(BaseDeDatosTarjetas bdd) {
        BaseDeDatosTarjetas bdd2 = new BaseDeDatosTarjetas();
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(archivo)));
            bdd2.carga(in);
            in.close();
        } catch (IOException ioe) {
            Assert.fail();
        }
        Lista<Tarjeta> l1 = bdd.getRegistros();
        l1 = l1.mergeSort((e1, e2) -> compara(e1, e2));
        Lista<Tarjeta> l2 = bdd2.getRegistros();
        l2 = l2.mergeSort((e1, e2) -> compara(e1, e2));
        Assert.assertTrue(l1.equals(l2));
    }

    /* Generador de números aleatorios. */
    private Random random;
    /* Servidor de base de datos. */
    private ServidorBaseDeDatosTarjetas sbdd;
    /* El total de tarjetas. */
    private int total;
    /* Las tarjetas. */
    private Tarjeta[] tarjetas;
    /* El archivo temporal de la base de datos. */
    private String archivo;
    /* El puerto. */
    private int puerto;

    /**
     * Crea un generador de números aleatorios para cada prueba y una base de
     * datos de tarjetas.
     */
    public TestServidorBaseDeDatosTarjetas() {
        random = new Random();
        total = 10 + random.nextInt(100);
        puerto = obtenPuerto();
    }

    /* Obtiene el puerto. */
    private int obtenPuerto() {
        int p = -1;
        while (p < 1024) {
            try {
                p = 1024 + random.nextInt(64500);
                ServerSocket s = new ServerSocket(p);
                s.close();
            } catch (BindException be) {
                p = -1;
                UtilRed.espera(10);
            } catch (IOException ioe) {
                Assert.fail();
            }
        }
        return p;
    }

    /**
     * Método que se ejecuta antes de cada prueba unitaria; crea el archivo de
     * la base de datos y hace servir el servidor.
     */
    @Before
    public void arma() {
        try {
            tarjetas = new Tarjeta[total];
            BaseDeDatosTarjetas bdd = new BaseDeDatosTarjetas();
            for (int i = 0; i < total; i++) {
                tarjetas[i] = TestTarjeta.tarjetaAleatoria();
                bdd.agregaRegistro(tarjetas[i]);
            }
            File f = null;
            String s = String.format("test-base-de-datos-%04d.db",
                    random.nextInt(9999));
            f = directorio.newFile(s);
            archivo = f.getAbsolutePath();
            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(archivo)));
            bdd.guarda(out);
            out.close();
            sbdd = new ServidorBaseDeDatosTarjetas(puerto, archivo);
            new Thread(() -> sbdd.sirve()).start();
        } catch (IOException ioe) {
            Assert.fail();
        }
    }

    /**
     * Método que se ejecuta despué de cada prueba unitaria; elimina el archivo
     * de la base de datos y detiene el servidor. Esto hace un método
     * testSirveDetenerServidor innecesario.
     */
    @After
    public void desarma() {
        File f = new File(archivo);
        f.delete();
        Cliente c = new Cliente(puerto);
        c.enviaMensaje(Mensaje.DETENER_SERVICIO);
        Assert.assertTrue(c.recibeCadena() == null);
    }

    /*
     * Crea una nueva conexión, enviando y recibiendo un eco para probarla de
     * inmediato.
     */
    private Cliente nuevoCliente() {
        Cliente c = new Cliente(puerto);
        c.enviaMensaje(Mensaje.ECO);
        Assert.assertTrue(c.recibeMensaje() == Mensaje.ECO);
        return c;
    }

    /**
     * Prueba unitaria para el mensaje {@link Mensaje#BASE_DE_DATOS} en el
     * método {@link ServidorBaseDeDatos#sirve}.
     */
    @Test
    public void testSirveBaseDeDatos() {
        Cliente c = nuevoCliente();
        c.enviaMensaje(Mensaje.BASE_DE_DATOS);
        Assert.assertTrue(c.recibeMensaje() == Mensaje.BASE_DE_DATOS);
        BaseDeDatosTarjetas bdd = c.recibeBaseDeDatos();
        Lista<Tarjeta> l = bdd.getRegistros();
        Assert.assertTrue(l.getLongitud() == total);
        int i = 0;
        for (Tarjeta t : l)
            Assert.assertTrue(t.equals(tarjetas[i++]));
    }

    /**
     * Prueba unitaria para el mensaje {@link Mensaje#REGISTRO_AGREGADO}
     * en el método {@link ServidorBaseDeDatos#sirve}.
     */
    @Test
    public void testSirveRegistroAgregado() {
        Cliente c1 = nuevoCliente();
        Cliente c2 = nuevoCliente();

        Tarjeta tarjeta = new Tarjeta("A", "A", 1, "A", 1);
        c1.enviaMensaje(Mensaje.REGISTRO_AGREGADO);
        c1.enviaTarjeta(tarjeta);

        c1.enviaMensaje(Mensaje.BASE_DE_DATOS);
        Assert.assertTrue(c1.recibeMensaje() == Mensaje.BASE_DE_DATOS);
        BaseDeDatosTarjetas bdd = c1.recibeBaseDeDatos();
        Lista<Tarjeta> l = bdd.getRegistros();
        Assert.assertTrue(l.contiene(tarjeta));

        Assert.assertTrue(c2.recibeMensaje() == Mensaje.REGISTRO_AGREGADO);
        Tarjeta t = c2.recibeTarjeta();
        Assert.assertTrue(t.equals(tarjeta));
        validaArchivo(bdd);
    }

    /**
     * Prueba unitaria para el mensaje {@link Mensaje#REGISTRO_ELIMINADO}
     * en el método {@link ServidorBaseDeDatos#sirve}.
     */
    @Test
    public void testSirveRegistroEliminado() {
        Cliente c1 = nuevoCliente();
        Cliente c2 = nuevoCliente();
        Tarjeta tarjeta = tarjetas[random.nextInt(total)];
        c1.enviaMensaje(Mensaje.REGISTRO_ELIMINADO);
        c1.enviaTarjeta(tarjeta);

        c1.enviaMensaje(Mensaje.BASE_DE_DATOS);
        Assert.assertTrue(c1.recibeMensaje() == Mensaje.BASE_DE_DATOS);
        BaseDeDatosTarjetas bdd = c1.recibeBaseDeDatos();
        Lista<Tarjeta> l = bdd.getRegistros();
        Assert.assertFalse(l.contiene(tarjeta));
        Assert.assertTrue(c2.recibeMensaje() == Mensaje.REGISTRO_ELIMINADO);
        Tarjeta t = c2.recibeTarjeta();
        Assert.assertTrue(t.equals(tarjeta));
        validaArchivo(bdd);
    }

    /**
     * Prueba unitaria para el mensaje {@link Mensaje#REGISTRO_MODIFICADO}
     * en el método {@link ServidorBaseDeDatos#sirve}.
     */
    @Test
    public void testSirveRegistroModificado() {
        Cliente c1 = nuevoCliente();
        Cliente c2 = nuevoCliente();
        Tarjeta tarjeta = tarjetas[random.nextInt(total)];
        Tarjeta m = new Tarjeta(null, null, 0, null, 0);
        m.actualiza(tarjeta);
        m.setNombreDelPropietario("A");
        c1.enviaMensaje(Mensaje.REGISTRO_MODIFICADO);
        c1.enviaTarjeta(tarjeta);
        c1.enviaTarjeta(m);
        c1.enviaMensaje(Mensaje.BASE_DE_DATOS);
        Assert.assertTrue(c1.recibeMensaje() == Mensaje.BASE_DE_DATOS);
        BaseDeDatosTarjetas bdd = c1.recibeBaseDeDatos();
        Lista<Tarjeta> l = bdd.getRegistros();
        Assert.assertFalse(l.contiene(tarjeta));
        Assert.assertTrue(l.contiene(m));
        Assert.assertTrue(c2.recibeMensaje() == Mensaje.REGISTRO_MODIFICADO);
        Tarjeta t = new Tarjeta(null, null, 0, null, 0);
        t = c2.recibeTarjeta();
        Assert.assertTrue(t.equals(tarjeta));
        t = c2.recibeTarjeta();
        Assert.assertTrue(t.equals(m));
        UtilRed.espera(10);
        validaArchivo(bdd);
    }

    /**
     * Prueba unitaria para el mensaje {@link Mensaje#DESCONECTAR}
     * en el método {@link ServidorBaseDeDatos#sirve}.
     */
    @Test
    public void testSirveDesconectar() {
        Cliente c = nuevoCliente();
        c.enviaMensaje(Mensaje.DESCONECTAR);
        Assert.assertTrue(c.recibeCadena() == null);
        c = nuevoCliente();
        c.enviaMensaje(Mensaje.ECO);
        Assert.assertTrue(c.recibeMensaje() == Mensaje.ECO);
    }

    /**
     * Prueba unitaria para el mensaje {@link Mensaje#ECO} en el método
     * {@link ServidorBaseDeDatos#sirve}.
     */
    @Test
    public void testSirveEco() {
        Cliente c = nuevoCliente();
        c.enviaMensaje(Mensaje.ECO);
        Assert.assertTrue(c.recibeMensaje() == Mensaje.ECO);
    }

    /**
     * Prueba unitaria para el mensaje {@link Mensaje#INVALIDO} en
     * el método {@link ServidorBaseDeDatos#sirve}.
     */
    @Test
    public void testSirveMensajeInvalido() {
        Cliente c = nuevoCliente();
        c.enviaMensaje(Mensaje.INVALIDO);
        Assert.assertTrue(c.recibeCadena() == null);
    }

    /**
     * Prueba unitaria para {@link ServidorBaseDeDatosTarjetas#agregaEscucha}
     * al realizar conexiones.
     */
    @Test
    public void testAgregaEscucha() {
        UtilRed.espera(10);
        Lista<String> mensajes = new Lista<String>();
        sbdd.agregaEscucha((f, a) -> {
            String s = a.length > 0 ? String.format(f, a) : f;
            mensajes.agregaFinal(s);
        });
        Cliente c = nuevoCliente();
        c.enviaMensaje(Mensaje.BASE_DE_DATOS);
        c.recibeMensaje();
        BaseDeDatosTarjetas bd = c.recibeBaseDeDatos();
        c.enviaMensaje(Mensaje.REGISTRO_AGREGADO);
        Tarjeta t = UtilRed.tarjetaAleatoria(total);
        c.enviaTarjeta(t);
        c.enviaMensaje(Mensaje.REGISTRO_ELIMINADO);
        c.enviaTarjeta(t);
        c.enviaMensaje(Mensaje.INVALIDO);
        UtilRed.espera(10);
        Iterator<String> i = mensajes.iterator();
        Assert.assertTrue(i.hasNext());
        String m = i.next();
        Assert.assertTrue(m.equals("Conexión recibida de: localhost."));
        Assert.assertTrue(i.hasNext());
        m = i.next();
        Assert.assertTrue(m.startsWith("Serial de conexión: "));
        m = m.replace("Serial de conexión: ", "").replace(".", "");
        int serial = Integer.parseInt(m);
        Assert.assertTrue(serial > 0);
        Assert.assertTrue(i.hasNext());
        m = i.next();
        String r = String.format("Solicitud de eco de %d.", serial);
        Assert.assertTrue(m.equals(r));
        Assert.assertTrue(i.hasNext());
        m = i.next();
        r = String.format("Base de datos pedida por %d.", serial);
        Assert.assertTrue(m.equals(r));
        Assert.assertTrue(i.hasNext());
        m = i.next();
        r = String.format("Registro agregado por %d.", serial);
        Assert.assertTrue(m.equals(r));
        Assert.assertTrue(i.hasNext());
        m = i.next();
        r = String.format("Guardando base de datos en %s.", archivo);
        Assert.assertTrue(m.equals(r));
        Assert.assertTrue(i.hasNext());
        m = i.next();
        Assert.assertTrue(m.equals("Base de datos guardada."));
        Assert.assertTrue(i.hasNext());
        m = i.next();
        r = String.format("Registro eliminado por %d.", serial);
        Assert.assertTrue(m.equals(r));
        Assert.assertTrue(i.hasNext());
        m = i.next();
        r = String.format("Guardando base de datos en %s.", archivo);
        Assert.assertTrue(m.equals(r));
        Assert.assertTrue(i.hasNext());
        m = i.next();
        Assert.assertTrue(m.equals("Base de datos guardada."));
        Assert.assertTrue(i.hasNext());
        m = i.next();
        r = String.format("Desconectando la conexión %d: Mensaje inválido.",
                serial);
        Assert.assertTrue(m.equals(r));
        Assert.assertTrue(i.hasNext());
        m = i.next();
        r = String.format("La conexión %d ha sido desconectada.", serial);
        Assert.assertTrue(m.equals(r));
        Assert.assertFalse(i.hasNext());
        Assert.assertTrue(c.recibeCadena() == null);
        sbdd.limpiaEscuchas();
    }

    /**
     * Prueba unitaria para {@link
     * ServidorBaseDeDatosTarjetas#creaBaseDeDatos}.
     */
    @Test
    public void testCreaBaseDeDatos() {
        BaseDeDatos<Tarjeta, CampoTarjeta> bdd = sbdd.creaBaseDeDatos();
        Assert.assertTrue(bdd instanceof BaseDeDatosTarjetas);
        Assert.assertTrue(bdd.getNumRegistros() == 0);
    }
}
