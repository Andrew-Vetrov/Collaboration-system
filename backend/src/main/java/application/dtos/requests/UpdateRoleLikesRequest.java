package application.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UpdateRoleLikesRequest {

    @JsonProperty("likes_amount")
    private Integer likesAmount;
}