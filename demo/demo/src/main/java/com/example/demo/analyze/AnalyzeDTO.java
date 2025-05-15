package com.example.demo.analyze;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AnalyzeDTO {

    @Schema(
            description = "Public image URL to be analyzed",
            example = "https://haja.net/files/attach/images/170/856/009/069e2c73692815323955ef70b3159cf7.jpg"
    )
    public String imageUrl;

    @Schema(
            description = "System prompt describing what the assistant should do",
            example = "이거 사용 x 사용 방법: POST /prompts/set 에 사용하고 싶은 프롬프트 작성. (잘 저장됐는지 GET /prompts/system/get으로 조회.)"
    )
    public String systemPrompt;
}