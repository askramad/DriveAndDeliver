# DriveAndDeliver - Carrefour Kata

## Description
This project is a basic implementation for the DriveAndDeliver kata by Carrefour. It includes functionalities for customers to choose their delivery methods and book time slots for deliveries.

## Prerequisites
- Java 21
- Maven
- Git

## Setup
1. Clone the repository:
    ```sh
    git clone https://github.com/askramad/DriveAndDeliver.git
    cd DriveAndDeliver
    ```

2. Build the project:
    ```sh
    mvn clean install
    ```

3. Run the application:
    ```sh
    mvn spring-boot:run
    ```

## Implementation Notes
- The project uses Spring Boot 3.x.x and Java 21.
- In-memory H2 database is used for simplicity.
- Time slots can be booked by customers, ensuring no double bookings.

## Future Enhancements
- Implement security for the API.
- Add more delivery methods and refine time slot management.
- Propose a data persistence solution and caching mechanism.
- Develop a CI/CD pipeline and containerize the application.
