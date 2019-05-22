package cci.service;

import java.util.List;
import cci.model.cert.Certificate;

public interface ICertificateService {
      public Certificate getByID (long id);
      public List<Certificate> getAll();
}
