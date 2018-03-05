package pl.grzegorz2047.botupvoter.database.interfaces;

import java.sql.Connection;

/**
 * Created by grzeg on 13.08.2017.
 */
public interface SQLImplementationStrategy {

    Connection getConnection() throws Exception;
}
