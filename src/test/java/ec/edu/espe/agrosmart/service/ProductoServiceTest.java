package ec.edu.espe.agrosmart.service;

import ec.edu.espe.agrosmart.domain.Producto;
import ec.edu.espe.agrosmart.entity.ProductoEntity;
import ec.edu.espe.agrosmart.exception.ProductoNoEncontradoException;
import ec.edu.espe.agrosmart.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

class ProductoServiceTest {

    @Test
    void obtenerProductosComercializables_conTresValidosYDosInvalidos_debeEmitirSoloLosValidos() {
        // Arrange
        ProductoRepository repo = Mockito.mock(ProductoRepository.class);
        Mockito.when(repo.findAll()).thenReturn(datosDePrueba());   // 3 válidos + 2 inválidos
        ProductoService service = new ProductoService(repo);

        // Act
        Flux<Producto> flujo = service.obtenerProductosComercializables();

        // Assert
        StepVerifier.create(flujo)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void obtenerProductosComercializables_cuandoTodosSonInvalidos_debeEmitirProductoGenerico() {
        // Arrange
        ProductoRepository repo = Mockito.mock(ProductoRepository.class);
        ProductoEntity invalido1 = new ProductoEntity(1L, "Inv1", BigDecimal.ZERO, 10, "Quinua", "mail@test.com");
        ProductoEntity invalido2 = new ProductoEntity(2L, "Inv2", new BigDecimal("10.00"), 10, "Quinua", "");
        Mockito.when(repo.findAll()).thenReturn(List.of(invalido1, invalido2));
        ProductoService service = new ProductoService(repo);

        // Act
        Flux<Producto> flujo = service.obtenerProductosComercializables();

        // Assert
        StepVerifier.create(flujo)
                .expectNextMatches(p -> p.getNombre().contains("GENERICO") || p.getNombre().contains("GENÉRICO"))
                .verifyComplete();
    }

    @Test
    void buscarPorId_conIdInexistente_debeLanzarExcepcion() {
        // Arrange
        ProductoRepository repo = Mockito.mock(ProductoRepository.class);
        Mockito.when(repo.findById(9999L)).thenReturn(Optional.empty());
        ProductoService service = new ProductoService(repo);

        // Act
        Mono<Producto> mono = service.buscarPorId(9999L);

        // Assert
        StepVerifier.create(mono)
                .expectError(ProductoNoEncontradoException.class)
                .verify();
    }

    private List<ProductoEntity> datosDePrueba() {
        ProductoEntity v1 = new ProductoEntity(1L, "Quinua 1", new BigDecimal("10.00"), 50, "Quinua", "v1@test.com");
        ProductoEntity v2 = new ProductoEntity(2L, "Quinua 2", new BigDecimal("15.00"), 50, "Quinua", "v2@test.com");
        ProductoEntity v3 = new ProductoEntity(3L, "Quinua 3", new BigDecimal("20.00"), 50, "Quinua", "v3@test.com");
        ProductoEntity i1 = new ProductoEntity(4L, "Quinua 4", BigDecimal.ZERO, 50, "Quinua", "i1@test.com"); // precio 0 (inválido)
        ProductoEntity i2 = new ProductoEntity(5L, "Quinua 5", new BigDecimal("25.00"), 50, "Quinua", ""); // sin correos (inválido)
        return List.of(v1, v2, v3, i1, i2);
    }
}