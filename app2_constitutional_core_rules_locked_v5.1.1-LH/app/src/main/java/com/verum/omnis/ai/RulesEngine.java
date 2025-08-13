
package com.verum.omnis.ai;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Verum Omnis Rules-Only Engine (v5.1.1-derived)
 * Deterministic, no-ML scorer using template rules:
 *  - Keyword/entity scanning
 *  - Contradiction heuristics (simple opposing statement detection)
 *  - Omission/evasion markers
 *  - Concealment patterns
 *  - Financial irregularity flags
 *
 * Produces a riskScore [0..1] and topLiabilities[] list.
 * Used automatically when ONNX models are missing.
 */
public class RulesEngine {

    public static class Result {
        public double riskScore;
        public String[] topLiabilities;
        public JSONObject diagnostics;
    }

    private static final List<String> KEYWORDS = Arrays.asList(
            "admit","deny","forged","access","delete","refuse","invoice","profit",
            "unauthorized","breach","hack","seizure","shareholder","oppression","contract","cash"
    );
    private static final List<String> ENTITIES = Arrays.asList(
            "RAKEZ","SAPS","Article 84","Greensky","UAE","EU","South Africa"
    );
    private static final List<String> EVASION = Arrays.asList(
            "i don't recall","can't remember","not sure","later","stop asking","leave me alone"
    );
    private static final List<String> CONTRADICT = Arrays.asList(
            "never happened","i never said","you forged","fake","that is not true","i paid","no deal","we had a deal"
    );
    private static final List<String> CONCEAL = Arrays.asList(
            "delete this","use my other phone","no email","don't write","keep it off the record","use cash"
    );
    private static final List<String> FINANCIAL = Arrays.asList(
            "invoice","wire","transfer","swift","bank","cash","under the table","kickback"
    );

    public static Result analyzeFile(Context ctx, File file) {
        Result r = new Result();
        try {
            String text = readAll(file).toLowerCase(Locale.ROOT);

            int kw = countMatches(text, KEYWORDS);
            int ent = countMatches(text, ENTITIES);
            int ev = countMatches(text, EVASION);
            int con = countMatches(text, CONTRADICT);
            int hid = countMatches(text, CONCEAL);
            int fin = countMatches(text, FINANCIAL);

            // Heuristic scoring: weighted sum normalized to [0..1]
            double score = (kw*0.05 + ent*0.04 + ev*0.08 + con*0.1 + hid*0.12 + fin*0.06);
            score = Math.min(1.0, score);

            List<String> liab = new ArrayList<>();
            if (con >= 2) liab.add("Contradictions in statements");
            if (hid >= 1) liab.add("Patterns of concealment");
            if (ev  >= 2) liab.add("Evasion/Gaslighting indicators");
            if (fin >= 2) liab.add("Financial irregularity signals");
            if (kw  >= 3 && ent >= 1) liab.add("Legal subject flags present");

            if (liab.isEmpty()) liab.add("General risk");
            r.riskScore = score;
            r.topLiabilities = liab.toArray(new String[0]);

            JSONObject d = new JSONObject();
            d.put("keywords", kw);
            d.put("entities", ent);
            d.put("evasion", ev);
            d.put("contradictions", con);
            d.put("concealment", hid);
            d.put("financial", fin);
            r.diagnostics = d;

            return r;
        } catch (Exception e) {
            r.riskScore = 0.0;
            r.topLiabilities = new String[]{"Rules engine error: " + e.getMessage()};
            r.diagnostics = new JSONObject();
            return r;
        }
    }

    private static int countMatches(String text, List<String> needles) {
        int total = 0;
        for (String n : needles) {
            int idx = 0;
            while (true) {
                idx = text.indexOf(n.toLowerCase(Locale.ROOT), idx);
                if (idx == -1) break;
                total++; idx += n.length();
            }
        }
        return total;
    }

    private static String readAll(File f) throws Exception {
        // Basic text read; if it's binary, this still yields some bytes—ok for heuristic counts.
        // Avoid using Java 9+ FileInputStream.readAllBytes() since Android API levels below 26
        // do not provide this method.  Read the file manually into a ByteArrayOutputStream.
        byte[] bytes;
        try (FileInputStream fis = new FileInputStream(f)) {
            java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bytes = bos.toByteArray();
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
