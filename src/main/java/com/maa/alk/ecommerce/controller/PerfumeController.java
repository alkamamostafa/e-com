package com.maa.alk.ecommerce.controller;

import com.maa.alk.ecommerce.dto.GraphQLRequest;
import com.maa.alk.ecommerce.dto.perfume.PerfumeResponse;
import com.maa.alk.ecommerce.dto.perfume.FullPerfumeResponse;
import com.maa.alk.ecommerce.dto.perfume.PerfumeSearchRequest;
import com.maa.alk.ecommerce.dto.review.ReviewResponse;
import com.maa.alk.ecommerce.mapper.PerfumeMapper;
import com.maa.alk.ecommerce.service.graphql.GraphQLProvider;
import graphql.ExecutionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/perfumes")
public class PerfumeController {

    private final PerfumeMapper perfumeMapper;
    private final GraphQLProvider graphQLProvider;

    @GetMapping
    public ResponseEntity<List<PerfumeResponse>> getAllPerfumes() {
        return ResponseEntity.ok(perfumeMapper.getAllPerfumes());
    }

    @GetMapping("/{perfumeId}")
    public ResponseEntity<FullPerfumeResponse> getPerfumeById(@PathVariable Long perfumeId) {
        return ResponseEntity.ok(perfumeMapper.getPerfumeById(perfumeId));
    }

    @GetMapping("/reviews/{perfumeId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByPerfumeId(@PathVariable Long perfumeId) {
        return ResponseEntity.ok(perfumeMapper.getReviewsByPerfumeId(perfumeId));
    }

    @PostMapping("/ids")
    public ResponseEntity<List<PerfumeResponse>> getPerfumesByIds(@RequestBody List<Long> perfumesIds) {
        return ResponseEntity.ok(perfumeMapper.getPerfumesByIds(perfumesIds));
    }

    @PostMapping("/search")
    public ResponseEntity<List<PerfumeResponse>> findPerfumesByFilterParams(@RequestBody PerfumeSearchRequest filter) {
        return ResponseEntity.ok(perfumeMapper.findPerfumesByFilterParams(filter));
    }

    @PostMapping("/search/gender")
    public ResponseEntity<List<PerfumeResponse>> findByPerfumeGender(@RequestBody PerfumeSearchRequest filter) {
        return ResponseEntity.ok(perfumeMapper.findByPerfumeGender(filter.getPerfumeGender()));
    }

    @PostMapping("/search/perfumer")
    public ResponseEntity<List<PerfumeResponse>> findByPerfumer(@RequestBody PerfumeSearchRequest filter) {
        return ResponseEntity.ok(perfumeMapper.findByPerfumer(filter.getPerfumer()));
    }

    @PostMapping("/graphql/ids")
    public ResponseEntity<ExecutionResult> getPerfumesByIdsQuery(@RequestBody GraphQLRequest request) {
        return ResponseEntity.ok(graphQLProvider.getGraphQL().execute(request.getQuery()));
    }

    @PostMapping("/graphql/perfumes")
    public ResponseEntity<ExecutionResult> getAllPerfumesByQuery(@RequestBody GraphQLRequest request) {
        return ResponseEntity.ok(graphQLProvider.getGraphQL().execute(request.getQuery()));
    }

    @PostMapping("/graphql/perfume")
    public ResponseEntity<ExecutionResult> getPerfumeByQuery(@RequestBody GraphQLRequest request) {
        return ResponseEntity.ok(graphQLProvider.getGraphQL().execute(request.getQuery()));
    }
}
