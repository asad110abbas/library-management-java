package com.portfolio.library;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LibraryApiTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    private long create(String url, String body) throws Exception {
        String res = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        JsonNode node = json.readTree(res);
        return node.get("id").asLong();
    }

    @Test
    void borrowAndReturnFlow() throws Exception {
        long book = create("/api/books", """
            {"isbn":"9780134685991","title":"Effective Java","author":"Joshua Bloch","publishedYear":2018,"copies":1}""");
        long member = create("/api/members", """
            {"name":"Ali","email":"ali@example.com"}""");
        long loan = create("/api/loans", "{\"memberId\":%d,\"bookId\":%d}".formatted(member, book));

        mvc.perform(get("/api/books?q=effective")).andExpect(jsonPath("$[0].availableCopies").value(0));

        mvc.perform(post("/api/loans").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"memberId\":%d,\"bookId\":%d}".formatted(member, book)))
                .andExpect(status().isConflict());

        mvc.perform(post("/api/loans/" + loan + "/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fine").value(0));
        mvc.perform(get("/api/members/" + member + "/loans")).andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void validationErrorsAreReported() throws Exception {
        mvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isbn\":\"123\",\"title\":\"\",\"author\":\"X\",\"publishedYear\":2000,\"copies\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.isbn").exists())
                .andExpect(jsonPath("$.errors.copies").exists());
    }

    @Test
    void unknownMemberIs404() throws Exception {
        mvc.perform(get("/api/members/999/loans")).andExpect(status().isNotFound());
    }
}
