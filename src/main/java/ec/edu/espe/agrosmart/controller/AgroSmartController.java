package ec.edu.espe.agrosmart.controller;

import ec.edu.espe.agrosmart.domain.Producto;
import ec.edu.espe.agrosmart.service.ProductoService;
import ec.edu.espe.agrosmart.service.PublicidadService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class AgroSmartController {

    private final ProductoService productoService;
    private final PublicidadService publicidadService;

    public AgroSmartController(ProductoService productoService, PublicidadService publicidadService) {
        this.productoService = productoService;
        this.publicidadService = publicidadService;
    }

    // Endpoint 1: Obtener productos comercializables
    @GetMapping("/api/productos")
    public Flux<Producto> obtenerProductos() {
        return productoService.obtenerProductosComercializables();
    }

    // Endpoint 2: Buscar producto por ID (lanza 404 si no existe mediante ProductoNoEncontradoException)
    @GetMapping("/api/productos/{id}")
    public Mono<Producto> obtenerProductoPorId(@PathVariable Long id) {
        return productoService.buscarPorId(id);
    }

    // Endpoint 3: Generar frase publicitaria mediante la IA (retorna texto plano)
    @GetMapping(value = "/api/agrosmart/publicidad", produces = "text/plain;charset=UTF-8")
    public Mono<String> generarPublicidad(
            @RequestParam String producto,
            @RequestParam String audiencia) {
        return publicidadService.generarPublicidad(producto, audiencia);
    }
}