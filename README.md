# 👥 Employee Management API – Play Framework + Slick + MySQL

A backend API built with Play Framework and Slick, designed to manage employees and their contracts.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Run Locally](#run-locally)
- [Database Setup](#database-setup)
- [Config Overview](#config-overview)
- [API Endpoints](#api-endpoints)
- [What I Learned](#what-i-learned)
- [Future Improvements](#future-improvements)
- [Author](#author)

---

## 📌 Overview

This is a backend-only MVP for managing employees and their contracts.  
Built with **Scala**, **Play Framework**, **Slick**, and **MySQL**.  
Uses a clean structure with seeding logic triggered on startup.

---

## 🚀 Run Locally

### Prerequisites

- [x] MySQL installed and running
- [x] SBT installed → [https://www.scala-sbt.org/download.html](https://www.scala-sbt.org/download.html)

### Steps

```bash
git clone https://github.com/edpau/scl-employee-api.git
cd scl-employee-api
sbt run
```

You should see output like:

```
Runs on startup
Seeding succeeded
```

---

## 🛠️ Database Setup

Make sure MySQL is running, then create the database:

```sql
CREATE DATABASE scl_employee_db
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

This ensures support for all Unicode characters (e.g. emojis, multilingual text).

---

## ⚙️ Config Overview (`application.conf`)

```hocon
# DB config
slick.dbs.default.profile = "slick.jdbc.MySQLProfile$"
slick.dbs.default.db.driver = "com.mysql.cj.jdbc.Driver"
slick.dbs.default.db.url = "jdbc:mysql://localhost:3306/scl_employee_db?characterEncoding=UTF-8&useUnicode=true"
slick.dbs.default.db.user = "root"
slick.dbs.default.db.user = ${?DB_USER}
slick.dbs.default.db.password = ""
slick.dbs.default.db.password = ${?DB_PASSWORD}

# Evolutions
play.evolutions.db.default.autoApply = true
play.evolutions.enabled = true

# Application Secret
play.http.secret.key = "changeme"
play.http.secret.key = ${?APPLICATION_SECRET}

# --- Play Modules ---
play.modules.enabled += "Module"
```

✅ This supports environment variable overrides — great for production.

---

## 📡 API Endpoints

Base URL: `http://localhost:9000`

### 👤 Employees

| Method | Endpoint          | Description                 |
|--------|-------------------|-----------------------------|
| GET    | /employees        | Get all employees           |
| GET    | /employees/:id    | Get a specific employee     |
| POST   | /employees        | Create a new employee       |
| PUT    | /employees/:id    | Update an employee          |
| DELETE | /employees/:id    | Delete an employee          |

#### 📥 Example – Create Employee (POST /employees)

```json
{
  "firstName": "Peter",
  "lastName": "Pan",
  "email": "peter.pan@example.com",
  "mobileNumber": "07123456789",
  "address": "Neverland"
}
```

#### 📥 Example – Update Employee (PUT /employees/:id)

```json
{
  "firstName": "Peter",
  "lastName": "Pan",
  "email": "peter.pan@example.com",
  "mobileNumber": "07987654321",
  "address": "London, UK"
}
```

---

### 📄 Contracts

| Method | Endpoint            | Description                 |
|--------|---------------------|-----------------------------|
| GET    | /contracts          | Get all contracts           |
| GET    | /contracts/:id      | Get a specific contract     |
| POST   | /contracts          | Create a new contract       |
| PUT    | /contracts/:id      | Update a contract           |
| DELETE | /contracts/:id      | Delete a contract           |

#### 📥 Example – Create Contract (POST /contracts)

```json
{
  "employeeId": 1,
  "contractType": "Permanent",
  "employmentType": "Full-time",
  "startDate": "2023-08-01",
  "endDate": null,
  "hoursPerWeek": 40
}
```

#### 📥 Example – Update Contract (PUT /contracts/:id)

```json
{
  "employeeId": 1,
  "contractType": "Fixed-term",
  "employmentType": "Part-time",
  "startDate": "2023-08-01",
  "endDate": "2024-08-01",
  "hoursPerWeek": 20
}
```

---

### 🧠 Why `id` Uses `Int` in This Project

For this learning project, `id` fields are defined as `Int` for simplicity and readability.  
In production systems, `Long` (`BIGINT`) is typically used to support very large datasets and prevent overflow.

---

## 📚 What I Learned

- How to structure a Play + Slick backend using domain-based layering 
- How Play Evolutions version and manage schema changes
- How to implement startup seed logic using Guice and Play's lifecycle
- How to combine DTOs with custom insert projections in Slick
- How to securely configure Play with environment variable fallback

See full log → [`docs/DEVLOG.md`](docs/DEVLOG.md)
---

## 🌱 Future Improvements

- [ ] Soft delete (archiving) for employees

---

## 👤 Author

- 🧑‍💻 GitHub: [@edpau](https://github.com/edpau)
- 🌐 Website: [edpau.me](https://edpau.me)
