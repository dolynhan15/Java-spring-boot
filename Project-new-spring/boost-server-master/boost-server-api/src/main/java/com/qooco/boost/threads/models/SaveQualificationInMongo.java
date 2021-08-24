package com.qooco.boost.threads.models;

import com.qooco.boost.data.oracle.entities.Assessment;
import com.qooco.boost.data.oracle.entities.UserQualification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class SaveQualificationInMongo {
    private UserQualification qualification;
    private Assessment assessment;
}
