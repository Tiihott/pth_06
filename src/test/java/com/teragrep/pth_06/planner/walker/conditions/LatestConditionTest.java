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
package com.teragrep.pth_06.planner.walker.conditions;

import org.jooq.Condition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LatestConditionTest {

    @Test
    void conditionTest() {
        String e = "(\n" + "  \"journaldb\".\"logfile\".\"logdate\" <= date '1970-01-01'\n"
                + "  and (UNIX_TIMESTAMP(STR_TO_DATE(SUBSTRING(REGEXP_SUBSTR(path,'[0-9]+(\\.log)?\\.gz(\\.[0-9]*)?$'), 1, 10), '%Y%m%d%H')) <= 1000)\n"
                + ")";
        Condition elementCondition = new LatestCondition("1000").condition();
        Assertions.assertEquals(e, elementCondition.toString());
    }

    @Test
    void conditionUpdatedTest() {
        String e = "(\n" + "  \"journaldb\".\"logfile\".\"logdate\" <= date '2000-01-01'\n"
                + "  and (UNIX_TIMESTAMP(STR_TO_DATE(SUBSTRING(REGEXP_SUBSTR(path,'[0-9]+(\\.log)?\\.gz(\\.[0-9]*)?$'), 1, 10), '%Y%m%d%H')) <= 946677600)\n"
                + ")";
        Condition elementCondition = new LatestCondition("946677600").condition();
        Assertions.assertEquals(e, elementCondition.toString());
    }

    @Test
    void equalsTest() {
        IndexCondition eq1 = new IndexCondition("946677600", "EQUALS", false);
        eq1.condition();
        IndexCondition eq2 = new IndexCondition("946677600", "EQUALS", false);
        IndexCondition eq3 = new IndexCondition("946677600", "EQUALS", true);
        eq3.condition();
        IndexCondition eq4 = new IndexCondition("946677600", "EQUALS", true);
        Assertions.assertEquals(eq1, eq2);
        Assertions.assertEquals(eq2, eq1);
        Assertions.assertEquals(eq3, eq4);
    }

    @Test
    void notEqualsTest() {
        IndexCondition eq1 = new IndexCondition("946677600", "EQUALS", false);
        IndexCondition notEq = new IndexCondition("1000", "EQUALS", false);
        IndexCondition notEq2 = new IndexCondition("946677600", "EQUALS", true);
        Assertions.assertNotEquals(eq1, notEq);
        Assertions.assertNotEquals(notEq, eq1);
        Assertions.assertNotEquals(eq1, null);
        Assertions.assertNotEquals(eq1, notEq2);
        Assertions.assertNotEquals(notEq, notEq2);
    }
}