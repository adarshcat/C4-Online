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
