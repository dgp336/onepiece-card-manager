package ual.dra.optcg.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import ual.dra.optcg.entity.Product;

import java.util.List;

@RepositoryRestResource(path = "products")
public interface ProductRepository extends CrudRepository<Product, Integer> {

    public List<Product> findByCardName(String name);

    public List<Product> findByCardCost(String cost);

    public List<Product> findByCardFeature(String feature);

}
