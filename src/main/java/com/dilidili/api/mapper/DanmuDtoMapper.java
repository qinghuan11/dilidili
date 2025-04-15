package com.dilidili.api.mapper;

import com.dilidili.dao.domain.Danmu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 弹幕 DTO 映射器
 */
@Mapper(componentModel = "spring")
public interface DanmuDtoMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    Danmu toEntity(Danmu request);
}