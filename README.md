# GridScore: Total Tic-Tac-Toe

## 🎮 CORE GAME RULES (Total Score Variant)
The game is played on a 9x9 grid composed of nine 3x3 small boards.

Movement: The position of a player's move on any small board determines which small board the opponent must play in next (Standard Ultimate Tic-Tac-Toe "Send Mechanic").

No Claims: Small boards are never claimed or marked as won. Play continues on all boards until all squares are filled.

Game End: The game ends only when all 81 squares across the entire grid are filled.

Win Condition (The Tweak): The winner is the player who achieves the highest total count of 3-in-a-row streaks (horizontal, vertical, or diagonal) across all 9 small boards combined.

Special Rule (Free Move): If a player is "sent" to a small board that is completely full, they get a "free move" and can play on any small board that still has an empty square.

## 💻 GAME MODES & AI LOGIC
A. OFFLINE MODE: Play vs. Computer
Requirement: Must function completely offline. AI logic must be within the app's domain layer (pure Kotlin).

AI Execution: AI decision-making must run on a background Coroutine (Dispatchers.Default) to ensure zero UI jank.

Difficulty Tiers:

Easy: Random valid move.

Medium: 1-Ply Lookahead (Prioritizes 1-move win/block on the active board). Uses a simple heuristic to "send" the human player to the least advantageous board (e.g., nearly full or where the human has no existing 2-in-a-row).

Hard: Implements the Minimax Algorithm (or Alpha-Beta Pruning) with a limited search depth (4-6 plies). The core of this AI is a custom Evaluation Function that scores the game state based on the calculated potential of the final total 3-in-a-row score (AI streaks minus Human streaks).

B. ONLINE MODE: Player vs. Player
Real-time: Utilizes Firestore Snapshot Listeners to sync game state in real-time.

Matchmaking: Simple "Create Game Room" with a shareable ID/Code, and a "Join Game" screen.

In-Game Chat: A simple, real-time, collapsible text chat for the two connected players.

## 💾 DATA PERSISTENCE & CACHING (Performance)
The application must implement the Repository Pattern for a responsive, Offline-First experience.

Room Database (Caching):

Used to cache all non-real-time user data: UserProfile (XP, Level, total streaks), and Leaderboard data.

Caching Strategy: When loading Profile or Leaderboards, data must be shown instantly from Room, followed by an update from Firestore in the background to ensure responsiveness.

Jetpack DataStore (Preferences):

Used for local user settings only: User-selected Theme and Marker Style (unlocked items).

Firebase Firestore:

Single Source of Truth for Online Game State and all leaderboard updates.

## ✨ GAMIFICATION & UI/UX
XP & Leveling: A system where players gain XP for matches completed and streaks achieved, with a Leveling component displayed prominently on the user profile.

Leaderboards: Two Firestore-backed global leaderboards: Total Wins and a competitive Skill Rating (SR) system.

Customization: Level-based unlocks for different app themes and custom "X" / "O" marker styles (persisted in DataStore).

Critical UI: The active small board must have a highly visible, contrasting Jetpack Compose visual highlight (e.g., animated border) to clearly show where the player is forced to play.

Final Tally Screen: A dedicated post-game screen that visually and numerically animates the counting of total 3-in-a-row streaks for both players before declaring the winner and showing XP/Level gains.
