package ec.edu.espe.agrosmart.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;

class PublicidadServiceTest {

    @Test
    void generarPublicidad_caminoFeliz_debeEmitirTextoGenerado() {
        // Arrange
        AgroSmartAIService ia = Mockito.mock(AgroSmartAIService.class);
        Mockito.when(ia.generarPublicidad("Quinua", "Exportadores"))
                .thenReturn("Frase publicitaria exitosa");
        PublicidadService service = new PublicidadService(ia);

        // Act
        Mono<String> mono = service.generarPublicidad("Quinua", "Exportadores");

        // Assert
        StepVerifier.create(mono)
                .expectNext("Frase publicitaria exitosa")
                .verifyComplete();
    }

    @Test
    void generarPublicidad_cuandoElProveedorFalla_debeEmitirMensajeDeRespaldo() {
        // Arrange
        AgroSmartAIService ia = Mockito.mock(AgroSmartAIService.class);
        Mockito.when(ia.generarPublicidad(any(), any()))
                .thenThrow(new RuntimeException("429 Too Many Requests"));
        PublicidadService service = new PublicidadService(ia);

        // Act & Assert
        StepVerifier.create(service.generarPublicidad("Cacao", "exportadores"))
                .expectNextMatches(texto -> texto.contains("no disponible"))
                .verifyComplete();
    }
}