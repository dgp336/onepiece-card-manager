package ual.dra.optcg.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import ual.dra.optcg.entity.Card;

@RepositoryRestResource
public interface CardRepository extends CrudRepository<Card, Long> {
    
}
