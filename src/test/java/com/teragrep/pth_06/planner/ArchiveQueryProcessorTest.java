/*
 * Teragrep Archive Datasource (pth_06)
 * Copyright (C) 2021-2024 Suomen Kanuuna Oy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 * Additional permission under GNU Affero General Public License version 3
 * section 7
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with other code, such other code is not for that reason alone subject to any
 * of the requirements of the GNU Affero GPL version 3 as long as this Program
 * is the same Program as licensed from Suomen Kanuuna Oy without any additional
 * modifications.
 *
 * Supplemented terms under GNU Affero General Public License version 3
 * section 7
 *
 * Origin of the software must be attributed to Suomen Kanuuna Oy. Any modified
 * versions must be marked as "Modified version of" The Program.
 *
 * Names of the licensors and authors may not be used for publicity purposes.
 *
 * No rights are granted for use of trade names, trademarks, or service marks
 * which are in The Program if any.
 *
 * Licensee must indemnify licensors and authors for any liability that these
 * contractual assumptions impose on licensors and authors.
 *
 * To the extent this program is licensed as part of the Commercial versions of
 * Teragrep, the applicable Commercial License may apply to this file if you as
 * a licensee so wish it.
 */
package com.teragrep.pth_06.planner;

import com.teragrep.pth_06.config.Config;
import com.teragrep.pth_06.jooq.generated.journaldb.tables.records.LogfileRecord;
import org.jooq.DSLContext;
import org.jooq.Record9;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.ULong;
import org.jooq.types.UShort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static com.teragrep.pth_06.jooq.generated.journaldb.Journaldb.JOURNALDB;

class ArchiveQueryProcessorTest {

    private MariaDBContainer<?> mariadb;
    private Connection connection;
    private ZoneId zoneId = ZoneId.of("America/New_York");
    private final String streamDBUsername = "streamdb";
    private final String streamDBPassword = "streamdb_pass";

    private final String streamdbName = "streamdb";
    private final String journaldbName = "journaldb";
    private final Map<String, String> opts = new HashMap<String, String>() {

        {
            put("S3endPoint", "mock");
            put("S3identity", "mock");
            put("S3credential", "mock");
            put("DBusername", streamDBUsername);
            put("DBpassword", streamDBPassword);
            put("DBstreamdbname", streamdbName);
            put("DBjournaldbname", journaldbName);
            put("queryXML", "<index value=\"example\" operation=\"EQUALS\"/>");
            put("archive.enabled", "true");
        }
    };

    @BeforeEach
    void setUp() {
        // Start mariadb testcontainer with timezone set to America/New_York (UTC-4). Also creates a second streamdb database inside the container alongside the default journaldb.
        mariadb = Assertions
                .assertDoesNotThrow(() -> new MariaDBContainer<>(DockerImageName.parse("mariadb:10.5")).withPrivilegedMode(false).withUsername(streamDBUsername).withPassword(streamDBPassword).withCommand("--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci", "--default-time-zone=" + zoneId.getId()).withDatabaseName(journaldbName).withCopyFileToContainer(MountableFile.forClasspathResource("CREATE_STREAMDB_DB.sql"), "/docker-entrypoint-initdb.d/"));
        mariadb.start();
        connection = Assertions
                .assertDoesNotThrow(
                        () -> DriverManager
                                .getConnection(mariadb.getJdbcUrl(), mariadb.getUsername(), mariadb.getPassword())
                );
        // streamdb and journaldb is populated with test data during MariaDBContainer startup using CREATE_STREAMDB_DB.sql. Logfile table of journaldb is left empty for tests to populate it.
    }

    @AfterEach
    void tearDown() {
        Assertions.assertDoesNotThrow(() -> connection.close());
        mariadb.stop();
    }

