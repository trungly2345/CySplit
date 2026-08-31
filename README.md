# 3_Hosen_3 — Expense Splitting Application

## Overview

This project was developed as part of **COM S 309: Software Development Practices at Iowa State University**.

Our team built an expense-splitting application designed to help users manage shared expenses between friends or groups. The application allows users to organize shared expenses and keep track of how much each person owes.

The project gave our team experience building a complete software system using a client-server architecture, REST APIs, database persistence, version control, and Agile development practices.

## My Role

I primarily worked on the **backend development** of the application.

My responsibilities included:

- Designing and implementing backend functionality using **Java and Spring Boot**
- Creating **RESTful API endpoints** used by the Android client
- Implementing application logic for users, groups, expenses, and balances
- Connecting backend services to persistent data storage
- Designing request and response structures between the frontend and backend
- Testing API endpoints during development
- Debugging integration issues between the Android application and backend
- Collaborating with teammates through GitLab branches and issue tracking

This project strengthened my understanding of how frontend applications communicate with backend services and how application state is managed across multiple users.

## Tech Stack

### Backend

- Java
- Spring Boot
- REST APIs
- Maven

### Frontend

- Android Studio
- Android application client

### Development Tools

- Git
- GitLab
- Postman
- IntelliJ IDEA

## Architecture

The application follows a client-server architecture:

```text
Android Application
        |
        | HTTP / REST
        v
Spring Boot Backend
        |
        | Business Logic
        v
Persistent Data Storage
```

The Android client communicates with the Spring Boot server through REST endpoints.

The backend is responsible for validating requests, processing application logic, updating stored data, and returning structured responses to the client.

## Core Features

### User Management

Users can maintain accounts that identify them throughout the application.

### Groups

Users can organize themselves into groups for situations such as:

- Roommates
- Trips
- Events
- Shared purchases

### Expense Tracking

Expenses can be associated with users or groups so that shared costs can be recorded and tracked.

### Balance Calculation

The backend processes expense information to determine balances between members of a group.

For example:

```text
Dinner: $90

Alex paid: $90

3 group members
----------------
Alex:   +$60
Sam:    -$30
Chris:  -$30
```

This allows the application to keep track of who paid and who still owes money.

## Backend API

The backend exposes RESTful endpoints that allow the Android application to interact with application data.

Conceptually, the API follows patterns such as:

```http
GET /users
GET /groups
GET /expenses

POST /users
POST /groups
POST /expenses

PUT /expenses/{id}

DELETE /expenses/{id}
```

The backend separates HTTP request handling from the application's business logic and data management.

## What I Learned

This project was valuable because it required more than completing isolated programming assignments. Our team had to build multiple parts of a software system that communicated with each other.

Some of the main concepts I gained experience with include:

- Designing RESTful APIs
- Building backend services with Spring Boot
- Structuring a Java backend project
- Client-server communication
- Handling application state and persistent data
- Debugging API integration issues
- Using Git branches in a team environment
- Resolving merge conflicts
- Breaking larger features into development tasks
- Collaborating within an Agile software development workflow

The project also gave me more experience with **backend architecture and API design**, which is an area of software engineering I am particularly interested in.

## Development Workflow

Development was managed through **GitLab**.

Our workflow included:

```text
Issue / Feature
      |
      v
Feature Branch
      |
      v
Implementation
      |
      v
Testing
      |
      v
Merge
      |
      v
Integration
```

This allowed team members to work independently on different parts of the application while maintaining a shared codebase.

## Running the Project

### Backend

Clone the repository:

```bash
git clone <repository-url>
cd <repository-directory>
```

Run the Spring Boot backend using Maven:

```bash
./mvnw spring-boot:run
```

Alternatively, run the Spring Boot application directly through IntelliJ IDEA.

### Android Client

Open the Android portion of the project in **Android Studio**, configure the backend server address, and run the application using an Android emulator or compatible Android device.

## Project Context

- **Course:** COM S 309 — Software Development Practices
- **University:** Iowa State University
- **Project Type:** Team Software Engineering Project
- **Primary Focus:** Full-stack application development
- **My Primary Contribution:** Backend development and API implementation