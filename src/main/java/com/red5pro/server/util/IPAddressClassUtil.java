package com.red5pro.server.util;

/**
 * IP address utility.
 *
 * @author Paul Gregoire
 */
public class IPAddressClassUtil {

    public static boolean isPrivateIPv4(String ipAddress) {
        try {
            String[] ipAddressArray = ipAddress.split("\\.");
            int[] ipParts = new int[ipAddressArray.length];
            for (int i = 0; i < ipAddressArray.length; i++) {
                ipParts[i] = Integer.parseInt(ipAddressArray[i].trim());
            }
            switch (ipParts[0]) {
                case 10:
                case 127:
                    return true;
                case 172:
                    return (ipParts[1] >= 16) && (ipParts[1] < 32);
                case 192:
                    return (ipParts[1] == 168);
                case 169:
                    return (ipParts[1] == 254);
            }
        } catch (Exception ex) {
        }
        return false;
    }

    public static boolean isPrivateIPv6(String ipAddress) {
        boolean isPrivateIPv6 = false;
        String[] ipParts = ipAddress.trim().split(":");
        if (ipParts.length > 0) {
            String firstBlock = ipParts[0];
            String prefix = firstBlock.substring(0, 2);
            if (firstBlock.equalsIgnoreCase("fe80") || firstBlock.equalsIgnoreCase("100") || ((prefix.equalsIgnoreCase("fc") && firstBlock.length() >= 4)) || ((prefix.equalsIgnoreCase("fd") && firstBlock.length() >= 4))) {
                isPrivateIPv6 = true;
            }
        }
        return isPrivateIPv6;
    }

}
