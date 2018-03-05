package pl.grzegorz2047.botupvoter.database.mysql;

import com.zaxxer.hikari.HikariDataSource;
import pl.grzegorz2047.botupvoter.database.interfaces.SQLImplementationStrategy;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by grzeg on 13.08.2017.
 */
public class MySQLImplementationStrategy implements SQLImplementationStrategy {


    private HikariDataSource hikari = new HikariDataSource();

    public MySQLImplementationStrategy(String host, int port, String user, String password, String dbName) {

        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", host);
        hikari.addDataSourceProperty("port", port);
        hikari.addDataSourceProperty("databaseName", dbName);
        hikari.addDataSourceProperty("user", user);
        hikari.addDataSourceProperty("password", password);
        hikari.addDataSourceProperty("cachePrepStmts", true);
        hikari.addDataSourceProperty("prepStmtCacheSize", 250);
        hikari.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
    }

    @Override
    public Connection getConnection() throws Exception {

        try {
            return hikari.getConnection();
        } catch (SQLException e) {
            throw new Exception("Connection error");
        }

    }

}

