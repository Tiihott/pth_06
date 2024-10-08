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
/*
 * This file is generated by jOOQ.
 */
package com.teragrep.pth_06.jooq.generated.bloomdb.tables.records;


import com.teragrep.pth_06.jooq.generated.bloomdb.tables.FilterExpected_100000Fpp_001;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class FilterExpected_100000Fpp_001Record extends UpdatableRecordImpl<FilterExpected_100000Fpp_001Record> implements Record3<Integer, ULong, byte[]> {

    private static final long serialVersionUID = -373382442;

    /**
     * Setter for <code>bloomdb.filter_expected_100000_fpp_001.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>bloomdb.filter_expected_100000_fpp_001.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>bloomdb.filter_expected_100000_fpp_001.partition_id</code>.
     */
    public void setPartitionId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>bloomdb.filter_expected_100000_fpp_001.partition_id</code>.
     */
    public ULong getPartitionId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>bloomdb.filter_expected_100000_fpp_001.filter</code>.
     */
    public void setFilter(byte... value) {
        set(2, value);
    }

    /**
     * Getter for <code>bloomdb.filter_expected_100000_fpp_001.filter</code>.
     */
    public byte[] getFilter() {
        return (byte[]) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<Integer, ULong, byte[]> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<Integer, ULong, byte[]> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return FilterExpected_100000Fpp_001.FILTER_EXPECTED_100000_FPP_001.ID;
    }

    @Override
    public Field<ULong> field2() {
        return FilterExpected_100000Fpp_001.FILTER_EXPECTED_100000_FPP_001.PARTITION_ID;
    }

    @Override
    public Field<byte[]> field3() {
        return FilterExpected_100000Fpp_001.FILTER_EXPECTED_100000_FPP_001.FILTER;
    }

    @Override
    public Integer component1() {
        return getId();
    }

    @Override
    public ULong component2() {
        return getPartitionId();
    }

    @Override
    public byte[] component3() {
        return getFilter();
    }

    @Override
    public Integer value1() {
        return getId();
    }

    @Override
    public ULong value2() {
        return getPartitionId();
    }

    @Override
    public byte[] value3() {
        return getFilter();
    }

    @Override
    public FilterExpected_100000Fpp_001Record value1(Integer value) {
        setId(value);
        return this;
    }

    @Override
    public FilterExpected_100000Fpp_001Record value2(ULong value) {
        setPartitionId(value);
        return this;
    }

    @Override
    public FilterExpected_100000Fpp_001Record value3(byte... value) {
        setFilter(value);
        return this;
    }

    @Override
    public FilterExpected_100000Fpp_001Record values(Integer value1, ULong value2, byte[] value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached FilterExpected_100000Fpp_001Record
     */
    public FilterExpected_100000Fpp_001Record() {
        super(FilterExpected_100000Fpp_001.FILTER_EXPECTED_100000_FPP_001);
    }

    /**
     * Create a detached, initialised FilterExpected_100000Fpp_001Record
     */
    public FilterExpected_100000Fpp_001Record(Integer id, ULong partitionId, byte[] filter) {
        super(FilterExpected_100000Fpp_001.FILTER_EXPECTED_100000_FPP_001);

        set(0, id);
        set(1, partitionId);
        set(2, filter);
    }
}
