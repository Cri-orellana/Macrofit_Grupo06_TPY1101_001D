package com.proyecto.macrofit.usuarios.model.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Objetivo")
public class ObjetivoEntity {

    @Id
    private Integer id_objetivo;

    @Column(name = "descrip_obj")
    private String descrip_obj;

    private float ajuste_calorico;

    public ObjetivoEntity() {
    }

    public ObjetivoEntity(Integer id_objetivo, String descrip_obj, float ajuste_calorico) {
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

    public float getAjuste_calorico() {
        return ajuste_calorico;
    }

    public void setAjuste_calorico(float ajuste_calorico) {
        this.ajuste_calorico = ajuste_calorico;
    }
}