/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.*;
import java.sql.*;

/**
 *
 * @author Iván Maldonado Fernandez
 */
public class Jugador {

    private int id;
    private String nombre;
    private String apellidos;
    private int edad;
    private int idEquipo;

    public Jugador() {
        
    }
    
    public Jugador(int id) {
        this.id = id;
    }

    public Jugador(String nombre, String apellidos, int edad) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.edad = edad;
    }

    public Jugador(int id, String nombre, String apellidos, int edad) {
        this(nombre, apellidos, edad);
        this.id = id;
    }

    public Jugador(String nombre, String apellidos, int edad, int idEquipo) {
        this(nombre, apellidos, edad);
        this.idEquipo = idEquipo;
    }

    public Jugador(int id, String nombre, String apellidos, int edad, int idEquipo) {
        this(id, nombre, apellidos, edad);
        this.idEquipo = idEquipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    // --------- OPERACIONES BD ----------------------------------------
    // ---------- CRUD BÁSICO
    public boolean create() {
        boolean exito = true;
        try (Connection conn = ConexionBd.obtener()){
            String sql = "INSERT INTO jugador(nombre, apellidos, edad, idequipo)"
                    + " values(? , ? , ? , ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1, getNombre());
                stmt.setString(2, getApellidos());
                stmt.setInt(3, getEdad());
                stmt.setInt(4, getIdEquipo());
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            exito = false;
        }
        return exito;
    }

    public boolean retrieve() {
        boolean exito = true;
        try (Connection conn = ConexionBd.obtener()) {
            String sql = "SELECT nombre, apellidos, edad, idequipo FROM jugador WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1, this.id);
                try (ResultSet rs = stmt.executeQuery()){
                    rs.next();
                    setNombre(rs.getString("nombre"));
                    setApellidos(rs.getString("apellidos"));
                    setEdad(rs.getInt("edad"));
                    setIdEquipo(rs.getInt("idequipo"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            exito = false;
        }
        return exito;
    }

    public boolean update() {
        boolean exito = true;
        try (Connection conn = ConexionBd.obtener()) {
            String sql = "UPDATE jugador SET nombre = ?, apellidos = 0, "
                    + "edad = 0, idequip = ?, WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1, getNombre());
                stmt.setString(2, getApellidos());
                stmt.setInt(3, getEdad());
                stmt.setInt(4, getIdEquipo());
                stmt.setInt(5, this.id);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            exito = false;
        }
        return exito;
    }

    public boolean delete() {
        boolean exito = true;
        try(Connection conn = ConexionBd.obtener()){
            String sql = "DELETE FROM jugador WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1, this.id);
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            exito = false;
        }
        return exito;
    }

    // ----------- Otras, de clase, no relacionadas con ÉSTE (this) objeto
    public static List<Jugador> obtenerJugadores(String busqueda, boolean esJunior, boolean esClass, boolean esMaster) {
        List<Jugador> resultado = new ArrayList<>();
        String sql = "SELECT id, nombre, apellidos, edad, idequipo FROM jugador ";
        String junior = " edad < 18 ";
        String classS = " edad >= 26 AND edad < 26 ";
        String master = " edad >= 26 ";
        //Si contiene busqueda
        if (!busqueda.equalsIgnoreCase("")){
            sql = sql + "WHERE nombre LIKE(?) OR apellidos LIKE(?) ";
            //Si es Junior
            if (esJunior){
                sql = sql + " AND " + junior; 
            //Si es Class
            } else if(esClass) {
                sql = sql + " AND " + classS;   
            //Si es Master
            } else if(esMaster) {
                sql = sql + " AND " + master;
            }
        //Si no contiene busqueda
        } else {
            //Si es Junior
            if (esJunior){
                sql = sql + " WHERE " + junior;
            //Si es Class
            } else if(esClass) {
                sql = sql + " WHERE " + classS;
            //Si es Master
            } else if(esMaster) {
                sql = sql + " WHERE " + master;
            }
        }
        System.err.println(sql);
        try (Connection conn = ConexionBd.obtener()){
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "%" + busqueda + "%");
                stmt.setString(2, "%" + busqueda + "%");
                try(ResultSet rs = stmt.executeQuery()){
                    while(rs.next()){
                        resultado.add(
                            new Jugador(
                                rs.getInt("id"),
                                rs.getString("nombre"),
                                rs.getString("apellidos"),
                                rs.getInt("edad"),
                                rs.getInt("idequipo")
                            )
                        );
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        
        return resultado;
    }
}
