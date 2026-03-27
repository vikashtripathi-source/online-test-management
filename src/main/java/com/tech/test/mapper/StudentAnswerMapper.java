package com.tech.test.mapper;

import com.tech.test.dto.StudentAnswerDTO;
import com.tech.test.entity.StudentAnswer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface StudentAnswerMapper {

    StudentAnswerMapper INSTANCE = Mappers.getMapper(StudentAnswerMapper.class);

    StudentAnswerDTO toDTO(StudentAnswer studentAnswer);

    StudentAnswer toEntity(StudentAnswerDTO studentAnswerDTO);

    java.util.List<StudentAnswerDTO> toDTOList(java.util.List<StudentAnswer> studentAnswers);
    java.util.List<StudentAnswer> toEntityList(java.util.List<StudentAnswerDTO> studentAnswerDTOs);
}

