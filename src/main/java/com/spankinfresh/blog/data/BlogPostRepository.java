package com.spankinfresh.blog.data;

import com.spankinfresh.blog.domain.BlogPost;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BlogPostRepository extends CrudRepository<BlogPost, Long> {
    List<BlogPost> findByCategoryOrderByDatePostedDesc(String category);
}
