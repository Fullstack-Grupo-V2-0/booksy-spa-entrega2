BOOKSY SPA   

Son dos Apps:

Booksy SPA      
/api/v2/   

MS Usuarios   
/api/v1/    


Links para Swagger:
http://localhost:8081/swagger-ui/index.html  (Booksy-SPA)
http://localhost:8082/swagger-ui/index.html  (ms-usuarios)



Para realizar los testeos, utilizar los siguientes URL y métodos:

--TEST DE AUTENTICACIÓN-- (App principal)

POST http://localhost:8081/api/v2/auth/login

{
    "email": "admin@booksyspa.cl",
    "password": "admin1234"
}


--TEST DE USUARIOS EN APP DE USUARIOS--

http://localhost:8082/api/v1/usuarios



--TEST EN LIBROS--

Test 1 — Body vacío (debe dar 400 con todos los errores)
POST http://localhost:8081/api/v2/libros
{}

Resultado esperado:
{
    "isbn": "El ISBN no puede estar vacío",
    "titulo": "El título no puede estar vacío",
    "precio": "El precio no puede ser nulo"
}

Test 2 — Precio inválido (debe dar 400)
POST http://localhost:8081/api/v2/libros
{
    "isbn": "978-123",
    "titulo": "Mi libro",
    "precio": -5.0
}

Resultado esperado:
{
    "precio": "El precio debe ser mayor a 0"
}

Test 3 — Todo correcto (debe dar 201)
POST http://localhost:8081/api/v2/libros
{
    "isbn": "978-123",
    "titulo": "Mi libro",
    "precio": 9990,
    "autor": { "idAutor": 1 },
    "categoria": { "idCategoria": 1 }
}

Resultado esperado: Deberías recibir el libro creado con su idLibro asignado.


--TEST EN CLIENTE-- (APP PRINCIPAL)

Test 1 — Body vacío (debe dar 400 con todos los errores)
POST http://localhost:8081/api/v2/clientes
{}

Resultado esperado:
{
    "rut": "El RUT no puede estar vacío",
    "nombre": "El nombre no puede estar vacío",
    "apellido": "El apellido no puede estar vacío",
    "email": "El email no puede estar vacío"
}

Test 2 — Email inválido (debe dar 400)
POST http://localhost:8081/api/v2/clientes
{
    "rut": "12345678-9",
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "esto-no-es-un-email"
}

Resultado esperado:
{
    "email": "El email debe tener un formato válido"
}

Test 3 — Todo correcto (debe dar 201)
POST http://localhost:8081/api/v2/clientes
{
    "rut": "12345678-9",
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan.perez@gmail.com"
}


--TEST EN AUTOR--

Test 1 — Body vacío (debe dar 400)
POST http://localhost:8081/api/v2/autores
{}

Resultado esperado:
{
    "nombre": "El nombre del autor no puede estar vacío",
    "apellido": "El apellido del autor no puede estar vacío"
}

Test 2 — Solo nombre (debe dar 400)
POST http://localhost:8081/api/v2/autores
{
    "nombre": "Gabriel"
}

Resultado esperado:
{
    "apellido": "El apellido del autor no puede estar vacío"
}

Test 3 — Todo correcto (debe dar 201)
POST http://localhost:8081/api/v2/autores
{
    "nombre": "Gabriel",
    "apellido": "García Márquez",
    "nacionalidad": "Colombiana"
}

Resultado esperado:
Deberías recibir el autor creado con su idAutor asignado.


--TEST EN CATEGORÍA--
Test 1 — Body vacío (debe dar 400)
POST http://localhost:8081/api/v2/categorias
{}

Resultado esperado:
{
    "nombre": "El nombre de la categoría no puede estar vacío"
}

Test 2 — Todo correcto (debe dar 201)
POST http://localhost:8081/api/v2/categorias
{
    "nombre": "Ciencia Ficción",
    "descripcion": "Libros de ciencia ficción y fantasía"
}

Resultado esperado:
Deberías recibir la categoría creada con su idCategoria asignado.


--TEST EN PEDIDO--

Test 1 — Body vacío (debe dar 400)
POST http://localhost:8081/api/v2/pedidos
{}

Resultado esperado:
{
    "cliente": "El cliente no puede ser nulo",
    "libro": "El libro no puede ser nulo",
    "estado": "El estado no puede ser nulo"
}

Test 2 — Estado inválido (debe dar 400)
POST http://localhost:8081/api/v2/pedidos
{
    "cliente": { "idCliente": 1 },
    "libro": { "idLibro": 1 },
    "estado": "INVALIDO"
}

Resultado esperado: Deberías recibir un 400 porque "INVALIDO" no existe en tu enum EstadoPedido.

Test 3 — Todo correcto (debe dar 201)
POST http://localhost:8081/api/v2/pedidos
{
    "cliente": { "idCliente": 1 },
    "libro": { "idLibro": 1 },
    "estado": "PENDIENTE",
    "fechaPedido": "2026-05-30",
    "total": 9990
}

