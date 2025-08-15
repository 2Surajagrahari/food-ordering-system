# food-ordering-system

# 🍔 Food Ordering System with VIP Priority

A Java/Spark backend with vanilla JS frontend demonstrating a priority queue system for handling food orders with VIP customers getting priority.

## 🌟 Features
- **VIP Priority Queue**: Min-heap implementation prioritizing VIP orders
- **Real-time Order Management**: 
  - Place new orders
  - View order queue
  - Process orders in priority sequence
- **Billing System**: Automatic total calculation
- **Responsive UI**: Works on mobile and desktop

## 🛠️ Tech Stack
| Component       | Technology               |
|----------------|--------------------------|
| Backend        | Java 11, SparkJava       |
| Frontend       | Vanilla JS, HTML5, CSS3  |
| Data Structure | Custom Min-Heap          |
| Build Tool     | Maven                    |

## 🚀 Setup Guide

### Prerequisites

- JDK 11+
- Maven 3.6+
- Modern browser (Chrome/Firefox)

### Installation
1. **Clone the repository**
   
   git clone https://github.com/2Surajagrahari/food-ordering-system.git
   cd food-ordering-system

### Build the backend

   cd backend
   mvn clean package

### Run the system
   
   -> Start backend:
   
   java -jar target/food-ordering-system-1.0-SNAPSHOT.jar
   
   -> Open frontend:

  Direct file access: frontend/index.html
  Or with Live Server in VS Code

🏗️ Project Structure

food-ordering-system/

├── backend/

│   ├── src/main/java/com/restaurant/

│   │   ├── Order.java          # Order entity

│   │   ├── FoodOrderQueue.java # Priority Queue

│   │   ├── FoodOrderAPI.java   # REST endpoints

│   │   └── Main.java           # Entry point

│   └── pom.xml                 # Maven config

└── frontend/

   ├── index.html              # Main UI
    ├── styles.css              # Styling
    └── app.js                  # Frontend logic

📡 API Endpoints
Endpoint	Method	Description

/menu	      GET	     Get available menu items

/orders	    POST	  Submit new order

/queue	    GET	    View current order queue

/next	      GET	    Get next order to process


📜 License
MIT License - See LICENSE

Happy coding! 🚀
For support, contact: surajagrahari265@gmail.com
