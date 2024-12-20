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
package com.teragrep.pth_06.planner.walker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import java.time.Instant;

/**
 * <h1>Earliest Walker</h1> Walker for earliest epoch.
 *
 * @since 30/08/2023
 * @author Mikko Kortelainen
 * @author Ville Manninen
 */
public class EarliestWalker extends XmlWalker<Long> {

    private final Logger LOGGER = LoggerFactory.getLogger(EarliestWalker.class);

    // TODO a hack to get global earliest value, default -24h from now
    public final long globalEarliestEpoch = Instant.now().getEpochSecond() - 24 * 3600;

    public EarliestWalker() {
        super();
    }

    @Override
    Long emitElem(Element current) {
        String tag = current.getTagName();
        String value = current.getAttribute("value");

        long earliestEpoch = globalEarliestEpoch;

        if ("earliest".equalsIgnoreCase(tag) || "index_earliest".equalsIgnoreCase(tag)) {

            earliestEpoch = Long.parseLong(value);

            if (globalEarliestEpoch < earliestEpoch) {
                earliestEpoch = globalEarliestEpoch;
            }
        }

        return earliestEpoch;
    }

    @Override
    public Long emitLogicalOperation(String op, Object l, Object r) throws IllegalStateException {
        Long rv;
        Long left = (Long) l;
        Long right = (Long) r;

        if (op == null) {
            throw new IllegalStateException("Parse error, unbalanced elements. " + left);
        }

        if ("AND".equalsIgnoreCase(op) || "OR".equalsIgnoreCase(op)) {
            rv = left < right ? left : right;
        }

        else {
            throw new IllegalStateException(
                    "Parse error, unsorted logical operation. op: " + op + " expression: " + left
            );
        }

        return rv;
    }

    @Override
    public Long emitUnaryOperation(String op, Element current) throws IllegalStateException {

        Long rv = emitElem(current);
        LOGGER.info("EarliestWalker.emitUnaryOperation incoming op: " + op + " element: " + current);

        if (op == null) {
            throw new IllegalStateException("Parse error op was null");
        }
        if (rv != null) {
            if ("NOT".equalsIgnoreCase(op)) {
                // Unary operations ignored
                rv = null;
            }
            else {
                throw new IllegalStateException(
                        "Parse error, unsupported logical operation: " + op + " expression: " + rv
                );
            }
        }

        return rv;
    }
}
