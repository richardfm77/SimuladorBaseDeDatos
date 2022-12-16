package mx.unam.ciencias.icc;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>Clase para listas genéricas doblemente ligadas.</p>
 *
 * <p>Las listas nos permiten agregar elementos al inicio o final de la lista,
 * eliminar elementos de la lista, comprobar si un elemento está o no en la
 * lista, y otras operaciones básicas.</p>
 *
 * <p>Las listas implementan la interfaz {@link Iterable}, y por lo tanto se
 * pueden recorrer usando la estructura de control <em>for-each</em>. Las listas
 * no aceptan a <code>null</code> como elemento.</p>
 *
 * @param <T> El tipo de los elementos de la lista.
 */
public class Lista<T> implements Iterable<T> {

    /* Clase interna privada para nodos. */
    private class Nodo {
        /* El elemento del nodo. */
        private T elemento;
        /* El nodo anterior. */
        private Nodo anterior;
        /* El nodo siguiente. */
        private Nodo siguiente;

        /* Construye un nodo con un elemento. */
        private Nodo(T elemento) {
             
            this.elemento = elemento;
        }
    }

    /* Clase Iterador privada para iteradores. */
    private class Iterador implements IteradorLista<T> {
        /* El nodo anterior. */
        private Nodo anterior;
        /* El nodo siguiente. */
        private Nodo siguiente;

        /* Construye un nuevo iterador. */
        private Iterador() {
             
            siguiente = cabeza;
            anterior = null;
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
             
            return siguiente != null;
        }

        /* Nos da el elemento siguiente. */
        @Override public T next() {
             
            if (siguiente == null)
                throw new NoSuchElementException();

            T t = siguiente.elemento;

            anterior = siguiente;
            siguiente = siguiente.siguiente;

            return t;
        }

        /* Nos dice si hay un elemento anterior. */
        @Override public boolean hasPrevious() {
             
            return anterior != null;
        }

        /* Nos da el elemento anterior. */
        @Override public T previous() {
             
            if (anterior == null)
                throw new NoSuchElementException();

            T t = anterior.elemento;

            siguiente = anterior;
            anterior = anterior.anterior;

            return t;
        }

        /* Mueve el iterador al inicio de la lista. */
        @Override public void start() {
             
            siguiente = cabeza;
            anterior = null;
        }

        /* Mueve el iterador al final de la lista. */
        @Override public void end() {
             
            siguiente = null;
            anterior = rabo;
        }
    }

    /* Primer elemento de la lista. */
    private Nodo cabeza;
    /* Último elemento de la lista. */
    private Nodo rabo;
    /* Número de elementos en la lista. */
    private int longitud;

    /**
     * Regresa la longitud de la lista.
     * @return la longitud de la lista, el número de elementos que contiene.
     */
    public int getLongitud() {
         
        return longitud;
    }

    /**
     * Nos dice si la lista es vacía.
     * @return <code>true</code> si la lista es vacía, <code>false</code> en
     *         otro caso.
     */
    public boolean esVacia() {
         
        return cabeza == null;
    }

