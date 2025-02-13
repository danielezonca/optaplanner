/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.common.experimental;

import java.util.Iterator;

public class IntervalTreeIterator<IntervalValue_, PointValue_ extends Comparable<PointValue_>>
        implements Iterator<IntervalValue_> {
    final Iterator<IntervalSplitPoint<IntervalValue_, PointValue_>> splitPointSetIterator;
    Iterator<IntervalValue_> splitPointValueIterator;

    public IntervalTreeIterator(Iterable<IntervalSplitPoint<IntervalValue_, PointValue_>> splitPointSet) {
        this.splitPointSetIterator = splitPointSet.iterator();
        if (splitPointSetIterator.hasNext()) {
            splitPointValueIterator = splitPointSetIterator.next().getValuesStartingFromSplitPointIterator();
        }
    }

    @Override
    public boolean hasNext() {
        return splitPointValueIterator != null && splitPointValueIterator.hasNext();
    }

    @Override
    public IntervalValue_ next() {
        IntervalValue_ next = splitPointValueIterator.next();

        while (!splitPointValueIterator.hasNext() && splitPointSetIterator.hasNext()) {
            splitPointValueIterator = splitPointSetIterator.next().getValuesStartingFromSplitPointIterator();
        }

        if (!splitPointValueIterator.hasNext()) {
            splitPointValueIterator = null;
        }

        return next;
    }
}
