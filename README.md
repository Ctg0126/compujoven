# CompuJoven

Portal web de formaciones para jóvenes. Spring Boot + Thymeleaf + JPA + **MySQL**.

## Requisitos
- Java 21
- MySQL (se administra con MySQL Workbench)

## Puesta en marcha
1. (Opcional) Ejecuta `database/compujoven.sql` en Workbench. Si no, la app crea la base `compujoven` sola.
2. Define las credenciales de MySQL como variables de entorno `DB_USER` y `DB_PASSWORD`
   (o edita los valores por defecto en `src/main/resources/application.properties`, sin subirlos a Git).
3. `./mvnw spring-boot:run` y abre http://localhost:8081

El administrador inicial se crea al arrancar: `admin@compujoven.com` / `admin123`
(cámbialo con `ADMIN_EMAIL` y `ADMIN_PASSWORD`).

## Modelo
`Usuario` 1—N `Postulacion` N—1 `Formacion` (tablas `usuarios`, `postulaciones`, `formaciones`).
