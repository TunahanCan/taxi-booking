# Taxi Booking Orchestrator Service

The **Taxi Booking Orchestrator Service** manages the entire workflow of a taxi booking process in a microservices architecture. It coordinates between different services like `Payment Service`, `Driver Service`, and `Booking Service` by sending trigger messages and listening for status updates via Kafka.

## Table of Contents
- [Overview](#overview)
- [Architecture](#architecture)
- [Technologies Used](#technologies-used)
- [Features](#features)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Orchestrator Workflow](#orchestrator-workflow)
- [Kafka Topics](#kafka-topics)
- [Entity and DTO Conversion](#entity-and-dto-conversion)
- [Contributing](#contributing)
- [License](#license)

## Overview

The **Orchestrator Service** handles all the steps in the taxi booking process:
1. Receives the booking request.
2. Triggers the **Payment Service** for payment authorization.
3. Upon successful payment, triggers the **Driver Service** for assigning a driver.
4. Listens to events from **Payment** and **Driver** services to track and manage the state of the workflow.

The service ensures the booking process flows correctly and handles any rollback scenarios in case of failures, such as payment failure or driver unavailability.

## Architecture

The system uses an **event-driven architecture** where different services communicate asynchronously via Kafka topics. The orchestrator coordinates and tracks the booking process across multiple services.

- **Booking Service**: Receives and validates the booking request.
- **Payment Service**: Manages the payment process.
- **Driver Service**: Assigns a driver to the booking.
- **Orchestrator Service**: Coordinates the entire process.

## Features

- **Event-Driven Orchestration**: Listens and sends messages between services to manage the booking process.
- **Saga Pattern**: Ensures that in case of failure (like payment or driver unavailability), the process can be rolled back to maintain data consistency.
- **Asynchronous Communication**: Uses Kafka for sending and receiving events between services, ensuring loose coupling and high scalability.


