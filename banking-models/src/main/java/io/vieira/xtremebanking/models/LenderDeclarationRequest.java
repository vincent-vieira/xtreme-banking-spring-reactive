package io.vieira.xtremebanking.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class LenderDeclarationRequest {

    @NotNull
    private String buyer;

    @JsonCreator
    public LenderDeclarationRequest(@JsonProperty("buyer") String buyer) {
        this.buyer = buyer;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }
}
