package study.datajpa.enitity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> { //Persistable 상속.

	@Id	//@GeneratedValue를 사용하지 않고 직접 식별자 값을 할당하면,
	// JPA는 식별자 값이 존재하기 때문에 이미 존재하는 엔티티로 판단하여 em.persist()가 아닌 em.merge()를 호출함.
	private String id;

	@CreatedDate
	private LocalDateTime createdDate;

	public Item(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	//isNew() 메소드를 직접 구현해서 JPA에게 기존에 해당 엔티티가 새로운 엔티티임을 알려줄 수 있음.
	@Override
	public boolean isNew() {
		return createdDate == null;
	}
}
