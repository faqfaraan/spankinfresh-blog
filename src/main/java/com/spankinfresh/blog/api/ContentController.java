package com.spankinfresh.blog.api;

import com.spankinfresh.blog.data.BlogPostJdbcTemplateRepository;
import com.spankinfresh.blog.domain.BlogPost;
import com.spankinfresh.blog.domain.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ContentController {

    private final BlogPostJdbcTemplateRepository blogPostJdbcTemplateRepository;

    public ContentController(BlogPostJdbcTemplateRepository blogPostJdbcTemplateRepository) {
        this.blogPostJdbcTemplateRepository = blogPostJdbcTemplateRepository;
    }

    @GetMapping("/api/summary/articles")
    public List<BlogPost> getAll() {
        return blogPostJdbcTemplateRepository.getAllBlogPostingsOmittingContent();
    }

    @GetMapping("/api/categories")
    List <Category> getAllCategories(){
        return blogPostJdbcTemplateRepository.getCategoryList();
    }
}
