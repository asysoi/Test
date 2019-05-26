package cci.test;

import cci.model.cert.Certificate;
import cci.service.CertificateService;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

public class JPATest {

    @Resource
    @Autowired
    private CertificateService certService;
    
    public void testGetCertificates() throws Exception {
        List<Certificate> certs = certService.getAll();
        System.out.println(certs.size());
    }
}
