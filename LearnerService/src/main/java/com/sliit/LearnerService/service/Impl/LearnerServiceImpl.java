package com.sliit.LearnerService.service.Impl;

import com.sliit.LearnerService.dao.domain.UserEnrolledCourse;
import com.sliit.LearnerService.dao.dto.CourseDto;
import com.sliit.LearnerService.dao.dto.LearnerProgressDto;
import com.sliit.LearnerService.dao.dto.RequestDto;
import com.sliit.LearnerService.dao.dto.ResponseDto;
import com.sliit.LearnerService.dao.repositories.UserEnrolledCourseRepository;
import com.sliit.LearnerService.service.i.LearnerService;
import com.sliit.LearnerService.util.enums.ApiStatus;
import com.sliit.LearnerService.util.enums.CommonStatus;

import client.CourseClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service

@RequiredArgsConstructor
public class LearnerServiceImpl implements LearnerService {

    private final UserEnrolledCourseRepository userEnrolledCourseRepository;
//    private final EnrolledCourseProgressRepository enrolledCourseProgressRepository;
    private final WebClient webClient;
    
    private static final Logger log = LoggerFactory.getLogger(LearnerServiceImpl.class);
    
    @Autowired
    private CourseClient courseClient;

    @Override
    public ResponseDto enrollCourse(RequestDto requestDto) {
        ResponseDto responseDto = new ResponseDto();
        try {
            if(requestDto.getUserEnrolledCourseDto() != null){

                //Check for existing values
                boolean isExists = userEnrolledCourseRepository.existsByEnrolledCourseIdAndUserIdAndStatus(requestDto.getUserEnrolledCourseDto().getEnrolledCourseId(),requestDto.getUserEnrolledCourseDto().getUserId(), CommonStatus.ACTIVE);
                if(isExists){
                    throw new RuntimeException("User has already enrolled to this course.");
                }

                UserEnrolledCourse userEnrolledCourse = UserEnrolledCourse.builder()
                        .status(CommonStatus.ACTIVE)
                        .userId(requestDto.getUserEnrolledCourseDto().getUserId())
                        .enrolledCourseId(requestDto.getUserEnrolledCourseDto().getEnrolledCourseId())
                        .enrolledDate(new Date())
                        .quiz(false)
                        .note(false)
                        .video(false)
                        //.paymentStatus(CommonStatus.UNPAID)
                        .build();
                UserEnrolledCourse savedCourse = userEnrolledCourseRepository.save(userEnrolledCourse);

                // method

                //savedCourse.setStatus(CommonStatus.ACTIVE);
                responseDto.setApiStatus(ApiStatus.SUCCESS);
                responseDto.setSuccess(true);
            } else {
                responseDto.setApiStatus(ApiStatus.INPUT_NULL);
            }
        } catch (Exception e){
            log.error(e.getMessage());
            responseDto.setApiStatus(ApiStatus.SOMETHING_WENT_WRONG);
            responseDto.setError(e.getMessage());
        }
        return responseDto;
    }

    @Override
    public ResponseDto cancelCourseEnroll(RequestDto requestDto) {
        ResponseDto responseDto = new ResponseDto();
        try {
            if(requestDto.getUserEnrolledCourseDto() != null){
                //Find existing value
                UserEnrolledCourse existingObj = userEnrolledCourseRepository.findByEnrolledCourseIdAndUserIdAndStatus(requestDto.getUserEnrolledCourseDto().getEnrolledCourseId(),requestDto.getUserEnrolledCourseDto().getUserId(), CommonStatus.ACTIVE);

                existingObj.setStatus(CommonStatus.DELETED);

                userEnrolledCourseRepository.save(existingObj);
                responseDto.setApiStatus(ApiStatus.DELETE_SUCCESS);
                responseDto.setSuccess(true);
            } else {
                responseDto.setApiStatus(ApiStatus.INPUT_NULL);
            }
        } catch (Exception e){
            log.error(e.getMessage());
            responseDto.setApiStatus(ApiStatus.SOMETHING_WENT_WRONG);
            responseDto.setError(e.getMessage());
        }
        return responseDto;
    }

    @Override
    public ResponseDto findUserEnrolledCoursesByUserId(RequestDto requestDto) {

        ResponseDto responseDto = new ResponseDto();
        
        try {
        System.out.println(requestDto);
			if(requestDto.getUserEnrolledCourseDto().getUserId()!= null) {
				//Find User enrolled course IDs
		        List<UserEnrolledCourse> courseIds = userEnrolledCourseRepository.findAllByUserIdAndStatus(requestDto.getUserEnrolledCourseDto().getUserId(), CommonStatus.ACTIVE);
		        System.out.println(courseIds);
		        // Create Empty CourseDto ArrayList
		        List<CourseDto> courseDtoList = new ArrayList<>();

		        //List<CourseDto> course = webClient.get().uri("http://localhost:8081/api/v1/courses/public/available")
     				   //.retrieve().bodyToMono(CourseDto.class).block();
		        List<CourseDto> allCourses = new ArrayList<>();
		        allCourses.addAll(courseClient.getApprovedCourses().getBody());
		        //Find Courses By Course IDs
		        for(CourseDto i : allCourses){
		            // Call the APi to find the CourseDto by course Id
		        	if(courseIds.stream().anyMatch(s->s.getEnrolledCourseId().equals(i.getId()))) {
		        		 courseDtoList.add(i);
		        	}
	               
		            
		        }
		        System.out.println(courseDtoList);
		        responseDto.setCourseDtoList(courseDtoList);
	            responseDto.setApiStatus(ApiStatus.SUCCESS);
	            responseDto.setSuccess(true);
	            
	        } else {
	            responseDto.setApiStatus(ApiStatus.INPUT_NULL);
	        }
			
			
		} catch (Exception e) {
			log.error(e.getMessage());
	        responseDto.setApiStatus(ApiStatus.SOMETHING_WENT_WRONG);
	        responseDto.setError(e.getMessage());
		}

        //responseDto.setCourseDtoList(courseDtoList);
        return responseDto;
    }

