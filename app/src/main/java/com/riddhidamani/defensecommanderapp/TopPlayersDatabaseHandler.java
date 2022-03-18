package com.riddhidamani.defensecommanderapp;

import android.os.Handler;
import android.os.Looper;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TopPlayersDatabaseHandler implements Runnable {

    private final String TAG = getClass().getSimpleName();

    private final MainActivity context;
    private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());

    private Connection conn;
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String SCORES_TABLE = "AppScores";
    private final static String dbName = "chri5558_missile_defense";
    private final static String dbURL = "jdbc:mysql://christopherhield.com:3306/" + dbName;
    private final static String dbUser = "chri5558_student";
    private final static String dbPass = "ABC.123";
    private final String initials;
    private final int scores;
    private final int level;


    TopPlayersDatabaseHandler(MainActivity ctx, String initials, int scores, int level) {
        context = ctx;
        this.initials = initials;
        this.scores = scores;
        this.level = level;
    }


    @Override
    public void run() {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(dbURL, dbUser, dbPass);

            if (level != -1) {
                Statement stmt = conn.createStatement();
                String sql = "insert into " + SCORES_TABLE + " values (" +
                        System.currentTimeMillis() + ", '" + initials + "', " + scores + ", " +
                        level + ")";
                stmt.executeUpdate(sql);
                stmt.close();

                TopPlayersDetails topPlayersDetails = getAllTopTen();
                context.setResults(topPlayersDetails.getTopPlayerDetails());
            }
            else {
                TopPlayersDetails topPlayersDetails = getAllTopTen();
                new Handler(Looper.getMainLooper()).post(() -> context.finalScoresResult(topPlayersDetails));
            }
            conn.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private TopPlayersDetails getAllTopTen() throws SQLException {
        TopPlayersDetails topPlayersDetails = new TopPlayersDetails();
        Statement stmt = conn.createStatement();
        String sql = "select * from " + SCORES_TABLE + "  ORDER BY Score DESC LIMIT 10";
        StringBuilder sb = new StringBuilder();
        String response = String.format(Locale.getDefault(),
                "%5s %6s %6s %6s %15s %n", "#", "Init", "Level", "Score", "Date/Time");
        sb.append(response);

        ResultSet rs = stmt.executeQuery(sql);
        int srNum = 1;
        int lowestGameScore = 0;
        while (rs.next()) {
            long millis = rs.getLong(1);
            String initials = rs.getString(2);
            int score = rs.getInt(3);
            int level = rs.getInt(4);
            lowestGameScore = score;
            sb.append(String.format(Locale.getDefault(),
                    "%5s %6s %6s %6s %15s %n", srNum++, initials.trim(), level, score, sdf.format(new Date(millis))));
        }
        rs.close();
        stmt.close();

        String topPlayerData = sb.toString();
        topPlayersDetails.setLowestGameScore(lowestGameScore);
        topPlayersDetails.setTopPlayerDetails(topPlayerData);

        return topPlayersDetails;
    }
}
