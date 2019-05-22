package cci.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import cci.config.AppConfig;
import cci.model.cert.Certificate;
import cci.service.CertificateService;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@DirtiesContext
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@WebAppConfiguration

public class JPATest {

    @Resource
    private EntityManagerFactory emf;
    protected EntityManager em;

    @Resource
    private CertificateService certService;
    
    @Before
    public void setUp() throws Exception {
        em = emf.createEntityManager();
    }

    @Test
    public void testGetCertificates() throws Exception {
        List<Certificate> certs = certService.getAll();
        System.out.println(certs.size());
    }
}
