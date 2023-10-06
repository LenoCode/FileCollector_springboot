package ai.atmc.hawkadoccollector;

import ai.atmc.hawkadoccollector.domain.dto.DtoModel;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TestJson implements DtoModel {


        @NotNull(groups = {TestGroup.class} ,message = "Name is required")
        private String name;

}
