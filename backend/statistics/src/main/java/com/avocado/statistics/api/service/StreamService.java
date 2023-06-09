package com.avocado.statistics.api.service;

import com.avocado.ActionType;
import com.avocado.Result;
import com.avocado.statistics.common.error.BaseException;
import com.avocado.statistics.common.error.ResponseCode;
import com.avocado.statistics.common.utils.CategoryTypeUtil;
import com.avocado.statistics.common.utils.DateUtil;
import com.avocado.statistics.common.utils.UUIDUtil;
import com.avocado.statistics.db.mysql.repository.mybatis.ConsumerRepository;
import com.avocado.statistics.db.mysql.entity.mybatis.Consumer;
import com.avocado.statistics.db.redis.repository.AdvertiseCountRepository;
import com.avocado.statistics.db.redis.repository.CategoryType;
import com.avocado.statistics.db.redis.repository.MerchandiseIdSetRepository;
import com.avocado.statistics.db.redis.repository.ScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StreamService {

    private final ScoreRepository scoreRepository;
    private final MerchandiseIdSetRepository merchandiseIdSetRepository;
    private final ConsumerRepository consumerRepository;
    private final AdvertiseCountRepository advertiseCountRepository;
    private final DateUtil dateUtil;
    private final CategoryTypeUtil categoryTypeUtil;
    private final UUIDUtil uuidUtil;

    public void consumeResult(Result result, Long merchandiseId) {

        ActionType resType = result.getAction();

        // 점수가 저장된 merchandise 로 처리하기
        merchandiseIdSetRepository.setUse(merchandiseId);

        // 광고 처리하기
        if (resType.equals(ActionType.AD_CLICK) || resType.equals(ActionType.AD_VIEW) || resType.equals(ActionType.AD_PAYMENT)) {
            String date = dateUtil.getNowDate();
            advertiseCountRepository.save(resType, date, merchandiseId);
        }
        // 점수 저장하기
        else {
            saveScore(result, merchandiseId);
        }
    }
    
    /**
     * 점수 저장 과정
     * @param result
     */
    private void saveScore(Result result, Long merchandiseId) {
        String userId = result.getUserId().replace("-", "");
        UUID consumerId = uuidUtil.joinByHyphen(userId);
        ActionType resType = result.getAction();

        Optional<Consumer> consumerO = consumerRepository.getById(consumerId);
        // 없는 소비자면 에러 내기
        if (consumerO.isEmpty()) {
            throw new BaseException(ResponseCode.INVALID_VALUE);
        }

        Consumer consumer = consumerO.get();
        
        // age-gender score 저장하기
        Integer age = consumer.getAge();
        String gender = consumer.getGender();
        if (age != null && gender != null) {
            int index = categoryTypeUtil.getIndexOfGenderAgeGroup(age, gender);
            scoreRepository.save(CategoryType.AGE_GENDER, resType, merchandiseId, index);
        }
        
        // mbti score 저장하기
        Integer mbtiId = consumer.getMbtiId();
        if (mbtiId != null) {
            scoreRepository.save(CategoryType.MBTI, resType, merchandiseId, mbtiId);
        }
        
        // personal color score 저장하기
        Integer personalColorId = consumer.getPersonalColorId();
        if (personalColorId != null) {
            scoreRepository.save(CategoryType.PERSONAL_COLOR, resType, merchandiseId, personalColorId);
        }
    }
}
