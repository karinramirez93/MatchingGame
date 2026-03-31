# 🧠 Matching Game (JavaFX + Client/Server)

![Java](https://img.shields.io/badge/Java-17-blue)
![JavaFX](https://img.shields.io/badge/JavaFX-UI-green)
![Gradle](https://img.shields.io/badge/Build-Gradle-orange)

This project is a multiplayer matching game built with Java, JavaFX, and Gradle.
It follows a client-server architecture where two players can connect, choose a username, synchronize their choices,
and play a turn-based memory game.

The purpose of this project was to practice object-oriented programming, socket communication, JavaFX interface design,
and synchronization between two players in a shared game session.

---

## 🚀 Features

- Multiplayer matching game for 2 players
- Client-server communication using sockets
- Username selection and lobby system
- Start confirmation between both players
- Difficulty selection with synchronization
- Turn-based board interaction
- Live score updates during the match
- Endgame result display in the main menu
- Exit synchronization between players

---

## 🖼️ Application Flow

### 1. Connect to the Server
The client starts by asking for the server host and port.

![Connect](https://raw.githubusercontent.com/karinramirez93/MatchingGame/ed310a90f6da9b35c3dfd358da288030913db4f4/images/1-connect.png.png)
---

### 2. Enter Username
Each player chooses a username before joining the lobby.

![Username](https://raw.githubusercontent.com/karinramirez93/MatchingGame/ed310a90f6da9b35c3dfd358da288030913db4f4/images/2-username.png.png)


---

### 3. Lobby Screen
The first connected player waits until a second player joins.

![Lobby](https://raw.githubusercontent.com/karinramirez93/MatchingGame/ed310a90f6da9b35c3dfd358da288030913db4f4/images/3-lobby.png.png)


---

### 4. Main Menu
Once both players are connected, they reach the main menu and can choose whether to start the match or exit.

![Main Menu](https://raw.githubusercontent.com/karinramirez93/MatchingGame/ed310a90f6da9b35c3dfd358da288030913db4f4/images/4-main-menu.png.png)


---

### 5. Start Synchronization
If one player chooses to start, the other player is notified and must choose the same option to continue.

![Start Sync](https://raw.githubusercontent.com/karinramirez93/MatchingGame/ed310a90f6da9b35c3dfd358da288030913db4f4/images/5-start-sync.png.png)

---

### 6. Difficulty Selection
Players can choose the difficulty level for the game: Easy, Medium, or Hard.

![Difficulty](https://raw.githubusercontent.com/karinramirez93/MatchingGame/ed310a90f6da9b35c3dfd358da288030913db4f4/images/6-difficulty.png.png)

---

### 7. Difficulty Synchronization
If one player selects a difficulty, the other player must select the same difficulty so the match can begin.

![Difficulty Sync](https://raw.githubusercontent.com/karinramirez93/MatchingGame/ed310a90f6da9b35c3dfd358da288030913db4f4/images/7-difficulty-sync.png.png)

---

### 8. Game Start
Once the difficulty is synchronized, the board is created and the game begins.

![Game Start](https://raw.githubusercontent.com/karinramirez93/MatchingGame/ed310a90f6da9b35c3dfd358da288030913db4f4/images/8-game-start.png.png)

---

### 9. Gameplay
Players reveal cards and try to find matching pairs. The score updates as matches are found.

![Gameplay](https://raw.githubusercontent.com/karinramirez93/MatchingGame/ed310a90f6da9b35c3dfd358da288030913db4f4/images/9-gameplay.png.png)

---

### 10. Turn Management
The interface clearly shows whose turn it is and whether the current player must wait.

![Turns](https://raw.githubusercontent.com/karinramirez93/MatchingGame/ed310a90f6da9b35c3dfd358da288030913db4f4/images/10-turns.png.png)

---

### 11. Match Results
When the game ends, the final result is displayed in the main menu, including the winner or a draw and the final score.

![Results](https://raw.githubusercontent.com/karinramirez93/MatchingGame/ed310a90f6da9b35c3dfd358da288030913db4f4/images/11-results.png.png)

---

### 12. Exit Handling
If one player chooses to exit, the other player is sent back to lobby waiting for another player to connect.

![Exit](https://raw.githubusercontent.com/karinramirez93/MatchingGame/ed310a90f6da9b35c3dfd358da288030913db4f4/images/12-oneOfThePlayerPressed_Exit.png)

---

## 🛠️ Technologies Used

- Java
- JavaFX
- Gradle
- TCP sockets
- Client-server architecture
- Object-oriented programming

---

## ▶️ How to Run the Project

### 1. Start the server
Run the server class first from your IDE.
src/main/java/server/Server.java


### 2. Run the client
Use Gradle to launch the JavaFX client:

```bash
gradle run