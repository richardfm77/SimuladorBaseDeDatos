package mx.unam.ciencias.icc;

/**
 * Interfaz para registros. Los registros deben de poder serializarse a y
 * deserializarse de una línea de texto. También deben poder determinar si sus
 * campos cazan valores arbitrarios y actualizarse con los valores de otro
 * registro.
 *
 * @param <R> El tipo de los registros, para poder actualizar registros del
 * mismo tipo.
 * @param <C> El tipo de los campos de los registros, que debe ser una
 * enumeración {@link Enum}.
 */
public interface Registro<R, C extends Enum> {

    /**
     * Regresa el registro serializado en una línea de texto. La línea de texto
     * que este método regresa debe ser aceptada por el método {@link
     * Registro#deserializa}.
     * @return la serialización del registro en una línea de texto.
     */
    public String serializa();

    /**
     * Deserializa una línea de texto en las propiedades del registro. La
     * serialización producida por el método {@link Registro#serializa} debe
     * ser aceptada por este método.
     * @param linea la línea a deserializar.
     * @throws ExcepcionLineaInvalida si la línea recibida es nula, vacía o no
     *         es una serialización válida de un registro.
     */
    public void deserializa(String linea);

    /**
     * Actualiza los valores del registro con los del registro recibido.
     * @param registro el registro con el cual actualizar los valores.
     */
    public void actualiza(R registro);

    /**
     * Nos dice si el registro caza el valor dado en el campo especificado.
     * @param campo el campo que hay que cazar.
     * @param valor el valor con el que debe cazar el campo del registro.
     * @return <code>true</code> si el campo especificado del registro caza el
     *         valor dado, <code>false</code> en otro caso.
     */
    public boolean caza(C campo, Object valor);
}
