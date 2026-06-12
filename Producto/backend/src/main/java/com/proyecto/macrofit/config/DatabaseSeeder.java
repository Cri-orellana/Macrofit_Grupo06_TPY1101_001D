package com.proyecto.macrofit.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.proyecto.macrofit.usuarios.model.ComidaRecomendada;
import com.proyecto.macrofit.usuarios.model.NvActividad;
import com.proyecto.macrofit.usuarios.model.Objetivo;
import com.proyecto.macrofit.usuarios.model.TipoAlimentacion;

import com.proyecto.macrofit.usuarios.repository.ComidaRecomendadaRepository;
import com.proyecto.macrofit.usuarios.repository.NvActividadRepository;
import com.proyecto.macrofit.usuarios.repository.ObjetivoRepository;
import com.proyecto.macrofit.usuarios.repository.TipoAlimentacionRepository;

@Component
public class DatabaseSeeder implements CommandLineRunner {

        private final TipoAlimentacionRepository tipoAlimentacionRepo;

        private final ComidaRecomendadaRepository comidaRepo;

        private final ObjetivoRepository objetivoRepo;

        private final NvActividadRepository actividadRepo;

        public DatabaseSeeder(TipoAlimentacionRepository tipoAlimentacionRepo,
                        ComidaRecomendadaRepository comidaRepo,
                        ObjetivoRepository objetivoRepo,
                        NvActividadRepository actividadRepo) {
                this.tipoAlimentacionRepo = tipoAlimentacionRepo;
                this.comidaRepo = comidaRepo;
                this.objetivoRepo = objetivoRepo;
                this.actividadRepo = actividadRepo;
        }

        @Override
        public void run(String... args) throws Exception {
                System.out.println("Verificando catálogos de base de datos...");

                if (objetivoRepo.count() == 0) {
                        System.out.println("Tabla Objetivo vacía. Insertando datos por defecto...");
                        objetivoRepo.saveAll(Arrays.asList(
                                        new Objetivo("Bajar peso (Déficit)", -500f),
                                        new Objetivo("Mantener peso", 0f),
                                        new Objetivo("Subir masa muscular (Volumen)", 500f)));
                }

                if (actividadRepo.count() == 0) {
                        System.out.println("Tabla Nv_Actividad vacía. Insertando datos por defecto...");
                        actividadRepo.saveAll(Arrays.asList(
                                        new NvActividad("Sedentario (Poco o ningún ejercicio)", 1.1f),
                                        new NvActividad("Ligero (Ejercicio 1-3 días/sem)", 1.375f),
                                        new NvActividad("Moderado (Ejercicio 3-5 días/sem)", 1.55f),
                                        new NvActividad("Intenso (Ejercicio 6-7 días/sem)", 1.725f),
                                        new NvActividad("Muy intenso (Ejercicio diario intenso o trabajo físico)",
                                                        1.9f)));
                }

                if (tipoAlimentacionRepo.count() == 0) {
                        tipoAlimentacionRepo.saveAll(Arrays.asList(
                                        new TipoAlimentacion("Omnívora"),
                                        new TipoAlimentacion("Vegetariana"),
                                        new TipoAlimentacion("Vegana"),
                                        new TipoAlimentacion("Keto")));
                }

                if (comidaRepo.count() == 0) {
                        TipoAlimentacion omni = tipoAlimentacionRepo.findAll().stream()
                                        .filter(t -> t.getNombre_tipo().equals("Omnívora")).findFirst().orElse(null);
                        TipoAlimentacion vegan = tipoAlimentacionRepo.findAll().stream()
                                        .filter(t -> t.getNombre_tipo().equals("Vegana")).findFirst().orElse(null);

                        if (omni != null && vegan != null) {
                                comidaRepo.saveAll(Arrays.asList(
                                                new ComidaRecomendada("Pollo con Quínoa y Brócoli",
                                                                "Pechuga a la plancha con base de quínoa y vegetales al vapor.",
                                                                450f, 40f, 50f, 10f, omni),
                                                new ComidaRecomendada("Bowl de Garbanzos y Camote",
                                                                "Garbanzos especiados con camote asado, espinaca y sésamo.",
                                                                520f, 18f, 75f, 12f, vegan)));
                        }
                }

                System.out.println("Catálogos listos y verificados.");
        }
}