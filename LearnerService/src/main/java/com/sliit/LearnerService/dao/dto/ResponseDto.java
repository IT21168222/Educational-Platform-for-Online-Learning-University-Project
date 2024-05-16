package com.sliit.LearnerService.dao.dto;

import java.util.List;

import com.sliit.LearnerService.dao.domain.UserEnrolledCourse;
import com.sliit.LearnerService.util.enums.ApiStatus;
import lombok.Data;

@Data
public class ResponseDto {
    private boolean isSuccess;
    private ApiStatus apiStatus;
    private String error;
    private List<CourseDto> courseDtoList;
    private boolean note;
    private boolean quiz;
    private boolean video;
    private UserEnrolledCourse enrolledCourse;
}
