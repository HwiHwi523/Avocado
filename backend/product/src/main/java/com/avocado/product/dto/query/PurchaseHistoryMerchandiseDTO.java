package com.avocado.product.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class PurchaseHistoryMerchandiseDTO extends DefaultMerchandiseDTO {
    private final UUID purchaseId;
    private final LocalDateTime purchaseDate;
    private final String size;
    private final Integer quantity;
    private final Float score;

    @QueryProjection
    public PurchaseHistoryMerchandiseDTO(UUID purchaseId, String brandName, Long merchandiseId, String merchandiseCategory, String imageUrl,
                                         String merchandiseName, Integer price, Integer discountedPrice, LocalDateTime purchaseDate, String size,
                                         Integer quantity, Float score, Byte mbtiId, Byte personalColorId, Short ageGroup) {
        super(brandName, merchandiseId, merchandiseCategory, imageUrl, merchandiseName, price, discountedPrice,
                mbtiId, personalColorId, ageGroup);
        this.purchaseId = purchaseId;
        this.purchaseDate = purchaseDate;
        this.size = size;
        this.quantity = quantity;
        this.score = score != null ? score : 0;
    }
}
