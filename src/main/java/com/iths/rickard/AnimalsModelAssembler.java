package com.iths.rickard;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AnimalsModelAssembler implements RepresentationModelAssembler<Cat, EntityModel<Cat>> {

    @Override
    public EntityModel<Cat> toModel(Cat cat) {
        return new EntityModel<>(cat,
                linkTo(methodOn(AnimalController.class).one(cat.getId())).withSelfRel(),
                linkTo(methodOn(AnimalController.class).all()).withRel("cats"));
    }

    @Override
    public CollectionModel<EntityModel<Cat>> toCollectionModel(Iterable<? extends Cat> entities) {
        var collection = StreamSupport.stream(entities.spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.toList());

        return new CollectionModel<>(collection,
                linkTo(methodOn(AnimalController.class).all()).withSelfRel());
    }
}