    /**
     * Agrega un elemento al final de la lista. Si la lista no tiene elementos,
     * el elemento a agregar será el primero y último.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void agregaFinal(T elemento) {
         
        if (elemento == null)
            throw new IllegalArgumentException();

        Nodo n = new Nodo(elemento);

        if (cabeza == null) {
            cabeza = rabo = n;
        } else {
            rabo.siguiente = n;
            n.anterior = rabo;
            rabo = n;
        }
        longitud++;
    }

    /**
     * Agrega un elemento al inicio de la lista. Si la lista no tiene elementos,
     * el elemento a agregar será el primero y último.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void agregaInicio(T elemento) {
         
        if (elemento == null)
            throw new IllegalArgumentException();

        Nodo n = new Nodo(elemento);

        if (cabeza == null) {
            cabeza = rabo = n;
        } else {
            Nodo var = cabeza;
            cabeza = n;
            cabeza.siguiente = var;
            cabeza.siguiente.anterior = cabeza;
        }
        longitud++;
    }

    /**
     * Inserta un elemento en un índice explícito.
     *
     * Si el índice es menor o igual que cero, el elemento se agrega al inicio
     * de la lista. Si el índice es mayor o igual que el número de elementos en
     * la lista, el elemento se agrega al fina de la misma. En otro caso,
     * después de mandar llamar el método, el elemento tendrá el índice que se
     * especifica en la lista.
     * @param i el índice dónde insertar el elemento. Si es menor que 0 el
     *          elemento se agrega al inicio de la lista, y si es mayor o igual
     *          que el número de elementos en la lista se agrega al final.
     * @param elemento el elemento a insertar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void inserta(int i, T elemento) {
         
        if (elemento == null)
            throw new IllegalArgumentException();

        if (cabeza == null) {
            if (i <= 0)
                agregaInicio(elemento);
            return;
        }

        if (i <= 0)
            agregaInicio(elemento);
        else if (i >= longitud)
            agregaFinal(elemento);
        else {

            Nodo n = getNodo(cabeza.siguiente, 1, i);

            Nodo nuevo = new Nodo(elemento);

            Nodo var = n.anterior;
            n.anterior = nuevo;
            nuevo.siguiente = n;
            nuevo.anterior = var;
            var.siguiente = nuevo;

            longitud++;
        }
    }

    private Nodo getNodo(Nodo n, int contador, int i) {

        while (n != null) {
            if (contador == i)
                return n;

            contador++;
            n = n.siguiente;
        }

        return n;
    }

    /**
     * Elimina un elemento de la lista. Si el elemento no está contenido en la
     * lista, el método no la modifica.
     * @param elemento el elemento a eliminar.
     */
    public void elimina(T elemento) {
         
        if (cabeza == null || elemento == null)
            return;

        Nodo aux = auxElimina(cabeza, elemento);

        if (aux == null)
            return;

        if (cabeza.equals(aux)) {
            eliminaPrimero();
        } else if (rabo.equals(aux)) {
            eliminaUltimo();
        } else {

            aux.siguiente = aux.siguiente;
            aux.siguiente.anterior = aux.anterior;
            aux.anterior = aux.anterior;
            aux.anterior.siguiente = aux.siguiente;

            longitud--;
        }
    }

    private Nodo auxElimina(Nodo n, Object elemento) {

        while (n != null) {
            if (n.elemento.equals(elemento))
                return n;
            n = n.siguiente;
        }
        return n;
    }

    /**
     * Elimina el primer elemento de la lista y lo regresa.
     * @return el primer elemento de la lista antes de eliminarlo.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T eliminaPrimero() {
         
        if (rabo == null)
            throw new NoSuchElementException();

        T o = cabeza.elemento;

        if (rabo.equals(cabeza)) {
            cabeza = rabo = null;
        } else {
            Nodo var = cabeza.siguiente;
            cabeza = var;
            var.anterior = null;
        }

        longitud--;

        return o;
    }

    /**
     * Elimina el último elemento de la lista y lo regresa.
     * @return el último elemento de la lista antes de eliminarlo.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T eliminaUltimo() {
         
        if (cabeza == null)
            throw new NoSuchElementException();

        T o = rabo.elemento;

        if (rabo.equals(cabeza)) {
            cabeza = rabo = null;
        } else {
            Nodo var = rabo.anterior;
            rabo = var;
            var.siguiente = null;
        }

        longitud--;

        return o;
    }

    /**
     * Nos dice si un elemento está en la lista.
     * @param elemento el elemento que queremos saber si está en la lista.
     * @return <code>true</code> si <code>elemento</code> está en la lista,
     *         <code>false</code> en otro caso.
     */
    public boolean contiene(T elemento) {
         
        if (elemento == null || cabeza == null)
            return false;

        return auxContiene(cabeza, elemento);
    }

    private boolean auxContiene(Nodo n, Object elemento) {

        while (n != null) {
            if (n.elemento.equals(elemento))
                return true;

            n = n.siguiente;
        }
        return false;
    }

    /**
     * Regresa la reversa de la lista.
     * @return una nueva lista que es la reversa la que manda llamar el método.
     */
    public Lista<T> reversa() {
         
        Lista<T> listCopia = new Lista<T>();
        if (cabeza == null)
            return listCopia;

        return auxList(listCopia, rabo, 0);
    }

