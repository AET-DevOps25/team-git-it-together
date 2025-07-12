package com.gitittogether.skillForge.server.course.dto.response.utils;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmbedResult {
   private boolean success;
   private String url;
   private Integer chunksEmbedded;
   private String message;
   private String error;
}

