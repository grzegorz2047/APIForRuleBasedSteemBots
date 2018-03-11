/*
 * Copyright 2014
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.grzegorz2047.botapi.database;

import pl.grzegorz2047.botapi.database.interfaces.SQLImplementationStrategy;
import pl.grzegorz2047.botapi.database.interfaces.SQLTables;
import pl.grzegorz2047.botapi.database.mysql.MySQLImplementationStrategy;
import pl.grzegorz2047.botapi.database.mysql.MySQLTables;
import pl.grzegorz2047.botapi.database.sqlite.SQLiteImplementationStrategy;
import pl.grzegorz2047.botapi.database.sqlite.SQLiteTables;
 
import java.sql.Connection;
import java.sql.Statement;

public final class SQLHandler {

    private String sqlTablePrefix = "bot_";
     
    private Statement statement;
    private SQLImplementationStrategy implementation;
    private SQLTables tables;

    private String cuboidsTableName = "`" + sqlTablePrefix + "cuboids`";
    private String playersTableName = "`" + sqlTablePrefix + "players`";
    private String alliesTableName = "`" + sqlTablePrefix + "allies`";
    private String guildsTableName = "`" + sqlTablePrefix + "guilds`";
    private String guildColumn = "guild";
    private String lastseennameColumn = "lastseenname";
    private String killsColumn = "kills";
    private String deathsColumn = "deaths";
    private String eloColumn = "elo";
    private String uuidColumn = "uuid";

    public SQLHandler( ){
    }


    public void loadSQLNames(String sqlTablePrefix) {
        if (sqlTablePrefix.length() <= 10 && sqlTablePrefix.length() >= 3) {
            this.sqlTablePrefix = sqlTablePrefix;
        } else {
            System.out.println("Could not load SQL table prefix - too low (3 chars) or too long (10 chars). Setting to default openguild_");

        }
        cuboidsTableName = "`" + sqlTablePrefix + "cuboids`";
        playersTableName = "`" + sqlTablePrefix + "players`";
        alliesTableName = "`" + sqlTablePrefix + "allies`";
        guildsTableName = "`" + sqlTablePrefix + "guilds`";

    }

    /**
     * This method connects plugin with database using informations from
     * configuration file.
     */
    public void loadDB(String host, int port, String user, String pass, String name) {
        Database database = Database.valueOf("sqlite".toUpperCase());
        String fileDir =  /*"file-dir",*/ "plugins/OpenGuild/og.db";

         switch (database) {
            case FILE:
                System.out.println("[SQLite] Connecting to SQLite database ...");
                implementation = new SQLiteImplementationStrategy(fileDir);
                tables = new SQLiteTables();
                System.out.println("[SQLite] Connected to SQLite successfully!");
                break;
            case MYSQL:
                implementation = new MySQLImplementationStrategy(host, port, user, pass, name);
                tables = new MySQLTables();
                break;
            default:
                System.out.println("[MySQL] Invalid database type '" + database.name() + "'!");
                implementation = new SQLiteImplementationStrategy(fileDir);
                tables = new SQLiteTables();
                System.out.println("[MySQL] Invalid database type! Setting db to SQLite!");
                break;
        }
    }

    public Connection getConnection() throws Exception {
        return implementation.getConnection();
    }

    public void startWork( ) {
        // Create tables is they doesn't exists
        tables.createTables(this);
    }
 
    public String getCuboidsTableName() {
        return cuboidsTableName;
    }

    public String getPlayersTableName() {
        return playersTableName;
    }

    public String getAlliesTableName() {
        return alliesTableName;
    }

    public String getGuildsTableName() {
        return guildsTableName;
    }

    public Statement createStatement() throws Exception {
        statement = this.getConnection().createStatement();
        return statement;
    }

    
}



