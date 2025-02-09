package com.postsuite.derailer.mapper;

import com.postsuite.derailer.entities.PauseEntity;
import com.postsuite.derailer.models.PauseModel;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.JAKARTA,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface PauseMapper {

    PauseModel toModel(final PauseEntity pause);

}