    /**
     * Regresa una copia de la lista. La copia tiene los mismos elementos que la
     * lista que manda llamar el método, en el mismo orden.
     * @return una copiad de la lista.
     */
    public Lista<T> copia() {
         
        if (cabeza == null)
            return new Lista<T>();

        return auxList(new Lista<T>(), cabeza, 1);
    }

    private Lista<T> auxList(Lista<T> listCopia, Nodo n, int opc) {

        while (n != null) {
            if (opc == 0) {
                listCopia.agregaFinal(n.elemento);
                n = n.anterior;
            } else {
                listCopia.agregaFinal(n.elemento);
                n = n.siguiente;
            }
        }

        return listCopia;
    }

    /**
     * Limpia la lista de elementos, dejándola vacía.
     */
    public void limpia() {
         
        cabeza = rabo = null;
        longitud = 0;
    }

    /**
     * Regresa el primer elemento de la lista.
     * @return el primer elemento de la lista.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T getPrimero() {
         
        if (cabeza == null)
            throw new NoSuchElementException();
        else
            return cabeza.elemento;
    }

    /**
     * Regresa el último elemento de la lista.
     * @return el primer elemento de la lista.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T getUltimo() {
         
        if (cabeza == null)
            throw new NoSuchElementException();
        else
            return rabo.elemento;
    }

    /**
     * Regresa el <em>i</em>-ésimo elemento de la lista.
     * @param i el índice del elemento que queremos.
     * @return el <em>i</em>-ésimo elemento de la lista.
     * @throws ExcepcionIndiceInvalido si <em>i</em> es menor que cero o mayor o
     *         igual que el número de elementos en la lista.
     */
    public T get(int i) {
         
        if (cabeza == null || i >= longitud || i < 0)
            throw new ExcepcionIndiceInvalido();
        return auxGet(i);
    }

    private T auxGet(int i) {
        Nodo n = cabeza;

        T result = null;

        int contador = 0;
        while (n != null) {

            if (i == contador) {
                result = n.elemento;
                break;
            }

            contador++;
            n = n.siguiente;
        }

        return result;
    }

    /**
     * Regresa el índice del elemento recibido en la lista.
     * @param elemento el elemento del que se busca el índice.
     * @return el índice del elemento recibido en la lista, o -1 si el elemento
     *         no está contenido en la lista.
     */
    public int indiceDe(T elemento) {
         
        if (elemento == null || cabeza == null)
            return -1;

        return auxIndiceDe(cabeza, elemento);
    }

    private int auxIndiceDe(Nodo n, Object elemento) {
        int contador = 0;
        while (n != null) {

            if (n.elemento.equals(elemento))
                return contador;

            contador++;
            n = n.siguiente;
        }
        return -1;
    }

    /**
     * Regresa una representación en cadena de la lista.
     * @return una representación en cadena de la lista.
     */
    @Override public String toString() {
         
        if (cabeza == null)
            return "[]";

        if (longitud == 1)
            return String.format("[%s]", rabo.elemento);

        String cadena = String.format("[%s, ", cabeza.elemento);

        Nodo n = cabeza.siguiente;

        while (n.siguiente != null) {
            cadena += String.format("%s, ", n.elemento);
            n = n.siguiente;
        }

        cadena += String.format("%s]", rabo.elemento);

        return cadena;
    }

    /**
     * Nos dice si la lista es igual al objeto recibido.
     * @param objeto el objeto con el que hay que comparar.
     * @return <code>true</code> si la lista es igual al objeto recibido;
     *         <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked") Lista<T> lista = (Lista<T>)objeto;
         
        if (longitud != lista.getLongitud())
            return false;

        if (cabeza == null || lista.esVacia())
            return ((cabeza == null) == lista.esVacia());

        return auxEquals(cabeza, lista.cabeza);
    }

    private boolean auxEquals(Nodo n1, Nodo n2) {

        while (n1 != null) {

            if (!n2.elemento.equals(n1.elemento))
                return false;

            n1 = n1.siguiente;
            n2 = n2.siguiente;
        }

        return true;
    }

    /**
     * Regresa un iterador para recorrer la lista en una dirección.
     * @return un iterador para recorrer la lista en una dirección.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Regresa un iterador para recorrer la lista en ambas direcciones.
     * @return un iterador para recorrer la lista en ambas direcciones.
     */
    public IteradorLista<T> iteradorLista() {
        return new Iterador();
    }

