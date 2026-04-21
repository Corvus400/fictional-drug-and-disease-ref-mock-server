---
paths:
  - "src/main/kotlin/**/fixture/**/*.kt"
  - "src/main/kotlin/**/catalog/**/*.kt"
---

# Reference Integrity Rules (drug ↔ disease)

This project exposes two fictional catalogs — drugs and diseases — that reference each other
(e.g. a drug fixture lists disease IDs it treats, and vice versa). Cross-references must stay
referentially consistent.

1. **ID format**:
   - Drugs: `drug_NNNN` (4-digit zero-padded, e.g. `drug_0001`)
   - Diseases: `disease_NNNN` (4-digit zero-padded, e.g. `disease_0001`)
   - No date/week suffixes; prefer stable long-lived IDs.

2. **Referential integrity**: Every drug ID referenced from a disease fixture (and vice versa) MUST
   exist as a registered fixture on the referenced side. Dangling references are a bug — the API
   would return IDs the other endpoint cannot resolve.

3. **Manual verification**: Until an automated checker exists, verify references by grep:

   ```bash
   # IDs referenced from either side must each be defined on their own side.
   grep -rhoE 'drug_[0-9]{4}'    src/main/kotlin/**/fixture/ | sort -u   # all drug IDs in use
   grep -rhoE 'disease_[0-9]{4}' src/main/kotlin/**/fixture/ | sort -u   # all disease IDs in use
   ```

4. **Adding a new ID**: When adding a new drug or disease fixture, also update any cross-reference
   lists on the opposite side if the new entity relates to existing ones. Missed updates surface as
   one-sided relationships (e.g. drug lists the disease, but the disease does not list the drug).

5. **Catalog display**: `/__admin/catalog` renders the fixture inventory (see
   `src/main/kotlin/**/catalog/`). When adding a new drug/disease fixture, confirm it appears on
   the catalog page so reviewers can spot missing cross-references visually.
