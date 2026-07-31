
```markdown
# Universidad de las Fuerzas Armadas ESPE
## Examen Final Práctico — Programación Avanzada
### Caso Integrador: AgroSmart 

* **Estudiante:** Harvey Joel Fonseca Escobar
* **Cédula:** 1725570889
* **Repositorio GitHub:** [https://github.com/hjfonseca/agrosmart-final-fonseca](https://github.com/hjfonseca/agrosmart-final-fonseca)

---

## 1. Cálculo de Semilla Personal

La semilla asignada para la parametrización del sistema se calculó mediante el módulo de la cédula de identidad:

$$\text{Semilla} = 1725570889 \pmod{100} = 89$$

### Parámetros asignados derivados de la semilla:
* **Puerto de la aplicación:** `8189`
* **Nombre de la tabla base en PostgreSQL:** `tbl_productos_base_89`
* **Categoría principal de filtrado:** `Quinua`

---

## 2. Instrucciones de Ejecución

### Prerrequisitos
* **Java 21 LTS** configurado en el `PATH`.
* **PostgreSQL 15+** en ejecución local en el puerto `5432`.

### Paso 1: Configurar la Base de Datos
Crear la base de datos y usuario en PostgreSQL desde `psql` o pgAdmin:

```sql
CREATE DATABASE agrosmart_db;
CREATE USER agrosmart WITH PASSWORD 'agrosmart';
GRANT ALL PRIVILEGES ON DATABASE agrosmart_db TO agrosmart;

```

### Paso 2: Variables de Entorno 


```bash
export DB_URL=jdbc:postgresql://localhost:5432/agrosmart_db
export DB_USERNAME=agrosmart
export DB_PASSWORD=agrosmart
export OPENAI_API_KEY=tu_api_key_aqui