    /**
     * Regresa una copia de la lista, pero ordenada. Para poder hacer el
     * ordenamiento, el método necesita una instancia de {@link Comparator} para
     * poder comparar los elementos de la lista.
     * @param comparador el comparador que la lista usará para hacer el
     *                   ordenamiento.
     * @return una copia de la lista, pero ordenada.
     */
    public Lista<T> mergeSort(Comparator<T> comparador) {
         
        return auxMergeSort(this.copia(), comparador);
    }

    private Lista<T> auxMergeSort(Lista<T> lista, Comparator<T> comparador) {

        int l = lista.longitud;

        if(l <= 1)
            return lista;


        Lista<T> l1 = new Lista<T>();
        Lista<T> l2 = new Lista<T>();

        int m = l % 2 == 0 ?  l/2 : (l-1)/2;

        Nodo n = lista.cabeza;
        for(int i = 1; n != null; i++){
            if(i <= m){
                l1.agregaFinal(n.elemento);
            }else{
                l2.agregaFinal(n.elemento);
            }
            n = n.siguiente;
        }

        l1 = auxMergeSort(l1, comparador);
        l2 = auxMergeSort(l2, comparador);

        return merge(l1, l2, comparador);
    }

    private Lista<T> 
            merge(Lista<T> l1, Lista<T> l2, Comparator<T> comparador){
        
        Lista<T> mezcla = new Lista<T>();

        Nodo n1 = l1.cabeza;
        Nodo n2 = l2.cabeza;

        while(n1 != null && n2 != null){
            if(comparador.compare(n1.elemento, n2.elemento) <= 0){
                mezcla.agregaFinal(n1.elemento);
                n1 = n1.siguiente;
            } else{
                mezcla.agregaFinal(n2.elemento);
                n2 = n2.siguiente;
            }
        }

        Nodo n = n1==null ? n2 : n1;
        
        while(n != null){
            mezcla.agregaFinal(n.elemento);
            n = n.siguiente;
        }

        return mezcla;
    }

    /**
     * Regresa una copia de la lista recibida, pero ordenada. La lista recibida
     * tiene que contener nada más elementos que implementan la interfaz {@link
     * Comparable}.
     * @param <T> tipo del que puede ser la lista.
     * @param lista la lista que se ordenará.
     * @return una copia de la lista recibida, pero ordenada.
     */
    public static <T extends Comparable<T>>
    Lista<T> mergeSort(Lista<T> lista) {
        return lista.mergeSort((a, b) -> a.compareTo(b));
    }

    /**
     * Busca un elemento en la lista ordenada, usando el comparador recibido. El
     * método supone que la lista está ordenada usando el mismo comparador.
     * @param elemento el elemento a buscar.
     * @param comparador el comparador con el que la lista está ordenada.
     * @return <code>true</code> si el elemento está contenido en la lista,
     *         <code>false</code> en otro caso.
     */
    public boolean busquedaLineal(T elemento, Comparator<T> comparador) {
         
        Nodo n = cabeza;

        while(n != null){
            if(comparador.compare(n.elemento, elemento) == 0)
                return true;
            n = n.siguiente;
        }
        return false;
    }

    /**
     * Busca un elemento en una lista ordenada. La lista recibida tiene que
     * contener nada más elementos que implementan la interfaz {@link
     * Comparable}, y se da por hecho que está ordenada.
     * @param <T> tipo del que puede ser la lista.
     * @param lista la lista donde se buscará.
     * @param elemento el elemento a buscar.
     * @return <code>true</code> si el elemento está contenido en la lista,
     *         <code>false</code> en otro caso.
     */
    public static <T extends Comparable<T>>
    boolean busquedaLineal(Lista<T> lista, T elemento) {
        return lista.busquedaLineal(elemento, (a, b) -> a.compareTo(b));
    }
}
