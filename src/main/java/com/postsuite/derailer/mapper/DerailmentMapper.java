package com.postsuite.derailer.mapper;

import com.postsuite.derailer.entities.DerailmentEntity;
import com.postsuite.derailer.entities.DerailmentState;
import com.postsuite.derailer.models.response.DerailmentModel;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.JAKARTA,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface DerailmentMapper {

    @Mapping(target = "rolledBack", expression = "java(isRolledBack(entity))")
    DerailmentModel toModel(final DerailmentEntity entity);

    default boolean isRolledBack(final DerailmentEntity derailmentEntity) {
        return derailmentEntity.getState().equals(DerailmentState.COMPLETE);
    }

    List<DerailmentModel> toModels(final List<DerailmentEntity> entityList);

}
