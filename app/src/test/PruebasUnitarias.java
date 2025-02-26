package com.cinefan.test;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cinefan.datos.BaseDatosHelper;
import com.cinefan.datos.PeliculaDAO;
import com.cinefan.modelo.Pelicula;
import com.cinefan.utilidades.ValidadorDatos;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Pruebas unitarias para la aplicación CineFan
 */
@RunWith(AndroidJUnit4.class)
public class PruebasUnitarias {

    private Context contexto;
    private PeliculaDAO peliculaDAO;
    private SQLiteDatabase db;

    @Before
    public void setUp() {
        // Configurar entorno de prueba
        contexto = ApplicationProvider.getApplicationContext();
        
        // Usar base de datos en memoria para pruebas
        BaseDatosHelper dbHelper = new BaseDatosHelper(contexto);
        db = dbHelper.getWritableDatabase();
        
        // Inicializar DAO
        peliculaDAO = new PeliculaDAO(contexto);
        peliculaDAO.abrir();
    }

    @After
    public void tearDown() {
        // Liberar recursos
        peliculaDAO.cerrar();
        db.close();
    }

    /**
     * Prueba 1: Inserción de película
     */
    @Test
    public void testInsertarPelicula() {
        // Crear película de prueba
        Pelicula pelicula = new Pelicula(
                "Película de prueba",
                2023,
                "Director de prueba",
                "Acción",
                "Sinopsis de prueba",
                8.5f,
                "",
                ""
        );
        
        // Insertar en la base de datos
        Pelicula peliculaInsertada = peliculaDAO.insertar(pelicula);
        
        // Verificar que la inserción fue exitosa
        assertNotNull("La inserción de la película no debería devolver null", peliculaInsertada);
        assertTrue("El ID de la película insertada debería ser mayor que 0", peliculaInsertada.getId() > 0);
    }

    /**
     * Prueba 2: Obtener película por ID
     */
    @Test
    public void testObtenerPeliculaPorId() {
        // Insertar película de prueba
        Pelicula pelicula = new Pelicula(
                "Película de prueba 2",
                2023,
                "Director de prueba 2",
                "Comedia",
                "Sinopsis de prueba 2",
                7.0f,
                "",
                ""
        );
        
        Pelicula peliculaInsertada = peliculaDAO.insertar(pelicula);
        int idPelicula = peliculaInsertada.getId();
        
        // Obtener la película por ID
        Pelicula peliculaObtenida = peliculaDAO.obtenerPorId(idPelicula);
        
        // Verificar que se obtuvo correctamente
        assertNotNull("La película obtenida no debería ser null", peliculaObtenida);
        assertEquals("El título debería coincidir", "Película de prueba 2", peliculaObtenida.getTitulo());
        assertEquals("El director debería coincidir", "Director de prueba 2", peliculaObtenida.getDirector());
    }

    /**
     * Prueba 3: Actualizar película
     */
    @Test
    public void testActualizarPelicula() {
        // Insertar película de prueba
        Pelicula pelicula = new Pelicula(
                "Película de prueba 3",
                2023,
                "Director de prueba 3",
                "Drama",
                "Sinopsis de prueba 3",
                6.5f,
                "",
                ""
        );
        
        Pelicula peliculaInsertada = peliculaDAO.insertar(pelicula);
        int idPelicula = peliculaInsertada.getId();
        
        // Modificar la película
        peliculaInsertada.setTitulo("Película modificada");
        peliculaInsertada.setDirector("Director modificado");
        
        // Actualizar en la base de datos
        int filasActualizadas = peliculaDAO.actualizar(peliculaInsertada);
        
        // Verificar que la actualización fue exitosa
        assertEquals("Debería actualizarse una fila", 1, filasActualizadas);
        
        // Obtener la película actualizada
        Pelicula peliculaActualizada = peliculaDAO.obtenerPorId(idPelicula);
        assertEquals("El título debería estar actualizado", "Película modificada", peliculaActualizada.getTitulo());
        assertEquals("El director debería estar actualizado", "Director modificado", peliculaActualizada.getDirector());
    }

    /**
     * Prueba 4: Eliminar película
     */
    @Test
    public void testEliminarPelicula() {
        // Insertar película de prueba
        Pelicula pelicula = new Pelicula(
                "Película de prueba 4",
                2023,
                "Director de prueba 4",
                "Ciencia ficción",
                "Sinopsis de prueba 4",
                9.0f,
                "",
                ""
        );
        
        Pelicula peliculaInsertada = peliculaDAO.insertar(pelicula);
        int idPelicula = peliculaInsertada.getId();
        
        // Eliminar la película
        int filasEliminadas = peliculaDAO.eliminar(idPelicula);
        
        // Verificar que la eliminación fue exitosa
        assertEquals("Debería eliminarse una fila", 1, filasEliminadas);
        
        // Intentar obtener la película eliminada
        Pelicula peliculaEliminada = peliculaDAO.obtenerPorId(idPelicula);
        assertNull("La película eliminada debería ser null", peliculaEliminada);
    }

