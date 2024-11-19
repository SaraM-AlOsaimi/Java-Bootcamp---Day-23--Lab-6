package com.example.employeemanagementsystem.Model;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Employee {
// : ID , name, email , phoneNumber ,age, position, onLeave, hireDate and
//annualLeave

    @NotNull(message = "ID can't be Null!")
    @Size(min = 3, message = "ID must be more than 2 characters")
    private String id;

    @NotEmpty(message = "Name filed is Empty")
    @Size(min = 5 ,max = 15, message = "Name length must be more than 4 characters.")
    @Pattern(regexp = "^[A-Za-z]+$" , message = "Name must contain only characters ")
    private String name;

   @NotBlank(message = "Email can't be empty")
    @Email(message = "Enter a valid email")
    private String email;

    @NotEmpty(message = "Phone Number can't be Empty")
    //Must start with "05".
    //- Must consists of exactly 10 digits
    @Pattern(regexp = "^05\\d{8}$",message = "Phone Number must start with 05 and consist of exactly 10 digits")
    private String phoneNumber;

    @NotNull(message = "Age filed can't be null")
    @Min(value = 26 , message = "Age must be more than 25")
    @Max(value = 65,message = "Age must be less than 65")
    private Integer age;

    @NotEmpty(message = "Position can't be null")
    // Must be either "supervisor" or "coordinator" only.
    @Pattern(regexp = "supervisor|coordinator", message = "Position must be either supervisor or coordinator only")
    private String position;

    // ▪ onLeave:
    //- Must be initially set to false.
    @AssertFalse(message = "On Leave must be false")
    private boolean onLeave;

    //▪ hireDate:
    //- Cannot be null.
    //- should be a date in the present or the past.
    @PastOrPresent(message = "Hire Date must be in the present or the past")
    @NotNull(message = "Hire Date can't be null")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime hireDate;


    //▪ AnnualLeave:
    //- Cannot be null.
    //- Must be a positive number.
    @NotNull(message = "Annual Leave can't be null")
    @Positive(message = "Annual Leave Must be a positive number")
    private Integer annualLeave;

}
