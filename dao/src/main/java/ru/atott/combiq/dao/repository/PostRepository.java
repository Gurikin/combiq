package ru.atott.combiq.dao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import ru.atott.combiq.dao.entity.PostEntity;

@Component
public interface PostRepository extends PagingAndSortingRepository<PostEntity, String> {

    Page<PostEntity> findByPublished(Pageable pageable, boolean published);
}
