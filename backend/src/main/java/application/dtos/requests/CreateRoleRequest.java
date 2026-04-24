package application.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CreateRoleRequest {

    private String name;

    private String color;

    @JsonProperty("likes_amount")
    private Integer likesAmount;
}