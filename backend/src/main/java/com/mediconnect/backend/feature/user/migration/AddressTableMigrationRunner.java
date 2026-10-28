package com.mediconnect.backend.feature.user.migration;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AddressTableMigrationRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AddressTableMigrationRunner.class);

    private final DataSource dataSource;

    public AddressTableMigrationRunner(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            boolean addressTableExists = false;

            try (ResultSet rs = metaData.getTables(null, null, "address", new String[]{"TABLE"})) {
                if (rs.next()) {
                    addressTableExists = true;
                }
            }

            if (!addressTableExists) {
                // Also check uppercase for databases that store unquoted identifiers in uppercase
                try (ResultSet rs = metaData.getTables(null, null, "ADDRESS", new String[]{"TABLE"})) {
                    if (rs.next()) {
                        addressTableExists = true;
                    }
                }
            }

            if (addressTableExists) {
                log.info("Legacy 'address' table detected. Migrating existing address records into 'app_user'...");
                try (Statement statement = connection.createStatement()) {
                    // Migrate address records to app_user (preferring default addresses)
                    String migrationSql = """
                            UPDATE app_user u
                            SET label = a.label,
                                address_line1 = a.address_line1,
                                address_line2 = a.address_line2,
                                city = a.city,
                                state = a.state,
                                postal_code = a.postal_code,
                                country = a.country,
                                latitude = a.latitude,
                                longitude = a.longitude,
                                is_default = a.is_default
                            FROM (
                                SELECT DISTINCT ON (user_id)
                                    user_id, label, address_line1, address_line2, city, state, postal_code, country, latitude, longitude, is_default
                                FROM address
                                ORDER BY user_id, is_default DESC, address_id ASC
                            ) a
                            WHERE u.user_id = a.user_id AND u.address_line1 IS NULL
                            """;
                    int updated = statement.executeUpdate(migrationSql);
                    log.info("Migrated {} address record(s) into 'app_user'.", updated);

                    // Safely drop the legacy address table
                    statement.executeUpdate("DROP TABLE IF EXISTS address CASCADE");
                    log.info("Legacy 'address' table dropped successfully.");
                }
            }
        } catch (Exception ex) {
            log.warn("Address table migration skipped or failed (safe if offline or database uninitialized): {}", ex.getMessage());
        }
    }
}

