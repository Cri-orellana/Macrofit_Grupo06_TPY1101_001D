package com.duoc.macrofit.utils

import android.util.Log

object TraduccionUtils {

    private val diccionario = mapOf(
        // Zonas Musculares
        "chest" to "Pecho",
        "back" to "Espalda",
        "shoulders" to "Hombros",
        "arms" to "Brazos",
        "legs" to "Piernas",
        "abs" to "Abdominales",
        "core" to "Núcleo",
        "full body" to "Cuerpo Completo",
        "lower body" to "Tren Inferior",
        "upper body" to "Tren Superior",
        "cardio" to "Cardio",
        "quads" to "Cuádriceps",
        "quadriceps" to "Cuádriceps",
        "hamstrings" to "Isquiotibiales",
        "glutes" to "Glúteos",
        "calves" to "Pantorrillas",
        "forearms" to "Antebrazos",
        "traps" to "Trapecios",
        "lats" to "Dorsales",
        "lower back" to "Lumbar",
        "middle back" to "Espalda media",

        // Ejercicios Comunes
        "push ups" to "Flexiones de brazos",
        "pull ups" to "Dominadas",
        "squats" to "Sentadillas",
        "lunges" to "Estocadas",
        "plank" to "Plancha",
        "bench press" to "Press de banca",
        "deadlift" to "Peso muerto",
        "bicep curls" to "Curls de bíceps",
        "tricep dips" to "Fondos de tríceps",
        "shoulder press" to "Press de hombros",
        "running" to "Correr",
        "jumping jacks" to "Saltos de tijera",
        "burpees" to "Burpees",
        "crunches" to "Abdominales",
        "leg raises" to "Elevación de piernas",
        "mountain climbers" to "Escaladores",
        "bicycle crunches" to "Abdominales de bicicleta",
        "tricep extensions" to "Extensiones de tríceps",
        "hammer curls" to "Curls de martillo",
        "lateral raises" to "Elevaciones laterales",
        "front raises" to "Elevaciones frontales",
        "face pulls" to "Face pulls",
        "rows" to "Remos",
        "bent over rows" to "Remo inclinado",
        "lat pulldowns" to "Jalón al pecho",
        "leg press" to "Prensa de piernas",
        "leg extensions" to "Extensiones de piernas",
        "leg curls" to "Curls de piernas",
        "calf raises" to "Elevación de pantorrillas",
        "hip thrusts" to "Empuje de cadera",

        // Implementos
        "none" to "Ninguno",
        "bodyweight" to "Peso corporal",
        "dumbbells" to "Mancuernas",
        "barbell" to "Barra",
        "kettlebell" to "Pesa rusa",
        "resistance band" to "Banda de resistencia",
        "bench" to "Banca",
        "machine" to "Máquina",

        // Niveles
        "beginner" to "Principiante",
        "intermediate" to "Intermedio",
        "advanced" to "Avanzado",

        // Ingredientes y Nutrición (Migaku)
        "onion" to "Cebolla",
        "garlic" to "Ajo",
        "tomato" to "Tomate",
        "pepper" to "Pimiento",
        "carrot" to "Zanahoria",
        "potato" to "Papa",
        "lettuce" to "Lechuga",
        "mushroom" to "Champiñón",
        "pork" to "Cerdo",
        "lamb" to "Cordero",
        "fish" to "Pescado",
        "shrimp" to "Camarón",
        "prawn" to "Gamba",
        "salt" to "Sal",
        "butter" to "Mantequilla",
        "flour" to "Harina",
        "sugar" to "Azúcar",
        "vinegar" to "Vinagre",
        "honey" to "Miel",
        "cream" to "Crema",
        "chicken breast" to "Pechuga de pollo",
        "chicken" to "Pollo",
        "brown rice" to "Arroz integral",
        "white rice" to "Arroz blanco",
        "eggs" to "Huevos",
        "egg" to "Huevo",
        "oats" to "Avena",
        "milk" to "Leche",
        "yogurt" to "Yogur",
        "greek yogurt" to "Yogur griego",
        "cottage cheese" to "Queso cottage",
        "spinach" to "Espinacas",
        "broccoli" to "Brócoli",
        "salmon" to "Salmón",
        "tuna" to "Atún",
        "banana" to "Plátano",
        "apple" to "Manzana",
        "avocado" to "Palta / Aguacate",
        "olive oil" to "Aceite de oliva",
        "peanut butter" to "Mantequilla de maní",
        "sweet potato" to "Camote / Batata",
        "quinoa" to "Quinua",
        "turkey" to "Pavo",
        "beef" to "Carne de vacuno",
        "almonds" to "Almendras",
        "water" to "Agua",
        "black beans" to "Porotos negros",
        "lentils" to "Lentejas",
        "whole wheat bread" to "Pan integral",
        "protein powder" to "Proteína en polvo",
        "creatine" to "Creatina",

        // Verbos y Acciones de Cocina (Migaku)
        "chop" to "Picar",
        "slice" to "Rebanar",
        "dice" to "Cortar en cubitos",
        "mince" to "Picar finamente",
        "peel" to "Pelar",
        "grate" to "Rallar",
        "boil" to "Hervir",
        "simmer" to "Cocinar a fuego lento",
        "fry" to "Freír",
        "sauté" to "Saltear",
        "bake" to "Hornear",
        "roast" to "Asar",
        "grill" to "Asar a la parrilla",
        "steam" to "Cocer al vapor",
        "stew" to "Estofar",
        "broil" to "Gratinar",
        "stir" to "Revolver",
        "whisk" to "Batir",
        "knead" to "Amasar",
        "mix" to "Mezclar",
        "blend" to "Licuar",
        "spread" to "Untar",
        "drain" to "Escurrir",
        "grease" to "Engrasar",
        "season" to "Sazonar",

        // Tipos de Dieta
        "vegan" to "Vegano",
        "vegetarian" to "Vegetariano",
        "keto" to "Keto / Cetogénica",
        "paleo" to "Paleo",
        "low carb" to "Bajo en carbohidratos",
        "mediterranean" to "Mediterránea",
        "gluten free" to "Sin gluten",
        "dairy free" to "Sin lácteos",

        // Varios
        "salad" to "Ensalada",
        "soup" to "Sopa",
        "grilled" to "Asado",
        "roasted" to "Rostizado"
    )

