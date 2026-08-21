package ru.test.sftpclient.model;

import java.util.Objects;

public class DomainIp {

    private String domain;
    private String ip;

    public DomainIp(String domain, String ip) {
        this.domain = domain;
        this.ip = ip;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return domain + " - " + ip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof DomainIp)) {
            return false;
        }

        DomainIp domainIp = (DomainIp) o;

        return Objects.equals(domain, domainIp.domain) && Objects.equals(ip, domainIp.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domain, ip);
    }
}
