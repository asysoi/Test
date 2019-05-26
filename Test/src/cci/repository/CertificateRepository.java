package cci.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cci.model.cert.Certificate;

@Repository
@Transactional
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

}


