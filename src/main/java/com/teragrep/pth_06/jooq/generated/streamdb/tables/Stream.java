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
package com.teragrep.pth_06.jooq.generated.streamdb.tables;

import com.teragrep.pth_06.jooq.generated.streamdb.Indexes;
import com.teragrep.pth_06.jooq.generated.streamdb.Keys;
import com.teragrep.pth_06.jooq.generated.streamdb.Streamdb;
import com.teragrep.pth_06.jooq.generated.streamdb.tables.records.StreamRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row5;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.UInteger;

/**
 * This class is generated by jOOQ.
 */
@Generated(value = {
        "http://www.jooq.org", "jOOQ version:3.12.4"
},
        comments = "This class is generated by jOOQ"
)
@SuppressWarnings({
        "all", "unchecked", "rawtypes"
})
public class Stream extends TableImpl<StreamRecord> {

    private static final long serialVersionUID = 1623008963;

    /**
     * The reference instance of <code>streamdb.stream</code>
     */
    public static final Stream STREAM = new Stream();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<StreamRecord> getRecordType() {
        return StreamRecord.class;
    }

    /**
     * The column <code>streamdb.stream.id</code>.
     */
    public final TableField<StreamRecord, UInteger> ID = createField(
            DSL.name("id"), org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false).identity(true), this, ""
    );

    /**
     * The column <code>streamdb.stream.gid</code>.
     */
    public final TableField<StreamRecord, UInteger> GID = createField(
            DSL.name("gid"), org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, ""
    );

    /**
     * The column <code>streamdb.stream.directory</code>.
     */
    public final TableField<StreamRecord, String> DIRECTORY = createField(
            DSL.name("directory"), org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, ""
    );

    /**
     * The column <code>streamdb.stream.stream</code>.
     */
    public final TableField<StreamRecord, String> STREAM_ = createField(
            DSL.name("stream"), org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, ""
    );

    /**
     * The column <code>streamdb.stream.tag</code>.
     */
    public final TableField<StreamRecord, String> TAG = createField(
            DSL.name("tag"), org.jooq.impl.SQLDataType.VARCHAR(48).nullable(false), this, ""
    );

    /**
     * Create a <code>streamdb.stream</code> table reference
     */
    public Stream() {
        this(DSL.name("stream"), null);
    }

    /**
     * Create an aliased <code>streamdb.stream</code> table reference
     */
    public Stream(String alias) {
        this(DSL.name(alias), STREAM);
    }

    /**
     * Create an aliased <code>streamdb.stream</code> table reference
     */
    public Stream(Name alias) {
        this(alias, STREAM);
    }

    private Stream(Name alias, Table<StreamRecord> aliased) {
        this(alias, aliased, null);
    }

    private Stream(Name alias, Table<StreamRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Stream(Table<O> child, ForeignKey<O, StreamRecord> key) {
        super(child, key, STREAM);
    }

    @Override
    public Schema getSchema() {
        return Streamdb.STREAMDB;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.STREAM_GID, Indexes.STREAM_PRIMARY);
    }

    @Override
    public Identity<StreamRecord, UInteger> getIdentity() {
        return Keys.IDENTITY_STREAM;
    }

    @Override
    public UniqueKey<StreamRecord> getPrimaryKey() {
        return Keys.KEY_STREAM_PRIMARY;
    }

    @Override
    public List<UniqueKey<StreamRecord>> getKeys() {
        return Arrays.<UniqueKey<StreamRecord>>asList(Keys.KEY_STREAM_PRIMARY);
    }

    @Override
    public List<ForeignKey<StreamRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<StreamRecord, ?>>asList(Keys.STREAM_IBFK_1);
    }

    public LogGroup logGroup() {
        return new LogGroup(this, Keys.STREAM_IBFK_1);
    }

    @Override
    public Stream as(String alias) {
        return new Stream(DSL.name(alias), this);
    }

    @Override
    public Stream as(Name alias) {
        return new Stream(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Stream rename(String name) {
        return new Stream(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Stream rename(Name name) {
        return new Stream(name, null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<UInteger, UInteger, String, String, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }
}
