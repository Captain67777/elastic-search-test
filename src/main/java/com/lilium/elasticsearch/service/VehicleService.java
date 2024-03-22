package com.lilium.elasticsearch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lilium.elasticsearch.document.Vehicle;
import com.lilium.elasticsearch.helper.Indices;
import com.lilium.elasticsearch.search.SearchRequestDTO;
import com.lilium.elasticsearch.search.util.SearchUtil;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class VehicleService {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(VehicleService.class);

    private final RestHighLevelClient client;

    @Autowired
    public VehicleService(RestHighLevelClient client) {
        this.client = client;
    }

    /**
     * Used to search for vehicles based on data provided in the {@link SearchRequestDTO} DTO. For more info take a look
     * at DTO javadoc.
     *
     * @param dto DTO containing info about what to search for.
     * @return Returns a list of found vehicles.
     */
    public List<Vehicle> search(final SearchRequestDTO dto) {
        // match query
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.VEHICLE_INDEX,
                dto
        );

        return searchInternal(request);
    }

    /**
     * Used to get all vehicles that have been created since forwarded date.
     *
     * @param datum Date that is forwarded to the search.
     * @return Returns all vehicles created since forwarded date.
     */
    public List<Vehicle> getAllVehiclesCreatedSince(final Date date) {
        // range query
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.VEHICLE_INDEX,
                "created",
                date
        );

        return searchInternal(request);
    }

    public List<Vehicle> getAllVehiclesCreatedBetween(final Date fromDate, final Date toDate) {
        // range query
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.VEHICLE_INDEX,
                "created",
                fromDate, toDate
        );

        return searchInternal(request);
    }

    public List<Vehicle> getAllVehiclesByDriver(final String firstName, final String lastName) {
        // example of nested query (on nested objects)
        final SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("vehicle");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String nestedPath = "drivers";
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery(); // similar to SQL WHERE EXISTS (select 'y' from subquery)
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("drivers.first_name", firstName); // similar to SQL WHERE clause
        NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(nestedPath, boolQueryBuilder.must(matchQueryBuilder), ScoreMode.None);
        searchSourceBuilder.query(nestedQueryBuilder); // prepare query
        searchRequest.source(searchSourceBuilder); // execute query

        try {
            final SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            final SearchHit[] searchHits = response.getHits().getHits();
            final List<Vehicle> vehicles = new ArrayList<>(searchHits.length);
            for (SearchHit hit : searchHits) {
                vehicles.add(
                        MAPPER.readValue(hit.getSourceAsString(), Vehicle.class)
                );
            }

            return vehicles;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Collections.emptyList();
        }

    }


    public List<Vehicle> searchCreatedSince(final SearchRequestDTO dto, final Date date) {
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.VEHICLE_INDEX,
                dto,
                date
        );

        return searchInternal(request);
    }


    private List<Vehicle> searchInternal(final SearchRequest request) {
        if (request == null) {
            LOG.error("Failed to build search request");
            return Collections.emptyList();
        }

        try {
            final SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            final SearchHit[] searchHits = response.getHits().getHits();
            final List<Vehicle> vehicles = new ArrayList<>(searchHits.length);
            for (SearchHit hit : searchHits) {
                vehicles.add(
                        MAPPER.readValue(hit.getSourceAsString(), Vehicle.class)
                );
            }

            return vehicles;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public Boolean index(final Vehicle vehicle) {
        try {
            final String vehicleAsString = MAPPER.writeValueAsString(vehicle); // convert to json string

            final IndexRequest request = new IndexRequest(Indices.VEHICLE_INDEX);
            request.id(vehicle.getId());
            request.source(vehicleAsString, XContentType.JSON);

            final IndexResponse response = client.index(request, RequestOptions.DEFAULT);

            return response != null && response.status().equals(RestStatus.OK);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    public Vehicle getById(final String vehicleId) {
        try {
            final GetResponse documentFields = client.get(
                    new GetRequest(Indices.VEHICLE_INDEX, vehicleId),
                    RequestOptions.DEFAULT
            );
            if (documentFields == null || documentFields.isSourceEmpty()) {
                return null;
            }

            return MAPPER.readValue(documentFields.getSourceAsString(), Vehicle.class); // convert json to Vehicle object
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
}