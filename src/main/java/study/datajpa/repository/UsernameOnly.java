package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

//인터페이스 기반의 Projections
public interface UsernameOnly {

	// 아래는 Open-Projections
	// @Value("#{target.username + ' ' + target.age})")
	String getUsername();
}
