x# Shopflo SDET Assignment

## Overview

This repository contains the solution for the Shopflo SDET Assignment.

### Assignment 1
- Manual Test Cases for SauceDemo
- Selenium + Java Automation Framework
- Positive and Negative Test Cases
- Page Object Model (POM) Design
- TestNG Framework
- Maven Build
- Extent Reports
- GitHub Actions CI

### Assignment 2
- REST API Automation using Rest Assured
- Cart CRUD Test Suite
- Authentication Tests
- Response Schema Validation
- Data-Driven Testing

---

## Framework Choice

### Selenium + Java + TestNG

The framework is implemented using Selenium WebDriver with Java and TestNG.

### Why this framework?

- Industry standard for UI automation
- Easy integration with Maven and CI/CD
- Supports Data Providers
- Easy maintenance using Page Object Model
- Good reporting support using Extent Reports
- Highly scalable for large regression suites

---

## Framework Design

```
src
 ├── main
 │    ├── pages
 │    ├── utilities
 │    └── base
 │
 ├── test
 │    ├── tests
 │    ├── resources
 │    └── testng.xml
```

### Design Pattern

- Page Object Model (POM)
- Base Test
- Driver Factory
- Utility Classes
- TestNG Data Providers

---

## Technologies Used

- Java 21
- Selenium 4
- TestNG
- Maven
- Rest Assured
- Extent Reports
- GitHub Actions

---

## Running Tests

Clone the repository

```
git clone <repository-url>
```

Run all tests

```
mvn clean test
```

Run a specific TestNG suite

```
mvn test -DsuiteXmlFile=testng.xml
```

---

## GitHub Actions

The repository includes a GitHub Actions workflow that automatically:

- Builds the project
- Downloads dependencies
- Executes the TestNG suite
- Reports build status on every push

---

## Extension Plan

The framework can be extended by adding:

- Parallel execution using TestNG Parallel Suites
- Cross-browser execution
- Docker Grid integration
- Jenkins Pipeline integration
- Extent Reporting
- Retry Analyzer
- Screenshot capture on failures
- Environment-specific configuration
- API and UI execution in the same pipeline

---

## Reporting

- TestNG Reports
- Extent Reports
- Maven Surefire Reports

---

## Author

Ankita Dhoke