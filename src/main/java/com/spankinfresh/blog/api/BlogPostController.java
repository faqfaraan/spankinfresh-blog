package com.spankinfresh.blog.api;

import com.spankinfresh.blog.data.BlogPostRepository;
import com.spankinfresh.blog.domain.BlogPost;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/articles")
public class BlogPostController {

    private final BlogPostRepository blogPostRepository;

    public BlogPostController(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    @PostMapping
    public ResponseEntity<BlogPost> createBlogEntry(
            @Valid @RequestBody BlogPost blogPost,
            UriComponentsBuilder uriComponentsBuilder) {
        blogPost.setDatePosted(new Date());
        BlogPost savedItem = blogPostRepository.save(blogPost);

        UriComponents uriComponents = uriComponentsBuilder.path("/api/articles/{id}")
                .buildAndExpand(savedItem.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", uriComponents.toUri().toString());
        return new ResponseEntity<>(savedItem, headers, HttpStatus.CREATED);
    }

    @GetMapping
    public Iterable<BlogPost> getAllItems() {
        return blogPostRepository.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Iterable<BlogPost>> getItemById(
            @PathVariable Long id) {
        Optional<BlogPost> blogPost = blogPostRepository.findById(id);
        if (blogPost.isPresent()) {
            return new ResponseEntity<>(
                    Collections.singletonList(blogPost.get()),HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("{id}")
    public ResponseEntity<BlogPost> updateBlogEntry(@PathVariable Long id,
                                                    @Valid @RequestBody BlogPost blogEntry) {

        if(blogEntry.getId() != id) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        if (blogPostRepository.existsById(id)) {
            blogPostRepository.save(blogEntry);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<BlogPost> deleteBlogEntryById(@PathVariable Long id) {
        Optional<BlogPost> blogEntry = blogPostRepository.findById(id);
        if (blogEntry.isPresent()) {
            blogPostRepository.delete(blogEntry.get());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/category")
    List<BlogPost> getAllBlogPostsByCategory(
            @RequestParam("categoryName") String categoryName){
        return blogPostRepository.findByCategoryOrderByDatePostedDesc(categoryName);
    }
}
