package com.iths.rickard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.Optional;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnimalController.class)
@Import({AnimalsModelAssembler.class})
@WebAppConfiguration
@ContextConfiguration(classes = MyWebConfig.class)
class ApplicationTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AnimalsRepository repository;

    @BeforeEach
    void setUp() {
        when(repository.findAll()).thenReturn(List.of(new Cat(1L, "pelle"), new Cat(2L, "mozart")));
        when(repository.findById(1L)).thenReturn(Optional.of(new Cat(1L, "pelle")));
        when(repository.save(any(Cat.class))).thenAnswer(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            var p = (Cat) args[0];
            return new Cat(1L, p.getName());
        });

    }

    @Test
    @DisplayName("Get all cats")
    void getCatsAndShowFirstTwo() throws Exception {
        mockMvc.perform(
                get("/api/cats").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.catList[0]._links.self.href", is("http://localhost/api/cats/1")))
                .andExpect(jsonPath("_embedded.catList[0]._links.cats.href", is("http://localhost/api/cats/")))
                .andExpect(jsonPath("_embedded.catList[0].id", is(1)))
                .andExpect(jsonPath("_embedded.catList[0].name", is("pelle")))
                .andExpect(jsonPath("_embedded.catList[1]._links.self.href", is("http://localhost/api/cats/2")))
                .andExpect(jsonPath("_embedded.catList[1]._links.cats.href", is("http://localhost/api/cats/")))
                .andExpect(jsonPath("_embedded.catList[1].id", is(2)))
                .andExpect(jsonPath("_embedded.catList[1].name", is("mozart")));
    }

    @Test
    @DisplayName("Calls Get method with url /api/cats/1")
    void getOneCat() throws Exception {
        mockMvc.perform(
                get("/api/cats/1").accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(1)))
                .andExpect(jsonPath("name", is("pelle")))
                .andExpect(jsonPath("_links.self.href", is("http://localhost/api/cats/1")))
                .andExpect(jsonPath("_links.cats.href", is("http://localhost/api/cats/")));
    }


    @Test
    @DisplayName("Calls Get method with invalid id")
    void getOnePersonWithInValidIdThree() throws Exception {
        mockMvc.perform(
                get("/api/cats/3").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Adds a cat")
    void addNewCatWithPost() throws Exception {
        mockMvc.perform(
                post("/api/cats/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":3,\"name\":\"christoffer\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id", is(3)))
                .andExpect(jsonPath("name", is("christoffer")));
    }

    @Test
    public void deleteByIDNotFound() throws Exception {
        mockMvc.perform(delete("/api/cats/{id}", 3L))
                .andExpect(status().isNotFound());
    }


    @Test
    void replaceCat() throws Exception {
        mockMvc.perform(
                put("/api/cats/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":3},{\"name\":\"Rickard\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void editCatInfo() throws Exception {
        mockMvc.perform(
                patch("/api/cats/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":5,\"name\":\"changedName\"}"))
                .andExpect(status().isOk());

    }
}