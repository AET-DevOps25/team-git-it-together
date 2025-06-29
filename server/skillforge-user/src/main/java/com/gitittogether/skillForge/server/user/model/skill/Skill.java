package com.gitittogether.skillForge.server.user.model.skill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "skills")
public class Skill {
    @Id
    private String id;
    private String name;
    private String description;
    private String category;
    private String level;
}
