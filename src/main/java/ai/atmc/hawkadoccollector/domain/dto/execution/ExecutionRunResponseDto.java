package ai.atmc.hawkadoccollector.domain.dto.execution;

import ai.atmc.hawkadoccollector.domain.dto.DtoModel;

public record ExecutionRunResponseDto (
        boolean initializationStatus,
        boolean finishStatus

) {


}
