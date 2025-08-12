
package com.verum.omnis.forensic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;

public class PdfSealerV2 implements PdfSealer {
    public static class SealResult { public File pdfFile; public String sha512Hex; }

    public static SealResult seal(Context ctx, File inputFile, Bitmap logo) throws Exception {
        // Minimal stub: produce a simple PDF page with hash text
        String sha512 = "unknown";
        try { sha512 = sha512File(inputFile); } catch (Exception ignore) {}

        PdfDocument doc = new PdfDocument();
        PdfDocument.PageInfo info = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = doc.startPage(info);
        Canvas canvas = page.getCanvas();
        Paint p = new Paint();
        p.setTextSize(12f);
        canvas.drawText("Verum Omnis – Sealed PDF (Stub)", 60, 60, p);
        canvas.drawText("SHA-512: " + sha512, 60, 80, p);
        doc.finishPage(page);

        File out = new File(ctx.getCacheDir(), "verum_stub_seal.pdf");
        FileOutputStream fos = new FileOutputStream(out);
        doc.writeTo(fos);
        fos.close();
        doc.close();

        SealResult r = new SealResult();
        r.pdfFile = out; r.sha512Hex = sha512;
        return r;
    }

    private static String sha512File(File f) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        java.io.InputStream is = new java.io.FileInputStream(f);
        byte[] buf = new byte[8192];
        int r;
        while ((r = is.read(buf)) != -1) md.update(buf, 0, r);
        is.close();
        byte[] d = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : d) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    @Override
    public PdfSealer.Result seal(Context ctx, File inputFile, Bitmap logo) throws Exception {
        SealResult s = PdfSealerV2.seal(ctx, inputFile, logo);
        PdfSealer.Result r = new PdfSealer.Result();
        r.pdfFile = s.pdfFile; r.sha512Hex = s.sha512Hex;
        return r;
    }
}
