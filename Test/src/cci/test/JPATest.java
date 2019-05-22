package cci.test;


import cci.config.AppConfig;
import cci.model.cert.Certificate;
import cci.service.CertificateService;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;


public class JPATest {

    @Resource
    private EntityManagerFactory emf;
    protected EntityManager em;

    @Resource
    private CertificateService certService;
    
    public void setUp() throws Exception {
        em = emf.createEntityManager();
    }
    
    public void testGetCertificates() throws Exception {
        List<Certificate> certs = certService.getAll();
        System.out.println(certs.size());
    }
}
