package com.spankinfresh.blog.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spankinfresh.blog.data.BlogPostRepository;
import com.spankinfresh.blog.domain.BlogPost;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BlogPostControllerMockTests {
    @MockBean
    private BlogPostRepository mockRepository;

    private static final String RESOURCE_URI = "/api/articles";
    private final ObjectMapper mapper = new ObjectMapper();
    private static final BlogPost testPosting = new BlogPost(0L, "category", null, "title", "content");

    private static final BlogPost putTestPosting = new BlogPost(100L, "category", null, "title", "content");

    @Test
    @DisplayName("T01 - POST accepts and returns blog post representation")
    public void postCreatesNewBlogEntry_Test(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.save(any(BlogPost.class))).thenReturn(testPosting);
        MvcResult result = mockMvc.perform(post(RESOURCE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(testPosting)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(testPosting.getId()))
            .andExpect(jsonPath("$.title").value(testPosting.getTitle()))
            .andExpect(jsonPath("$.category").value(testPosting.getCategory()))
            .andExpect(jsonPath("$.content").value(testPosting.getContent()))
            .andReturn();
        MockHttpServletResponse mockResponse = result.getResponse();
        assertEquals(String.format(
                "http://localhost/api/articles/%d", testPosting.getId()),
                mockResponse.getHeader("Location"));
    }

    @Test
    @DisplayName("T02 - POST automatically adds the datePosted")
    public void test02(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(post(RESOURCE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(testPosting)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.datePosted").isNotEmpty());
    }

    @Test
    @DisplayName("T03 - POST with missing values returns bad request")
    public void test03(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(post(RESOURCE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(new BlogPost())))
            .andExpect(status().isBadRequest());
        verify(mockRepository, never()).save(any(BlogPost.class));
    }

    @Test
    @DisplayName("T04 - Field errors present for each invalid property")
    public void test04(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(post(RESOURCE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new BlogPost())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.category")
                        .value("must not be null"))
                .andExpect(jsonPath("$.fieldErrors.title")
                        .value("must not be null"))
                .andExpect(jsonPath("$.fieldErrors.content")
                        .value("must not be null"));
        mockMvc.perform(post(RESOURCE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new BlogPost(0L, "",
                        null, "", ""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.category").value(
                        "Please enter a category name of up to 200 characters"))
                .andExpect(jsonPath("$.fieldErrors.title").value(
                        "Please enter a title up to 200 characters in length"))
                .andExpect(
                        jsonPath("$.fieldErrors.content")
                                .value("Content is required"));
        verify(mockRepository, never()).save(any(BlogPost.class));
    }

    private Collection<BlogPost> createMockBlogEntryList(BlogPost... itemArgs) {
        HashMap<Long, BlogPost> blogEntries = new HashMap<>();
        for (BlogPost blogPost : itemArgs) {
            blogEntries.put(blogPost.getId(), blogPost);
        }
        return blogEntries.values();
    }

    @Test
    @DisplayName("T05 - GET All works for empty list")
    public void test05(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.findAll()).
                thenReturn(createMockBlogEntryList());
        mockMvc.perform(get(RESOURCE_URI)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
        verify(mockRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("T06 - GET All works for single item list")
    public void test06(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.findAll())
                .thenReturn(createMockBlogEntryList(testPosting));
        mockMvc.perform(get(RESOURCE_URI)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$.[0].id").value(testPosting.getId()))
                .andExpect(jsonPath(
                        "$.[0].title").value(testPosting.getTitle()))
                .andExpect(jsonPath(
                        "$.[0].category").value(testPosting.getCategory()))
                .andExpect(jsonPath(
                        "$.[0].content").value(testPosting.getContent()));
        verify(mockRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("T07 GET with valid ID returns expected data")
    public void test07(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.findById(anyLong()))
                .thenReturn(Optional.of(testPosting));
        mockMvc.perform(get(RESOURCE_URI + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$.[0].id").value(testPosting.getId()))
                .andExpect(jsonPath(
                        "$.[0].title").value(testPosting.getTitle()))
                .andExpect(
                        jsonPath("$.[0].category").value(testPosting.getCategory()))
                .andExpect(
                        jsonPath("$.[0].content").value(testPosting.getContent()));
        verify(mockRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("T08 GET by invalid ID returns 404 not found")
    public void test08(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        mockMvc.perform(get(RESOURCE_URI + "/1"))
                .andExpect(status().isNotFound());
        verify(mockRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("T08a GET by non-numeric ID returns 400 bad request")
    public void test08a(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(get(RESOURCE_URI + "/ABC"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("T09 - PUT works as expected")
    public void test09(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.existsById(anyLong())).thenReturn(true);
        mockMvc.perform(put(RESOURCE_URI + "/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(putTestPosting)))
                .andExpect(status().isNoContent());
        verify(mockRepository,
                times(1)).save(any(BlogPost.class));
    }

    @Test
    @DisplayName("T10 - PUT with invalid ID works as expected")
    public void test10(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.existsById(anyLong())).thenReturn(false);
        mockMvc.perform(put(RESOURCE_URI + "/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(putTestPosting)))
                .andExpect(status().isNotFound());
        verify(mockRepository, never()).save(any(BlogPost.class));
    }

    @Test
    @DisplayName("T10a - PUT with non-numeric ID works as expected")
    public void test10a(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(put(RESOURCE_URI + "/ABC")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(putTestPosting)))
                .andExpect(status().isBadRequest());
        verify(mockRepository, never()).save(any(BlogPost.class));
    }

    @Test
    @DisplayName("T11 - PUT with validation errors works as expected")
    public void test11(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(put(RESOURCE_URI + "/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new BlogPost())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.category")
                        .value("must not be null"))
                .andExpect(jsonPath("$.fieldErrors.title")
                        .value("must not be null"))
                .andExpect(jsonPath("$.fieldErrors.content")
                        .value("must not be null"));
        verify(mockRepository, never()).save(any(BlogPost.class));
    }

    @Test
    @DisplayName("T11a - PUT returns conflict on ID mismatch")
    public void test11a(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.existsById(anyLong())).thenReturn(true);
        mockMvc.perform(put(RESOURCE_URI + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(putTestPosting)))
                .andExpect(status().isConflict());
        verify(mockRepository, never()).save(any(BlogPost.class));
    }

    @Test
    @DisplayName("T12 - DELETE existing item returns no content")
    public void test12 (@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.findById(1L))
                .thenReturn(Optional.of(testPosting));
        mockMvc.perform(delete(RESOURCE_URI + "/1"))
                .andExpect(status().isNoContent());
        verify(mockRepository,
                times(1)).delete(any(BlogPost.class));
    }

    @Test
    @DisplayName("T13 - DELETE nonexistent item returns not found")
    public void test13 (@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        mockMvc.perform(delete(RESOURCE_URI + "/1"))
                .andExpect(status().isNotFound());
        verify(mockRepository, never()).delete(any(BlogPost.class));
    }

    @Test
    @DisplayName("T14 - DELETE with non-numeric ID returns bad request")
    public void test14(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(delete(RESOURCE_URI + "/ABC")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(putTestPosting)))
                .andExpect(status().isBadRequest());
        verify(mockRepository, never()).save(any(BlogPost.class));
    }
}
