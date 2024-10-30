# Task Manager Application

Task Manager Application is a Spring Boot-based API for managing tasks. It allows users to create, delete, update, and retrieve tasks. The application also includes additional features like notifications via Telegram and integration with RabbitMQ for message queueing.

## Features

- **Task Creation**: Create a new task with a title, description, and status.
- **Task Deletion**: Delete a task by its ID.
- **Update Task Status**: Update the status of a specific task.
- **Update Task Fields**: Modify specific fields of a task.
- **Retrieve All Tasks**: Get a list of all tasks.
- **Telegram Notifications**: Send notifications to specified Telegram chat(s) when a new task is created.
- **RabbitMQ Integration**: Send task-related messages to a RabbitMQ queue for asynchronous processing.
- **Automatic Failover for Database**: Automatically connects to a secondary database if the primary database becomes unavailable.
- **Event Logging**: Logs events using Log4j for monitoring and troubleshooting.

## Technologies Used

- **Java 17**
- **Spring Boot 3.1.4** - Web framework for building RESTful APIs.
- **Spring Data JPA** - Data access with Hibernate.
- **H2 Database** - In-memory database for development and testing.
- **PostgreSQL** - Main production database.
- **Automatic Failover with HikariCP** - Configured to switch to a failover database when the primary database is unavailable.
- **RabbitMQ** - Message broker for handling task-related events.
- **Spring AMQP** - Integration with RabbitMQ.
- **Telegram Bot API** - For sending notifications.
- **Lombok** - Reduces boilerplate code.
- **OpenAPI (Swagger)** - API documentation.
- **MapStruct** - Mapping between DTOs and entities.
- **Log4j 2** - Logging framework for recording events and application activity.

## API Documentation

The API is documented using OpenAPI. After starting the application, you can access the API documentation at:
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## Prerequisites

- **Java 17**
- **Maven** - For building the project.
- **RabbitMQ** - Ensure RabbitMQ is installed and running.
- **PostgreSQL** - Ensure PostgreSQL is installed and configured.
- **Telegram Bot Token** - Create a Telegram bot and get the bot token. Also, specify the chat IDs to send notifications.

## Running the Application

### 1. Clone the Repository

    git clone https://github.com/beemaya13/taskmanager.git
    cd taskmanager

### 2. Install Dependencies

Use Maven to install all dependencies.

    mvn clean install

### 3. Run the Application

Start the application using Maven.

    mvn spring-boot:run

### 4. Access the API

The application will be running at `http://localhost:8080`. Use the Swagger UI or API client (e.g., Postman) to interact with the API.

### 5. Automatic Failover for Database

This application has an automatic failover mechanism. If the primary H2 database is unavailable, it will automatically switch to a configured failover PostgreSQL database using HikariCP. This ensures that the application remains operational even if the main database experiences downtime.

### 6. Event Logging with log4j

The application uses Log4j 2 for logging important events and error tracking. Logs are configured in `log4j2.xml`, which includes logging levels and file output settings.

## Available Endpoints

### Task Management
- **POST** `/api/tasks` - Create a new task.
- **DELETE** `/api/tasks/{id}` - Delete a task by ID.
- **PUT** `/api/tasks/{id}/status` - Update the status of a task.
- **PATCH** `/api/tasks/{id}` - Update specific fields of a task.
- **GET** `/api/tasks` - Retrieve all tasks.

## Admin Consoles

- **H2 Database Console**: Accessible at [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- **RabbitMQ Management Console**: Accessible at [http://localhost:15672](http://localhost:15672)


## Examples

### Create Task
**POST** `/api/tasks`

    {
      "title": "New Task",
      "description": "This is a new task",
      "status": "NEW"
    }

### Update Task Status
**PUT** `/api/tasks/1/status?status=IN_PROGRESS`

## Error Handling
Custom exceptions include:

- **TaskNotFoundException** - Returned when a task ID does not exist.
- **DuplicateTaskTitleException** - Returned when a task with the same title already exists.
- **TaskLimitReachedException** - Returned when the maximum number of tasks is reached.
- **MethodArgumentNotValidException** - Returned for validation errors.

## Unit Testing
The application includes unit tests for the services. To run the tests, use:
    
    mvn test

## Author
Developed by Maya Nilga