    private LogfileRecord logfileRecordForEpoch(long epoch, boolean hasNullEpochColumns) {
        Instant instant = Instant.ofEpochSecond(epoch);
        ZonedDateTime zonedDateTime = instant.atZone(zoneId); // expects path dates to be in same timezone as mariadb system timezone
        int year = zonedDateTime.getYear();
        // format 0 in front of string if 1-9
        String month = String.format("%02d", zonedDateTime.getMonthValue());
        String day = String.format("%02d", zonedDateTime.getDayOfMonth());
        String hour = String.format("%02d", zonedDateTime.getHour());

        String filename = "example.log-@" + epoch + "-" + year + month + day + hour + ".log.gz";
        String path = year + "/" + month + "-" + day + "/example.tg.dev.test/example/" + filename;
        LogfileRecord logfileRecord = new LogfileRecord(
                ULong.valueOf(ThreadLocalRandom.current().nextLong(0L, Long.MAX_VALUE)),
                Date.valueOf(zonedDateTime.toLocalDate()),
                Date.valueOf(zonedDateTime.plusYears(1).toLocalDate()),
                UShort.valueOf(1),
                path,
                null,
                UShort.valueOf(1),
                filename,
                new Timestamp(epoch),
                ULong.valueOf(120L),
                "sha256 checksum 1",
                "archive tag 1",
                "oldExample",
                UShort.valueOf(2),
                UShort.valueOf(1),
                ULong.valueOf(390L),
                ULong.valueOf(epoch),
                ULong.valueOf(epoch + (365 * 24 * 3600)),
                ULong.valueOf(epoch),
                ULong.valueOf(1),
                null
        );

        LogfileRecord nullEpochRecord = new LogfileRecord(
                ULong.valueOf(ThreadLocalRandom.current().nextLong(0L, Long.MAX_VALUE)),
                Date.valueOf(zonedDateTime.toLocalDate()),
                Date.valueOf(zonedDateTime.plusYears(1).toLocalDate()),
                UShort.valueOf(1),
                path,
                null,
                UShort.valueOf(1),
                filename,
                new Timestamp(epoch),
                ULong.valueOf(120L),
                "sha256 checksum 1",
                "archive tag 1",
                "oldExample",
                UShort.valueOf(2),
                UShort.valueOf(1),
                ULong.valueOf(390L),
                null,
                null,
                null,
                ULong.valueOf(1),
                null
        );

        if (hasNullEpochColumns) {
            return nullEpochRecord;
        }
        return logfileRecord;
    }

    @Test
    void processBetweenUnixEpochHours() {
        // Add test data to logfile table in journaldb.
        final DSLContext ctx = DSL.using(connection, SQLDialect.MYSQL);
        // Set logdate and logtime to 2023-10-04:22 UTC-4 and set epoch_hour in path to 2023-10-05:02 UTC.
        ZonedDateTime zonedDateTimeUTCLogfile = ZonedDateTime.of(2023, 10, 5, 2, 0, 0, 0, ZoneId.of("UTC"));
        LogfileRecord logfileRecord = logfileRecordForEpoch(zonedDateTimeUTCLogfile.toEpochSecond(), false);
        ctx.insertInto(JOURNALDB.LOGFILE).set(logfileRecord).execute();
        // Set logdate and logtime to 2023-10-04:23 UTC-4 and set epoch_hour in path to 2023-10-05:03 UTC.
        LogfileRecord logfileRecord2 = logfileRecordForEpoch(
                zonedDateTimeUTCLogfile.plusHours(1).toEpochSecond(), false
        );
        ctx.insertInto(JOURNALDB.LOGFILE).set(logfileRecord2).execute();

        final Map<String, String> opts = new HashMap<>(this.opts);
        opts.put("DBurl", mariadb.getJdbcUrl());
        final Long earliestEpoch = zonedDateTimeUTCLogfile.minusHours(1).toEpochSecond();
        opts.put("queryXML", "<index_earliest value=\"" + earliestEpoch + "\" operation=\"GE\"/>");
        final Config config = new Config(opts);
        ArchiveQueryProcessor aq = new ArchiveQueryProcessor(config);
        // incrementAndGetLatestOffset() should pull all logfiles to the slicetable and return epoch_hour value of logfileRecord2.
        Long latestOffset = aq.incrementAndGetLatestOffset();
        Assertions.assertEquals(logfileRecord2.getEpochHour(), ULong.valueOf(latestOffset));
        // Assert that processBetweenUnixEpochHours() can get all the logfile records.
        final Result<Record9<ULong, String, String, String, String, String, Long, ULong, ULong>> hourRange = aq
                .processBetweenUnixEpochHours(earliestEpoch, latestOffset);
        Assertions.assertEquals(2, hourRange.size());
        // Assert that the resulting logfile metadata is as expected for logtime.
        Assertions.assertEquals(logfileRecord.getEpochHour(), hourRange.get(0).get(6, ULong.class));
        Assertions.assertEquals(logfileRecord2.getEpochHour(), hourRange.get(1).get(6, ULong.class));
    }

