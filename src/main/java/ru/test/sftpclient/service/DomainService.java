package ru.test.sftpclient.service;

import ru.test.sftpclient.model.DomainIp;
import ru.test.sftpclient.repository.DomainRepository;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class DomainService {

    private final DomainRepository domainRepository;

    public DomainService(DomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    public List<DomainIp> getAll() throws IOException {
        List<DomainIp> domainIps = domainRepository.findAll();
        domainIps.sort(Comparator.comparing(DomainIp::getDomain));

        return domainIps;
    }

    public Optional<String> findIpByDomain(String domain) throws IOException {
        List<DomainIp> domainIps = domainRepository.findAll();

        return domainIps.stream()
                .filter(pair -> pair.getDomain().equals(domain))
                .map(DomainIp::getIp)
                .findFirst();
    }

    public Optional<String> findDomainByIp(String ip) throws IOException {
        List<DomainIp> domainIps = domainRepository.findAll();

        return domainIps.stream()
                .filter(pair -> pair.getIp().equals(ip))
                .map(DomainIp::getDomain)
                .findFirst();
    }

    public void add(String ip, String domain) throws IOException {
        validateIpv4(ip);
        validateDomain(domain);

        List<DomainIp> domainIps = domainRepository.findAll();

        if (domainIps.stream().
            anyMatch(pair -> pair.getIp().equals(ip))) {
            throw new IllegalArgumentException("Ip already exists");
        }

        if (domainIps.stream().
                anyMatch(pair -> pair.getDomain().equals(domain))) {
            throw new IllegalArgumentException("Domain already exists");
        }

        domainIps.add(new DomainIp(domain, ip));
        domainRepository.save(domainIps);
    }

    public boolean deleteByIp(String ip) throws IOException {
        List<DomainIp> domainIps = domainRepository.findAll();
        boolean deleted = domainIps.removeIf(pair -> pair.getIp().equals(ip));
        if (deleted) {
            domainRepository.save(domainIps);
        }

        return deleted;
    }

    public boolean deleteByDomain(String domain) throws IOException {
        List<DomainIp> domainIps = domainRepository.findAll();
        boolean deleted = domainIps.removeIf(pair -> pair.getDomain().equals(domain));
        if (deleted) {
            domainRepository.save(domainIps);
        }

        return deleted;
    }


    private void validateIpv4(String ip) {
        if (ip == null || ip.isEmpty()) {
            throw new IllegalArgumentException("ip is null or empty");
        }

        String[] ipParts = ip.split("\\.", -1);

        if (ipParts.length != 4) {
            throw new IllegalArgumentException("invalid ip format");
        }

        for(String ipPart : ipParts) {
            if (ipPart.isEmpty()) {
                throw new IllegalArgumentException("invalid ip format");
            }

            try {
                if (Integer.parseInt(ipPart) > 255 || Integer.parseInt(ipPart) < 0) {
                    throw new IllegalArgumentException("invalid ip format");
                }
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("invalid ip format");
            }
        }
    }

    private void validateDomain(String domain) {
        if (domain == null || domain.isEmpty()) {
            throw new IllegalArgumentException("domain is null or empty");
        }
    }
}
