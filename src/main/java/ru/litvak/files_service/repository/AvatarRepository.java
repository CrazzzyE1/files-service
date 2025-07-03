package ru.litvak.files_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.litvak.files_service.enumerated.SizeType;
import ru.litvak.files_service.model.entity.Avatar;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {

    Optional<Avatar> findByUserIdAndSize(UUID userId, SizeType size);
}
