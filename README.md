# E-shop
Backend for an E-shop implemented in [Kotlin](https://kotlinlang.org/), [Spring Boot](https://spring.io/projects/spring-boot), [Spring Data JDBC](https://spring.io/projects/spring-data-jdbc), [Spring Modulith](https://spring.io/projects/spring-modulith), [Spring Security](https://spring.io/projects/spring-security) and [Postgres](https://www.postgresql.org/). 

TODO pwestlin: Doc modules

```mermaid
graph TD
    A([Användare: Klickar på 'Köp']) -->|"POST /checkout"| B(PENDING)
    B -->|"Lyckat betalningsevent / Lager reserverat"| C(PAID / PROCESSING)
    B -->|"Betalning misslyckad / Slut i lager"| D(FAILED)
    B -->|"Manuell avbrytning före skickning"| E(CANCELLED)
    C -->|"ship() metod anropas"| F(SHIPPED)
    C -->|"Manuell avbrytning före skickning"| E
    F -->|"Levererad till kund"| G(COMPLETED)

    classDef initial fill:#f9f,stroke:#333,stroke-width:2px;
    classDef state fill:#ccf,stroke:#333,stroke-width:1px;
    classDef final fill:#8f8,stroke:#333,stroke-width:1px;
    classDef error fill:#f88,stroke:#333,stroke-width:1px;

    class A initial;
    class B,C,F,E state;
    class G final;
    class D error;
```

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