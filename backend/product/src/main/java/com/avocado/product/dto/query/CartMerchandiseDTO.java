package com.avocado.product.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class CartMerchandiseDTO extends DefaultMerchandiseDTO {
    private final Long cartId;
    private final Float score;

    @QueryProjection
    public CartMerchandiseDTO(Long cartId, String brandName, Long merchandiseId, String merchandiseCategory, String imageUrl,
                              String merchandiseName, Integer price, Integer discountedPrice, Float score) {
        super(brandName, merchandiseId, merchandiseCategory, imageUrl, merchandiseName, price, discountedPrice);
        this.cartId = cartId;
        this.score = score != null ? score : 0;
    }
}
