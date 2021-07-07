package com.chore.crunch.repository.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "This model represents a node stored in the repository")
@DynamoDBTable(tableName = "chore_crunch")
public class Node {

    @Schema(description = "nodeId", example = "Hvk031weL")
    @DynamoDBHashKey(attributeName = "nodeId")
    @DynamoDBAutoGeneratedKey
    private String nodeId;
    @DynamoDBAttribute(attributeName = "name")
    private String name;
    @DynamoDBAttribute(attributeName = "path")
    @DynamoDBRangeKey
    private String path;
    @DynamoDBAttribute(attributeName = "properties")
    private List<Property> properties;

}
