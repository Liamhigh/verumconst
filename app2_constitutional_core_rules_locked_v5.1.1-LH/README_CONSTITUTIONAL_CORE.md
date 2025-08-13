# Verum Omnis – Constitutional Core Integration (v5.1.1-LH)

This update adds the immutable constitutional core. Assets and a runtime Hash Gate are included.

## Added
- `app/src/main/assets/verum_constitution/constitution.json`
- `app/src/main/assets/verum_constitution/model_hashes.json`
- `app/src/main/assets/verum_constitution/jurisdiction_packs.json`
- `app/src/main/assets/verum_constitution/hash_manifest.json`
- `app/src/main/assets/templates/Verum_Omnis_Constitution_Core.pdf`
- `app/src/main/java/com/verum/omnis/security/ConstitutionGate.kt`

## Enforce at startup
Kotlin (Application.onCreate):
```kotlin
val res = com.verum.omnis.security.ConstitutionGate.verifyAll(this)
require(res.ok) { "HARD_STOP: INTEGRITY_BREACH => $res" }
```

Java (Application.onCreate):
```java
com.verum.omnis.security.ConstitutionGate.VerificationResult res =
    com.verum.omnis.security.ConstitutionGate.INSTANCE.verifyAll(this);
if (!res.getOk()) throw new IllegalStateException("HARD_STOP: INTEGRITY_BREACH => " + res.getDetails());
```

**Note:** No Application class detected. Add the verification call to your launcher Activity's onCreate().

## Pin your artifacts
- Place ONNX files under `app/src/main/assets/models/` and insert their SHA-512 into `model_hashes.json`.
- Place jurisdiction packs under `app/src/main/assets/jurisdictions/` and insert their SHA-512 into `jurisdiction_packs.json`.
- For each added file, also add an entry to `verum_constitution/hash_manifest.json` so it is verified at startup.

## Runtime rules
- Offline only. Deterministic seeds. B9 is non‑voting.
- Reports must use the sealed PDF template (visible SHA‑512 + QR + ✔ block).
