package com.nexora.rsp.talentcore.dto;

import com.nexora.rsp.talentcore.search.ActiveStatusSearchValueTransformer;
import com.nexora.rsp.talentcore.search.SearchField;
import com.nexora.rsp.talentcore.search.SearchOperation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "User search filters and pagination request")
public class UserSearchRequest extends PageRequestDto {

    @SearchField(operation = SearchOperation.LIKE)
    @Schema(description = "Optional first name filter. Uses case-insensitive LIKE matching.", example = "murali")
    private String firstName;

    @SearchField(operation = SearchOperation.LIKE)
    @Schema(description = "Optional last name filter. Uses case-insensitive LIKE matching.", example = "kumar")
    private String lastName;

    @SearchField(operation = SearchOperation.LIKE)
    @Schema(description = "Optional email filter. Uses case-insensitive LIKE matching.", example = "test@gmail.com")
    private String email;

    @SearchField(path = "userStatus", transformer = ActiveStatusSearchValueTransformer.class)
    @Schema(description = "Optional active status filter. true maps to ACTIVE and false maps to INACTIVE.", example = "true")
    private Boolean active;

    @SearchField(path = "userRoles.role.roleCode")
    @Schema(description = "Optional exact role code filter", example = "TEAM_MANAGER")
    private String roleCode;
}
