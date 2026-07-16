# E-shop
Backend for an E-shop implemented in [Kotlin](https://kotlinlang.org/), [Spring Boot](https://spring.io/projects/spring-boot), [Spring Data JDBC](https://spring.io/projects/spring-data-jdbc), [Spring Modulith](https://spring.io/projects/spring-modulith), [Spring Security](https://spring.io/projects/spring-security) and [Postgres](https://www.postgresql.org/).

For testing it uses [Spring Modulith test](https://docs.spring.io/spring-modulith/reference/testing.html), [JUnit](https://junit.org/), [AssertJ](https://assertj.github.io/doc/) and [Testcontainers](https://testcontainers.com/).

TODO pwestlin: Doc modules


TODO pwestlin: Fixa flödet nedan så den stämmer med koden.
```mermaid
flowchart TD
    %% States
    Start([Checkout Initiated]) --> Pending[Pending]
    
    Pending -->|Inventory Allocation Successful| StockReserved[StockReserved]
    Pending -->|Inventory Allocation Failed| Cancelled[Cancelled]
    
    StockReserved -->|Payment Successful| Paid[Paid]
    StockReserved -->|Payment Failed| Cancelled
    
    Paid -->|Order Shipped| Shipped[Shipped]
    
    %% Styling
    classDef default fill:#f9f9f9,stroke:#333,stroke-width:2px;
    classDef status fill:#e1f5fe,stroke:#0288d1,stroke-width:2px;
    classDef final fill:#ffe0b2,stroke:#f57c00,stroke-width:2px;
    classDef error fill:#ffebee,stroke:#c62828,stroke-width:2px;
    
    class Pending,StockReserved,Paid,Shipped status;
    class Shipped final;
    class Cancelled error;
```