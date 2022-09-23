package com.example.rewardyourteachersq011bjavapode.serviceImpl;

import com.example.rewardyourteachersq011bjavapode.config.Security.CustomUserDetails;
import com.example.rewardyourteachersq011bjavapode.enums.NotificationType;
import com.example.rewardyourteachersq011bjavapode.event.OnUserLogoutSuccessEvent;
import com.example.rewardyourteachersq011bjavapode.exceptions.UserNotFoundException;
import com.example.rewardyourteachersq011bjavapode.exceptions.WalletNotFoundException;
import com.example.rewardyourteachersq011bjavapode.dto.UserProfileDto;
import com.example.rewardyourteachersq011bjavapode.enums.Role;
import com.example.rewardyourteachersq011bjavapode.event.OnUserLogoutSuccessEvent;
import com.example.rewardyourteachersq011bjavapode.exceptions.ResourceNotFoundException;
import com.example.rewardyourteachersq011bjavapode.exceptions.UserNotFoundException;
import com.example.rewardyourteachersq011bjavapode.models.Teacher;
import com.example.rewardyourteachersq011bjavapode.models.User;
import com.example.rewardyourteachersq011bjavapode.models.Wallet;
import com.example.rewardyourteachersq011bjavapode.repository.SubjectRepository;
import com.example.rewardyourteachersq011bjavapode.repository.TeacherRepository;
import com.example.rewardyourteachersq011bjavapode.repository.UserRepository;
import com.example.rewardyourteachersq011bjavapode.repository.WalletRepository;
import com.example.rewardyourteachersq011bjavapode.response.ApiResponse;
import com.example.rewardyourteachersq011bjavapode.service.NotificationService;
import com.example.rewardyourteachersq011bjavapode.service.UserService;
import com.example.rewardyourteachersq011bjavapode.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;



import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletRepository walletRepository;
    private final UserUtil userUtil;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher applicationEventPublisher;


    @Override
    public ApiResponse<String> logoutUser(CustomUserDetails currentUser, String bearerToken) {
        String token = bearerToken.substring(7);
        OnUserLogoutSuccessEvent logoutSuccessEvent = new OnUserLogoutSuccessEvent(currentUser.getUsername(), token);
        applicationEventPublisher.publishEvent(logoutSuccessEvent);
        String response = currentUser.getUsername() + " has successfully logged out from the system!";
        return new ApiResponse<>("success", LocalDateTime.now(), response);
    }




    @Override
    public ApiResponse<List<User>> searchTeacher(String name) {
        List<User> teacher = userRepository.findByRoleAndNameContainingIgnoreCase(Role.TEACHER, name);
        log.info("{}", teacher);
        return new ApiResponse<>("success", LocalDateTime.now(), teacher);
    }


    public User findById(Long id) {
        User teacher = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("user not found"));
        return teacher;
    }

    @Override
    public ApiResponse<UserProfileDto> viewProfile(Long id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new UserNotFoundException("user id not found"));
        UserProfileDto dto = convertModelToDto(teacher);
        return new ApiResponse<>("success", LocalDateTime.now(), dto);
    }

    @Override
    public BigDecimal currentBalance(Long user_id) {
        BigDecimal walletBalance = null;
        User user = findUserById(user_id);
        if (user.getId() != null) {
            Wallet userWallet = walletRepository.findById(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Wallet Not Found"));
            walletBalance = userWallet.getBalance();
        }
        return walletBalance;
    }

    @Override
    public BigDecimal currentBalance() {
        String userEmail = userUtil.getAuthenticatedUserEmail();
        Wallet wallet = walletRepository.findWalletByUserEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("Wallet of user not found"));
        return wallet.getBalance();
    }


    private UserProfileDto convertModelToDto(Teacher teacher) {
        UserProfileDto dto = new UserProfileDto();
        dto.setName(teacher.getName());
        dto.setSchool(teacher.getSchool());
        dto.setPost(teacher.getPost());
        dto.setAbout(teacher.getAbout());
        dto.setEmail(teacher.getEmail());
        dto.setTelephone(teacher.getTelephone());
        return dto;
    }

    @Override
    public User findUserById(Long user_id) {
        return userRepository.findById(user_id).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

}
