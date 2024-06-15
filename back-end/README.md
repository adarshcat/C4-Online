
# C4 Online

 * ## Database Structure (SQL)

    1. Users:
    	- id (Primary Key, Auto Increment)
    	- username (Unique, VARCHAR)
    	- password_hash (VARCHAR)
    	- email (Unique, VARCHAR)
    	- created_at (TIMESTAMP)
    	- last_login (TIMESTAMP)
    	
    2. Games:
        - id (Primary Key, Auto Increment)
        - player1_id (Foreign Key to Users.id)
        - player2_id (Foreign Key to Users.id)
        - status (ENUM: 'ongoing', 'completed', 'abandoned')
        - winner_id (Foreign Key to Users.id, Nullable)
        - created_at (TIMESTAMP)
        - completed_at (TIMESTAMP, Nullable)
    
    3. Moves:
        - id (Primary Key, Auto Increment)
        - game_id (Foreign Key to Games.id)
        - player_id (Foreign Key to Users.id)
        - column (INT)
        - move_number (INT)
        - created_at (TIMESTAMP)
    
    4. Rankings:
        - id (Primary Key, Auto Increment)
        - user_id (Foreign Key to Users.id)
        - ranking_points (INT)
        - games_played (INT)
        - games_won (INT)
        - games_lost (INT)
        - last_game (TIMESTAMP)