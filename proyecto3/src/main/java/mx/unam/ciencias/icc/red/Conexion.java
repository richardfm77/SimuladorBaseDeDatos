package mx.unam.ciencias.icc.red;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import mx.unam.ciencias.icc.EventoBaseDeDatos;
import mx.unam.ciencias.icc.ExcepcionLineaInvalida;
import mx.unam.ciencias.icc.BaseDeDatos;
import mx.unam.ciencias.icc.Lista;
import mx.unam.ciencias.icc.Registro;

/**
 * Clase para conexiones de la base de datos.
 */
public class Conexion<R extends Registro<R, ?>> {

    /* Contador de números seriales. */
    private static int contadorSerial;

    /* La entrada de la conexión. */
    private BufferedReader in;
    /* La salida de la conexión. */
    private BufferedWriter out;
    /* La base de datos. */
    private BaseDeDatos<R, ?> bdd;
    /* Lista de escuchas de conexión. */
    private Lista<EscuchaConexion<R>> escuchas;
    /* El enchufe. */
    private Socket enchufe;
    /* Si la conexión está activa. */
    private boolean activa;
    /* El número serial único de la conexión. */
    private int serial;

    /**
     * Define el estado inicial de una nueva conexión.
     * 
     * @param bdd     la base de datos.
     * @param enchufe el enchufe de la conexión ya establecida.
     * @throws IOException si ocurre un error de entrada o salida.
     */
    public Conexion(BaseDeDatos<R, ?> bdd, Socket enchufe) throws IOException {
         
        try {
            this.bdd = bdd;
            this.enchufe = enchufe;

            InputStreamReader isIn = new InputStreamReader(this.enchufe.getInputStream());
            in = new BufferedReader(isIn);

            OutputStreamWriter osOut = new OutputStreamWriter(this.enchufe.getOutputStream());
            out = new BufferedWriter(osOut);

            contadorSerial++;
            serial++;
            activa = true;

            escuchas = new Lista<>();
        } catch (IOException e) {
            throw new IOException();
        }
    }

    /**
     * Recibe los mensajes de la conexión. El método no termina hasta que la
     * conexión sea cerrada. Al ir leyendo su entrada, la conexión convertirá lo
     * que lea en mensajes y reportará cada uno a los escuchas.
     */
    public void recibeMensajes() {
         
        try {
            String linea = in.readLine();
            while (linea != null) {
                activarEscuchas(Mensaje.getMensaje(linea));
                linea = in.readLine();
            }
            activa = false;
        } catch (IOException e) {
            if (activa)
                activarEscuchas(Mensaje.INVALIDO);
        }
        activarEscuchas(Mensaje.DESCONECTAR);
    }

    private void activarEscuchas(Mensaje mensaje) {
        for (EscuchaConexion<R> escucha : escuchas)
            escucha.mensajeRecibido(this, mensaje);
    }

    /**
     * Recibe la base de datos del otro lado de la conexión.
     * 
     * @throws IOException si la base de datos no puede recibirse.
     */
    public void recibeBaseDeDatos() throws IOException {
         
        bdd.carga(in);
    }

    /**
     * Envía la base de datos al otro lado de la conexión.
     * 
     * @throws IOException si la base de datos no puede enviarse.
     */
    public void enviaBaseDeDatos() throws IOException {
         
        bdd.guarda(out);
        out.newLine();
        out.flush();
    }

    /**
     * Recibe un registro del otro lado de la conexión.
     * 
     * @return un registro del otro lado de la conexión.
     * @throws IOException si el registro no puede recibirse.
     */
    public R recibeRegistro() throws IOException {
         
        String rS = null;
        R resgistro = bdd.creaRegistro();
        try {
            rS = in.readLine();
            resgistro.deserializa(rS);
        } catch (IOException e) {
            throw new IOException();
        }
        return resgistro;
    }

    /**
     * Envía un registro al otro lado de la conexión.
     * 
     * @param registro el registro a enviar.
     * @throws IOException si el registro no puede enviarse.
     */
    public void enviaRegistro(R registro) throws IOException {
         
        String rs = registro.serializa();
        out.write(rs);
        out.flush();
    }

    /**
     * Envía mensaje registro al otro lado de la conexión.
     * 
     * @param mensaje el mensaje a enviar.
     * @throws IOException si el mensaje no puede enviarse.
     */
    public void enviaMensaje(Mensaje mensaje) throws IOException {
         
        out.write(mensaje.toString() + "\n");
        out.flush();
    }

    /**
     * Regresa un número serial para cada conexión.
     * 
     * @return un número serial para cada conexión.
     */
    public int getSerial() {
         
        return contadorSerial;
    }

    /**
     * Cierra la conexión.
     */
    public void desconecta() {
         
        activa = false;
        try {
            enchufe.close();
        } catch (IOException e) {
        }
    }

    /**
     * Nos dice si la conexión es activa.
     * 
     * @return <code>true</code> si la conexión es activa; <code>false</code> en
     *         otro caso.
     */
    public boolean isActiva() {
         
        return activa;
    }

    /**
     * Agrega un escucha de conexión.
     * 
     * @param escucha el escucha a agregar.
     */
    public void agregaEscucha(EscuchaConexion<R> escucha) {
         
        escuchas.agregaFinal(escucha);
    }
}
