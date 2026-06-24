package com.proyecto.macrofit.usuarios.model;

public class LoginResponse {

    private String token;
    private Integer id_usuario;
    private String nom_usuario;
    private String correo;
    private String rol;

    public LoginResponse(String token, Integer id_usuario,
            String nom_usuario, String correo, String rol) {
        this.token = token;
        this.id_usuario = id_usuario;
        this.nom_usuario = nom_usuario;
        this.correo = correo;
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public Integer getId_usuario() {
        return id_usuario;
    }

    public String getNom_usuario() {
        return nom_usuario;
    }

    public String getCorreo() {
        return correo;
    }

    public String getRol() {
        return rol;
    }
}