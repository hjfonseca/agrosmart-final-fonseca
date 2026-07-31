package ec.edu.espe.agrosmart.service;

import ec.edu.espe.agrosmart.domain.Producto;
import ec.edu.espe.agrosmart.domain.ProductoFilters;
import ec.edu.espe.agrosmart.exception.ProductoNoEncontradoException;
import ec.edu.espe.agrosmart.mapper.ProductoMapper;
import ec.edu.espe.agrosmart.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.Collections;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    // Producto genérico estático exigido para defaultIfEmpty
    private static final Producto PRODUCTO_GENERICO = new Producto(
            0L, "PRODUCTO GENÉRICO", "Quinua", BigDecimal.ZERO, Collections.emptyList()
    );

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    // Obtener productos comercializables
    public Flux<Producto> obtenerProductosComercializables() {
        // fromCallable difiere la consulta: nada se ejecuta hasta que alguien se suscriba
        return Mono.fromCallable(repository::findAll)
                // boundedElastic: JPA/Hibernate bloquea el hilo. Si esto corriera en el
                // event loop de Netty, un solo hilo bloqueado degradaría todas las peticiones
                .subscribeOn(Schedulers.boundedElastic())
                // flatMapMany: convierte la lista materializada en un flujo reactivo Flux
                .flatMapMany(Flux::fromIterable)
                // map: transforma la entidad JPA a nuestro modelo de dominio inmutable
                .map(ProductoMapper::toDominio)
                // map: transforma el nombre a mayúsculas retornando un nuevo objeto inmutable
                .map(ProductoFilters.A_MAYUSCULAS)
                // filter: descarta productos no comercializables
                .filter(ProductoFilters.IS_VALID)
                // doOnNext: efecto secundario de trazabilidad por consola, sin transformar
                .doOnNext(ProductoFilters.LOG_PRODUCTO)
                // defaultIfEmpty: emite un producto genérico si el filtro dejó el flujo vacío
                .defaultIfEmpty(PRODUCTO_GENERICO);
    }

    // Buscar producto por ID
    public Mono<Producto> buscarPorId(Long id) {
        // fromCallable difiere la consulta por ID al momento de suscripción
        return Mono.fromCallable(() -> repository.findById(id))
                // boundedElastic aísla el bloqueo I/O de la base de datos fuera del event loop
                .subscribeOn(Schedulers.boundedElastic())
                // flatMap con justOrEmpty: desempaca el Optional; si está vacío, el Mono resultante queda vacío
                .flatMap(Mono::justOrEmpty)
                // map: transforma la entidad encontrada a dominio
                .map(ProductoMapper::toDominio)
                // switchIfEmpty: el no encontrado se resuelve DENTRO del flujo,
                // sin sacar el valor del contexto reactivo, lanzando la excepción
                .switchIfEmpty(Mono.error(new ProductoNoEncontradoException(id)));
    }
}