package com.qooco.boost.data.oracle.reposistories;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.springframework.asm.SpringAsmInfo.ASM_VERSION;

@Component
@Getter
public class RepositoryUtils {
    public static final boolean IS_SPRING1 = ASM_VERSION <= 393216;
    public static final String PAGEABLE_HOLDER = IS_SPRING1 ? "  \n-- #pageable \n " : " ";
    public static final String ORDERBY_PAGEABLE_HOLDER = IS_SPRING1 ? "  ORDER BY ?#{#pageable} " : " ";

    @Value("#{'${spring.datasource.url}'.contains('jdbc:h2:') ? null : ''}")
    private String nullValue;
}
