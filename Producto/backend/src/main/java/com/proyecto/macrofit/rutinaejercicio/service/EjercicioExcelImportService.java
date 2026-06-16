package com.proyecto.macrofit.rutinaejercicio.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.proyecto.macrofit.rutinaejercicio.model.Ejercicio;

@Service
public class EjercicioExcelImportService {

    @Autowired
    private EjercicioService ejercicioService;

    public int importarEjercicios(MultipartFile archivo) {
        int importados = 0;

        try (InputStream inputStream = archivo.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet hoja = workbook.getSheet("Exercises");

            if (hoja == null) {
                throw new RuntimeException("No se encontró la hoja 'Exercises' en el Excel.");
            }

            Row filaEncabezado = hoja.getRow(15); // Fila 16 en Excel

            if (filaEncabezado == null) {
                throw new RuntimeException("No se encontró la fila de encabezados.");
            }

            Map<String, Integer> columnas = obtenerColumnas(filaEncabezado);

            validarColumna(columnas, "Exercise");
            validarColumna(columnas, "Short YouTube Demonstration");
            validarColumna(columnas, "Difficulty Level");
            validarColumna(columnas, "Target Muscle Group");
            validarColumna(columnas, "Primary Equipment");
            validarColumna(columnas, "Body Region");

            for (int i = 16; i <= hoja.getLastRowNum(); i++) {
                Row fila = hoja.getRow(i);

                if (fila == null) {
                    continue;
                }

                String nombreEjercicio = obtenerTexto(fila, columnas, "Exercise");

                if (nombreEjercicio == null || nombreEjercicio.isBlank()) {
                    continue;
                }

                String videoEjercicio = obtenerTextoOHipervinculo(fila, columnas, "Short YouTube Demonstration");

                if (videoEjercicio == null || videoEjercicio.isBlank()) {
                    continue;
                }

                Ejercicio ejercicio = new Ejercicio();

                ejercicio.setIdEjercicio(null);

                ejercicio.setNombreEjercicio(nombreEjercicio);
                ejercicio.setVideoEjercicio(obtenerTextoOHipervinculo(fila, columnas, "Short YouTube Demonstration"));
                ejercicio.setNivelDificultad(traducirDificultad(obtenerTexto(fila, columnas, "Difficulty Level")));
                ejercicio.setMusculoObjetivo(traducirMusculo(obtenerTexto(fila, columnas, "Target Muscle Group")));

                String implementoTraducido = traducirImplemento(
                        obtenerTexto(fila, columnas, "Primary Equipment")
                );

                if (implementoTraducido == null) {
                    continue; 
                }

                ejercicio.setImplemento(implementoTraducido);
                ejercicio.setZonaMuscular(traducirZonaMuscular(obtenerTexto(fila, columnas, "Body Region")));

                ejercicio.setDescripcion(null);
                ejercicio.setImagenEjercicio(null);
                ejercicio.setActivoCatalogo(true);

                ejercicioService.crearEjercicio(ejercicio);
                importados++;
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al importar ejercicios desde Excel: " + e.getMessage(), e);
        }

        return importados;
    }

    private Map<String, Integer> obtenerColumnas(Row filaEncabezado) {
        Map<String, Integer> columnas = new HashMap<>();
        DataFormatter formatter = new DataFormatter();

        for (Cell celda : filaEncabezado) {
            String nombreColumna = formatter.formatCellValue(celda);

            if (nombreColumna != null && !nombreColumna.isBlank()) {
                columnas.put(nombreColumna.trim(), celda.getColumnIndex());
            }
        }

        return columnas;
    }

    private void validarColumna(Map<String, Integer> columnas, String nombreColumna) {
        if (!columnas.containsKey(nombreColumna)) {
            throw new RuntimeException("Falta la columna obligatoria: " + nombreColumna);
        }
    }

    private String obtenerTexto(Row fila, Map<String, Integer> columnas, String nombreColumna) {
        Integer index = columnas.get(nombreColumna);

        if (index == null) {
            return null;}

        Cell celda = fila.getCell(index);

        if (celda == null) {
            return null;}

        DataFormatter formatter = new DataFormatter();
        String valor = formatter.formatCellValue(celda);

        if (valor == null || valor.trim().isEmpty()) {
            return null;}

        return valor.trim();
    }

    private String traducirDificultad(String valor) {
        if (valor == null) return null;

        return switch (valor.trim().toLowerCase()) {
            case "novice" -> "Sedentario";
            case "beginner" -> "Ligero";
            case "intermediate" -> "Moderado";
            case "advanced" -> "Intenso";
            case "expert" -> "Intenso";
            default -> capitalizar(valor);
        };
    }

    private String traducirZonaMuscular(String valor) {
        if (valor == null) return null;

        return switch (valor.trim().toLowerCase()) {
            case "upper body" -> "Tren superior";
            case "lower body" -> "Tren inferior";
            case "core" -> "Core";
            case "full body" -> "Cuerpo completo";
            default -> capitalizar(valor);
        };
    }

    private String traducirMusculo(String valor) {
        if (valor == null) return null;

        return switch (valor.trim().toLowerCase()) {
            case "abdominals" -> "Abdominales";
            case "glutes" -> "Glúteos";
            case "hamstrings" -> "Isquiotibiales";
            case "quadriceps" -> "Cuádriceps";
            case "chest" -> "Pecho";
            case "back" -> "Espalda";
            case "shoulders" -> "Hombros";
            case "biceps" -> "Bíceps";
            case "triceps" -> "Tríceps";
            case "calves" -> "Pantorrillas";
            case "forearms" -> "Antebrazos";
            case "adductors" -> "Aductores";
            case "abductors" -> "Abductores";
            case "hip flexors" -> "Flexores de cadera";
            case "erector spinae" -> "Erectores espinales";
            case "lats" -> "Dorsales";
            case "traps" -> "Trapecios";
            default -> capitalizar(valor);
        };
    }

    private String traducirImplemento(String valor) {
    if (valor == null || valor.isBlank()) {
        return null;
    }

    return switch (valor.trim().toLowerCase()) {
        case "ab wheel" -> "Rueda abdominal";
        case "barbell" -> "Barra";
        case "battle ropes" -> "Cuerdas de batalla";
        case "bodyweight" -> "Peso corporal";
        case "bulgarian bag" -> "Saco búlgaro";
        case "cable" -> "Polea";
        case "climbing rope" -> "Cuerda de escalada";
        case "dumbbell" -> "Mancuerna";
        case "ez bar" -> "Barra EZ";
        case "gymnastic rings" -> "Anillas gimnásticas";
        case "heavy sandbag" -> "Saco de arena";
        case "kettlebell" -> "Kettlebell";
        case "landmine" -> "Landmine";
        case "medicine ball" -> "Balón medicinal";
        case "miniband" -> "Minibanda";
        case "parallette bars" -> "Barras paralelas";
        case "pull up bar" -> "Barra de dominadas";
        case "resistance band" -> "Banda elástica";
        case "sandbag" -> "Saco de arena";
        case "slamball" -> "Balón de golpeo";
        case "stability ball" -> "Balón de estabilidad";
        case "superband" -> "Superbanda";
        case "suspension trainer" -> "TRX";
        case "tire" -> "Neumático";
        case "trap bar" -> "Barra hexagonal";
        case "wall ball" -> "Balón medicinal";
        case "weight plate" -> "Disco de peso";
        default -> null;
    };
}

    //Leer hipervinculo
    private String obtenerTextoOHipervinculo(Row fila, Map<String, Integer> columnas, String nombreColumna) {
        Integer index = columnas.get(nombreColumna);

        if (index == null) {
            return null;}
        Cell celda = fila.getCell(index);

        if (celda == null) {
            return null;}
        Hyperlink hyperlink = celda.getHyperlink();

        if (hyperlink != null && hyperlink.getAddress() != null && !hyperlink.getAddress().isBlank()) {
            return hyperlink.getAddress().trim();}

        DataFormatter formatter = new DataFormatter();
        String valor = formatter.formatCellValue(celda);

        if (valor == null || valor.trim().isEmpty()) {
            return null;}
        return valor.trim();
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isBlank()) {
            return texto;
        }

        String limpio = texto.trim().toLowerCase();
        return limpio.substring(0, 1).toUpperCase() + limpio.substring(1);
    }
}