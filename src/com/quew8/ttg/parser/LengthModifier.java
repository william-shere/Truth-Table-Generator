/* 
 * Copyright (C) 2014 William Shere
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.quew8.ttg.parser;

/**
 *
 * @author Quew8
 */
 enum LengthModifier {
    ONE(true, false), ONE_OR_MORE(true, true), ZERO_OR_MORE(false, true), ONE_OR_ZERO(false, false);

    private LengthModifier(boolean failOnNone, boolean acceptMore) {
        this.failOnNone = failOnNone;
        this.acceptMore = acceptMore;
    }
    private final boolean failOnNone;
    private final boolean acceptMore;

    public boolean isFailOnNone() {
        return failOnNone;
    }

    public boolean isAcceptMore() {
        return acceptMore;
    }
    
}
