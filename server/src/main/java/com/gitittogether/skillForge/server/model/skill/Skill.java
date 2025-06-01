package com.gitittogether.skillForge.server.model.skill;

import com.gitittogether.skillForge.server.model.course.Category;
import com.gitittogether.skillForge.server.model.course.Level;
import lombok.*;
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

    @NonNull
    private String name;

    @NonNull
    private String description;

    @NonNull
    private Category category;

    @NonNull
    private String iconUrl;

    @NonNull
    private Level level;

}
