src/main/java/com/example/
│
├── boundary/
│   ├── messaging/                # Kafka-related components
│   │   ├── KafkaProducerService.java
│   │   └── KafkaConsumerService.java
│   ├── dto/                      # DTOs for Kafka messages
│   ├── controller/               # REST Controllers
│   └── exception/                # Exception handling
│
├── control/
│   ├── service/                  # Services with business logic
│   └── logic/                    # Core business logic
│
├── entity/
│   ├── model/                    # Domain models
│   └── repository/               # Repositories
│
├── config/                       # Kafka configuration
│
└── Application.java              # Main Spring Boot Application class