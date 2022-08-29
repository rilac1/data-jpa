package study.datajpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import study.datajpa.enitity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	List<Member> findByUsernameAndAgeGreaterThan(String name, int age);

	List<Member> findTop3HelloBy();
}
