package study.datajpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import study.datajpa.enitity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	List<Member> findByUsernameAndAgeGreaterThan(String name, int age);

	List<Member> findTop3HelloBy();

	// @Query(name = "Member.findByUsername")
	List<Member> findByUsername(@Param("username") String username);
}
