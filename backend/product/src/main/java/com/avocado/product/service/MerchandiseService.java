package com.avocado.product.service;

import com.avocado.product.dto.query.ClickMerchandiseDTO;
import com.avocado.product.dto.query.DetailMerchandiseDTO;
import com.avocado.product.dto.query.SimpleMerchandiseDTO;
import com.avocado.product.dto.response.ClickMerchandiseResp;
import com.avocado.product.dto.response.DetailMerchandiseResp;
import com.avocado.product.dto.response.PageResp;
import com.avocado.product.dto.response.SimpleMerchandiseResp;
import com.avocado.product.repository.MerchandiseRepository;
import com.avocado.product.repository.PurchaseRepository;
import com.avocado.product.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MerchandiseService {
    private final MerchandiseRepository merchandiseRepository;
    private final PurchaseRepository purchaseRepository;  // 구매 여부 조회하기 위한 repo
    private final ReviewRepository reviewRepository;  // 리뷰 여부 조회하기 위한 repo

    // 대표 퍼스널컬러, MBTI, 나이대 등 개인화 정보 조회를 위한 service
    private final ScoreService scoreService;

    /**
     * 상품 목록을 카테고리와 브랜드 이름으로 조회한 데이터를 제공하는 서비스
     * @param categoryId : 카테고리 ID
     * @param brandName : 브랜드 이름
     * @param lastMerchandiseId : 마지막으로 조회한 상품 ID
     * @param size : 한 번에 조회할 데이터의 개수
     * @return : 상품 목록
     */
    @Transactional(readOnly = true)
    public PageResp showMerchandiseList_NoOffset(Short categoryId, String brandName,
                                               Long lastMerchandiseId, Integer size) {
        // DB 조회
        Page<SimpleMerchandiseDTO> result = merchandiseRepository
                .findByCategoryAndBrand_NoOffset(categoryId, brandName, lastMerchandiseId, PageRequest.ofSize(size));

        // DTO -> Response 변환
        List<SimpleMerchandiseResp> respContent;

        try {
            respContent = scoreService.insertPersonalInfoIntoList(result.getContent(), SimpleMerchandiseResp.class);
        } catch (Exception e) {
            throw new RuntimeException();
        }

        // 마지막으로 조회한 ID
        Long newLastMerchandiseId = respContent.isEmpty()
                ? null
                : respContent.get(respContent.size() - 1).getMerchandise_id();

        return PageResp.of(respContent, result.isLast(), newLastMerchandiseId, null);
    }

    /**
     * 특정 상품의 상세정보 조회하기
     * @param merchandiseId : 상품 ID
     * @return : 해당 상품의 상세정보, 요청한 사용자가 해당 상품을 구매했는지, 리뷰를 남겼는지 true / false
     */
    @Transactional(readOnly = true)
    public DetailMerchandiseResp showDetailMerchandise(UUID consumerId, Long merchandiseId) {
        // DB 조회
        DetailMerchandiseDTO queryContent = merchandiseRepository.findDetailMerchandise(merchandiseId);

        // 대표 퍼스널컬러, MBTI, 나이대 정보 부착
        DetailMerchandiseResp respContent = scoreService.insertPersonalInfo(queryContent);

        // 추가 이미지 조회 및 부착
        List<String> additionalImages = merchandiseRepository.findAdditionalImages(merchandiseId);
        respContent.updateImages(additionalImages);

        // 요청한 사용자가 있다면 구매 여부, 리뷰 여부 구하기
        if (consumerId != null) {
            // 요청한 사용자가 이 상품을 구매했는지 확인
            Boolean isPurchased = purchaseRepository.checkPurchased(consumerId, merchandiseId);
            respContent.updateIsPurchased(isPurchased);

            // 요청한 사용자가 이 상품을 구매했다면, 리뷰도 남겼는지 확인
            if (isPurchased) {
                Boolean isReviewed = reviewRepository.checkReviewed(consumerId, merchandiseId);
                respContent.updateIsReviewed(isReviewed);
            }
        }

        return respContent;
    }

    /**
     * 최근 본 상품 조회하기
     * @param consumerId : 사용자 ID
     * @return : 해당 사용자가 최근 조회한 상품 목록
     */
    @Transactional(readOnly = true)
    public List<ClickMerchandiseResp> showRecentMerchandises(UUID consumerId) {
        // DB 조회
        List<ClickMerchandiseDTO> recentMerchandises = merchandiseRepository.findRecentMerchandises(consumerId);

        // 대표 퍼스널컬러, MBTI, 나이대 추가 + DTO -> Response 변환
        try {
            return scoreService.insertPersonalInfoIntoList(recentMerchandises, ClickMerchandiseResp.class);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

//    @Transactional(readOnly = true)
//    public PageResp showPurchaseMerchandises(UUID consumerId, LocalDateTime lastPurchaseDate, Integer size) {
//        // DB 조회
//        Page<SimpleMerchandiseDTO> result = merchandiseRepository
//                .findPurchaseHistories(consumerId, lastPurchaseDate, PageRequest.ofSize(size));
//
//        // DTO -> Response 변환
//        List<SimpleMerchandiseResp> respContent = scoreService.insertPersonalInfoIntoList(
//                result.getContent()
//        );
//
//        // 마지막 상품의 구매일자
//        Long newLastMerchandiseId = respContent.isEmpty()
//                ? null
//                : respContent.get(respContent.size() - 1).get;
//
//        return PageResp.of(respContent, result.isLast(), null, newLastMerchandiseId);
//    }

    // Offset 사용 버전.
//    @Transactional(readOnly = true)
//    public PageResp showMerchandiseList_Offset(Short categoryId, String brandName,
//                                               Integer page, Integer size) {
//        Page<SimpleMerchandiseDTO> result = merchandiseRepository
//                .findByCategoryAndBrand_Offset(categoryId, brandName, PageRequest.of(page, size));
//
//        // DTO -> Response 변환
//        List<SimpleMerchandiseResp> respContent = scoreService.appendPersonalInfo(
//                result.getContent()
//        );
//
//        // 마지막으로 조회한 ID
//        Long newLastMerchandiseId = respContent.isEmpty()
//                ? null
//                : respContent.get(respContent.size() - 1).getMerchandise_id();
//
//        return PageResp.of(respContent, result.isLast(), newLastMerchandiseId);
//    }
}
