# Smart Workflow Engine & Notification System (SWENS)

**Real-time Custom Workflows & Notifications for Scalable Organizations**

[GitHub Repository](https://github.com/Waseeq-Zafar/S.W.E.N.S.git)

> **Made by:** *Mohd Waseeq Zafar*

SWENS is a backend-first platform that empowers organizations to define custom workflows, manage task lifecycles, automate alerts, and deliver notifications via multiple channels — all in real-time with a scalable, event-driven architecture.

---

## 🔥 Problem Statement

In large organizations, handling real-time workflows like approvals, escalations, status transitions, and role-based notifications becomes increasingly complex. Current systems often fail at:

- **Real-time notifications at scale** – Managing alerts for large user bases
- **Event inconsistency** – Ensuring data synchronization across diverse services
- **High availability** – Maintaining system uptime under heavy loads
- **Efficient task state tracking** – Real-time monitoring of task transitions
- **Custom workflows** – Departments need tailored process automation

---

## 💡 Solution Overview

SWENS addresses these challenges through:

- **Custom Workflow Definition** – Create and manage unique processes tailored to your organization
- **Real-time Task Management** – Track task state transitions instantly
- **Multi-channel Notifications** – Deliver alerts via email and other channels
- **Scalable Event Processing** – Kafka-based architecture for high throughput
- **Secure Authentication** – JWT-based security for robust access control

---

## 🏗️ Architecture

### Full API Movement Overview

1. **User Signup**
    - Handled by `User Service`.

2. **User Login**
    - Handled by `Auth Service`, returns:
        - `accessToken`
        - `refreshToken`

3. **Admin Creates Workflow**
    - Handled by `Workflow Service`, which:
        - Assigns tasks to users.
        - If a user is not available, makes an internal **gRPC** call to `User Service` to fetch users based on roles (e.g., Admin, User).
        - Stores results in **Redis** for caching.

4. **Task Assignment in Workflow**
    - Triggers Kafka event for:
        - **Task Created**
        - **Task Updated**
        - **Task Finished**

5. **Notifications**
    - `Notification Service` consumes Kafka events and:
        - Sends **email** to admin on workflow completion.
        - Sends **notifications** to assigned users for:
            - Task Created
            - Task Updated
            - Task Finished

---

## 🧱 Microservices Overview

| Service               | Technology Stack            | Responsibility                                                                 |
|-----------------------|-----------------------------|--------------------------------------------------------------------------------|
| **API Gateway**       | Spring Cloud Gateway        | Routes requests, applies security                                              |
| **User Service**      | PostgreSQL                  | Handles user data, role management                                             |
| **Auth Service**      | Redis, JWT, gRPC            | Handles authentication, login, token management                               |
| **Task Service**      | MongoDB, Kafka, gRPC, Redis | Task CRUD operations, produces events to Kafka                                |
| **Workflow Service**  | MongoDB, Kafka              | Creates workflows, assigns tasks, produces events to Kafka                    |
| **Notification Service** | Kafka, Spring Mail          | Sends notifications and emails to users and admins                            |

---

## 🛠️ Tech Stack

### Core Components

- **Spring Boot** – Java Microservice framework
- **Apache Kafka** – Event streaming platform
- **gRPC** – Fast inter-service communication
- **Docker** – Containerization and deployment

### Data Management

- **MongoDB** – For Workflow and Task data
- **PostgreSQL** – For User data
- **Redis** – Caching and session management

### Additional Tools

- **Spring Mail** – For sending emails
- **JWT** – Authentication and Role-Based Access Control(RBAC)

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- REST API
- Docker & Docker Compose
- Apache Kafka
- MongoDB
- PostgreSQL
- Redis
- gRPC

### Installation


#### 1. Clone repository
git clone https://github.com/Waseeq-Zafar/S.W.E.N.S.git
cd S.W.E.N.S


---


### 2. Build services
mvn clean install
🔧 Additional Configuration (One-Time Setup After Build)
After running the build, perform the following steps for each of the following services:

user-service

auth-service

task-service

workflow-service

notification-service

📂 Mark Protobuf Folders as Generated Source Roots
Navigate to:
target/generated-sources/protobuf/grpc-java
→ Right-click → Select "Mark Directory as" → Choose "Generated Sources Root"

Then navigate to:
target/generated-sources/protobuf/java
→ Right-click → Select "Mark Directory as" → Choose "Generated Sources Root"

✅ This helps your IDE (e.g., IntelliJ) recognize and index the generated gRPC files correctly.

✉️ Email Configuration for Notification Service
Go to:
notification-service/src/main/resources/application.properties

Find and replace the following lines:

spring.mail.username= #YOUR_EMAIL_ID_TO_SEND
spring.mail.password= #YOUR_EMAIL_APP_PASS_FOR_MFA
🔐 Instructions for Password:
If you use 2-factor authentication, go to your email provider’s account settings.

Search for "App Passwords".

Create a new one (e.g., Mail App).

It will return a 16-character password.

Remove all spaces and paste it in spring.mail.password.

📤 Update Sender Email in Notification Logic
Open the file:
notification-service/src/main/java/.../mail/EmailService.java

In the method sendTaskNotification, find the line:

message.setFrom("email-address-here");
Replace "email-address-here" with your actual sender email address.

📨 This is the email that will appear as the From field when a user receives workflow-related notifications.


---


### 3. Start the backend server
from the parent directory where docker-compose.yml located
docker-compose up --build

---

## 📚 API Documentation
### For complete API specifications and interactive testing:

➡️ Visit the API_DOCS folder and run the api.html file
📎 API Documentation

✨ Key Features
Event-driven architecture – Kafka-based real-time communication

Horizontal scalability – Built with microservices architecture

RBAC implementation – Secure role-based access control

Multi-tenant support – Separate workflows per department/tenant

Real-time notifications – Email and in-app alerts

Workflow automation – Task lifecycle and status transition automation

## 🤝 Contributing

```bash
# Fork the repository

# Create your feature branch
git checkout -b feature/AmazingFeature

# Commit your changes
git commit -m 'Add AmazingFeature'

# Push to your branch
git push origin feature/AmazingFeature

# Open a Pull Request
```