    /**
     * Prueba 5: Obtener todas las películas
     */
    @Test
    public void testObtenerTodasLasPeliculas() {
        // Limpiar películas existentes e insertar varias de prueba
        List<Pelicula> peliculasIniciales = peliculaDAO.obtenerTodas();
        for (Pelicula p : peliculasIniciales) {
            peliculaDAO.eliminar(p.getId());
        }
        
        // Insertar películas de prueba
        peliculaDAO.insertar(new Pelicula("Película A", 2021, "Director A", "Acción", "Sinopsis A", 8.0f, "", ""));
        peliculaDAO.insertar(new Pelicula("Película B", 2022, "Director B", "Comedia", "Sinopsis B", 7.5f, "", ""));
        peliculaDAO.insertar(new Pelicula("Película C", 2023, "Director C", "Drama", "Sinopsis C", 9.0f, "", ""));
        
        // Obtener todas las películas
        List<Pelicula> peliculas = peliculaDAO.obtenerTodas();
        
        // Verificar que se obtuvieron todas
        assertEquals("Deberían haber 3 películas", 3, peliculas.size());
    }

    /**
     * Prueba 6: Buscar películas por título
     */
    @Test
    public void testBuscarPeliculasPorTitulo() {
        // Limpiar películas existentes e insertar varias de prueba
        List<Pelicula> peliculasIniciales = peliculaDAO.obtenerTodas();
        for (Pelicula p : peliculasIniciales) {
            peliculaDAO.eliminar(p.getId());
        }
        
        // Insertar películas de prueba
        peliculaDAO.insertar(new Pelicula("Matrix", 1999, "Hermanas Wachowski", "Ciencia ficción", "Sinopsis Matrix", 8.7f, "", ""));
        peliculaDAO.insertar(new Pelicula("Matrix Reloaded", 2003, "Hermanas Wachowski", "Ciencia ficción", "Sinopsis Matrix 2", 7.2f, "", ""));
        peliculaDAO.insertar(new Pelicula("El Padrino", 1972, "Francis Ford Coppola", "Drama", "Sinopsis El Padrino", 9.2f, "", ""));
        
        // Buscar películas con "Matrix" en el título
        List<Pelicula> peliculasMatrix = peliculaDAO.buscarPorTitulo("Matrix");
        
        // Verificar resultados
        assertEquals("Deberían haber 2 películas con 'Matrix' en el título", 2, peliculasMatrix.size());
    }

    /**
     * Prueba 7: Validación de año válido
     */
    @Test
    public void testValidacionAnioValido() {
        // Años válidos
        assertTrue("El año 1920 debería ser válido", ValidadorDatos.esAnioValido("1920"));
        assertTrue("El año 2000 debería ser válido", ValidadorDatos.esAnioValido("2000"));
        assertTrue("El año actual debería ser válido", ValidadorDatos.esAnioValido("2023"));
        
        // Años inválidos
        assertFalse("El año 1800 debería ser inválido (anterior al cine)", ValidadorDatos.esAnioValido("1800"));
        assertFalse("El año 2050 debería ser inválido (muy futuro)", ValidadorDatos.esAnioValido("2050"));
        assertFalse("Una cadena no numérica debería ser inválida", ValidadorDatos.esAnioValido("abcd"));
    }

    /**
     * Prueba 8: Validación de correo electrónico
     */
    @Test
    public void testValidacionCorreoElectronico() {
        // Correos válidos
        assertTrue("Correo simple debería ser válido", ValidadorDatos.esCorreoValido("usuario@ejemplo.com"));
        assertTrue("Correo con puntos debería ser válido", ValidadorDatos.esCorreoValido("usuario.nombre@ejemplo.com"));
        
        // Correos inválidos
        assertFalse("Correo sin @ debería ser inválido", ValidadorDatos.esCorreoValido("usuarioejemplo.com"));
        assertFalse("Correo sin dominio debería ser inválido", ValidadorDatos.esCorreoValido("usuario@"));
    }
    
    /**
     * Prueba 9: Validación de contraseña segura
     */
    @Test
    public void testValidacionContrasenaSegura() {
        // Contraseñas seguras
        assertTrue("Contraseña con letras y números debería ser válida", 
                ValidadorDatos.esContrasenaSegura("Abc12345"));
        
        // Contraseñas inseguras
        assertFalse("Contraseña sin mayúsculas debería ser inválida", 
                ValidadorDatos.esContrasenaSegura("abc12345"));
        assertFalse("Contraseña sin números debería ser inválida", 
                ValidadorDatos.esContrasenaSegura("Abcdefgh"));
        assertFalse("Contraseña corta debería ser inválida", 
                ValidadorDatos.esContrasenaSegura("Ab1"));
    }
    
    /**
     * Prueba 10: Validación de valoración de película
     */
    @Test
    public void testValidacionValoracion() {
        // Valoraciones válidas
        assertTrue("Valoración 0 debería ser válida", ValidadorDatos.esValoracionValida("0"));
        assertTrue("Valoración 5 debería ser válida", ValidadorDatos.esValoracionValida("5"));
        assertTrue("Valoración 8.5 debería ser válida", ValidadorDatos.esValoracionValida("8.5"));
        assertTrue("Valoración 10 debería ser válida", ValidadorDatos.esValoracionValida("10"));
        
        // Valoraciones inválidas
        assertFalse("Valoración negativa debería ser inválida", ValidadorDatos.esValoracionValida("-1"));
        assertFalse("Valoración mayor a 10 debería ser inválida", ValidadorDatos.esValoracionValida("11"));
        assertFalse("Texto no numérico debería ser inválido", ValidadorDatos.esValoracionValida("excelente"));
    }
}
