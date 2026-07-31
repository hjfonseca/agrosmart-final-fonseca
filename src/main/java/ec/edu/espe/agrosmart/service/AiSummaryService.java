package ec.edu.espe.agrosmart.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import ec.edu.espe.agrosmart.domain.Producto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AiSummaryService {

    private final ChatLanguageModel chatLanguageModel;

    public AiSummaryService(ChatLanguageModel chatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
    }

    public String generarResumenEjecutivo(List<Producto> productos) {
        if (productos == null || productos.isEmpty()) {
            return "No existen productos válidos en la categoría Quinua para resumir.";
        }

        String detalleProductos = productos.stream()
                .map(p -> String.format("- %s (Precio: $%s)", p.getNombre(), p.getPrecioUsd()))
                .collect(Collectors.joining("\n"));

        String prompt = String.format(
                "Eres un especialista agroindustrial de Ecuador. Analiza los siguientes productos válidos de la categoría Quinua y genera un resumen ejecutivo breve de 2 a 3 oraciones destacando su valor comercial:\n%s",
                detalleProductos
        );

        return chatLanguageModel.generate(prompt);
    }
}