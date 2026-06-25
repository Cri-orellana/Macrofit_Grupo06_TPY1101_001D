package com.proyecto.macrofit.usuarios.service;

import org.springframework.stereotype.Service;

/**
 * LibreTranslate deshabilitado temporalmente.
 * El servidor no está disponible,
 * por lo que se devuelve el texto original en inglés.
 *
 * Para reactivar: restaurar la implementación HTTP
 * cuando el servidor esté disponible.
 */
@Service
public class LibreTranslateService {

    public String traducirEnEsp(String textoIngles) {
        if (textoIngles == null || textoIngles.isBlank())
            return "";
        return textoIngles.replaceAll("<[^>]*>", "").trim();
    }
}