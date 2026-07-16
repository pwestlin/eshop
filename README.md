# E-shop
Backend for an E-shop implemented in [Kotlin](https://kotlinlang.org/), [Spring Boot](https://spring.io/projects/spring-boot), [Spring Data JDBC](https://spring.io/projects/spring-data-jdbc), [Spring Modulith](https://spring.io/projects/spring-modulith), [Spring Security](https://spring.io/projects/spring-security) and [Postgres](https://www.postgresql.org/). 

TODO pwestlin: Doc modules


TODO pwestlin: Fixa flödet nedan så den stämmer med koden.
```mermaid
flowchart TD
    %% States
    Start([Checkout Initiated]) --> PENDING[PENDING]
    
    PENDING -->|Inventory Allocation Successful| STOCK_RESERVED[STOCK_RESERVED]
    PENDING -->|Inventory Allocation Failed| FAILED[FAILED]
    
    STOCK_RESERVED -->|Payment Confirmed| PAID[PAID / PROCESSING]
    STOCK_RESERVED -->|Payment Failed| FAILED
    
    PAID -->|Order Shipped| SHIPPED[SHIPPED]
    SHIPPED -->|Order Delivered| COMPLETED[COMPLETED]
    
    COMPLETED -->|Customer Return / Refund| REFUNDED[REFUNDED]

    %% Styling
    classDef default fill:#f9f9f9,stroke:#333,stroke-width:2px;
    classDef status fill:#e1f5fe,stroke:#0288d1,stroke-width:2px;
    classDef final fill:#ffe0b2,stroke:#f57c00,stroke-width:2px;
    classDef error fill:#ffebee,stroke:#c62828,stroke-width:2px;
    
    class PENDING,STOCK_RESERVED,PAID,SHIPPED status;
    class COMPLETED,REFUNDED final;
    class FAILED error;
```