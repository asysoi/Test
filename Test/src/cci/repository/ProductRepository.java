package cci.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cci.model.cert.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	
	@Query("FROM c_product where cert_id = :cert_id order by product_id")
	List<Product> getProductsById(@Param("cert_id") Long cert_id);
}
