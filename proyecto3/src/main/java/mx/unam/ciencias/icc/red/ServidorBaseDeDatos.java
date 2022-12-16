package mx.unam.ciencias.icc.red;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import mx.unam.ciencias.icc.BaseDeDatos;
import mx.unam.ciencias.icc.Lista;
import mx.unam.ciencias.icc.Registro;

/**
 * Clase abstracta para servidores de bases de datos genéricas.
 */
public abstract class ServidorBaseDeDatos<R extends Registro<R, ?>> {

    /* La base de datos. */
    private BaseDeDatos<R, ? extends Enum> bdd;
    /* El servidor de enchufes. */
    private ServerSocket servidor;
    /* El puerto. */
    private int puerto;
    /* El archivo donde cargar/guardar la base de datos. */
    private String archivo;
    /* Lista con las conexiones. */
    private Lista<Conexion<R>> conexiones;
    /* Bandera de continuación. */
    private Boolean continuaEjecucion;
    /* Escuchas del servidor. */
    private Lista<EscuchaServidor> escuchas;

    /**
     * Crea un nuevo servidor usando el archivo recibido para poblar la base de
     * datos.
     * 
     * @param puerto  el puerto dónde escuchar por conexiones.
     * @param archivo el archivo en el disco del cual cargar/guardar la base de
     *                datos. Puede ser <code>null</code>, en cuyo caso se usará
     *                el nombre por omisión <code>base-de-datos.bd</code>.
     * @throws IOException si ocurre un error de entrada o salida.
     */
    public ServidorBaseDeDatos(int puerto, String archivo)
            throws IOException {
         
        this.puerto = puerto;
        this.archivo = (archivo != null) ? archivo : "base-de-datos.db";
        servidor = new ServerSocket(puerto);
        conexiones = new Lista<Conexion<R>>();
        escuchas = new Lista<EscuchaServidor>();
        bdd = creaBaseDeDatos();
        carga();
    }

    /**
     * Comienza a escuchar por conexiones de clientes.
     */
    public void sirve() {
         
        continuaEjecucion = true;
        imprimeMensaje("Escuchando en el puerto: %d.", puerto);
        while (continuaEjecucion) {
            try {
                Socket enchufe = servidor.accept();
                Conexion<R> conexion = new Conexion<R>(bdd, enchufe);
                String hostName = enchufe.getInetAddress().getCanonicalHostName();
                imprimeMensaje("Conexión recibida de: %s.", hostName);
                imprimeMensaje("Serial de conexión: %d.", conexion.getSerial());
                conexion.agregaEscucha((c, m) -> mensajeRecibido(c, m));
                new Thread(() -> conexion.recibeMensajes()).start();
                synchronized (conexiones) {
                    conexiones.agregaFinal(conexion);
                }
            } catch (IOException e) {
                if (continuaEjecucion)
                    imprimeMensaje("Error al recibir una conexión.");
            }
        }
        imprimeMensaje("La ejecución del servidor ha terminado.");
    }

    /**
     * Agrega un escucha de servidor.
     * 
     * @param escucha el escucha a agregar.
     */
    public void agregaEscucha(EscuchaServidor escucha) {
         
        escuchas.agregaFinal(escucha);
    }

    /**
     * Limpia todos los escuchas del servidor.
     */
    public void limpiaEscuchas() {
         
        escuchas.limpia();
    }

    /**
     * Crea la base de datos concreta.
     * 
     * @return la base de datos concreta.
     */
    public abstract BaseDeDatos<R, ? extends Enum> creaBaseDeDatos();

