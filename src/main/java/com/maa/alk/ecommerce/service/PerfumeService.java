package com.maa.alk.ecommerce.service;

import com.maa.alk.ecommerce.domain.Perfume;
import com.maa.alk.ecommerce.domain.Review;
import graphql.schema.DataFetcher;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PerfumeService {

    Perfume getPerfumeById(Long perfumeId);

    List<Perfume> getAllPerfumes();

    List<Perfume> getPerfumesByIds(List<Long> perfumesId);

    List<Perfume> findPerfumesByFilterParams(List<String> perfumers, List<String> genders, List<Integer> prices, boolean sortByPrice);

    List<Perfume> findByPerfumer(String perfumer);

    List<Perfume> findByPerfumeGender(String perfumeGender);

    Perfume savePerfume(Perfume perfume, MultipartFile file);

    String deletePerfume(Long perfumeId);

    List<Review> getReviewsByPerfumeId(Long perfumeId);

    DataFetcher<Perfume> getPerfumeByQuery();

    DataFetcher<List<Perfume>> getAllPerfumesByQuery();

    DataFetcher<List<Perfume>> getAllPerfumesByIdsQuery();
}
