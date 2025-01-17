package cart.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

public class ModifyProductRequest {

    @NotBlank
    private final String name;

    @PositiveOrZero
    private final long price;

    @NotBlank
    private final String imageUrl;

    public ModifyProductRequest(final String name,
                                final long price,
                                final String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public long getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
