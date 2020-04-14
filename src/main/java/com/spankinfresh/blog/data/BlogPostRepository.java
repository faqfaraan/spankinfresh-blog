package com.spankinfresh.blog.data;

import com.spankinfresh.blog.domain.BlogPost;
import org.springframework.data.repository.CrudRepository;

public interface BlogPostRepository extends CrudRepository<BlogPost, Long> {

}
