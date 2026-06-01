package com.proyecto.macrofit.usuarios.model.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Nv_Actividad")
public class NvActividadEntity {

    @Id
    private Integer id_nv_act;

    @Column(name = "desc_nv_act")
    private String desc_nv_act;

    private float multiplicador;

    public NvActividadEntity() {
    }

    public NvActividadEntity(Integer id_nv_act, String desc_nv_act, float multiplicador) {
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

    public float getMultiplicador() {
        return multiplicador;
    }

    public void setMultiplicador(float multiplicador) {
        this.multiplicador = multiplicador;
    }
}