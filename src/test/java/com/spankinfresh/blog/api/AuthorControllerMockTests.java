package com.spankinfresh.blog.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spankinfresh.blog.data.AuthorRepository;
import com.spankinfresh.blog.domain.Author;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorControllerMockTests {

    @MockBean
    private AuthorRepository mockRepository;

    private static final Author testPosting = new Author(0L, "first", "last", "foobar@mail.com");
    private static final Author putTestPosting = new Author(100L, "ufirst", "ulast", "ufoobar@mail.com");
    private final ObjectMapper mapper = new ObjectMapper();

    private static final String RESOURCE_URI = "/api/authors";

    @Test
    @DisplayName("ST01: POST without JWT is forbidden")
    public void sTest01(@Autowired MockMvc mockMvc)
            throws Exception {
        when(mockRepository.save(any(Author.class))).thenReturn(testPosting);
        mockMvc.perform(post(RESOURCE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(testPosting)))
                .andExpect(status().isForbidden());
        verify(mockRepository, never()).save(any(Author.class));
    }

    @Test
    @DisplayName("ST02: PUT without JWT is forbidden")
    public void sTest02(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.existsById(anyLong())).thenReturn(true);
        mockMvc.perform(put(RESOURCE_URI + "/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(putTestPosting)))
                .andExpect(status().isForbidden());
        verify(mockRepository, never()).save(any(Author.class));
    }

    @Test
    @DisplayName("ST03: DELETE without JWT is forbidden")
    public void sTest03(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.findById(1L))
                .thenReturn(Optional.of(testPosting));
        mockMvc.perform(delete(RESOURCE_URI + "/1"))
                .andExpect(status().isForbidden());
        verify(mockRepository, never()).delete(any(Author.class));
    }
}
