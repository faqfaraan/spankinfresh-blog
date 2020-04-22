package com.spankinfresh.blog.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spankinfresh.blog.domain.BlogPost;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BlogPostControllerTests {
    private static final String RESOURCE_URI = "/api/articles";
    private final ObjectMapper mapper = new ObjectMapper();
    private static final BlogPost testPosting = new BlogPost(0L, null, "category", null, "title", "content");

    @Test
    @DisplayName("T01 - POST accepts and returns blog post representation")
    public void postCreatesNewBlogEntry_Test(@Autowired MockMvc mockMvc) throws Exception {
        MvcResult result = mockMvc.perform(post(RESOURCE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(testPosting)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value(testPosting.getTitle()))
            .andExpect(jsonPath("$.category").value(testPosting.getCategory()))
            .andExpect(jsonPath("$.content").value(testPosting.getContent()))
            .andReturn();
        MockHttpServletResponse mockResponse = result.getResponse();
        assertEquals("http://localhost/api/articles/1",
                mockResponse.getHeader("Location"));

        result = mockMvc.perform(post(RESOURCE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(testPosting)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.title").value(testPosting.getTitle()))
                .andExpect(jsonPath("$.category").value(testPosting.getCategory()))
                .andExpect(jsonPath("$.content").value(testPosting.getContent()))
                .andReturn();
        mockResponse = result.getResponse();
        assertEquals("http://localhost/api/articles/2",
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

}
