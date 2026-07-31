package ec.edu.espe.agrosmart.service;

import ec.edu.espe.agrosmart.domain.Producto;
import ec.edu.espe.agrosmart.domain.ProductoFilters;
import ec.edu.espe.agrosmart.mapper.ProductoMapper;
import ec.edu.espe.agrosmart.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class ProductoService {

    private final ProductoRepository repository;
    private final AiSummaryService aiSummaryService;

    public ProductoService(ProductoRepository repository, AiSummaryService aiSummaryService) {
        this.repository = repository;
        this.aiSummaryService = aiSummaryService;
    }

    public Flux<Producto> obtenerProductosComercializables() {
        return Mono.fromCallable(repository::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .map(ProductoMapper::toDominio)
                .filter(ProductoFilters.IS_VALID)
                .doOnNext(ProductoFilters.LOG_PRODUCTO)
                .map(ProductoFilters.A_MAYUSCULAS);
    }

    // Procesamiento reactivo
    public Mono<String> obtenerResumenIA() {
        return obtenerProductosComercializables()
                .collectList()
                .flatMap(productos -> Mono.fromCallable(() -> aiSummaryService.generarResumenEjecutivo(productos))
                        .subscribeOn(Schedulers.boundedElastic()));
    }
}