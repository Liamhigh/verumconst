# Verum Omnis v5.2.6 — Full Forensic Android App (Skeleton)

This is a *drop-in* Android Studio project that demonstrates the **full forensic** pipeline:
- Local-only processing (no telemetry)
- SHA-512 hashing
- Watermark + bottom-left tick + truncated hash
- Bottom-right QR
- Placeholder blockchain anchor
- Jurisdiction-aware scaffolding (ZAF/UAE/EU raw configs included)
- Dark UI + trial notice

> You will need to replace placeholder models in `app/src/main/assets/model/` with your production artifacts.

## Build
1. Open the folder in **Android Studio**.
2. Let Gradle sync.
3. Run on a device (minSdk 24).

## Where to extend
- `core/BehavioralAnalyzer.java`: connect 9 AI brains + consensus.
- `core/PDFSealer.java`: swap in your PDF/A-3B pipeline.
- `core/BlockchainService.java`: anchor to your chain/attestations.
- `core/AnalysisEngine.java`: wire image/video/audio validators & metadata.

## License
Proprietary © Verum Omnis. For internal use only.

## Embedded compliance docs (assets/docs)
- Verum_Omnis_Forensic_Standards_Report.pdf
- VerumOmnis_Sealed_ContradictionEngine_Certificate.pdf
- Verum_Omnis_Full_Template_v5.1.1_Complete.pdf

## Rules-Only Engine (v5.1.1-derived)
When ONNX models are absent, the app uses a deterministic **Rules Engine** that implements your template heuristics:
- Keyword/entity scanning
- Contradiction, omission/evasion, concealment, financial irregularity signals
- Normalized risk score [0..1] + top liabilities
Set your models later for full AI mode.


## R&D Brain (stateless, independent)
- Module: `com.verum.omnis.ai.RnDController`
- Consumes **Rules Engine** diagnostics only (no network, no servers).
- Produces **in-memory** training directives and a small dynamic risk-weight boost (max +0.5).
- Optional export via `forensic/RnDReportExporter` seals a demo PDF to **app cache** only.
- No telemetry, no analytics, no remote logging. Nothing is written unless the user explicitly exports.


## Mesh Learning (stateless, P2P, opt-in)
- Module: `com.verum.omnis.ai.RnDMeshExchange`
- **Export:** `exportPacket(context, feedback)` → writes a `.json` into app external files (or cache). Share this file peer‑to‑peer (no central server).  
- **Import:** read the JSON text and call `importAndApply(context, json, feedback)` → merges boolean directives (logical OR) after **hash** verification (`sha512` inside file).
- No case data, no telemetry: only aggregated directives + counts. If the hash doesn’t match, import is rejected.


## Recovery Ledger (business-only)
- Module: `forensic/RecoveryLedger`
- Auto-logs **only when responsible party looks like a business** (Ltd/LLC/Inc/Corp/GmbH/Pty/etc.). Private citizens are **skipped**.
- Fields: case_id, fraud_amount, currency, party_name, jurisdiction, source_sha512, detected_at, detected_by, entry_sha512.
- Storage: local JSONL at `files/recovery_ledger.jsonl` (stateless; no telemetry).
- Optional: call `RecoveryLedger.sealEntryPdf(context, entry)` to produce a sealed PDF (uses current stub sealer).
