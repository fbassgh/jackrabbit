/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.spi2dav;

import java.util.Map;
import java.util.HashMap;

import javax.jcr.Value;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFactory;

import org.apache.jackrabbit.spi.QueryResultRow;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.QValueFactory;
import org.apache.jackrabbit.spi.IdFactory;
import org.apache.jackrabbit.spi.commons.value.ValueFormat;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.name.NameConstants;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.jcr.search.SearchResultProperty;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * <code>QueryResultRowImpl</code> implements a QueryResultRow that is
 * initialized from a multi status response.
 */
public class QueryResultRowImpl implements QueryResultRow {

    private static final Logger log = LoggerFactory.getLogger(QueryResultRowImpl.class);

    private final Map<Name, NodeId> nodeIds = new HashMap<Name, NodeId>();

    private final Map<Name, Double> scores = new HashMap<Name, Double>();

    private final Map<String, QValue> qValues = new HashMap<String, QValue>();

    private final String[] columnNames;

    public QueryResultRowImpl(MultiStatusResponse response,
                              String[] columnNames,
                              NamePathResolver resolver,
                              QValueFactory qValueFactory,
                              ValueFactory valueFactory,
                              IdFactory idFactory)
            throws RepositoryException {
        this.columnNames = columnNames;
        DavPropertySet okSet = response.getProperties(DavServletResponse.SC_OK);

        String jcrPath = resolver.getJCRName(NameConstants.JCR_PATH);
        String jcrScore = resolver.getJCRName(NameConstants.JCR_SCORE);
        DavProperty davProp = okSet.get(SearchResultProperty.SEARCH_RESULT_PROPERTY);
        SearchResultProperty resultProp = new SearchResultProperty(davProp, valueFactory);
        Value[] values = resultProp.getValues();
        String[] names = resultProp.getColumnNames();
        String[] selectorNames = resultProp.getSelectorNames();
        for (int i = 0; i < values.length; i++) {
            try {
                QValue v = (values[i] == null) ? null : ValueFormat.getQValue(values[i], resolver, qValueFactory);
                Name s = (selectorNames[i] == null) ? null : resolver.getQName(selectorNames[i]);
                if (jcrScore.equals(names[i])) {
                    Double score = 0.0;
                    if (v != null) {
                        score = v.getDouble();
                    }
                    scores.put(s, score);
                } else if (jcrPath.equals(names[i])) {
                    NodeId id = null;
                    if (v != null) {
                        id = idFactory.createNodeId((String) null, v.getPath());
                    }
                    nodeIds.put(s, id);
                }
                qValues.put(names[i], v);
            } catch (RepositoryException e) {
                // should not occur
                log.error("Malformed value: " + values[i].toString());
            }
        }
    }

    public NodeId getNodeId(Name selectorName) {
        NodeId id = nodeIds.get(selectorName);
        if (id == null) {
            if (nodeIds.size() == 1) {
                return nodeIds.values().iterator().next();
            } else {
                throw new IllegalArgumentException(selectorName + " is not a valid selectorName");
            }
        }
        return id;
    }

    public double getScore(Name selectorName) {
        Double score = scores.get(selectorName);
        if (score == null) {
            if (scores.size() == 1) {
                return scores.values().iterator().next();
            } else {
                throw new IllegalArgumentException(selectorName + " is not a valid selectorName");
            }
        }
        return score;
    }

    public QValue[] getValues() {
        QValue[] values = new QValue[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            values[i] = qValues.get(columnNames[i]);
        }
        return values;
    }
}
