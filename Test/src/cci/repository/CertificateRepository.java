package cci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cci.model.cert.Certificate;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

}


