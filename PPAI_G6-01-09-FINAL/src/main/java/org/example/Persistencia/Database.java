//Este archivo es el PUENTE entre tu aplicación Java y una base de datos real.
    // Vistas → GestorRI → DAO/Repositorios → Database → Base de datos

package org.example.Persistencia;
import java.sql.Connection;
// Connection representa una conexión abierta con una base de datos (puerta abierta para ejecutar SQL).

import java.sql.DriverManager;
// DriverManager es el “administrador” que sabe crear una Connection según una URL JDBC.

import java.sql.SQLException;
// SQLException es la excepción típica cuando falla algo de SQL (conexión, query, etc.).

public class Database {
    //Todo se guardará en un archivo local, NO hay servido la base es un archivo donde todos los datos de prueba saldrán de ahí.
    private static final String URL = "jdbc:sqlite:ppai.db";
    //sqlite-jdbc: Es el DRIVER que traduce: llamadas Java a comandos SQL de SQLite/Postgres.
    //DbInit.java: Crea las tablas la primera vez. Evita error “tabla no existe”.

    public static Connection getConnection() throws SQLException {
        // Método estático: no se necesita instanciar Database para pedir una conexión.
        // "throws SQLException" porque DriverManager puede fallar al conectar.

        return DriverManager.getConnection(URL);
        // Devuelve una Connection lista para ejecutar SQL contra la BD indicada por URL.
    }
}
