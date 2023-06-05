package com.rizkyjayusman.chat.document.query;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SearchQuery {

    protected Set<Criteria> criterion;

    public Query getQuery() {
        if (criterion.isEmpty()) {
            return new Query();
        }

        Criteria[] criteriaArray = criterion.toArray(new Criteria[0]);
        return new Query().addCriteria(new Criteria().andOperator(criteriaArray));
    }

    public Aggregation getAggregation(Sort sort) {
        return getAggregation(sort, null, null);
    }

    public Aggregation getAggregation(Sort sort, Long skip, Long limit) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        if (criterion.isEmpty()) {
            Criteria[] criteriaArray = criterion.toArray(new Criteria[0]);
            MatchOperation matchOperation = Aggregation.match(new Criteria().andOperator(criteriaArray));
            aggregationOperations.add(matchOperation);
        }

        if (sort.isSorted()) {
            aggregationOperations.add(Aggregation.sort(sort));
        }

        if (skip != null) {
            SkipOperation skipOperation = Aggregation.skip(skip);
            aggregationOperations.add(skipOperation);
        }

        if (limit != null) {
            LimitOperation limitOperation = Aggregation.limit(limit);
            aggregationOperations.add(limitOperation);
        }

        return Aggregation.newAggregation(aggregationOperations);
    }

}

