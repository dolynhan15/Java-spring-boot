package com.example.swaggerbasic;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Contact {
    @ApiModelProperty(notes = "ID book", name = "id", required = true, value = "1")
    private String id;

    @ApiModelProperty(notes = "Name of book", name = "name", required = true, value = "John")
    private String name;

    @ApiModelProperty(notes = "Phone Number", name = "phoneNumber", required = true, value = "123-123-123")
    private String phone;


}
