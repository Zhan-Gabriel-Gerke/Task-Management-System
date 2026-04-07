
# Task-Management-System

## 📌 About This Project
The **Task Management System** is a RESTful API for managing users and tasks, similar to Jira or Trello.  
It provides secure authentication, task assignment, status tracking, and a nested commenting system.

---

## 🛠 Tech Stack

| Category | Technologies |
|---------|-------------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.5.10 (Web, Security, Data JPA) |
| **Authentication** | JWT |
| **Database** | PostgreSQL 18 |
| **Mapping** | MapStruct |
| **Infrastructure** | Docker, Docker Compose |
| **Testing** | JUnit 5, MockMvc, Testcontainers |

---

## 🚀 Core Features

### 🔐 Authentication
- Registration & login  
- Stateless JWT authorization

### 📋 Task Management
- CRUD operations for tasks  
- Fields: title, description, status

### 🔒 Access Control
- Only author or assignee can update status  
- Only author can change assignee

### 🔎 Filtering
- Pagination  
- Filter by author or assignee email

### 💬 Comments
- Nested comment threads per task

### ⚠️ Error Handling
- Global exception handler  
- Structured validation & access error responses

---

## 🧰 Getting Started

### ✔ Prerequisites
- Java 21+
- Docker & Docker Compose
- Gradle 8.14 (wrapper included)

---

## ⚙ Setup

### 1️⃣ Clone the repository
```bash
git clone <repository-url>
cd Task-Management-System
```

### 2️⃣ Environment Variables
Create a `.env` file based on `.env-example`:

```
POSTGRES_DB=prod_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_password
POSTGRES_PORT=5432
JWT_SECRET_KEY=your_secret_key
JWT_EXPIRATION=86400000
```

### 3️⃣ Start PostgreSQL
```bash
docker-compose up -d
```

### 4️⃣ Run the Application
```bash
./gradlew bootRun
```

---

## 📡 API Summary

### 🔐 Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and receive JWT |

### 📝 Tasks
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/tasks` | Create a task |
| GET | `/api/tasks` | List tasks (pagination + filtering) |
| PATCH | `/api/tasks/{id}/status` | Update task status |
| PATCH | `/api/tasks/{id}/assignee` | Update task assignee |

### 💬 Comments
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/tasks/{id}/comments` | Add a comment |
| GET | `/api/tasks/{id}/comments` | List comments |

---

## 🧪 Testing
Run tests (Testcontainers will start PostgreSQL automatically):

```bash
./gradlew test
```
```