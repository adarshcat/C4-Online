package c4online.db;

import c4online.sessions.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RatingDB {
    private static final String ratingsTable = "ratings";

    private Connection conn;

    public RatingDB(Connection _conn) {
        conn = _conn;
    }

    public boolean updateRating(int userId, int ratingChange) {
        String sign = (ratingChange < 0)?"-":"+";
        try {
            String sqlQuery = "UPDATE "+ratingsTable+" SET rating = rating "+sign+" ? WHERE user_id = ?";

            PreparedStatement stmt = conn.prepareStatement(sqlQuery);
            stmt.setInt(1, Math.abs(ratingChange));
            stmt.setInt(2, userId);

            int rowUpdated = stmt.executeUpdate();

            if (rowUpdated != 0) return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateGamePlayed(int userId, boolean won, boolean draw){
        try {
            String gamePlayedIncr = "UPDATE "+ratingsTable+" SET games_played = games_played + 1 WHERE user_id = ?";

            PreparedStatement stmt = conn.prepareStatement(gamePlayedIncr);
            stmt.setInt(1, userId);

            stmt.executeUpdate();

            if (!draw) {
                String gameOutcome = won?"games_won":"games_lost";
                String gameOutcomeIncr = "UPDATE " + ratingsTable + " SET "+gameOutcome+" = "+gameOutcome+" + 1 WHERE user_id = ?";

                PreparedStatement stmt2 = conn.prepareStatement(gameOutcomeIncr);
                stmt2.setInt(1, userId);

                stmt2.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean populateUserDataById(int userId, User user) {
        boolean populated = false;

        try {
            String sqlQuery = "SELECT * FROM "+ratingsTable+" WHERE user_id = ?";

            PreparedStatement stmt = conn.prepareStatement(sqlQuery);
            stmt.setInt(1, userId);

            ResultSet rset = stmt.executeQuery();

            while(rset.next()) {
                populated = true;

                user.rating = rset.getInt("rating");
                user.gamesPlayed = rset.getInt("games_played");
                user.gamesWon = rset.getInt("games_won");
                user.gamesLost = rset.getInt("games_lost");
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return populated;
    }
}
