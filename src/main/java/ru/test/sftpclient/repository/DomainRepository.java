package ru.test.sftpclient.repository;
import ru.test.sftpclient.model.DomainIp;
import java.util.List;
import java.io.IOException;

public interface DomainRepository {

    List<DomainIp> findAll() throws IOException;
    void save(List<DomainIp> domainIps) throws IOException;
}
