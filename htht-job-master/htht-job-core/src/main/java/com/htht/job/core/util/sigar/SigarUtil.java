package com.htht.job.core.util.sigar;

/**
 * Created by zzj on 2018/1/31.
 */

import org.hyperic.sigar.Sigar;

import java.io.File;

public class SigarUtil {
    private SigarUtil() {
        try {
            //String file = Resources.getResource("sigar/.sigar_shellrc").getFile();
            String file = this.getClass().getClassLoader().getResource("sigar/.sigar_shellrc").getFile();
            File classPath = new File(file).getParentFile();
            //System.getProperty("java.library.path");
        /*    if (OsCheck.getOperatingSystemType() == OsCheck.OSType.Windows) {
                path += ";" + classPath.getCanonicalPath();
            } else {
                path += ":" + classPath.getCanonicalPath();
            }*/
            //System.out.println(classPath.getCanonicalPath());
            System.setProperty("java.library.path", classPath.getCanonicalPath());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static final Sigar getInstance() {
        return SigarUtilHolder.Sigar;
    }

    public static final SigarUtil getSigarUtilInstance() {
        return SigarUtilHolder.INSTANCE;
    }

    private static class SigarUtilHolder {
        private static final SigarUtil INSTANCE = new SigarUtil();
        private static final Sigar Sigar = new Sigar();
    }

}