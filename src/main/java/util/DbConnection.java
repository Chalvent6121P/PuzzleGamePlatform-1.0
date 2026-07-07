package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DbConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/puzzlegame"
          + "?useSSL=false&serverTimezone=Asia/Taipei"
          + "&allowPublicKeyRetrieval=true";

    private static final String USER = "root";
    private static final String PASSWORD = "your MySQL PASSWORD";

    private DbConnection() {
    }

    public static Connection getDb() {
        try {
            return DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );
        } catch (SQLException e) {
            throw new RuntimeException(
                    "MySQL 連線失敗，請確認 MySQL 已啟動、puzzlegame Schema 存在，"
                  + "以及 DbConnection 的帳號密碼正確。",
                    e
            );
        }
    }
}
