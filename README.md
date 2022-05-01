# TDD APLICADO A SPRING BOOT

Ejemplo de programa Spring Boot al que se le aplica TDD.
Se parte de unas mínimas clases ya creadas y se hará con TDD lo referente al Service, Repository y Controller.

Para DAO se hace uso de Spring Data JPA y para base de datos H2, para que los test de integración sean más rápidos.

Se cambia al final a MySql.
Hay un fichero para crear el esquema y las tablas en resources llamado: create_mysql_database_script