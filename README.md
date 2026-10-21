# smart-pantry-inventory-system
# Smart Pantry Inventory System

A Java Swing desktop application for managing household pantry inventory, tracking expiry dates, and organizing grocery shopping.

## Features

- User registration and login.
- Add, edit, delete, and search pantry items.
- Track quantities, expiry dates, and low-stock thresholds.
- Generate shopping lists from low-stock items.
- Update pantry stock when purchases are recorded.
- Store data locally using text files.

## Technologies

Java 17, Swing, AWT, and file-based persistence. The project demonstrates encapsulation, inheritance, interfaces, and the observer pattern.

## Getting Started

Install JDK 17 or later, then run these commands from the project folder:

**macOS/Linux:**

```bash
bash build.sh
java -cp build/classes application.LoginFrame
```

**Windows PowerShell:**

```powershell
.\build.ps1
java -cp build/classes application.LoginFrame
```

Use **Sign Up** to create an account. Pantry inventory is shared across accounts in the current implementation.

## Project Structure

| Folder | Purpose |
| --- | --- |
| `src/application/` | Graphical interface |
| `src/models/` | Application models |
| `src/managers/` | User and shopping-list management |
| `src/service/` | Pantry operations |
| `src/repo/` | File persistence |
| `src/interfaces/` | Storage and observer interfaces |
| `images/` | Application logo |
| `examples/` | Sample inventory |

Developed for **COSC 230 — Object-Oriented Programming**.
