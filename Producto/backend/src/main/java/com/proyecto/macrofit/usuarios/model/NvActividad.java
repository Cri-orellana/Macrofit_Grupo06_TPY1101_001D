package com.proyecto.macrofit.usuarios.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Nv_Actividad")
public class NvActividad {
    @Id
    @Column(name = "id_nv_act")
    private Integer id_nv_act;

    @Column(name = "desc_nv_act")
    private String desc_nv_act;

    @Column(name = "multiplicador")
    private Float multiplicador;

    public NvActividad() {
    }

    public NvActividad(Integer id_nv_act, String desc_nv_act, Float multiplicador) {
        this.id_nv_act = id_nv_act;
        this.desc_nv_act = desc_nv_act;
        this.multiplicador = multiplicador;
    }

    public Integer getId_nv_act() {
        return id_nv_act;
    }

    public void setId_nv_act(Integer id_nv_act) {
        this.id_nv_act = id_nv_act;
    }

    public String getDesc_nv_act() {
        return desc_nv_act;
    }

    public void setDesc_nv_act(String desc_nv_act) {
        this.desc_nv_act = desc_nv_act;
    }

    public Float getMultiplicador() {
        return multiplicador;
    }

    public void setMultiplicador(Float multiplicador) {
        this.multiplicador = multiplicador;
    }
}