package com.tech.test.mapper;

import com.tech.test.dto.StudentTestRecordDTO;
import com.tech.test.entity.StudentTestRecord;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface StudentTestRecordMapper {

    StudentTestRecordMapper INSTANCE = Mappers.getMapper(StudentTestRecordMapper.class);

    StudentTestRecordDTO toDTO(StudentTestRecord studentTestRecord);

    StudentTestRecord toEntity(StudentTestRecordDTO studentTestRecordDTO);

    java.util.List<StudentTestRecordDTO> toDTOList(
            java.util.List<StudentTestRecord> studentTestRecords);

    java.util.List<StudentTestRecord> toEntityList(
            java.util.List<StudentTestRecordDTO> studentTestRecordDTOs);
}
