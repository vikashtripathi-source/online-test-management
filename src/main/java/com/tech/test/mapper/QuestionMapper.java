package com.tech.test.mapper;

import com.tech.test.dto.QuestionDTO;
import com.tech.test.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    QuestionDTO toDTO(Question question);

    Question toEntity(QuestionDTO questionDTO);

    java.util.List<QuestionDTO> toDTOList(java.util.List<Question> questions);
    java.util.List<Question> toEntityList(java.util.List<QuestionDTO> questionDTOs);
}

