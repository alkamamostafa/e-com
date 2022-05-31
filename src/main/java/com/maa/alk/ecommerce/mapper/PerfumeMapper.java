package com.maa.alk.ecommerce.mapper;

import com.maa.alk.ecommerce.domain.Perfume;
import com.maa.alk.ecommerce.dto.perfume.PerfumeResponse;
import com.maa.alk.ecommerce.dto.perfume.PerfumeRequest;
import com.maa.alk.ecommerce.dto.perfume.FullPerfumeResponse;
import com.maa.alk.ecommerce.dto.perfume.PerfumeSearchRequest;
import com.maa.alk.ecommerce.dto.review.ReviewResponse;
import com.maa.alk.ecommerce.exception.InputFieldException;
import com.maa.alk.ecommerce.service.PerfumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PerfumeMapper {

    private final CommonMapper commonMapper;
    private final PerfumeService perfumeService;

    public FullPerfumeResponse getPerfumeById(Long perfumeId) {
        return commonMapper.convertToResponse(perfumeService.getPerfumeById(perfumeId), FullPerfumeResponse.class);
    }

    public List<ReviewResponse> getReviewsByPerfumeId(Long perfumeId) {
        return commonMapper.convertToResponseList(perfumeService.getReviewsByPerfumeId(perfumeId), ReviewResponse.class);
    }

    public List<PerfumeResponse> getPerfumesByIds(List<Long> perfumesId) {
        return commonMapper.convertToResponseList(perfumeService.getPerfumesByIds(perfumesId), PerfumeResponse.class);
    }

    public List<PerfumeResponse> getAllPerfumes() {
        return commonMapper.convertToResponseList(perfumeService.getAllPerfumes(), PerfumeResponse.class);
    }

    public List<PerfumeResponse> findPerfumesByFilterParams(PerfumeSearchRequest filter) {
        List<Perfume> perfumeList = perfumeService.findPerfumesByFilterParams(filter.getPerfumers(), filter.getGenders(), 
                filter.getPrices(), filter.isSortByPrice());
        return commonMapper.convertToResponseList(perfumeList, PerfumeResponse.class);
    }

    public List<PerfumeResponse> findByPerfumer(String perfumer) {
        return commonMapper.convertToResponseList(perfumeService.findByPerfumer(perfumer), PerfumeResponse.class);
    }

    public List<PerfumeResponse> findByPerfumeGender(String perfumeGender) {
        return commonMapper.convertToResponseList(perfumeService.findByPerfumeGender(perfumeGender), PerfumeResponse.class);
    }

    public FullPerfumeResponse savePerfume(PerfumeRequest perfumeRequest, MultipartFile file, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InputFieldException(bindingResult);
        }
        Perfume perfume = commonMapper.convertToEntity(perfumeRequest, Perfume.class);
        return commonMapper.convertToResponse(perfumeService.savePerfume(perfume, file), FullPerfumeResponse.class);
    }

    public String deletePerfume(Long perfumeId) {
        return perfumeService.deletePerfume(perfumeId);
    }
}
