package com.iths.rickard;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/cats")
@Slf4j
public class AnimalController {


    private final AnimalsRepository repository;
    private final AnimalsModelAssembler assembler;

    @Autowired
    public AnimalController(AnimalsRepository repository, AnimalsModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("")
    public CollectionModel<EntityModel<Cat>> all() {
        log.debug("All Cats called");
        return assembler.toCollectionModel(repository.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<EntityModel<Cat>> one(@PathVariable long id) {
        return repository.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Cat> addNewCat(@RequestBody Cat cat) {
        log.info("Post create Cats " + cat);
        var p = repository.save(cat);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(AnimalController.class).slash(cat.getId()).toUri());
        headers.add("Location", "/api/persons/" + p.getId());
        return new ResponseEntity<>(cat, headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCat(@PathVariable Long id) {
        if (repository.existsById(id)) {
            log.info("Cat with id " + id + " deleted");
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Cat> replaceCat(@RequestBody Cat newCat, @PathVariable Long id) {
        return repository.findById(id)
                .map(cat -> {
                    cat.setName(newCat.getName());
                    repository.save(cat);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(linkTo(AnimalController.class).slash(cat.getId()).toUri());
                    return new ResponseEntity<>(cat, headers, HttpStatus.OK);
                })
                .orElseGet(() ->
                        new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<Cat> modifyCat(@RequestBody Cat newCat, @PathVariable Long id) {
        return repository.findById(id)
                .map(cat -> {
                    if (newCat.getName() != null)
                        cat.setName(newCat.getName());
                    repository.save(cat);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(linkTo(AnimalController.class).slash(cat.getId()).toUri());
                    return new ResponseEntity<>(cat, headers, HttpStatus.OK);
                })
                .orElseGet(() ->
                        new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
