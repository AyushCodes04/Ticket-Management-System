# 🎫 Ticket Management System

A full-stack **Event Ticketing & QR-Based Entry Management System** built with Spring Boot, MySQL, and React. Designed for managing events, issuing digital tickets, and verifying entry via QR code scanning.

---

## 👥 Team AlgoFlow

| Member | Role |
|---|---|
| Ayush Chauhan | Backend (Spring Boot) |
| Amanpreet Singh | Frontend (Swing) |
| Mahak Gupta | Database (MySQL) |

---

## 🚀 Tech Stack

**Backend**
- Java 21
- Spring Boot 4.0.3
- Spring Security + JWT Authentication
- Spring Data JPA + Hibernate
- MySQL 8.x
- ZXing (QR Code Generation)
- Lombok

**Frontend**
- Swing
- Java FX

**Database**
- MySQL
- Auto schema generation via Hibernate

---

## ✨ Features

- ✅ User Registration & Login
- ✅ JWT-based Authentication
- ✅ Role-based Access Control (ADMIN / USER)
- ✅ Create, View, Update, Delete Tickets
- ✅ Event Management
- ✅ QR Code Generation per Ticket
- ✅ QR Code Verification at Entry Gate
- ✅ One-time QR Scan (ticket marked as used after scan)
- ✅ Comment System on Tickets
- ✅ Password Hashing (BCrypt)
- ✅ CORS configured for React frontend

---

## 📁 Project Structure

```
src/main/java/com/tms/ticket_management/
├── config/
│   ├── CorsConfig.java
│   ├── JwtAuthFilter.java
│   ├── JwtService.java
│   ├── PasswordConfig.java
│   └── SecurityConfig.java
├── controller/
│   ├── AuthController.java
│   ├── CommentController.java
│   ├── QRCodeController.java
│   ├── TicketController.java
│   └── UserController.java
├── dto/
│   ├── AuthDTO.java
│   ├── CommentDTO.java
│   ├── TicketDTO.java
│   └── UserDTO.java
├── model/
│   ├── Comment.java
│   ├── Event.java
│   ├── Ticket.java
│   └── User.java
├── repository/
│   ├── CommentRepository.java
│   ├── TicketRepository.java
│   └── UserRepository.java
├── service/
│   ├── CommentService.java
│   ├── QRCodeService.java
│   ├── TicketService.java
│   └── UserService.java
└── TicketManagementApplication.java
```

---

## ⚙️ Setup & Installation

### Prerequisites
- Java 21
- Maven
- MySQL 8.x

### Backend Setup

**1. Clone the repository**
```bash
git clone https://github.com/AyushCodes04/Ticket-Management-System.git
cd Ticket-Management-System/spring\ backend/ticket-management
```

**2. Create MySQL database**
```sql
CREATE DATABASE ticket_management_db;
```

**3. Configure `application.properties`**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ticket_management_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
server.port=8080
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=86400000
```

**4. Run the application**
```bash
mvn spring-boot:run
```

The server starts at `http://localhost:8080`

---

## 📡 API Endpoints

### Authentication
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/auth/login` | Public | Login and get JWT token |
| POST | `/api/users/register` | Public | Register new user |

### Users
| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/users` | ADMIN | Get all users |
| GET | `/api/users/{id}` | ADMIN | Get user by ID |
| PUT | `/api/users/{id}` | ADMIN | Update user |
| DELETE | `/api/users/{id}` | ADMIN | Delete user |

### Tickets
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/tickets` | ADMIN, USER | Create ticket |
| GET | `/api/tickets` | ADMIN, USER | Get all tickets |
| GET | `/api/tickets/{id}` | ADMIN, USER | Get ticket by ID |
| GET | `/api/tickets/status/{status}` | ADMIN, USER | Get tickets by status |
| GET | `/api/tickets/user/{userId}` | ADMIN, USER | Get tickets by user |
| PUT | `/api/tickets/{id}` | ADMIN, USER | Update ticket |
| DELETE | `/api/tickets/{id}` | ADMIN, USER | Delete ticket |

### QR Code
| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/qr/generate/{ticketId}` | ADMIN, USER | Generate QR code image |
| GET | `/api/qr/verify/{qrCode}` | Public | Verify QR code |
| POST | `/api/qr/scan/{qrCode}` | Public | Scan QR at gate (marks as used) |

### Comments
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/comments` | ADMIN, USER | Add comment |
| GET | `/api/comments/ticket/{ticketId}` | ADMIN, USER | Get comments by ticket |
| DELETE | `/api/comments/{id}` | ADMIN, USER | Delete comment |

---

## 🔐 Authentication

This project uses **JWT (JSON Web Token)** authentication.

**Login to get token:**
```json
POST /api/auth/login
{
    "username": "ayush",
    "password": "ayush123"
}
```

**Use token in requests:**
```
Authorization: Bearer <your_token>
```

---

## 🗄️ Database Schema

| Table | Description |
|---|---|
| `users` | Stores user accounts |
| `tickets` | Stores event tickets with QR codes |
| `events` | Stores event details |
| `comments` | Stores comments on tickets |

> Tables are auto-created by Hibernate on first run.

---

## 📱 QR Code Flow

```
1. User registers → Login → Get JWT token
2. Create ticket → QR code ID auto-generated
3. GET /api/qr/generate/{ticketId} → Returns QR code as PNG image
4. At entry gate → Scan QR code
5. POST /api/qr/scan/{qrCode} → Ticket marked as used
6. Same QR cannot be used twice
```

---

## 📄 License

This project is for educational purposes — built as part of a B.Tech Computer Science project.

---

> Built with ❤️ by Team AlgoFlow
