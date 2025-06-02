package com.example.ecommerce.dto;


import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeightDto {
    private List<String> weights;
}
