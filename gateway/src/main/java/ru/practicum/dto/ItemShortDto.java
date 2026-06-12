package ru.practicum.dto;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemShortDto {

    private Long id;
    private String name;
    private Long ownerId;
}