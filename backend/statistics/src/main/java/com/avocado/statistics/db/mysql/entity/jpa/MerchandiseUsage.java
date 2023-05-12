package com.avocado.statistics.db.mysql.entity.jpa;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MerchandiseUsage {
    @Id
    private Short id;

    // 제품 용도 (한국어)
    @Column(length = 30)
    private String nameKor;

    // 제품 용도 (영어)
    @Column(length = 30)
    private String nameEng;

//    @Builder
//    public MerchandiseUsage(Short id, String nameKor, String nameEng) {
//        this.id = id;
//        this.nameKor = nameKor;
//        this.nameEng = nameEng;
//    }
}
