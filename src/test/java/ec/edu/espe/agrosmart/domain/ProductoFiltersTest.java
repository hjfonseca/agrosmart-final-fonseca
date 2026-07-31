package ec.edu.espe.agrosmart.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductoFiltersTest {

    @Test
    void isValid_conProductoValido_debeRetornarTrue() {
        // Arrange
        Producto productoValido = new Producto(1L, "Quinua Perlada", "Quinua",
                new BigDecimal("12.50"), List.of("info@quinua.ec"));

        // Act & Assert
        assertTrue(ProductoFilters.IS_VALID.test(productoValido));
    }

    @Test
    void isValid_conPrecioCero_debeRetornarFalse() {
        // Arrange
        Producto productoInvalido = new Producto(2L, "Quinua Gratis", "Quinua",
                BigDecimal.ZERO, List.of("info@quinua.ec"));

        // Act & Assert
        assertFalse(ProductoFilters.IS_VALID.test(productoInvalido));
    }

    @Test
    void isValid_conListaCorreosVacia_debeRetornarFalse() {
        // Arrange
        Producto productoInvalido = new Producto(3L, "Quinua Sin Correo", "Quinua",
                new BigDecimal("15.00"), Collections.emptyList());

        // Act & Assert
        assertFalse(ProductoFilters.IS_VALID.test(productoInvalido));
    }
}