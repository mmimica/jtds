package net.sourceforge.jtds.jdbc.dns;

import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

/**
 * DNS Utilities
 *
 * {@link #findSrvRecords(String)} Find all the DNS Record for SRV records and
 * get an object you may use to perform Round-Robin based algorithm using
 * priorities and weights.
 */
public final class DNSUtilities {

    private DNSUtilities(){}

    /**
     * Find all SRV Record using DNS.
     *
     * @param dnsSrvRecordToFind
     *            the DNS record, for instance: _kerberos._udp.DOMAIN.COM
     * @return the collection of records with facilities to find the best
     *         candidate
     * @throws NamingException
     *             if DNS is not available
     */
    public static Set<DNSRecordSRV> findSrvRecords(final String dnsSrvRecordToFind) throws NamingException {
        Hashtable<Object, Object> env = new Hashtable<Object, Object>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        env.put("java.naming.provider.url", "dns:");
        DirContext ctx = new InitialDirContext(env);
        Attributes attrs = ctx.getAttributes(dnsSrvRecordToFind, new String[] { "SRV" });
        NamingEnumeration<? extends Attribute> allServers = attrs.getAll();
        TreeSet<DNSRecordSRV> records = new TreeSet<DNSRecordSRV>();
        while (allServers.hasMoreElements()) {
            Attribute a = allServers.nextElement();
            NamingEnumeration<?> srvRecord = a.getAll();
            while (srvRecord.hasMore()) {
                final String record = String.valueOf(srvRecord.nextElement());
                try {
                    DNSRecordSRV rec = DNSRecordSRV.parseFromDNSRecord(record);
                    if (rec != null) {
                        records.add(rec);
                    }
                } catch (IllegalArgumentException err) {
                    net.sourceforge.jtds.util.Logger.println(String.format("Failed to parse SRV DNS Record: '%s'", record));
                }
            }
        }
        return records;
    }

}
