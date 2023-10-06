package ai.atmc.hawkadoccollector.domain.dao.documentCollector;


import ai.atmc.hawkadoccollector.domain.mappers.general.HashMapConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

@Entity
@Table(name = "hawkadoc_document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HawkadocDocumentDao {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "execution_iteration_id")
    private Integer id;

    @Column(name = "execution_id")
    private Long executionId;

    @Column(name = "output_data_object_id")
    private Long outputDataObjectId;

    @Column(name = "document_name")
    private String documentName;

    @JoinColumn(name = "hitl_status")
    private Boolean hitlStatus;

    @Column(name = "output_data")
    @Convert(converter = HashMapConverter.class)
    private HashMap<String,Object> outputData;

    @Column(name = "original_info")
    private String tempInfo;

    @Column(name = "processing_time")
    private Long processingTime;

    @Column(name = "ingredients")
    private Long ingredients;

    @Column(name = "ingredients_found")
    private Long ingredientsFound;

    @Column(name = "ingredients_revised")
    private Long ingredientsRevised;

    @Column(name = "document_description")
    private String description;

    @Column(name = "archive_info")
    private String archiveInfo;

    @Column(name = "archived")
    private boolean archived;

}
