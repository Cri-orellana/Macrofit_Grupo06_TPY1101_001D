package com.proyecto.macrofit.usuarios.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Objetivo")
public class Objetivo {
    @Id
    @Column(name = "id_objetivo")
    private Integer id_objetivo;

    @Column(name = "descrip_obj")
    private String descrip_obj;

    @Column(name = "ajuste_calorico")
    private Float ajuste_calorico;

    public Objetivo() {
    }

    public Objetivo(Integer id_objetivo, String descrip_obj, Float ajuste_calorico) {
        this.id_objetivo = id_objetivo;
        this.descrip_obj = descrip_obj;
        this.ajuste_calorico = ajuste_calorico;
    }

    public Integer getId_objetivo() {
        return id_objetivo;
    }

    public void setId_objetivo(Integer id_objetivo) {
        this.id_objetivo = id_objetivo;
    }

    public String getDescrip_obj() {
        return descrip_obj;
    }

    public void setDescrip_obj(String descrip_obj) {
        this.descrip_obj = descrip_obj;
    }

    public Float getAjuste_calorico() {
        return ajuste_calorico;
    }

    public void setAjuste_calorico(Float ajuste_calorico) {
        this.ajuste_calorico = ajuste_calorico;
    }
}