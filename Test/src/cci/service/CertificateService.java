package cci.service;

import java.util.List;
import org.springframework.stereotype.Service;
import cci.model.cert.Certificate;
import cci.model.cert.Product;
import cci.repository.CertificateRepository;
import cci.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CertificateService implements ICertificateService {

	@Autowired
	private CertificateRepository certRepository;
	@Autowired
	private ProductRepository productRepository;
	
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
