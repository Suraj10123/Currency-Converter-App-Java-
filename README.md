# 💱 Currency Converter App

A simple Java console application to record purchase transactions and convert them into another currency using live exchange rates.

## 📦 Features

- Add a transaction with:
  - Description
  - Date (auto-set to today's date)
  - Amount in USD
- Convert and retrieve a saved transaction into a different currency using real-time exchange rates from the internet
- Save transactions to a CSV file for persistence

## 🛠️ Tech Stack

- Java
- Gradle
- OpenExchangeRates API (or another free currency API)
- CSV file storage that saves the transactions to a file called transactions.csv
- Docker (for containerizing the app)
- Kubernetes (for deployment in a containerized environment)

## 🚀 Getting Started

### Prerequisites

Before running the app, ensure you have the following installed:

- Java 11 or higher
- Gradle installed
- Internet connection for the exchange rate API
- Docker (for containerizing the app)
- Kubernetes (for deployment in a containerized environment)

### Docker

This project is Dockerized for easy deployment and testing in a containerized environment.

### Kubernetes

For deployment on Kubernetes, the app has been containerized and can be deployed using the included Kubernetes YAML files.
