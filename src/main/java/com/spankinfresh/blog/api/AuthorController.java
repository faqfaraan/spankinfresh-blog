package com.spankinfresh.blog.api;

import com.spankinfresh.blog.data.AuthorRepository;
import com.spankinfresh.blog.domain.Author;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorRepository authorRepository;

    public AuthorController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @PostMapping
    public ResponseEntity<Author> createAuthor(
            @Valid @RequestBody Author author,
            UriComponentsBuilder uriComponentsBuilder) {
        Author savedItem = authorRepository.save(author);

        UriComponents uriComponents = uriComponentsBuilder.path("/api/authors/{id}")
                .buildAndExpand(savedItem.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", uriComponents.toUri().toString());
        return new ResponseEntity<>(savedItem, headers, HttpStatus.CREATED);
    }

    @GetMapping
    public Iterable<Author> getAllItems() {
        return authorRepository.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Iterable<Author>> getItemById(
            @PathVariable Long id) {
        Optional<Author> author = authorRepository.findById(id);
        if (author.isPresent()) {
            return new ResponseEntity<>(
                    Collections.singletonList(author.get()),HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("{id}")
    public ResponseEntity<Author> updateAuthorEntry(@PathVariable Long id,
                                                    @Valid @RequestBody Author author) {

        if(author.getId() != id) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        if (authorRepository.existsById(id)) {
            authorRepository.save(author);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Author> deleteAuthorEntryById(@PathVariable Long id) {
        Optional<Author> author = authorRepository.findById(id);
        if (author.isPresent()) {
            authorRepository.delete(author.get());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

//    @GetMapping("/category")
//    List<BlogPost> getAllBlogPostsByCategory(
//            @RequestParam("categoryName") String categoryName){
//        return authorRepository.findByCategoryOrderByDatePostedDesc(categoryName);
//    }

}
