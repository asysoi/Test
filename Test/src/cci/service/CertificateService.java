package cci.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import cci.model.cert.Certificate;
import cci.model.cert.Product;
import cci.repository.CertificateRepository;
import cci.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class CertificateService implements ICertificateService {

	public CertificateService() {
	    System.out.println("CertificateService inited = " + this.getClass());
	    
	}
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CertificateRepository certRepository;
	
	@Override
	public Certificate getByID(long id) {
		Certificate cert = certRepository.findById(id).get();
		List<Product> products = productRepository.getProductsById(cert.getCert_id());
		cert.setProducts(products);
		return cert;
	}

	@Override
	public List<Certificate> getAll() {
		return certRepository.findAll();
	}

}
