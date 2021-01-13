package org.jobrunr.storage.sql.common;

import org.jobrunr.JobRunrException;
import org.jobrunr.configuration.JobRunr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DatabaseCreatorTest {

    static final String SQLITE_DB1 = "/tmp/jobrunr-test.db";
    static final String SQLITE_DB2 = "/tmp/jobrunr-failing-test.db";

    @BeforeEach
    void setupJobStorageProvider() throws IOException {
        JobRunr.configure();
        Files.deleteIfExists(Paths.get(SQLITE_DB1));
    }

    @Test
    void testSqlLiteMigrationsUsingMainMethod() {
        assertThatCode(() -> DatabaseCreator.main(new String[]{"jdbc:sqlite:" + SQLITE_DB1, "", ""})).doesNotThrowAnyException();
    }

    @Test
    void testSqlLiteMigrations() {
        final DatabaseCreator databaseCreator = new DatabaseCreator(createDataSource("jdbc:sqlite:" + SQLITE_DB1));
        assertThatCode(databaseCreator::runMigrations).doesNotThrowAnyException();
        assertThatCode(databaseCreator::validateTables).doesNotThrowAnyException();
    }

    @Test
    void testValidateWithoutTables() {
        final DatabaseCreator databaseCreator = new DatabaseCreator(createDataSource("jdbc:sqlite:" + SQLITE_DB2));
        assertThatThrownBy(databaseCreator::validateTables).isInstanceOf(JobRunrException.class);
    }

    private static SQLiteDataSource createDataSource(String url) {
        SQLiteDataSource sqLiteDataSource = new SQLiteDataSource();
        sqLiteDataSource.setUrl(url);
        return sqLiteDataSource;
    }

}