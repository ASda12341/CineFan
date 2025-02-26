package com.cinefan.utilidades;

import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * Clase de utilidad para validar datos de entrada
 */
public class ValidadorDatos {

    /**
     * Valida si un año es válido
     * @param anioStr Cadena que representa el año a validar
     * @return true si es un año válido, false en caso contrario
     */
    public static boolean esAnioValido(String anioStr) {
        try {
            // Comprobar que es un número
            int anio = Integer.parseInt(anioStr);
            
            // Comprobar que está en un rango razonable (desde 1888, primer año de cine, hasta el año actual + 5)
            int anioActual = Calendar.getInstance().get(Calendar.YEAR);
            int anioMinimo = 1888; // Primer año con película registrada
            int anioMaximo = anioActual + 5; // Permitir películas programadas para los próximos 5 años
            
            return anio >= anioMinimo && anio <= anioMaximo;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida si un correo electrónico es válido
     * @param correo Correo electrónico a validar
     * @return true si es un correo válido, false en caso contrario
     */
    public static boolean esCorreoValido(String correo) {
        // Patrón simple para validar correo electrónico
        String patronCorreo = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return Pattern.compile(patronCorreo).matcher(correo).matches();
    }

    /**
     * Valida si una contraseña es segura
     * @param contrasena Contraseña a validar
     * @return true si es una contraseña segura, false en caso contrario
     */
    public static boolean esContrasenaSegura(String contrasena) {
        // Al menos 8 caracteres, una letra mayúscula, una minúscula y un número
        String patronContrasena = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
        return Pattern.compile(patronContrasena).matcher(contrasena).matches();
    }

    /**
     * Valida si un nombre de usuario es válido
     * @param nombreUsuario Nombre de usuario a validar
     * @return true si es un nombre de usuario válido, false en caso contrario
     */
    public static boolean esNombreUsuarioValido(String nombreUsuario) {
        // Entre 3 y 20 caracteres, letras, números y guiones bajos
        String patronNombreUsuario = "^[a-zA-Z0-9_]{3,20}$";
        return Pattern.compile(patronNombreUsuario).matcher(nombreUsuario).matches();
    }

    /**
     * Valida si una valoración es válida
     * @param valoracionStr Cadena que representa la valoración
     * @return true si es una valoración válida, false en caso contrario
     */
    public static boolean esValoracionValida(String valoracionStr) {
        try {
            float valoracion = Float.parseFloat(valoracionStr);
            return valoracion >= 0 && valoracion <= 10;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