```

### Paso 3: Clonar y Ejecutar la Aplicación

```bash
git clone [https://github.com/hjfonseca/agrosmart-final-fonseca.git](https://github.com/hjfonseca/agrosmart-final-fonseca.git)
cd agrosmart-final-fonseca

# Ejecutar con el perfil de producción en el puerto 8189
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

```

---

## 3. Tabla de Endpoints y Ejemplos Reales de `curl`

| Método | Ruta | Descripción | Comando `curl` |
| --- | --- | --- | --- |
| **GET** | `/api/productos` | Obtiene el `Flux` de productos comercializables en mayúsculas | `curl -i http://localhost:8189/api/productos` |
| **GET** | `/api/productos/{id}` | Busca un producto por su ID (`Mono<Producto>`) | `curl -i http://localhost:8189/api/productos/1` |
| **GET** | `/api/productos/9999` | Manejo de error cuando el ID no existe (404) | `curl -i http://localhost:8189/api/productos/9999` |
| **GET** | `/api/agrosmart/publicidad` | Genera anuncio con IA LangChain4j (`Mono<String>`) | `curl -i "http://localhost:8189/api/agrosmart/publicidad?producto=Quinua%20organica&audiencia=exportadores"` |

### Salidas Reales de Ejecución

#### 1. Consulta de productos comercializables (`GET /api/productos`)

**Entrada:**

```bash
curl -i http://localhost:8189/api/productos

```

**Salida real:**

[{"id":1,"nombre":"QUINUA PERLADA SELECCIONADA CHIMBORAZO","categoria":"Quinua","precioUsd":11.80,"correosNotificacion":["ventas@sumakgranosis.com.ec","contacto@agrochimborazo.org"]},{"id":2,"nombre":"HARINA INTEGRAL DE QUINUA ANDINA 500G","categoria":"Quinua","precioUsd":4.75,"correosNotificacion":["pedidos@molinosandinos.ec"]},{"id":3,"nombre":"HOJUELAS DE QUINUA PRECOCIDAS EXPORT","categoria":"Quinua","precioUsd":16.50,"correosNotificacion":["comercial@ecoquinua-ecuador.com"]}]

```

#### 2. Consulta por ID existente (`GET /api/productos/1`)

**Entrada:**

```bash
curl -i http://localhost:8189/api/productos/1

```

**Salida real:**

{"id":1,"nombre":"Quinua Perlada Seleccionada Chimborazo","categoria":"Quinua","precioUsd":11.80,"correosNotificacion":["ventas@sumakgranosis.com.ec","contacto@agrochimborazo.org"]}

```

#### 3. Consulta por ID inexistente (`GET /api/productos/9999`)

**Entrada:**

```bash
curl -i http://localhost:8189/api/productos/9999

```

**Salida real:**

Whitelabel Error Page
This application has no configured error view, so you are seeing this as a fallback.

Fri Jul 31 14:54:03 ECT 2026
[f75dc9d1-4] There was an unexpected error (type=Not Found, status=404).

```

#### 4. Generación de publicidad con IA (`GET /api/agrosmart/publicidad`)

**Entrada:**

```bash
curl -i "http://localhost:8189/api/agrosmart/publicidad?producto=Quinua%20organica&audiencia=exportadores"

```

**Salida real:**

```http
HTTP/1.1 200 OK
Content-Type: text/plain;charset=UTF-8

"Impulsa tu negocio con nuestra quinua orgánica: calidad premium para mercados internacionales."

```

---

## 4. Justificación de Operadores Reactivos Utilizados

* **`Mono.fromCallable(repository::findAll)`**: Envuelve la llamada síncrona I/O de Spring Data JPA dentro de una fábrica diferida que solo se ejecuta cuando un suscriptor se conecta al pipeline reactivo.
* **`subscribeOn(Schedulers.boundedElastic())`**: Redirige la suscripción y ejecución de operaciones bloqueantes a un grupo de hilos elásticos reutilizables, evitando congelar los hilos no bloqueantes del Event Loop.
* **`flatMapMany(Flux::fromIterable)`**: Desempaqueta la colección `List<ProductoEntity>` traída por JPA y la transforma en un flujo reactivo `Flux<ProductoEntity>` elemento a elemento.
* **`map(...)`**: Aplica transformaciones puras en memoria, convirtiendo cada entidad en un registro inmutable del dominio y transformando el nombre del producto a mayúsculas.
* **`filter(ProductoFilters.IS_VALID)`**: Evalúa cada producto contra el predicado de negocio, dejando pasar únicamente aquellos con precio positivo y correos de notificación definidos.
* **`defaultIfEmpty(PRODUCTO_GENERICO)`**: Previene un flujo vacío en caso de que ningún producto pase el filtro, emitiendo una entidad estándar de respaldo.
* **`onErrorResume(...)`**: Captura excepciones durante la invocación al servicio remoto de IA o base de datos y retorna un mensaje de contingencia amigable sin interrumpir el servidor HTTP.

---

## 5. Explicación del Puente Bloqueante → Reactivo (`boundedElastic`)

El núcleo WebFlux de Spring se ejecuta sobre el motor **Netty**, el cual utiliza un número reducido de hilos no bloqueantes (**Event Loop** `reactor-http-nio-*`) diseñados para procesar miles de peticiones I/O simultáneas por segundo.

Si dentro de un hilo del Event Loop se ejecuta una operación síncrona bloqueante —como una consulta JPA a PostgreSQL o una llamada de red síncrona mediante LangChain4j— el hilo queda totalmente congelado esperando la respuesta. Esto destruye la reactividad y la capacidad del servidor para atender otras solicitudes.

Para resolver este problema, se utilizó **`Schedulers.boundedElastic()`** como un **puente seguro**:

```java
Mono.fromCallable(() -> repository.findAll())
    .subscribeOn(Schedulers.boundedElastic())

```

El operador `.subscribeOn(Schedulers.boundedElastic())` instruye a Project Reactor para que descargue la tarea bloqueante en un hilo dedicado pertenenciente a un pool secundario elástico. Una vez terminada la lectura en PostgreSQL, el resultado regresa al flujo reactivo principal, manteniendo el **Event Loop de Netty libre de bloqueos en todo momento**.
