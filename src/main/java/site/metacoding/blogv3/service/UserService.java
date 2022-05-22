package site.metacoding.blogv3.service;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.metacoding.blogv3.domain.user.User;
import site.metacoding.blogv3.domain.user.UserRepository;
import site.metacoding.blogv3.web.dto.UpdatedDto;

@RequiredArgsConstructor
@Service // IoC 등록
public class UserService {

    // DI
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void 회원가입(User user) {
        // 1. save 한번
        String rawPassword = user.getPassword(); // 1234
        String encPassword = bCryptPasswordEncoder.encode(rawPassword); // 해쉬 알고리즘
        user.setPassword(encPassword);

        User userEntity = userRepository.save(user);
    }

    public boolean 유저네임중복체크(String username) {
        Optional<User> userOp = userRepository.findByUsername(username);

        if (userOp.isPresent()) {
            return false;
        } else {
            return true;
        }
    }

    // 회원정보 데이터 가져오기
    public User 회원정보보기(Integer id) {
        Optional<User> userOp = userRepository.findById(id);

        if (userOp.isPresent()) {
            return userOp.get();
        } else {
            System.out.println("아이디를 찾을 수 없습니다.");
            return null;
        }
    }

    // 회원정보 수정
    @Transactional
    public User 회원수정(Integer id, UpdatedDto updateDto) {

        Optional<User> userOp = userRepository.findById(id);

        if (userOp.isPresent()) {
            // 영속화된 오브젝트 수정
            User userEntity = userOp.get();
            userEntity.setUsername(updateDto.getUsername());
            String rawPassword = updateDto.getPassword(); // 수정될 비밀번호 받아오기
            String encPassword = bCryptPasswordEncoder.encode(rawPassword); // 해쉬 알고리즘
            userEntity.setPassword(encPassword);
            return userEntity;
        } else {
            throw new RuntimeException("아이디를 찾을 수 없습니다.");
        }
    }

    // 회원 탈퇴하기
    @Transactional
    public void 회원탈퇴(Integer id) {
        userRepository.deleteById(id);
    }

}
