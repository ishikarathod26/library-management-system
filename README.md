# 📚 BiblioHub - Modern Library Management System

A full-stack, enterprise-ready **Library Management System** built with **Spring Boot 4**, **Java 25**, **Spring Data JPA**, **MySQL**, **H2 (Test)**, **Swagger / OpenAPI 3**, and a responsive modern Web UI.

![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen?logo=springboot)
![Database](https://img.shields.io/badge/Database-MySQL%20%7C%20H2-blue?logo=mysql)
![OpenAPI](https://img.shields.io/badge/API%20Docs-Swagger%20UI-green?logo=swagger)
![License](https://img.shields.io/badge/License-MIT-purple)

---

## 🌟 Key Features

### 📖 1. Book Management
- **Full CRUD operations**: Create, Read, Update, and Delete books.
- **Search & Filter**: Real-time search across titles, authors, and ISBNs.
- **Availability Tracking**: Real-time badges for available vs issued books.
- **Delete Protection**: Prevents deleting books that are currently issued to students.

### 🎓 2. Student Management
- **Student Registration**: Manage student details (name, email) with validation.
- **Directory Search**: Search students by name or email.
- **Loan Counter**: Displays how many active books each student currently has.
- **Delete Protection**: Prevents deleting students with active borrowed books.

### 🔄 3. Issue & Return Desk
- **Book Issuance**: Issue available books with custom due dates (defaults to 14 days).
- **Validation**:
  - Rejects past due dates.
  - Rejects attempts to issue already-issued books.
  - Automatically updates book availability to `false`.
- **Book Return**:
  - Single-click returns from the active issues dashboard.
  - Fast Return by Loan ID.
  - Automatically restores book availability to `true`.
  - Rejects duplicate return attempts.
- **Overdue Detection**: Highlights overdue loans with automatic alert badges.

### 💻 4. Interactive Web UI
- **Single Page Application (SPA)** served directly at `http://localhost:9090/`.
- Built with Tailwind CSS, FontAwesome icons, and Vanilla JavaScript (zero extra build step needed).
- Real-time stat cards (Total Books, Available Books, Issued Books, Registered Students).
- Toast notification alerts for user actions and error feedback.

### 📑 5. API Documentation
- Interactive Swagger UI available at `http://localhost:9090/swagger-ui.html`.
- OpenAPI JSON specification available at `http://localhost:9090/v3/api-docs`.

---

## 🏗️ Architecture & Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Java 25 LTS |
| **Framework** | Spring Boot 4.1.1 (Spring Web MVC) |
| **Persistence** | Spring Data JPA / Hibernate 7 |
| **Production Database** | MySQL 8.x |
| **Testing Database** | H2 In-Memory Database (MySQL Mode) |
| **Validation** | Jakarta Bean Validation (`@Valid`, `@NotBlank`, `@Email`) |
| **API Docs** | Springdoc OpenAPI 3.1.1 & Swagger UI |
| **Frontend** | Responsive SPA (HTML5, Tailwind CSS, FontAwesome, JavaScript ES6) |
| **Testing** | JUnit 5, Mockito, Spring Boot MockMvc (48 automated tests) |
| **CI/CD** | GitHub Actions Workflow (`maven.yml`) |

---

## 🚀 Getting Started

### Prerequisites
- **JDK 25** or higher installed and configured in your `PATH`.
- **Apache Maven 3.9+** (or use the included `./mvnw`).
- **MySQL Server** (optional for production; tests run out-of-the-box with embedded H2).

### 1. Clone the Repository
```bash
git clone https://github.com/ishikarathod26/library-management-system.git
cd library-management-system
```

### 2. Configure Database (Optional)
By default, the application connects to MySQL on `localhost:3306/library_db`.
Set your database password via environment variable or in `application.properties`:
```bash
export DB_PASSWORD="your_password"
```

### 3. Run Automated Tests
All 48 tests run hermetically using an embedded in-memory H2 database:
```bash
mvn clean test
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

Once started:
- 🌐 **Web Application**: [http://localhost:9090/](http://localhost:9090/)
- 📖 **Swagger UI Docs**: [http://localhost:9090/swagger-ui.html](http://localhost:9090/swagger-ui.html)

---

## 📡 REST API Reference

### 📚 Books API (`/books`)
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/books` | Get all books (supports `?search=query`) |
| `GET` | `/books/{id}` | Get book by ID (returns 404 if not found) |
| `POST` | `/books` | Create a new book (`title`, `author`, `isbn`, `available`) |
| `PUT` | `/books/{id}` | Update existing book |
| `DELETE` | `/books/{id}` | Delete book (returns 409 if currently issued) |

### 🎓 Students API (`/students`)
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/students` | Get all students (supports `?search=query`) |
| `GET` | `/students/{id}` | Get student by ID (returns 404 if not found) |
| `POST` | `/students` | Register a new student (`name`, `email`) |
| `PUT` | `/students/{id}` | Update student details |
| `DELETE` | `/students/{id}` | Delete student (returns 409 if student has borrowed books) |

### 🔄 Book Issues API (`/book-issues`)
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/book-issues` | Get all loan records (supports `?status=active`) |
| `GET` | `/book-issues/active` | Get all currently unreturned loans |
| `GET` | `/book-issues/{id}` | Get issue record by ID |
| `GET` | `/book-issues/student/{studentId}` | Get loan history for a student |
| `GET` | `/book-issues/book/{bookId}` | Get loan history for a book |
| `POST` | `/book-issues` | Issue a book (`studentId`, `bookId`, `dueDate`) |
| `PUT` | `/book-issues/{id}/return` | Mark book as returned |

---

## 🧪 Testing Summary

The project includes **48 automated unit and integration tests** covering:
- **Controllers**: `MockMvc` request/response validation, HTTP status codes, and error responses.
- **Services**: Business logic, constraint validations, availability transitions, conflict handling.
- **Repositories**: In-memory database persistence and custom queries.
- **Exception Handling**: Standardized error responses across all failure scenarios.

Run all tests:
```bash
mvn test
```

---

## 👩‍💻 Author
**Ishika Rathod**  
GitHub: [@ishikarathod26](https://github.com/ishikarathod26)