    fun traducir(termino: String?): String {
        Log.d("TRADUCCION_DEBUG", "Tratando de traducir: '$termino'")
        println("TRADUCCION_STDOUT: Tratando de traducir: '$termino'")
        if (termino == null || termino.isBlank()) return ""
        val normalizado = termino.lowercase().trim()
        
        // 1. Intentar traducción exacta
        diccionario[normalizado]?.let { 
            Log.d("TRADUCCION_DEBUG", "Encontrado exacto: $it")
            return it 
        }

        // 2. Intentar traducción por palabras
        val palabras = termino.split(" ")
        if (palabras.size > 1) {
            val traducido = palabras.joinToString(" ") { palabra ->
                val pNormalizada = palabra.lowercase().replace(",", "").replace(".", "").trim()
                diccionario[pNormalizada] ?: palabra
            }
            if (traducido != termino) {
                Log.d("TRADUCCION_DEBUG", "Traducido por partes: $traducido")
                return traducido.replaceFirstChar { it.uppercase() }
            }
        }

        Log.w("TRADUCCION_DEBUG", "SIN TRADUCCION para: '$termino'")
        return termino.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    fun traducirAlIngles(termino: String?): String? {
        Log.d("TRADUCCION_DEBUG", "Traducir al INGLES: '$termino'")
        if (termino == null || termino.isBlank()) return null
        val normalizado = termino.lowercase().trim()
        
        for ((ingles, espanol) in diccionario) {
            if (espanol.lowercase() == normalizado) {
                Log.d("TRADUCCION_DEBUG", "Encontrado inverso: $ingles")
                return ingles
            }
        }

        // Intento por palabras para búsqueda
        val palabras = termino.split(" ")
        if (palabras.size > 1) {
            val result = palabras.joinToString(" ") { palabra ->
                var encontrada = palabra
                for ((ingles, espanol) in diccionario) {
                    if (espanol.lowercase() == palabra.lowercase()) {
                        encontrada = ingles
                        break
                    }
                }
                encontrada
            }
            return result
        }

        return termino
    }

    fun traducirLista(lista: List<String>?): List<String> {
        return lista?.map { traducir(it) } ?: emptyList()
    }
}
