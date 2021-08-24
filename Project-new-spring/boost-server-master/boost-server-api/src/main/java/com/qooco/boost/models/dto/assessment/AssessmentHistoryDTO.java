package com.qooco.boost.models.dto.assessment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.LevelEmbedded;
import com.qooco.boost.data.mongo.entities.AssessmentTestHistoryDoc;
import com.qooco.boost.models.dto.BaseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/*
 * Copyright: Falcon Team - AxonActive
 * User: tnlong
 * Date: 10/1/2018 - 2:48 PM
 */
@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldNameConstants
public class AssessmentHistoryDTO extends BaseDTO {
    private String name;
    private List<TestHistoryDTO> testHistories;
    private List<LevelDTO> levels;
    private int minLevel;
    private int maxLevel;

    public AssessmentHistoryDTO(String name, List<TestHistoryDTO> history) {
        this.name = name;
        this.testHistories = history;
    }

    public AssessmentHistoryDTO(List<AssessmentTestHistoryDoc> historyDocs, List<LevelEmbedded> levels, int expiredDay) {
        if (CollectionUtils.isNotEmpty(historyDocs)) {
            this.name = historyDocs.get(0).getAssessmentName();
            this.minLevel = historyDocs.get(0).getMinLevel();
            this.maxLevel = historyDocs.get(0).getMaxLevel();

            this.testHistories = historyDocs.stream().map(hd -> new TestHistoryDTO(hd, expiredDay)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(levels)) {
                this.levels = levels.stream().map(LevelDTO::new).collect(Collectors.toList());
            }
        }
    }


}