    @Override
    public ResponseDto updateEnrolledCourseContent(RequestDto requestDto) {
        /*
        *  Inputs :  quizMarks, courseId, contentId, userId
        * */
    	ResponseDto responseDto = new ResponseDto();
    	try {
    		if (requestDto.getUserEnrolledCourseDto() != null && requestDto.getUserEnrolledCourseDto().getCourseDto() != null) {
    			// Find UserEnrolledCourse by courseId, userId and status(ACTIVE)
    			UserEnrolledCourse userEnrolledCourse = userEnrolledCourseRepository.findByEnrolledCourseIdAndUserIdAndStatus(
                        requestDto.getUserEnrolledCourseDto().getCourseDto().getId(),
                        requestDto.getUserId(),
                        CommonStatus.ACTIVE
                );
    			if (userEnrolledCourse != null) {
    				// Find EnrolledCourseProgress by UserEnrolledCourse and contentId
                    userEnrolledCourse.setQuiz(requestDto.getUserEnrolledCourseDto().getQuiz());
                    userEnrolledCourse.setNote(requestDto.getUserEnrolledCourseDto().getNote());
                    userEnrolledCourse.setVideo(requestDto.getUserEnrolledCourseDto().getVideo());

                    userEnrolledCourseRepository.save(userEnrolledCourse);
                } else {
                    throw new RuntimeException("User enrolled course not found.");
                }
            } else {
                responseDto.setApiStatus(ApiStatus.INPUT_NULL);
            }
    			
    		
		} catch (Exception e) {
			log.error(e.getMessage());
	        responseDto.setApiStatus(ApiStatus.SOMETHING_WENT_WRONG);
	        responseDto.setError(e.getMessage());
		}

        return responseDto;
    }

    @Override
    public List<LearnerProgressDto> retrieveLearnerProgresses(String courseId) {
        List<UserEnrolledCourse> userEnrolledCourseList = userEnrolledCourseRepository.findAllByEnrolledCourseId(courseId);

        ArrayList<LearnerProgressDto> learnerProgressDtos = new ArrayList<>();

        for (UserEnrolledCourse userEnrolledCourse: userEnrolledCourseList) {
            LearnerProgressDto learnerProgressDto = new LearnerProgressDto();
            learnerProgressDto.setCourseId(userEnrolledCourse.getEnrolledCourseId());
            learnerProgressDto.setUserId(userEnrolledCourse.getUserId());
            learnerProgressDto.setNote(userEnrolledCourse.getNote());
            learnerProgressDto.setQuiz(userEnrolledCourse.getQuiz());
            learnerProgressDto.setVideo(userEnrolledCourse.getVideo());

            learnerProgressDtos.add(learnerProgressDto);
        }
        System.out.println(learnerProgressDtos);
        return learnerProgressDtos;
    }

    @Override
    public LearnerProgressDto retrieveLearnerProgressOfUser(String courseId, String userId) {
        UserEnrolledCourse userEnrolledCourse = userEnrolledCourseRepository.findByEnrolledCourseIdAndUserId(courseId, userId);

        LearnerProgressDto learnerProgress = new LearnerProgressDto();

        learnerProgress.setCourseId(userEnrolledCourse.getEnrolledCourseId());
        learnerProgress.setUserId(userEnrolledCourse.getUserId());
        learnerProgress.setNote(userEnrolledCourse.getNote());
        learnerProgress.setQuiz(userEnrolledCourse.getQuiz());
        learnerProgress.setVideo(userEnrolledCourse.getVideo());

        return learnerProgress;
    }

	@Override
	public ResponseDto findProgressByCourseIdAndUserId(RequestDto requestDto) {
		ResponseDto responseDto = new ResponseDto();
		
		try {
			if(requestDto.getUserId() != null && requestDto.getContentId() != null) {
				UserEnrolledCourse userEnrolledCourse = userEnrolledCourseRepository.findByEnrolledCourseIdAndUserId(requestDto.getCourseId(), requestDto.getUserId());
				if(userEnrolledCourse != null) {
					responseDto.setEnrolledCourse(userEnrolledCourse);
					responseDto.setApiStatus(ApiStatus.SUCCESS);
					responseDto.setSuccess(true);
				}else {
					responseDto.setApiStatus(ApiStatus.NOT_FOUND);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			responseDto.setApiStatus(ApiStatus.SOMETHING_WENT_WRONG);
			responseDto.setError(e.getMessage());
		}
		
		return responseDto;
	}
}
