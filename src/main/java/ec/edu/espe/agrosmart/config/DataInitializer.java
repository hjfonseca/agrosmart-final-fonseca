package ec.edu.espe.agrosmart.config;

import ec.edu.espe.agrosmart.entity.ProductoEntity;
import ec.edu.espe.agrosmart.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(ProductoRepository repository) {
        return args -> {

            if (repository.count() == 0) {
                // 3 Productos validos
                ProductoEntity p1 = new ProductoEntity(
                        null,
                        "Quinua Perlada Seleccionada Chimborazo",
                        new BigDecimal("11.80"),
                        450,
                        "Quinua",
                        "ventas@sumakgranosis.com.ec,contacto@agrochimborazo.org"
                );

                ProductoEntity p2 = new ProductoEntity(
                        null,
                        "Harina Integral de Quinua Andina 500g",
                        new BigDecimal("4.75"),
                        800,
                        "Quinua",
                        "pedidos@molinosandinos.ec"
                );

                ProductoEntity p3 = new ProductoEntity(
                        null,
                        "Hojuelas de Quinua Precocidas Export",
                        new BigDecimal("16.50"),
                        250,
                        "Quinua",
                        "comercial@ecoquinua-ecuador.com"
                );

                // 2 Productos invalidos
                // Inválido 1: precio_usd = 0
                ProductoEntity p4 = new ProductoEntity(
                        null,
                        "Muestra Degustación Quinua Tricolor 100g",
                        new BigDecimal("0.00"),
                        60,
                        "Quinua",
                        "promociones@sumakgranosis.com.ec"
                );

                // Inválido 2: correos vacíos
                ProductoEntity p5 = new ProductoEntity(
                        null,
                        "Saco Quinua Lavada Granel 50kg",
                        new BigDecimal("85.00"),
                        120,
                        "Quinua",
                        ""
                );

                repository.saveAll(List.of(p1, p2, p3, p4, p5));
                System.out.println(">>> Siembra de datos completada con exito: 5 productos de Quinua en tbl_productos_base_89.");
            }
        };
    }
}
