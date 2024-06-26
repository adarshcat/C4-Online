package c4online.game;

import c4online.db.DatabaseManager;
import c4online.sessions.SessionManager;
import c4online.sessions.User;

public class RatingManager {

    static int calcRatingChange(int player1Rating, int player2Rating, Player.type winner){
        int ratingDifference;
        if (winner == Player.type.PLAYER1){
            ratingDifference = player2Rating - player1Rating;
        } else if (winner == Player.type.PLAYER2){
            ratingDifference = player1Rating - player2Rating;
        } else return 0;

        return ratingFunc(ratingDifference);
    }

    static int ratingFunc(int ratingDifference){
        int ratingPoints = ratingDifference/25;
        int apparentRating = 16 + ratingPoints;
        apparentRating = Math.max(apparentRating, 0);
        apparentRating = Math.min(apparentRating, 32);

        return apparentRating;
    }

    static void updateRatingInDB(int player1dr, int player2dr, int player1id, int player2id){
        DatabaseManager.ratingdb.updateRating(player1id, player1dr);
        DatabaseManager.ratingdb.updateRating(player2id, player2dr);

        // after changing the ratings in the database, ask the session manager to reload the data
        User updatedUser1 = DatabaseManager.getUserDataById(player1id);
        User updatedUser2 = DatabaseManager.getUserDataById(player2id);

        SessionManager.updateSessionByUserId(player1id, updatedUser1);
        SessionManager.updateSessionByUserId(player2id, updatedUser2);
    }
}
