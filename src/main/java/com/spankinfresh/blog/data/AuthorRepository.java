package com.spankinfresh.blog.data;

import com.spankinfresh.blog.domain.Author;
import org.springframework.data.repository.CrudRepository;

public interface AuthorRepository extends CrudRepository<Author, Long> {
}
