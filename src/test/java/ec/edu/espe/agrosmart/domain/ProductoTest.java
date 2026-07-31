package ec.edu.espe.agrosmart.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductoTest {

    @Test
    void constructor_conValoresValidos_debeAsignarAtributosCorrectamente() {
        // Arrange
        List<String> correos = List.of("ventas@quinua.ec");

        // Act
        Producto producto = new Producto(1L, "Quinua Perlada", "Quinua", new BigDecimal("11.80"), correos);

        // Assert
        assertEquals(1L, producto.getId());
        assertEquals("Quinua Perlada", producto.getNombre());
        assertEquals("Quinua", producto.getCategoria());
        assertEquals(new BigDecimal("11.80"), producto.getPrecioUsd());
        assertEquals(correos, producto.getCorreosNotificacion());
    }

    @Test
    void getCorreosNotificacion_alMutarLaListaOriginal_noDebeAfectarAlProducto() {
        // Arrange
        List<String> correos = new ArrayList<>();
        correos.add("ventas@agrosmart.ec");
        Producto producto = new Producto(1L, "Cacao fino", "Cacao",
                new BigDecimal("120.50"), correos);

        // Act
        correos.add("intruso@mail.com");

        // Assert
        assertEquals(1, producto.getCorreosNotificacion().size());
        assertNotSame(correos, producto.getCorreosNotificacion());
    }

    @Test
    void getCorreosNotificacion_alIntentarMutarListaRetornada_debeLanzarExcepcion() {
        // Arrange (Copia defensiva de salida - Lista inmodificable)
        List<String> correos = List.of("ventas@agrosmart.ec");
        Producto producto = new Producto(1L, "Cacao fino", "Cacao",
                new BigDecimal("120.50"), correos);

        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> {
            producto.getCorreosNotificacion().add("intruso@mail.com");
        });
    }
}