package com.asd.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OpenAiResponse {
    private String shortText;
    private String summarizedText;
}
