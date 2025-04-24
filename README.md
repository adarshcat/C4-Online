# C4 Online 🎮

**C4 Online** is a fully functional, real-time multiplayer Connect 4 game featuring live matchmaking, persistent user accounts, and a rating system. Designed from the ground up, this project showcases seamless client-server communication using WebSockets, a custom Java backend, and a lightweight, interactive front-end built with p5.js.

---

## 🚀 Features

- 🔌 **Real-Time Online Multiplayer** – Challenge other players in live Connect 4 matches over WebSockets.
- 🧠 **Custom Matchmaking System** – Automatically pairs players based on availability and reconnects dropped users to ongoing games.
- 🗂️ **Persistent User Accounts** – Secure sign-up and login system with hashed authentication.
- 📊 **Player Ratings** – Tracks win/loss records and player skill ratings using a MySQL database.
- 🎨 **p5.js Frontend** – A lightweight and responsive interface with smooth animations and clean UI.
- 🧰 **Java Backend** – Handles all game logic, user sessions, and real-time networking using Jetty.
- 💾 **MySQL Integration** – Stores user credentials, match results, and ratings.

---

## 🛠 Tech Stack

### Backend
- **Java**
- **Jetty** (HTTP and WebSocket server)
- **MySQL** (User data and rating storage)
- **CopyOnWriteArrayList** (Thread-safe management of active games and players)
- **Custom Session + Matchmaker Logic**

### Frontend
- **HTML / CSS / JavaScript**
- **p5.js** (Game UI and canvas rendering)

---

## 📦 Getting Started

1. Clone the repository.
2. Set up your MySQL database with the provided schema.
3. Configure your database credentials in the backend.
4. Run the Java server (`Jetty`) to start handling HTTP and WebSocket traffic.
5. Open the front-end in a browser to start playing.

---

## 📌 Notes

- If a player disconnects, the system supports **reconnection** by matching player IDs and restoring the session.
- If a player is inactive for more than 1 minute, the other player is declared the winner.

---

## ✨ Future Improvements

- Add in-game chat
- Match history and leaderboard
- Friend system and rematch option

---

## 👤 Author

**Adarsh Bharti**  
Connect with me on [LinkedIn](https://www.linkedin.com/in/adarsh-bharti-26aa3a353/) or visit my [Portfolio](https://adarshcat.github.io)

---

