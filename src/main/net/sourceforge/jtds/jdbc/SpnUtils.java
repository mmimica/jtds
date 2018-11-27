package net.sourceforge.jtds.jdbc;

import java.util.Locale;

import javax.naming.NamingException;

import net.sourceforge.jtds.jdbc.dns.DNSKerberosLocator;

public class SpnUtils {

    private SpnUtils() {}

    public static String findRealmInHostName(String hostname) {
        try {
            return findRealmFromHostname(hostname);
        } catch (NamingException err) {
            return null;
        }
    }

    private static String findRealmFromHostname(String hostname) throws NamingException {
        if (hostname == null) {
            return null;
        }
        int index = 0;
        // Starting from the left, we try to find a valid Kerberos Realm, each time
        // incrementing to next DNS label.
        while (index != -1 && index < hostname.length() - 2) {
            String realm = hostname.substring(index);
            if (DNSKerberosLocator.isRealmValid(realm)) {
                return realm.toUpperCase(Locale.ENGLISH);
            }
            index = hostname.indexOf(".", index + 1);
            if (index != -1) {
                index++;
            }
        }
        return null;
    }
}
