package edu.unc.cem.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;

public class WriteVersionInfo {
    public static void writeVersionInfo(File out, String version) throws IOException {
        String timestamp = new Date(new java.util.Date().getTime()).toString(); //in the yyyy-mm-dd format
        String ls = System.getProperty("line.separator");

        String versionInfoClass = "package edu.unc.cem.util;" + ls + ls
                + "/**" + ls
                + " * NOTE: Auto-generated file. Please don't try to change." + ls
                + " * @version $Revision$ $Date$" + ls
                + " */" + ls
                + "public class VersionInfo {" + ls
                + "	public static final String version = \"" + version + "\";" + ls
                + "	public static final String date = \"" + timestamp + "\";" + ls + ls
                + "	public static String getVersion() {" + ls
                + "		return version;" + ls
                + "	}" + ls + ls
                + "	public static String getDate() {" + ls
                + "		return date;" + ls
                + "	}" + ls + ls
                + "}";

        try (FileWriter writer = new FileWriter(out)) {
            writer.write(versionInfoClass);
        }
    }
}
