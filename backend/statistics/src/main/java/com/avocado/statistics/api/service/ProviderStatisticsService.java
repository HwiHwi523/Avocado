package com.avocado.statistics.api.service;

import com.avocado.statistics.api.response.*;
import com.avocado.statistics.common.error.BaseException;
import com.avocado.statistics.common.error.ResponseCode;
import com.avocado.statistics.common.utils.JwtUtils;
import com.avocado.statistics.common.utils.UUIDUtil;
import com.avocado.statistics.db.mysql.repository.dto.AgeGroupDistributionDTO;
import com.avocado.statistics.db.mysql.repository.dto.ChartDistributionDTO;
import com.avocado.statistics.db.mysql.repository.dto.GenderDistributionDTO;
import com.avocado.statistics.db.mysql.repository.dto.SellCountTotalRevenueDTO;
import com.avocado.statistics.db.mysql.repository.jpa.ProviderStatisticsRepository;
import com.avocado.statistics.db.redis.repository.ProviderRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProviderStatisticsService {
    private final JwtUtils jwtUtils;
    private final ProviderStatisticsRepository jpaProviderStatisticsRepository;
    private final ProviderRepository providerRepository;  // Redis

    public ProviderStatisticsResp getStatisticsInfo(Claims claims) {
        String type = jwtUtils.getType(claims);
        // 권한이 provider 가 아니면 FORBIDDEN 에러 내기
        if (!type.equals("provider")) {
            throw new BaseException(ResponseCode.FORBIDDEN);
        }

        // 조회수, 판매수, 총 판매액, 상품수 조회
        UUID providerId = jwtUtils.getId(claims);

        // Redis 데이터 확인 & Hit일 경우 반환, Miss일 경우 DB에서 쿼리 조회
        ProviderStatisticsResp providerStatisticsResp = providerRepository.load(UUIDUtil.removeHyphen(providerId));
        if (providerStatisticsResp != null)
            return providerStatisticsResp;

        Long clickCount = jpaProviderStatisticsRepository.getClickCount(providerId);  // 조회수
        SellCountTotalRevenueDTO sellCountTotalRevenueDTO = jpaProviderStatisticsRepository
                .getSellCountTotalRevenue(providerId);  // 판매수, 총 판매액
        Long merchandiseCount = jpaProviderStatisticsRepository.getMerchandiseCount(providerId);

        // 성별 분포 조회
        List<GenderDistributionDTO> genderDist = jpaProviderStatisticsRepository
                .getGenderDistribution(providerId);
        // Response 생성
        List<GenderDistributionResp> genders = new ArrayList<>();
        for (GenderDistributionDTO data : genderDist) {
            if (data.getGender() == null) continue;  // 성별 미설정 구매자 집계 제외
            genders.add(GenderDistributionResp.from(data));
        }

        // MBTI 분포 조회
        List<ChartDistributionDTO> mbtiDist = jpaProviderStatisticsRepository
                .getMBTIDistribution(providerId);
        // Response 생성
        List<MBTIDistributionResp> mbtis = new ArrayList<>();
        for (ChartDistributionDTO data : mbtiDist) {
            if (data.getId() == null) continue;  // MBTI 미설정 구매자 집계 제외
            mbtis.add(MBTIDistributionResp.from(data));
        }

        // 퍼스널컬러 분포 조회
        List<ChartDistributionDTO> personalColorDist = jpaProviderStatisticsRepository
                .getPersonalColorDistribution(providerId);
        // Response 생성
        List<PersonalColorDistributionResp> personalColors = new ArrayList<>();
        for (ChartDistributionDTO data : personalColorDist) {
            if (data.getId() == null) continue;  // 퍼스널컬러 미설정 구매자 집계 제외
            personalColors.add(PersonalColorDistributionResp.from(data));
        }

        // 연령대 분포 조회
        List<AgeGroupDistributionDTO> ageGroupDist = jpaProviderStatisticsRepository
                .getAgeGroupDistribution(providerId);
        // Response 생성
        List<AgeGroupDistributionResp> ageGroups = new ArrayList<>();
        for (AgeGroupDistributionDTO data : ageGroupDist) {
            if (data.getAgeGroup() == null) continue;  // 연령대 미설정 구매자 집계 제외
            ageGroups.add(AgeGroupDistributionResp.from(data));
        }

        // Response DTO 생성
        providerStatisticsResp = new ProviderStatisticsResp();
        providerStatisticsResp.updateNumericStatistics(
                clickCount,
                sellCountTotalRevenueDTO.getSellCount(),
                sellCountTotalRevenueDTO.getTotalRevenue(),
                merchandiseCount,
                genders,
                mbtis,
                personalColors,
                ageGroups
        );

        // Redis 저장
        providerRepository.save(UUIDUtil.removeHyphen(providerId), providerStatisticsResp);

        return providerStatisticsResp;
    }
}
