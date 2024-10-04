
# Mutual Fund System

The **Mutual Fund System** is a Spring Boot application designed to fetch mutual fund data from a public API and store it in a PostgreSQL database. The service fetches data once a day and automatically stores it in the database upon initialization. The system also supports querying mutual fund data based on various criteria.

## Table of Contents

- [Project Description](#project-description)
- [Technologies Used](#technologies-used)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Setup and Installation](#setup-and-installation)
- [Running the Application](#running-the-application)
- [Database Setup](#database-setup)
- [Using Docker](#using-docker)
- [Testing](#testing)
- [API Endpoints](#api-endpoints)
- [License](#license)

## Project Description

The **Mutual Fund System** fetches mutual fund data from the [mfapi.in](https://api.mfapi.in) and stores it in a PostgreSQL database. The application is based on a microservice architecture and supports fetching, storing, and querying mutual fund data. The application initializes by automatically pulling the latest data and storing it in the database.

## Technologies Used

- **Java 17**
- **Spring Boot**
- **Spring Data JPA**
- **PostgreSQL**
- **Docker / Podman**
- **RestTemplate**
- **JUnit** for testing
- **Angular** (for frontend, optional)

## Features

- Fetches and stores mutual fund data from `mfapi.in`.
- Supports fetching mutual funds by `schemeName` and `schemeType`.
- Uses a scheduler to pull data once per day.
- Microservice architecture.
- Dockerized for container-based deployment.
- Batch commits for performance improvement.

## Prerequisites

- Java 17+
- Maven 3+
- Docker or Podman
- PostgreSQL (for production)
- H2 (for local development)
  
## Setup and Installation

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/mutual-fund-system.git
cd mutual-fund-system
```

### 2. Set up PostgreSQL
- Run PostgreSQL in Docker or on your local machine.
- Create a database called `mutualfunddb`.

### 3. Configure Application Properties

Modify `src/main/resources/application.properties` to set the correct database details.

For **H2 (local development)**:
```properties
spring.datasource.url=jdbc:h2:mem:mutualfunddb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

For **PostgreSQL (production)**:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mutualfunddb
spring.datasource.username=admin
spring.datasource.password=admin123
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### 4. Build the Project
```bash
mvn clean install
```

### 5. Run the Application
```bash
mvn spring-boot:run
```

## Database Setup

1. **Access PostgreSQL**: If using Docker:
    ```bash
    docker exec -it <postgres-container> psql -U admin -d mutualfunddb
    ```

2. **List tables**:
    ```sql
    \dt
    ```

3. **Query Mutual Fund data**:
    ```sql
    SELECT * FROM mutualfunds;
    ```

## Using Docker

### 1. Build and Run Docker Container

- Build the Docker image for the application:

```bash
docker build -t mutual-fund-app .
```

- Run the Docker container with PostgreSQL:

```bash
docker run --name postgres-db -e POSTGRES_DB=mutualfunddb -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin123 -d postgres
docker run --name mutual-fund-service --link postgres-db:postgres -p 8080:8080 mutual-fund-app
```

### 2. Run the Application with Podman (if using Podman):

- Pull and run PostgreSQL:
```bash
podman run --name postgres-db -e POSTGRES_DB=mutualfunddb -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin123 -d postgres
```

- Build and run the application:
```bash
podman build -t mutual-fund-app .
podman run --name mutual-fund-service --link postgres-db:postgres -p 8080:8080 mutual-fund-app
```

## Testing

- Run the test cases using the following Maven command:
```bash
mvn test
```

- Unit tests are provided for the `MutualFundService` class, ensuring that the mutual fund data is fetched and stored correctly.

## API Endpoints

- **Get Mutual Funds by Scheme Name**:
  - `GET /api/mutualfunds/schemeName/{schemeName}`

- **Get Mutual Funds by Scheme Type**:
  - `GET /api/mutualfunds/schemeType/{schemeType}`

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
