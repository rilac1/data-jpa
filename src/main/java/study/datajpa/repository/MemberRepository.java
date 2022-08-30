package study.datajpa.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import study.datajpa.dto.MemberDto;
import study.datajpa.enitity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	List<Member> findByUsernameAndAgeGreaterThan(String name, int age);

	// By 앞에 오는 Hello는 커스텀이 가능함. (아무거나 적어도 됨)
	List<Member> findHelloBy();

	// Limit 쿼리 (Hello는 빼도 됨)
	List<Member> findTop3HelloBy();

	// NamedQuery
	// NamedQuery 이름을 아래처럼 관례에 맞게 짓는다면, 아래 줄 생략 가능.
	@Query(name = "Member.findByUsername")
	List<Member> findByUsername(@Param("username") String username);

	// JPQL 파라미터 바인딩 (@Param)
	@Query("select m from Member m where m.username = :username and m.age = :age")
	List<Member> findMember(@Param("username") String username, @Param("age") int age);

	// 특정 필드만 조회
	@Query("select m.username from Member m")
	List<String> findUsernameList();

	// DTO로 조회하기
	@Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
	List<MemberDto> findMemberDto();

	// where-in 쿼리를 위해 Collection을 파라미터로 사용하기
	@Query("select m from Member m where m.username in :names")
	List<Member> findByNames(@Param("names") Collection<String> names);

	// List, T, Optional 등의 반환타입을 지원한다.
	List<Member> findListByUsername(String username);
	Member findMemberByUsername(String username);
	Optional<Member> findOptionalByUsername(String username);

	// Page에서 count 쿼리를 분리할 수 있다.
	// (성능 최적화 - left join의 경우 count 하는데 join이 필요하지 않기 때문)
	@Query(value = "select m from Member m left join m.team t",
		countQuery = "select count(m) from Member m")
	Page<Member> findByAge(int age, Pageable pageable);

	// 벌크성 수정 쿼리 (@Modifying 꼭 달아줘야 함)
	@Modifying(clearAutomatically = true) // 벌크 연산 후 영속성 컨텍스트 자동 초기화 옵션
	@Query("update Member m set m.age = m.age + 1 where m.age >= :age")
	int bulkAgePlus(@Param("age") int age);

	// FetchJoin (EntityGraph)
	@Override
	@EntityGraph(attributePaths = {"team"})
	List<Member> findAll();

	// 읽기 전용 쿼리.
	// JPA가 Snapshot을 만들지 않기 때문에 성능상에 조금 이점이 있지만, 변경감지가 작동하지 않음.
	@QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
	Member findReadOnlyByUsername(String username);

	// Lock을 걸면서 조회하는 쿼리
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	List<Member> findLockByUsername(String username);
}
