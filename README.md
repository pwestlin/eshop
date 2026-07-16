# E-shop
Backend for an E-shop implemented in [Kotlin](https://kotlinlang.org/), [Spring Boot](https://spring.io/projects/spring-boot), [Spring Data JDBC](https://spring.io/projects/spring-data-jdbc), [Spring Modulith](https://spring.io/projects/spring-modulith), [Spring Security](https://spring.io/projects/spring-security) and [Postgres](https://www.postgresql.org/). 

TODO pwestlin: Doc modules

```mermaid
graph TD
    A([Användare: Klickar på 'Köp']) -->|POST /checkout| B(PENDING);
    B -->|Lyckat betalningsevent / Lager reserverat| C(PAID / PROCESSING);
    B -->|Betalning misslyckad / Slut i lager| D(FAILED);
    B -->|Manuell avbrytning före skickning| E(CANCELLED);
    C -->|ship() metod anropas| F(SHIPPED);
    C -->|Manuell avbrytning före skickning| E;
    F -->|Levererad till kund| G(COMPLETED);

    classDef initial fill:#f9f,stroke:#333,stroke-width:2px;
    classDef state fill:#ccf,stroke:#333,stroke-width:1px;
    classDef final fill:#8f8,stroke:#333,stroke-width:1px;
    classDef error fill:#f88,stroke:#333,stroke-width:1px;

    class A initial;
    class B,C,F state;
    class G final;
    class D error;
    class E state;
```