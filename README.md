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
- OpenExchangeRates API 
- CSV file storage that saves the transactions to a file called transactions.csv

## 🚀 Getting Started

### Prerequisites

- Java 11 or higher
- Gradle installed
- An active Internet connection (for exchange rate API)

### Build & Run

```bash
./gradlew build
./gradlew run