    @Test
    void commit() {
    }

    @Test
    void getInitialOffsetFromQueryTest() {
        final Map<String, String> opts = new HashMap<>(this.opts);
        opts.put("DBurl", mariadb.getJdbcUrl());
        ZonedDateTime zonedDateTimeUTCLogfile = ZonedDateTime.of(2023, 10, 4, 0, 0, 0, 0, ZoneId.of("UTC"));
        opts
                .put(
                        "queryXML",
                        "<index_earliest value=\"" + zonedDateTimeUTCLogfile.toEpochSecond() + "\" operation=\"GE\"/>"
                );
        final Config config = new Config(opts);
        ArchiveQueryProcessor aq = new ArchiveQueryProcessor(config);
        // Assert that the initialOffset is the same as the index_earliest value of the queryXML from config.
        Assertions.assertEquals(zonedDateTimeUTCLogfile.toEpochSecond(), aq.getInitialOffset());
    }

    @Test
    void getInitialOffsetDefaultTest() {
        final Map<String, String> opts = new HashMap<>(this.opts);
        opts.put("DBurl", mariadb.getJdbcUrl());
        final Config config = new Config(opts);
        ArchiveQueryProcessor aq = new ArchiveQueryProcessor(config);
        long expectedEpoch = Instant.now().getEpochSecond() - 24 * 3600;
        long initialOffset = aq.getInitialOffset();
        // Assert that the default initialOffset is very close to expectedEpoch.
        Assertions.assertTrue(expectedEpoch >= initialOffset && (expectedEpoch - 10) < initialOffset);
    }

    @Test
    void incrementAndGetLatestOffset() {
        // Add test data to logfile table in journaldb.
        final DSLContext ctx = DSL.using(connection, SQLDialect.MYSQL);
        // Set logdate and logtime to 2023-10-04:22 UTC-4 and set epoch_hour in path to 2023-10-05:02 UTC.
        ZonedDateTime zonedDateTimeUTCLogfile = ZonedDateTime.of(2023, 10, 5, 2, 0, 0, 0, ZoneId.of("UTC"));
        LogfileRecord logfileRecord = logfileRecordForEpoch(zonedDateTimeUTCLogfile.toEpochSecond(), false);
        ctx.insertInto(JOURNALDB.LOGFILE).set(logfileRecord).execute();
        // Set logdate and logtime to 2023-10-04:23 UTC-4 and set epoch_hour in path to 2023-10-05:03 UTC.
        LogfileRecord logfileRecord2 = logfileRecordForEpoch(
                zonedDateTimeUTCLogfile.plusHours(1).toEpochSecond(), false
        );
        ctx.insertInto(JOURNALDB.LOGFILE).set(logfileRecord2).execute();

        final Map<String, String> opts = new HashMap<>(this.opts);
        opts.put("DBurl", mariadb.getJdbcUrl());
        final Long earliestEpoch = zonedDateTimeUTCLogfile.minusDays(1).toEpochSecond();
        opts.put("queryXML", "<index_earliest value=\"" + earliestEpoch + "\" operation=\"GE\"/>");
        final Config config = new Config(opts);
        ArchiveQueryProcessor aq = new ArchiveQueryProcessor(config);
        // incrementAndGetLatestOffset() should pull all logfiles to the slicetable and return epoch_hour value of logfileRecord2.
        Long latestOffset = aq.incrementAndGetLatestOffset();
        Assertions.assertEquals(logfileRecord2.getEpochHour(), ULong.valueOf(latestOffset));
        // Assert that the logfiles are present in slicetable.
        final Result<Record9<ULong, String, String, String, String, String, Long, ULong, ULong>> hourRange = aq
                .processBetweenUnixEpochHours(earliestEpoch, latestOffset);
        Assertions.assertEquals(2, hourRange.size());
        // Assert that the resulting logfile metadata is as expected for logtime.
        Assertions.assertEquals(logfileRecord.getEpochHour(), hourRange.get(0).get(6, ULong.class));
        Assertions.assertEquals(logfileRecord2.getEpochHour(), hourRange.get(1).get(6, ULong.class));
    }

    @Test
    void mostRecentOffset() {
    }

    @Test
    void currentDatabaseMetrics() {
    }
}
