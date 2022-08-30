# 스프링 데이터 JPA

#### 쿼리 자세히 볼 수 있는 라이브러리

`implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.5.7'`

#### 공통 인터페이스 구성

JpaRepository &larr; PagingAndSortingRepository &larr; CrudRepository &larr; Repository

### 쿼리 메소드 기능

>  [스프링 데이터 JPA 공식 문서 참고](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation)

#### JPA NamedQuery

- 실무에서 쓸 일이 없음
  - 쿼리가 레포지토리가 아닌 엔티티에 있어야 함.

##### `@Query`

- 복잡한 쿼리를 레포지토리 내부에 작성할 수 있음.
- NamedQuery와 마찬가지로 쿼리 오류가 애플리케이션 실행 시점에 잡힘.

#### 정리

1. 간단한 쿼리는 DataJpa 만 사용한다.
2. 조건이 3개 이상 되는 복잡한 쿼리는 DataJpa를 사용할 경우 메서드 명이 너무 길어지거나 DataJpa만으로 구현이 불가능한 경우가 발생한다.
3. 따라서 `@Query`를 사용하여 JPQL로 직접 쿼리를 작성한다.
4. 동적 쿼리의 경우 `Querydsl`을 사용한다.

#### 반환 타입

- List 타입일 때 결과가 없다면 Null이 아닌 빈 List가 반환된다.
- 단건 조회일 때 결과가 없다면 Null이 반환됨.
  - 그래서 Optional을 사용하는 것이 좋다.
- 단건 조회일 때 결과가 여러 개라면 예외가 터짐.

#### 스프링 데이터 JPA 페이징과 정렬 [중요!!!]

- `Sort` : 정렬 기능
  - `Pagable` 인터페이스를 파라미터로 가짐.
  - 보통 구현체로 `PageRequest`를 아규먼트로 전달함.
- `Pageable` : 페이징 기능 (내부에 `Sort` 포함)
- `Page` : 추가 count 쿼리 결과를 포함하는 페이징
  - count 쿼리는 데이터가 많아질수록 성능 저하가 심함.
  - 따라서 join과 관계없이 데이터 수가 고정되는 쿼리라면 count 쿼리를 분리하는 것이 좋음. (`@Query` 어노테이션에서 기능 제공)
- `Slice` : 추가 count 쿼리 없이 다음 페이지만 확인 가능 (내부적으로 limit + 1 조회)
  - (더 보기) 스타일은 count가 필요없음.
  - 다음 페이지가 있는지 없는지만 확인하면 되기 때문에 limit보다 1개 더 조회함.
- `List` : 추가 count 쿼리 없이 결과만 반환

#### 벌크성 수정 쿼리

- `@Modifying` 어노테이션 달아줘야 함.
  - `clearAutomatically = true` 옵션 달면 자동으로 영속성 컨텍스트를 비워줌.
- 벌크연산은 영속성 컨텍스트에 반영되지 않음.
  - 따라서 벌크연산을 하고 나서는 영속성 컨텍스트를 전부 비워줘야 함.
    - `em.flush()`,  `em.clear()`
    - 또는 `@Modifying`에 옵션 달기

#### 엔티티 그래프

- 간단한 쿼리에 FetchJoin을 적용하고 싶은데 JPQL을 작성하고 싶진 않을 때 사용함.
- `@EntityGraph(attributePaths = {"team"})`
  - 위 어노테이션을 달면 FetchJoin이 적용됨.

### 확장 기능

#### 사용자 정의 리포지토리 구현

- CustomRepository의 구현체는 반드시 끝에 Impl을 붙여야 함.

- 모든 것을 사용자 정의 리포지토리에 우겨넣지 말자.
  - 임의의 레포지토리를 만들어도 된다.
  - 핵심 비즈니스 로직과 화면에 맞춘 쿼리는 분리하는 것이 유지보수에 좋다. 

#### Auditing

- BaseEntity
  - `@MappedSuperclass`
  - `@EntityListeners(AuditingEntityListener.class)`
- MainApplication
  - `@EnableJpaAuditing`

#### Web 확장 - 페이징과 정렬

- 파라미터로 `Pageable`을 받을 수 있다.
  - `Pageable` 인터페이스는 내부적으로 `PageRequest` 구현체를 생성함.
- JPA find 메소드에 `Pageable`을 아규먼트로 전달하면 Page 객체가 조회된다.

- `@PageableDefault(size=5, sort = "username")`
  - `Pageable`의 기본값을 정의할 수 있음.

### 스프링 데이터 JPA 분석

#### 스프링 데이터 JPA 구현체 분석

- Jpa 구현체에서는 기본적으로 모두 `@Transactional`이 걸려있다.
  - 서비스 계층에서 트랜젝션을 시작하지 않으면 레포지토리에서 트랜젝션 시작
  - 서비스 계층에서 트랜젝션을 시작하면 레포지토리는 해당 트랜잭션을 전파 받아서 사용.

- `@Transcational(readOnly = true)`
  - `readOnly = true ` 옵션을 사용하면
    - 변경감지 X
    - 플러쉬 X
  - 약간의 성능 향상을 얻을 수 있음
- 매우 중요
  - `save()` 메서드는 새로운 엔티티면 `em.persist()`를 호출하고, 아니라면 `em.merge()`를 호출한다.
  - **가급적 `merge`를 사용하면 안된다.**
    - merge는 DB에 select 쿼리가 발생한다. (성능 저하)
    - merge는 영속 상태에 엔티티가 영속상태를 벗어났을 때 다시 영속상태로 만들어줄 때에만 사용해라.
  - 데이터의 변경은 항상 변경감지를 통해 이루어져야 한다.

#### 새로운 엔티티를 구별하는 방법

- 식별자가 *객체* 일 때는 *null* 이면 새로운 엔티티
- 식별자가 *primitive 타입* 일 때는 *0* 이면 새로운 엔티티

#### 식별자를 커스텀하여 사용한다면?

- `@GeneratedValue`를 사용하지 않고 직접 식별자 값을 할당하면 이미 식별자 값이 있는 상태로 `save()`를 호출하게 된다.
-  따라서 JPA는 기존에 존재하던 엔티티로 판단하여 항상 `em.merge()`를 호출한다.
- 이를 해결하기 위해서는 엔티티에서 `Persistable` 인터페이스를 상속시켜서 `isNew()` 메서드를 구현한다.

