package com.dilidili.api.mapper;

import com.dilidili.api.dto.UserRegisterRequest;
import com.dilidili.dao.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用户 DTO 映射器
 */
@Mapper(componentModel = "spring")
public interface UserDtoMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    User toEntity(UserRegisterRequest request);
}