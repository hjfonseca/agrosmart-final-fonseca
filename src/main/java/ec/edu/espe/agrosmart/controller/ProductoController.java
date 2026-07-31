package ec.edu.espe.agrosmart.controller;

import ec.edu.espe.agrosmart.domain.Producto;
import ec.edu.espe.agrosmart.service.ProductoService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping(value = "/procesados", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Producto> getProductosProcesados() {
        return productoService.obtenerProductosProcesados();
    }

    @GetMapping(value = "/resumen-ia", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> getResumenIA() {
        return productoService.obtenerProductosProcesados()
                .collectList()
                .flatMap(productos -> productoService.obtenerResumenIA()
                        .map(resumen -> {
                            Map<String, Object> response = new HashMap<>();
                            response.put("estudiante", "Harvey Joel Fonseca Escobar");
                            response.put("categoria", "Quinua");
                            response.put("total_procesados", productos.size());
                            response.put("resumen_ejecutivo_ia", resumen);
                            return response;
                        }));
    }
}