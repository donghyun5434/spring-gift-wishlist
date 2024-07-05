package gift.controller;

import gift.dto.UserRequestDto;
import gift.dto.UserResponseDto;
import gift.service.JwtProvider;
import gift.service.UserService;
import java.util.HashMap;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final JwtProvider jwtProvider;

    public UserController(UserService userService, JwtProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRequestDto requestDto){
        userService.save(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponseDto("유저 생성 완료"));
    }

    @PostMapping("/login")
    public ResponseEntity<HashMap<String,String>> login(@RequestBody UserRequestDto requestDto){
        // 회원 존재 확인
        userService.authenticate(requestDto.getEmail(),requestDto.getPassword());
        // 토큰 생성
        String token = jwtProvider.createToken(requestDto.getEmail());
        // 응답 생성
        HashMap<String,String> response = new HashMap<>();
        response.put("accessToken",token);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer" + token);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(response);
    }
}