    /* Carga la base de datos del disco duro. */
    private void carga() {
        try {
            imprimeMensaje("Cargando base de datos de %s.", archivo);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(archivo)));
            bdd.carga(in);
            in.close();
            imprimeMensaje("Base de datos cargada exitosamente de %s.", archivo);
        } catch (IOException e) {
            imprimeMensaje("Ocurrió un error al tratar de cargar $s.", archivo);
            imprimeMensaje("La base de datos estará inicialmente vacía.");
        }
    }

    /* Guarda la base datos en el disco duro. */
    private void guarda() {
        try {
            imprimeMensaje("Guardando base de datos en %s.", archivo);
            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(archivo)));
            bdd.guarda(out);
            out.close();
            imprimeMensaje("Base de datos guardada.");
        } catch (IOException e) {
            imprimeMensaje("Ocurrió un error al guardar la base de datos.");
        }
    }

    /* Recibe los mensajes de la conexion. */
    private void mensajeRecibido(Conexion<R> conexion, Mensaje mensaje) {
        if (!conexion.isActiva())
            return;
        switch (mensaje) {
            case BASE_DE_DATOS:
                manejaBaseDeDatos(conexion);
                break;
            case REGISTRO_AGREGADO:
                manejaRegistroAlterado(conexion, mensaje);
                break;
            case REGISTRO_ELIMINADO:
                manejaRegistroAlterado(conexion, mensaje);
                break;
            case REGISTRO_MODIFICADO:
                manejaRegistroModificado(conexion);
                break;
            case DESCONECTAR:
                manejaDesconectar(conexion);
                break;
            case DETENER_SERVICIO:
                manejaDetenerServidor(conexion);
                break;
            case ECO:
                manejaEco(conexion);
                break;
            case INVALIDO:
                error(conexion, "Mensaje inválido");
                break;
        }
    }

    /* Maneja el mensaje BASE_DE_DATOS */
    private void manejaBaseDeDatos(Conexion<R> conexion) {
        try {
            conexion.enviaMensaje(Mensaje.BASE_DE_DATOS);
            conexion.enviaBaseDeDatos();
        } catch (IOException e) {
            error(conexion, "Error enviando la base de datos.");
        }
        imprimeMensaje("Base de datos pedida por %d.", conexion.getSerial());
    }

    /* Maneja los mensajes REGISTRO_AGREGADO y REGISTRO_MODIFICADO */
    private void manejaRegistroAlterado(Conexion<R> conexion, Mensaje mensaje) {
        R r = null;
        try {
            r = conexion.recibeRegistro();
        } catch (IOException e) {
            error(conexion, "Error recibiendo registro.");
            return;
        }
        String accion;
        if (mensaje == Mensaje.REGISTRO_AGREGADO) {
            agregaRegistro(r);
            accion = "agregado";
        } else {
            eliminaRegistro(r);
            accion = "eliminado";
        }
        for (Conexion<R> c : conexiones) {
            if (conexion == c)
                continue;
            try {
                c.enviaMensaje(mensaje);
                c.enviaRegistro(r);
            } catch (IOException io) {
                error(conexion, "Error recibiendo registro.");
            }
        }
        imprimeMensaje("Registro %s por %d.", accion, conexion.getSerial());
        guarda();
    }

    /* Maneja el mensaje REGISTRO_MODIFICADO */
    private void manejaRegistroModificado(Conexion<R> conexion) {
        R r1, r2 = null;
        try {
            r1 = conexion.recibeRegistro();
            r2 = conexion.recibeRegistro();
        } catch (IOException io) {
            error(conexion, "Error recibiendo registro.");
            return;
        }

        modificaRegistro(r1, r2);
        for (Conexion<R> c : conexiones) {
            if (c == conexion)
                continue;
            try {
                c.enviaMensaje(Mensaje.REGISTRO_MODIFICADO);
                c.enviaRegistro(r1);
                c.enviaRegistro(r2);
            } catch (IOException io) {
                error(conexion, "Error recibiendo registro.");
            }
        }
        imprimeMensaje("Registro modifia por %d.", conexion.getSerial());
        guarda();
    }

    /* Maneja el mensaje DESCONECTAR */
    private void manejaDesconectar(Conexion<R> conexion) {
        imprimeMensaje("Solicitud de desconexión de %d.", conexion.getSerial());
        desconecta(conexion);
    }

    /* Maneja el mensaje DETENER_SERVIDOR */
    private void manejaDetenerServidor(Conexion<R> conexion) {
        imprimeMensaje("Solicitud de detener servicio de %d.", conexion.getSerial());
        continuaEjecucion = false;
        for (Conexion<R> c : conexiones)
            c.desconecta();
        try {
            servidor.close();
        } catch (IOException io) {
        }
    }

    /* Maneja el mensaje ECO */
    private void manejaEco(Conexion<R> conexion) {
        try {
            imprimeMensaje("Solicitud de eco de %d.",
                            conexion.getSerial());
            conexion.enviaMensaje(Mensaje.ECO);
        } catch (IOException io) {
            error(conexion, "Error enviando eco.");
            return;
        }
    }

    /* Imprime un mensaje a los escuchas y desconecta la conexion */
    private void error(Conexion<R> conexion, String mensaje) {
        imprimeMensaje("Desconectando la conexión %d: %s.", 
                        conexion.getSerial(), mensaje);
        desconecta(conexion);                
    }

    /* Desconecta la conexión */
    private void desconecta(Conexion<R> conexion){
        conexion.desconecta();
        synchronized (conexiones){
            conexiones.elimina(conexion);
        }
        imprimeMensaje("La conexión %d ha sido desconectada.", 
                        conexion.getSerial());
    }

    /* Agrega el registro a la base de datos */
    private synchronized void agregaRegistro(R registro) {
        bdd.agregaRegistro(registro);
    }

    /* Elimina el registro de la base de datos */
    private synchronized void eliminaRegistro(R registro) {
        bdd.eliminaRegistro(registro);
    }

    /* Modifica el registro en la base de datos */
    private synchronized void modificaRegistro(R registro1, R registro2) {
        bdd.modificaRegistro(registro1, registro2);
    }

    /* Procesa los mensajes de todos los escuchas */
    private synchronized void imprimeMensaje(String formato, Object... argumentos) {
        for (EscuchaServidor escucha : escuchas)
            escucha.procesaMensaje(formato, argumentos);
    }
}
