package com.example.rewardyourteachersq011bjavapode.serviceImpl;

import com.example.rewardyourteachersq011bjavapode.dto.TeacherRegistrationDto;
import com.example.rewardyourteachersq011bjavapode.dto.UserDto;
import com.example.rewardyourteachersq011bjavapode.enums.NotificationType;
import com.example.rewardyourteachersq011bjavapode.enums.Role;
import com.example.rewardyourteachersq011bjavapode.enums.SchoolType;
import com.example.rewardyourteachersq011bjavapode.enums.Status;
import com.example.rewardyourteachersq011bjavapode.exceptions.UserAlreadyExistException;
import com.example.rewardyourteachersq011bjavapode.models.*;
import com.example.rewardyourteachersq011bjavapode.repository.SubjectRepository;
import com.example.rewardyourteachersq011bjavapode.repository.UserRepository;
import com.example.rewardyourteachersq011bjavapode.repository.WalletRepository;
import com.example.rewardyourteachersq011bjavapode.response.UserRegistrationResponse;
import com.example.rewardyourteachersq011bjavapode.utils.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Calendar.SEPTEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserRepository userRepository;
    @Mock
    SubjectRepository subjectRepository;
    @Mock
    WalletRepository walletRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    UserUtil userUtil;


    @InjectMocks
    AuthServiceImpl authService;

    @InjectMocks
    UserServiceImpl userService;


    private LocalDateTime localDateTime;


    private User user;

    private Wallet wallet;

    private BigDecimal bigDecimal;
    private Teacher teacher;

    private List<Subject> subjectList;
    private List<String> listSubject;
    private List<Transaction> transactionList;
    private List<Message> messageList;
    private List<Notification> notificationList;
    private UserRegistrationResponse userRegistrationResponse;

    private School school;
    private Transaction transaction;

    private MultipartFile multipartFile;
    private Subject subject;
    private Notification notification;
    private Message message;
    private List<User> userList;
    private MultipartFile teacherId;


    @BeforeEach
    void setUp() {
        localDateTime = LocalDateTime.of(2022, SEPTEMBER, 14, 6, 30, 40, 50000);
        user = new User(2L, localDateTime, localDateTime, "chioma", Role.STUDENT, "chioma@gmail.com", "1234", transactionList, messageList, notificationList, "school");
        teacher = new Teacher("20", Status.INSERVICE, SchoolType.SECONDARY, "oxy.png", subjectList);
        message = new Message("new message", user);
        notification = new Notification("alertz", NotificationType.CREDIT_NOTIFICATION, user);
        subject = new Subject("Economics", teacher);
        wallet = new Wallet(new BigDecimal(100), user);
        listSubject = new ArrayList<>();
        listSubject.add("Math");
    }

    @Test
    void registerUser() {
        UserDto userDto = new UserDto("chioma", "chioma@gmail.com", passwordEncoder.encode("1234"), "school");
        when(userRepository.findUserByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        var actual = authService.registerUser(userDto);
        assertEquals("success", actual.getMessage());

    }

    @Test
    void registerAlreadyExistingUser() {
        UserDto userDto = new UserDto("chioma", "chioma@gmail.com", passwordEncoder.encode("1234"), "school");
        when(userRepository.findUserByEmail(userDto.getEmail())).thenReturn(Optional.of(user));
        assertThrows(UserAlreadyExistException.class, () -> authService.registerUser(userDto));

    }

    @Test
    void registerTeacher() throws IOException {
        multipartFile = mock(MultipartFile.class);
        TeacherRegistrationDto teacherDto = new TeacherRegistrationDto("2012-2016", listSubject, SchoolType.SECONDARY);
        when(userRepository.findUserByEmail(teacherDto.getEmail())).thenReturn(Optional.empty());
        when(userUtil.uploadImage(multipartFile)).thenReturn("uploaded");
        var actual = authService.registerTeacher(teacherDto, multipartFile);
        assertEquals("success", actual.getMessage());
    }

    @Test
    void currentUserWalletBalance() {
        when(userUtil.getAuthenticatedUserEmail()).thenReturn(user.getEmail());
        when(walletRepository.findWalletByUserEmail(user.getEmail())).thenReturn(Optional.of(wallet));
        var actual = userService.currentBalance();
        assertEquals(new BigDecimal(100), actual);
    }

}