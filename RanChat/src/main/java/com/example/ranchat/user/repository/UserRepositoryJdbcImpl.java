package com.example.ranchat.user.repository;

import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.ranchat.user.entity.User;

import lombok.RequiredArgsConstructor;

@Repository("UserRepositoryJdbcImpl")
@RequiredArgsConstructor
public class UserRepositoryJdbcImpl implements UserRepository {
	private final JdbcTemplate jdbcTemplate;
	private final RowMapper<User> userRowMapper = (rs, rowNum) -> User.builder()
		.username(rs.getString("username"))
		.password(rs.getString("password"))
		.role(rs.getString("role"))
		.build();

	@Override
	public Boolean existsByUsername(String username) {
		String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
		return count != null && count > 0;
	}

	@Override
	public Optional<User> findByUsername(String username) {
		String sql = "SELECT * FROM users WHERE username = ?";
		return Optional.ofNullable(jdbcTemplate.queryForObject(sql, userRowMapper, username));
	}

	@Override
	public User save(User user) {
		String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
		jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getRole());

		return findByUsername(user.getUsername()).get();
	}

	@Override
	public Optional<User> findById(Long userId) {
		String sql = "SELECT * FROM users WHERE id = ?";
		return Optional.ofNullable(jdbcTemplate.queryForObject(sql, userRowMapper, userId));
	}
}
