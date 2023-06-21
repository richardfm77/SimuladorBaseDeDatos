# Simulador de una base de datos en red (tarjetas bancarias).
[![License](https://img.shields.io/badge/license-GPLv2-blue.svg)](https://wordpress.org/about/license/)

Simula una base de datos en red sobre tarjetas bacarias, con interfaz gráficia hecha en java fx. 

El programa maneja el modelo cliente-servidor, vista-controlador, y es capaz de hacer todas las operaciones de un **CRUD**.

Es decir, se proveé de un cliente y un servidor.

Desarrollado con JAVA 10.

## Pre-requisitos.
**_El programa hace uso del empaquetador MAVEN 3.8.6 o superior._**

* Linux.
```sh
  https://maven.apache.org/download.cgi
```

## Instalación.

1. Clonar el repositorio.
```sh
   git clone https://github.com/richardfm77/SimuladorBaseDeDatos.git
```
```sh
   cd SimuladorBaseDeDatos/proyecto3
```

2. Compilar.
```sh
   mvn compile
```

3. Correr pruebas.
```sh
   mvn test
```

## Ejecucíon.

* Instalación de los archivos **.jar**.
```sh
   mvn install
```

* Ejecutar servidor.
```sh
   ./bin/servidor-proyecto3 [P]
```
Donde **[P]** es el puerto de escucha del servidor. Por ejemplo *8080*.

* Ejecutar cliente.
```sh
   ./bin/cliente-proyecto3
```
Se puede ejecutar y conectar tantos clientes como se requiera.

## Uso del cliente.

Para conectar el cliente se requiere que el servidor ya esté funcionando en un cierto puerto. Por ejemplo *8080* (Se pueden conectar tantos clientes como se requiera).

1. De click en Base de Datos seguido de Conectar.

2. Introduzada la *ip* del servidor y el puerto de escucha.

3. Ya puede hacer operaciones *CRUD* con la base de datos.
