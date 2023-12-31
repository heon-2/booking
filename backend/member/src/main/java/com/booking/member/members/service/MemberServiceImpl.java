package com.booking.member.members.service;

import com.booking.member.Auth.TokenDto;
import com.booking.member.Auth.TokenProvider;
import com.booking.member.members.domain.Gender;
import com.booking.member.members.domain.Member;
import com.booking.member.members.domain.UserRole;
import com.booking.member.members.dto.ChangeLocationRequestDto;
import com.booking.member.members.dto.MemberInfoResponseDto;
import com.booking.member.members.dto.ModifyRequestDto;
import com.booking.member.members.dto.SignUpRequestDto;
import com.booking.member.members.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    @Override
    @Transactional
    public Mono<String> signup(SignUpRequestDto req) {

        return Mono.justOrEmpty(memberRepository.findByLoginId(req.loginId()))
                .defaultIfEmpty(new Member())
                .flatMap(member -> {
                    if (checkMemberDuplicate(req.loginId())) {
                        return Mono.error(new RuntimeException("이미 가입된 회원입니다."));
                    } else if (checkNicknameDuplicate(req.nickname())) {
                        return Mono.error(new RuntimeException("중복된 닉네임"));
                    }

                    String[] split=parseAddr(req.address());
                    Double lat=Double.parseDouble(split[0].trim());
                    Double lgt=Double.parseDouble(split[1].trim());

                    Member mem=Member.builder()
                            .age(req.age())
                            .email(req.email())
                            .gender(Gender.valueOf(req.gender()))
                            .loginId(req.loginId())
                            .nickname(req.nickname())
                            .fullName(req.fullName())
                            .lat(lat)
                            .lgt(lgt)
                            .role(UserRole.USER)
                            .profileImage(req.profileImage())
                            .provider(req.provider())
                            .point(0)
                            .build();

                    return Mono.fromRunnable(() ->  memberRepository.save(mem))
                            .subscribeOn(Schedulers.boundedElastic())
                            .then(Mono.just(mem));

                })
                .flatMap(member ->
                        Mono.fromCallable(()-> tokenProvider.createToken(member.getLoginId(), member.getId()))
                                .subscribeOn(Schedulers.boundedElastic())
                                .map(TokenDto::getAccessToken))
                .onErrorResume(e -> {
                    log.error("회원 가입 에러: {}", e.getMessage());
                    return Mono.error(e);
                });
    }

    @Override
    public Mono<MemberInfoResponseDto> loadMemberInfo(String loginId) {
        Member member = memberRepository.findByLoginId(loginId);
        if (member == null) return Mono.error(new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return Mono.just(MemberInfoResponseDto.of(member));
    }

    @Override
    public Mono<MemberInfoResponseDto> loadMemberInfoByPk(Integer memberPk) {
        Optional<Member> optionalMember = memberRepository.findById(memberPk);
        if(optionalMember.isPresent()){
            Member member=optionalMember.get();
            return Mono.just(MemberInfoResponseDto.of(member));
        }
        else{
            return Mono.error(new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        }

    }

    @Override
    public Mono<MemberInfoResponseDto> loadMemberInfoByNickname(String nickname) {
        Member member= memberRepository.findByNickname(nickname);
        if(member==null)throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        return Mono.just(MemberInfoResponseDto.of(member));
    }

    @Override
    @Transactional
    public Mono<Void> modifyMemberInfo(ModifyRequestDto req) {
        return Mono.defer(() -> {
                    Member member = memberRepository.findByLoginId(req.loginId());
                    if (member == null) return Mono.error(new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

                    if(req.nickname().isEmpty())return Mono.error(new RuntimeException("닉네임 빈 문자열"));

                    member.setNickname(req.nickname());
                    member.setProfileImage(req.profileImage());

                    return Mono.fromRunnable(() -> memberRepository.save(member))
                            .subscribeOn(Schedulers.boundedElastic())
                            .then();
                })
                .then()
                .onErrorResume(e -> {
                    log.error("회원 수정 에러: {}", e.getMessage());
                    return Mono.error(e);
                });
    }

    @Override
    @Transactional
    public Mono<Void> deleteMember(String loginId) {
        return Mono.defer(() -> {
                    Member member = memberRepository.findByLoginId(loginId);
                    if (member == null) return Mono.error(new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
                    return Mono.fromRunnable(() -> memberRepository.delete(member))
                            .subscribeOn(Schedulers.boundedElastic())
                            .then();
                })
                .then()
                .onErrorResume(e -> {
                    log.error("회원 탈퇴 에러: {}", e.getMessage());
                    return Mono.error(e);
                });
    }

    @Override
    public Mono<String> login(String loginId) {
        Member member=memberRepository.findByLoginId(loginId);
        if (member == null) {
            return Mono.error(new UsernameNotFoundException("회원 가입이 필요합니다."));
        }
        return Mono.fromCallable(() -> tokenProvider.createToken(loginId,member.getId()))
                .map(TokenDto::getAccessToken);
    }

    @Override
    public Mono<Void> changeLocation(ChangeLocationRequestDto req,String loginId) {
        Member member=memberRepository.findByLoginId(loginId);

        String[] split=parseAddr(req.address());
        Double lat=Double.parseDouble(split[0].trim());
        Double lgt=Double.parseDouble(split[1].trim());

        member.setLat(lat);
        member.setLgt(lgt);
        memberRepository.save(member);
        return Mono.empty();
    }

    public boolean checkMemberDuplicate(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }

    public boolean checkNicknameDuplicate(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    public String[] parseAddr(String address){
        String addr=address.substring(14);
        addr=addr.substring(0,addr.indexOf("hAcc")).trim();
        return addr.split(",");
    }
}
