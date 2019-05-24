package com.rafaborrego.audit.mapper;

import com.rafaborrego.audit.dto.auditInputDto;
import com.rafaborrego.audit.dto.auditOutputDto;
import com.rafaborrego.audit.entity.audit;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Configuration;

/**
 * Orika configuration for mapping the fields of entities and DTOs
 */
@Configuration
public class BeanMapperConfiguration implements FactoryBean<MapperFactory> {

    @Override
    public MapperFactory getObject() {
        return getDefaultMapperFactory();
    }

    public static MapperFactory getDefaultMapperFactory() {

        final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        registerauditInputDtoTransformation(mapperFactory);
        registerauditOutputDtoTransformation(mapperFactory);

        return mapperFactory;
    }

    private static void registerauditInputDtoTransformation(MapperFactory mapperFactory) {

        final ClassMapBuilder<audit, auditInputDto> classMapBuilder =
                mapperFactory.classMap(audit.class, auditInputDto.class);

        classMapBuilder
                .byDefault()
                .register();
    }

    private static void registerauditOutputDtoTransformation(MapperFactory mapperFactory) {

        final ClassMapBuilder<audit, auditOutputDto> classMapBuilder =
                mapperFactory.classMap(audit.class, auditOutputDto.class);

        classMapBuilder
                .byDefault()
                .register();
    }

    @Override
    public Class<?> getObjectType() {
        return MapperFactory.class;
    }
}
