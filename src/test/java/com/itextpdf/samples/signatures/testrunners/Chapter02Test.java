/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    For more information, please contact iText Software at this address:
    sales@itextpdf.com
 */
package com.itextpdf.samples.signatures.testrunners;

import com.itextpdf.io.font.FontCache;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.kernel.Version;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.licensekey.LicenseKey;
import com.itextpdf.samples.SignatureTest;
import com.itextpdf.test.RunnerSearchConfig;
import com.itextpdf.test.WrappedSamplesRunner;

import org.junit.Test;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Collection;

public class Chapter02Test extends WrappedSamplesRunner {
    private static final Map<String, List<Rectangle>> classAreaMap;

    static {
        classAreaMap = new HashMap<>();
        classAreaMap.put("com.itextpdf.samples.signatures.chapter02.C2_01_SignHelloWorld",
                new ArrayList<Rectangle>(Arrays.asList(new Rectangle(36, 648, 200, 100))));
        classAreaMap.put("com.itextpdf.samples.signatures.chapter02.C2_02_SignHelloWorldWithTempFile",
                new ArrayList<Rectangle>(Arrays.asList(new Rectangle(36, 648, 200, 100))));
        classAreaMap.put("com.itextpdf.samples.signatures.chapter02.C2_03_SignEmptyField",
                new ArrayList<Rectangle>(Arrays.asList(new Rectangle(46, 472, 287, 255))));
        classAreaMap.put("com.itextpdf.samples.signatures.chapter02.C2_04_CreateEmptyField",
                new ArrayList<Rectangle>(Arrays.asList(new Rectangle(72, 632, 200, 100))));
        classAreaMap.put("com.itextpdf.samples.signatures.chapter02.C2_05_CustomAppearance",
                new ArrayList<Rectangle>(Arrays.asList(new Rectangle(46, 472, 287, 255))));
        classAreaMap.put("com.itextpdf.samples.signatures.chapter02.C2_06_SignatureAppearance",
                new ArrayList<Rectangle>(Arrays.asList(new Rectangle(46, 472, 287, 255))));
        classAreaMap.put("com.itextpdf.samples.signatures.chapter02.C2_07_SignatureAppearances",
                new ArrayList<Rectangle>(Arrays.asList(new Rectangle(46, 472, 287, 255))));
        classAreaMap.put("com.itextpdf.samples.signatures.chapter02.C2_08_SignatureMetadata",
                new ArrayList<Rectangle>(Arrays.asList(new Rectangle(46, 472, 287, 255))));
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        RunnerSearchConfig searchConfig = new RunnerSearchConfig();
        searchConfig.addPackageToRunnerSearchPath("com.itextpdf.samples.signatures.chapter02");

        // Samples are run by separate samples runner
        searchConfig.ignorePackageOrClass("com.itextpdf.samples.signatures.chapter02.C2_12_LockFields");
        searchConfig.ignorePackageOrClass("com.itextpdf.samples.signatures.chapter02.C2_10_SequentialSignatures");
        searchConfig.ignorePackageOrClass("com.itextpdf.samples.signatures.chapter02.C2_09_SignatureTypes");
        searchConfig.ignorePackageOrClass("com.itextpdf.samples.signatures.chapter02.C2_11_SignatureWorkflow");

        return generateTestsList(searchConfig);
    }

    @Test(timeout = 60000)
    public void test() throws Exception {
        LicenseKey.loadLicenseFile(System.getenv("ITEXT7_LICENSEKEY") + "/all-products.xml");
        FontCache.clearSavedFonts();
        FontProgramFactory.clearRegisteredFonts();

        runSamples();
        unloadLicense();
    }

    @Override
    protected void comparePdf(String outPath, String dest, String cmp) {
        List<Rectangle> ignoredAreas = classAreaMap.get(sampleClass.getName());
        Map<Integer, List<Rectangle>> ignoredAreasMap = new HashMap<>();
        ignoredAreasMap.put(1, ignoredAreas);

        String[] resultFiles = getResultFiles(sampleClass);
        for (int i = 0; i < resultFiles.length; i++) {
            String currentDest = dest + resultFiles[i];
            String currentCmp = cmp + resultFiles[i];
            try {
                addError(new SignatureTest().checkForErrors(currentDest, currentCmp, outPath, ignoredAreasMap));
            } catch (InterruptedException | IOException | GeneralSecurityException exc) {
                addError("Exception has been thrown: " + exc.getMessage());
            }
        }
    }

    @Override
    protected String getOutPath(String dest) {
        return new File(dest).getParent();
    }

    private static String[] getResultFiles(Class<?> c) {
        try {
            Field field = c.getField("RESULT_FILES");
            if (field == null) {
                return null;
            }
            Object obj = field.get(null);
            if (obj == null || !(obj instanceof String[])) {
                return null;
            }
            return (String[]) obj;
        } catch (Exception e) {
            return null;
        }
    }

    private void unloadLicense() {
        try {
            Field validators = LicenseKey.class.getDeclaredField("validators");
            validators.setAccessible(true);
            validators.set(null, null);
            Field versionField = Version.class.getDeclaredField("version");
            versionField.setAccessible(true);
            versionField.set(null, null);
        } catch (Exception ignored) {

            // No exception handling required, because there can be no license loaded
        }
    }
}
